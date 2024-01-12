package com.bourge.neat;

import java.util.ArrayList;
import java.util.HashMap;

public class Connection {
    private Node inputNode;
    private Node outputNode;
    private double weight;
    private int inovationNumber;

    public Connection(Node inputNode, Node outputNode) {
        this.inputNode = inputNode;
        this.outputNode = outputNode;
        randomizeWeight();
    }

    public String calculateId() {
        return inputNode.getId() + "." + outputNode.getId();
    }

    // initialisation des poids selon une loi normale
    public void randomizeWeight() {
        weight = Math.random() * 4 - 2;
    }

    public Node getInputNode() {
        return inputNode;
    }

    public void setInputNode(Node inputNode) {
        this.inputNode = inputNode;
    }

    public Node getOutputNode() {
        return outputNode;
    }

    public void setOutputNode(Node outputNode) {
        this.outputNode = outputNode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getInovationNumber() {
        return inovationNumber;
    }

    public void setInovationNumber(int inovationNumber) {
        this.inovationNumber = inovationNumber;
    }

    // copy de la connection
    public Connection copy(HashMap<String, Node> nodeHashMap, ArrayList<Node> hiddenNodes, Node[] outputNodes, Node[] inputNodes) {
        boolean newIn = false;
        boolean newOut = false;
        if (!nodeHashMap.containsKey(inputNode.getId())) {
            nodeHashMap.put(inputNode.getId(), inputNode.copy());
            newIn = true;
        }
        if (!nodeHashMap.containsKey(outputNode.getId())) {
            nodeHashMap.put(outputNode.getId(), outputNode.copy());
            newOut = true;
        }
        Node in = nodeHashMap.get(inputNode.getId());
        Node out = nodeHashMap.get(outputNode.getId());
        if (hiddenNodes != null) {
            setupExternalNeuronVector(hiddenNodes, inputNodes, newIn, in);
            setupExternalNeuronVector(hiddenNodes, outputNodes, newOut, out);
        }
        Connection connection = new Connection(in, out);
        out.getInputLinks().add(connection);
        connection.setWeight(weight);
        connection.setInovationNumber(inovationNumber);
        return connection;
    }

    private static void setupExternalNeuronVector(ArrayList<Node> hiddenNodes, Node[] outputNodes, boolean newOut, Node out) {
        if (newOut) {
            if (!out.isEdge())
                hiddenNodes.add(out);
            else {
                for (int i = 0; i < outputNodes.length; i++) {
                    if (outputNodes[i] == null) {
                        outputNodes[i] = out;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Link{" +
                "input=" + inputNode.getId() +
                ", output=" + outputNode.getId() +
                ", weight=" + weight +
                ", innovationNumber=" + inovationNumber +
                '}';
    }
}
