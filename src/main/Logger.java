package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public final class Logger {

	private static StringBuilder BUFFER = new StringBuilder();
	private final String outputFilePath;
	
	public Logger( final String outputFilePath ) {
		this.outputFilePath = outputFilePath;
	}
	
	public void log( final String message, final Object... args ) {
		final String msg = String.format( message, args ) + "\n";
		System.out.print( msg );
		BUFFER.append( msg );
	}
	
	public void flush() {
		log( "Appending log to output file %s", outputFilePath );
		PrintWriter writer = null;
		try {
			final File file = new File( outputFilePath );
			if( !file.exists() ) 
				file.createNewFile();
			
			writer = new PrintWriter( file );
			writer.append( BUFFER.toString() );
		} catch( final IOException e) {
			throw new RuntimeException( "Couldn't write to output file!!!", e);
		} finally {
			if( writer != null ) {
				writer.close();
			}
		}
	}
}
