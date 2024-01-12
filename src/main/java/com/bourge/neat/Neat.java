package com.bourge.neat;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Neat implements Runnable {
    // nombre de réseaux de neurones constituant la population
    private int populationSize;
    // nombre de tours de boucle maximum
    private int maxIter;
    // méthode de calcul de la fitness
    private FitnessCalculator fitnessCalculator;
    // fitness limite à atteindre
    private double solutionFitness;
    // nombre des meilleurs réseaux à conserver après chaque tour de boucles
    private int bestToKeep;
    // taille de la sélection lors de l'utilisation de l'algo de sélection par tournoi
    private int tournamentSize;
    private double crossoverRate;
    private double weightsMutationRate;
    private double connectionMutationRate;
    private double nodeMutationRate;
    private final HashMap<String, Object> results = new HashMap<>();
    // listes contenant la population et leur fitness respective
    private ArrayList<FitnessGenomePair> fitnessGenomePairs = new ArrayList<>();
    private ArrayList<FitnessGenomePair> temp = new ArrayList<>();

    public Neat() {
    }


    private void computeFitness(NeatNetwork[] population) {
        for (NeatNetwork genome : population) {
            double fitnessFuture = fitnessCalculator.calculateFitness(genome);
            FitnessGenomePair pair = new FitnessGenomePair(genome, fitnessFuture);
            fitnessGenomePairs.add(pair);
        }
    }

    @Override
    public void run() {
        fitnessGenomePairs.clear();
        temp.clear();
        Instant t0 = Instant.now();
        NeatNetwork[] population = new NeatNetwork[populationSize];
        // initialisation de la population
        IntStream.range(0, populationSize).forEach(i -> population[i] = new NeatNetwork(new Double[]{0.0, 0.0}, 1));
        computeFitness(population);
        FitnessGenomePair bestFitness = null;
        int i = 0;
        // on vérifie le nombre de boucles et aussi que la meilleure fitness ne dépasse pas l'objectif
        while (i++ < maxIter && (bestFitness = getBestFitnessGenomePair()).getFitness() < solutionFitness) {
            evolve();
            System.out.println("best fitness = " + bestFitness.getFitness() + " -> i =  " + i);
        }
        Instant t1 = Instant.now();
        // calcul du temps effectué par l'algorithme pour se terminer
        var time = t1.toEpochMilli() - t0.toEpochMilli();
        results.put("best-pair", bestFitness);
        results.put("iterations", i - 1);
        results.put("time", (int) time);
    }

    // sélection par tournoi
    private NeatNetwork tournamentSelection() {
        Collections.shuffle(fitnessGenomePairs);
        FitnessGenomePair fitnessGenomePair = fitnessGenomePairs
                .stream()
                .limit(tournamentSize)
                .min((o1, o2) -> Double.compare(o2.getFitness(), o1.getFitness())).orElse(null);
        return Objects.requireNonNull(fitnessGenomePair).getGenome();
    }

    public void evolve() {
        // on conserve les bestToKeep meilleurs
        List<FitnessGenomePair> sortedPairs = fitnessGenomePairs
                .stream()
                .sorted((o1, o2) -> Double.compare(o2.getFitness(), o1.getFitness()))
                .limit(bestToKeep)
                // on incrémente l'age de l'individu
                .peek(pair -> pair.getGenome().gettingOlder())
                .peek(pair -> pair.setFitness(fitnessCalculator.calculateFitness(pair.getGenome())))
                .collect(Collectors.toList());
        temp.addAll(sortedPairs);
        for (int i = bestToKeep; i < populationSize; i++) {
            NeatNetwork genome1 = tournamentSelection();
            NeatNetwork genome2 = tournamentSelection();
            NeatNetwork child = Math.random() <= crossoverRate ? NeatNetwork.crossover(genome1, genome2) : genome1;
            mutate(child);
            double fitness = fitnessCalculator.calculateFitness(child);
            FitnessGenomePair tempGenome = new FitnessGenomePair(child, fitness);
            temp.add(tempGenome);
        }
        fitnessGenomePairs = temp;
        temp = new ArrayList<>();
    }

    // applications des 3 mutations en fonction de leur "rate"
    private void mutate(NeatNetwork network) {
        if (Math.random() <= connectionMutationRate) {
            network.mutateConnection(1);
        }
        if (Math.random() <= weightsMutationRate) {
            network.mutateWeights((int) (network.getConnections().size() * Math.random()));
        }
        if (Math.random() <= nodeMutationRate) {
            network.mutateInsertion(1);
        }
    }

    // récupération du meilleurs génôme
    public FitnessGenomePair getBestFitnessGenomePair() {
        return fitnessGenomePairs.stream()
                .min((o1, o2) -> Double.compare(o2.getFitness(), o1.getFitness())).orElse(null);
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getMaxIter() {
        return maxIter;
    }

    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    public FitnessCalculator getFitnessCalculator() {
        return fitnessCalculator;
    }

    public void setFitnessCalculator(FitnessCalculator fitnessCalculator) {
        this.fitnessCalculator = fitnessCalculator;
    }

    public double getSolutionFitness() {
        return solutionFitness;
    }

    public void setSolutionFitness(double solutionFitness) {
        this.solutionFitness = solutionFitness;
    }

    public int getBestToKeep() {
        return bestToKeep;
    }

    public void setBestToKeep(int bestToKeep) {
        this.bestToKeep = bestToKeep;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public double getWeightsMutationRate() {
        return weightsMutationRate;
    }

    public void setWeightsMutationRate(double weightsMutationRate) {
        this.weightsMutationRate = weightsMutationRate;
    }

    public double getConnectionMutationRate() {
        return connectionMutationRate;
    }

    public void setConnectionMutationRate(double connectionMutationRate) {
        this.connectionMutationRate = connectionMutationRate;
    }

    public double getNodeMutationRate() {
        return nodeMutationRate;
    }

    public void setNodeMutationRate(double nodeMutationRate) {
        this.nodeMutationRate = nodeMutationRate;
    }


    public ArrayList<FitnessGenomePair> getFitnessGenomePairs() {
        return fitnessGenomePairs;
    }

    public HashMap<String, Object> getResults() {
        return results;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

}
