import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class PlayerType implements ActionListener{
	String[] cmbList = {"Random Player", "Heuristic Player", "MinMax Player"};
    JComboBox cmb;
    Game g;
	int id;
    public PlayerType(int id, Game g){
    	this.g = g;
		this.id = id;
		if(id == 1) {	//Theseus has all 3 types of Players available
			cmb = new JComboBox(cmbList);
		}
		else {	//For Minotaur we have only implemented the random player
			String[] str = {"Random Player"};
			cmb = new JComboBox(str);
		}
		cmb.setSelectedIndex(0);
		cmb.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(id == 2) {
			return; //If it's about Minotaur we have only implemented the Random Player
		}
		String PlType = (String) cmb.getSelectedItem();
		switch(PlType) {
			case "Random Player":
				g.currentTheseusType = 0;
				break;
			case "Heuristic Player":
				g.currentTheseusType = 1;
				break;
			case "MinMax Player":
				g.currentTheseusType = 2;
				break;
		}
		System.out.println(g.theseusType);
		
	}

}
