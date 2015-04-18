import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class LSHTest 
{
	
	public static void main(String[] args)
	{
		int[] numberOfSetPoints={10, 100, 1000, 10000, 20000};
		int[] sideDimensions={2, 4, 8, 16, 32, 64};
		
		for(int numberOfSetPointsIndex=0; numberOfSetPointsIndex<numberOfSetPoints.length; numberOfSetPointsIndex++)
		{
			for(int sideDimensionsIndex=0; sideDimensionsIndex<sideDimensions.length; sideDimensionsIndex++)
			{
				System.out.print("{"+numberOfSetPoints[numberOfSetPointsIndex]+", "+sideDimensions[sideDimensionsIndex]+", ");
				test(numberOfSetPoints[numberOfSetPointsIndex], 10000, sideDimensions[sideDimensionsIndex], 10000);
				System.out.println("},");
			}
		}
	}
	
	public static void test(int numberSetPoints, int numberQueryPoints, int sideDimension, int numberOfTests)
	{
		double[][] setPoints=getImagePoints(numberSetPoints, sideDimension, 0);
		double[][] queryPoints=getImagePoints(numberSetPoints, sideDimension, 0);
		double w=Math.pow(24,  1/4);
		int u=(int)Math.round(Math.pow(2, 24*Math.log(2))*Math.log(setPoints.length));
		HashTable LSHHashTable=new HashTable(w, setPoints[0].length, u);
		LSHHashTable.preProcess(setPoints);
		long time=System.nanoTime();
		for(int testNumber=0; testNumber<numberOfTests; testNumber++)
		{
			LSHHashTable.query(new Node(queryPoints[testNumber%queryPoints.length]));
		}
		time=System.nanoTime()-time;
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
				img=ImageIO.read(new File("C:\\Users\\C\\workspace\\Trevi Images\\patches"+zeros.substring((""+numberImagesRead).length())+(""+numberImagesRead)+".bmp"));
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
