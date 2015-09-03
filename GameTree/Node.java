package GameTree;

import Game.Checkers.Checkers;
import NeuralNetwork.NeuralNetwork;

import java.util.LinkedList;

/**
 * Created by tinkie101 on 2015/06/08.
 */
public class Node{

    private Checkers gameState;
    private LinkedList<Node> children;
    private Node parent;
    private boolean max;
    private Double val;
    private Node bestChild;

    public Node(Checkers gameState, boolean max) throws Exception{
        this.gameState = gameState;

        this.children = new LinkedList<Node>();
        this.parent = null;
        this.max = max;
    }

    public Node(Checkers gameState, Node parent, boolean max) throws Exception{
        this.gameState = gameState;
        this.children = new LinkedList<Node>();

        this.parent = parent;
        this.max = max;
    }

    public void updateValue(Double val, Node child){
        if(this.val == null || (this.max && val > this.val) || (!this.max && val < this.val)) {
            this.val = val.doubleValue();
            this.bestChild = child;
        }
    }

    public Double getValue(){
        return this.val;
    }

    public Node getBestChild(){
        return bestChild;
    }

    public void setVal(Double val){
        this.val = val.doubleValue();
    }

    public void addChild(Node child){
        children.add(child);
    }

    public Checkers getGame(){
        return gameState;
    }

    public LinkedList<Node> getChildren()
    {
        return children;
    }

    public Node getParent(){
        return parent;
    }

    public void printState(){
        System.out.println("=================================");
        gameState.printCurrentBoardState();
        gameState.printPlayerItems();
        System.out.println("=================================");
    }

//    public Double calculateFitness(int player, NeuralNetwork neuralNetwork) throws Exception{
//        Double result = 12.0d;
//
//        int opponent = Checkers.getOpponent(player);
//
//        if(gameState.hasLost(opponent))
//            result = -999.9d;
//        else
//            result -= gameState.getPlayerPieceCount(opponent);
//
//        return result;
//    }

	private double getPieceValue(int cellValue, int player) throws Exception{

        if(player == 1) {
            switch (cellValue) {
                case 0:
                    return 0.5d;
                case 1:
                    return 0.75d;
                case 2:
                    return 1.0d;
                case 3:
                    return 0.25d;
                case 4:
                    return 0.0d;
                default:
                    throw new Exception("Invalid Piece value!");

            }
        }else if(player == 2){
            switch (cellValue) {
                case 0:
                    return 0.5d;
                case 1:
                    return 0.25d;
                case 2:
                    return 0.0d;
                case 3:
                    return 0.75d;
                case 4:
                    return 0.1d;
                default:
                    throw new Exception("Invalid Piece value!");
            }
        }
        else
        {
            throw new Exception("Invalid Player!");
        }
	}

	public Double calculateFitness(int player, NeuralNetwork neuralNetwork) throws Exception{
		int[][] board = gameState.getCurrentBoardState();
		Double[][] input = new Double[32][1];
		int count = 0;

		for(int y = 0; y < board.length; y++)
		{
			for(int x = 0; x < board[y].length; x++)
			{
				int cellVal = board[y][x];
				if(cellVal != -1)
					input[count++][0] = getPieceValue(cellVal, player);
			}
		}

		if(count != 32)
			throw new Exception("Invalid board state!");

		return neuralNetwork.getOutput(input)[0];
	}
}
