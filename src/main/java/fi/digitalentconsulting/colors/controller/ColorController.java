package fi.digitalentconsulting.colors.controller;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fi.digitalentconsulting.colors.entity.Color;
import fi.digitalentconsulting.colors.repository.ColorRepository;
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
	
	@Autowired
	public ColorController(ColorRepository colorRepository) {
		super();
		this.colorRepository = colorRepository;
	}

	@Operation(summary = "Get a list of colors")
	@ApiResponses(value = { 
			  @ApiResponse(responseCode = "200", description = "List of colors", 
			    content = { @Content(mediaType = "application/json", 
			        array = @ArraySchema(schema = @Schema(implementation = Color.class))) 
		        })
		})
	@GetMapping("/")
	public List<Color> colors(@RequestParam(name="prefix", required = false) String prefix) {
		if (prefix == null) {
			return colorRepository.findAll();
		}
		return colorRepository.findAllNameBeginsWith(prefix);
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
	public Color color(@Parameter(description = "Name of the color") 
										@PathVariable(name="name") String name) {
		Color found = colorRepository.findByName(name);
		if (found == null) {
			throw new NoSuchElementException("Ei oo väriä");
		}
		return found;
	}
	
	@Operation(summary = "Save a new color")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "201", description = "Created a color", 
			    content = { @Content(mediaType = "application/json", 
			    	schema = @Schema(implementation = Color.class))
			    }),
			@ApiResponse(responseCode = "409", description = "Color name already exists",
				content = {@Content(mediaType = "text/plain",
					schema=@Schema(implementation = String.class))
				})
		})
	@PostMapping("/")
	public Color addColor(@RequestBody Color color) {
		LOGGER.info("Adding a new color: {}", color);
		if (colorRepository.findByName(color.getName()) == null) {
			Color saved = colorRepository.save(color); 
			return saved;
		}
		throw new RuntimeException("Color already present with that name");
	}
	
	@Operation(summary = "Delete a color")
	@DeleteMapping("/{name}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeColor(@Parameter(description = "The name of the color to remove") 
	@PathVariable(name="name") String name) {
		if (colorRepository.delete(new Color(name, null))) {
			return;
		}
		throw new NoSuchElementException("No such color");
	}
	
	@Operation(summary = "Modify a color")
	@PutMapping("/{name}")
	public Color changeColor(@PathVariable(name="name") String name, @RequestBody Color color) {
		Color aboutToChange = colorRepository.findByName(name);
		if (aboutToChange == null) {
			throw new NoSuchElementException("No such color");
		}
		if (color.getHex()!=null) {
			aboutToChange.setHex(color.getHex());
		}
		if (color.getRgb() != null) {
			aboutToChange.setRgb(color.getRgb());
		}
		Color saved = colorRepository.save(color);
		return saved;
	}
}
