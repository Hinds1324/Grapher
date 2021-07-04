package sini.grapher;

import java.awt.Color;
import java.util.function.Function;

public class SimpleFunctionCurve extends ParametricCurve {
	public SimpleFunctionCurve(Display display, Function<Double, Double> f) {
		super(display, (double[] p) -> new double[] {p[0], f.apply(p[0])});
	}
	
	public SimpleFunctionCurve(Display display, Function<Double, Double> f, Color color) {
		super(display, (double[] p) -> new double[] {p[0], f.apply(p[0])}, color);
	}
}
