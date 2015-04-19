
public class EuclideanMetric extends Metric<Double[]> {

	@Override
	public double distanceSquared(Double[] point1, Double[] point2) {
		double distSq = 0;
		for (int i = 0; i < point1.length; i++) {
			distSq += (point1[i] - point2[i]) * (point1[i] - point2[i]);
		}
		return distSq;
	}

}
