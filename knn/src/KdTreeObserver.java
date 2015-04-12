
public interface KdTreeObserver {

	public void activeDataChanged(Comparable[] data);
	public void currentMaximumDistanceChanged(double newDistance);
	public void queryChanged(Comparable[] query);
	
}
