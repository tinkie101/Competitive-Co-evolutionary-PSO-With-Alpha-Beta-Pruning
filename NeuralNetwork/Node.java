package NeuralNetwork;

import Utils.RandomGenerator;

/**
 * Created by tinkie101 on 2015/03/14.
 */
public class Node {

    private Double[] inputs;
    private Double[] inputWeights;
    private boolean isBias;

    public Node(int numInputs, boolean randomiseWeights)
    {
        inputs = new Double[numInputs];
        inputWeights = new Double[numInputs];

        if(randomiseWeights)
            this.initialiseRandomWeights();
        else
        {
            for(int i = 0; i < inputWeights.length; i++)
            {
                inputWeights[i] = 1.0d;
            }
        }
        isBias = false;
    }

    public Node(){
        inputs = new Double[1];
        inputWeights = new Double[1];

        inputs[0] = -1.0d;
        inputWeights[0] = 1.0d;

        isBias = true;
    }

    public boolean isBias()
    {
        return isBias;
    }

    public void setWeights(Double[] inputWeights) throws Exception {
        if(this.isBias)
            throw new Exception("Cannot alter Bias Node!");

        if (inputs.length != inputWeights.length)
            throw new Exception("Number of Inputs must be the same as Number of weights!");

        //Set input weights
        for (int i = 0; i < inputs.length; i++) {
            this.inputWeights[i] = inputWeights[i];
        }

    }

    public void setInput(Double[] inputs) throws Exception{
//        if (inputs.length != this.inputs.length)
//            throw new Exception("Invalid Inputs!");

        for (int i = 0; i < inputs.length; i++) {
            this.inputs[i] = inputs[i].doubleValue();
        }
    }

    //TODO Initialisation of weights
    private void initialiseRandomWeights()
    {
        Double min = -1.0d / (Math.sqrt(inputWeights.length));
        Double max = 1.0d / (Math.sqrt(inputWeights.length));

        for(int i = 0; i < inputWeights.length; i++)
        {
            inputWeights[i] = RandomGenerator.getInstance().getRandomRangedDoubleValue(min, max);
        }
    }

    public double getOutput()
    {
        if(isBias)
            return getNetInput();

        return activationFunction();
    }

    //TODO use strategy pattern
    private double activationFunction()
    {
        return calculateSigmoid();
    }

    //TODO
    private double calculateSigmoid() {
        double net = getNetInput();
        return 1.0d/(1.0d + Math.exp(-1.0d * net));
    }

    /*
        Calculates the input value for the node
     */
    private double getNetInput(){
        double result = 0.0d;

        for(int i = 0; i < inputs.length; i++)
        {
            result += inputs[i]*inputWeights[i];
        }

        return result;
    }


    public Double[] getWeights()
    {
        Double[] result = new Double[inputWeights.length];

        for(int i = 0; i < inputWeights.length; i++)
        {
            result[i] = inputWeights[i].doubleValue();
        }

        return result;
    }


}
