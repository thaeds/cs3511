
public class Node 
{
	
	public int[] location;
	
	public Node(int[] location)
	{
		this.location=location;
	}
	
	public double l2Distance(Node other)
	{
		double distance=0;
		for(int dimension=0; dimension<location.length; dimension++)
		{
			distance+=((double)location[dimension]-(double)other.location[dimension])*((double)location[dimension]-(double)other.location[dimension]);
		}
		return distance;
	}

}
