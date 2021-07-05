package sini.grapher;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

import sini.complex.Complex;
import sini.complex.ComplexMath;

public class CurveList {
	
	private static Random rand = Main.rand;
	
	private static Color randomColor() {
		return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}

	public static ArrayList<Curve> getCurves() {
		ArrayList<Curve> curves = new ArrayList<Curve>();
		
		//curves.add(new ParametricCurve(p -> tetrationCardioid(p), new Interval(0, 4), 0.01));
		//curves.add(new SimpleFunctionCurve(x -> Math.sin(x), randomColor()));
		//curves.add(new SimpleFunctionCurve(x -> x*x, randomColor()));
		curves.add(new SimpleFunctionCurve(x -> funnyFractionFractal(x), randomColor()));
		curves.add(new ParametricCurve(x -> x, Interval.product(new Interval(1, 2), new Interval(3, 5)), 0.1));
		
		return curves;
	}

	private static double funnyFractionFractal(double x) {
		double result = Math.abs(x);
		
		int iterations = 300;
		
		for(int i = 1; i <= iterations; i++) {
			result = Math.abs(result - (double)iterations / i);
		}
		
		return result;
	}

	private static double[] tetrationCardioid(double[] p) {
		Complex c = new Complex(p[0], 0);
		c = expi(expi(c));
		return new double[] {c.real, c.imaginary};
	}
	
	private static Complex expi(Complex c) {
		return ComplexMath.exp(Complex.I.multiply(c.multiply(Math.PI / 2)));
	}
}
