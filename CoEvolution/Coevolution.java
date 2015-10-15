package CoEvolution;

import GameTree.PlayGame;
import Main.XMLParser.Problem;
import NeuralNetwork.NeuralNetwork;
import PSO.Neighbourhoods.Neighbourhood;
import PSO.Neighbourhoods.VonNeumann;
import PSO.PSO;
import PSO.Particle;
import PSO.Problems.CoevolutionProblem;
import Utils.FileHandler;
import java.text.DecimalFormat;

/**
 * Created by tinkie101 on 2015/06/25.
 */
public class Coevolution {
    public static final int NUM_RANDOM_PLAYS = 5;
    public static final int NUM_CONTROL_GAMES = 10000;
    public static final int NUM_RUNS = 1;
    public static int MAX_NUM_MOVES = 50;
    public static int PLY_DEPTH = 2;
    private static int NUM_PARTICLES = 27;

    private static int x = 3, y = 3, z = 3;
    private PSO pso;
    private Boolean AlphaBeta;
    private double probability;

    public Coevolution(Boolean alphaBeta, double probability) throws Exception {
        this.AlphaBeta = alphaBeta;
        this.probability = probability;
    }

    public void runCoevolution(int numEpochs, String output) throws Exception {

        System.out.println(numEpochs + "; " + AlphaBeta + "; " + probability);

        double finalResult = 0.0d;
        StringBuilder stringBuilder = new StringBuilder();
        double time = System.currentTimeMillis();

        //TODO Remove
        stringBuilder.append("# Iteration\n");
        stringBuilder.append("# measurement.pso.Fitness\n");
        stringBuilder.append("# measurement.player1.FMeasure\n");
        stringBuilder.append("# measurement.player2.FMeasure\n");
        stringBuilder.append("# measurement.final.FMeasure\n");


        for (int e = 0; e < NUM_RUNS; e++) {
            stringBuilder.append(e + " ");
            System.out.println("Run " + (e+1) + " of " + NUM_RUNS);
            //1
            CoevolutionProblem problem = new CoevolutionProblem(NUM_RANDOM_PLAYS, MAX_NUM_MOVES, PLY_DEPTH, AlphaBeta, probability);
            Neighbourhood neighbourhood = new VonNeumann(x, y, z);

            pso = new PSO(false, problem, NUM_PARTICLES, neighbourhood);

            int winPlayer1 = 0;
            int losePlayer1 = 0;
            int drawPlayer1 = 0;

            int winPlayer2 = 0;
            int losePlayer2 = 0;
            int drawPlayer2 = 0;

            Particle gBestStart = pso.getGlobalBest();
            double startVal = gBestStart.getPBestValue();


            System.out.println("Training ");
            DecimalFormat df = new DecimalFormat("#.00");
            //2
            for (int i = 0; i < numEpochs; i++) {
                double percentage = (double) i / (double) numEpochs * 100.0d;

                System.out.print("\r" + df.format(percentage) + "%");
                pso.runUpdateStep();
            }
            System.out.print("\r" + df.format(100.0d) + "%");
            System.out.println();

            //3
            Particle gBest = pso.getGlobalBest();
            Double[] position = gBest.getPBestPosition();

            System.out.println("\nStart: " + startVal + "; End: " + gBest.getPBestValue());
            stringBuilder.append(gBest.getPBestValue() + " ");

            //4
            NeuralNetwork tempNeuralNet = problem.getNewNeuralNetwork();
            Double[][][] tempPlayerWeights = tempNeuralNet.getWeights();

            System.out.print("{");
            int count = 0;
            for (int n = 0; n < tempPlayerWeights.length; n++) {
                for (int l = 0; l < tempPlayerWeights[n].length; l++) {
                    for (int k = 0; k < tempPlayerWeights[n][l].length; k++) {
                        tempPlayerWeights[n][l][k] = position[count++];


                        System.out.print(position[count-1] + ", ");

                    }
                }
            }
            System.out.print("}\n");

            tempNeuralNet.setWeights(tempPlayerWeights);

            System.out.println("Playing as Player 1");
            for (int i = 0; i < NUM_CONTROL_GAMES; i++) {
                double percentage = (double) i / (double) NUM_CONTROL_GAMES * 100.0d;
                System.out.print("\r" + df.format(percentage) + "%");

                NeuralNetwork untrainedNeuralNet = problem.getNewNeuralNetwork();

                PlayGame tempGame = new PlayGame(tempNeuralNet, null, PLY_DEPTH, PLY_DEPTH, AlphaBeta, false, MAX_NUM_MOVES, probability);

                int outcome = tempGame.play();

                switch (outcome) {
                    case 0:
                        losePlayer1++;
                        break;
                    case 1:
                        winPlayer1++;
                        break;
                    case 2:
                        drawPlayer1++;
                        break;
                    default:
                        throw new Exception("Invalid result!");
                }
            }
            System.out.print("\r" + df.format(100.0d) + "%");
            System.out.println();

            double Player1WinScore = (double) winPlayer1 / (double) NUM_CONTROL_GAMES * 3.0d;
            double Player1LoseScore = (double) losePlayer1 / (double) NUM_CONTROL_GAMES * 1.0d;
            double Player1DrawScore = (double) drawPlayer1 / (double) NUM_CONTROL_GAMES * 2.0d;
            double Player1Score = Player1DrawScore + Player1LoseScore + Player1WinScore;

            System.out.println(winPlayer1 + "; " + drawPlayer1 + "; " + losePlayer1);


            double tempScore = ((2.0d) * (Player1Score - 1.0d)) / (2.0d);
            System.out.println(tempScore);
            tempScore = tempScore / 2.0d * 100.0d;
            stringBuilder.append(tempScore + " ");

            //5
            System.out.println("Playing as Player 2");

            for (int i = 0; i < NUM_CONTROL_GAMES; i++) {

                double percentage = (double) i / (double) NUM_CONTROL_GAMES * 100.0d;
                System.out.print("\r" + df.format(percentage) + "%");

                NeuralNetwork untrainedNeuralNet = problem.getNewNeuralNetwork();

                PlayGame tempGame = new PlayGame(null, tempNeuralNet, PLY_DEPTH, PLY_DEPTH, false, AlphaBeta, MAX_NUM_MOVES, probability);

                int outcome = tempGame.play();

                switch (outcome) {
                    case 0:
                        winPlayer2++;
                        break;
                    case 1:
                        losePlayer2++;
                        break;
                    case 2:
                        drawPlayer2++;
                        break;
                    default:
                        throw new Exception("Invalid result!");
                }
            }
            System.out.print("\r" + df.format(100.0d) + "%");
            System.out.println();


            double Player2WinScore = (double) winPlayer2 / (double) NUM_CONTROL_GAMES * 3.0d;
            double Player2LoseScore = (double) losePlayer2 / (double) NUM_CONTROL_GAMES * 1.0d;
            double Player2DrawScore = (double) drawPlayer2 / (double) NUM_CONTROL_GAMES * 2.0d;
            double Player2Score = Player2DrawScore + Player2LoseScore + Player2WinScore;


            System.out.println(winPlayer2 + "; " + drawPlayer2 + "; " + losePlayer2);

            tempScore = ((2.0d) * (Player2Score - 1.0d)) / (2.0d);
            System.out.println(tempScore);
            tempScore = tempScore / 2.0d * 100.0d;

            stringBuilder.append(tempScore + " ");

            double finalScore = (Player1Score + Player2Score) / 2.0d;
            finalScore = ((2.0d) * (finalScore - 1.0d)) / (2.0d);
            finalScore = finalScore / 2.0d * 100.0d;
            finalResult += finalScore;
            System.out.println("==========================================");
            System.out.println("Final Score:" + finalScore);
            stringBuilder.append(finalScore);
            System.out.println("==========================================\n");

            FileHandler.writeFile(output, stringBuilder.toString());
        }

       // System.out.println("==========================================");
       // finalResult = finalResult / NUM_RUNS;
       // System.out.println("Final Result:" + finalResult);
    }
}
