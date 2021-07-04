package sini.grapher;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import sini.complex.Complex;
import sini.complex.ComplexMath;

public class CurveList {
	
	private static Random rand = Main.rand;
	
	private static Color randomColor() {
		return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	}

	public static ArrayList<Curve> getCurves() {
		ArrayList<Curve> curves = new ArrayList<Curve>();
		
		curves.add(new ParametricCurve(p -> tetrationCardoid(p), new Interval(0, 4), 0.01));
		curves.add(new SimpleFunctionCurve(x -> Math.sin(x), randomColor()));
		curves.add(new SimpleFunctionCurve(x -> x*x, randomColor()));
		
		return curves;
	}

	private static double[] tetrationCardoid(double[] p) {
		Complex c = new Complex(p[0], 0);
		c = expi(expi(c));
		return new double[] {c.real, c.imaginary};
	}
	
	private static Complex expi(Complex c) {
		return ComplexMath.exp(Complex.I.multiply(c.multiply(Math.PI / 2)));
	}
}
