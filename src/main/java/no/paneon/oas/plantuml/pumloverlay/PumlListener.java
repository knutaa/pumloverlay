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
        
        Map<String,PumlAttribute> attributes = new HashMap<>();
        
        if(ctx.attribute()!=null) {
        	ctx.attribute().forEach(arg -> {
        		String name = getFullText(arg.name_declaration());
        	    String type = getFullText(arg.type_ident());

        		
        		if(name.isEmpty()) {
        			name = type;
        			type = "";
        		}
        		
        		LOG.debug("attribute ... {}", name);

        		String attr_color  = getFullText(arg.attribute_color);
        		String attr_field  = getFullText(arg.attr_field());
        		String start_sep = getFullText(arg.start_sep);
        	    String visibility = getFullText(arg.visibility());
        	    String modifiers =  getFullText(arg.modifiers());
        	    String type_marker = arg.type_marker!=null ? arg.type_marker.getText() : "";
        	    String type_color = getFullText(arg.type_color);
        	    String enum_marker = getFullText(arg.enum_marker());
        	    String mandatory =  arg.mandatory()!=null ? getFullText(arg.mandatory().mand()) : "";
        	    String mandatory_color = arg.mandatory()!=null ? getFullText(arg.mandatory().color()) : "";
        	    String cardinality = getFullText(arg.cardinality());
        	    String end_sep = getFullText(arg.end_sep);
        	    String attr_content = getFullText(arg);
        		
        		PumlAttribute pa = new PumlAttribute(attr_color, attr_field, start_sep, visibility, modifiers, name, 
        											 type_marker, type_color, 
        											 enum_marker, type, 
        											 mandatory_color, mandatory, cardinality, end_sep, attr_content);

        		attributes.put(name, pa);
        		
        	});
        	
        	content.addAll( ctx.attribute().stream().map(PumlListener::getFullText).collect(Collectors.toList()) );  	
        }
        
        if(ctx.assignment()!=null) {
        	content.addAll( ctx.assignment().stream().map(PumlListener::getFullText).collect(Collectors.toList()) );  	
        }
                       
        List<String> stereotypes = ctx.stereotype().stream().map(StereotypeContext::getText).collect(Collectors.toList());
        
        content = content.stream()
					.filter(s -> !s.endsWith("--"))
					.collect(Collectors.toList());
        
        LOG.debug("enterClass: content={}", content);
        
        boolean blank = ctx.blank()!=null && !ctx.blank().isEmpty();
 
//        if(ctx.enum_field()!=null && !ctx.enum_field().isEmpty()) {
//        	ctx.enum_field().forEach(e -> Out.debug("enum_field: {}" + getFullText(e)) );
//            System.out.println("class: " + ctx.enum_field());
//        }
        
        Map<String,String> decorations = new HashMap<>();
        if(ctx.decoration()!=null) {
        	ctx.decoration().decorationItem().stream().forEach(item -> {
        		String color = item.color()!=null ? getFullText(item.color()) : "";
        		String label = getFullText(item.ident());
        		decorations.put(label,color); 
        	});
        }
 
        if(ctx.discriminators()!=null && ctx.discriminators().discriminator()!=null) {
        	List<String> discriminators = ctx.discriminators().discriminator().stream().map(disc -> {
        		
        		String color = getFullText(disc.color());
        		String label = getFullText(disc.line());

        		return color + label;
        		
        	}).collect(Collectors.toList());
        	
        	LOG.debug("discriminator: {}",  discriminators);
        	
        	puml.classes.put(cls, new PumlClass(cls,attributes, content,stereotypes,discriminators,blank,decorations) );
        	
        } else {
        	puml.classes.put(cls, new PumlClass(cls,attributes, content,stereotypes,blank,decorations));

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

    	StringBuilder legend = new StringBuilder();

    	puml.legend.addAll(
    		ctx.legend_rows().stream().map(row -> {
	        	StringBuilder res = new StringBuilder();
	    		row.pipe_and_cell().stream().forEach(cell -> {
	    			boolean pipe = cell.pipe()!=null && !cell.pipe().isEmpty();
	    			String content = getFullText(cell.legend_line());
	    			if(pipe) res.append("|");
	    			if(!content.isEmpty() && !content.startsWith("=")) res.append(" ");
	    			res.append(content);
	    			if(!content.isEmpty()) res.append(" ");
	    		});
	    		return res.toString();
	    	}).collect(Collectors.toList())
    	);
    	
    	
//    	puml.legend.addAll( ctx.legend_rows().stream().map(row -> {
//        	StringBuilder res = new StringBuilder();
//        	row.legend_line().stream().map(col -> {
//        		row.pipe().
//        	})
//        	
//    		if(line.pipe()!=null) res.append(getFullText(line.pipe()));
//    		if(line.legend_line()!=null) res.append(getFullText(line.legend_line()));
//    		return res.toString();
//    	}).collect(Collectors.toList()));
    			
//    	puml.legend.addAll( ctx.legend_line().stream().map(line -> {
//        	StringBuilder res = new StringBuilder();
//        	if(line.p)
//        } )
//      .collect(Collectors.toList()) );
        
    }
    
    private static String getFullText(ParserRuleContext context) {
    	String res="";
    	if(context==null) return res;
    	
        if (context.start == null || context.stop == null || context.start.getStartIndex() < 0 || context.stop.getStopIndex() < 0)
            res = context.getText();
        else
        	res = context.start.getInputStream().getText(Interval.of(context.start.getStartIndex(), context.stop.getStopIndex()));
        
        return res.replace("\n", "");
        
    }

    
}
