package com.bourge.neat;

public class FitnessGenomePair {
    private final NeatNetwork genome;
    private double fitness;

    public FitnessGenomePair(NeatNetwork genome, double fitness) {
        this.genome = genome;
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public NeatNetwork getGenome() {
        return genome;
    }

}
