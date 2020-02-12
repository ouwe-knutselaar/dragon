package dragoncontrol;

import javafx.scene.control.MenuBar;



import javafx.scene.control.Menu;
import javafx.scene.control.ToggleGroup;

/**
 * This class maintains the menustructure
 * @author gebruiker
 *
 */
public class DragonMenu extends MenuBar 
{

	// Top menubar items
	Menu effect=new Menu("File");
	Menu midi=  new Menu("Settings");
	

	
	final ToggleGroup toggleGroup = new ToggleGroup();
	
	
	
	public DragonMenu()
	{
		this.getMenus().addAll(effect,midi);
	}
	




}
