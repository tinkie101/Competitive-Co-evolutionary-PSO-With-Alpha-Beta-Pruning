package GameTree;

import Game.Checkers.Checkers;
import NeuralNetwork.NeuralNetwork;

import java.util.concurrent.Callable;

/**
 * Created by tinkie101 on 2015/07/01.
 */
public class PlayGameThread implements Callable
{
	public static final int PLAYER1 = 1;
	public static final int PLAYER2 = 2;

	private int PLY_DEPTH;
	private boolean ALPHA_BETA;


	private NeuralNetwork tempPlayer1NeuralNet;
	private NeuralNetwork tempPlayer2NeuralNet;

	public PlayGameThread(final NeuralNetwork tempPlayer1NeuralNet, final NeuralNetwork tempPlayer2NeuralNet, int ply_depth, boolean alpha_beta){
		this.tempPlayer1NeuralNet =  tempPlayer1NeuralNet;
		this.tempPlayer2NeuralNet = tempPlayer2NeuralNet;

		this.PLY_DEPTH = ply_depth;
		this.ALPHA_BETA = alpha_beta;
	}

	@Override
	public Integer call()
	{
		try
		{
			Checkers game = new Checkers();
			GameTree gameTree;
			Node nextMove;

			int moveCount = 0;
			while (moveCount < 50)
			{
				if (game.hasLost(PLAYER1))
				{
					//Update performance measure for Loss
					return 0;
				} else
				{
					gameTree = new GameTree(game, tempPlayer1NeuralNet);
					if(tempPlayer1NeuralNet == null){
						nextMove = gameTree.getRandomMove(PLAYER1);
					}
					else{
						gameTree.generateTree(PLAYER1, PLY_DEPTH, ALPHA_BETA);
						nextMove = gameTree.getBestMove(PLAYER1);
					}
					game = nextMove.getGame();
				}

				if (game.hasLost(PLAYER2))
				{
					//Update performance measure for Win
					return 1;
				} else
				{
					gameTree = new GameTree(game, tempPlayer2NeuralNet);

					if(tempPlayer2NeuralNet == null){
						nextMove = gameTree.getRandomMove(PLAYER2);
					}
					else
					{
						gameTree.generateTree(PLAYER2, PLY_DEPTH, ALPHA_BETA);
						nextMove = gameTree.getBestMove(PLAYER2);
					}
					game = nextMove.getGame();
				}
				moveCount++;
			}
		}
		catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		return 2;
	}
}
