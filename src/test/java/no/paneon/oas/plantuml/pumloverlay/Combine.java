package no.paneon.oas.plantuml.pumloverlay;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import no.paneon.oas.plantuml.g4.PlantumlLexer;
import no.paneon.oas.plantuml.g4.PlantumlParser;
import org.springframework.core.io.ClassPathResource;

public class Combine extends TestCase {

    public Combine( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( Combine.class );
    }

    public void test1()
    {
    	test("test-3.puml");
    }
    
    public void test2()
    {
    	test("test-4.puml");
    }
    
    public void test(String file)
    {

		System.out.println("test: " + file);
		
		try {
			InputStream is = new ClassPathResource(file).getInputStream();
		    String content = IOUtils.toString(is, StandardCharsets.UTF_8.name());
		    
			CharStream input = CharStreams.fromString(content);			
	        PlantumlLexer lexer = new PlantumlLexer(input);
	        CommonTokenStream tokens = new CommonTokenStream(lexer);
	        PlantumlParser parser = new PlantumlParser(tokens);
	
	        ParseTree tree = parser.uml(); 

	        ParseTreeWalker walker = new ParseTreeWalker();
	        PumlListener classListener= new PumlListener();

	        walker.walk(classListener, tree);

	        System.out.println(classListener.puml.toString());

		} catch(Exception ex) {
			System.out.println("exception: " + ex);
		}
		
	}

}