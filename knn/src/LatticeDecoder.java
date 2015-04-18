import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public class LatticeDecoder 
{
	
	private LUDecomposition inverseLeech;
	
	public LatticeDecoder(double w)
	{
		inverseLeech=new LUDecomposition(new Array2DRowRealMatrix(
											  new double[][]
											  {
											  	new double[]{8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{2, 2, 2, 2, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{2, 0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0},
											  	new double[]{2, 0, 2, 0, 2, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0},
											  	new double[]{2, 0, 0, 2, 2, 2, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0},
											  	new double[]{2, 2, 0, 0, 2, 0, 2, 0, 2, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0},
											  	new double[]{0, 2, 2, 2, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0},
											  	new double[]{0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 0, 0},
											  	new double[]{0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0},
											  	new double[]{-3,1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
											  }).scalarMultiply(w));
		/*orthangonalizedLeech=GramSchmidtOrthogonalization.orthogonalize(new Array2DRowRealMatrix(
				  new double[][]
				  {
				  	new double[]{3, 7},
				  	new double[]{4, 24},
				  }));*/
	}
	
	/*public double[] decode(double[] offset, double[] point)
	{
		RealVector pointVector=new ArrayRealVector(point);
		RealVector offsetVector=new ArrayRealVector(offset);
		pointVector.subtract(offsetVector);
		double[] vectorScaleFactors=new double[orthangonalizedLeech.getColumnDimension()];
		for(int columnNumber=0; columnNumber<orthangonalizedLeech.getColumnDimension(); columnNumber++)
		{
			vectorScaleFactors[columnNumber]=hillClimbingOptimize(orthangonalizedLeech.getColumnVector(columnNumber), pointVector);
		}
		RealVector vectorScaleFactorsVector=new ArrayRealVector(vectorScaleFactors);
		return orthangonalizedLeech.preMultiply(vectorScaleFactorsVector).toArray();
	}*/
	
	public double[] decode(double[] offset, double[] point)
	{
		RealVector pointVector=new ArrayRealVector(point);
		RealVector offsetVector=new ArrayRealVector(offset);
		pointVector=pointVector.subtract(offsetVector);
		RealVector decoded=inverseLeech.getSolver().solve(pointVector);
		for(int row=0; row<decoded.getDimension(); row++)
		{
			decoded.setEntry(row, Math.round(decoded.getEntry(row)));
		}
		return decoded.toArray();
	}
	
	private int hillClimbingOptimize(RealVector vector, RealVector point)
	{
		double dotProduct=vector.dotProduct(point);
		int ceilScale=(int)Math.ceil(dotProduct);
		int floorScale=(int)Math.floor(dotProduct);
		double ceilL2Distance=vector.mapMultiply(ceilScale).getDistance(point);
		double floorL2Distance=vector.mapMultiply(floorScale).getDistance(point);
		if(ceilL2Distance<floorL2Distance)
		{
			return ceilScale;
		}
		else
		{
			return floorScale;
		}
	}
	
	

}
