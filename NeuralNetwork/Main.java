package NeuralNetwork;

/**
 * Created by tinkie101 on 2015/03/14.
 *
 * For Testing the Neural Network's Feed Forward Phase
 */
public class Main {

    public static final int NUM_LAYERS = 3;
    public static final Integer[] numLayerNodes = {3,2,1};

    public static void main(String[] args) throws Exception
    {
        System.out.println("Testing Neural Network");


        NeuralNetwork neuralNetwork = new NeuralNetwork(NUM_LAYERS, numLayerNodes, true);

        System.out.println("--------------------------------------------------");
        System.out.println("Testing initialisation of weights");
        neuralNetwork.printWeights();

        System.out.println("--------------------------------------------------");
        System.out.println("Testing get weights");
        Double[][][] weights = neuralNetwork.getWeights();

        for(int i = 0; i < weights.length; i++)
        {
            for(int l = 0; l < weights[i].length; l++){
                for(int k = 0; k < weights[i][l].length; k++){
                    weights[i][l][k] = 9.0d;
                }

            }

        }

        System.out.println("--------------------------------------------------");
        System.out.println("Testing set weights");
        neuralNetwork.setWeights(weights);
        neuralNetwork.printWeights();


        System.out.println("--------------------------------------------------");
        System.out.println("Testing set inputs");
        Double[][][] inputs = new Double[NUM_LAYERS][][];

        for(int i =0; i < inputs.length; i++)
        {
            Double[][] temp = new Double[numLayerNodes[i]][];

            for(int l = 0; l < numLayerNodes[i]; l++)
            {
                if(i == 0)
                {
                    temp[l] = new Double[1];
                    temp[l][0] = 0.5d;
                }
                else{
                    temp[l] = new Double[inputs[i-1].length];

                    for(int t = 0; t < temp[l].length; t++){
                        temp[l][t] = 0.5d;
                    }
                }
            }
            inputs[i] = temp;
        }

        neuralNetwork.setInput(inputs);


        System.out.println("--------------------------------------------------");
        System.out.println("Testing get Input");
        inputs = neuralNetwork.getInputs();

        for(int i = 0; i < inputs.length; i++)
        {
            for(int l = 0; l < inputs[i].length; l++)
            {
                for(int k = 0; k < inputs[i][l].length; k++)
                    System.out.print(inputs[i][l][k] + ", ");

                System.out.println();
            }
            System.out.println("_____________________________________________");
        }

        System.out.println("--------------------------------------------------");
        System.out.println("Testing get Output");

        Double[][] testInput = new Double[numLayerNodes[0]][1];

        for(int i = 0; i < testInput.length; i++){
            testInput[i][0] = 1.0d - ((double)i/10.0d);
            System.out.print(testInput[i][0] + ", ");
        }
        System.out.println("\n\nFinal Output: ");


        Double[] output = neuralNetwork.getOutput(testInput);

        for(int i = 0; i < output.length; i++)
        {
            System.out.print(output[i] + "; ");
        }
    }
}
