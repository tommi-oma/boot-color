package fi.digitalentconsulting.colors.service;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatamuseService {

	private static Logger LOGGER = LoggerFactory.getLogger(DatamuseService.class);
	private ObjectMapper mapper = new ObjectMapper();
	private final WebClient webClient;
	private final URL baseUrl;
	private final String wordPart; 
	
	public DatamuseService(String baseUrl, String wordPart) throws MalformedURLException {
		this.webClient = WebClient.create();
		this.baseUrl = new URL(baseUrl);
		this.wordPart = wordPart;
		
	}
	
	public List<String> getSynonyms(String word) throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
		final String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8.toString());
		LOGGER.info("Encoded word: '{}'", encodedWord);
		String jsonResult = webClient.get()
                .uri(baseUrl + wordPart + encodedWord)
                .retrieve()
                .bodyToMono(String.class)
                .block();
		List<MuseWord> words = mapper.readValue(jsonResult,
        		new TypeReference<List<MuseWord>>() {});
		return words.stream().map(MuseWord::getWord).limit(10).collect(Collectors.toList());
	}
}

class MuseWord {
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