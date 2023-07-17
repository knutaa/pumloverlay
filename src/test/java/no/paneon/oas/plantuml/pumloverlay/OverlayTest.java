package no.paneon.oas.plantuml.pumloverlay;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class OverlayTest extends TestCase {

	String targetDir; 
	static String resourceDir = "src/test/resources/";

    public OverlayTest( String testName )
    {
        super( testName );
        
        try {
	    	// targetDir = createTempDir();
	    	
           	String path = System.getProperty("user.home");
        	targetDir = path + "/test-overlay/3";
   	
    		
        } catch(Exception ex) {
        	
        }
        
    }

    public static Test suite()
    {
        return new TestSuite( OverlayTest.class );
    }

    
    public void test00()
    {
    	    	
    	try {
		    	
			String prev = resourceDir + "TMF620-ProductCatalog-v4.2.0.swagger.json";
			String current = resourceDir + "TMF620-Product_Catalog_Management-v5.0.0.oas.yaml";
			
			test(prev,current,targetDir);
			
			assert(true);
			
    	} catch(Exception ex) {
    		System.out.println("Exception: " + ex.getLocalizedMessage());
    		assert(false);
    	}

    }
    
    
    public void test0()
    {
    	    	    	
//    	try {
//		    	
//			String prev = resourceDir + "TMF622-ProductOrdering-v4.0.0.swagger.json";
//			String current = resourceDir + "TMF622-ProductOrdering-v5.0.0.oas.yaml";
//			
//			test(prev,current,targetDir);
//			
//			assert(true);
//			
//    	} catch(Exception ex) {
//    		System.out.println("Exception: " + ex.getLocalizedMessage());
//    		assert(false);
//    	}

    }
    
    public void test1()
    {
    	
    	try {
    		
    		targetDir = createTempDir();
    			    	
			String prev = resourceDir + "Test_API_1_v4.json";
			String current = resourceDir + "Test_API_1_v4_1.json";
			
			String tmpDir = test(prev,current,targetDir);
			
			File file = FileUtils.getFile(targetDir,"Resource_ProductOrder.puml");
			
			assert(file!=null);
			
			String content = FileUtils.readFileToString(file,Charset.defaultCharset());
								
			assert( content.contains("<color:blue>newCancellationDate : DateTime") );
			
			assert( content.contains("<color:red>cancellationDate : DateTime") );

			// assert( content.contains("*-[#blue]-> \"0..*\" \"PaymentRef\" : <color:blue>\"payment\"" ) );
			
    		System.out.println("test1: tmpDir: " + tmpDir);

			assert(true);
			
    	} catch(Exception ex) {
    		System.out.println("Exception: " + ex.getLocalizedMessage());
    		assert(false);
    	}

    }
    

    public void test2()
    {
    	   	
    	try {
	    	
			String prev = resourceDir + "Test_API_1_v5_0.yaml";
			String current = resourceDir + "Test_API_1_v5_1.yaml";
			
			String tmpDir = test(prev,current,targetDir);
			
			File file = FileUtils.getFile(targetDir,"Resource_ProductOrder.puml");
			
			assert(file!=null);
			
			String content = FileUtils.readFileToString(file,Charset.defaultCharset());
					
			// assert( content.contains("{field} @type : String <color:blue>(1)") );
			
			assert( content.contains("<color:blue>newRequestedCompletionDate : DateTime") );
			
			assert( content.contains("<color:red>cancellationDate : DateTime") );

			// assert( content.contains("*-[#blue]-> \"0..*\" \"PaymentRef\" : <color:blue>\"payment\"" ) );
			
    		System.out.println("test1: tmpDir: " + tmpDir);

			assert(true);
			
    	} catch(Exception ex) {
    		System.out.println("Exception: " + ex.getLocalizedMessage());
    		assert(false);
    	}

    }
    
    
    
    public String test(String prev, String current, String targetDir)
    {
    	
      	try {

	    	Args args = new Args();
			
			Args.Batch argsBatch = args.new Batch();
	    	
			argsBatch.prev = prev;
			argsBatch.current = current;
			argsBatch.targetDirectory = targetDir;
			
			argsBatch.generateImages = true;
			
			argsBatch.keepTempdir = true;
					
			Batch batch = new Batch(argsBatch);
			
			return batch.execute();
			
			
    	} catch(Exception ex) {
    		System.out.println("Exception: " + ex.getLocalizedMessage());
    		assert(false);
    	}
		
      	return "";
      	
	}

    private static String createTempDir() throws Exception {
    	Path path = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(), UUID.randomUUID().toString());
    	String tmpdir = Files.createDirectories(path).toFile().getAbsolutePath();

    	return tmpdir;
    }
    
    
}
