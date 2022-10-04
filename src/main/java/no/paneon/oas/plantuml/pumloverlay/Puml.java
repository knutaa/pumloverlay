package no.paneon.oas.plantuml.pumloverlay;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Puml extends PumlFormat {

	Map<String,PumlClass> classes;
	Map<String,PumlClass> enums;
	Map<String,PumlConnection> connections;
	Map<String,List<String>> skinparams;
	List<String> misc;
	List<String> legend;

	Puml() {
		classes = new HashMap<>();
		enums = new HashMap<>();
		connections = new HashMap<>();

		misc = new LinkedList<>();
		skinparams = new HashMap<>();
		legend = new LinkedList<>();
	}
	
	static String NEWLINE = "\n";
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append("@startuml").append(NEWLINE);
		
		classes.entrySet().forEach(c -> {
			s.append(c.getValue().toString()).append(NEWLINE);
		});
		
		enums.entrySet().forEach(c -> {
			s.append(c.getValue().toString()).append(NEWLINE);
		});
		
		List<String> printedConnections = new LinkedList<>();
		connections.entrySet().forEach(c -> {
			String conn = c.getValue().toString();
			if(!printedConnections.contains(conn)) s.append(conn).append(NEWLINE);
			printedConnections.add(conn);
		});
		
		misc.stream().filter(c->!c.contains("skinparam")).forEach(c -> {
			s.append(c).append(NEWLINE);
		});
		
		skinparams.entrySet().forEach(c -> {
			s.append("skinparam ").append(c.getKey()).append(" {").append(NEWLINE);
			c.getValue().forEach(line -> {
				s.append(INDENT).append(line).append(NEWLINE);
			});
			s.append("}").append(NEWLINE);
		});
		
		if(!legend.isEmpty()) {
			s.append("legend ").append(NEWLINE);
			Map<Boolean,List<String>> groups = legend.stream().collect(Collectors.groupingBy(l -> l.contains("|")));
			
			if(groups.containsKey(true)) {
				groups.get(true).forEach(c -> {
					if(c.contains("|") && !c.startsWith("|")) {
						s.append(INDENT).append("|").append(c).append("|").append(NEWLINE);
					} else {
						s.append(c).append(NEWLINE);
					}
				});
			} 
			
			if(groups.containsKey(false)) {
				groups.get(false).forEach(c -> s.append(c).append(NEWLINE) );
			}
			s.append("endlegend ").append(NEWLINE);
			
		}
		
		misc.stream().filter(c->c.contains("skinparam")).forEach(c -> {
			s.append(c).append(NEWLINE);
		});
		
		s.append("@enduml").append(NEWLINE);

		return s.toString();
	}
	
}
