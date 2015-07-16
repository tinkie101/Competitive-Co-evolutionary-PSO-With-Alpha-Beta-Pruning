package Utils;

import java.util.Random;

/**
 * Created by tinkie101 on 2015/02/26.
 */
public class RandomGenerator {
    private static RandomGenerator ourInstance = new RandomGenerator();

    public static RandomGenerator getInstance() {
        return ourInstance;
    }

    MersenneTwister randomGenerator;

    private RandomGenerator() {
        randomGenerator = new MersenneTwister();
    }

//    public float getRandomFloatValue() {
//        return randomGenerator.nextFloat();
//    }

    public double getRandomDoubleValue() {
        return randomGenerator.nextDouble(true,true);
    }

	public double getRandomRangedDoubleValue(double min, double max) {
		double result = min + (randomGenerator.nextDouble(true,true) * (max - min));
		return result;
	}

	//included
	public int getRandomRangedIntValue(int max) {
		return randomGenerator.nextInt(max+1);
	}
}
