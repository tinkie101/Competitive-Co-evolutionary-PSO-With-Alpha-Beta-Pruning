package NeuralNetwork;

import Utils.RandomGenerator;

/**
 * Created by tinkie101 on 2015/03/14.
 */
public class NeuralNetwork {
    public static final int MIN_NUM_LAYERS = 3;
    private Layer[] layers;

    public NeuralNetwork(Integer numLayers, Integer[] numLayerNodes, boolean bias ) throws Exception
    {
        if(numLayers < MIN_NUM_LAYERS)
            throw new Exception("Invalid number of Layers (Minimum of 3 required!");

        if(numLayerNodes.length != numLayers)
            throw new Exception("Invalid number of layer nodes!");

        layers = new Layer[numLayers];


        //Initialise each layer
        for(int i = 0; i < numLayers; i++)
        {
            if(i == 0)
                layers[i] = new Layer(numLayerNodes[i], null, bias);
            else if(i >= numLayers-1)
                layers[i] = new Layer(numLayerNodes[i],layers[i-1], false);
            else
                layers[i] = new Layer(numLayerNodes[i], layers[i-1], bias);
        }
    }

    //weights doesn't contain input layers weights.
    //Don't include bias input weights (but include it's output weights)
    public void setWeights(Double[][][] weights) throws Exception{
        if(weights.length != layers.length-1)
            throw new Exception("Invalid weight inputs!");

        //skip the first layer, we don't set it's weights EVER!
        for(int i = 0; i < layers.length-1; i++){
            layers[i+1].setInputWeights(weights[i]);
        }
    }

    public Double[][][] getInputs() throws Exception{
        Double[][][] result = new Double[layers.length][][];

        for(int i = 0; i < layers.length; i++){
            result[i] = layers[i].getInput();
        }

        return result;
    }

    //Don't return the first layer's input weights!
    // Because the first layer's input should be pure and unadjusted.
    //Also don't return bias input weight
    public Double[][][] getWeights() throws Exception{
        Double[][][] result = new Double[layers.length-1][][];

        for(int i = 1; i < layers.length; i++){
            result[i-1] = layers[i].getInputWeights();
        }

        return result;
    }

    public void setInput(Double[][][] input) throws Exception{

        for(int i = 0; i < input.length; i++)
        {
            layers[i].setInput(input[i]);
        }
    }

    public Double[] getOutput(Double[][]inputs) throws Exception{

        Layer previousLayer = null;

        for(int i = 0; i < layers.length; i++) {
            Layer currentLayer = layers[i];
            if(previousLayer == null) {
                currentLayer.setInput(inputs);
            }
            else
            {
                Double[] tempOutput = previousLayer.getOutput();

                Double[][] temp = null;
                if(currentLayer.hasBias())
                    temp = new Double[currentLayer.getNumberOfNodes()-1][];
                else
                    temp = new Double[currentLayer.getNumberOfNodes()][];

                for(int t = 0; t < temp.length; t++)
                {
                    temp[t] = tempOutput;

                }

                currentLayer.setInput(temp);
            }

            previousLayer = currentLayer;
        }

        return previousLayer.getOutput();
    }

    //Print input weights too
    public void printWeights() throws Exception{
        Double[][][] weights = new Double[layers.length][][];

        for(int i = 0; i < layers.length; i++){
            weights[i] = layers[i].getInputWeights();
        }

        for(int i = 0; i < weights.length; i++)
        {
            for(int l = 0; l < weights[i].length; l++){
                for(int k = 0; k < weights[i][l].length; k++){
                    System.out.print(weights[i][l][k] + ", ");
                }
                System.out.println();
            }
            System.out.println("________________________________________________");
        }
    }
}
