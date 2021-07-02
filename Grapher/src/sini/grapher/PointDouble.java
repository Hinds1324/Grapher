package sini.grapher;

import java.awt.Point;

public class PointDouble {
	public double x, y;
	
	public PointDouble(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public PointDouble() {
		this.x = 0;
		this.y = 0;
	}
	
	public Point getPoint() {
		return new Point((int)x, (int)y);
	}
}
