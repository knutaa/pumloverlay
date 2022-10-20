package no.paneon.oas.plantuml.pumloverlay;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener extends BaseErrorListener {

	@Override
	public void syntaxError( Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e ) {
		
		Out.println("issue: " + msg + " (line=" + line + " pos=" + charPositionInLine + ")");
		throw new Error();
		
	}

}
