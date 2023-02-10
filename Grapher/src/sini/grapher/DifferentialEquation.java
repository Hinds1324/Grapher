package sini.grapher;

import java.util.ArrayList;
import java.util.function.Function;
import java.awt.Color;

public class DifferentialEquation {
	
	private Function<double[], Double> f;
	
	public DifferentialEquation(Function<double[], Double> f) {
		this.f = f;
	}
	
	public Curve getSolutionCurve(Interval interval, double t0, double x0, double step, int maxSteps) {
		return getSolutionCurve(interval, t0, x0, step, maxSteps, Color.BLUE, 2);
	}
	
	public Curve getSolutionCurve(Interval interval, double t0, double x0, double step, int maxSteps, Color color, float thickness) {
		double t = t0;
		double x = x0;
		
		int steps = 0;
		
		double[] currentPoint = new double[] {t, x};
		
		ArrayList<double[]> points = new ArrayList<>();
		
		while(interval.contains(currentPoint) && steps < maxSteps) {
			steps++;
			
			currentPoint = new double[] {t, x};
			
			points.add(currentPoint);
			
			double m = f.apply(currentPoint);
			
			if(m != 0) {
				double tstep = step;
				tstep = Math.min(step, Math.abs(step / m));
				 
				t += tstep;
				x += m*tstep;
				
			} else {
				t += step;
				
			}
		}
		
		t = t0;
		x = x0;

		currentPoint = new double[] {t, x};
		
		steps = 0;
		
		while(interval.contains(currentPoint) && steps < maxSteps) {
			steps++;
			
			double m = f.apply(currentPoint);
			
			if(m != 0) {
				double tstep = step;
				tstep = Math.min(step, Math.abs(step / m));
				 
				t -= tstep;
				x -= m*tstep;
			} else {
				t -= step;
				
			}

			currentPoint = new double[] {t, x};
			
			points.add(0, currentPoint);
		}
		
		return new PointListCurve(points, color, thickness);
	}
	
	public DirectionField getDirectionField(Interval interval, double step) {
		return new DirectionField(f, interval, step);
	}

}
