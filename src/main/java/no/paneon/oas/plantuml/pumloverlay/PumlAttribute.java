package no.paneon.oas.plantuml.pumloverlay;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PumlAttribute extends PumlFormat {

    static final Logger LOG = LogManager.getLogger(PumlAttribute.class);

	String attribute_color;
	String attr_field;
	String start_sep;
    String visibility;
    String modifiers;
    String name;
    String type_marker;
    // String type_color;
    String enum_marker;
    String type;
    // String mandatory_color;
    String mandatory;
    String cardinality;
    String end_sep;
	String content;
	
	static final String SPACE = " ";
	
	public PumlAttribute(String attribute_color,
		String attr_field,
		String start_sep,
	    String visibility,
	    String modifiers,
	    String name,
	    String type_marker,
	    String type_color,
	    String enum_marker,
	    String type,
	    String mandatory_color,
	    String mandatory,
	    String cardinality,
	    String end_sep,
	    String content) {
		
		this.attribute_color = attribute_color;
		this.attr_field = attr_field;
		this.start_sep = start_sep;
	    this.visibility = visibility;
	    this.modifiers = modifiers;
	    this.name = name;
	    this.type_marker = type_marker;
	    // this.type_color = type_color;
	    this.enum_marker = enum_marker;
	    this.type = type_color + type;
	    // this.mandatory_color = mandatory_color;
	    this.mandatory = mandatory_color + mandatory;
	    this.cardinality = cardinality;
	    this.end_sep = end_sep;
	    this.content = content;
		
		LOG.debug("Attribute: {} {}", this.name, this.content);
		
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
 
		if(!this.mandatory.isEmpty() && this.attr_field.isEmpty()) this.attr_field = "{field}";
		
		if(!this.name.isEmpty()) {
			s.append(this.attribute_color);
			s.append(this.attr_field); if(!this.attr_field.isEmpty()) s.append(SPACE);
			s.append(this.start_sep); if(!this.start_sep.isEmpty()) s.append(SPACE);
			s.append(this.visibility); if(!this.visibility.isEmpty()) s.append(SPACE);
			s.append(this.modifiers); if(!this.modifiers.isEmpty()) s.append(SPACE);
			s.append(this.name); if(!this.name.isEmpty()) s.append(SPACE);
			s.append(this.type_marker); if(!this.type_marker.isEmpty()) s.append(SPACE);
			// s.append(this.type_color);
			s.append(this.type); if(!this.type.isEmpty()) s.append(SPACE);
			// s.append(this.mandatory_color);
			s.append(this.mandatory); if(!this.mandatory.isEmpty()) s.append(SPACE);
			s.append(this.cardinality); if(!this.cardinality.isEmpty()) s.append(SPACE);
			s.append(this.end_sep); if(!this.end_sep.isEmpty()) s.append(SPACE);
		} else {
			s.append(this.content);
		}
		
		return s.toString().trim() + NEWLINE;

	}

}
