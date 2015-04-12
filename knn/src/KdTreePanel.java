import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class KdTreePanel extends JPanel implements MouseListener, KdTreeObserver {

	private VisualKdTree tree;
	
	private float alpha = 1;
	private List<Double[]> closest = new ArrayList<Double[]>();
	
	private Double[] current;
	private double max = 1000000;
	private Double[] query;
	
	public KdTreePanel(VisualKdTree tree) {
		this.tree = tree;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		tree.draw(g);
		
		if (alpha >= 0) {
			g.setColor(new Color(0, 0, 1f, alpha));
			for (Double[] point : closest) {
				g.fillOval((int)(point[0] - 3), (int)(point[1] - 3), 6, 6);
			}
			
			alpha -= 0.01;
		}
		
		g.setColor(Color.RED);
		
		if (current != null) {
			g.fillOval((int)(current[0] - 3), (int)(current[1] - 3), 6, 6);
		}
		
		if (query != null) {
			double radius = max;
			g.drawOval((int)(query[0] - radius), (int)(query[1] - radius), (int)(2 * radius), (int)(2 * radius));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		new Thread() {
			public void run() {
			List<Double[]> l = null;
			l = tree.kNN(new Double[] {(double) e.getX(), (double) e.getY()}, 1);
			closest.clear();
			closest.addAll(l);
			alpha = 1;
			}
		}.start();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void activeDataChanged(Comparable[] data) {
		current = (Double[]) data;
	}

	@Override
	public void currentMaximumDistanceChanged(double newDistance) {
		max = newDistance;
	}
	
	@Override
	public void queryChanged(Comparable[] query) {
		this.query = (Double[]) query;
	}
	
}
