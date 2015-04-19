import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


public class Main {
	
	public static Runtime runtime = Runtime.getRuntime();
	public static KdTree<Double> treviTree;
	public static BufferedWriter output;
	
	private static final int PATCHES_PER_IMAGE = 16 * 16;

	public static void main(String[] args) throws Exception {
		kdTreeVisual();
		//kdTreeTest(100000, 1000);
		//LSH();
		
		
		/*
		int[] battery = new int[] {10, 100, 1000, 10000, 20000};
		int[] numTesting = new int[] {1000, 1000, 100, 10, 10, 1};
		int[] sizes = new int[] {2, 4, 8, 16, 32, 64};
		for (int i = 0; i < sizes.length; i++) {
			treviBattery(root + "\\kdTreejbk" + "Optimized" + sizes[i] + ".txt", battery, numTesting[i], 100, true, sizes[i]);
			//treviBattery(root + "\\kdTree" + "Standard" + sizes[i] + ".txt", battery, numTesting[i], 100, false, sizes[i]);
		}
		
		//treviBattery(root + "\\kdTree111" + "Optimized" + sizes[3] + ".txt", new int[] {10000}, 1000, 100, true, 64);
		//treviBattery(root + "\\kdTree111" + "Optimized" + sizes[4] + ".txt", battery, 1000, 100, true, sizes[4]);
		 */
		

	}
	
	public static void treviBattery(String fileName, int[] numPointsArr, int numTesting, int numCycles, boolean optimize, int size) throws Exception {
		////File outputFile = new File(fileName);
		//outputFile.createNewFile();
		//output = new BufferedWriter(new FileWriter(outputFile));
		String queryString = numTesting == 1 ? "query" : "queries";
		String repString = numCycles == 1 ? "repetition" : "repetitions";
		for (int numPoints : numPointsArr) {
			println("For " + numPoints + " " + size + "x" + size + " patches with " + numTesting + " distinct " + queryString + " averaged over " + numCycles + " " + repString + ":");
			List<Double[]> queries = readTreviData(numPoints, numTesting, true, size);
			treviTest(numPoints, numTesting, numCycles, queries, treviTree);
			println("");
		}
		//output.flush();
		//output.close();
	}
	
	public static List<Double[]> readTreviData(int numPoints, int numTesting, boolean optimize, int size) throws Exception {
		treviTree = new KdTree<Double>(size * size, new EuclideanMetric());
		List<Double[]> data = createImageMOs(0, numPoints, size);
		List<Double[]> queries = createImageMOs(300 * PATCHES_PER_IMAGE, 300 * PATCHES_PER_IMAGE + numTesting, size);
		println("    Read all necessary data from files");
		long startTime = System.nanoTime();
		for (Double[] datum : data) {
			treviTree.add(datum);
		}
		long endTime = System.nanoTime();
		println("    Added data to Trevi tree: " + (endTime - startTime) + " ns");
		if (optimize) {
			startTime = System.nanoTime();
			treviTree.optimize();
			endTime = System.nanoTime();
			println("    Optimized tree: " + (endTime - startTime) + " ns");
		} else {
			println("    Did not optimize tree");
		}
		return queries;
	}
	
	public static void treviTest(int numPoints, int numTesting, int numCycles, List<Double[]> queries, KdTree treviTree) throws Exception {
		long totalTime = 0;
		print("    Run number: ");
		for (int i = 0; i < numCycles; i++) {
			print(i + " ");
			long startTime = System.nanoTime();
			for (Double[] query : queries) {
				treviTree.kNN(query, 1).get(0);
			}
			long endTime = System.nanoTime();
			totalTime += endTime - startTime;
		}
		println();
		double averageResponseTime = totalTime / (double)(numCycles * queries.size());
		println("    Average response time: " + averageResponseTime + " ns");
		println("    Memory usage: " + (runtime.totalMemory() - runtime.freeMemory()));
	}
	
	public static void kdTreeVisual() {
		VisualKdTree tree = new VisualKdTree(2);
		KdTreePanel panel = new KdTreePanel(tree);
		tree.addObserver(panel);
		
		new Thread() { 
			public void run() {
				for (int j = 0; j < 40; j++) {
					for (int i = 0; i < 1; i++) {
						Double[] data = {500 * Math.random(), 500 * Math.random()};
						tree.add(data);
					}

					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				tree.update();
				panel.repaint();
				tree.optimize();
				tree.update();
				panel.repaint();
				/*while (true) {
					panel.repaint();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}*/
			}
		}.start();
		//System.out.println(tree);
		
		panel.addMouseListener(panel);
		
		
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(panel);
		window.setSize(600, 600);
		window.setVisible(true);
		
		
		
		//Median test code
		/*for (int i = 0; i < 100; i++) {
			List<Double[]> list = new ArrayList<Double[]>();
			int size = (int)(Math.random() * 1000 + 1);
			for (int j = 0; j < size; j++) {
				Double[] data = {Math.random(), Math.random()};
				list.add(data);
			}
			int k = list.size() / 2;
			Double[] guess = tree.kthSmallest(list, 0, k);
			int count = 0;
			for (int j = 0; j < size; j++) {
				if (list.get(j)[0] < guess[0]) {
					count++;
				}
			}
			System.out.println("Wanted " + k + " smaller out of " + list.size() + ". Found " + count + " smaller.");
		}*/
	}
	
	public static List<Double[]> createImageMOs(int low, int high, int size) throws Exception {
		int patchIndex = low;
		String zeros = "0000";
		List<Double[]> queries = new ArrayList<Double[]>();
		int imagesRead = 0;
		
		while(patchIndex < high) {
			BufferedImage img = null;
			int imageIndex = patchIndex / (PATCHES_PER_IMAGE);
			img = ImageIO.read(new File("C:\\Users\\Colby_000\\Desktop\\Trevi Images\\patches" + zeros.substring(("" + imageIndex).length())+("" + imageIndex) + ".bmp"));
			imagesRead++;
			
			while (patchIndex < PATCHES_PER_IMAGE * (imageIndex + 1) && patchIndex < high) {
				Double[] imageVector = getPatch(img, patchIndex - PATCHES_PER_IMAGE * imageIndex, size);
				patchIndex++;
				queries.add(imageVector);
			}

		}
		println("    Read " + imagesRead + " file" + (imagesRead == 1 ? "" : "s"));
		return queries;
	}
	
	public static Double[] getPatch(BufferedImage img, int patchNumber, int size) {
		int[][] patch = new int[64][64];
		int rowOffset = (patchNumber / 16) * 64;
		int colOffset = (patchNumber % 16) * 64;
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				patch[row][col] = img.getRGB(row + rowOffset, col + colOffset);	
			}    					
		}
		Double[] imageVector = new Double[patch.length * patch[0].length];
		for(int rowNum = 0; rowNum < patch.length; rowNum++) {
			for(int colNum=0; colNum < patch.length; colNum++) {
				imageVector[patch.length * rowNum+colNum]=(double)patch[rowNum][colNum];        		            	
			}      	
		}
		return imageVector;
	}
	
	public static Double[] closestPointLinearSearch(List<Double[]> data, Double[] query) {
		double minDistanceSq = Double.MAX_VALUE;
		Double[] closest = data.get(0);
		for (Double[] datum : data) {
			double distSq = 0;
			for (int i = 0; i < query.length; i++) {
				distSq += ((Double)query[i] - (Double)datum[i]) * ((Double)query[i] - (Double)datum[i]); //Generalize for any metric
			}
			if (distSq < minDistanceSq) {
				closest = datum;
				minDistanceSq = distSq;
			}
		}
		return closest;
	}

	public static void println() {
		println("");
	}
	
	public static void println(String s) {
		print(s + "\n");
	}
	
	public static void print() {
		print("");
	}
	
	public static void print(String s) {
		System.out.print(s);
		/*try {
			output.write(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
