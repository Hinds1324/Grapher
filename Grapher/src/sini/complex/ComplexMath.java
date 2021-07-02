package sini.complex;

public class ComplexMath {
	
	public static Complex add(Complex c1, Complex c2) {
		return c1.add(c2);
	}
	
	public static Complex multiply(Complex c1, Complex c2) {
		return c1.multiply(c2);
	}
	
	public static Complex pow(Complex c, int n) {
		if(n < 0) {
			return pow(c, -n).invert();
		}
		
		if(n == 0) {
			return Complex.ONE;
		}
		
		if(n == 1) {
			return c;
		}
		
		Complex result = c;
		int k = Integer.highestOneBit(n);
		int r = n - k;
		
		while(k > 1) {
			result = multiply(result, result);
			k /= 2;
		}
		
		return multiply(result, pow(c, r));
	}
	
	public static Complex exp(Complex c) {
		Complex result = Complex.ONE;
		Complex currentTerm = Complex.ONE;
		
		for(int i = 1; i < 32; i++) {
			currentTerm = currentTerm.multiply(c).multiply((double)1 / (double)i);
			result = result.add(currentTerm);
		}
		
		return result;
	}
}
