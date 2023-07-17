package no.paneon.oas.plantuml.pumloverlay;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import no.paneon.api.diagram.GenerateDiagram;
import no.paneon.api.extensions.ExtractExtensions;
import no.paneon.api.utils.Config;
import no.paneon.api.utils.Utils;

public class Batch 
{
	
	static final Logger LOG = LogManager.getLogger(Batch.class);
    
	Args.Batch args;
	
	public Batch(Args.Batch args) {
		this.args = args;
	}
	
    String execute() {
    	
    	try {
	    	String tmpDirsLocation = System.getProperty("java.io.tmpdir");
	    	Path path = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(), UUID.randomUUID().toString());
	    	String tmpdir = Files.createDirectories(path).toFile().getAbsolutePath();
	    	
	    	if(args.keepTempdir) {
	    		Out.debug("... temporary directory {}",  tmpdir);
	    	}
	    	
	    	String addedLabel = args.addedLabel;
	    	String addedColor = args.addedColor;
	    	
	    	String removedLabel = args.removedLabel;
	    	String removedColor = args.removedColor;
	    	
	    	extract_extensions(args.prev,args.current,addedLabel,addedColor,tmpdir,"current", Optional.empty());
	    	
	        Optional<String> subresource_config=create_subresource_config(tmpdir, tmpdir + "/current/diagrams.yaml");

	    	LOG.debug("... temporary directory  subresource_config={}",   subresource_config);

	        extract_extensions(args.current,args.prev,removedLabel,removedColor,tmpdir,"prev", subresource_config);
	    		    	
	        for(String base : Utils.getFiles(".puml", tmpdir,"current")) {
		    	Out.debug("... generating overlay for {}", base);

		    	String file1 = tmpdir + "/prev/" + base;
		    	String file2 = tmpdir + "/current/" + base;
		    	if(isFile(file1)) {
		    		generate_overlay(base,file1,file2,args.targetDirectory);
		    	} else {
		    		final boolean OVERWRITE=true;
		    		Utils.copyFile(file2, args.targetDirectory + "/" + base, OVERWRITE);
		    	}
	        };
	        
	        if(this.args.generateImages) {
	        	GenerateDiagram.generateImage(args.targetDirectory, Utils.getFiles(".puml", args.targetDirectory));
	        }
	        
	        if(!this.args.keepTempdir) {
	        		        	
	        	Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
	                @Override
	                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	                	try {
	                		Files.delete(dir);
	                	} catch(Exception e) {
	                		// ignore
	                	}
	                    return FileVisitResult.CONTINUE;
	                }
	                
	                @Override
	                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                	try {
	                		Files.delete(file);
	                	} catch(Exception e) {
	                		// ignore
	                	}	                    
	                	return FileVisitResult.CONTINUE;
	                };
	        	});
	        }
	        
	    	Out.debug("... done - output to {}", args.targetDirectory);

	    	return tmpdir;
	    	
    	} catch(Exception ex) {
    		Out.printAlways("ERROR: {}", ex.getLocalizedMessage());
    		ex.printStackTrace();
    	}
    	
    	return "";
    }
    
	
	private void generate_overlay(String base, String file1, String file2, String targetDirectory) {    	

    	Args.Overlay  argsOverlay = (new Args()).new Overlay();
		
		argsOverlay.files = new LinkedList<>();
		argsOverlay.files.add(file1);
		argsOverlay.files.add(file2);

		argsOverlay.output = targetDirectory + "/" + base;

		Utils.createDirectory(argsOverlay.output);
	
		Overlay overlay = new Overlay(argsOverlay);
		overlay.execute();
		
	}

	private Optional<String> create_subresource_config(String dir, String file) {
    	JSONObject config = new JSONObject();
    	
    	if(isFile(file)) {
    		JSONObject diagrams = Config.readJSONOrYaml(file);
    		if(diagrams.has("graphs")) {
    			JSONArray resources = diagrams.optJSONArray("graphs");
    			LOG.debug("create_subresource_config: resources={}", resources);
    			
    			List<String> keys = resources.toList().stream()
    			 	.filter(c -> c instanceof HashMap<?,?>)
    			    .map(c -> (HashMap<String,Object>) c)
    			    .map(c -> c.keySet())
    			    .flatMap(Set::stream)
    			    .filter(k -> k.contains("_"))
    			    .collect(Collectors.toList());

				JSONObject res = new JSONObject();
    			keys.forEach(key -> {
    				String[] parts = key.split("_");
    				if(parts.length==2) {
    					String resource = parts[0];
    					String subResource = parts[1];
    					
    					if(!res.has(resource)) res.put(resource, new JSONArray());
    					res.optJSONArray(resource).put(subResource);
    				}
    			});
    			if(!res.isEmpty()) {
    				config.put("subResourceConfig",res);
    			}
    			
    			LOG.debug("create_subresource_config: keys={}", res.keySet());

    			
    		}
    	} 
     
    	String subresource_json=dir + "/subresource.json";
    	Utils.saveAsJson(config, subresource_json);
    	
		return Optional.of(subresource_json);
		
	}

    private boolean isFile(String file) {
    	return Files.exists(Paths.get(file));
	}

	private String create_config(String dir, String file) {
		Config.init();
		
    	JSONObject config = Config.getConfig("overlay");

    	String expand_json=dir + "/" + file;

    	Utils.saveAsJson(config, expand_json);

    	return expand_json;
    }
    
	private void extract_extensions(String base, String api, String label, String color, String dir, String subdir, Optional<String> subConfig) {

    	LOG.debug("... extract_extensions base={} api={} label={} color={} dir={} subdir={}", 
    				base, api, label, color, dir, subdir);

    	String expand_json = create_config(dir,"expand.json");

		String extract_config="config-" + label + ".json";
				
    	LOG.debug("... extract_extensions extract_config={}",   extract_config);

    	boolean error=false;
    	if(!Files.exists(Paths.get(base))) {
    		Out.printAlways("ERROR: file {} not found", base);
    		error=true;
    	}
    	
    	if(!Files.exists(Paths.get(api))) {
    		Out.printAlways("ERROR: file {} not found", api);
    		error=true;
    	}
    	
    	if(error) {
    		System.exit(1);
    	}
    	
		no.paneon.api.diagram.app.Args.ExtractExtensions  argsExtractExtensions = (new no.paneon.api.diagram.app.Args()).new ExtractExtensions();
		
		String extractConfigFilename = dir + "/" + extract_config;
		
		argsExtractExtensions.baseSpecification 	= base;
		argsExtractExtensions.openAPIFile 			= api;
		argsExtractExtensions.extensionLabel 		= label;
		argsExtractExtensions.extensionColor 		= color;
		argsExtractExtensions.targetDirectory		= dir;
		argsExtractExtensions.outputFileName		= extract_config;
		
		argsExtractExtensions.configs = new LinkedList<>();
		argsExtractExtensions.configs.add(expand_json);
		
		argsExtractExtensions.configs.addAll(this.args.configs);
		
    	LOG.debug("... extract_extensions argsExtractExtensions={}",  argsExtractExtensions.outputFileName);

   		Config.reset();
 		ExtractExtensions extensions = new ExtractExtensions(argsExtractExtensions);
 		
   		LOG.debug("... extract_extensions extensions={}", extensions);

   		extensions.execute();
   		   		
   		Out.debug("... extract_extensions output to {}", extract_config);

   		no.paneon.api.diagram.app.Args.Diagram  argsDiagram = (new no.paneon.api.diagram.app.Args()).new Diagram();
		
   		argsDiagram.openAPIFile     = api;
   		argsDiagram.targetDirectory	= dir + "/" + subdir;
   		
   		argsDiagram.configs = new LinkedList<>();
   		argsDiagram.configs.add(expand_json);
   		argsDiagram.configs.add(extractConfigFilename);
   	     
   		argsDiagram.configs.addAll(this.args.configs);

   		LOG.debug("... configs={}", argsDiagram.configs);

   	    if(subConfig.isPresent() && subConfig.get()!=null) {
   	   		argsDiagram.configs.add(subConfig.get());
   	   		LOG.debug("... extract_extensions subConfig={}", subConfig);

   	    }
   		
   		Config.reset();
		GenerateDiagram diagram = new GenerateDiagram(argsDiagram);
		
   		LOG.debug("... extract_extensions diagram={}", diagram);

		diagram.execute();
   		
	}

}
