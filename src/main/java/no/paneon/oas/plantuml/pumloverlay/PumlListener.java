package no.paneon.oas.plantuml.pumloverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.paneon.oas.plantuml.g4.PlantumlBaseListener;
import no.paneon.oas.plantuml.g4.PlantumlParser;
import no.paneon.oas.plantuml.g4.PlantumlParser.LineContext;
import no.paneon.oas.plantuml.g4.PlantumlParser.StereotypeContext;

public class PumlListener extends PlantumlBaseListener {

	static final Logger LOG = LogManager.getLogger(PumlListener.class);

	public Puml puml = new Puml();
    
    @Override
    public void enterClass_declaration(PlantumlParser.Class_declarationContext ctx) {
        String cls = ctx.ident().getText();
        
        List<String> content = new LinkedList<>();
        
        if(ctx.attribute()!=null) {
        	content.addAll( ctx.attribute().stream().map(PumlListener::getFullText).collect(Collectors.toList()) );  	
        }
        
        if(ctx.assignment()!=null) {
        	content.addAll( ctx.assignment().stream().map(PumlListener::getFullText).collect(Collectors.toList()) );  	
        }
                
        List<String> stereotypes = ctx.stereotype().stream().map(StereotypeContext::getText).collect(Collectors.toList());
        
        content = content.stream()
					.filter(s -> !s.endsWith("--"))
					.collect(Collectors.toList());
        
        boolean blank = ctx.blank()!=null && !ctx.blank().isEmpty();
        
        if(ctx.discriminator()!=null) {
        	List<String> discriminators = ctx.discriminator().line().stream().map(PumlListener::getFullText).collect(Collectors.toList());
        	puml.classes.put(cls, new PumlClass(cls,content,stereotypes,discriminators,blank));
        	
        } else {
        	puml.classes.put(cls, new PumlClass(cls,content,stereotypes,blank));

        }
        
    }
    
    @Override
    public void enterEnum_declaration(PlantumlParser.Enum_declarationContext ctx) {
        String cls = ctx.ident().getText();
        List<LineContext> lines = ctx.line();
        List<String> stereotypes = ctx.stereotype().stream().map(PumlListener::getFullText).collect(Collectors.toList());

        // System.out.println("class: " + cls);
        
        List<String> content = lines.stream().map(PumlListener::getFullText).collect(Collectors.toList());
        
        puml.classes.put(cls, new PumlClass(cls,content,stereotypes));
        
    }
    
    
    @Override
    public void enterConnection(PlantumlParser.ConnectionContext ctx) {
    	                
    	LOG.debug("connection: {}", getFullText(ctx));
    	
        String left = ctx.left.getText();
        String left_multiplicity = ctx.leftMultiplicity != null ? ctx.leftMultiplicity.getText() : "";
        
        String connector = ctx.CONNECTOR().getText();

        String right = ctx.right.getText();
        String right_multiplicity = ctx.rightMultiplicity != null ? ctx.rightMultiplicity.getText() : "";
 
        String property_color = ctx.property_color!=null ? ctx.property_color.getText() : "";
        String property       = ctx.property!=null ? ctx.property.getText() : "";

        String label = property;
        if(label.isEmpty()) {
        	if(connector.contains("<|--") || connector.contains("--|>")) label = "##inherit";
        	if(connector.contains("<..") || connector.contains("..>")) label = "##discriminator";
        }
        String connection = left + "-" + right + "-" + label;
        
       	puml.connections.put(connection, new PumlConnection(left,left_multiplicity,connector,right,right_multiplicity,property,property_color));

            
    }
        
    @Override
    public void enterHide_declaration(PlantumlParser.Hide_declarationContext ctx) {
    	String line = "hide " + ctx.type.getText();
        puml.misc.add(line);
    }
    
    @Override
    public void enterShow_statement(PlantumlParser.Show_statementContext ctx) {
    	String line = getFullText(ctx);
        puml.misc.add(line);
    }
        
    @Override
    public void enterSkinparam_declaration(PlantumlParser.Skinparam_declarationContext ctx) {
    	if(ctx.type!=null) {
	        List<LineContext> lines = ctx.line();        
	        List<String> content = lines.stream().map(PumlListener::getFullText).collect(Collectors.toList());
	        
	        String id = ctx.type.getText();
	        puml.skinparams.put(id, content);
	        
	        // System.out.println("skin: " + id);
	        
    	} else {
    		if(ctx.ident()!=null && ctx.value()!=null) {
    			String skin = "skinparam " + ctx.ident().getText() + " " + ctx.value().getText();
    			puml.misc.add(skin);
    		}
    	}
    }
    
	public List<String> legend = new LinkedList<>();
    
    @Override
    public void enterLegend_statement(PlantumlParser.Legend_statementContext ctx) {
        puml.legend.addAll( ctx.line().stream().map(PumlListener::getFullText).collect(Collectors.toList()) );
        
    }
    
    private static String getFullText(ParserRuleContext context) {
        if (context.start == null || context.stop == null || context.start.getStartIndex() < 0 || context.stop.getStopIndex() < 0)
            return context.getText();

        return context.start.getInputStream().getText(Interval.of(context.start.getStartIndex(), context.stop.getStopIndex()));
    }

    
}
