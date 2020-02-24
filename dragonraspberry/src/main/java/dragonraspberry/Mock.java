package dragonraspberry;

import dragonraspberry.continuemovement.MovementArray;

public class Mock {

	public static void main(String argv[])
	{
		MovementArray test=new MovementArray(100,100,0,0);
		
		for(int tel=0;tel<100;tel++)
		{
			System.out.println(" "+tel+" "+test.getNext());
		}
	}
	
}
