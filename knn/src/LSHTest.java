import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;


public class LSHTest 
{
	
	public static void main(String[] args)
	{
		int[] numberOfSetPoints={10, 100, 1000, 10000, 20000};
		int[] sideDimensions={2, 4, 8, 16, 32, 64};
		
		for(int numberOfSetPointsIndex=0; numberOfSetPointsIndex<numberOfSetPoints.length; numberOfSetPointsIndex++)
		{
			for(int sideDimensionsIndex=5; sideDimensionsIndex<sideDimensions.length; sideDimensionsIndex++)
			{
				System.out.print("{"+numberOfSetPoints[numberOfSetPointsIndex]+", "+sideDimensions[sideDimensionsIndex]+", ");
				test(numberOfSetPoints[numberOfSetPointsIndex], 10000, sideDimensions[sideDimensionsIndex], 10000);
				System.out.println("},");
			}
		}
	}
	
	public static double[] linearSearch(double[][] setPoints, double[] query) {
		double minDist = Integer.MAX_VALUE;
		double[] minPoint = null;
		for (int i = 0; i < setPoints.length; i++) {
			double distSq = 0;
			for (int j = 0; j < setPoints[0].length; j++) {
				distSq += (setPoints[i][j] - query[j]) * (setPoints[i][j] - query[j]);
			}
			if (distSq < minDist) {
				minDist = distSq;
				minPoint = setPoints[i];
			}
		}
		return minPoint;
	}
	
	public static void test(int numberSetPoints, int numberQueryPoints, int sideDimension, int numberOfTests) {
		double[][] setPoints=getImagePoints(numberSetPoints, sideDimension, 0);
		System.out.println("Got set points");
		double[][] queryPoints=getImagePoints(numberSetPoints, sideDimension, 0);
		System.out.println("Got query points");
		double w = Math.pow(24,  1.0/4);
		int u = (int)Math.round(Math.pow(2, 24*Math.log(2))*Math.log(setPoints.length));
		LSH lsh = new LSH(setPoints);
		System.out.println("Created lsh");
		long time = System.nanoTime();
		for(int testNumber = 0; testNumber < numberOfTests; testNumber++) {
			System.out.println("Test number:" + testNumber);
			int[] lshClosest = lsh.nearestNeighbor(queryPoints[testNumber%queryPoints.length]).location;
			double[] actualClosest = linearSearch(setPoints, queryPoints[testNumber%queryPoints.length]);
			boolean correct = true;
			System.out.println(Arrays.toString(lshClosest));
			System.out.println(Arrays.toString(actualClosest));
			System.out.println();
		}
		time = System.nanoTime() - time;
		System.out.print(time);
	}
	
	
	
	
	public static double[][] getImagePoints(int number, int sideDimension, int imgStartOffset)
    {
    	int numberCreated=0;
    	int numberImagesRead=imgStartOffset;
    	String zeros="0000";
    	double[][] points=new double[number][sideDimension*sideDimension];
    	while(numberCreated<number)
    	{
    		BufferedImage img=null;
    		try 
    		{
				img=ImageIO.read(new File("C:\\Users\\Colby_000\\Desktop\\Trevi Images\\patches"+zeros.substring((""+numberImagesRead).length())+(""+numberImagesRead)+".bmp"));
			} 
    		catch (IOException e) 
    		{
				e.printStackTrace();
			}
    		for(int rowPix=0; rowPix<img.getHeight() && numberCreated<number; rowPix+=64)
    		{
    			for(int colPix=0; colPix<img.getWidth() && numberCreated<number; colPix+=64)
        		{
    				for(int row=0; row<sideDimension; row++)
    				{
    					for(int col=0; col<sideDimension; col++)
        				{
    						points[numberCreated][row*sideDimension+col]=img.getRGB(row+rowPix, col+colPix);	
        				}    					
    				}  		     
    				numberCreated++;
        		}
    		}
    		numberImagesRead++;
    		System.out.println(numberCreated);
    	} 
    	return points;
    }
	
}
