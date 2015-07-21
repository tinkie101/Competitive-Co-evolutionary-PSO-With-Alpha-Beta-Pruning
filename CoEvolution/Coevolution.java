package CoEvolution;

import GameTree.PlayGameThread;
import NeuralNetwork.NeuralNetwork;
import PSO.Neighbourhoods.Neighbourhood;
import PSO.Neighbourhoods.VonNeumann;
import PSO.PSO;
import PSO.Particle;
import PSO.Problems.CoevolutionProblem;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by tinkie101 on 2015/06/25.
 */
public class Coevolution
{
	public static final int NUM_RANDOM_PLAYS = 5;
	public static final int NUM_CONTROL_GAMES = 10000;
	public static final int NUM_THREADS = 1000;

	private static int numParticles = 27;
	private static int x = 3, y = 3, z = 3;
	private PSO pso;
	private int PlyDepth;
	private boolean AlphaBeta;

	Coevolution(int plyDepth, boolean alphaBeta) throws Exception{

		this.PlyDepth = plyDepth;
		this.AlphaBeta = alphaBeta;

	}

	public void runCoevolution(int numEpochs) throws Exception{

		double finalResult = 0.0d;

		for(int e = 0; e < 15; e++)
		{

			//1
			CoevolutionProblem problem = new CoevolutionProblem(NUM_RANDOM_PLAYS, PlyDepth, AlphaBeta);
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

//			ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
//			Set<Future<Integer>> set = new HashSet<Future<Integer>>();

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
				Callable<Integer> callable = new PlayGameThread(tempNeuralNet, null, PlyDepth, AlphaBeta);

				switch (callable.call())
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
//				Future<Integer> future = threadPool.submit(callable);
//				set.add(future);
			}

//			for (Future<Integer> future : set) {
//				switch (future.get())
//				{
//					case 0:
//						losePlayer1++;
//						break;
//					case 1:
//						winPlayer1++;
//						break;
//					case 2:
//						drawPlayer1++;
//						break;
//					default:
//						throw new Exception("Invalid result!");
//				}
//			}

			//5
			System.out.println("Playing as Player 2");

//			threadPool.shutdown();
//			threadPool = Executors.newFixedThreadPool(NUM_THREADS);
//			set = new HashSet<Future<Integer>>();

			for (int i = 0; i < NUM_CONTROL_GAMES; i++)
			{
				Callable<Integer> callable = new PlayGameThread(null, tempNeuralNet, PlyDepth, AlphaBeta);
//				Future<Integer> future = threadPool.submit(callable);
//				set.add(future);

				switch (callable.call())
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

//			for (Future<Integer> future : set) {
//				switch (future.get())
//				{
//					case 0:
//						winPlayer2++;
//						break;
//					case 1:
//						losePlayer2++;
//						break;
//					case 2:
//						drawPlayer2++;
//						break;
//					default:
//						throw new Exception("Invalid result!");
//				}
//			}

//			threadPool.shutdown();

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
		Coevolution coevolution = new Coevolution(1, true);
		coevolution.runCoevolution(500);

		long endTime = System.currentTimeMillis();
		System.out.println("\nTook " + ((endTime - startTime) / 1000.0d) + " seconds");
	}
}
