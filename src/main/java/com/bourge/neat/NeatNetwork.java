package com.bourge.neat;

import java.util.*;
import java.util.stream.IntStream;

public class NeatNetwork {
    private final ArrayList<Connection> connections = new ArrayList<>();
    private final Node[] inputLayer;
    private final ArrayList<Node> hiddenLayer = new ArrayList<>();
    private final Node[] outputLayer;
    private int age = 1;

    public NeatNetwork(Double[] inputs, int outputSize) {
        int counter = 0;
        inputLayer = new Node[inputs.length + 1];
        inputLayer[0] = new Node(1.0, String.valueOf(++counter));
        for (int i = 1; i < inputLayer.length; i++) {
            inputLayer[i] = new Node(inputs[i - 1], String.valueOf(++counter));
        }
        outputLayer = new Node[outputSize];
        for (int i = 0; i < outputSize; i++) {
            outputLayer[i] = new Node(String.valueOf(++counter));
            outputLayer[i].setEdge(true);
        }
        initializeLinks();
    }

    public NeatNetwork(Node[] inputs, int lenOutput) {
        inputLayer = new Node[inputs.length];
        outputLayer = new Node[lenOutput];
    }

    public static NeatNetwork crossover(NeatNetwork parent1, NeatNetwork parent2) {
        NeatNetwork child = new NeatNetwork(parent1.inputLayer, parent1.outputLayer.length);
        // map dont la clé = inoveation number et la valeur, les listes des connections affiliées
        HashMap<Integer, ArrayList<Connection>> inovationMap = new HashMap<>();
        setupInovationMap(parent1, inovationMap);
        setupInovationMap(parent2, inovationMap);
        // map permettant de ne pas duppliquer les neurones lors des copies des liens
        HashMap<String, Node> neuronHashMap = new HashMap<>();
        // index de coupure (end essosu de l'index, on récupère les connexions du parent1 sinon celles du parent 2)
        int cut = (int) (Math.random() * inovationMap.size());
        int i = 0;
        for (var connections : inovationMap.values()) {
            Connection connection;
            if (connections.size() == 2) {
                connection = i < cut ?
                        connections.get(0).copy(neuronHashMap, child.hiddenLayer, child.outputLayer, child.inputLayer) :
                        connections.get(1).copy(neuronHashMap, child.hiddenLayer, child.outputLayer, child.inputLayer);
            } else {
                connection = connections.get(0).copy(neuronHashMap, child.hiddenLayer, child.outputLayer, child.inputLayer);
            }
            child.getConnections().add(connection);
            i++;
        }
        return child;
    }

    private static void setupInovationMap(NeatNetwork network, HashMap<Integer, ArrayList<Connection>> innovationMap) {
        network.getConnections().forEach(connection -> {
            int in = connection.getInovationNumber();
            if (!innovationMap.containsKey(in))
                innovationMap.put(in, new ArrayList<>());
            var list = innovationMap.get(in);
            if (list.size() < 2)
                list.add(connection);
        });
    }

    public void mutateWeights(int numberOfLinks) {
        IntStream.range(0, numberOfLinks)
                .forEach(i -> connections.get(new Random().nextInt(connections.size())).randomizeWeight());
    }

    public void mutateInsertion(int numberOfInsertions) {
        IntStream.range(0, numberOfInsertions).forEach(i -> {
            Connection connection = connections.get(new Random().nextInt(connections.size()));
            if (connection.getInputNode() != inputLayer[0]) {
                //create neuron to insert
                Node node = new Node(connection.calculateId());
                //link neuron with the old link
                node.getInputLinks().add(connection);
                hiddenLayer.add(node);
                //get the output neuron from old link
                Node outNode = connection.getOutputNode();
                //insert new neuron to the old link as output
                connection.setOutputNode(node);
                InovationNumberProvider.register(connection);
                //create new link and link it with new neuron as input and ad old neuron as output
                Connection newConnection = new Connection(node, outNode);
                outNode.getInputLinks().remove(connection);
                outNode.getInputLinks().add(newConnection);
                InovationNumberProvider.register(newConnection);
                connections.add(newConnection);
            }
        });
    }

    //take a neuron based on global index
    private Node getNode(int index) {
        if (index >= inputLayer.length) {
            if (index >= inputLayer.length + hiddenLayer.size()) {
                return outputLayer[index - (inputLayer.length + hiddenLayer.size())];
            } else {
                return hiddenLayer.get(index - inputLayer.length);
            }
        }
        return inputLayer[index];
    }

    public void mutateConnection(int numberOfConnections) {
        Random random = new Random();
        int totalNeurons = inputLayer.length + outputLayer.length + hiddenLayer.size();
        IntStream.range(0, numberOfConnections).forEach(i -> {
            int r1 = random.nextInt(totalNeurons - 1) + 1;
            int r2 = random.nextInt(totalNeurons - 1) + 1;
            if (r1 != r2) {
                if (r1 > r2)
                    r1 = r2;
                Node n1 = getNode(r1);
                Node n2 = getNode(r2);
                if (inputLayer[0] != n1 && inputLayer[0] != n2) {
                    final Node temp = n1;
                    if (!n2.isNotMutable() && n2.getInputLinks().stream().noneMatch(connection -> connection.getInputNode() == temp)) {
                        if (!Objects.equals(n1.getId(), n2.getId())) {
                            Connection connection = new Connection(n1, n2);
                            InovationNumberProvider.register(connection);
                            n2.getInputLinks().add(connection);
                            connections.add(connection);
                        }
                    }
                }
            }
        });
    }

    private void initializeLinks() {
        for (Node in : inputLayer) {
            for (Node out : outputLayer) {
                Connection connection = new Connection(in, out);
                InovationNumberProvider.register(connection);
                connections.add(connection);
                out.getInputLinks().add(connection);
            }
        }
    }

    public double[] computeOutput() {
        double[] values = new double[outputLayer.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = outputLayer[i].calculate();
        }
        for (int i = 0; i < values.length; i++) {
            outputLayer[i].reset();
        }
        for (Node hiddenNode : hiddenLayer) {
            hiddenNode.reset();
        }

        return values;
    }

    public double[] computeOutput(double[] inputs) {
        for (int i = 1; i < inputs.length + 1; i++) {
            inputLayer[i].setOutput(inputs[i - 1]);
        }

        return computeOutput();
    }

    public double[][] computeOutputs(double[][] inputsSet) {
        double[][] outputs = new double[inputsSet.length][];
        for (int i = 0; i < inputsSet.length; i++) {
            outputs[i] = computeOutput(inputsSet[i]);
        }
        return outputs;
    }

    public void print() {
        System.out.println("\nInput neurons: " + inputLayer.length);
        Arrays.stream(inputLayer).forEach(System.out::println);
        System.out.println("\nHidden neurons: " + hiddenLayer.size());
        hiddenLayer.forEach(System.out::println);
        System.out.println("\nOutput neurons: " + outputLayer.length);
        Arrays.stream(outputLayer).forEach(System.out::println);
        System.out.println("\nLinks: " + connections.size());
        connections.forEach(System.out::println);
        System.out.println("\nAge: " + age);
    }

    public int getAge() {
        return age;
    }

    public void gettingOlder() {
        age++;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public Node[] getInputLayer() {
        return inputLayer;
    }

    public ArrayList<Node> getHiddenLayer() {
        return hiddenLayer;
    }

    public Node[] getOutputLayer() {
        return outputLayer;
    }
}
