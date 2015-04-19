import java.util.Queue;

public interface KdTreeObserver {

	public void activeDataChanged(Comparable[] data);
	public void currentMaximumDistanceChanged(double newDistance);
	public void queueChanged(Queue<KdTree.Node> que);
	public void queryChanged(Comparable[] query);
	
}
