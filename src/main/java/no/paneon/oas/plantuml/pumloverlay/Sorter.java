package no.paneon.oas.plantuml.pumloverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sorter {

	static final Logger LOG = LogManager.getLogger(Sorter.class);

	static String ENUM_VALUE_PREFIX = "{field} //";
	static String NEWLINE = "\n";
	
	public static List<String> sort(List<String> data) {
		List<String> res = new LinkedList<>();
				
		Map<String,String> map = new HashMap<>();
		
		ListIterator<String> iter = data.listIterator();
		int count=0;
		while(iter.hasNext()) {
			count++;
			String line = iter.next();
			String cleaned = removePrefix(line);
			String parts[] = cleaned.split(":");
			if(parts.length>0) {
				String key = parts[0];
				LOG.debug("sort:: key={}",  key);

				if(line.contains("ENUM")) {
					String enumValues = getEnumValues(iter);
					line = line + enumValues;
				}
				if(map.containsKey(key)) key = key + "_" + count;
				map.put(key, line);
				LOG.debug("sort:: add key={} line={}",  key, line);
			}
		}
		
		List<String> sortedList = new ArrayList<>(map.keySet());
		Collections.sort(sortedList);
		
		for(String key : sortedList) {
			res.add(map.get(key));
		}
		
		return res;
		
	}
	
	private static String getEnumValues(ListIterator<String> iter) {
		StringBuilder res = new StringBuilder();
		boolean finished=false;
		while(!finished && iter.hasNext()) {
			String line = iter.next();
			LOG.debug("getEnumValues:: line={}", line);

			finished = !line.contains(ENUM_VALUE_PREFIX);
			if(finished) {
				iter.previous();
			} else {
				res.append(NEWLINE);
				res.append(line);
			} 
		}
		
		LOG.debug("getEnumValues:: res={}", res.toString());

		return res.toString();
	}

	static String removePrefix(String s) {
		return s.replace("{field}", "").replaceAll("<color:[a-z]+>","").replaceAll("^[ ]+", "");
	}
	
}
