package no.paneon.oas.plantuml.pumloverlay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class Args {

	@Parameter(names = { "--file" }, description = "Plantuml input file (two or more)")
	public List<String> files = new LinkedList<>();

	@Parameter(names = { "--output" }, description = "Plantuml output file")
	public String output = "output.puml";

}
