package no.paneon.oas.plantuml.pumloverlay;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PumlClass extends PumlFormat {

	static final Logger LOG = LogManager.getLogger(PumlClass.class);

	String name;
	List<String> content;
	List<String> stereotypes;
	List<String> discriminators;
	boolean blank;
	
	Map<String,String> decorations;
	Map<String,PumlAttribute> attributes;

	PumlClass(String cls, List<String> content, List<String> stereotypes) {
		this.name = cls;
		this.content = content;
		this.stereotypes = stereotypes;
		this.discriminators = new LinkedList<>();
		this.blank = false;
		this.decorations = new HashMap<>();
		this.attributes = new HashMap<>();

        LOG.debug("PumlClass: content={}", content);

	}
	
	PumlClass(String cls, Map<String,PumlAttribute> attributes, List<String> content, List<String> stereotypes, boolean blank, Map<String,String> decorations) {
		this(cls,content,stereotypes);
		this.blank = blank;
		this.decorations.putAll(decorations);
		this.attributes.putAll(attributes);

	}
	
	PumlClass(String cls, Map<String,PumlAttribute> attributes, List<String> content, List<String> stereotypes, List<String> discriminators, boolean blank, Map<String,String> decorations) {
		this(cls,attributes, content,stereotypes,blank,decorations);
		this.discriminators.addAll(discriminators);
	}
	
	static final String BLANK_LINE = "{field}//                                        //";

	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append("class ").append(name); 
		
		if(!decorations.isEmpty()) {
			s.append("<extends ");
			decorations.entrySet().forEach(decoration -> {
				s.append("\\n");
				if(!decoration.getValue().isEmpty()) s.append(decoration.getValue());
				s.append(decoration.getKey());
			});
			s.append(" >");
		}
		stereotypes.forEach(t -> {
			s.append(" ").append(t);
		});
		
		s.append(" ").append("{").append(NEWLINE);
		
//		List<String> sortedContent = sort(content);
//		
//		sortedContent.forEach(line -> {
//			s.append(INDENT).append(line).append(NEWLINE);
//		});
		
		Set<String> attributeNames = new TreeSet(attributes.keySet());
		attributeNames.stream().forEach(attr -> {
			s.append(INDENT).append(attributes.get(attr).toString());
		});
		
		if(this.blank) {
			s.append(INDENT).append(BLANK_LINE).append(NEWLINE);
		}
		
		if(!discriminators.isEmpty()) {
			s.append(INDENT).append("--").append(NEWLINE);
			s.append(INDENT).append("discriminator:").append(NEWLINE);
			discriminators.forEach(line -> {
				s.append(INDENT).append(line).append(NEWLINE);
			});
		}
		
		s.append("}").append(NEWLINE);

		return s.toString();

	}

	private List<String> sort(List<String> content) {
		List<String> res=content;
		
		LOG.debug("sort: content={}", content);
		
//		Map<String,String> map = new HashMap<>();
//		
//		content.forEach(line -> {
//			String cleaned = line.replaceAll("<.*>", "").replace("{field}", "");
//			map.put(cleaned, line);
//		});
//		
//		res = map.keySet().stream()
//			.sorted()
//			.collect(Collectors.partitioningBy(p -> p.startsWith("@")))
//			.values().stream()
//			.flatMap(List::stream)
//			.map(map::get)
//			.toList();
//		
//		LOG.debug("sort: map={}", map);
		
		return res;
	}
}
