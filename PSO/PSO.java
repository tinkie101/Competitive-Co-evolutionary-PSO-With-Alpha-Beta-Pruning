package PSO;

import PSO.Neighbourhoods.Neighbourhood;
import PSO.Problems.CoevolutionProblem;
import PSO.Problems.Problem;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by tinkie101 on 2015/02/22.
 */

public class PSO
{

	class CalculateFitnessThread implements Callable
	{
		private LinkedList<Double[]> swarmPositions;
		private Particle particle;
		private int removePos;

		CalculateFitnessThread(final int removePos, final LinkedList<Double[]> swarmPositions, Particle particle)
		{
			this.swarmPositions = swarmPositions;
			this.particle = particle;
			this.removePos = removePos;
		}

		@Override
		public Particle call()
		{
			try
			{
				Double[] position = swarmPositions.remove(removePos);
				Double temp = problem.calculateFitness(particle.getPosition(), (LinkedList<Double[]>) swarmPositions.clone());
				particle.setFitnessValue(temp);
				swarmPositions.add(removePos, position);
				swarmPositions.remove(removePos + 1);
				Double tempPBest = problem.calculateFitness(particle.getPBestPosition(), swarmPositions);
				particle.setPBestValue(tempPBest);

				return particle;
			}
			catch (Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			System.exit(-1);
			return null;
		}
	}


	private int numParticles;
	private Particle[] swarm;
	private Problem problem;
	private Neighbourhood neighbourhood;
	private boolean minimisation;
	private int processors;

	//  1) initialize n-dimensional swarm
	public PSO(boolean minimisation, Problem problem, int numParticles, Neighbourhood neighbourhood) throws Exception
	{
		processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of threads: " + processors);

		this.minimisation = minimisation;

		this.problem = problem;

		this.neighbourhood = neighbourhood;

		this.numParticles = numParticles;

		this.swarm = new Particle[numParticles];

		//Create swarm particles
		LinkedList<Double[]> tempSwarm = new LinkedList<>();
		for (int i = 0; i < numParticles; i++)
		{
			Particle tempParticle = new Particle(problem, CoevolutionProblem.generateRandomWeights());

			swarm[i] = tempParticle;

			tempSwarm.add(tempParticle.getPosition());
			tempSwarm.add(tempParticle.getPBestPosition());
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(processors);
		Set<Future<Particle>> set = new HashSet<>();

		//Calculate each particle's initial pBest
		for (int i = 0, count = 0; i < tempSwarm.size(); i+=2, count++)
		{
			if(tempSwarm.size() != swarm.length*2)
				throw new Exception("Invalid tempSwarm size!");

			Callable<Particle> callable =  new CalculateFitnessThread(i, (LinkedList<Double[]>) tempSwarm.clone(), swarm[count]);
			Future<Particle> future = threadPool.submit(callable);
			set.add(future);
		}

		for (Future<Particle> future : set) {
			Particle tempParticle = future.get();
			tempParticle.setPBestValue(tempParticle.getFitnessValue());
		}

		threadPool.shutdown();

		this.neighbourhood.setParticles(swarm);
	}

	public Particle getGlobalBest()
	{
		Particle tempGBest = null;
		boolean first = true;

		for (int i = 0; i < numParticles; i++)
		{
			if (minimisation)
			{
				if (first || tempGBest.getPBestValue() > swarm[i].getPBestValue())
				{
					first = false;
					tempGBest = swarm[i];
				}
			} else
			{
				if (first || tempGBest.getPBestValue() < swarm[i].getPBestValue())
				{
					first = false;
					tempGBest = swarm[i];
				}
			}
		}
		return tempGBest;
	}

	public Particle getGlobalWorst()
	{
		Particle tempGWorst = null;
		boolean first = true;

		for (int i = 0; i < numParticles; i++)
		{
			if (!minimisation)
			{
				if (first || tempGWorst.getFitnessValue() > swarm[i].getFitnessValue())
				{
					first = false;
					tempGWorst = swarm[i];
				}
			} else
			{
				if (first || tempGWorst.getFitnessValue() < swarm[i].getFitnessValue())
				{
					first = false;
					tempGWorst = swarm[i];
				}
			}
		}
		return tempGWorst;
	}


	//Run Update Step and return the updated swarm
	public Particle[] runUpdateStep() throws Exception
	{
		Double[][] newSwarm = new Double[swarm.length][];

		for (int i = 0; i < numParticles; i++)
		{
			Particle nBest = neighbourhood.getNeigbourhoodBest(swarm[i], minimisation);

			//  2) Update particle velocity
			swarm[i].updateVelocity(nBest.getPosition());

			//  3) update particle position
			newSwarm[i] = swarm[i].calculateNewPosition();
		}

		for(int i = 0; i < swarm.length; i++){
			swarm[i].setPosition(newSwarm[i]);
		}

		LinkedList<Double[]> tempSwarm = new LinkedList<>();

		for(int l = 0; l < numParticles; l++)
		{
			tempSwarm.add(swarm[l].getPosition());
			tempSwarm.add(swarm[l].getPBestPosition());
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(processors);
		Set<Future<Particle>> set = new HashSet<>();
		int count = 0;
		//  1) set each particle's pBest
		for (int i = 0; i < tempSwarm.size(); i+=2, count++)
		{
			if(tempSwarm.size() != swarm.length*2)
				throw new Exception("Invalid tempSwarm size!");

			Callable<Particle> callable =  new CalculateFitnessThread(i, (LinkedList<Double[]>) tempSwarm.clone(), swarm[count]);
			Future<Particle> future = threadPool.submit(callable);
			set.add(future);
		}

		if(count != swarm.length)
			throw new Exception("Invalid count!");

		count = 0;
		for (Future<Particle> future : set) {
			Particle particle = future.get();
			Double newFitness = particle.getFitnessValue();
			Double[] position = particle.getPosition();

			double pBestFitness = particle.getPBestValue();

			if (minimisation)
			{
				if (pBestFitness > newFitness)
				{
					particle.setPBestPosition(position);
					particle.setPBestValue(newFitness);
				}
			} else //maximization
			{
				if (pBestFitness < newFitness)
				{
					particle.setPBestPosition(position);
					particle.setPBestValue(newFitness);
				}
			}
			count++;
		}

		threadPool.shutdown();

		if(count != swarm.length)
			throw new Exception("Invalid count!");

		return swarm;
	}

	public Particle[] getSwarm(){
		return swarm;
	}
}
