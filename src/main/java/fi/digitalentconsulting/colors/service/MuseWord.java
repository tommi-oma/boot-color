package fi.digitalentconsulting.colors.service;

import java.util.List;

public class MuseWord {
	private String word;
	private Integer score;
	private List<String> tags;
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}