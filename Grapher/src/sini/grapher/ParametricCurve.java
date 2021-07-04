package sini.grapher;

import java.util.ArrayList;
import java.util.Iterator;

public class ParametricCurve {
	
	private Function f;
	
	public ParametricCurve(Function f) {
		this.f = f;
	}
	
	public ArrayList<double[][]> generateMesh(Interval interval, double step) {
		ArrayList<double[][]> mesh = new ArrayList<double[][]>();
		Iterator<double[][]> iter = interval.getPointCubeIterator(step);
		
		while(iter.hasNext()) {
			double[][] currentMeshSeg = iter.next();
			for(double[] point: currentMeshSeg) point = f.image(point);
			mesh.add(currentMeshSeg);
		}
		
		return mesh;
	}
}
