package no.paneon.oas.plantuml.pumloverlay;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class Unit extends TestCase {

    public Unit( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( Unit.class );
    }

    public void test_sort_properties()
    {
    	String file = "puml_properties_1.txt";
    	try {
			InputStream is = new ClassPathResource(file).getInputStream();
		    String properties = IOUtils.toString(is, StandardCharsets.UTF_8.name());
    		
    		Out.debug("sort_properties:: properties={}", properties);
    		
    		List<String> lines = Arrays.asList(properties.split("\n"));
    		
    		List<String> sorted = Sorter.sort(lines);
    		
    		Out.debug("sort_properties:: sorted={}", sorted);
    		
    		assert(true);
    		
    	} catch(Exception e) {
    		Out.debug("sort_properties:: exception={}",  e);
    		assert(false);
    	}
    	
    			
    }
    


}
