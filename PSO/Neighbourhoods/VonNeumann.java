package PSO.Neighbourhoods;

import PSO.Particle;

import java.util.LinkedList;

/**
 * Created by tinkie101 on 2015/06/16.
 */
public class VonNeumann extends Neighbourhood
{
	private int x, y, z;
	private Particle[][][] structure;
	private int numParticles;

	public VonNeumann(int x, int y, int z) throws Exception{
		this.numParticles = x * y * z;

		if(x < 1 || y < 1 || z < 0)
			throw new Exception("Invalid structure size!");

		this.x = x;
		this.y = y;
		this.z = z;
		structure = null;
	}

	@Override
	public void setParticles(Particle[] particles) throws Exception
	{
		if(numParticles != particles.length)
			throw new Exception("Invalid number of particles");

		structure = new Particle[x][y][z];

		int count = 0;
		for(int i = 0; i < x; i++){
			for(int l = 0; l < y; l++)
			{
				for(int k = 0; k < z; k++)
				{
					structure[i][l][k] = particles[count++];
				}
			}
		}
	}

	@Override
	protected LinkedList<Particle> getNeighbours(Particle particle) throws Exception
	{
		if(structure == null)
			throw new Exception("Structure not Set!");

		LinkedList<Particle> result = new LinkedList<Particle>();

		result.add(particle);

		for(int i = 0; i < x; i++){
			for(int l = 0; l < y; l++)
			{
				for(int k = 0; k < z; k++)
				{
					if(structure[i][l][k] == particle){
						//calculate neighbours
						if( i - 1 >= 0){
							result.add(structure[i-1][l][k]);
						}

						if(i + 1 < x){
							result.add(structure[i+1][l][k]);
						}

						if( l - 1 >= 0){
							result.add(structure[i][l-1][k]);
						}

						if(l + 1 < y){
							result.add(structure[i][l+1][k]);
						}

						if( k - 1 >= 0){
							result.add(structure[i][l][k-1]);
						}

						if(k + 1 < z){
							result.add(structure[i][l][k+1]);
						}

						if(result.size() < 1 && (x > 1 || y > 1 || z > 1))
							throw new Exception("Couldn't Find any neighbours, but particle should have neighbours!");

						return result;
					}
				}
			}
		}

		throw new Exception("Couldn't find particle!");
	}

	@Override
	public Particle getNeigbourhoodBest(Particle particle, boolean minimisation) throws Exception{
		if(structure == null)
			throw new Exception("Structure not Set!");

		LinkedList<Particle> neighbours = getNeighbours(particle);

		Double tempBestVal;
		Particle tempBestParticle = null;

		if(minimisation)
			tempBestVal = Double.MAX_VALUE;
		else
			tempBestVal = -Double.MAX_VALUE;


		for(int i = 0; i < neighbours.size(); i++)
		{
			Double temp = neighbours.get(i).getFitnessValue();
			if(minimisation){
				if(temp < tempBestVal){
					tempBestVal = temp;
					tempBestParticle = neighbours.get(i);
				}
			}
			else{
				if(temp > tempBestVal){
					tempBestVal = temp;
					tempBestParticle = neighbours.get(i);
				}
			}
		}
		return tempBestParticle;
	}
}
