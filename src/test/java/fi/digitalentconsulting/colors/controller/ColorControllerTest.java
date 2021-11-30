package fi.digitalentconsulting.colors.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.digitalentconsulting.colors.entity.Color;
import fi.digitalentconsulting.colors.repository.ColorRepository;
import fi.digitalentconsulting.colors.service.DatamuseService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ColorControllerTest {
	private static String BASE_URL = "/api/v1/colors/";
	@Autowired
	WebApplicationContext webApplicationContext;
	@Autowired
	private ColorRepository colorRepository;
	public static final List<Color> initialColors = Arrays.asList(
			new Color("red", "#ff0000"),
			new Color("blue", "#0000ff"),
			new Color("fuchsia", "#FF00FF"),
			new Color("yellow", "#FFFF00"),		
			new Color("white", "#ffffff"),
			new Color("black", "#000000")
    		);
	private static final List<String> mockedSynonyms = Arrays.asList("ONE", "TWO"); 

	private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
        
    @Autowired
    DatamuseService datamuseService;
        
	@BeforeEach
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        colorRepository.saveAll(initialColors);
        Mockito.doReturn(mockedSynonyms)
        		.when(datamuseService).getSynonyms(anyString());
	}
	
	@Test
	public void smoketestProducts() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
	}

	@Test
	public void productListAsExpected() throws Exception {
        MvcResult res = mockMvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Color> colors = mapper.readValue(res.getResponse().getContentAsByteArray(),
        		new TypeReference<List<Color>>() {});
        assertThat(colors.size()).isEqualTo(initialColors.size());
	}

	@Test
	public void colorListFilteringWithPrefixSucceeds() throws Exception {
        MvcResult res = mockMvc.perform(get(BASE_URL+"?prefix=bl")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Color> colors = mapper.readValue(res.getResponse().getContentAsByteArray(),
        		new TypeReference<List<Color>>() {});
        List<Color> filtered = initialColors.stream().filter(c->c.getName().startsWith("bl")).toList();
        assertThat(colors.size()).isEqualTo(filtered.size());
	}

	@Test
	public void existingSingleColorIsFound() throws Exception {
		MvcResult res = mockMvc.perform(get(BASE_URL+"/blue")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Color returned = mapper.readValue(res.getResponse().getContentAsString(),
                Color.class);
		assertThat(returned.getName()).isEqualTo("blue");
	}

	@Test
	public void missingSingleProductIsNotFound() throws Exception {
		mockMvc.perform(get(BASE_URL+"/nosuchcolor")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
	}
	
	@Test
	public void creatingProperColorSucceeds() throws Exception {
		Color color = new Color("testcolor", "#123456");
		MvcResult res = mockMvc.perform(post(BASE_URL)
				.content(mapper.writeValueAsBytes(color))
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
		String location = res.getResponse().getHeader("location");
		assertThat(location).endsWith("/colors/"+"testcolor");
        Color returned = mapper.readValue(res.getResponse().getContentAsString(),
                Color.class);
		assertThat(returned.getName()).isEqualTo("testcolor");
		assertThat(returned.isCustom()).isEqualTo(false);
		colorRepository.delete(returned);
	}
	
	@Test
	public void creatingMisconfiguredProductFails() throws Exception {
		Color product = new Color();
		mockMvc.perform(post(BASE_URL)
				.content(mapper.writeValueAsBytes(product))
				.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void synonymsAreReturned() throws Exception {
		MvcResult res = mockMvc.perform(get(BASE_URL+"/blue/synonyms")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<String> returned = mapper.readValue(res.getResponse().getContentAsString(),
        		new TypeReference<List<String>>() {});
		assertThat(returned.size()).isEqualTo(mockedSynonyms.size());
		returned.forEach(syn -> {
			assertThat(syn).isIn(mockedSynonyms);
		});
	}
}
