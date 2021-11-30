package fi.digitalentconsulting.colors.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import fi.digitalentconsulting.colors.entity.Color;
import fi.digitalentconsulting.colors.repository.ColorRepository;
import fi.digitalentconsulting.colors.service.DatamuseService;
import fi.digitalentconsulting.colors.service.MuseWord;
import fi.digitalentconsulting.colors.service.WordServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/colors")
public class ColorController {
	private static Logger LOGGER = LoggerFactory.getLogger(ColorController.class);
	private ColorRepository colorRepository;
	private DatamuseService datamuseService;
	
	@Autowired
	public ColorController(ColorRepository colorRepository, DatamuseService datamuseService) {
		this.colorRepository = colorRepository;
		this.datamuseService = datamuseService;
	}

	@Operation(summary = "Get a list of colors")
	@ApiResponses(value = { 
			  @ApiResponse(responseCode = "200", description = "List of colors", 
			    content = { @Content(mediaType = "application/json", 
			        array = @ArraySchema(schema = @Schema(implementation = Color.class))) 
		        })
		})
	@GetMapping("/")
	public ResponseEntity<List<Color>> colors(@RequestParam(name="prefix", required = false) String prefix) {
		if (prefix == null) {
			return ResponseEntity.ok(colorRepository.findAll());
		}
		return ResponseEntity.ok(colorRepository.findAllNameBeginsWith(prefix));
	}
	

	@Operation(summary = "Get a color with a name")
	@ApiResponses(value = { 
			  @ApiResponse(responseCode = "200", description = "Color for a name", 
			    content = { @Content(mediaType = "application/json", 
			        schema = @Schema(implementation = Color.class)) 
		        }),
			  @ApiResponse(responseCode = "404", description = "Color not found")
		})
	@GetMapping("/{name}")
	public ResponseEntity<?> color(@Parameter(description = "Name of the color") 
										@PathVariable(name="name") String name) {
		Color found = colorRepository.findByName(name);
		if (found == null) {
			throw new NoSuchElementException("No color with name: " + name);
		}
		return ResponseEntity.ok(found);
	}
	
	@Operation(summary = "Save a new color")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Created a color", 
			    content = { @Content(mediaType = "application/json", 
			    	schema = @Schema(implementation = Color.class))
			    }),
			@ApiResponse(responseCode = "409", description = "Color name already exists",
				content = {@Content(mediaType = "application/json",
					schema=@Schema(implementation = ExceptionMessage.class))
				})
		})
	@PostMapping("/")
	public ResponseEntity<?> addColor(@Valid @RequestBody Color color) {
		LOGGER.info("Adding a new color: {}", color);
		if (colorRepository.findByName(color.getName()) == null) {
			Color saved = colorRepository.save(color); 
			URI location = ServletUriComponentsBuilder
		            .fromCurrentRequest().path("/{name}")
		            .buildAndExpand(saved.getName()).toUri();
			return ResponseEntity.created(location).body(saved);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionMessage("Name already exists", color.getName()));
	}
	
	@Operation(summary = "Delete a color")
	@DeleteMapping("/{name}")
	public ResponseEntity<?> removeColor(@Parameter(description = "The name of the color to remove") 
	@PathVariable(name="name") String name) {
		if (colorRepository.delete(new Color(name, null))) {
			return ResponseEntity.noContent().build();
		}
		throw new NoSuchElementException(String.format("Can not delete Color with name \"%s\" - not existing", name));
	}
	
	@Operation(summary = "Modify a color")
	@PutMapping("/{name}")
	public ResponseEntity<?> changeColor(@PathVariable(name="name") String name, @RequestBody Color color) {
		Color aboutToChange = colorRepository.findByName(name);
		if (aboutToChange == null) {
			return ResponseEntity.notFound().build();
		}
		if (color.getHex()!=null) {
			aboutToChange.setHex(color.getHex());
		}
		if (color.getRgb() != null) {
			aboutToChange.setRgb(color.getRgb());
		}
		Color saved = colorRepository.save(color);
		return ResponseEntity.ok(saved);
	}
	
	@Operation(summary = "Get synonyms for a color's name")
	@ApiResponses(value = { 
			  @ApiResponse(responseCode = "200", description = "Synonyms, max 10", 
					    content = { @Content(mediaType = "application/json", 
					    		array = @ArraySchema(schema = @Schema(oneOf = {String.class, MuseWord.class}))) 
					    }),
			  @ApiResponse(responseCode = "404", description = "Color not found", 
			    content = @Content(schema=@Schema(implementation=ExceptionMessage.class)))})
	@GetMapping("/{name}/synonyms")
	public ResponseEntity<?> colorNameSynonyms(@Parameter(description="Color name") @PathVariable String name,
			@RequestParam(name="full") Optional<Boolean> full) throws NoSuchElementException {
		Color color = colorRepository.findByName(name);
		if (color == null) {
			throw new NoSuchElementException("No color with name " + name);
		}
		try {
			if (full.isPresent()) {
				List<MuseWord> words = datamuseService.getMatchingWords(color.getName());
				return ResponseEntity.ok(words);								
			} else {
				List<String> synonyms = datamuseService.getSynonyms(color.getName());
				return ResponseEntity.ok(synonyms);				
			}
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new WordServiceException("Problem with synonyms", e);
		}
	}

}









