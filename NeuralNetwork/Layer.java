package NeuralNetwork;

/**
 * Created by tinkie101 on 2015/03/14.
 */
public class Layer {

    private Node[] nodes;
    private boolean bias;
    private Layer previousLayer;
    private Double[][] input;

    public Layer(int numNodes, Layer previousLayer, boolean bias)
    {
        this.previousLayer = previousLayer;
        this.bias = bias;

        if(bias){
            this.nodes = new Node[numNodes+1];
        }
        else{
            this.nodes = new Node[numNodes];
        }

        int numInput;

        if(previousLayer != null)
            numInput = previousLayer.getNumberOfNodes();
        else
            numInput = 1;

        for(int i = 0; i < this.nodes.length; i++) {
            if( bias && (i + 1) >= this.nodes.length)
            {
                nodes[i] = new Node();
            }
            else {

                if (previousLayer != null)
                    nodes[i] = new Node(numInput, true);
                else
                    nodes[i] = new Node(numInput, false);
            }
        }

    }

    public void setInput(Double[][] inputs) throws Exception{
        if(bias) {
            if (inputs.length != nodes.length - 1)
                throw new Exception("Invalid input length! (No input for Bias node should be given!)");
        }else
        {
            if (inputs.length != nodes.length)
                throw new Exception("Invalid input length!");
        }

        for(int i = 0; i < inputs.length; i++)
        {
            if(!nodes[i].isBias()){
                nodes[i].setInput(inputs[i]);
            }
            else
                throw new Exception("Cannot set Bias input!");

        }

        this.input = inputs;
    }

    public Double[][] getInput(){
        return this.input;
    }

    public void setInputWeights(Double[][] weights) throws Exception{
        if(bias) {
            if (weights.length != nodes.length - 1)
                throw new Exception("Invalid weights length! (No weight for bias node should be given!)");
        }
        else{
            if (weights.length != nodes.length)
                throw new Exception("Invalid weights length!");
        }

        if(previousLayer == null)
            throw new Exception("Cannot set input layer's weights");

        for(int i = 0; i < weights.length; i++)
        {
            if(!nodes[i].isBias())
                nodes[i].setWeights(weights[i]);
            else
                throw new Exception("Cannot set Bias weight!");
        }
    }

    //Don't return bias input weight
    public Double[][] getInputWeights() throws Exception
    {
        if(bias){
            Double[][] result = new Double[nodes.length-1][];
            for (int i = 0; i < nodes.length-1; i++) {
                if(nodes[i].isBias())
                    throw new Exception("Cannot return bias weights");

                result[i] = nodes[i].getWeights();
            }
            return result;
        }
        else {
            Double[][] result = new Double[nodes.length][];
            for (int i = 0; i < nodes.length; i++) {
                if(nodes[i].isBias())
                    throw new Exception("Cannot return bias weights");

                result[i] = nodes[i].getWeights();
            }
            return result;
        }
    }

    //Includes bias output
    public Double[] getOutput()
    {
        Double[] result = new Double[nodes.length];

        for(int i = 0; i < nodes.length; i++)
        {
            result[i] = nodes[i].getOutput();
        }

        return result;
    }

    //Including bias
    public int getNumberOfNodes()
    {
        return nodes.length;
    }

    public Node getNode(int index) throws Exception
    {
        if(index < 0 || index > nodes.length-1)
            throw new Exception("Invalid node index!");

        return nodes[index];
    }

    public boolean hasBias()
    {
        return bias;
    }
}
