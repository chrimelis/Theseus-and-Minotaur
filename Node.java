import java.util.ArrayList;

public class Node {
	//fields of the class Node
	
	//the parent Node
	Node parent;
	//contains the children of the node in a list
	ArrayList<Node> children;
	//the depth of the node in the MinMax tree
	int nodeDepth;
	
	/* @ind 0 : x coordinate of the corresponding move
	   @ind 1 : y    >>      >>  >>      >>        >>
	   @ind 2: the dice     >>  >>      >>        >>
	*/
	int[] nodeMove;
	
	//the state of the Board of the corresponding move 
	Board nodeBoard;
	
	//the value of the move according to the evaluation function
	double nodeEvaluation;
	
	
	//constructors
	
	public Node() {
		parent = null;
		children = null;
		nodeDepth = -1;
		nodeMove = new int[3];
		nodeMove[0] = -1;		//out of bounds
		nodeMove[1] = -1;		//out of bounds
		nodeMove[2] = -1;		//invalid dice
		nodeBoard = null;
		nodeEvaluation = 0;		//not evaluated so it has a neutral value
		
	}
	public Node(Node parent, ArrayList<Node> children, int nodeDepth, int[] nodeMove, Board nodeBoard,
			double nodeEvaluation) {
		this.parent = parent;
		this.children = children;
		this.nodeDepth = nodeDepth;
		this.nodeMove = nodeMove;
		this.nodeBoard = nodeBoard;
		this.nodeEvaluation = nodeEvaluation;
	}
	
	
	//setters and getters for every variable
	
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public ArrayList<Node> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}
	
	public int getNodeDepth() {
		return nodeDepth;
	}
	public void setNodeDepth(int nodeDepth) {
		this.nodeDepth = nodeDepth;
	}
	public int[] getNodeMove() {
		return nodeMove;
	}
	//extra getter with index of the matrix nodeMove
	public int getNodeMove(int index) {
		if(index < 0 || index > 2) {
			System.out.println("Error, Wrong Index in getNodeMove!");
			System.exit(1);
		}
		return nodeMove[index];
	}
	
	public void setNodeMove(int[] nodeMove) {
		this.nodeMove = nodeMove;
	}
	
	public Board getNodeBoard() {
		return nodeBoard;
	}
	public void setNodeBoard(Board nodeBoard) {
		this.nodeBoard = nodeBoard;
	}
	public double getNodeEvaluation() {
		return nodeEvaluation;
	}
	public void setNodeEvaluation(double nodeEvaluation) {
		this.nodeEvaluation = nodeEvaluation;
	}
	
	
	//find child Node with dice x, where x belongs in {1,3,5,7}
	/**
	 * 
	 * @param x the dice that must be checked if it is contained in the children list
	 * @return -1 if x is not contained or the index of the list of children which contains the dice x
	 */
	public int childDice(int x){
		for(int i = 0; i < children.size(); i++) {
			if(children.get(i).getNodeMove(2) != x) {
				continue;
			}
			return i;
		}
		return -1;	//if dice x is not contained in the children's list
	}
	
}
