package PSO.Problems;

import NeuralNetwork.NeuralNetwork;
import Utils.RandomGenerator;
import GameTree.PlayGameThread;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by tinkie101 on 2015/02/25.
 */
public class CoevolutionProblem extends Problem
{

	public static final int NUM_LAYERS = 3;
	public static final int NUM_INPUTS = 32;
	public static final int NUM_HIDDEN = 5;
	public static final int NUM_OUTPUT = 1;

	public static final int BIAS = 1;
	public static final Integer[] numLayerNodes = {NUM_INPUTS, NUM_HIDDEN, NUM_OUTPUT};
	public static final int numWeights = ((NUM_INPUTS+BIAS)*NUM_HIDDEN) + ((NUM_HIDDEN+BIAS)*NUM_OUTPUT);


	private int PLY_DEPTH;
	private boolean ALPHA_BETA;


	private int numRandomPlays;
	private NeuralNetwork mockNeuralNet;


	//Hard code the problem function parameters
	public CoevolutionProblem(int numRandomPlays, int ply_depth, boolean alpha_beta) throws Exception
	{
		super( numWeights );

		mockNeuralNet = new NeuralNetwork(NUM_LAYERS, numLayerNodes, (BIAS == 1));

		this.numRandomPlays = numRandomPlays;
		this.PLY_DEPTH = ply_depth;
		this.ALPHA_BETA = alpha_beta;
	}

	public static NeuralNetwork getNewNeuralNetwork() throws Exception{
		return new NeuralNetwork(NUM_LAYERS, numLayerNodes, (BIAS == 1));
	}

	public static Double[] generateRandomWeights() throws Exception{
		NeuralNetwork temp = new NeuralNetwork(NUM_LAYERS, numLayerNodes, (BIAS == 1));

		Double[][][] weights = temp.getWeights();
		LinkedList<Double> tempList = new LinkedList<Double>();


		for(int i = 0; i < weights.length; i++)
		{
			for (int l = 0; l < weights[i].length; l++)
			{
				for (int k = 0; k < weights[i][l].length; k++)
				{
					tempList.add(weights[i][l][k]);
				}
			}
		}

		Double[] array = tempList.toArray(new Double[tempList.size()]);

		return array;
	}

	@Override
	public double calculateFitness(Double[] position, LinkedList<Double[]> swarmPositions) throws Exception
	{
		//set neuralNetwork weights of player
		Double[][][] tempPlayer1Weights = mockNeuralNet.getWeights();

		int count = 0;
		for(int i = 0; i < tempPlayer1Weights.length; i++){
			for(int l = 0; l < tempPlayer1Weights[i].length; l++){
				for(int k = 0; k < tempPlayer1Weights[i][l].length; k++){
					tempPlayer1Weights[i][l][k] = position[count++];
				}
			}
		}

		if(count != numWeights)
			throw new Exception("Invalid number of weights!");


		NeuralNetwork tempPlayer1NeuralNet = new NeuralNetwork(NUM_LAYERS, numLayerNodes, (BIAS == 1));
		tempPlayer1NeuralNet.setWeights(tempPlayer1Weights);

		ExecutorService threadPool = Executors.newFixedThreadPool(numRandomPlays);
		Set<Future<Integer>> set = new HashSet<Future<Integer>>();

		for(int c = 0; c < numRandomPlays; c++)
		{
			int random = RandomGenerator.getInstance().getRandomRangedIntValue(swarmPositions.size()-1);
			Double[] opponent = swarmPositions.remove(random);

			//set neuralNetwork weights of opponent
			Double[][][] tempPlayer2Weights = mockNeuralNet.getWeights();

			count = 0;
			for(int i = 0; i < tempPlayer2Weights.length; i++){
				for(int l = 0; l < tempPlayer2Weights[i].length; l++){
					for(int k = 0; k < tempPlayer2Weights[i][l].length; k++){
						tempPlayer2Weights[i][l][k] = opponent[count++];
					}
				}
			}

			if(count != numWeights)
				throw new Exception("Invalid number of weights!");

			NeuralNetwork tempPlayer2NeuralNet = new NeuralNetwork(NUM_LAYERS, numLayerNodes, (BIAS == 1));
			tempPlayer2NeuralNet.setWeights(tempPlayer2Weights);

			//Play Game
			Callable<Integer> callable = new PlayGameThread(tempPlayer1NeuralNet, tempPlayer2NeuralNet, PLY_DEPTH, ALPHA_BETA);
			Future<Integer> future = threadPool.submit(callable);
			set.add(future);
		}

		Double score = 0.0d;

		for (Future<Integer> future : set) {
			score += getResultScore(future.get());
		}

		threadPool.shutdown();
		return score;
	}


	/**
	 loss	=	0
	 win		=	1
	 draw	=	2
	 */
	private Double getResultScore(int val) throws Exception
	{
		switch (val){
			case 0: return -2.0d;
			case 1: return 1.0d;
			case 2: return 0.0d;
			default: throw  new Exception("Invalid result!");
		}
	}

	public int getPlyDepth(){
		return PLY_DEPTH;
	}

	public boolean getAlphaBeta(){
		return ALPHA_BETA;
	}
}
