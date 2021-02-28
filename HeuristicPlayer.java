import java.util.ArrayList;


public class HeuristicPlayer extends Player {
	
	private ArrayList<ArrayList<Integer>> path;
	

	public HeuristicPlayer() {
		super();
		path = new ArrayList<ArrayList<Integer>>(0);	//initial size = 0
	}
	
	public HeuristicPlayer(int playerId, String name, Board board, int score, int x, int y, int size) {
		super(playerId, name, board, score, x, y);
		path = new ArrayList<ArrayList<Integer>>(size);	//We give as a parameter the initial size of the ArrayList
	}

	public HeuristicPlayer(HeuristicPlayer hero) {
		super(hero.getPlayerId(), hero.getName(), hero.getBoard(), hero.getScore(), hero.getx(), hero.gety());
		//Attention: this is not a copy initialization, the HeuristicPlayer we are creating will control the same path as the hero
		this.path = hero.getPath();	
	}
	
	public void setPath(ArrayList<Integer> moveInfo) {
		path.add(moveInfo);
	}
	
	//this returns the full path
	public ArrayList<ArrayList<Integer>> getPath(){
		return path;
	}
	//we only get information about a single move
	public ArrayList<Integer> getPath(int index){
		if(index >= path.size()) {
			System.out.println("Wrong index in getPath");
			System.exit(1);
		}
		return path.get(index);
	}
	
	public double evaluate(int currentPos, int dice) {
		//We must check in the direction pointed by dice
		/*
		 1)If there is a wall next to us we return the smallest value(we defined) 
		 	so as to completely avoid making an illegal move (we could return Double.NEGATIVE_INFINITY)
		   If there is a wall in the our path that is visible we also take proper care.
		 
		 2)If  Minotaur and LAST supply are on the same tile
		  	go for the win
		 3)else if Minotaur and supply are on the same tile
		 	make Theseus run for his life
		 
		 4)only Mino
		 
		 5)only supply
		 
		 We may also have an empty tile
		*/
		
		// i is the iterator of the do-while loop and indicates the distance Theseus has moved in the direction of dice
		int i = 0;
		
		//we store all the info needed from cases 1 to 5 regarding all the tiles we can see from our spot
		//rows means distance in tiles (we can see up to 3 tiles in one direction)
		//columns hold flags for the 5 different cases we examine 
		boolean[][] cases = new boolean[3][5];
		for(int j = 0; j < 3; j++) {
			for(int k = 0; k < 5; k++) {
				cases[j][k] = false;
			}
		}
		do {
			
			
			//1st case if we find a wall in our vision scope
			switch(dice) {
				case 1: if(board.getTile(currentPos).getUp()) {
							cases[i][0] = true;
							if(i == 0) {	
								return -100000;
							}
						}
				break;
				case 3: if(board.getTile(currentPos).getRight()){
							cases[i][0] = true;
							if(i == 0) {
								return -100000;
							}
						}
				break;
				case 5: if((board.getTile(currentPos).getDown() || currentPos == 0)){
							cases[i][0] = true;
							if(i == 0) {
								return -100000;
							}
						}
				break;
				case 7: if(board.getTile(currentPos).getLeft()){
							cases[i][0] = true;
							if(i == 0) {
								return -100000;
							}
						}
				break;
				default: 
					System.out.println("Error in evaluate. Unknown dice value!");
					System.exit(1);
			}
			//Stop the loop if you find a Wall
			if(cases[i][0]) {
				break;
			}
			int closeId = closeTileId(currentPos, dice);
			
			
			//2nd case Minotaur and last supply
				
				// playerId % 2 + 1 gives the opponent's id
				if((board.getPlayerTile(playerId % 2 + 1) == closeId) && 	
					(board.searchSupply(closeId) != -1) &&
					(board.countSupplies() == 1)) {
					cases[i][1] = true;
					if(i == 0) {
						return 10000.0;	//go for the win
					}
				}
				
			//3rd case: if Minotaur and supply are on the same tile
			 	//make Theseus run for his life
				else if((board.getPlayerTile(playerId % 2 + 1) == closeId) && 	
						(board.searchSupply(closeId) != -1)){
					cases[i][2] = true;
					if(i == 0)
						return -100.0;	//run for your life
				}
			//4th only Minotaur
				else if((board.getPlayerTile(playerId % 2 + 1) == closeId)) {
					cases[i][3] = true;
					if(i != 2) {
						return -1000.0;
					}
						
				}
			//5th only Supply
				else if((board.searchSupply(closeId) != -1)) {
					cases[i][4] = true;
				}
			//update virtual position
			currentPos = closeId;
			
			
			i++;
			//Stop if you check through all your visible area in the specified direction
		}while((i < 3));
		
		//What is the overall evaluation of the move
		double eval = 0;	//hold the result of the final evaluation
		double NearSupplies = 0;	//the coefficient that shows the existence of supplies
		double NearOpponent = 0;	//the coefficient that shows the presence of Minotaur near us
		//do the calculations
		for(int k = 0; k < 3; k++) {
			if(cases[k][0] == true) {
				break;
			}
			if(cases[k][1] == true) {
				//if k = 0 i.e. the tile with Minotaur and the last supply 
				//is next to Theseus, we have already handled this case in the do-while loop
				//now it is dangerous to play , so we prefer to do another move if possible
				eval = -5;//
				return eval;
			}
			if(cases[k][2] == true) {
				eval = -6; 	// run for your life, Mino steps on the same tile with a supply
				return eval;
			}
			if(cases[k][3] == true) {
				NearOpponent = NearOpponent - measureStep(k);
			}
			else if(cases[k][4] == true) {
				NearSupplies = NearSupplies + measureStep(k);
			}
		}
		eval = NearSupplies*0.46 + NearOpponent*0.54;
		return eval;
		
	}
	/**
	 * 
	 * @param currentPos the initial tileId of the player before moving in this round
	 * 
	 * @return the tileId of the next move of the player
	 * In the variable path we save an ArrayList<Integer> that contains
	 * @ ind = 0  the direction of the move i.e. {up,right,down,left} encoded TOGETHER with the tileId of the new move
	 * @ ind = 1  if the player collected a supply or not
	 * @ ind = 2  the supplyId of the supply (if it was collected) or -1 (nothing was collected)
	 * @ ind = 3  the distance from the opponent measured in number of tiles or -1(opponent not visible)
	 * @ ind = 4,...,size-1 possibly(may not exist) the distance from nearby supplies TOGETHER with the direction
	 */
	public int getNextMove(int currentPos) {
		//We make the assumption that the player can move in any tile that is located up down left or right
		//In the above function(evaluate) we check if there is a wall to any of those directions
		// and we take care of the case in which the player would go out of bounds
		//So for a given tileId we evaluate 4 moves
		ArrayList<Double> moveEvaluation = new ArrayList<Double>(0);
		for (int i = 0; i < 4; i++) {
			// 2*i + 1 , where i iterates in {0,1,2,3} so 2*i+1 iterates in {1,3,5,7} i.e. all possible dice values
			double temp = evaluate(currentPos, 2*i+1);
			moveEvaluation.add(temp);
			
		}
		
		double maxPts = moveEvaluation.get(0); //max algorithm
		int bestMove = 1;
		for(int i = 1; i < 4; i++) {
			if(moveEvaluation.get(i) > maxPts) {
				maxPts = moveEvaluation.get(i);
				bestMove = 2*i+1;
			}
		}
		//if more than one move has this maxPts evaluation we store them in an ArrayList
		ArrayList<Integer> equalMoves = new ArrayList<Integer>(0);
		for(int i = 0; i < 4; i++) {
			if(moveEvaluation.get(i) == maxPts) {
				equalMoves.add(2*i+1);	
			}
		}
		//If we have equally evaluated moves we want to explore new Tiles
		if(equalMoves.size() != 1) {
			//with this function we want to move in a tile that we have not visited yet or we have visited less times
			bestMove = exploreMore(currentPos, equalMoves);
		}
		
		int newPos = closeTileId(currentPos, bestMove);	//the tileId of the newPosition
		if(newPos < 0 || newPos>=board.getN()*board.getN()) {
			System.out.println("Wrong Move");
			System.exit(1);
		}
		
		ArrayList<Integer> moveInfo = new ArrayList<Integer>(0);
		
		moveInfo.add((bestMove+newPos)*prime(bestMove));	//save the dice together with the tileId
		int collectedSupply = 0;
		if(board.searchSupply(newPos) != -1){
			collectedSupply = 1;
		}
		moveInfo.add(collectedSupply);	//save 1 if he collected a supply or 0 otherwise 
		//we also add the supplyId to the matrix we create or -1 if no supplies are collected
		int temp = -1;
		for(int i  = 0; i < board.getS(); i++) {
			if(board.getSupply(i).getSupplyTileId() == newPos) {
				temp = i;
				board.getSupply(i).setSupplyTileId(-1);
				
			}
		}
		moveInfo.add(temp);
		
		
		moveInfo.add(opponentDist(newPos));	//save the distance from the opponent
		
		ArrayList<Integer> supD = supplyDist(newPos);
		if(supD.size() > 0) {
			for(int i = 0; i < supD.size(); i++) {
				moveInfo.add(supD.get(i));	//save the distance from a supply (or supplies) in the newPos in an encoded form
			}
		}
		
		if(temp!=-1){
			board.getSupply(temp).setSupplyTileId(newPos);	//we let move to delete the supply from the board
		}

		setPath(moveInfo);
		return newPos;
	}
	
	/**
	 * 
	 * @param flag is false if called for printing the statistics of a round
	 * 			   is true if called for printing the statistics of the game in the end
	 * 
	 */
	public void statistics(boolean flag) {
		

		for(int i = 0; i < path.size(); i++){
			//if flag = false we want to print the info of the last round
			if(!flag){
				i = path.size() -1;
			}
			System.out.print("\n");
			//the round of the game is given by 2*i+1
			if(flag && i == (path.size() - 1)) {
				System.out.print(getName() + " in final round");
			}
			else{
				System.out.print(getName() + " in round: "+ (2*i+1));
			}

			//
			//in coded form the tileId and the dice of the round
			int coded = getPath(i).get(0);
			//the dice of the round
			int dice1 = (decode(coded))[1];
			//the tileId of the round
			int tileId = (decode(coded))[0];
			System.out.print(" ,has set the dice to: "+dice1);
			if(getPath(i).get(1) == 1) {
				int supId = getPath(i).get(2);
				System.out.print(" and collected supply S"+(supId + 1));
			}
			System.out.print(".\nHe is located at (x,y) = ("+tileId/board.getN()+","+tileId%board.getN()+").\n");

			//inform about score
			if(roundScore(i+1) == 1) {
				System.out.println("Theseus has collected : "+roundScore(i+1)+" supply until now!");
			}
			else {
				System.out.println("Theseus has collected : "+roundScore(i+1)+" supplies until now!");
			}
			
			//check the distance from Minotaur 
			//get the i-th ArrayList<Integer> contained in the path. And from this arraylist take the element @ ind = 3
			if(getPath(i).get(3) != -1) {
				System.out.println("Theseus is : "+getPath(i).get(3)+" tiles far from the Minotaur!");
			}
			else
				System.out.println("Theseus cannot see Minotaur from his position.");

			//check for the nearest supply and its relative distance to the player's new location
			if(getPath(i).size() > 4) {
				int encoded = getPath(i).get(4);
				int dice = (decode(encoded))[1];
				int tileDist = (decode(encoded))[0];
				switch(dice) {
				case 1: System.out.println("Theseus can see a supply "+tileDist+" tile(s) up from his position."); break;
				case 3: System.out.println("Theseus can see a supply "+tileDist+" tile(s) right from his position."); break;
				case 5: System.out.println("Theseus can see a supply "+tileDist+" tile(s) down from his position."); break;
				case 7: System.out.println("Theseus can see a supply "+tileDist+" tile(s) far from his position."); break;
				}
			}
			else{
				System.out.println("Theseus cannot see any supplies from his position.");
			}
		}
		
		//if we are in the end of the game, we also print the final statistics
		if(flag) {
			System.out.println("\nFinal Statistics:");
			//gather the required information from each move and
			int countUp = 0;
			int countRight = 0;
			int countDown = 0;
			int countLeft = 0;
			for(int i = 0; i < path.size(); i++) {
				int direction = decode(path.get(i).get(0))[1];
				switch(direction) {
					case 1: countUp++;    break;
					case 3: countRight++; break;
					case 5: countDown++;  break;
					case 7: countLeft++;  break;
					default:
						System.out.println("Error in statistics!");
						System.exit(1);
				}
			}
			if(countUp != 1) {
				System.out.println(getName()+" decided to move up(1) " + countUp + " times.");
			}
			else {
				System.out.println(getName()+" decided to move up(1) " +  "once.");
			} 
			if(countRight != 1) {
				System.out.println(getName()+" decided to move right(3) " + countRight + " times.");
			}
			else {
				System.out.println(getName()+" decided to move right(3) " + "once.");
			}
			if(countDown != 1) {
				System.out.println(getName()+" decided to move down(5) " + countDown + " times.");
			}
			else {
				System.out.println(getName()+" decided to move down(5) " + "once.");
			}
			if(countLeft != 1) {
				System.out.println(getName()+" decided to move left(7) " + countLeft + " times.");
			}
			else {
				System.out.println(getName()+" decided to move left(7) " + "once.");
			}
		}
	}
	
	//extra methods
	
	
	/**
	 * *
	 * @param currentPos the tileId of the Player's hypothetical or actual spot
	 * @param dice the direction {up,right,down,left} <-> {1,3,5,7} we want to check
	 * @return the tileId of the neighboring tile (specified by dice). If the neighboring tile is not valid -1 is returned 
	 */
	
	public int closeTileId(int currentPos, int dice) {
		int N = board.getN();
		int x = currentPos / N;
		int y = currentPos % N;
		
		switch(dice) {
		case 1: if(board.getTile(currentPos).closeIsValid(x+1, N)){ 
					return (x+1)*N + y;
		}
		break;
		case 3: if(board.getTile(currentPos).closeIsValid(y+1, N)){ 
					return x*N + y+1;
				}	
		break;
		case 5: if(board.getTile(currentPos).closeIsValid(x-1, N)){
					return (x-1)*N + y;
				}
		break;
		case 7:	if(board.getTile(currentPos).closeIsValid(y-1, N)){
					return x*N + y-1;
				} 
		break;
		default:
			System.out.println("Error in closeTileId. Unknown dice value!");
			System.exit(1);
			return -10000;
		}
		return -1;
	}
	
	/**
	 * 
	 * @param k = distance - 1 where distance refers to the number of tiles that "line up" (I can see 3 at max)
	 * next to the tile that the player is located in one of the 4 directions as presented in the task 
	 * @return part of the cost of the path if the player had to take the particular step 
	 */
	
	public double measureStep(int k) {
		switch(k) {
			case 0: return 1;	
			case 1: return 0.5;	
			case 2: return 0.3;	
			default: 
				System.out.println("Unknown Value in measureStep");
				System.exit(1);
				return Double.NEGATIVE_INFINITY;
		}
	}
	
	/**
	 * Depth First Search Algorithm
	 * @return the distance in number of tiles of the opponent, given that he is visible
	 * If the opponent is not visible in our vision scope (the cross) we return -1
	 * else the distance in tiles is returned
	 */
	public int opponentDist(int currentPos) {
		int dist = -1;
		int closeId;
		
		
		//keep track of the steps you are taking in every direction by saving the currentPosition of the player in each step
		int initialPos =  currentPos;
		
		//j = d - 1 shows where d is the distance in tiles from the initialPos
		for(int i = 0; i < 4; i++) {
            int dice = 2*i+1;
            currentPos = initialPos;	//after checking 3 steps in the direction indicated by dice,
            //start checking step triplets in other directions
			for(int j = 0; j < 3; j++) {
				
				boolean flag = false;
				//if you stumble in a wall continue searching in another direction
				switch(dice) {
					case 1: if(board.getTile(currentPos).getUp()) {
						flag = true;
					}
					break;
					case 3: if(board.getTile(currentPos).getRight()) {
						flag = true;
					}
					break;
					case 5: if((board.getTile(currentPos).getDown() || currentPos == 0)) {
						flag = true;
					}
		        	break;
					case 7: if(board.getTile(currentPos).getLeft()) {
						flag = true;
					}
					break;
					default: 
			        	System.out.println("Error in opponentDist. Unknown dice value!");
						System.exit(1);
				}
				if(flag) break;
				closeId = closeTileId(currentPos, dice);
					
				// playerId % 2 + 1 gives the opponent's id
				if((board.getPlayerTile(playerId % 2 + 1) == closeId)) {
					dist = j + 1;
					return dist;
				}
				//update virtual position
				currentPos = closeId;
				
			}
		}
		return dist;
	}
	
	// return all visible supplies from our new position
	/**
	 * Breadth First Search is used in order to save the closest supplies first
	 * @param currentPos the current tileId 
	 * @return a list that contains all distances from supplies.
	 */
	/*
	 * dice:        1(up)             3(right)       5(down)        7(left)
	 *                |                  |              |              |
	 * distance:    1,2,3              1,2,3          1,2,3          1,2,3
	 * 
	 * I will multiply the sum of (location + distance) with a relative (to the output values of the sum) big PRIME Number
	 * so as to encode ,simultaneously, BOTH the information of the distance from the supply AND the direction(up,right,down,left)
	 */
	public ArrayList<Integer> supplyDist(int currentPos) {
		int dist = -1;
		int closeId;
		
		boolean stumbledInWall[] = new boolean[4];	//holds true if we find a wall in the direction 2*i+1 
		for(int i = 0; i < 4; i++) {
			stumbledInWall[i] = false;
		}
		
		//create the structure that will hold our results
		ArrayList<Integer> result = new ArrayList<Integer>(0);
		
		//keep track of the steps you are taking in every direction by saving the currentPosition of the player in each step
		int[] currentP =  new int[4];
		for(int i = 0; i < 4; i++) {
			currentP[i] = currentPos;
		}
		
		//i = d - 1 shows where d is the distance in tiles from the currentPos
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 4; j++) {
				int dice = 2*j+1;
				
				if(stumbledInWall[j] == false) {
					switch(dice) {
						case 1: if(board.getTile(currentP[j]).getUp()) {
									stumbledInWall[0] = true;
									continue;
						}
						break;
						case 3: if(board.getTile(currentP[j]).getRight()){
									stumbledInWall[1] = true;
									continue;
						}
						break;
						case 5: if((board.getTile(currentP[j]).getDown() || currentP[j] == 0)){
									stumbledInWall[2] = true;
									continue;
						}
						break;
						case 7: if(board.getTile(currentP[j]).getLeft()){
									stumbledInWall[3] = true;
									continue;
						}
						break;
						default: 
							System.out.println("Error in supplyDist. Unknown dice value!");
							System.exit(1);
					}
					
					closeId = closeTileId(currentP[j], dice);
					
					
					if((board.searchSupply(closeId) != -1)) {
						//we encode the distance = i+1 WITH the direction-dice as described in the beginning of the function
						dist = ((i + 1) + dice)*prime(dice);
						result.add(dist);
					}
					//update virtual position in direction 2*j+1
					currentP[j] = closeId;
				}
			}
		}
		return result;
	}
	/**
	 * 
	 * @param dice the direction {up,right,down,left} <-> {1,3,5,7}
	 * @return the prime number that will be used for encoding
	 */
	public int prime(int dice) {
		switch(dice) {
			case 1: return 401; 
			case 3: return 421; 
			case 5: return 431; 
			case 7: return 461; 
		}
		System.out.println("Wrong call prime!");
		System.exit(1);
		return 1;
	}
	/**
	 * 
	 * @param dist the encoded distance using the formula (distance+dice)*prime_number(dice)
	 * @return an Integer[] array containing in the #0 spot the distance in tiles from the supply
	 * 									and in the  #1 spot the dice-direction(up,right,down,left)
	 * 
	 * ALTERNATIVELY this can be used:
	 * @param dist the encoded dice in the form (dice+ TileId of New Position)*prime(dice)
	 * @return an Integer[] array containing in the #0 spot the tileId of the move
	 * 													(The Player only knows the x,y coords so in order for him to 
	 * 													use this info we must first decompose it to x and y)
	 * 									and in the  #1 spot the dice-direction(up,right,down,left) of the move(did he move up,right,etc)
	 */		
	public Integer[] decode(int dist) {
		Integer[] result = new Integer[2];
		if(dist%401 == 0) {
			dist = dist/401;
			dist = dist - 1;	//subtract the dice
			result[0] = dist;
			result[1] = 1;	//dice(up)
			return result;
		}
		else if(dist%421 == 0) {
			dist = dist/421;
			dist = dist - 3;	//subtract the dice
			result[0] = dist;
			result[1] = 3;	//dice (right)
			return result;
		}
		else if(dist%431 == 0) {
			dist = dist/431;
			dist = dist - 5;	//subtract the dice
			result[0] = dist;
			result[1] = 5;	//dice (down)
			return result;
		}
		else if(dist%461 == 0) {
			dist = dist/461;
			dist = dist - 7;	//subtract the dice
			result[0] = dist;
			result[1] = 7;	//dice (left)
			return result;
		}
		System.out.println("Cannot decode this number!");
		System.exit(1);
		return result;	//This would give an error. We just persuade the compiler that we return something
	}
	
	/**
	 * 
	 * does not belong in the explored set ideally 
	 * or
	 * has been visited the least times
	 * @param currentPos the current position of the player
	 * @param equalMoves an array with dices that have the same evaluation result
	 * @return the bestMove as a dice
	 */
	public int exploreMore(int currentPos, ArrayList<Integer> equalMoves) {
		//store the number of times each one of the equalMoves has been repeated
		ArrayList<Integer> movesRepetition = new ArrayList<Integer>(0);
		//loop over equalMoves,  find the number of times each move has been repeated
		for(int i = 0; i < equalMoves.size(); i++) {
			int closeId = closeTileId(currentPos, equalMoves.get(i));
			//if closeId = -1 it means we cannot move. That cannot happen because we have already evaluated each move
			movesRepetition.add(timesVisited(closeId));
			
		}
		//Now we want to find the least visited tile.
		//If 2 tiles are visited the same number of times we choose randomly
		int min = movesRepetition.get(0);
		ArrayList<Integer> minId = new ArrayList<Integer>(0);
		minId.add(0);
		for (int i = 1; i < movesRepetition.size(); i++) {
			if(min > movesRepetition.get(i)) {
				min = movesRepetition.get(i);
				minId.clear();	//clear the list of indexes that corresponded to the previously least visited tile 
				minId.add(i);
			}
			else if(min == movesRepetition.get(i)) {
				minId.add(i);	//save the index of tiles that have been visited the same number of times 
			}
		}
		int randomId = (int) (Math.random()*100);
		int size = minId.size();
		randomId = randomId % size;
		int bestMove = equalMoves.get(minId.get(randomId));	// belongs in {1,3,5,7} <-> {up,right,down,left}
		
		return bestMove;	
	}
	
	/**
	 * @param tileId the tileId of the tile we are checking
	 * @return the times that a tile has already been visited
	 */
	public int timesVisited(int tileId) {
		
		int count = 0;
		
		for(int i = 0; i < path.size(); i++) {
			//encoded form of the tileId of the newPos
			int tileId_dice = path.get(i).get(0);
			int newPos = decode(tileId_dice)[0];
			if(tileId == newPos) 
				count++;
		}
		//The initial Position is not stored in the path
		if(tileId == 0){
			count++;
		}
		return count;
	}

	/**
	 * We want to get hold of the score in every round after the game has ended
	 * In order to achieve this we will use the info stored in path and get the element with index = 1
	 * from each ArrayList<Integer> that describes each move.
	 * @param n the n-th dice of Theseus
	 * @return the score at the round corresponding to the n-th dice of Theseus
	 */
	public int roundScore(int n){
		int score = 0;
		for(int i = 0; i < n; i++){
			if(path.get(i).get(1) == 1){
				score++;
			}
		}
		return score;
	}
}
