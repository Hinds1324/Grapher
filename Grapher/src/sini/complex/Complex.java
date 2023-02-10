package sini.complex;

public class Complex {
	
	public static final Complex I = new Complex(0, 1);
	public static final Complex ZERO = new Complex(0, 0);
	public static final Complex ONE = new Complex(1, 0);
	
	public double real;
	public double imaginary;
	
	public Complex(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}
	
	public double arg() {
		return Math.atan2(imaginary, real);
	}
	
	public double magnitude() {
		return Math.sqrt(real*real + imaginary*imaginary);
	}
	
	public double squaredMagnitude() {
		return real*real + imaginary*imaginary;
	}
	
	public Complex add(Complex c) {
		return new Complex(this.real + c.real, this.imaginary + c.imaginary);
	}
	
	public Complex multiply(Complex c) {
		return new Complex(c.real*this.real - c.imaginary*this.imaginary, c.real*this.imaginary + c.imaginary*this.real);
	}
	
	public Complex multiply(double r) {
		return new Complex(r*real, r*imaginary);
	}
	
	public Complex invert() {
		return new Complex(real / (real*real - imaginary*imaginary), -imaginary / (real*real - imaginary*imaginary));
	}
	
	public Complex negate() {
		return new Complex(-real, -imaginary);
	}
	
	public Complex conjugate() {
		return new Complex(real, -imaginary);
	}
	
	public static Complex real(double real) {
		return new Complex(real, 0);
	}
	
	public static Complex imaginary(double imaginary) {
		return new Complex(0, imaginary);
	}
	
	public String toString() {
		return "(" + real + ", " + imaginary + "i)";
	}
	
	public boolean equals(Object o) {
		if(o instanceof Complex) {
			Complex c = (Complex) o;
			return (real == c.real) && (imaginary == c.imaginary);
		}
		return false;
	}
}
