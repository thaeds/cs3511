import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;


public class KdTree<T extends Comparable<T>> {
	
	private int recordSize;
	private int size;
	protected Node<T> root;
	
	private final int ROOT_DISC = 0;
	private List<KdTreeObserver> observers = new ArrayList<KdTreeObserver>();
	
	public KdTree(int recordSize) {
		this.recordSize = recordSize;
	}
	
	public T[] add(T[] record) {
		if (root == null) {
			root = new Node<T>(record);
			size++;
		} else {
			Node<T> parent = root;
			Node<T> current = new Node<T>(record);
			while (parent != null) {
				if (parent.equals(current)) {
					return (T[]) parent.record;
				} else {
					Node<T> son = successor(parent, current);
					if (son == null) {
						setSuccessor(parent, current, current);
						size++;
					}
					parent = son;
				}
			}
		}
		return null;
	}
	
	public String toString() {
		return root.toString();
	}
	
	private int nextDisc(int disc) {
		return (disc + 1) % recordSize;
	}
	
	private Node<T> successor(Node<T> current, Node<T> lookingFor) {
		int j = current.disc;
		if (lookingFor.getKey(j).compareTo(current.getKey(j)) < 0) {
			return current.loson;
		} else if (lookingFor.getKey(j).compareTo(current.getKey(j)) > 0) {
			return current.hison;
		} else {
			return current.hison; //Todo use better tie-breaker;
		}
	}
	
	public int size() {
		return size;
	}
	
	private void setSuccessor(Node<T> current, Node<T> lookingFor, Node<T> son) {
		int j = current.disc;
		if (lookingFor.getKey(j).compareTo(current.getKey(j)) < 0) {
			current.setLoson(son);
			son.setParent(current);
		} else if (lookingFor.getKey(j).compareTo(current.getKey(j)) > 0) {
			current.setHison(son);
			son.setParent(current);
		} else {
			current.setHison(son);
			son.setParent(current); //Todo use better tie-breaker;
		}
	}
	
	public void delete(T[] record) {
		throw new UnsupportedOperationException("Deletion is not yet implemented.");
	}
	
	public List<T[]> preOrder() {
		List<T[]> preOrder = new ArrayList<T[]>();
		Deque<Node<T>> stack = new ArrayDeque<Node<T>>();
		if (root != null) {
			stack.push(root);
			Node<T> current;
			while (!stack.isEmpty()) {
				current = stack.pop();
				preOrder.add(current.record);
				if (current.loson != null) {
					stack.push(current.loson);
				}
				if (current.hison != null) {
					stack.push(current.hison);
				}
			}
		}
		return preOrder;
	}
	
	public List<T[]> kNN(T[] query, int k) {
		notifyObserversQueryChange(query);
		PriorityQueue<Node<T>> que = new PriorityQueue<Node<T>>(new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				Node<T> n1 = (Node<T>) o1;
				Node<T> n2 = (Node<T>) o2;
				return (int)Math.signum(n2.temp - n1.temp);
			}
			
		});
		int count = kNNHelper(query, k, que, root);
		
		List<T[]> result = new ArrayList<T[]>();
		for (Node<T> n : que) {
			result.add(n.record);
		}
		return result;
	}
	
	private int kNNHelper(T[] query, int k, PriorityQueue<Node<T>> que, Node<T> node) {
		/*try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		notifyObserversNodeChange(node.record);
		double distSq = 0;
		for (int i = 0; i < query.length; i++) {
			distSq += ((Double)query[i] - (Double)node.record[i]) * ((Double)query[i] - (Double)node.record[i]); //Generalize for any metric
		}
		node.temp = distSq;
		
		if (que.size() < k) {
			que.add(node);
		} else if (que.peek().temp > node.temp) {
			que.remove();
			que.add(node);
			notifyObserversMaxChange(que.peek().temp);
		}
		
		int checked = 0;
		if (query[node.disc].compareTo(node.record[node.disc]) < 0) {
			if (node.getLoson() != null) {
				checked += kNNHelper(query, k, que, node.getLoson());
				if (node.getHison() != null) {
					if (que.size() < k || (Double)query[node.disc] + que.peek().temp >= (Double)node.record[node.disc]) {
						checked += kNNHelper(query, k, que, node.getHison());
					}
				}
			}
		} else {
			if (node.getHison() != null) {
				checked += kNNHelper(query, k, que, node.getHison());
				if (node.getLoson() != null) {
					if (que.size() < k || (Double)query[node.disc] - que.peek().temp <= (Double)node.record[node.disc]) {
						checked += kNNHelper(query, k, que, node.getLoson());
					}
				}
			}
		}
		return checked;

	}
	
	public void optimize() {
		List<T[]> data = preOrder();
		root = optimizedTree(data, ROOT_DISC);
	}
	
	private Node<T> optimizedTree(List<T[]> data, int disc) {
		if (data.size() == 0) {
			return null;
		}
		
		List<T[]> lower = new ArrayList<T[]>();
		List<T[]> higher = new ArrayList<T[]>();
		
		T[] pivot = median(data, disc);
		
		for (T[] datum : data) {
			int compare = datum[disc].compareTo(pivot[disc]);
			if (compare < 0) {
				lower.add(datum);
			} else if (compare > 0) {
				higher.add(datum);
			}
		}
		
		Node<T> root = new Node<T>(pivot);
		root.disc = disc;
		Node<T> loson = optimizedTree(lower, nextDisc(disc));
		Node<T> hison = optimizedTree(higher, nextDisc(disc));
		root.setLoson(loson);
		root.setHison(hison);
		
		return root;
	}
	
	private T[] median(List<T[]> data, int disc) {
		if (data.size() > 2) {
			return kthSmallest(data, disc, data.size() / 2);
		} else {
			return data.get(0);
		}
	}
	
	private T[] kthSmallest(List<T[]> data, int disc, int k) {
		if (data.size() == 1) {
			return data.get(0);
		}
		
		List<T[]> lower = new ArrayList<T[]>();
		List<T[]> higher = new ArrayList<T[]>();
		
		int pivotIndex = (int)(Math.random() * data.size());
		T[] pivot = data.get(pivotIndex);
		
		for (T[] datum : data) {
			int compare = datum[disc].compareTo(pivot[disc]);
			if (compare < 0) {
				lower.add(datum);
			} else if (compare > 0) {
				higher.add(datum);
			}
		}
		
		if (k < lower.size()) {
			return lower.size() > 0 ? kthSmallest(lower, disc, k) : pivot; //TODO This check shouldn't be necessary
		} else if (k > lower.size()) {
			return higher.size() > 0 ? kthSmallest(higher, disc, k - lower.size() - 1) : pivot;
		} else {
			return pivot;
		}
	}
	
	public class Node<T extends Comparable<T>> {
		private T[] record;
		private Node<T> parent;
		private Node<T> loson;
		private Node<T> hison;
		private int disc;
		private int depth;
		private double temp = 0;
		
		private List<Node> bucket; //TODO implement multiple points per bucket
		private static final int BUCKET_SIZE = 3; //TODO
		
		public Node(T[] record) {
			this.record = record;
			disc = ROOT_DISC;
			depth = 0;
		}
		
		public void setParent(Node<T> parent) {
			this.parent = parent;
			this.disc = nextDisc(parent.disc);
			this.depth = parent.depth + 1;
		}
		
		public void setHison(Node<T> hison) {
			this.hison = hison;
			if (hison != null) {
				hison.setParent(this);
			}
		}
		
		public void setLoson(Node<T> loson) {
			this.loson = loson;
			if (loson != null) {
				loson.setParent(this);
			}
		}
		
		public T getKey(int index) {
			return record[index];
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof KdTree.Node)) {
				return false;
			}
			Node<T> n = (Node<T>) o;
			for (int i = 0; i < record.length; i++) {
				if (!n.record[i].equals(record[i])) {
					return false;
				}
			}
			return true;
		}
		
		public int hashCode() {
			Integer hash = 1;
			for (T elem : record) {
				hash *= elem.hashCode();
			}
			return hash;
		}
		
		private String toString(StringBuffer b, int indent) {

			b.append(Arrays.toString(record));
			b.append('\n');
			if (loson != null) {
				for (int i = 0; i < indent; i++) {
					b.append(' ');
				}
				b.append("left: ");
				loson.toString(b, indent + 1);
			}
			if (hison != null) {
				for (int i = 0; i < indent; i++) {
					b.append(' ');
				}
				b.append("right: ");
				hison.toString(b, indent + 1);
			}
			return new String(b);
		}
		
		public String toString() {
			return toString(new StringBuffer(), 0);
		}
		
		public int getDisc() {
			return disc;
		}
		
		public boolean hasLoson() {
			return loson != null;
		}
		
		public boolean hasHison() {
			return hison != null;
		}
		
		public Node<T> getLoson() {
			return loson;
		}
		
		public Node<T> getHison() {
			return hison;
		}
	}
	
	private void notifyObserversMaxChange(double max) {
		for (KdTreeObserver observer : observers) {
			observer.currentMaximumDistanceChanged(max);
		}
	}
	
	private void notifyObserversNodeChange(T[] data) {
		for (KdTreeObserver observer : observers) {
			observer.activeDataChanged(data);
		}
	}
	
	private void notifyObserversQueryChange(T[] query) {
		for (KdTreeObserver observer : observers) {
			observer.queryChanged(query);
		}
	}
	
	public void addObserver(KdTreeObserver observer) {
		observers.add(observer);
	}
	
}
