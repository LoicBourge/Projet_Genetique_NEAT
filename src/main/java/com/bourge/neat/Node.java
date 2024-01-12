package com.bourge.neat;

import java.util.ArrayList;

public class Node {
    private double output;
    private Boolean calculated = null;
    private final String id;
    // true = neurone est un neurone d'entrée ou de sortie, false = neurone est un neurone caché
    private boolean edge;
    private final ArrayList<Connection> inputConnections = new ArrayList<>();

    public Node(double output, String id) {
        this.output = output;
        this.id = id;
        edge = true;
    }

    public Node(String id) {
        output = 0;
        calculated = false;
        this.id = id;
    }

    //activation function
    public static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.pow(Math.E, -x));
    }

    public boolean isNotMutable() {
        return calculated == null;
    }

    public double calculate() {
        if (calculated != null && !calculated) {
            calculated = true;
            output = Node.sigmoid(inputConnections.stream()
                    .mapToDouble(value -> value.getInputNode().calculate() * value.getWeight())
                    .sum());
        }
        return output;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Connection> getInputLinks() {
        return inputConnections;
    }

    public Node copy() {
        Node node = new Node(id);
        node.output = output;
        node.edge = edge;
        node.calculated = calculated;
        return node;
    }

    public boolean isEdge() {
        return edge;
    }

    public void setEdge(boolean edge) {
        this.edge = edge;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void reset() {
        if (calculated != null) {
            if (calculated)
                calculated = false;
        }
    }

    @Override
    public String toString() {
        return "Neuron{" +
                "output=" + output +
                ", id='" + id + '\'' +
                ", inputConnections=" + inputConnections.size() +
                '}';
    }
}
