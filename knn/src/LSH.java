
public class LSH {
	
	private HashTable[] hashTables;
	
	public LSH(double[][] setPoints) {
		double maxW = Math.pow(Math.pow(2, 64) * setPoints.length, 1.0 / 2);
		//double maxW = 1.5;

		hashTables = new HashTable[(int)Math.ceil(Math.log(maxW) / Math.log(2))];
		int u = (int)Math.round(Math.pow(2, 24 * Math.log(2)) * Math.log(setPoints.length));
		int hashTableIndex = hashTables.length - 1;
		while(maxW > 1 && hashTableIndex >= 0) {
			System.out.println("maxW: " + maxW);
			hashTables[hashTableIndex] = new HashTable(maxW, setPoints[0].length, u);
			hashTables[hashTableIndex].preProcess(setPoints);
			System.out.println("Preprocessed");
			maxW /= 2;
			hashTableIndex--;
		}
	}
	
	public Node nearestNeighbor(double[] queryPoint) {
		int specificity = hashTables.length - 1;
		Node n;
		while(specificity >= 0 && (n = hashTables[specificity].query(new Node(queryPoint))) != null) {
			specificity--;
		}
		specificity++;
		n = hashTables[specificity].query(new Node(queryPoint));
		Node nearestNeighbor = hashTables[specificity].queryBest(new Node(queryPoint));
		if (nearestNeighbor == null) {
			int i = 0;
		};
		return nearestNeighbor;
	}

}
