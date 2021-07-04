package sini.grapher;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

public class ParametricCurve implements Curve {
	
	private Color color;
	private Function<double[], double[]> f;
	private Display display;
	private ArrayList<double[][]> mesh;
	
	public ParametricCurve(Display display, Function<double[], double[]> f) {
		this(display, f, new Color(60, 100, 255));
	}
	
	public ParametricCurve(Display display, Function<double[], double[]> f, Color color) {
		this.color = color;
		this.display = display;
		this.f = f;
		mesh = new ArrayList<double[][]>();
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public ArrayList<double[][]> getMesh() {
		return mesh;
	}
	
	public void updateMesh(Interval interval, double step) {
		mesh = new ArrayList<double[][]>();
		Iterator<double[][]> iter = interval.getPointCubeIterator(step);
		
		while(iter.hasNext()) {
			double[][] currentMeshSeg = iter.next();
			for(int i = 0; i < currentMeshSeg.length; i++) currentMeshSeg[i] = f.apply(currentMeshSeg[i]);
			mesh.add(currentMeshSeg);
		}
	}
	
	public void draw(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		for(double[][] meshSegment: mesh) {
			fillPolytope(g, meshSegment);
		}
	}

	private void fillPolytope(Graphics2D g, double[][] meshSegment) {
		switch(intLog(meshSegment.length)) {
		case 1: // 1-dimensional interval - draw a line
			// we are assuming the points on the mesh are 2 dimensional for now
			PointDouble p1 = display.getDisplayCoord(new PointDouble(meshSegment[0][0], meshSegment[0][1]));
			PointDouble p2 = display.getDisplayCoord(new PointDouble(meshSegment[1][0], meshSegment[1][1]));;
			g.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
			
		case 2: // 2-dimensional interval - draw a polygon
			// we are assuming the points on the mesh are 2 dimensional for now
			Polygon p = new Polygon();
			for(int i=0; i < meshSegment.length; i++) {
				p.addPoint((int)display.getDisplayX(meshSegment[i][0]), (int)display.getDisplayY(meshSegment[i][1]));
			}
			g.fill(p);
		}
	}
	
	private int intLog(int n) {
		int result = 0;
		while(n != 1) {
			n /= 2;
			result++;
		}
		return result;
	}
}
