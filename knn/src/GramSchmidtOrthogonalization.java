import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public class GramSchmidtOrthogonalization 
{
	
	public static RealMatrix orthogonalize(RealMatrix toOrthogonalize)
	{
		for(int columnNumber=0; columnNumber<toOrthogonalize.getColumnDimension(); columnNumber++)
		{
			double vectorLength=toOrthogonalize.getColumnVector(columnNumber).getNorm();
			for(int rowNumber=0; rowNumber<toOrthogonalize.getRowDimension(); rowNumber++)
			{
				toOrthogonalize.multiplyEntry(rowNumber, columnNumber, 1/vectorLength);
			}
			for(int subtractColumnNumber=columnNumber+1; subtractColumnNumber<toOrthogonalize.getColumnDimension(); subtractColumnNumber++)
			{
				RealVector projection=toOrthogonalize.getColumnVector(columnNumber).mapMultiplyToSelf(toOrthogonalize.getColumnVector(subtractColumnNumber).dotProduct(toOrthogonalize.getColumnVector(columnNumber)));
				toOrthogonalize.setColumnVector(subtractColumnNumber, toOrthogonalize.getColumnVector(subtractColumnNumber).subtract(projection));
			}
		}
		return toOrthogonalize;
	}

}
