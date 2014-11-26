package main;

import java.util.regex.Pattern;

/**
 * Classe utilitária para Strings.
 */
public final class StringUtils {

	public StringUtils() {
		throw new UnsupportedOperationException();
	}

	private static final Pattern allPunctuationPattern = Pattern.compile("(\\p{Punct})");
	
	private static final Pattern internalExtraSpaces = Pattern.compile("\\p{Blank}+");
	
	public static String smartRemovePunctuation( final String string ){
		//return smartPunctuationPattern.matcher( string ).replaceAll(" ");
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	public static String removeAllPunctuation( final String string ) {
		return allPunctuationPattern.matcher( string ).replaceAll(" "); 
	}
	
	public static String removeAllInternalExtraSpaces( final String string ){
		return internalExtraSpaces.matcher( string ).replaceAll(" ");
	}
	
}
