import java.awt.Color;
import java.awt.Graphics;
import java.util.List;


public class VisualKdTree extends KdTree<Double> {

	private List<Double[]> preOrder;
	
	private final int scale = 1;
	private final int NODE_RADIUS = 1;
	
	public VisualKdTree(int recordSize) {
		super(recordSize);
	}
	
	public void update() {
		preOrder = preOrder();
	}

	public void draw(Graphics g) {
		g.setColor(new Color(0, 0, 1, 0.3f));
		draw(g, root, -10000000, 10000000, -10000000, 10000000);
		g.setColor(new Color(0, 0, 1f, 1f));
		drawPoints(g);
	}
	
	public void drawPoints(Graphics g) {
		for (Double[] data : preOrder) {
			g.fillOval((int)(scale * (data[0]) - NODE_RADIUS), (int)(scale * (data[1]) - NODE_RADIUS), 2 * NODE_RADIUS, 2 * NODE_RADIUS);
		}
	}
	
	private void draw(Graphics g, Node<Double> root, double loX, double hiX, double loY, double hiY) {
		if (size() > 0) {
			if (root.getDisc() == 0) {
				g.drawLine((int)(scale * root.getKey(0)), (int)(scale * loY), (int)(scale * root.getKey(0)), (int)(scale * hiY));
				if (root.hasLoson()) {
					draw(g, root.getLoson(), loX, root.getKey(0), loY, hiY);
				}
				if (root.hasHison()) {
					draw(g, root.getHison(), root.getKey(0), hiX, loY, hiY);
				}
			} else {
				g.drawLine((int)(scale * loX), (int)(scale * root.getKey(1)), (int)(scale * hiX), (int)(scale * root.getKey(1)));
				if (root.hasLoson()) {
					draw(g, root.getLoson(), loX, hiX, loY, root.getKey(1));
				}
				if (root.hasHison()) {
					draw(g, root.getHison(), loX, hiX, root.getKey(1), hiY);
				}
			}
		}

	}
	
}
