package CoEvolution;

import GameTree.PlayGame;
import NeuralNetwork.NeuralNetwork;
import PSO.Neighbourhoods.Neighbourhood;
import PSO.Neighbourhoods.VonNeumann;
import PSO.PSO;
import PSO.Particle;
import PSO.Problems.CoevolutionProblem;

/**
 * Created by tinkie101 on 2015/06/25.
 */
public class Coevolution
{
	public static final int NUM_RANDOM_PLAYS = 5;
	public static final int NUM_CONTROL_GAMES = 10000;
	public static int MAX_NUM_MOVES = 50;

	private static int numParticles = 27;
	private static int x = 3, y = 3, z = 3;
	private PSO pso;
	private int PlyDepth;
	private Boolean AlphaBeta;
	private double probability;

	Coevolution(int plyDepth, Boolean alphaBeta, double probability) throws Exception{

		this.PlyDepth = plyDepth;
		this.AlphaBeta = alphaBeta;
		this.probability = probability;

	}

	public void runCoevolution(int numEpochs) throws Exception{

		double finalResult = 0.0d;

		for(int e = 0; e < 15; e++)
		{

			//1
			CoevolutionProblem problem = new CoevolutionProblem(NUM_RANDOM_PLAYS, MAX_NUM_MOVES, PlyDepth, AlphaBeta, probability);
			Neighbourhood neighbourhood = new VonNeumann(x, y, z);

			pso = new PSO(false, problem, numParticles, neighbourhood);

			int winPlayer1 = 0;
			int losePlayer1 = 0;
			int drawPlayer1 = 0;

			int winPlayer2 = 0;
			int losePlayer2 = 0;
			int drawPlayer2 = 0;

			Particle gBestStart = pso.getGlobalBest();
			double startVal = gBestStart.getPBestValue();
			//2
			for (int i = 0; i < numEpochs; i++)
			{
				System.out.println(i);
				pso.runUpdateStep();
			}

			//3
			Particle gBest = pso.getGlobalBest();
			Double[] position = gBest.getPBestPosition();
			System.out.println("\nStart: " + startVal + "; End: " + gBest.getPBestValue());

			//4
			System.out.println("Playing as Player 1");

			NeuralNetwork tempNeuralNet = CoevolutionProblem.getNewNeuralNetwork();

			Double[][][] tempPlayerWeights = tempNeuralNet.getWeights();

			int count = 0;
			for (int n = 0; n < tempPlayerWeights.length; n++)
			{
				for (int l = 0; l < tempPlayerWeights[n].length; l++)
				{
					for (int k = 0; k < tempPlayerWeights[n][l].length; k++)
					{
						tempPlayerWeights[n][l][k] = position[count++];
					}
				}
			}
			tempNeuralNet.setWeights(tempPlayerWeights);

			for (int i = 0; i < NUM_CONTROL_GAMES; i++)
			{
				PlayGame tempGame = new PlayGame(tempNeuralNet, null, PlyDepth, AlphaBeta, MAX_NUM_MOVES, probability);

				switch (tempGame.play())
				{
					case 0:
						losePlayer1++;
						break;
					case 1:
						winPlayer1++;
						break;
					case 2:
						drawPlayer1++;
						break;
					default:
						throw new Exception("Invalid result!");
				}
			}

			//5
			System.out.println("Playing as Player 2");

			for (int i = 0; i < NUM_CONTROL_GAMES; i++)
			{
				PlayGame tempGame = new PlayGame(null, tempNeuralNet, PlyDepth, AlphaBeta, MAX_NUM_MOVES, probability);

				switch (tempGame.play())
				{
					case 0:
						winPlayer2++;
						break;
					case 1:
						losePlayer2++;
						break;
					case 2:
						drawPlayer2++;
						break;
					default:
						throw new Exception("Invalid result!");
				}
			}

			double Player1WinScore = (double) winPlayer1 / (double) NUM_CONTROL_GAMES * 3.0d;
			double Player1LoseScore = (double) losePlayer1 / (double) NUM_CONTROL_GAMES * 1.0d;
			double Player1DrawScore = (double) drawPlayer1 / (double) NUM_CONTROL_GAMES * 2.0d;
			double Player1Score = Player1DrawScore + Player1LoseScore + Player1WinScore;

			double Player2WinScore = (double) winPlayer2 / (double) NUM_CONTROL_GAMES * 3.0d;
			double Player2LoseScore = (double) losePlayer2 / (double) NUM_CONTROL_GAMES * 1.0d;
			double Player2DrawScore = (double) drawPlayer2 / (double) NUM_CONTROL_GAMES * 2.0d;
			double Player2Score = Player2DrawScore + Player2LoseScore + Player2WinScore;

			double finalScore = (Player1Score + Player2Score) / 2.0d;
			finalScore = ((2.0d) * (finalScore - 1.0d)) / (2.0d);
			finalScore = finalScore / 2.0d * 100.0d;
			finalResult += finalScore;
			System.out.println("==========================================");
			System.out.println("Final Score:" + finalScore);
		}

		System.out.println("==========================================");
		finalResult = finalResult / 15.0d;
		System.out.println("Final Result:" + finalResult);

	}

	public static void main(String[] args) throws Exception{

		long startTime = System.currentTimeMillis();
	//TODO settings!
		double probability = 1.0d;
		Boolean AlphaBeta = true;
		int plyDepth = 2;
		int numEpochs = 500;

		Coevolution coevolution = new Coevolution(plyDepth, AlphaBeta, probability);
		coevolution.runCoevolution(numEpochs);

		long endTime = System.currentTimeMillis();
		System.out.println("\nTook " + ((endTime - startTime) / 1000.0d) + " seconds");
	}
}
