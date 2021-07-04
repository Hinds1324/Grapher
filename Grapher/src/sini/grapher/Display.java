package sini.grapher;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Display extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {
	private static final long serialVersionUID = -6696561578530033238L;
	
	private static final int DEFAULT_PADDING = 50;
	
	private PointDouble viewPoint; // Represents the top left coordinate of the viewport
	private double zoom;
	
	ArrayList<Curve> curves;

	public Display() {
		zoom = 2;
		setBackground(Color.WHITE);
		
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		
		curves = CurveList.getCurves();
	}
	
	private void updateCurves() {
		for(Curve curve: curves) {
			if(curve instanceof SimpleFunctionCurve) {
				curve.updateMesh(new Interval(getPlaneX(0), getPlaneX(getWidth())), 1.0 / (100 * zoom));
			}
		}
	}
	
	/**
	 * Centres the viewport on the given point p, where p represents a point on the coordinate plane.
	 */
	public void centerViewpoint(PointDouble p) {
		viewPoint = new PointDouble(
				p.x - (double)getWidth() / (2 * DEFAULT_PADDING * zoom),
				p.y + (double)getHeight() / (2 * DEFAULT_PADDING * zoom)
				);
		
		updateCurves();
		repaint();
	}
	
	/**
	 * Moves the viewport such that the point on the plane pointPlane sits at the point pointDisplay relative to the display.
	 */
	public void movePlanePointToDisplayPoint(PointDouble pointPlane, Point pointDisplay) {
		PointDouble pointDisplayPlaneCoord = getPlaneCoord(pointDisplay);
		
		viewPoint = new PointDouble(
				viewPoint.x + pointPlane.x - pointDisplayPlaneCoord.x,
				viewPoint.y + pointPlane.y - pointDisplayPlaneCoord.y
				);
		
		updateCurves();
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// Draw grid lines
		
		g2.setColor(Color.LIGHT_GRAY);
		
		double lineX = DEFAULT_PADDING * zoom * ((1 - viewPoint.x) % 1); 
		double lineY = DEFAULT_PADDING * zoom * (viewPoint.y % 1); 
		
		while(lineX < getWidth()) {
			g2.drawLine((int)lineX, 0, (int)lineX, getHeight());
		
			lineX += DEFAULT_PADDING * zoom;
		}
		
		while(lineY < getHeight()) {
			g2.drawLine(0, (int)lineY, getWidth(), (int)lineY);
		
			lineY += DEFAULT_PADDING * zoom;
		}
		
		
		// Draw axis lines
		
		g2.setColor(Color.BLACK);
		
		double yAxisCoord = Math.max(0, Math.min(getWidth(), getDisplayX(0)));
		g2.drawLine((int)yAxisCoord, 0, (int)yAxisCoord, getHeight());
		
		double xAxisCoord = Math.max(0, Math.min(getWidth(), getDisplayY(0)));
		g2.drawLine(0, (int)xAxisCoord, getWidth(), (int)xAxisCoord);
		
		
		// Draw graph
		for(Curve curve: curves) {
			curve.draw(this, g2);
		}
	}
	
	
	/**
	 * Returns the x-coordinate on the plane that corresponds to the x-coordinate on the display given by {@code x}.
	 * @param x the x-coordinate of a point on the display.
	 * @return the x-coordinate on the plane that corresponds to the x-coordinate on the display given by {@code x}.
	 */
	public double getPlaneX(double x) {
		return x / (zoom * DEFAULT_PADDING) + viewPoint.x;
	}
	
	
	/**
	 * Returns the y-coordinate on the plane that corresponds to the y-coordinate on the display given by {@code y}.
	 * @param y the y-coordinate of a point on the display.
	 * @return the y-coordinate on the plane that corresponds to the y-coordinate on the display given by {@code y}.
	 */
	public double getPlaneY(double y) {
		return -y / (zoom * DEFAULT_PADDING) + viewPoint.y;
	}
	
	
	/**
	 * Returns the x-coordinate on the display that corresponds to the x-coordinate on the plane given by {@code x}.
	 * @param x the x-coordinate of a point on the plane.
	 * @return the x-coordinate on the display that corresponds to the x-coordinate on the plane given by {@code x}.
	 */
	public double getDisplayX(double x) {
		return (int)((x - viewPoint.x) * zoom * DEFAULT_PADDING);
	}
	
	
	/**
	 * Returns the y-coordinate on the display that corresponds to the y-coordinate on the plane given by {@code y}.
	 * @param y the y-coordinate of a point on the plane.
	 * @return the y-coordinate on the display that corresponds to the y-coordinate on the plane given by {@code y}.
	 */
	public double getDisplayY(double y) {
		return (int)((-y + viewPoint.y) * zoom * DEFAULT_PADDING);
	}
	
	
	/**
	 * Returns the point on the plane that corresponds to the point on the display given by {@code p}.
	 * @param p a point on the display.
	 * @return the point on the plane that corresponds to the point on the display given by {@code p}.
	 */
	public PointDouble getPlaneCoord(Point p) {
		return new PointDouble(getPlaneX(p.x), getPlaneY(p.y));
	}
	
	
	/**
	 * Returns the point on the display that corresponds to the point on the plane given by {@code p}.
	 * @param p a point on the plane.
	 * @return the point on the display that corresponds to the point on the plane given by {@code p}.
	 */
	public PointDouble getDisplayCoord(PointDouble p) {
		return new PointDouble(getDisplayX(p.x), getDisplayY(p.y));
	}
	
	
	// Mouse Listeners

	private Point anchorPos;
	
	@Override
	public void mousePressed(MouseEvent e) {
		anchorPos = e.getPoint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		Point mousePos = e.getPoint();
		PointDouble planePos = getPlaneCoord(anchorPos);
		movePlanePointToDisplayPoint(planePos, mousePos);
		anchorPos = mousePos;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point mousePos = e.getPoint();
		PointDouble planePos = getPlaneCoord(mousePos);
		
		if(e.getWheelRotation() < 0)
			zoom *= 1.2;
		else {
			zoom /= 1.2;
		}
		
		movePlanePointToDisplayPoint(planePos, mousePos);
	}

	
	// Unused listener methods
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
