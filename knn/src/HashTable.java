import java.util.ArrayList;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.Hashtable;
import java.util.Random;

public class HashTable {
	private static final int DIMS = 24;
//	private static final double loadFactor = .75;
	private RealMatrix A;
	private Object hashbydim[][]; //Object is ArrayList<Node>
	private int U;
	private int w;
	private LatticeDecoder decoder;

	public HashTable(int w, int dimensions, int numberOfRounds) {
		this.decoder = new LatticeDecoder(this.w);
		Random rnd = new Random();
		this.w = w;
		this.U = numberOfRounds;
		this.A = A.createMatrix(dimensions, DIMS);

		for(int i = 0; i < dimensions; i++) {
			for(int j = 0; j < DIMS; j++) {
				A.setEntry(i,j,rnd.nextDouble());
			}
		}

	}
	
	/*
     * @param points Takes in an array of n length double arrays, each representing a point in n space
     */
	public void preProcess(double dataPoints[][])
	{
        this.hashbydim = new Object[DIMS][(int) Math.round(dataPoints[0].length/this.w)];
//        this.hashbydim = new ArrayList<ArrayList<ArrayList<Node>>(Integer.MAX_VALUE/this.w)>(DIMS); // [DIMS][Integer.MAX_VALUE/this.w];

		for(double[] point : dataPoints) {
			int ipoint[point.length];
			double[] projectedpoint;

			for(int i = 0; i < point.length; i++) {
				ipoint[i] = (int) point[i];
			}
			Node thisNode = new Node(ipoint);
			projectedpoint = A.operate(point);

			double latticepoint[] = decoder.decode(offset, projectedpoint); //TODO figure offset out
			for (int i = 0; i < DIMS; i++)
			{
				ArrayList<Node> bucket = (ArrayList<Node>) hashbydim[i][(int)latticepoint[i]];
			}
		}

	}

	public Node query(Node queryNode) {
		double dlocation[queryNode.location.length];
		for(int i = 0; i < queryNode.location.length; i++) {
			dlocation[i] = (double) queryNode.location[i];
		}
		double projectedpoint[];
		projectedpoint = this.A.operate(dlocation);
		double latticepoint[] = decoder.decode(offset, projectedpoint); //TODO figure offset out
		ArrayList<Node> nodesFound;
		for(int i = 0; i < DIMS; i++) {
			ArrayList<Node> nodesInBucket = ((ArrayList<Node>) hashbydim[i][(int) latticepoint[i]]);
			if (0 == i) {
				nodesFound = nodesInBucket;
			} else {
				for(node : nodesFound) {
					if(!nodesInBucket.contains(node)) {
						nodesFound.remove(node);
					}
				}
			}
		}
	}

//	/*
//     * @return Returns a 24 dim vector representing the adjacent lattice
//     */
//	ArrayList<Integer> leechLatticeDecode(ArrayList<Integer> offset, int w, ArrayList<Integer> point) {
//		return point;
//	}
}
