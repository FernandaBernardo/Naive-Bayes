package main;

public class Tweet {
	private int id;
	private boolean sentiment;
	private String text;
	
	public Tweet(int id, boolean sentiment, String text) {
		this.id = id;
		this.sentiment = sentiment;
		this.text = text;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isHappy() {
		return sentiment;
	}
	public void setSentiment(boolean sentiment) {
		this.sentiment = sentiment;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
