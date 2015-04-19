
public abstract class Metric<T> {

	public double distance(T point1, T point2) {
		return Math.sqrt(distanceSquared(point1, point2));
	};
	
	public abstract double distanceSquared(T point1, T point2);
	
}
