package sini.grapher;

import java.awt.Graphics2D;

public interface Curve {
	public abstract void draw(Graphics2D g);
	
	public abstract void updateMesh(Interval interval, double step);
}
