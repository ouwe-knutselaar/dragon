package dragonraspberry.pojo;

public class DisplayTools {
	
	public static String ArrayToHexString(int value[])
	{
		StringBuilder output=new StringBuilder();
	
		for(int tel=0;tel<value.length;tel++)
		{
			output.append(Integer.toHexString(value[tel])).append(" ");
		
		}
		return output.toString();
	}

}
