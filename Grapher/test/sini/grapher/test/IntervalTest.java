package sini.grapher.test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import sini.grapher.Interval;

public class IntervalTest {
	
	Interval i1;
	Interval i2;
	Interval prod;
	
	@Before
	public void setup() {
		i1 = new Interval(1, 3);
		i2 = new Interval(2, 5);
		prod = Interval.product(i1, i2);
	}
	
	@Test
	public void testContains() {
		System.out.println("\n--- TEST CONTAINS ---\n");
		System.out.println(i1);
		System.out.println(i2);
		System.out.println(prod);
		
		assertTrue(i1.contains(1));
		assertTrue(i1.contains(2));
		assertTrue(i1.contains(1.9));
		assertTrue(!i1.contains(4));
		assertTrue(prod.contains(new double[] {1.5, 2.5}));
		assertTrue(prod.contains(new double[] {1.5, 3}));
		assertTrue(prod.contains(new double[] {1, 2.5}));
		assertTrue(prod.contains(new double[] {1, 3}));
		assertTrue(!prod.contains(new double[] {3, 1}));
		assertTrue(!prod.contains(1));
		assertTrue(!prod.contains(new double[] {1.5, 2.5, 2}));
	}
	
	@Test
	public void testPow() {
		System.out.println("\n--- TEST POW ---\n");
		System.out.println(Interval.pow(i1, 5));
		assertTrue(Interval.pow(i1, 5).contains(new double[] {1, 1.1, 1.5, 2, 1}));
	}
	
	@Test
	public void testMeasure() {
		assertEquals(2, i1.measure());
		assertEquals(3, i2.measure());
		assertEquals(6, prod.measure());
	}
	
	@Test
	public void testEquals() {
		assertTrue(
				prod.equals(
						Interval.product(
								new Interval(i1.lower(), i1.upper()), 
								new Interval(i2.lower(), i2.upper()))));
	}
	
	@Test
	public void testGrow() {
		prod.grow(1.5);
		assertEquals(Interval.product(new Interval(1, 4.5), new Interval(2, 6.5)), prod);
	}
	
	@Test
	public void testFactorIterator() {
		System.out.println("\n--- FACTOR ITERATOR ---\n");
		
		Iterator<Interval> iter = Interval.product(prod, new Interval(9, 11)).getFactorIterator(); 
		while(iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
	@Test
	public void testPointIterator() {
		System.out.println("\n--- POINT ITERATOR ---\n");
		
		Iterator<double[]> iter = Interval.product(prod, new Interval(9, 11)).getPointIterator(1); 
		int index = 0;
		
		while(iter.hasNext()) {
			double[] point = iter.next();
			System.out.println(index + ": [" + String.format("%.1f", point[0]) + ", " + String.format("%.1f", point[1]) + ", " + String.format("%.1f", point[2]) + "]");
			
			index++;
		}
	}
	
	@Test
	public void testPolytopeIterator() {
		System.out.println("\n--- POLYTOPE ITERATOR ---\n");
		
		Iterator<double[][]> iter = Interval.product(prod, new Interval(9, 11)).getPointCubeIterator(1); 
		int index = 0;
		
		while(iter.hasNext()) {
			double[][] polytope = iter.next();
			
			System.out.print(index + ": { ");
			for(double[] point: polytope)
				System.out.print("[" + String.format("%.1f", point[0]) + ", " + String.format("%.1f", point[1]) + ", " + String.format("%.1f", point[2]) + "], ");
			System.out.println("}");
			
			index++;
		}
	}
}
