package no.paneon.oas.plantuml.pumloverlay;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PumlConnection extends PumlFormat {

    static final Logger LOG = LogManager.getLogger(PumlConnection.class);

	String left; 
	String left_multiplicity;
	String connector;
	String right;
	String right_multiplicity;
	String property;
	String property_color;
	
	static final String SPACE = " ";
	
	public PumlConnection(String left, String left_multiplicity, String connector, String right,
			String right_multiplicity, String property, String property_color) {
		
		this.left = left;
		this.left_multiplicity = left_multiplicity;
		this.connector = connector;
		this.right = right;
		this.right_multiplicity = right_multiplicity;
		this.property = property;
		this.property_color = property_color;
		
		LOG.debug("Connector: {} {} {} {} {} {} {}", left, left_multiplicity, connector, right, right_multiplicity, property, property_color);
		
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append(this.left).append(SPACE);
		
		if(!this.left_multiplicity.isEmpty()) s.append(this.left_multiplicity).append(SPACE);
		
		s.append(this.connector).append(SPACE);
		
		if(!this.right.isEmpty()) s.append(this.right_multiplicity).append(SPACE);
		
		s.append(this.right).append(SPACE);
		
		if(!this.property.isEmpty()) {
			s.append(":").append(SPACE).append(this.property_color).append(this.property);
		}

		s.append(NEWLINE);
		
		return s.toString();

	}

}
