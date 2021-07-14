package sini.grapher;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

import sini.complex.Complex;
import sini.complex.ComplexMath;

@SuppressWarnings("unused")
public class CurveList {
	
	private static Random rand = Main.rand;
	
	private static Color randomColor() {
		return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}

	public static ArrayList<Curve> getCurves() {
		ArrayList<Curve> curves = new ArrayList<Curve>();
		
		//curves.add(new ParametricCurve(p -> p, Interval.product(new Interval(-3, 3.09), new Interval(-3, 3.09)), 0.1));
		//curves.add(new ParametricCurve(p -> tetrationCardioid(p), new Interval(0, 4), 0.01));
		curves.add(new SimpleFunctionCurve(x -> Math.sin(x)));
		//curves.add(new SimpleFunctionCurve(x -> Math.tan(x), randomColor()));
		//curves.add(new SimpleFunctionCurve(x -> funnyFractionFractal(x), randomColor()));
		//curves.add(new ParametricCurve(p -> function2D(rotation(p,45)), Interval.product(new Interval(-3, 3), new Interval(-3, 3)), 0.1));
		//curves.add(new ParametricCurve(p -> rotation(tetrationCardioid(p), 70), new Interval(0, 4), 0.01, new Color(200, 80, 40)));
		//curves.add(new ParametricCurve(p -> p, Interval.product(new Interval(-3, 3), new Interval(-3, 3)), 0.1));
		
		return curves;
	}

	private static double funnyFractionFractal(double x) {
		int iterations = 1000;
		double result = Math.abs(iterations * x);
		
		for(int i = 1; i <= iterations; i++) {
			result = Math.abs(result - (double)iterations / i);
		}
		
		return result;
	}
	
	private static double[] function2D(double[] p) {
		double x = p[0];
		double y = p[1];
		
		return new double[] {x*x, x*y};
	}
	
	private static double[] rotation(double[] p, double theta) {
		double x = p[0];
		double y = p[1];
		
		theta = Math.toRadians(theta);
		
		return new double[] {Math.cos(theta)*x + Math.sin(theta)*y, Math.sin(theta)*x - Math.cos(theta)*y};
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
