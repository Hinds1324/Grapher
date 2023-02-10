package sini.grapher;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

public class PointListCurve implements Curve {
	
	private Color color;
	private ArrayList<double[]> points;
	private float thickness;

	public PointListCurve(ArrayList<double[]> points) {
		this(points, Color.BLUE, 2);
	}
	
	public PointListCurve(ArrayList<double[]> points, Interval interval) {
		this(points);
	}
	
	public PointListCurve(ArrayList<double[]> points, Interval interval, Color color, float thickness) {
		this(points, color, thickness);
	}
	
	public PointListCurve(ArrayList<double[]> points, Color color, float thickness) {
		this.thickness = thickness;
		this.color = color;
		this.points = points;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void draw(Display display, Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);
		g.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		Iterator<double[]> iter = points.iterator();
		double[] lastPoint = iter.next();
		double[] curPoint = iter.next();
		
		while(iter.hasNext()) {
			PointDouble p1 = display.getDisplayPoint(new PointDouble(lastPoint[0], lastPoint[1]));
			PointDouble p2 = display.getDisplayPoint(new PointDouble(curPoint[0], curPoint[1]));
			
			g.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
			
			lastPoint = curPoint;
			curPoint = iter.next();
		}
	}

	@Override
	public void updateMesh(Interval interval, double step) {}

}
