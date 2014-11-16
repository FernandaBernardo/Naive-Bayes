package main;

import java.util.regex.Pattern;

/**
 * Classe utilitária para Strings.
 */
public class StringUtils {

	public StringUtils() {
		throw new UnsupportedOperationException();
	}

	// Em construção
	private static final String words = "\\w";
	private static final String hours = "[(.*)&&[^((([0-1][0-9])|(2[0-3])):([0-5][0-9]))]]";
	private static final String urls = "((http|https|www)(://.+\\.\\.*))";
	
	private static final Pattern smartPunctuationPattern = 
			Pattern.compile(hours);
	
	private static final Pattern allPunctutaionPattern = Pattern.compile("(\\p{Punct}|:|(\\.+))");
	
	private static final Pattern internalExtraSpaces = Pattern.compile("\\p{Blank}+");
	
	public static String smartRemovePunctuation( final String string ){
		//return smartPunctuationPattern.matcher( string ).replaceAll(" ");
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	public static String removeAllPunctuation( final String string ) {
		return allPunctutaionPattern.matcher( string ).replaceAll(" "); 
	}
	
	public static String removeAllInternalExtraSpaces( final String string ){
		return internalExtraSpaces.matcher( string ).replaceAll(" ");
	}
	
}
