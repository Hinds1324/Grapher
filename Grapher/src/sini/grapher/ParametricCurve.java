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
	private ArrayList<double[][]> mesh;
	
	public ParametricCurve(Function<double[], double[]> f) {
		this(f, new Color(60, 100, 255));
	}
	
	public ParametricCurve(Function<double[], double[]> f, Interval interval, double step) {
		this(f);
		updateMesh(interval, step);
	}
	
	public ParametricCurve(Function<double[], double[]> f, Interval interval, double step, Color color) {
		this(f, color);
		updateMesh(interval, step);
	}
	
	public ParametricCurve(Function<double[], double[]> f, Color color) {
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
	
	public void updateMesh(Interval interval, double step) {
		mesh = new ArrayList<double[][]>();
		Iterator<double[][]> iter = interval.getPointCubeIterator(step);
		
		while(iter.hasNext()) {
			double[][] currentMeshSeg = iter.next();
			
			// If 2 dimensional segment, swap the last two points so that we have a simple polygon (this fix is temporary and should be generalised to higher dimensions)
			if(currentMeshSeg.length == 4) {
				double[] temp = currentMeshSeg[2];
				currentMeshSeg[2] = currentMeshSeg[3];
				currentMeshSeg[3] = temp;
			}
			
			for(int i = 0; i < currentMeshSeg.length; i++) currentMeshSeg[i] = f.apply(currentMeshSeg[i]);
			mesh.add(currentMeshSeg);
		}
	}

	public void draw(Display display, Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color);
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		for(double[][] meshSegment: mesh) {
			fillPolytope(display, g, meshSegment);
		}
	}

	private void fillPolytope(Display display, Graphics2D g, double[][] meshSegment) {
		switch(intLog(meshSegment.length)) {
		case 1: // 1-dimensional interval - draw a line
			// we are assuming the points on the mesh are 2 dimensional for now
			PointDouble p1 = display.getDisplayPoint(new PointDouble(meshSegment[0][0], meshSegment[0][1]));
			PointDouble p2 = display.getDisplayPoint(new PointDouble(meshSegment[1][0], meshSegment[1][1]));;
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
