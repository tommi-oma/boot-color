package fi.digitalentconsulting.colors.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import fi.digitalentconsulting.colors.entity.Color;

@Repository
public class ColorRepository {
	private static Logger LOGGER = LoggerFactory.getLogger(ColorRepository.class);
	private Map<String, Color> colors = new HashMap<>();

	public ColorRepository() {
		save(new Color("red", "#ff0000"));
		save(new Color("blue", "#0000ff"));
		save(new Color("fuchsia", "#FF00FF"));
		save(new Color("yellow", "#FFFF00"));		
		save(new Color("white", "#ffffff"));
		save(new Color("black", "#000000"));
	}

	public Color save(final Color color) {
		colors.put(color.getName(), color);
		return color;
	}

	public Color findByName(final String name) {
		Color found = colors.get(name);
		return found;
	}
	
	public List<Color> findAllNameBeginsWith(String prefix) {
		List<Color> filteredColors = colors.values().stream()
				.filter(c->c.getName().startsWith(prefix))
				.collect(Collectors.toList());
		return filteredColors;
	}

	public List<Color> findAll() {
		// Safe copy
		return new ArrayList<Color>(colors.values());
	}
	
	public boolean delete(Color color) {
		return (colors.remove(color.getName()) != null);
	}
	
}
