package sini.grapher;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

public class DirectionField implements Curve {
	
	private Color color;
	private Function<double[], Double> f; // Let's assume f : R^2 -> R
	private ArrayList<double[][]> mesh;

	public DirectionField(Function<double[], Double> f) {
		this(f, Color.RED);
	}
	
	public DirectionField(Function<double[], Double> f, Interval interval, double step) {
		this(f);
		updateMesh(interval, step);
	}
	
	public DirectionField(Function<double[], Double> f, Interval interval, double step, Color color) {
		this(f, color);
		updateMesh(interval, step);
	}
	
	public DirectionField(Function<double[], Double> f, Color color) {
		this.color = color;
		this.f = f;
		mesh = new ArrayList<double[][]>();
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public ArrayList<double[][]> getMesh() {
		return mesh;
	}

	@Override
	public void draw(Display display, Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		for(double[][] meshSegment: mesh) {
			PointDouble p1 = display.getDisplayPoint(new PointDouble(meshSegment[0][0], meshSegment[0][1]));
			PointDouble p2 = display.getDisplayPoint(new PointDouble(meshSegment[1][0], meshSegment[1][1]));;
			g.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
		}
	}

	@Override
	public void updateMesh(Interval interval, double step) {
		mesh = new ArrayList<double[][]>();
		Iterator<double[]> iter = interval.getPointIterator(step);
		
		while(iter.hasNext()) {
			double[] currentMeshPoint = iter.next();
			
			// We're assuming the point is 2 dimensional for now. I really don't care about doing 3D direction fields
			if(currentMeshPoint.length == 2) {
				double m = f.apply(new double[]{currentMeshPoint[0], currentMeshPoint[1]}); 
				double r = step/3;
				
				double x1 = 0;
				double x2 = 0;
				double y1 = -r;
				double y2 = -y1;
				
				if(m != 0) {
					x1 = -Math.sqrt(r*r / (m*m + 1));
					x2 = -x1;
					
					y1 = m * x1;
					y2 = m * x2;
				}
				
				// Now we want a line that is centred at the currentMeshPoint and has slope equal to derivative.
				x1 += currentMeshPoint[0]; x2 += currentMeshPoint[0];
				y1 += currentMeshPoint[1]; y2 += currentMeshPoint[1];
				
				double[] p1 = new double[] {x1, y1};
				double[] p2 = new double[] {x2, y2};
				
				mesh.add(new double[][] {p1, p2});
			}
		}
	}
	
	
}
