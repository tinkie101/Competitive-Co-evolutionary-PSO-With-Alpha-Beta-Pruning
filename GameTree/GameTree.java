package GameTree;

import Game.Checkers.Checkers;
import NeuralNetwork.NeuralNetwork;
import Utils.RandomGenerator;

import java.util.LinkedList;

/**
 * Created by tinkie101 on 2015/06/08.
 */
public class GameTree {

    public int PLY_DEPTH;
    private Checkers game;
    private Node root;
	private NeuralNetwork neuralNetwork;

    public GameTree( Checkers game, NeuralNetwork neuralNetwork){
        this.game = game;
		this.neuralNetwork = neuralNetwork;
        this.root = null;
    }

    public void generateTree(int player, int depth, boolean AlphaBeta) throws Exception{
		PLY_DEPTH = depth;
        root = new Node(game.clone(), true);
        Node currentNode = root;

        //Build game tree
        if(AlphaBeta)
            expandChildrenAlphaBeta(0, currentNode, player, true);
        else
            expandChildren(0, currentNode, player, true);
    }

	public void generateRandomPruneTree(int player, int depth, Double probability) throws Exception{
		PLY_DEPTH = depth;
		root = new Node(game.clone(), true);
		Node currentNode = root;

		//Build game tree
		expandChildrenRandom(0, currentNode, player, true, probability);
	}

	public Node getRandomMove(int player) throws Exception{
		generateTree(player, 1, false);

		LinkedList<Node> children = root.getChildren();

		return children.get(RandomGenerator.getInstance().getRandomRangedIntValue(children.size()-1));
	}

    public Node getBestMove(int player) throws Exception{
        Node temp = getLeaf(root, player);

        LinkedList<Node> children = temp.getChildren();

        for(int i = 0; i < children.size(); i++)
        {
			if(children.get(i) == temp.getBestChild()) {
				if(!children.get(i).getValue().equals(temp.getValue()))
					throw new Exception("Invalid best child for root node!");
				return children.get(i);
			}
        }

        throw new Exception("Couldn't find next Move!");
    }

    public Node getLeaf(Node currentNode, int player) throws Exception{
        LinkedList<Node> children = currentNode.getChildren();

        if(children.size() < 1){
            currentNode.setVal(currentNode.calculateFitness(player, neuralNetwork));

            return currentNode;
        }

        for(int i = 0; i < children.size(); i++)
        {
            Node temp = getLeaf(children.get(i), Checkers.getOpponent(player));
            currentNode.updateValue(temp.getValue(), temp);
        }
        return currentNode;
    }

	//Random Pruning
	public void expandChildrenRandom(int level, Node currentNode, int player, boolean max, Double probability) throws Exception{
		if (level >= PLY_DEPTH)
			return;

		expandNode(currentNode, player, !max);
		LinkedList<Node> children = currentNode.getChildren();

		int size = children.size();

		for(int c = 0; c < size; c++)
		{
			Double randomNumber = RandomGenerator.getInstance().getRandomDoubleValue();

			//Prune randomly
			if(!(c == 0 && children.size() <= 1) && randomNumber < probability){
				children.remove(c);
				size = children.size();
				c--;
			}
			else
				expandChildrenRandom(level + 1, children.get(c), Checkers.getOpponent(player), !max, probability);
		}

	}

    //Alpha-Beta Pruning
    public Double expandChildrenAlphaBeta(int level, Node currentNode, int player, boolean max) throws Exception{
        if (level >= PLY_DEPTH) {
			double fitness = currentNode.calculateFitness(player, neuralNetwork);
            currentNode.setVal(fitness);
            return fitness;
        }

        Node parent = currentNode.getParent();

        expandNode(currentNode, player, !max);
        LinkedList<Node> children = currentNode.getChildren();

        for(int c = 0; c < children.size(); c++)
		{
            currentNode.updateValue(expandChildrenAlphaBeta(level + 1, children.get(c), Checkers.getOpponent(player), !max), children.get(c));

			if(parent != null && parent.getValue() != null && currentNode.getValue() != null){
				if(max ){
					if(currentNode.getValue() >= parent.getValue())
						return parent.getValue();
				} else
				if (currentNode.getValue() <= parent.getValue())
					return parent.getValue();
			}
        }

        if(currentNode.getValue() == null)
            currentNode.setVal(currentNode.calculateFitness(player, neuralNetwork));

        return currentNode.getValue();
    }

    public void expandChildren(int level, Node currentNode, int player, boolean max) throws Exception{
        if (level >= PLY_DEPTH)
            return;

        expandNode(currentNode, player, !max);
        LinkedList<Node> children = currentNode.getChildren();

        for(int c = 0; c < children.size(); c++)
        {
            expandChildren(level + 1, children.get(c), Checkers.getOpponent(player), !max);
        }
    }

    private void expandNode(Node currentNode, int player, boolean max ) throws Exception{
        Checkers tempCurrentGame = currentNode.getGame();

        LinkedList<Integer[]> nextJumps = tempCurrentGame.getAllPossibleJumps(player);

		boolean hasJumps = false;

		//Jumps
		for (int l = 0; l < nextJumps.size(); l++) {
			Checkers newGame = tempCurrentGame.clone();
			Integer[] jump = nextJumps.get(l);
			newGame.jump(jump);
			Integer[] newPos = {jump[2],jump[3]};

			LinkedList<Integer[]> tempJumps = newGame.getJumps(newPos);
			LinkedList<Integer[]> forceJumps = new LinkedList<>();

			Integer[] temp;

			for(int j = 0; j < tempJumps.size(); j++) {
				temp = new Integer[4];
				temp[0] = newPos[0].intValue();
				temp[1] = newPos[1].intValue();
				temp[2] = tempJumps.get(j)[0].intValue();
				temp[3] = tempJumps.get(j)[1].intValue();
				forceJumps.add(temp);
			}

			if(forceJumps.size() < 1){

				if (newGame.shouldKing(newPos))
					newGame.king(newPos);

				Node child = new Node(newGame, currentNode, max);
				currentNode.addChild(child);
				hasJumps = true;
			}
			else{
				//Forced Jumps
				LinkedList<Checkers> forcedGames = new LinkedList<Checkers>();

				for(int g = 0; g < forceJumps.size(); g++)
					forcedGames.add(newGame);

				while(forceJumps.size() > 0) {

					if(forcedGames.size() != forceJumps.size())
						throw new Exception("Invalid forced jumps!");

					Checkers forceNewGame = forcedGames.get(0).clone();
					Integer[] forceJump = forceJumps.get(0);
					forcedGames.remove(0);
					forceJumps.remove(0);
					forceNewGame.jump(forceJump);
					Integer[] tempNewPos = {forceJump[2], forceJump[3]};

					if (forceNewGame.shouldKing(tempNewPos)) {
						//Stop jumping
						forceNewGame.king(tempNewPos);
						Node child = new Node(forceNewGame, currentNode, max);
						currentNode.addChild(child);
						hasJumps = true;
					} else {
						//Check for more jumps
						LinkedList<Integer[]> tempNewForceJumps = forceNewGame.getJumps(tempNewPos);
						LinkedList<Integer[]> tempForceJumps = new LinkedList<Integer[]>();

						for(int j = 0; j < tempNewForceJumps.size(); j++) {
							temp = new Integer[4];
							temp[0] = tempNewPos[0].intValue();
							temp[1] = tempNewPos[1].intValue();
							temp[2] = tempNewForceJumps.get(j)[0].intValue();
							temp[3] = tempNewForceJumps.get(j)[1].intValue();
							tempForceJumps.add(temp);
						}

						if (tempForceJumps.size() < 1) {
							//Stop jumping
							Node child = new Node(forceNewGame, currentNode, max);
							currentNode.addChild(child);
							hasJumps = true;
						} else {
							for (int f = 0; f < tempForceJumps.size(); f++) {
								forceJumps.add(tempForceJumps.get(f));
								forcedGames.add(forceNewGame);
							}
						}

					}
				}
			}
		}

		//Jumps are compulsory, so if there are jumps, moves are unnecessary to expand
		if(!hasJumps)
		{
			LinkedList<Integer[]> nextMoves = tempCurrentGame.getAllPossibleMoves(player);

			//Moves
			for (int l = 0; l < nextMoves.size(); l++)
			{
				Checkers newGame = tempCurrentGame.clone();
				Integer[] move = nextMoves.get(l);
				newGame.move(move);
				Integer[] newPos = {move[2], move[3]};

				if (newGame.shouldKing(newPos))
				{
					newGame.king(newPos);
				}

				Node child = new Node(newGame, currentNode, max);
				currentNode.addChild(child);
			}
		}
    }

//    public static void main(String[] args) throws Exception{
//        Checkers game = new Checkers();
//
//        int[][] tempBoard = game.getCurrentBoardState();
//
//        for(int i = 0; i < tempBoard.length; i++)
//        {
//            for(int k = 0; k < tempBoard[i].length; k++)
//            {
//                if((i == 3 || i == 5) && (k == 2 || k == 4))
//                {
//                    tempBoard[i][k] = 3;
//                }
//                else if(i == 4 && k == 3){
//                    tempBoard[i][k] = 1;
//                }
//                else if(i == 6 && k == 1){
//                    tempBoard[i][k] = 1;
//                }
//                else if(i == 1 && k == 6){
//                    tempBoard[i][k] = 4;
//                }
//                else if(i == 5 && k == 6){
//                    tempBoard[i][k] = 4;
//                }
//                else if(i == 5 && k == 6){
//                    tempBoard[i][k] = 4;
//                }
//                else if(i == 3 && k == 6){
//                    tempBoard[i][k] = 4;
//                }
//                else if(i == 1 && k == 4){
//                    tempBoard[i][k] = 4;
//                }
//                else if(i == 1 && k == 0){
//                    tempBoard[i][k] = 4;
//                }
//                else if( tempBoard[i][k] != -1)
//                    tempBoard[i][k] = 0;
//            }
//        }
//        game.setCurrentBoardState(tempBoard);
//
//		GameTree gameTree;
//		Node nextMove;
//
//		int count = 0;
//		while(count < 100)
//		{
//			if(game.hasLost(1))
//			{
//				System.out.println("Player 1 Lost the game");
//				break;
//			}
//			else
//			{
//				System.out.println("1______________________________");
//				gameTree = new GameTree(game);
//				gameTree.generateTree(1, 6, true);
//
//
//				nextMove = gameTree.getBestMove(1);
//
//				nextMove.printState();
//
//
//				game = nextMove.getGame();
//			}
//
//			if(game.hasLost(2))
//			{
//				System.out.println("Player 2 Lost the game");
//				break;
//			}
//			else
//			{
//
//				System.out.println("2______________________________");
//				gameTree = new GameTree(game);
//				gameTree.generateTree(2, 1, true);
//
//
//				nextMove = gameTree.getBestMove(2);
//
//				nextMove.printState();
//				game = nextMove.getGame();
//			}
//			count++;
//
//		}
//		System.out.println("Done");
//    }
}
