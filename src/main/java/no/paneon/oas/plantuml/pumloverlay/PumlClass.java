package no.paneon.oas.plantuml.pumloverlay;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PumlClass extends PumlFormat {

	static final Logger LOG = LogManager.getLogger(PumlClass.class);

	String name;
	List<String> content;
	List<String> stereotypes;
	List<String> discriminators;
	boolean blank;
	
	PumlClass(String cls, List<String> content, List<String> stereotypes) {
		this.name = cls;
		this.content = content;
		this.stereotypes = stereotypes;
		this.discriminators = new LinkedList<>();
		this.blank = false;
	}
	
	PumlClass(String cls, List<String> content, List<String> stereotypes, boolean blank) {
		this(cls,content,stereotypes);
		this.blank = blank;
	}
	
	PumlClass(String cls, List<String> content, List<String> stereotypes, List<String> discriminators, boolean blank) {
		this(cls,content,stereotypes,blank);
		this.discriminators.addAll(discriminators);
	}
	
	static final String BLANK_LINE = "{field}//                                        //";

	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append("class ").append(name); 
		
		stereotypes.forEach(t -> {
			s.append(" ").append(t);
		});
		
		s.append(" ").append("{").append(NEWLINE);
		
		List<String> sortedContent = sort(content);
		
		sortedContent.forEach(line -> {
			s.append(INDENT).append(line).append(NEWLINE);
		});
		
		if(this.blank) {
			s.append(INDENT).append(BLANK_LINE).append(NEWLINE);
		}
		
		if(!discriminators.isEmpty()) {
			s.append(INDENT).append("--").append(NEWLINE);
			s.append(INDENT).append("discriminator:").append(NEWLINE);
			discriminators.forEach(line -> {
				s.append(INDENT).append(line).append(NEWLINE);
			});
		}
		
		s.append("}").append(NEWLINE);

		return s.toString();

	}

	private List<String> sort(List<String> content) {
		List<String> res=content;
		Map<String,String> map = new HashMap<>();
		
		content.forEach(line -> {
			String cleaned = line.replaceAll("<.*>", "").replace("{field}", "");
			map.put(cleaned, line);
		});
		
		res = map.keySet().stream()
			.sorted()
			.collect(Collectors.partitioningBy(p -> p.startsWith("@")))
			.values().stream()
			.flatMap(List::stream)
			.map(map::get)
			.collect(Collectors.toList());
		
		LOG.debug("sort: map={}", map);
		
		return res;
	}
}
