package sini.grapher;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@code Interval} class provides an implementation of multi-dimensional mathematical intervals. The most basic type of interval
 * is a 1-dimensional interval, which is thought of as a set of all the real numbers that lie between two given real numbers.
 * We can then construct n-dimensional intervals by taking the Cartesian product of an ordered list of n 1-dimensional intervals.
 * 
 * <p>Internally, all of the information about an interval is stored in three fields:
 * <ul>
 * 	<li>{@code double upper} and {@code double lower}, which store the upper and lower bounds of a 1-dimensional interval respectively.
 * 	<li> {@code Interval factor}, an additional {@code Interval} object that, when it is multiplied with the 1-dimensional interval whose
 * bounds are given by {@code upper} and {@code lower}, gives the interval that is represented by this {@code Interval} object. Note that
 * {@code factor} may also store an {@code Interval} object of its own, which in turn can also store an {@code Interval} object, and so
 * on. Hence, the 1-dimensional intervals whose Cartesian product make up a multi-dimensional interval are chained recursively through
 * the intervals referenced by this field. If this field is instead assigned {@code null}, then we are not taking a Cartesian product
 * of any additional intervals and we just have a simple 1-dimensional interval.
 * </ul>
 * 
 * @author Sini
 */
public class Interval implements Cloneable {
	
	/**
	 * An exception that is thrown when attempting to raise an interval to the power of a non-positive integer, as this is not a well-defined
	 * operation.
	 * @author Sini
	 */
	private static class InvalidPowerException extends RuntimeException {
		private static final long serialVersionUID = -7019353633442043060L;

		public InvalidPowerException(String message) {
			super(message);
		}
	}
	
	
	/**
	 * An exception that is thrown when attempting to create an instance of {@code IntervalIterator} with a non-positive step size.
	 * @author Sini
	 */
	private static class InvalidStepSizeException extends RuntimeException {
		private static final long serialVersionUID = 2368389791932656103L;

		public InvalidStepSizeException(String message) {
			super(message);
		}
	}
	
	
	private double lower;
	private double upper;
	
	private Interval factor;
	
	
	/**
	 * Creates a new 1-dimensional <code>Interval</code> whose bounds are the values of <code>a</code> and <code>b</code>. 
	 * These arguments do not necessarily need to be passed in the correct order, as this will be done internally.
	 */
	public Interval(double a, double b) {
		this(a, b, null);
	}
	
	
	/**
	 * Creates a new <code>Interval</code> that is the Cartesian product of the following intervals:
	 * 
	 * <ul>
	 * 	<li> The 1-dimensional interval whose bounds are the values of <code>a</code> and <code>b</code>. 
	 * These arguments do not necessarily need to be passed in the correct order, as this will be done internally. </li>
	 * 
	 * <li> The interval given by <code>factor</code>.
	 * </ul>
	 */
	public Interval(double a, double b, Interval factor) {
		lower = Math.min(a, b);
		upper = Math.max(a, b);
		this.factor = factor;
	}
	
	
	@Override
	public Interval clone() {
		if(factor == null) return new Interval(lower, upper);
		return new Interval(lower, upper, factor.clone());
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Interval) {
			Interval interval = (Interval) obj;
			boolean isEqualFactors = (factor == null) ? interval.getFactor() == null : factor.equals(interval.getFactor());
			return isEqualFactors && (upper == interval.upper()) && (lower == interval.lower());
		}
		return super.equals(obj);
	}
	
	
	@Override
	public String toString() {
		if(factor == null) return "[" + lower + ", " + upper + "]";
		return "[" + lower + ", " + upper + "] x " + factor.toString();
	}
	
	
	/**
	 * Returns the lower bound of this interval, given that this interval has a dimension of 1. If the interval has dimension greater than 1, then the lower
	 * bound of this interval's leading factor will be returned instead.
	 * @return the lower bound of this interval.
	 */
	public double lower() {
		return lower;
	}
	
	
	/**
	 * Returns the upper bound of this interval, given that this interval has a dimension of 1. If the interval has dimension greater than 1, then the upper
	 * bound of this interval's leading factor will be returned instead.
	 * @return the upper bound of this interval.
	 */
	public double upper() {
		return upper;
	}
	
	
	/**
	 * Returns the length of this interval, given that this interval has a dimension of 1. If the interval has dimension greater than 1, then the length
	 * of this interval's leading factor will be returned instead.
	 * @return the length of this interval, given by {@code upper - lower}.
	 */
	public double length() {
		return upper - lower;
	}
	
	
	/**
	 * Returns the value of the measure of this interval, with respect to the Lebesgue measure on <b>R</b><sup>n</sup>, where n is the dimension
	 * of this interval. If n is 1, this corresponds to the length of the line represented by this interval. If n is 2, this corresponds to
	 * the area of the interval in the plane. If n is 3, this corresponds to the volume of the interval in 3D space, and so on.
	 * @return the value of the measure of this interval, with respect to the Lebesgue measure on <b>R</b><sup>n</sup>, where n is the dimension
	 * of this interval.
	 */
	public double measure() {
		if(factor == null) return length();
		return length() * factor.measure();
	}
	
	
	/**
	 * Returns the {@code Interval} object that, when it is multiplied with the 1-dimensional interval whose
	 * bounds are given by {@code upper} and {@code lower}, gives the interval that is represented by this {@code Interval} object.
	 * @return
	 */
	public Interval getFactor() {
		return factor;
	}
	
	
	/**
	 * Returns the dimension of this interval. This number is defined to be the size of the
	 * ordered list of all the 1-dimensional intervals whose Cartesian product equals this interval.
	 * @return The dimension of this interval.
	 */
	public int getDimension() {
		if (factor == null) return 1;
		return factor.getDimension() + 1;
	}
	
	
	/**
	 * Takes a parameter <code>point</code> representing an n-dimensional point, and returns <code>true</code> if it
	 * is contained within this interval. Note that the dimension of the point must be equal to the dimension of the interval
	 * if the point is to possibly be contained within the interval.
	 * @param point an n-dimensional point.
	 * @return <code>true</code> if <code>v</code> is contained in this interval, <code>false</code> otherwise.
	 */
	public boolean contains(double[] point) {
		if (point.length != getDimension()) return false;
		if (point.length == 1) return (lower <= point[0]) && (point[0] <= upper);
		
		double[] pointTruncated = new double[point.length - 1];
		for(int i=0; i < pointTruncated.length; i++) {
			pointTruncated[i] = point[i+1];
		}
		
		return (lower <= point[0]) && (point[0] <= upper) && factor.contains(pointTruncated);
	}
	
	
	/**
	 * Takes a real-valued parameter <code>value</code>, and returns <code>true</code> if it
	 * is contained within this interval. Note that this interval must be of dimension 1
	 * if this number is to possibly be contained within it.
	 * @param value a real number.
	 * @return <code>true</code> if <code>v</code> is contained in this interval, <code>false</code> otherwise.
	 */
	public boolean contains(double value) {
		return contains(new double[] {value});
	}
	
	
	/**
	 * Post-multiplies the interval given by the parameter <code>newFactor</code> into this interval via the Cartesian product.
	 * @param newFactor the interval to be post-multiplied into this interval.
	 */
	public void multiply(Interval newFactor) {
		newFactor = newFactor.clone();
		
		if (factor == null) factor = newFactor;
		else factor.multiply(newFactor);
	}
	
	
	/**
	 * Post-multiplies the intervals given by the array <code>newFactors</code>, in order, into this interval via the Cartesian product.
	 * @param newFactors the array of intervals to be post-multiplied into this interval.
	 */
	public void multiply(Interval[] newFactors) {
		for(int i = 0; i < newFactors.length; i++) {
			multiply(newFactors[i]);
		}
	}
	
	
	/**
	 * Raises this interval to the power of <code>n</code> via the Cartesian product.
	 * @throws InvalidPowerException if <code>n</code> is not a positive integer.
	 * @param n a positive integer.
	 */
	public void pow(int n) {
		if(n <= 0) throw new InvalidPowerException("Cannot raise an interval to power " + n + ". Must be a positive integer");
		
		Interval thisClone = this.clone();
		
		for(int i = 1; i < n; i++) {
			multiply(thisClone);
		}
	}
	
	
	/**
	 * Grows the upper boundary of this interval by {@code amount}, keeping the lower corner fixed in place. (The lower corner being
	 * the point on the interval with the smallest possible coordinates).
	 * @param amount the amount by which to grow the interval.
	 */
	public void grow(double amount) {
		upper += amount;
		if(factor != null) factor.grow(amount);
	}
	
	
	/**
	 * Shrinks the upper boundary of this interval by {@code amount}, keeping the lower corner fixed in place. (The lower corner being
	 * the point on the interval with the smallest possible coordinates).
	 * @param amount the amount by which to shrink the interval.
	 */
	public void shrink(double amount) {
		grow(-amount);
	}
	
	
	/**
	 * Expands the size of the interval outward in all directions by {@code amount}
	 * @param amount the amount by which to expand the interval.
	 */
	public void expand(double amount) {
		upper += amount;
		lower -= amount;
		if(factor != null) factor.expand(amount);
	}
	
	
	/**
	 * Contracts the size of the interval inward in all directions by {@code amount}
	 * @param amount the amount by which to contract the interval.
	 */
	public void contract(double amount) {
		expand(-amount);
	}
	
	
	/**
	 * Returns the Cartesian product of the intervals <code>i1</code> and <code>i2</code>, in that order.
	 * @param i1 the first interval to be multiplied via the Cartesian product.
	 * @param i2 the second interval to be multiplied via the Cartesian product.
	 * @return the Cartesian product of the intervals <code>i1</code> and <code>i2</code>
	 */
	public static Interval product(Interval i1, Interval i2) {
		Interval result = i1.clone();
		result.multiply(i2);
		return result;
	}
	
	
	/**
	 * Returns the Cartesian product of the intervals given by the array <code>intervals</code>, in order.
	 * @param intervals the array of intervals to be multiplied via the Cartesian product.
	 * @return the Cartesian product of the intervals given by the array <code>intervals</code>.
	 */
	public static Interval product(Interval[] intervals) {
		Interval result = intervals[0].clone();
		
		for(int i = 1; i < intervals.length; i++) {
			result.multiply(intervals[i]);
		}
		
		return result;
	}
	
	
	/**
	 * Returns the interval given by raising the parameter <code>interval</code> to the power of <code>n</code>.
	 * @param interval the interval to raise to the power of <code>n</code>.
	 * @param n a positive integer
	 * @throws InvalidPowerException if <code>n</code> is not a positive integer.
	 * @return the interval given by raising the parameter <code>interval</code> to the power of <code>n</code>.
	 */
	public static Interval pow(Interval interval, int n) {
		if(n <= 0) throw new InvalidPowerException("Cannot raise an interval to power " + n + ". Must be a positive integer");
		
		Interval result = interval.clone();
		result.pow(n);
		return result;
	}
	
	
	/**
	 * Returns an iterator that iterates over all the 1-dimensional factors of this interval.
	 * @return an iterator that iterates over all the 1-dimensional factors of this interval.
	 */
	public Iterator<Interval> getFactorIterator() {
		return new IntervalFactorIterator();
	}
	
	
	/**
	 * Returns an iterator that iterates over all the points that are part of the lattice
	 * contained within this interval where:
	 * 
	 * <ul>
	 * 	<li> The points on the lattice are equally spaced and separated by the step size given by {@code step}.
	 * 	<li> The point at the lower corner of the interval is included in the lattice.
	 * (The lower corner being the point on the interval with the smallest possible coordinates)
	 * </ul>
	 * 
	 * The step size can take any positive value. A smaller step size will result in an iteration
	 * over a denser set of points contained within the interval.
	 * 
	 * @param step the step size by which every point in the iteration will be separated.
	 * @return an iterator that iterates over all the points contained within this interval.
	 */
	public Iterator<double[]> getPointIterator(double step) {
		return new IntervalPointIterator(step);
	}
	
	
	/**
	 * Returns an iterator that iterates over all the possible n-cubes with a side length of {@code step}
	 * whose vertices are part of the lattice contained within this interval where:
	 * 
	 * <ul>
	 * 	<li> The points on the lattice are equally spaced and separated by the step size given by {@code step}.
	 * 	<li> The point at the lower corner of the interval is included in the lattice.
	 * 	(The lower corner being the point on the interval with the smallest possible coordinates)
	 * </ul>
	 * 
	 * The step size can take any positive value. A smaller step size will result in an iteration
	 * over a denser set of n-cubes contained within the interval.
	 * 
	 * @param step the step size by which every point in the iteration will be separated.
	 * @return an iterator that iterates over a set of n-cubes contained within this interval.
	 */
	public Iterator<double[][]> getPointCubeIterator(double step) {
		return new IntervalPointCubeIterator(step);
	}
	
	
	private class IntervalFactorIterator implements Iterator<Interval> {
		Interval currentInterval;
		
		public IntervalFactorIterator() {
			currentInterval = Interval.this;
		}

		@Override
		public boolean hasNext() {
			return currentInterval != null;
		}

		@Override
		public Interval next() {
			if(!hasNext()) throw new NoSuchElementException();
			
			Interval returnInterval = currentInterval;
			currentInterval = currentInterval.getFactor();
			return returnInterval;
		}
	}
	
	
	private class IntervalPointIterator implements Iterator<double[]> {
		
		private int index;
		private double step;
		
		private double[] currentPoint;
		
		public IntervalPointIterator(double step) {
			if(step <= 0) throw new InvalidStepSizeException("Cannot create an " + this.getClass().getName() + " instance with step size " + step + ". Step size must be positive");
			
			index = -1;
			this.step = step;
		}
		
		public boolean hasNext() {
			if(currentPoint == null) return true;
			
			Iterator<Interval> iter = Interval.this.getFactorIterator();
			for(int i = 0; i < currentPoint.length; i++) {
				Interval currentFactor = iter.next();
				if(currentPoint[i] + step <= currentFactor.upper()) return true;
			}
			
			return false;
		}
		
		public double[] next() {
			if(!hasNext()) throw new NoSuchElementException();
			
			index++;
			
			if(index == 0) {
				currentPoint = new double[getDimension()];

				Iterator<Interval> iter = Interval.this.getFactorIterator();
				for(int i = 0; i < currentPoint.length; i++) {
					Interval currentFactor = iter.next();
					currentPoint[i] = currentFactor.lower();
				}
				
				return currentPoint;
			}
			
			Iterator<Interval> iter = Interval.this.getFactorIterator();
			currentPoint[0] += step;
			for(int i = 1; i < currentPoint.length; i++) {
				Interval currentFactor = iter.next();
				
				if(currentPoint[i-1] > currentFactor.upper()) {
					currentPoint[i-1] = currentFactor.lower();
					currentPoint[i] += step;
				}
			}
			
			return currentPoint;
		}
	}
	
	
	private class IntervalPointCubeIterator implements Iterator<double[][]> {

		private Interval shrunkenInterval;
		private Iterator<double[]> pointIterator;
		
		private double step;
		
		public IntervalPointCubeIterator(double step) {
			this.step = step;
			
			shrunkenInterval = Interval.this.clone();
			shrunkenInterval.shrink(step);
			
			pointIterator = shrunkenInterval.getPointIterator(step);
		}

		@Override
		public boolean hasNext() {
			return pointIterator.hasNext();
		}

		@Override
		public double[][] next() {
			double[] nextPoint = pointIterator.next();
			double[][] cube = new double[1 << nextPoint.length][]; // create an array of 2^(nextPoint.length) points that are the vertices of the cube
			
			for(int i = 0; i < cube.length; i++) {
				cube[i] = new double[nextPoint.length];
				
				for(int j = 0; j < nextPoint.length; j++) {
					cube[i][j] = nextPoint[j] + ((i >> j) % 2) * step; // multiply step by the j-th binary digit of i to generate a unique point on the n-cube
				}
			}
			
			return cube;
		}	
	}
}
