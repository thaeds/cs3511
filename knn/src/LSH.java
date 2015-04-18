
public class LSH 
{
	
	private HashTable[] hashTables;
	
	public LSH(double[][] setPoints)
	{
		hashTables=new HashTable[32];
		int u=(int)Math.round(Math.pow(2, 24*Math.log(2))*Math.log(setPoints.length));
		for(int wLog=0; wLog<hashTables.length; wLog++)
		{
			
		}
		
	}

}
