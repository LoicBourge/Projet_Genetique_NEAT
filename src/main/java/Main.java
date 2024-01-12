import com.bourge.neat.FitnessGenomePair;
import com.bourge.neat.Neat;
import com.bourge.neat.NeatNetwork;

import java.util.stream.IntStream;

public class Main {

    private static double calculateFitness(NeatNetwork genome)
    {
        double[] expected = {0, 1, 1, 0};
        double[][] outputs = genome.computeOutputs(new double[][]{
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1}
        });
        double fitness = 1.0 / IntStream
                .range(0, expected.length)
                .mapToDouble(i -> expected[i] - outputs[i][0])
                .map(operand -> operand * operand)
                .average().orElse(0);
        if (genome.getHiddenLayer().size() == 1)
            fitness *= 1.5;
        if (genome.getAge() > 10)
            fitness *= 0.5;
        return fitness;
    };

    public static void main(String[] args) {

        Neat neat = new Neat();

        neat.setConnectionMutationRate(0.01);
        neat.setNodeMutationRate(0.01);
        neat.setWeightsMutationRate(0.2);
        neat.setCrossoverRate(0.7);
        neat.setBestToKeep(1);
        neat.setMaxIter(1000);
        neat.setPopulationSize(100);
        neat.setSolutionFitness(100);
        neat.setFitnessCalculator(Main::calculateFitness);
        neat.setTournamentSize(5);
        neat.run();


        var result = neat.getResults();
        FitnessGenomePair bestPair = (FitnessGenomePair) result.get("best-pair");

        bestPair.getGenome().print();


        double[][] inputs = new double[][]{
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1}
        };
        double[][] outputs = bestPair.getGenome().computeOutputs(inputs);
        for (int i = 0; i < outputs.length; i++) {
            System.out.println((int) inputs[i][0] + " xor " + (int) inputs[i][1] + " = " + outputs[i][0] + " => " + (outputs[i][0] > 0.5 ? 1 : 0));
        }
        
        System.out.println("\n" + result.get("iterations") + " iterations in " + result.get("time") + "ms");
        System.out.println("Fitness: " + bestPair.getFitness());
    }
}
