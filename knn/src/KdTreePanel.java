import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class KdTreePanel extends JPanel implements MouseListener, KdTreeObserver {

	private VisualKdTree tree;
	
	private float alpha = 1;
	private List<Double[]> closest = new ArrayList<Double[]>();
	
	private Double[] current;
	private double max = 1000000;
	private Double[] query;
	private Queue<KdTree.Node> que;
	
	private BufferedImage img;
	
	private Double[] goal;
	
	int frame = 0;
	
	public KdTreePanel(VisualKdTree tree) {
		this.tree = tree;
		img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
	}
	
	public void actualDraw(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Composite comp = g.getComposite();
		g.setComposite(
				AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, img.getWidth(), img.getHeight()); 
				g.fill(rect);
		g.setComposite(comp);
				
		tree.draw(g);
		
		int rad = 5;
		
		if (alpha >= 0) {
			g.setColor(new Color(0, 0, 1f, alpha));
			for (Double[] point : closest) {
				g.fillOval((int)(point[0] - rad), (int)(point[1] - rad), 2 * rad, 2 * rad);
			}
			
			alpha -= 0.0001;
		}
		
		g.setColor(Color.RED);
		
		if (current != null) {
			g.fillOval((int)(current[0] - rad - 3), (int)(current[1] - rad - 3), 2 * rad + 6, 2 * rad + 6);
		}
		
		if (query != null) {
			double radius = max;
			g.drawOval((int)(query[0] - radius), (int)(query[1] - radius), (int)(2 * radius), (int)(2 * radius));
		}
				
		g.setColor(new Color(255, 255, 0));
		if (que != null) {
			for (KdTree.Node n : que) {
				Double[] query = new Double[] {(Double)(n.getKey(0)), (Double)(n.getKey(1))};
				g.fillOval((int)(query[0] - rad), (int)(query[1] - rad), (int)(2 * rad), (int)(2 * rad));
			}
		}
		if (goal != null) {
			g.setColor(Color.BLACK);
			g.fillRect((int)(goal[0] - rad), (int)(goal[1] - rad), (int)(2 * rad), (int)(2 * rad));
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		actualDraw(img.getGraphics());
		g.drawImage(img, 0, 0, this);
		
		File file = new File("C:\\Users\\Colby_000\\Desktop\\kdTreeExample\\" + frame + ".png");
		try {
			file.createNewFile();
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frame++;
	}
	
	public void setGoal(Double[] goal) {
		this.goal = goal;
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
			setGoal(new Double[] {(double) e.getX(), (double) e.getY()});
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
		repaint();
	}

	@Override
	public void currentMaximumDistanceChanged(double newDistance) {
		max = newDistance;
		repaint();
	}
	
	@Override
	public void queryChanged(Comparable[] query) {
		this.query = (Double[]) query;
		repaint();
	}

	@Override
	public void queueChanged(Queue<KdTree.Node> que) {
		this.que = que;
		repaint();
	}
	
}
