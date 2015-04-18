
public class Vector {

	private double[] elems;
	
	public Vector(double... elems) {
		this.elems = elems.clone();
	}
	
	public Vector add(Vector v) {
		double[] result = new double[elems.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = elems[i] + v.elems[i];
		}
		return new Vector(result);
	}
	
	public Vector subtract(Vector v) {
		double[] result = new double[elems.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = elems[i] - v.elems[i];
		}
		return new Vector(result);
	}
	
	public Vector scale(double s) {
		double[] result = new double[elems.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = elems[i] * s;
		}
		return new Vector(result);
	}
	
	public double mag2() {
		return dot(this);
	}
	
	public double mag() {
		return Math.sqrt(mag2());
	}
	
	public double dot(Vector v) {
		double product = 0;
		for (int i = 0; i < elems.length; i++) {
			product += elems[i] * v.elems[i];
		}
		return product;
	}
	
	public Vector normalize() {
		return scale(1 / mag());
	}
	
	public Vector projectOnto(Vector axis) {
		Vector normalAxis = axis.normalize();
		return normalAxis.scale(dot(normalAxis));
	}
	
	public Vector rejectFrom(Vector axis) {
		return subtract(projectOnto(axis));
	}
	
}
