package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import model.Facade;
import model.liarsDice.LiarsDiceGameFactory;

public class GUI extends JFrame {
   
    private Facade facade;
    
    private JMenuBar menuBar;
    private JMenu menu, tournamentOptionsMenu, submenu;
    private JRadioButtonMenuItem liarsDiceRadioButton, exampleRadioButton;
    private JMenuItem exitMenuItem;
//    private JCheckBoxMenuItem cbMenuItem;
    
    private TournamentView tournamentPane;
    private LiarsDicePlayView playPane;
    private long timeoutInMicroseconds;
   
    public GUI()
    {
    	facade = new Facade();
    	
    	//defaults for tournament setup
    	facade.changeGame("LiarsDice");
    	timeoutInMicroseconds = 50000;
    	facade.setTimeout(timeoutInMicroseconds);
    	
    	//setup the general layout
        Container pane = getContentPane();
    	
    	/* Menu bar example: (has example of checkboxes too)
    	 * http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
    	 */
    	//Create the menu bar.
    	menuBar = new JMenuBar();
    	menuBar.setBackground(new Color(135, 196, 250)); //sky blue
    	//menuBar.setBackground(new Color(200, 200, 200)); //light grey

    	//Build the first menu.
    	menu = new JMenu("Menu");
    	menu.setMnemonic(KeyEvent.VK_M);
    	menu.getAccessibleContext().setAccessibleDescription("Menu options");
    	
    	exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_E);
    	exitMenuItem.getAccessibleContext().setAccessibleDescription("Exit the program");
    	exitMenuItem.addActionListener(new MenuItemListener());
    	menu.add(exitMenuItem);
    	
    	menuBar.add(menu);
    	
    	//menuBar.add(new JSeparator(JSeparator.VERTICAL)); //throws off the alignment :(
    	
    	tournamentOptionsMenu = new JMenu("Tournmanent Options");
    	tournamentOptionsMenu.setMnemonic(KeyEvent.VK_T);
    	tournamentOptionsMenu.getAccessibleContext().setAccessibleDescription("Change/view tournament options");

    	//a submenu
    	tournamentOptionsMenu.addSeparator();
    	submenu = new JMenu("Choose Tournament Game");
    	submenu.setMnemonic(KeyEvent.VK_C);

    	//a group of radio button menu items
    	ButtonGroup group = new ButtonGroup();
    	liarsDiceRadioButton = new JRadioButtonMenuItem("Liar's Dice");
    	liarsDiceRadioButton.setSelected(true);
    	liarsDiceRadioButton.setMnemonic(KeyEvent.VK_L);
    	liarsDiceRadioButton.setActionCommand("LiarsDice");
    	liarsDiceRadioButton.addActionListener(new RadioButtonListener());
    	//liarsDiceMenuItem.set
    	group.add(liarsDiceRadioButton);
    	submenu.add(liarsDiceRadioButton);

    	exampleRadioButton = new JRadioButtonMenuItem("(add future games here)");
    	exampleRadioButton.setMnemonic(KeyEvent.VK_A);
    	exampleRadioButton.setActionCommand("GameNameHere");
    	exampleRadioButton.addActionListener(new RadioButtonListener());
    	group.add(exampleRadioButton);
    	submenu.add(exampleRadioButton);
    	
    	tournamentOptionsMenu.add(submenu);
    	
    	menuBar.add(tournamentOptionsMenu);
    	
    	this.setJMenuBar(menuBar);

    	//end of menu bar
    	
    	tournamentPane = new TournamentView(facade);
    	tournamentPane.loadTable();
        
        playPane = new LiarsDicePlayView(facade);
        
        //tabs
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Tournament", tournamentPane);
        tabPane.addTab("Liar's Dice", playPane);
        pane.add(tabPane);
        
        //wrapup
        setTitle("Programmer AI Tournament");
        setLocation(100, 0); 
        this.setMinimumSize(new Dimension(735, 700));
        this.setPreferredSize(new Dimension(750, 750));
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }
    
    private class RadioButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			facade.changeGame(e.getActionCommand());
		}
    }
    
    private class MenuItemListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == exitMenuItem){
				System.exit(0);
			}
		}
    }
	
    public static void main(String[] args)
    {
//    	try{
//			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//		        if ("Nimbus".equals(info.getName())) {
//		            UIManager.setLookAndFeel(info.getClassName());
//		            break;
//		        }
//		    }
//		}catch(Exception e){
//			//use standard
//		}
    	
    	GUI gui = new GUI();
    }
}
