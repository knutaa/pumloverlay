package no.paneon.oas.plantuml.pumloverlay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

public class Out {

	private Out() {
	}
	
	static final Logger LOG = LogManager.getLogger(Out.class);

	public static boolean silentMode = false;

	public static void println(String s) {
		if(!silentMode) LOG.log(Level.INFO, s);
	}
	
	public static void println() {
		if(!silentMode)  LOG.log(Level.INFO, "");
	}
	
	public static void printAlways(String s) {
		LOG.log(Level.INFO, s);
	}
	
	public static void printAlways(String format, Object ...args) {
		LOG.log(Level.INFO, format, args);
	}

	public static void println(String ... args) {
		if(!silentMode && LOG.isInfoEnabled()) {
			StringBuilder builder = new StringBuilder();
			String delim = "";
			for(String s : args) {
				builder.append(delim + s);
				delim = " ";
			}
			LOG.log(Level.DEBUG, builder.toString());
		}
	}
	
	public static void debug(String format, Object ...args) {
		LOG.log(Level.INFO, format, args);
	}
}
