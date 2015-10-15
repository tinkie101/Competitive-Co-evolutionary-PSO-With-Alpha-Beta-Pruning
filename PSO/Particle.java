package PSO;

import PSO.Problems.Problem;
import Utils.RandomGenerator;

/**
 * Created by tinkie101 on 2015/02/22.
 */
public class Particle
{
	private Double[] position;
	private Double[] velocity;
	private Double[] pBestPosition;
	private Double pBestValue;
	private Double fitnessValue;
	private Integer numDimensions;
	private Double c1, c2;

	public Particle(Problem problem, Double[] neuralNetworkWeights) throws Exception
	{

		this.numDimensions = problem.getNumDimensions();

		if (numDimensions != neuralNetworkWeights.length)
			throw new Exception("Invalid neural network weights");

		this.c1 = problem.getC1();
		this.c2 = problem.getC2();

		//Initialise position, pBestPosition and initial velocity.
		position = new Double[numDimensions];
		pBestPosition = new Double[numDimensions];
		velocity = new Double[numDimensions];

		for (int i = 0; i < numDimensions; i++)
		{
			position[i] = neuralNetworkWeights[i];
			pBestPosition[i] = neuralNetworkWeights[i];
			velocity[i] = 0.0d;
		}
	}

	public void setPBestPosition(Double[] pBest)
	{
		this.pBestPosition = pBest;
	}

	public void setPBestValue(Double pBest)
	{
		this.pBestValue = pBest;
	}

	public Double getPBestValue()
	{
		return pBestValue;
	}

	public void setFitnessValue(Double currentFitness){
		this.fitnessValue = currentFitness;
	}

	public Double getFitnessValue(){
		return fitnessValue;
	}

	public Double[] getPBestPosition()
	{
		Double[] result = new Double[numDimensions];

		for (int i = 0; i < numDimensions; i++)
			result[i] = pBestPosition[i];

		return result;
	}

	public Double[] getPosition()
	{
		Double[] result = new Double[numDimensions];

		for (int i = 0; i < numDimensions; i++)
			result[i] = position[i];

		return result;
	}

	public void setPosition(Double[] newPosition) throws Exception{

		if(newPosition.length != this.position.length)
			throw new Exception("Invalid position!");

		this.position = newPosition;
	}


	public void updateVelocity(Double[] nBest) throws Exception
	{
		for (int i = 0; i < velocity.length; i++)
		{
			double r1 = RandomGenerator.getInstance().getRandomDoubleValue();
			double r2 = RandomGenerator.getInstance().getRandomDoubleValue();

			velocity[i] = 0.7d*velocity[i] + (c1 * r1 * (pBestPosition[i] - position[i])) + (c2 * r2 * (nBest[i] - position[i]));

			//TODO Velocity clamping, reset to 0
			if(velocity[i] > 0.2d || velocity[i] < -0.2d)
				velocity[i] = 0.0d;
		}
	}

	public Double[] calculateNewPosition() throws Exception
	{
		return vectorAdd(position, velocity);
	}

	/*
		Vector addition
	 */
	private Double[] vectorAdd(Double[] first, Double[] second) throws Exception
	{
		if (first.length != second.length)
			throw new Exception("Vector addition must have the same dimensions");

		Double[] result = new Double[first.length];
		for (int i = 0; i < first.length; i++)
		{
			result[i] = first[i] + second[i];
		}

		return result;
	}

	@Override
	public String toString()
	{
		String result = "[";


		for (int i = 0; i < pBestPosition.length; i++)
		{

			if (i + 1 >= pBestPosition.length)
			{
				result = result + pBestPosition[i];
			} else
			{
				result = result + pBestPosition[i] + ",";
			}
		}

		result = result + "]";
		return result;
	}
}
