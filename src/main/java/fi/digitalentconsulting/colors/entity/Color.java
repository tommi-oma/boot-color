package fi.digitalentconsulting.colors.entity;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Color {
	private static Logger LOGGER = LoggerFactory.getLogger(Color.class);

	private String name;
	private int[] rgb = new int[3];
	private String hex;
	private boolean custom;
	
	public Color() {}
	
	public Color(final String name, final String hex) {
		this.name = name;
		setHex(hex); // also sets the rgb
	}
	
	public String hexFromRgb(int r, int g, int b) {
		if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
			throw new IllegalArgumentException("Values need to be between 0 and 255");
		return String.format("#%02x%02x%02x", r, g, b);
	}
	
	public String hexFromRgb(int[] rgb) {
		if (rgb == null) return null;
		if (rgb.length != 3)
			throw new IllegalArgumentException("Need three values: RGB");
		return hexFromRgb(rgb[0], rgb[1], rgb[2]);
	}
	
	public int[] rgbFromHex(String hex) {
		if (hex == null) return null;
		int[] rgb = new int[3];
		rgb[0] = Integer.valueOf(hex.substring(1, 3), 16);
		rgb[1] = Integer.valueOf(hex.substring(3, 5), 16);
		rgb[2] = Integer.valueOf(hex.substring(5, 7), 16);
		return rgb;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHex() {
		return hex;
	}
	public void setHex(String hex) {
		LOGGER.info("Setting hex for {}: {}", name, hex);
		this.hex = hex == null ? hex : hex.toLowerCase();
		this.rgb = rgbFromHex(this.hex);
	}
	public int[] getRgb() {
		return rgb;
	}
	public void setRgb(int[]rgb) {
		this.rgb = rgb;
		if (rgb != null) this.hex = hexFromRgb(rgb);
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	@Override
	public String toString() {
		return "Color [name=" + name + ", rgb=" + Arrays.toString(rgb) + ", hex=" + hex + ", custom=" + custom + "]";
	}

}
