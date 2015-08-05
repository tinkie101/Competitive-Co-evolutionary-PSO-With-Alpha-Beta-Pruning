package Main;

import CoEvolution.Coevolution;
import Main.XMLParser.Algorithm;
import Main.XMLParser.Measurements;
import Main.XMLParser.Problem;
import Main.XMLParser.Simulation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tinkie101 on 2015/08/05.
 */
public class Main {
    public static void main(String[] args) throws Exception {


        LinkedList<Algorithm> algorithmList = new LinkedList<>();
        LinkedList<Problem> problemList = new LinkedList<>();
        LinkedList<Simulation> simulationList = new LinkedList<>();
        Measurements measurements = null;


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Load the input XML document, parse it and return an instance of the
        // Document class.
        Document document = builder.parse(new File(args[0]));

        NodeList nodeList = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;

                if (elem.getNodeName().equals("algorithms")) {
                    // Get the value of all sub-elements.
                    NodeList algorithms = elem.getElementsByTagName("algorithm");

                    //Algorithms
                    for (int a = 0; a < algorithms.getLength(); a++) {
                        Node algNode = algorithms.item(a);

                        if (algNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element algElem = (Element) algNode;

                            // Get the value of the ID attribute.
                            String _id = algElem.getAttributes().getNamedItem("id").getNodeValue();

                            // Get the value of the class attribute.
                            String _class = algElem.getAttributes().getNamedItem("class").getNodeValue();

                            algorithmList.add(new Algorithm(_id, _class));
                        }
                    }
                } else if (elem.getNodeName().equals("problems")) {
                    // Get the value of all sub-elements.
                    NodeList problems = elem.getElementsByTagName("problem");

                    //Algorithms
                    for (int p = 0; p < problems.getLength(); p++) {
                        Node probNode = problems.item(p);

                        if (probNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element probElem = (Element) probNode;

                            // Get the value of the ID attribute.
                            String _id = probElem.getAttributes().getNamedItem("id").getNodeValue();

                            // Get the value of the class attribute.
                            String _class = probElem.getAttributes().getNamedItem("class").getNodeValue();

                            problemList.add(new Problem(_id, _class));
                        }
                    }
                } else if (elem.getNodeName().equals("measurements")) {
                    // Get the value of all sub-elements.
                    NodeList measurement = elem.getElementsByTagName("addMeasurement");

                    String _id = elem.getAttributes().getNamedItem("id").getNodeValue();
                    String _class = elem.getAttributes().getNamedItem("class").getNodeValue();
                    int _resolution = Integer.parseInt(elem.getAttributes().getNamedItem("resolution").getNodeValue());

                    measurements = new Measurements(_id, _class, _resolution);

                    //Algorithms
                    for (int m = 0; m < measurement.getLength(); m++) {
                        Node measurementNode = measurement.item(m);

                        if (measurementNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element measurementElem = (Element) measurementNode;

                            // Get the value of the class attribute.
                            String _tempClass = measurementElem.getAttributes().getNamedItem("class").getNodeValue();

                            measurements.addMeasurement(_tempClass);
                        }
                    }
                } else if (elem.getNodeName().equals("simulations")) {
                    // Get the value of all sub-elements.
                    NodeList simulation = elem.getElementsByTagName("simulation");

                    //Algorithms
                    for (int s = 0; s < simulation.getLength(); s++) {
                        Node simulationNode = simulation.item(s);

                        if (simulationNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element simulationElem = (Element) simulationNode;

                            // Get the value of the class attribute.
                            int _samples = Integer.parseInt(simulationElem.getAttributes().getNamedItem("samples").getNodeValue());
                            String tempAlgorithm = simulationElem.getElementsByTagName("algorithm").item(0).getAttributes().getNamedItem("idref").getNodeValue();
                            Algorithm algorithm = null;
                            for (int a = 0; a < algorithmList.size(); a++) {
                                if (algorithmList.get(a).get_Id().equals(tempAlgorithm)) {
                                    algorithm = algorithmList.get(a);
                                    break;
                                }
                            }

                            String tempProblem = simulationElem.getElementsByTagName("problem").item(0).getAttributes().getNamedItem("idref").getNodeValue();
                            Problem problem = null;
                            for (int p = 0; p < problemList.size(); p++) {
                                if (problemList.get(p).get_Id().equals(tempProblem)) {
                                    problem = problemList.get(p);
                                    break;
                                }
                            }

                            String tempOutput = simulationElem.getElementsByTagName("output").item(0).getAttributes().getNamedItem("file").getNodeValue();

                            Simulation tempSimulation = new Simulation(_samples, algorithm, problem, measurements, tempOutput);
                            simulationList.add(tempSimulation);
                        }
                    }
                }

            }
        }


        long startTime = System.currentTimeMillis();
//        //TODO settings!
        double probability = 0.1d;
        Boolean AlphaBeta = false;
        int numEpochs = 500;

        Coevolution coevolution = new Coevolution(AlphaBeta, probability);
        coevolution.runCoevolution(numEpochs);

        long endTime = System.currentTimeMillis();
        System.out.println("\nTook " + ((endTime - startTime) / 1000.0d) + " seconds");
    }
}
