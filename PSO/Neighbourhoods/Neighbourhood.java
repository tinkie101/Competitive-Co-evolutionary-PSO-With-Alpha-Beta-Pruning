package PSO.Neighbourhoods;

import PSO.Particle;

import java.util.LinkedList;

/**
 * Created by tinkie101 on 2015/06/16.
 */
public abstract class Neighbourhood
{
	protected abstract LinkedList<Particle> getNeighbours(Particle particle) throws Exception;

	public abstract void setParticles(Particle[] particles) throws Exception;

	public abstract Particle getNeigbourhoodBest(Particle particle, boolean minimisation) throws Exception;
}
