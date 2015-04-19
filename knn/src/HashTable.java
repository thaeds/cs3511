import java.util.ArrayList;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class HashTable {
	private static final int DIMS = 24;
	private static final double loadFactor = .75;
	private RealMatrix A;
	private Hashtable<ArrayList<Double>, Node> buckets;
	private List<double[]> offsets;
	private double w;
	private LatticeDecoder decoder;

	public HashTable(double w, int dimensions, int numberOfRounds) {
		this.w = w;
		this.decoder = new LatticeDecoder(this.w);
		Random rnd = new Random();
		this.A = MatrixUtils.createRealMatrix(DIMS, dimensions);
		offsets = new ArrayList<double[]>();
		for(int i = 0; i < dimensions; i++) {
			for(int j = 0; j < DIMS; j++) {
				A.setEntry(j, i, rnd.nextDouble());
			}
		}

		for(int i = 0; i < numberOfRounds; i++) {
			double rndoffset[] = new double[DIMS];
			double magnitude = 0;
			for(int j = 0; j < DIMS; j++) {
				double rndval = rnd.nextDouble();
				rndoffset[j] = rndval;
				magnitude += rndval*rndval;
			}
			double scaleFactor = Math.sqrt(magnitude)/4/this.w;
			for(int j = 0; i < rndoffset.length; i++) {
				rndoffset[j] = rndoffset[j]/scaleFactor;
			}
			offsets.add(rndoffset);
		}

	}
	
	/*
     * @param points Takes in an array of n length double arrays, each representing a point in n space
     */
	public void preProcess(double dataPoints[][])
	{
		this.buckets = new Hashtable<ArrayList<Double>, Node>((int) ((dataPoints.length + 1)/loadFactor));
//        this.buckets = new ArrayList<ArrayList<ArrayList<Node>>(Integer.MAX_VALUE/this.w)>(DIMS); // [DIMS][Integer.MAX_VALUE/this.w];

		for(double[] point : dataPoints) {
			int ipoint[] = new int[point.length];
			double[] projectedpoint = A.operate(point);
			Node thisNode;

			for(int i = 0; i < point.length; i++) {
				ipoint[i] = (int) point[i];
			}
            thisNode = new Node(ipoint);
			for(int trial = 0; trial < offsets.size(); trial++) {
				double latticepoint[];
				ArrayList<Double> lp;

				latticepoint = decoder.decode(offsets.get(trial), projectedpoint); //TODO figure offset out
				lp = new ArrayList<Double>(DIMS);
				for(int i = 0; i < DIMS; i++) {
					lp.add(latticepoint[i]);
				}
				buckets.put(lp, thisNode);
			}
		}

	}

	public Node query(Node queryNode) {
		double projectedpoint[];
		double latticepoint[];
		ArrayList<Double> hashablelocation;
		Node foundNode = null;

		projectedpoint = this.A.operate(intAtodoubleA(queryNode.location));
		for(int i = 0; null == foundNode && i < offsets.size(); i++) {
			latticepoint = decoder.decode(offsets.get(i), projectedpoint);
			hashablelocation = PrimdoubleToAL(latticepoint);
			foundNode = buckets.get(PrimdoubleToAL(latticepoint));
		}

		return foundNode;
	}

	public Node queryBest(Node queryNode) {
		Node bestNode = null;
		double bestDist = Integer.MAX_VALUE;

		ArrayList<Node> nodes = new ArrayList<Node>(offsets.size());
		for(int i = 0; i < offsets.size(); i++) {
			nodes.add(queryOffset(queryNode, offsets.get(i)));
		}
		for(int i = 0; i < nodes.size(); i++) {
			Node thisNode = nodes.get(i);
			if(thisNode != null) {
				double thisDist = queryNode.l2Distance((thisNode));
				if(bestDist > thisDist) {
					bestNode = thisNode;
					bestDist = queryNode.l2Distance(thisNode);
				}
			}
		}
		//System.out.println(Arrays.toString(nodes.toArray()));
		return bestNode;
	}

	private Node queryOffset(Node queryNode, double[] offset) {
		double projectedpoint[];
		ArrayList<Double> hashablelocation;

		projectedpoint = this.A.operate(intAtodoubleA(queryNode.location));
		hashablelocation = PrimdoubleToAL(decoder.decode(offset, projectedpoint));

		return buckets.get(hashablelocation);
	}

	private double[] intAtodoubleA(int in[]) {
		double out[] = new double[in.length];
		for(int i = 0; i < in.length; i++) {
			out[i] = (double) in[i];
		}

		return out;
	}

	private ArrayList<Double> PrimdoubleToAL(double in[]) {
		ArrayList<Double> out = new ArrayList<Double>(in.length);
		for (int i = 0; i < in.length; i++) {
			out.add(in[i]);
		}
		return out;
	}

//	/*
//     * @return Returns a 24 dim vector representing the adjacent lattice
//     */
//	ArrayList<Integer> leechLatticeDecode(ArrayList<Integer> offset, int w, ArrayList<Integer> point) {
//		return point;
//	}
}
