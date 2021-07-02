package sini.grapher;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

import sini.complex.Complex;
import sini.complex.ComplexMath;

public class Display extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {
	private static final long serialVersionUID = -6696561578530033238L;
	
	private static final int DEFAULT_PADDING = 50;
	
	private PointDouble viewPoint; // Represents the top left coordinate of the viewport
	private double zoom;

	public Display() {
		zoom = 2;
		setBackground(Color.WHITE);
		
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
	}
	
	/**
	 * Centres the viewport on the given point p, where p represents a point on the coordinate plane.
	 */
	public void centerViewpoint(PointDouble p) {
		viewPoint = new PointDouble(
				p.x - (double)getWidth() / (2 * DEFAULT_PADDING * zoom),
				p.y + (double)getHeight() / (2 * DEFAULT_PADDING * zoom)
				);
		
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
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(new Color(60, 100, 255));
		g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		int fineness = 2048;
		Polygon poly = new Polygon();
		
		for(int i = 0; i <= fineness; i++) {
			double x = 4 * (double)i/(double)fineness + 1;
			Complex c = expi(expi(new Complex(x,0)));
			
			PointDouble p = getPointFromComplex(c);
			Point drawPoint = getDisplayCoord(p).getPoint();
			
			poly.addPoint(drawPoint.x, drawPoint.y);
		}
		
		g2.drawPolygon(poly);
		g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		g2.setColor(Color.BLACK);
		for(int i = 0; i < poly.npoints; i++) {
			Point p = new Point(poly.xpoints[i], poly.ypoints[i]);
			g2.drawLine(p.x, p.y, p.x, p.y);
		}
	}
	
	private double getPlaneX(double x) {
		return x / (zoom * DEFAULT_PADDING) + viewPoint.x;
	}
	
	private double getPlaneY(double y) {
		return -y / (zoom * DEFAULT_PADDING) + viewPoint.y;
	}
	
	private double getDisplayX(double x) {
		return (int)((x - viewPoint.x) * zoom * DEFAULT_PADDING);
	}
	
	private double getDisplayY(double y) {
		return (int)((-y + viewPoint.y) * zoom * DEFAULT_PADDING);
	}
	
	private PointDouble getPlaneCoord(Point p) {
		return new PointDouble(getPlaneX(p.x), getPlaneY(p.y));
	}
	
	private PointDouble getDisplayCoord(PointDouble p) {
		return new PointDouble(getDisplayX(p.x), getDisplayY(p.y));
	}
	
	private Complex expi(Complex c) {
		return ComplexMath.exp(Complex.I.multiply(c.multiply(Math.PI / 2)));
	}
	
	public PointDouble getPointFromComplex(Complex c) {
		return new PointDouble(c.real, c.imaginary);
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
