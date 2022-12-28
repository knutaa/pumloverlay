package no.paneon.oas.plantuml.pumloverlay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class Args {

	public class Common {
		@Parameter(names = { "--target-directory" }, description = "Target directory for generated overlays (defaults to current directory)")
		public String targetDirectory = ".";
		
		@Parameter(names = { "--generate-images" }, description = "Generate image of .puml files")
		public boolean generateImages = false;
		
	}

	public class Overlay extends Common {
		@Parameter(names = { "--file" }, description = "Plantuml input file (two or more)")
		public List<String> files = new LinkedList<>();
	
		@Parameter(names = { "--output" }, description = "Plantuml output file")
		public String output = "output.puml";
	
	}

	public class Batch extends Common {
		@Parameter(names = { "--prev" }, description = "Plantuml input file (prev/earlier version)", required=true)
		public String prev;
	
		@Parameter(names = { "--current" }, description = "Plantuml input file (current/latest version)", required=true)
		public String current;
		
		@Parameter(names = { "-c", "--config" }, description = "Config files (.json) - one or more")
		public List<String> configs = new ArrayList<>();

		@Parameter(names = { "--addedLabel" }, description = "Legend to use for added elements (in current but not in prev) - default is 'added'")
		public String addedLabel = "added";
		
		@Parameter(names = { "--addedColor" }, description = "Color to use for added elements - default is 'blue'")
		public String addedColor = "blue";
		
		@Parameter(names = { "--removedLabel" }, description = "Legend to use for removed (or replaced) elements (in prev but not identical in current) - default is 'removed'")
		public String removedLabel = "removed";
		
		@Parameter(names = { "--removedColor" }, description = "Color to use for removed elements - default is 'red'")
		public String removedColor = "red";
		
		@Parameter(names = { "--keep-tempdir" }, description = "Keep temporary directory (can be useful for debug)")
		public Boolean keepTempdir=false;
		
	}
	
}
