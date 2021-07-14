package sini.grapher;

import java.awt.Color;
import java.util.HashMap;

public class Theme {
	private HashMap<String, Color> theme = new HashMap<String, Color>();
	
	public static final String BACKGROUND = "bg";
	public static final String AXES = "axes";
	public static final String MINOR_TICKS = "minor ticks";
	public static final String MAJOR_TICKS = "major ticks";
	public static final String CURVE_DEFAULT = "curve default";
	
	public Theme(HashMap<String, Color> theme) {
		this.theme = theme;
	}
	
	public Theme() {
		super();
	}

	public void setColor(String colorKey, Color color) {
		theme.put(colorKey, color);
	}
	
	public Color getColor(String colorKey) {
		return theme.get(colorKey);
	}
}
