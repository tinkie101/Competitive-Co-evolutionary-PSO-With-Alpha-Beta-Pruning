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
		private Double[] position;
		private LinkedList<Double[]> swarmPositions;
		private Particle particle;

		CalculateFitnessThread(final Double[] position, final LinkedList<Double[]> swarmPositions, Particle particle)
		{
			this.position = position;
			this.swarmPositions = swarmPositions;
			this.particle = particle;
		}

		@Override
		public Particle call()
		{
			try
			{
				Double temp = problem.calculateFitness(position, swarmPositions);
				particle.setFitnessValue(temp);
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

	//  1) initialize n-dimensional swarm
	public PSO(boolean minimisation, Problem problem, int numParticles, Neighbourhood neighbourhood) throws Exception
	{
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

		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Set<Future<Particle>> set = new HashSet<>();

		//Calculate each particle's initial pBest
		for (int i = 0, count = 0; i < tempSwarm.size(); i+=2, count++)
		{
			Double[] position = tempSwarm.remove(i);

			if(tempSwarm.size() != (swarm.length*2)-1)
				throw new Exception("Invalid tempSwarm size!");

			Callable<Particle> callable =  new CalculateFitnessThread(position, (LinkedList<Double[]>) tempSwarm.clone(), swarm[count]);
			Future<Particle> future = threadPool.submit(callable);
			set.add(future);

			tempSwarm.add(i,position);
		}

		for (Future<Particle> future : set) {
			Particle tempParticle = future.get();
			tempParticle.setPBestValue(tempParticle.getFitnessValue());
		}

		threadPool.shutdown();

		neighbourhood.setParticles(swarm);
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


	//Run Update Step and return the updated swarm
	public Particle[] runUpdateStep() throws Exception
	{
		for (int i = 0; i < numParticles; i++)
		{
			Particle nBest = neighbourhood.getNeigbourhoodBest(swarm[i], minimisation);

			//  2) Update particle velocity
			swarm[i].updateVelocity(nBest.getPBestPosition());

			//  3) update particle position
			swarm[i].updatePosition();
		}


		LinkedList<Double[]> tempSwarm = new LinkedList<>();

		for(int l = 0; l < numParticles; l++)
		{
			tempSwarm.add(swarm[l].getPosition());
			tempSwarm.add(swarm[l].getPBestPosition());
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		Set<Future<Particle>> set = new HashSet<>();

		//  1) set each particle's pBest
		for (int i = 0, count = 0; i < tempSwarm.size(); i+=2, count++)
		{
			Double[] position = tempSwarm.remove(i);

			if(tempSwarm.size() != (swarm.length*2)-1)
				throw new Exception("Invalid tempSwarm size!");

			Callable<Particle> callable =  new CalculateFitnessThread(position, (LinkedList<Double[]>) tempSwarm.clone(), swarm[count]);
			Future<Particle> future = threadPool.submit(callable);
			set.add(future);

			tempSwarm.add(i, position);
		}

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
		}

		threadPool.shutdown();

		return swarm;
	}

	public Particle[] getSwarm(){
		return swarm;
	}
}
