/* 
 * This file contains the main function.
 * To compile use:	> javac *.java
 * 					> java Game
*/

public class Game {
	int winner = 0; //if winner = 0, draw, if winner = 1, winner is Theseus, if winner = 2, winner is Minotaur
	Board board;
	Player[] p;
	int playsFirst;
	int round;
	int theseusType;	//gets values 0 1 2 which correspond to Random, 
	//Heuristic and MinMax Player
	//Minotaur is always a Random Player so we don't have a similar variable
	int currentTheseusType;	//if the user changes the type of player during the game we store the new result
	//so that in the next game if they choose to play the new value of theseusType can be updated
	
	public Game()
	{
		round = 0;	//no one has played in the beginning of the Game
		currentTheseusType = theseusType = 0;
		board = null;
		p = null;
				
	}

	public Game(int round)
	{
		this.round = round;
	}

	public Game(Game game)
	{
		round = game.getRound();
	}

	public void setRound(int round)
	{
		this.round = round;
	}

	public int getRound()
	{
		return round;
	}

	/**	A quick intro of our project
		A night in the musuem
		with credits
	*/
	public void credits(){
			//This loop prints a series of messages
			//and pauses for a small period of time according to the index i
		for (int i = 0; i < 16; i++) {
		    if(i == 0){
		    	System.out.println("Welcome to the event...");

		    }
		    else if(i == 1)
		    	System.out.println("\"A night in the musuem\"\n");
		    else if(i == 2){
		        System.out.println("Enjoy the minigame...");
		    }
		    else if(i == 3){
		        System.out.println("\"Theseus and the Minotaur\"\n");
		    }
		    else if(i == 5){
		    	System.out.println("The content creators:");
		    }
		    else if(i == 6){
		      System.out.println("Ioannis Kallimanis");
		      System.out.println("Christos Melissaris");
		    }
		    try {
		        // thread to sleep for 1000 milliseconds
		    	if(i == 2 || i == 3)
		          Thread.sleep(1000);
		        else if(i == 0 || i == 1)
		          Thread.sleep(1000);
		        else if(i == 4)
		          Thread.sleep(1000);
		        else{
		          Thread.sleep(200);
		          System.out.println();
		        }
		    }
		    catch (Exception e) {
		        System.out.println(e);
		    }
		}
	}
	/**
	 @return an integer in {0,1,2}
	2 means Minotaurus won
	1 means Theseus won
	0 means nobody won
	*/
	public int checkGameOver(int S, Player p1, Player p2){
		int p1Tile = p1.getPlayerTile();
		int p2Tile = p2.getPlayerTile();
		if(p1.getScore() == S || p2.getScore() == S){
		return 1; //  Theseus has won
		}
		if(p1Tile == p2Tile){
		return 2;  //Minotaurus has won
		}
		return 0; //Draw
	}

	public static void printEndScreen(){
		System.out.print("\n");
		int h = 6;
		int n = 2*h;	//n is even && n>=2
		int width = 6 + n;
		for(int k = 0; k < h; k++){
			for(int i = 0; i < width; i++){           // *     * 
				if(i == k || i == (width - k - 1))
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.print("\n");
		}
		for(int i = 0; i < width; i++){            
			if(i == h || i == (h+5))
				System.out.print("*");
			else if(i > h && i < (h+5)){
				System.out.print("-");
			}
			else
				System.out.print(" ");
		}
		System.out.print("\n");
		for(int i = 0; i < width; i++){            
			if(i == h || i == h+5)
				System.out.print("|");
			else if(i == (h+1)){
				System.out.print("G");
			}
			else if(i == (h+2)){
				System.out.print("A");
			}
			else if(i == (h+3)){
				System.out.print("M");
			}
			else if(i == (h+4)){
				System.out.print("E");
			}
			else
				System.out.print(" ");
		}
		System.out.print("\n");
		for(int i = 0; i < width; i++){            
			if(i == h || i == (h+5))
				System.out.print("|");
			else if(i == (h+1)){
				System.out.print("O");
			}
			else if(i == (h+2)){
				System.out.print("V");
			}
			else if(i == (h+3)){
				System.out.print("E");
			}
			else if(i == (h+4)){
				System.out.print("R");
			}
			else
				System.out.print(" ");
		}
		System.out.print("\n");
		for(int i = 0; i < width; i++){            
			if(i == h || i == (h+5))
				System.out.print("*");
			else if(i > h && i < (h+5)){
				System.out.print("-");
			}
			else
				System.out.print(" ");
		}
		System.out.print("\n");
		for(int k = h - 1; k >= 0; k--){
			for(int i = 0; i < width; i++){
				if(i == k || i == (width - k - 1))
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	/**
	 * 
	 * @param game the game which is created when we start the application
	 * @return an array which stores a code value in the #0 index to indicate the end of the game
	 				and in the #1 index may store a representative value of the moveScore
	 */
	public int[] setTurns(Game game) {
		if(game.p == null) {
			int[] arr = {-100};
			return arr;
		}
		if(game.winner != 0 || game.getRound()>=200) {
			
			switch(game.theseusType) {
			case 0:
			break;
			case 1:
				System.out.println("Statistics of the Game:");
				
				((HeuristicPlayer) game.p[1]).statistics(true);
			break;
			case 2:
				System.out.println("Statistics of the Game:");
				
				((MinMaxPlayer) game.p[2]).statistics(true);
				break;
			}
			//print the final status who wins or draw
			switch(winner) {
				case 0:	//Draw
					System.out.println("Draw!!");
					break;
				case 1: //Theseus wins
					System.out.println("Theseus is the winner!!!");
					break;
				case 2:	//Minotaur wins
					System.out.println("Minotaur is the winner!!!");
					break;
			}
			System.out.println("Thanks for your participation. See you next time ;)");
			int[] arr = {-1};
			return arr;
		}
		if(game.getRound() == 0) {
		    game.playsFirst  = (int)(Math.random()*101);
		    game.playsFirst = (game.playsFirst % 2) + 1;
		}
		
		
		int theseusTile = game.p[game.theseusType].getBoard().getPlayerTile(game.p[game.theseusType].getPlayerId());
		int minotaurTile = game.p[3].getBoard().getPlayerTile(game.p[3].getPlayerId());
		
		int[] moveInfo = new int[4];
		int newPos = -1;
		
		if(game.playsFirst == game.p[game.theseusType].getPlayerId() ) {
			if((game.getRound()%2 == 0)) {	//Theseus is playing
				switch(game.theseusType) {
				case 0:
					newPos = game.p[0].diceAndJudge(theseusTile);
					moveInfo = game.p[0].move(theseusTile, newPos, game.board);
					System.out.println(game.p[0].getName() + " now is in Tile " + moveInfo[0] + " with " + moveInfo[1] + ", " + moveInfo[2]+".");
					if(moveInfo[3] >= 0)
					System.out.println("He collected supply s"+(moveInfo[3]+1));
				break;
				case 1:
					newPos = ((HeuristicPlayer) game.p[1]).getNextMove(theseusTile);
					moveInfo = ((HeuristicPlayer) game.p[1]).move(theseusTile, newPos, game.board);
					((HeuristicPlayer) game.p[1]).statistics(false);
				break;
				case 2:
					moveInfo = ((MinMaxPlayer) game.p[2]).getNextMove(theseusTile, minotaurTile);
					((MinMaxPlayer) game.p[2]).statistics(false);
				break;
				}
			}
			else {	//Minotaur is playing
				newPos = game.p[3].diceAndJudge(minotaurTile);
				moveInfo = game.p[3].move(minotaurTile, newPos, game.board);
				System.out.println(game.p[3].getName() + " now is in Tile " + moveInfo[0] + " with " + moveInfo[1] + ", " + moveInfo[2]+".");
			}
		}
		else {
			if((game.getRound()%2 != 0)) {	//Theseus is playing 2nd
				switch(game.theseusType) {
				case 0:
					newPos = game.p[0].diceAndJudge(theseusTile);
					moveInfo = game.p[0].move(theseusTile, newPos, game.board);
					System.out.println(game.p[0].getName() + " now is in Tile " + moveInfo[0] + " with " + moveInfo[1] + ", " + moveInfo[2]+".");
					if(moveInfo[3] >= 0)
					System.out.println("He collected supply s"+(moveInfo[3]+1));
				break;
				case 1:
					newPos = ((HeuristicPlayer) game.p[1]).getNextMove(theseusTile);
					moveInfo = ((HeuristicPlayer) game.p[1]).move(theseusTile, newPos, game.board);
					((HeuristicPlayer) game.p[1]).statistics(false);
				break;
				case 2:
					moveInfo = ((MinMaxPlayer) game.p[2]).getNextMove(theseusTile, minotaurTile);
					((MinMaxPlayer) game.p[2]).statistics(false);
				break;
				}
			}
			else {	//Minotaur is playing
				newPos = game.p[3].diceAndJudge(minotaurTile);
				moveInfo = game.p[3].move(minotaurTile, newPos, game.board);
				System.out.println(game.p[3].getName() + " now is in Tile " + moveInfo[0] + " with " + moveInfo[1] + ", " + moveInfo[2]+".");
			}
		}
		
		
		int n = 15;
		theseusTile = game.p[game.theseusType].getBoard().getPlayerTile(game.p[game.theseusType].getPlayerId());
		minotaurTile = game.p[3].getBoard().getPlayerTile(game.p[3].getPlayerId());
		//represent the board
		String[][] array = new String[2 * n + 1][n];
		//get and print initial state
		array = game.board.getStringRepresentation(theseusTile, minotaurTile);
		//note that we have included the change of line in the function getStringRepresentation
		for(int j = (2 * n); j >= 0; j--) {
			for(int k = 0; k < n; k++) {
				System.out.print(array[j][k]);
			}
		}
		
		
		//increment the round of the game by 1 and check for end of the game
		int s = 4; 	// number of supplies
		game.setRound(game.getRound()+1);
		game.winner = game.checkGameOver(s, game.p[game.theseusType], game.p[3]);
		
		//return the move score and add one to the total score if it is not negative
		//in the fist place we will have the code number that will signal the end of the game
		//in the second place we will have a value that shows the move score 
		//in the third place the new x-position
		//the new y-position
		int[] arr = {0, moveInfo[3], moveInfo[1], moveInfo[2]};
		
		return arr;
	}

	public static void main(String[] args) {
		//make a new game
		Game game = new Game();
		
		//make the gui of the game that was just created
		new Gui(game);
		
		//show credits
		game.credits();
		
		//set parameters
		//length/width of board
		int n = 15;
		
		
		//The board is created when pressing the button Generate Board
		
		while(game.p == null) {
			//wait until Board and players are created
		}
		
		int theseusTile = game.p[game.theseusType].getBoard().getPlayerTile(game.p[game.theseusType].getPlayerId());
		int minotaurTile = game.p[3].getBoard().getPlayerTile(game.p[3].getPlayerId());

		//Create array that will hold the String representation of the board
		String[][] array = new String[2 * n + 1][n];
		//get and print initial state
		array = game.board.getStringRepresentation(theseusTile, minotaurTile);
		//note that we have included the change of line in the function getStringRepresentation
		for(int j = (2 * n); j >= 0; j--) {
			for(int k = 0; k < n; k++) {
				System.out.print(array[j][k]);
			}
		}
		
	}
}

