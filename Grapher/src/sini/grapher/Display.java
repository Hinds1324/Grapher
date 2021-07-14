package sini.grapher;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.JPanel;

public class Display extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {
	private static final long serialVersionUID = -6696561578530033238L;
	
	private static final int GRID_CELL_DISPLAY_SIZE = 50;
	public static final double GRID_SCALE_FACTOR = 10;
	
	private PointDouble viewPoint = new PointDouble(); // Represents the top left coordinate of the viewport
	private double zoom;
	
	ArrayList<Curve> curves;
	
	Theme theme;

	public Display() {
		zoom = 2;
		
		setBackground(Color.WHITE);
		
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		
		curves = CurveList.getCurves();
		
		defineThemes();
	}
	
	private void defineThemes() {
		Theme themeLight = new Theme();
		themeLight.setColor(Theme.BACKGROUND, Color.WHITE);
		themeLight.setColor(Theme.AXES, Color.BLACK);
		themeLight.setColor(Theme.MAJOR_TICKS, Color.LIGHT_GRAY);
		themeLight.setColor(Theme.MINOR_TICKS, new Color(230, 230, 230));
		themeLight.setColor(Theme.CURVE_DEFAULT, new Color(60, 100, 255));
		
		Theme themeDark = new Theme();
		themeDark.setColor(Theme.BACKGROUND, new Color(20, 20, 20));
		themeDark.setColor(Theme.AXES, new Color(200, 200, 200));
		themeDark.setColor(Theme.MAJOR_TICKS, new Color(80, 80, 80));
		themeDark.setColor(Theme.MINOR_TICKS, new Color(40, 40, 40));
		themeDark.setColor(Theme.CURVE_DEFAULT, new Color(60, 100, 255));
		
		Theme themeBlueprint = new Theme();
		themeBlueprint.setColor(Theme.BACKGROUND, new Color(110, 140, 255));
		themeBlueprint.setColor(Theme.AXES, new Color(200, 230, 255));
		themeBlueprint.setColor(Theme.MAJOR_TICKS, new Color(170, 200, 255));
		themeBlueprint.setColor(Theme.MINOR_TICKS, new Color(130, 160, 255));
		themeBlueprint.setColor(Theme.CURVE_DEFAULT, Color.WHITE);
		
		theme = themeBlueprint;
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
	public void centerViewport(PointDouble p) {
		viewPoint = new PointDouble(
				p.x - (double)getWidth() / (2 * getDisplayUnit()),
				p.y + (double)getHeight() / (2 * getDisplayUnit())
				);
		
		updateCurves();
		repaint();
	}
	
	/**
	 * Moves the viewport such that the point on the plane pointPlane sits at the point pointDisplay relative to the display.
	 */
	public void movePlanePointToDisplayPoint(PointDouble pointPlane, Point pointDisplay) {
		PointDouble pointDisplayPlaneCoord = getPlanePoint(pointDisplay);
		
		viewPoint = new PointDouble(
				viewPoint.x + pointPlane.x - pointDisplayPlaneCoord.x,
				viewPoint.y + pointPlane.y - pointDisplayPlaneCoord.y
				);
		
		updateCurves();
		repaint();
	}
	
	private void doGrid(Consumer<Double> consumerV, Consumer<Double> consumerH, double subDivision) {
		double gridScale = getGridScale();
		double lineSpacing = getDisplayUnit() * gridScale / subDivision;
		
		double lineX = getDisplayUnit() * (-viewPoint.x % gridScale - gridScale); 
		double lineY = getDisplayUnit() * (viewPoint.y % gridScale - gridScale);
		
		while(lineX < getWidth()) {
			consumerV.accept(lineX);
			lineX += lineSpacing;
		}
		
		while(lineY < getHeight()) {
			consumerH.accept(lineY);
			lineY += lineSpacing;
		}
	}
	
	private void doGrid(Consumer<Double> consumerH, Consumer<Double> consumerV) {
		doGrid(consumerH, consumerV, 1);
	}
	
	public void paintComponent(Graphics g) {
		setBackground(theme.getColor(Theme.BACKGROUND));
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		int yAxisDisplayPos = (int)getDisplayX(0);
		int xAxisDisplayPos = (int)getDisplayY(0);
		
		
		// Draw minor grid lines
		
		g2.setColor(theme.getColor(Theme.MINOR_TICKS));
		doGrid(
				x -> g2.drawLine(x.intValue(), 0, x.intValue(), getHeight()),
				y -> g2.drawLine(0, y.intValue(), getWidth(), y.intValue()),
				GRID_SCALE_FACTOR
				);
		
		
		// Draw major grid lines
		
		g2.setColor(theme.getColor(Theme.MAJOR_TICKS));
		doGrid(
				x -> g2.drawLine(x.intValue(), 0, x.intValue(), getHeight()),
				y -> g2.drawLine(0, y.intValue(), getWidth(), y.intValue())
				);

		
		// Draw graph
		
		for(Curve curve: curves) {
			curve.draw(this, g2);
		}
		
		
		// Draw axis lines
		
		g2.setStroke(new BasicStroke(1));
		g2.setColor(theme.getColor(Theme.AXES));
		g2.drawLine(yAxisDisplayPos, 0, yAxisDisplayPos, getHeight());
		g2.drawLine(0, xAxisDisplayPos, getWidth(), xAxisDisplayPos);
		
		
		// Number the axes
		g2.setColor(theme.getColor(Theme.AXES));
		doGrid(x -> drawXAxisNumber(g2, x.intValue()), y -> drawYAxisNumber(g2, y.intValue()));
	}
	
	private void drawYAxisNumber(Graphics2D g, int y) {
		int yAxisDisplayPos = (int)getDisplayX(0);
		int numberLength = (int)Math.max(0, Math.floor(Math.log(zoom) / Math.log(GRID_SCALE_FACTOR)));
		int stringPadding = 5;
		
		String numberString = String.format("%." + numberLength + "f", getPlaneY(y));
		int stringWidth = g.getFontMetrics().stringWidth(numberString);
		int numberXPos;
		
		if(yAxisDisplayPos > getWidth() - stringWidth - stringPadding) {
			numberXPos = getWidth() - stringWidth - stringPadding;
		} else if (yAxisDisplayPos < 0) {
			numberXPos = stringPadding;
		} else {
			numberXPos = yAxisDisplayPos + stringPadding;
		}
		
		if(y != (int)getDisplayY(0)) { 
			g.drawString(
				numberString, 
				numberXPos, 
				y);
		}
	}
	
	private void drawXAxisNumber(Graphics2D g, int x) {
		int xAxisDisplayPos = (int)getDisplayY(0);
		int numberLength = (int)Math.max(0, Math.floor(Math.log(zoom) / Math.log(GRID_SCALE_FACTOR)));
		int stringPadding = 5;
		
		String numberString = String.format("%." + numberLength + "f", getPlaneX(x));
		int stringHeight = g.getFontMetrics().getHeight();
		int numberYPos;
		
		if(xAxisDisplayPos > getHeight() - stringPadding) {
			numberYPos = getHeight() - stringPadding;
		} else if (xAxisDisplayPos < stringHeight + 2*stringPadding) {
			numberYPos = stringHeight + stringPadding;
		} else {
			numberYPos = xAxisDisplayPos - stringPadding;
		}
		
		if(x != (int)getDisplayX(0)) { 
			g.drawString(
				numberString, 
				x,
				numberYPos);
		}
	}
	
	public double getGridScale() {
		return Math.pow(GRID_SCALE_FACTOR, -Math.floor(Math.log(zoom) / Math.log(GRID_SCALE_FACTOR)));
	}
	
	
	/**
	 * Returns the length on the display (in pixels) that corresponds to a length of one unit on the plane.
	 */
	public double getDisplayUnit() {
		return zoom * GRID_CELL_DISPLAY_SIZE;
	}
	
	
	/**
	 * Returns the x-coordinate on the plane that corresponds to the x-coordinate on the display given by {@code x}.
	 * @param x the x-coordinate of a point on the display.
	 * @return the x-coordinate on the plane that corresponds to the x-coordinate on the display given by {@code x}.
	 */
	public double getPlaneX(double x) {
		return x / getDisplayUnit() + viewPoint.x;
	}
	
	
	/**
	 * Returns the y-coordinate on the plane that corresponds to the y-coordinate on the display given by {@code y}.
	 * @param y the y-coordinate of a point on the display.
	 * @return the y-coordinate on the plane that corresponds to the y-coordinate on the display given by {@code y}.
	 */
	public double getPlaneY(double y) {
		return -y / getDisplayUnit() + viewPoint.y;
	}
	
	
	/**
	 * Returns the x-coordinate on the display that corresponds to the x-coordinate on the plane given by {@code x}.
	 * @param x the x-coordinate of a point on the plane.
	 * @return the x-coordinate on the display that corresponds to the x-coordinate on the plane given by {@code x}.
	 */
	public double getDisplayX(double x) {
		return (x - viewPoint.x) * getDisplayUnit();
	}
	
	
	/**
	 * Returns the y-coordinate on the display that corresponds to the y-coordinate on the plane given by {@code y}.
	 * @param y the y-coordinate of a point on the plane.
	 * @return the y-coordinate on the display that corresponds to the y-coordinate on the plane given by {@code y}.
	 */
	public double getDisplayY(double y) {
		return (-y + viewPoint.y) * getDisplayUnit();
	}
	
	
	/**
	 * Returns the point on the plane that corresponds to the point on the display given by {@code p}.
	 * @param p a point on the display.
	 * @return the point on the plane that corresponds to the point on the display given by {@code p}.
	 */
	public PointDouble getPlanePoint(Point p) {
		return new PointDouble(getPlaneX(p.x), getPlaneY(p.y));
	}
	
	
	/**
	 * Returns the point on the display that corresponds to the point on the plane given by {@code p}.
	 * @param p a point on the plane.
	 * @return the point on the display that corresponds to the point on the plane given by {@code p}.
	 */
	public PointDouble getDisplayPoint(PointDouble p) {
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
		PointDouble planePos = getPlanePoint(anchorPos);
		movePlanePointToDisplayPoint(planePos, mousePos);
		anchorPos = mousePos;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point mousePos = e.getPoint();
		PointDouble planePos = getPlanePoint(mousePos);
		
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
