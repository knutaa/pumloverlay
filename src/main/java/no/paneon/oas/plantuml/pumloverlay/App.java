package no.paneon.oas.plantuml.pumloverlay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.beust.jcommander.JCommander;
import no.paneon.api.utils.Config;

public class App 
{
	
	static final Logger LOG = LogManager.getLogger(App.class);

	JCommander commandLine;

	Args argm;
	
	Args.Overlay            argsOverlay;
	Args.Batch  	        argsBatch;
	
	static final String SPACE = " ";
	
	App(String ... argv) {
		     
		argm = new Args();
		
		argsOverlay = argm.new Overlay();
		argsBatch   = argm.new Batch();

		commandLine = JCommander.newBuilder()
		    .addCommand("overlay", argsOverlay)
		    .addCommand("batch",   argsBatch)
		    .build();

		try {
			commandLine.parse(argv);						
		} catch(Exception ex) {
			Out.println(ex.getMessage());
			Out.println("Use option --help or -h for usage information");
			
			System.exit(1);
		}
		
	}

    public static void main(String ... argv )
    {
		LOG.debug("Plantuml overlay");
        
		// Config.forceConfig();
		
		// Config.setBoolean("pumloverlay",true);
		
		Out.debug("Plantuml overlay");

        App app = new App(argv);
    	    
        app.run();
        
    }
    
    void run() {
		if (commandLine.getParsedCommand()==null) {
            commandLine.usage();
            return;
        }
		
    	switch(commandLine.getParsedCommand()) {
    
    	case "--help":
    	case "help":
    	default:
    		commandLine.usage();
    		break;
    		
    	case "overlay":
    		Overlay overlay = new Overlay(argsOverlay);
    		overlay.execute();
    		break;
    		
    	case "batch":
    		Batch batch = new Batch(argsBatch);
    		batch.execute();
    		break;	
    	}
    	
    }
    
 
}
