import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Gui{
	Game g;
    JFrame frame;	//The window of our application
    JPanel panel;	//The main panel that holds the round number and the game information of every player in labels
    JLabel roundLabel;
    JLabel theseus;
    JLabel mino;
    JLabel moveT;
    JLabel moveM;
    JLabel scoreT;
    JLabel scoreM;
    JLabel start;
	JLabel gameOver;
    int tx,ty,mx,my;	//holds the coordinates of each player
    boolean flag;	//for technical reasons to know when to refresh the frame
    Image minotaurImg;
    Image theseusImg;
    Image supplyImg;
    int[] paintcode; //used for knowing what to paint
    //constructor of the Gui class
    public Gui(Game g){
		// initialize images
		minotaurImg = new ImageIcon("images/minotaur.png").getImage();
		theseusImg = new ImageIcon("images/theseus.png").getImage();
		supplyImg = new ImageIcon("images/supply.png").getImage();
		//tileSize
		int tileSize = 36;
		//imageSize
		int imageSize = tileSize-2;
		//offset
		int offset = (tileSize-imageSize)/2;
		//number of tiles in each line (horizontally or vertically)
		int n = 15;
		//Jpanel size
		int jpanelSize = tileSize*n;

    	flag = false;//when creating a board we set it to true
    	paintcode = new int[3];	//first spot for recognizing calls
    	//second spot for the distance in scaling factor of pixels in the x-direction
    	//third spot >>      >>      >>    >>       >>        >>     >>   y-direction
    	paintcode[0] = 0;
    	this.g = g;
        frame = new JFrame();	//make a new frame (window)
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics gr) {
	            super.paintComponent(gr);
	            gr.drawRect(0, 0, jpanelSize, jpanelSize);
	            gr.setColor(new Color(255,204,153));	// orange-brown color
	            gr.fillRect(0, 0, jpanelSize, jpanelSize);
	           
	            gr.setColor(new Color(224,224,224));	// gray color
	            for(int i = 0; i < n; i++) {
	            gr.drawLine(0, tileSize*i, jpanelSize, tileSize*i);
	            }
	            for(int i = 0; i < n; i++) {
	            	gr.drawLine(tileSize*i, 0, tileSize*i, jpanelSize);
	            }
	            
				//labyrinth entrance
				gr.setColor(Color.WHITE);
	            gr.drawLine(0, jpanelSize, tileSize, jpanelSize);
				// boundary walls
	            gr.setColor(Color.BLACK);
	            gr.drawLine(0, 0, jpanelSize, 0);	//top
	            gr.drawLine(0, 0, 0, jpanelSize);	//left
	            gr.drawLine(jpanelSize, 0, jpanelSize, jpanelSize);	//right
	            gr.drawLine(tileSize, jpanelSize, jpanelSize, jpanelSize);	//bottom


 	            if(paintcode[0] != 0) {
 	            	if(paintcode[0]==1) {	//when the game starts
 	            		ty = 0; tx= g.board.getN() - 1;
 	            		mx = my =(g.board.getN() - 1) / 2;
						// Draw Minotaur
					    gr.drawImage(minotaurImg, my*tileSize+offset, mx*tileSize+offset, imageSize, imageSize, this);
					    // Draw Theseus
					    gr.drawImage(theseusImg, ty*tileSize+offset, tx*tileSize+offset, imageSize, imageSize, this);
 	            	}
 	            	
            		for(int i = 0; i < n*n; i++) {
            			int supId = -1;	//the id of supply that is located on the specific tile
            			for(int k = 0; k < g.board.getS(); k++) {
        					if(g.board.supplies[k].getSupplyTileId() == i) supId = k;
        				}
            			
            			int theseusTile = g.p[g.theseusType].getPlayerTile();	//theseus tile
            			int minotaurTile = g.p[3].getPlayerTile();	//minotaur tile
            			
            			//call from play button for theseus
            			if(( theseusTile == i) && paintcode[0] == 2) {	
            				if((theseusTile == i) && (minotaurTile == i) ) {
            					if(g.board.countSupplies() == 0) {	//Theseus has won
            						tx = g.board.getN() - 1 - (theseusTile/g.board.getN());
                    				ty = theseusTile % g.board.getN();
					    			gr.drawImage(theseusImg, ty*tileSize+offset, tx*tileSize+offset, imageSize, imageSize, this);
								}
            					else {	//Draw only minotaur
									gr.drawImage(minotaurImg, my*tileSize+offset, mx*tileSize+offset, imageSize, imageSize, this);
            					}
            				}
            				else {	//Draw both players
            					tx = g.board.getN() - 1 - (theseusTile/g.board.getN());
                				ty = theseusTile % g.board.getN();
					    		gr.drawImage(theseusImg, ty*tileSize+offset, tx*tileSize+offset, imageSize, imageSize, this);
								//Minotaur
								gr.drawImage(minotaurImg, my*tileSize+offset, mx*tileSize+offset, imageSize, imageSize, this);							
            				}
            				
            			}
            			//call from play button for minotaur
            			if((minotaurTile == i) && paintcode[0] == 3) {
            				if((theseusTile == i) && (minotaurTile == i)) {	//Minotaur won, draw only him
            					mx = g.board.getN() - 1 - (minotaurTile/g.board.getN());
                				my = minotaurTile % g.board.getN();
								gr.drawImage(minotaurImg, my*tileSize+offset, mx*tileSize+offset, imageSize, imageSize, this);
            				}
            				else if(supId == -1) {
            					// Minotaur
								mx = g.board.getN() - 1 - (minotaurTile/g.board.getN());
                				my = minotaurTile % g.board.getN();
								gr.drawImage(minotaurImg, my*tileSize+offset, mx*tileSize+offset, imageSize, imageSize, this);
            					//Theseus
					    		gr.drawImage(theseusImg, ty*tileSize+offset, tx*tileSize+offset, imageSize, imageSize, this);
            				}
            				else if(supId != -1){
								gr.drawImage(supplyImg, tileSize*(i % n)+offset, tileSize*(n - 1 -(i / n))+offset,imageSize, imageSize, this);
								mx = g.board.getN() - 1 - (minotaurTile/g.board.getN());
                				my = minotaurTile % g.board.getN();
								gr.drawImage(minotaurImg, my*tileSize+offset, mx*tileSize+offset, imageSize, imageSize, this);
					    		gr.drawImage(theseusImg, ty*tileSize+offset, tx*tileSize+offset, imageSize, imageSize, this);
            				}
            			}
            			if(supId != -1){
							gr.drawImage(supplyImg, tileSize*(i % n)+offset, tileSize*(n - 1 -(i / n))+offset,imageSize, imageSize, this);
            				// If the Minotaur shares the tile with a Supply 
							// => display the Minotaur on top of the supply
							if(minotaurTile == g.board.supplies[supId].getSupplyTileId()){
								gr.drawImage(minotaurImg, my*tileSize+offset, mx*tileSize+offset, imageSize, imageSize, this);
							}
            			}
            			//The walls are printed in every round
            			if(g.board.getTile(i).getUp()) {
    						paintcode[1] = n - 1 -(i / n); //the row count starts from top now not from bottom as before
    						paintcode[2] = i % n;	//the column number is the same
    						//paint an up wall
    			    		gr.setColor(Color.BLACK);
    			    		int k = paintcode[1];
    			    		int j = paintcode[2];
    			    		
    			    		gr.drawLine(j*tileSize, k*tileSize, (j+1)*tileSize, k*tileSize);
    					
    					}
    					if(g.board.getTile(i).getRight()) {
    						paintcode[1] = n - 1 -(i / n); //the row count starts from top now not from bottom as before
    						paintcode[2] = i % n;	//the column number is the same
    						gr.setColor(Color.BLACK);
    			    		//paint a right wall
    			    		int k = paintcode[1];
    			    		int j = paintcode[2];
    			    		gr.drawLine((j+1)*tileSize, k*tileSize, (j+1)*tileSize, (k+1)*tileSize);
    					}
    					if(g.board.getTile(i).getDown()) {    					
    						paintcode[1] = n - 1 -(i / n); //the row count starts from top now not from bottom as before
    						paintcode[2] = i % n;	//the column number is the same
    						gr.setColor(Color.BLACK);
    			    		//paint a down wall
    			    		int k = paintcode[1];
    			    		int j = paintcode[2];
    			    		gr.drawLine(j*tileSize, (k+1)*tileSize, (j+1)*tileSize, (k+1)*tileSize);
    					}
    					if(g.board.getTile(i).getLeft()) {
    						paintcode[1] = n - 1 -(i / n); //the row count starts from top now not from bottom as before
    						paintcode[2] = i % n;	//the column number is the same
    						gr.setColor(Color.BLACK);
    			    		//paint a left wall
    			    		int k = paintcode[1];
    			    		int j = paintcode[2];
    			    		gr.drawLine(j*tileSize, k*tileSize, j*tileSize, (k+1)*tileSize);
    					}
    					
    				}	
            	}
                //now whenever the game is on we will be seeing the maze without any walls inside
                //when the generate board button is pressed we must see the new maze 
                //with walls and the players in their starting positions
            }
        };
		//make a panel
        panel.setBounds(170, 50, jpanelSize+1, jpanelSize+1);
        
        // panel.setBorder(null);	//set it to null so we can work from left to right
        // panel.setLayout(null);	//similar initialization so as to have the layout manager work as needed
        frame.setTitle("Theseus and Minotaur");	//the Title of the Window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.add(panel);
        
        
        roundLabel = new JLabel("Round: 0");
        roundLabel.setBounds(10, 20, 100, 40);
        roundLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        
        
        frame.add(roundLabel);
        
        theseus = new JLabel("Theseus");
        theseus.setForeground(Color.BLUE);
        theseus.setBounds(30, 500+70, 80, 25);
        theseus.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        
        moveT =  new JLabel("Move Score: 0");
        moveT.setForeground(Color.BLUE);
        moveT.setBounds(10, 500+100, 150, 25);
        moveT.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        
        scoreT =  new JLabel("Total Score: 0");
        scoreT.setForeground(Color.BLUE);
        scoreT.setBounds(10, 500+120, 150, 25);
        scoreT.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        
 
        mino = new JLabel("Minotaur");
        mino.setBounds(600+180, 500+70, 150, 25);
        mino.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        
        moveM =  new JLabel("Move Score: 0");
        moveM.setBounds(600+160, 500+100, 150, 25);
        moveM.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        
        scoreM =  new JLabel("Total Score: 0");
        scoreM.setBounds(600+160, 500+120, 150, 25);
        scoreM.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        
        start = new JLabel("To begin press New Game!");
        start.setBounds(250, 20,300,25);
        start.setForeground(Color.RED);
        start.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        
		//JLabel with game over message
		gameOver = new JLabel("");
		gameOver.setBounds(430, 20,300,25);
		gameOver.setForeground(Color.RED);
		gameOver.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));

        //add all the labels that were created above to the panel
        frame.add(theseus);	
        frame.add(mino);
        frame.add(moveT);
        frame.add(moveM);
        frame.add(scoreT);
        frame.add(scoreM);
        frame.add(start);
		frame.add(gameOver);
        
        
        addPlayerType(1,g);	//add a comboBox for Theseus's Player Type
        addPlayerType(2,g);	//add a comboBox for Minotaur
        
        
        //Here add 3 buttons
        
        //First make the layout correct and after finishing add the function for each button
        
        // 1) Generate Board		
        // 2) Play					
        // 3) Quit 					
        
		int h_buttons = 25;			// height of the buttons
		int y_buttons = 600;		// vertical displacement of all buttons from the top of the frame
		int w_genB = 100;			// width of the New Game button
		int w_play = 60;			// width of the Play button
		int w_quit = 60;			// width of the Quit button
		int x_genB = 330;			// horizontal displacement of the genB button from the left of the frame
		int x_play = x_genB + w_genB + 5; // horizontal displacement of the Play button from the left of the frame
		int x_quit = x_play + w_play + 5; // horizontal displacement of the Quit button from the left of the frame
        
        //Generate Board Button
        JButton genB = new JButton("New Game");
        genB.setBounds(x_genB, y_buttons, 100, h_buttons);
        genB.addActionListener(new ActionListener(){  
		    public void actionPerformed(ActionEvent e){ 
		    	gameOver.setText("");
		    	start.setText("");
		    	g.setRound(0);
		    	roundLabel.setText("Round: 0");
		    	moveT.setText("Move Score: 0");
		    	scoreT.setText("Total Score: 0");
		    	moveM.setText("Move Score: 0");
		    	scoreM.setText("Total Score: 0");
		    	g.theseusType = g.currentTheseusType;	//whenever a new board is created the playerType can change
		    	//length/width of board
				int n = 15;
				//number of supplies
				int s = 4;
				//number of walls
				int w = 199;
		    	//create a new board
				g.board = null;	
				g.board = new Board(n, s, w);
				g.board.createBoard();
				
				//create 2 players
				g.p = new Player[4];
				
				//in the first 3 indexes we store Theseus possible playerType
				g.p[0] = new Player(1, "Theseus", g.board, 0, 0, 0);	
				g.p[1] = new HeuristicPlayer(1, "Theseus", g.board, 0, 0, 0, 0);
				g.p[2] = new MinMaxPlayer(1, "Theseus", g.board, 0, 0, 0, 0);
				
				//Here we store Minotaur
				g.p[3] = new Player(2, "Minotaur", g.board, 0, (n / 2), (n / 2));
				
				//here we make the board appear in the gui app
				paintcode[0] = 1;
				paintAgain();
				flag = true;
				
	    }});
        frame.add(genB);
        
        
        //Play Button
        JButton play = new JButton("Play");
        play.setBounds(x_play, y_buttons, w_play, h_buttons);
		play.addActionListener(new ActionListener(){  
		
		    public void actionPerformed(ActionEvent e){  
		    	flag = false;
		    	int[] arr = g.setTurns(g);
		    	if(arr[0] == -1) {	//has the game ended?
		    		System.out.println("Game Over!");
		    		//JLabel with game over message
					gameOver.setText("Game Over!");
		    	}
		    	else if(g.getRound() != 0){
		    		
		    		if(g.playsFirst == 1 && (g.getRound() % 2) == 1) {
		    			paintcode[0] = 2;	//call from play for Theseus
		    		}
		    		else if(g.playsFirst == 1 && (g.getRound() % 2) == 0) {
		    			paintcode[0] = 3;	//call from play for Minotaur
		    		}
		    		else if((g.getRound() % 2) == 1) {	
		    			paintcode[0] = 3;	//call from play for Minotaur
		    		}
		    		else if((g.getRound() % 2) == 0) {
		    			paintcode[0] = 2;	//call from play for Theseus
		    		}
					paintcode[1] = g.board.getN() - 1 - arr[2];
					paintcode[2] = arr[3];
					
					panel = new JPanel();
					panel.repaint();
					frame.remove(panel);
		    		frame.add(panel);
					panel.setSize(500,500);
					
					// System.out.println(paintcode[1]+" "+paintcode[2]);
					
		    		roundLabel.setText("Round: " + g.getRound());
		    		if(g.playsFirst == 1) {	//if Theseus goes first
		    			if((g.getRound()) % 2 == 1) { //if Theseus has just played
		    				if(arr[1] >= 0) {
		    					moveT.setText("Move Score: 1");//Theseus collected a supply
		    					
		    				}
		    				else {
		    					moveT.setText("Move Score: 0");
		    				}
	        			   
		    				scoreT.setText("Total Score: " + g.p[g.theseusType].getScore());
		    			}
		    			else {//if we are in an even round Mino just played
	        			   
		    			}
		    		}
		    		else {//Minotaur moves first
		    			if((g.getRound()) % 2 == 0) { 
		    				//if we are in an even round Theseus just played
		    				if(arr[1] >= 0) {
		    					moveT.setText("Move Score: 1");//Theseus collected a supply
		    				}
		    				else {
		    					moveT.setText("Move Score: 0");
		    				}
		    				//Danger if the user changes theseusType
		    				scoreT.setText("Total Score: " + g.p[g.theseusType].getScore());
		    			}
		    			else {
		    				
		    			}
		    		}
		    	}
		    }});
		
		frame.add(play);
		
		
		//Quit button
        JButton quit = new JButton("Quit");
        quit.setBounds(x_quit, y_buttons, w_quit, h_buttons);
        quit.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
             System.out.println("User hit the Quit Button");
             System.exit(0);
        }});
        frame.add(quit);
        frame.add(new JLabel(""));
      
        
        
        
        frame.setVisible(true);
    }
    
    public void paintAgain() {
    	
    	panel = new JPanel();
    	panel.repaint();
    	if(!flag) {
    		frame.remove(panel);
    		frame.add(panel);
        	panel.setSize(500,500);
    	}
    	else {			
    		frame.setVisible(false);
    		frame.setVisible(true);
    	}

    }
    
    public void addPlayerType(int id, Game g) {
    	PlayerType p = new PlayerType(id, g);
    	frame.add(p.cmb);
    	if(id == 1) {	//Theseus's PlayerType must be aligned with his score and Name(so has the same x distance)
    		p.cmb.setBounds(0, 300+170, 150, 40);
    		p.cmb.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
    	}
    	else if(id == 2) {	//Similarly for Minotaur we align his PlayerType with his information seen in the applcation window
    		p.cmb.setBounds(570+160, 300+170, 150, 40);
    		p.cmb.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
    	}
    	else {
    		System.out.println("Wrong value");
    		System.exit(id);
    	}
    }
    

    
}