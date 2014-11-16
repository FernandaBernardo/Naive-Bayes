package main;

public class Tweet {
	private final int id;
	private final boolean sentiment;
	private String text;
	
	public Tweet(int id, boolean sentiment, String text) {
		this.id = id;
		this.sentiment = sentiment;
		this.text = text;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isHappy() {
		return sentiment;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText( final String newText ){
		this.text = newText;
	}
	
}
