package CoEvolution;

import GameTree.PlayGame;
import NeuralNetwork.NeuralNetwork;
import PSO.Neighbourhoods.Neighbourhood;
import PSO.Neighbourhoods.VonNeumann;
import PSO.PSO;
import PSO.Particle;
import PSO.Problems.CoevolutionProblem;
import Utils.FileHandler;

import java.text.DecimalFormat;

/**
 * Created by tinkie101 on 2015/06/25.
 */
public class Coevolution
{
	public static final int NUM_RANDOM_PLAYS = 5;
	public static final int NUM_CONTROL_GAMES = 10000;
	public static final int NUM_RUNS = 30;
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
		StringBuilder stringBuilder = new StringBuilder();
		double time = System.currentTimeMillis();

		stringBuilder.append("NUM_RANDOM_PLAYS: " + NUM_RANDOM_PLAYS + "\n");
		stringBuilder.append("NUM_CONTROL_GAMES: " + NUM_CONTROL_GAMES + "\n");
		stringBuilder.append("NUM_RUNS: " + NUM_RUNS + "\n");
		stringBuilder.append("MAX_NUM_MOVES: " + MAX_NUM_MOVES + "\n");
		stringBuilder.append("numParticles: " + numParticles + "\n");
		stringBuilder.append("PlyDepth: " + PlyDepth + "\n");
		stringBuilder.append("AlphaBeta: " + AlphaBeta + "\n");
		stringBuilder.append("probability: " + probability + "\n");

		FileHandler.writeFile("output/" + time + "/Settings.txt" , stringBuilder.toString());

		for(int e = 0; e < NUM_RUNS; e++)
		{
			stringBuilder = new StringBuilder();
			System.out.println("Run " + e + " of " + NUM_RUNS);
			stringBuilder.append("Run " + e + " of " + (NUM_RUNS-1) +"\n");
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
			System.out.println("Training ");
			DecimalFormat df = new DecimalFormat("#.00");
			//2
			for (int i = 0; i < numEpochs; i++)
			{
				double percentage = (double)i/(double)numEpochs*100.0d;

				System.out.print("\r" + df.format(percentage) + "%");
				pso.runUpdateStep();
			}
			System.out.print("\r" + df.format(100.0d) + "%");
			System.out.println();

			//3
			Particle gBest = pso.getGlobalBest();
			Double[] position = gBest.getPBestPosition();

			System.out.println("\nStart: " + startVal + "; End: " + gBest.getPBestValue());
			stringBuilder.append("Start: " + startVal + "; End: " + gBest.getPBestValue() +"\n");

			//4
			NeuralNetwork tempNeuralNet = CoevolutionProblem.getNewNeuralNetwork();

			Double[][][] tempPlayerWeights = tempNeuralNet.getWeights();


			stringBuilder.append("NN Weights: ");
			stringBuilder.append(gBest.toString() + "\n");

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

			System.out.println("Playing as Player 1");
			for (int i = 0; i < NUM_CONTROL_GAMES; i++)
			{
				double percentage = (double)i/(double)NUM_CONTROL_GAMES*100.0d;
				System.out.print("\r" + df.format(percentage) + "%");

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
			System.out.print("\r" + df.format(100.0d) + "%");
			System.out.println();

			//5
			System.out.println("Playing as Player 2");

			for (int i = 0; i < NUM_CONTROL_GAMES; i++)
			{

				double percentage = (double)i/(double)NUM_CONTROL_GAMES*100.0d;
				System.out.print("\r" + df.format(percentage) + "%");
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
			System.out.print("\r" + df.format(100.0d) + "%");
			System.out.println();

			double Player1WinScore = (double) winPlayer1 / (double) NUM_CONTROL_GAMES * 3.0d;
			double Player1LoseScore = (double) losePlayer1 / (double) NUM_CONTROL_GAMES * 1.0d;
			double Player1DrawScore = (double) drawPlayer1 / (double) NUM_CONTROL_GAMES * 2.0d;
			double Player1Score = Player1DrawScore + Player1LoseScore + Player1WinScore;
			stringBuilder.append("Player1 win/lose/draw: " + winPlayer1 + "/" + losePlayer1 + "/" + drawPlayer1 + "\n");

			double tempScore = ((2.0d) * (Player1Score - 1.0d)) / (2.0d);
			tempScore = tempScore / 2.0d * 100.0d;

			stringBuilder.append("Player1 Score: " + tempScore + "\n");

			double Player2WinScore = (double) winPlayer2 / (double) NUM_CONTROL_GAMES * 3.0d;
			double Player2LoseScore = (double) losePlayer2 / (double) NUM_CONTROL_GAMES * 1.0d;
			double Player2DrawScore = (double) drawPlayer2 / (double) NUM_CONTROL_GAMES * 2.0d;
			double Player2Score = Player2DrawScore + Player2LoseScore + Player2WinScore;

			stringBuilder.append("Player2 win/lose/draw: " + winPlayer2 + "/" + losePlayer2 + "/" + drawPlayer2 + "\n");

			tempScore = ((2.0d) * (Player2Score - 1.0d)) / (2.0d);
			tempScore = tempScore / 2.0d * 100.0d;
			stringBuilder.append("Player2 Score: " + tempScore + "\n");

			double finalScore = (Player1Score + Player2Score) / 2.0d;
			finalScore = ((2.0d) * (finalScore - 1.0d)) / (2.0d);
			finalScore = finalScore / 2.0d * 100.0d;
			finalResult += finalScore;
			System.out.println("==========================================");
			System.out.println("Final Score:" + finalScore);
			stringBuilder.append("F-measure: " + finalScore + "\n");
			System.out.println("==========================================\n");

			String ab = "null";
			if( AlphaBeta != null)
				ab = AlphaBeta.toString();

			FileHandler.writeFile("output/" + time + "/" + PlyDepth + "-" + ab + "-" + probability + "-" + e + ".txt", stringBuilder.toString());
		}


		stringBuilder = new StringBuilder();

		System.out.println("==========================================");
		finalResult = finalResult / NUM_RUNS;
		System.out.println("Final Result:" + finalResult);
		stringBuilder.append(finalResult);
		FileHandler.writeFile("output/" + time + "/AverageFMeasure.txt" , stringBuilder.toString());

	}

	public static void main(String[] args) throws Exception{

		long startTime = System.currentTimeMillis();
	//TODO settings!
		double probability = 0.1d;
		Boolean AlphaBeta = false;
		int plyDepth = 4;
		int numEpochs = 500;

		Coevolution coevolution = new Coevolution(plyDepth, AlphaBeta, probability);
		coevolution.runCoevolution(numEpochs);

		long endTime = System.currentTimeMillis();
		System.out.println("\nTook " + ((endTime - startTime) / 1000.0d) + " seconds");
	}
}
