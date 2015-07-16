package PSO.Problems;

import java.util.LinkedList;

/**
 * Created by tinkie101 on 2015/02/25.
 */
public abstract class Problem
{
	protected final int numDimensions;

	protected final double c1 = 1.4;
	protected final double c2 = 1.2;

	public Problem(int numDimensions)
	{
		this.numDimensions = numDimensions;
	}

	//Abstract methods
	public abstract double calculateFitness(Double[] position, LinkedList<Double[]> swarmPositions) throws Exception;

	public double getC1()
	{
		return c1;
	}

	public double getC2()
	{
		return c2;
	}

	public int getNumDimensions()
	{
		return numDimensions;
	}
}
