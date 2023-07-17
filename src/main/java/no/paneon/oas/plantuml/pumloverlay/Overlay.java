package no.paneon.oas.plantuml.pumloverlay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.paneon.api.utils.Config;
import no.paneon.oas.plantuml.g4.PlantumlLexer;
import no.paneon.oas.plantuml.g4.PlantumlParser;


public class Overlay 
{
	
	static final Logger LOG = LogManager.getLogger(Overlay.class);
	
	static final String SPACE = " ";
	
	static final String COLOR_MOVED = "colorMoved";

	Args.Overlay args;
	
	public Overlay(Args.Overlay args) {
		this.args = args;
	}

	void execute() {
    
    	if(args.files.size()<2) {
    		Out.printAlways("Error: expecting at least two file arguments (--file)");
    		System.exit(1);
    	}
    	
    	final List<String> fileContent = new LinkedList<>();
    				
		args.files.forEach(file -> {
			try {
				InputStream is = new FileInputStream(new File(file));	
			    String content = IOUtils.toString(is, StandardCharsets.UTF_8.name());
			    fileContent.add(content);
			} catch(Exception ex) {
				Out.printAlways("Error: Unable to read file: " + file);
				System.exit(1);
			}
		});
		
		final String EXTENSION_OLD = "<<Extension>>";
		final String EXTENSION_NEW = "<<Extension-XX>>";

		Integer[] idx = { 1 };
		List<String> updatedContent = fileContent.stream().map(s -> {
			String newExt = EXTENSION_NEW.replace("XX",idx[0].toString());
			idx[0]++;
			return s.replaceAll(EXTENSION_OLD, newExt);
		}).collect(Collectors.toList());
					
		List<Puml> puml = updatedContent.stream().map(this::convertToPuml).collect(Collectors.toList());
		
		Puml base = puml.remove(0);
		
		puml.forEach(overlay -> {
			
			processClasses(base,overlay);
			
			processConnections(base,overlay);

			processMisc(base,overlay);
			
			processEnums(base,overlay);
			
			processLegend(base,overlay);
			
			processSkinparams(base,overlay);

			
		});

		
		// Out.println(base.toString());
		
		try {
			FileWriter fileWriter = new FileWriter(args.output);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(base.toString());
		    printWriter.close();
		} catch(Exception ex) {
			Out.printAlways("Error: Unable to write output to " + args.output);
			Out.printAlways("Error: " + ex.getLocalizedMessage());

			System.exit(1);
		}
		

		
    }
    

	private void processLegend(Puml base, Puml overlay) {
		overlay.legend.forEach(l -> {
			if(!base.legend.contains(l)) base.legend.add(l);
		});		
	}

	private void processEnums(Puml base, Puml overlay) {
    	Set<String> enums = overlay.enums.keySet();
		for(String cls : enums) {
			List<String> baseLines = base.enums.get(cls).content;
			List<String> lines = overlay.classes.get(cls).content;
				
			List<String> merged = mergeList(baseLines,lines);
							
			base.enums.get(cls).content=merged;
				
		}
	}
	
	private void processSkinparams(Puml base, Puml overlay) {
    	Set<String> params = overlay.skinparams.keySet();
		for(String cls : params) {
			List<String> baseLines = base.skinparams.get(cls);
			List<String> lines = overlay.skinparams.get(cls);
				
			List<String> merged = mergeList(baseLines,lines);
							
			base.skinparams.put(cls,merged);
				
		}
	}
	
	private void processMisc(Puml base, Puml overlay) {
		overlay.misc.forEach(l -> {
			if(!base.misc.contains(l)) base.misc.add(l);
		});
				
	}

	private void processClasses(Puml base, Puml overlay) {
    	Set<String> classes = overlay.classes.keySet();
		for(String cls : classes) {
			if(!base.classes.containsKey(cls)) {
				base.classes.put(cls, overlay.classes.get(cls));
			} else {
				
				Map<String,PumlAttribute> baseAttributes = base.classes.get(cls).attributes;
				
				overlay.classes.get(cls).attributes.entrySet().stream().forEach(e -> {
					String attrName = e.getKey();
					
					if(!baseAttributes.containsKey(attrName)) {
						baseAttributes.put(attrName,  e.getValue());
						
						LOG.debug("processClasses: #1 cls={} attribute={}", cls, attrName);

					} else {
										
						LOG.debug("processClasses: #2 cls={} attribute={}", cls, attrName);

						PumlAttribute baseAttribute = baseAttributes.get(attrName);
						PumlAttribute overlayAttribute = e.getValue();

						LOG.debug("processClasses: #2 cls={} color baseAttribute={}", cls, baseAttribute.attribute_color);
						LOG.debug("processClasses: #2 cls={} color overlayAttribute={}", cls, overlayAttribute.attribute_color);

						// String mand=baseAttribute.mandatory+overlayAttribute.mandatory;
						
//						if(!mand.isEmpty()) 
//							Out.debug("attrName={} base_mandatory={} overlay_mandatory{}", attrName, baseAttribute.mandatory, overlayAttribute.mandatory);

						baseAttribute.attribute_color = combineColor(baseAttribute.attribute_color, overlayAttribute.attribute_color);
						
						baseAttribute.type = combineText(baseAttribute.type, overlayAttribute.type);
						baseAttribute.mandatory = combineText(baseAttribute.mandatory, overlayAttribute.mandatory);
						baseAttribute.cardinality = combineText(baseAttribute.cardinality, overlayAttribute.cardinality);
										
					}
				});
			
				List<String> discriminators = mergeDiscriminators(base.classes.get(cls).discriminators,overlay.classes.get(cls).discriminators);

				LOG.debug("mergeDiscriminators: {}", discriminators);

				base.classes.get(cls).discriminators=discriminators;
				
				// Out.println("attributes: " + attributes);
				// Out.println("partitions: " + partitions);
				
				Map<String,String> baseDecorations = base.classes.get(cls).decorations;
				Map<String,String> decorations = overlay.classes.get(cls).decorations;
			
				decorations.entrySet().forEach(item -> {
					if(!item.getValue().isEmpty() || !baseDecorations.containsKey(item.getKey())) baseDecorations.put(item.getKey(), item.getValue());
				});
								
			}
		}		
	}

	private String combineColor(String color1, String color2) {
		if(color1.isEmpty() ) {
			return color2;
		} else if(color2.isEmpty()) {
			return color1;
		} else if(!color1.contentEquals(color2)) {
			return Config.getString(COLOR_MOVED);
		} else {
			return color1;
		}
	}

	private String combineText(String s1, String s2) {
		LOG.debug("combineText: s1={} s2={}", s1, s2);
		if(s1.isEmpty()) {
			return s2;
		} else if(s2.isEmpty()) {
			return s1;
		} else if(s1.contentEquals(s2)) {
			return s1;
		} else if(s1.contains(s2)) {
			return s1;
		} else if(s2.contains(s1)) {
			return s2;
		} else {
			return s1 + SPACE + s2;
		}
	}

	private void processConnections(Puml base, Puml overlay) {
	  	Set<String> connections = overlay.connections.keySet();
		for(String key : connections) {
			PumlConnection reverseConn = getReverse(base,key);
			
			if(!base.connections.containsKey(key) && reverseConn==null) {
				base.connections.put(key, overlay.connections.get(key));
			} else {

				PumlConnection newConn = overlay.connections.get(key);
				PumlConnection baseConn = reverseConn!=null ? reverseConn : base.connections.get(key);
				
				if(!baseConn.left_multiplicity.isBlank()) {
					baseConn.left_multiplicity = mergeMultiplicity(baseConn.left_multiplicity,newConn.left_multiplicity, newConn.right_multiplicity);
				}
				
				if(!baseConn.right_multiplicity.isBlank()) {
					baseConn.right_multiplicity = mergeMultiplicity(baseConn.right_multiplicity,newConn.left_multiplicity, newConn.right_multiplicity);
				}		
				
				if(baseConn.connector.contains("[#") && newConn.connector.contains("[#")) {
					baseConn.connector = baseConn.connector.replaceAll("\\[#[a-z]+\\]", "");
				}
				
				if(!baseConn.property_color.isEmpty() && !newConn.property_color.isEmpty()) {
					baseConn.property_color="";
				}
				
				base.connections.put(key,baseConn);
				
			}
		}				
	}
	   
	private PumlConnection getReverse(Puml base, String key) {
		LOG.debug("getReverse: key={}", key);
		String parts[] = key.split("-");
		if(parts.length<2) return null;
		String reverseKey = parts[1] + "-" + parts[0] + "-";
		if(parts.length>2) reverseKey = reverseKey + parts[2];
		return base.connections.get(reverseKey);
	}

	private String mergeMultiplicity(String m1, String m2, String m3) {
		String res = m1;
		if(!m1.contentEquals(m2)) {
			res = res + m2;
			res = res.replace("\"\"", " ");
		}
		if(!m1.contentEquals(m3)) {
			res = res + m3;
			res = res.replace("\"\"", " ");
		}
		return res;	
	}

	private List<String> mergeDiscriminators(List<String> base, List<String> partitions) {    	
    	LOG.debug("mergeDiscriminators: base={} partitions={}", base, partitions);
    	
    	return mergeList(base,partitions);	
    	
	}

	private List<String> mergeAttributes(List<List<String>> basePartitions, List<List<String>> partitions) {
    	List<String> res = new LinkedList<>();
    	
    	if(basePartitions.size()>0 && partitions.size()>0) {
    		return mergeAttributeList(basePartitions.get(0),partitions.get(0));
    	}
    	
		return res;
	}

	private List<String> mergeAttributeList(List<String> partA, List<String> partB) {
    	List<String> res = new LinkedList<>();
    	
		res.addAll(partA);
		res.addAll(partB.stream().filter(s->!partA.contains(s)).collect(Collectors.toList()));
	
		return res;
	}
	
	private List<String> mergeList(List<String> partA, List<String> partB) {
    	List<String> res = new LinkedList<>();
    	
		res.addAll(partA);
		res.addAll(partB.stream().filter(s->!partA.contains(s)).collect(Collectors.toList()));
	
		return res;
	}
	
	private List<List<String>> getPartition(List<String> lines) { 
    	    	
    	int[] indexes = 
    		      Stream.of(IntStream.of(-1), IntStream.range(0, lines.size())
    		      .filter(i -> lines.get(i).contains("  --")), IntStream.of(lines.size()))
    		      .flatMapToInt(s -> s).toArray();
    	
	    List<List<String>> subSets = 
	      IntStream.range(0, indexes.length - 1)
	               .mapToObj(i -> lines.subList(indexes[i] + 1, indexes[i + 1]))
	               .collect(Collectors.toList());
    		  
	    return subSets;
	}

	Puml convertToPuml(String pumlContent) {
	    
		LOG.debug("convertToPuML: content={}",  pumlContent);
		
		CharStream input = CharStreams.fromString(pumlContent);
		
        PlantumlLexer lexer = new PlantumlLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        PlantumlParser parser = new PlantumlParser(tokens);
        
        ErrorListener errorListener = new ErrorListener();

        lexer.removeErrorListeners();
        lexer.addErrorListener( errorListener );

        parser.removeErrorListeners();
        parser.addErrorListener( errorListener );
        
        ParseTree tree = parser.uml(); 

        ParseTreeWalker walker = new ParseTreeWalker();
        PumlListener listener = new PumlListener();

        walker.walk(listener, tree);

        return listener.puml;

    }
}
