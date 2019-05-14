package com.genetics.surface;

import java.util.List;
import java.util.ArrayList;
import com.genetics.surface.genoma.Specimen;


public class Manager {


    public static void solutionAndExit(int generation, Specimen solution) {
        System.out.println("Solution found at generation " + generation + "!");
        solution.print();
        System.exit(0);
    }


    public static void run(int populationSize, int maxGenerations, double mutationProbability, int printInterval) {

        // create initial population
        List<Specimen> population = new ArrayList<Specimen>();
        population = Population.initial(populationSize);

        int generation = 0;

        while(generation < maxGenerations) {

        	// Solution found!!!
            if (population.get(0).getScore() == 0) solutionAndExit(generation, population.get(0));

            // Print output, periodically
            if (generation % printInterval == 0) Population.print(generation, population, Constants.PRINT_SPECIMEN);

            // Generate 50% from crossing with mutation
            List<Specimen> crossed = Population.crossover(population, mutationProbability, Constants.MAX_X, Constants.MAX_Y);

            // Mutate best elements
            List<Specimen> mutatedBest = Population.mutateBest(population, Constants.MAX_BEST, Constants.MAX_MUTATIONS);

            // # of used mutate best changes per cycle, avoiding overfit
            mutatedBest = Population.sort(mutatedBest).subList(0, Constants.BEST_CYCLE - (generation % Constants.BEST_CYCLE));

            // Copy all
            population.clear();
            population.addAll(mutatedBest);
            population.addAll(crossed);

            // max to live
            population = Population.trim(population, Constants.MAX_TO_LIVE);

            // add NEW_BLOOD elements until reaching max population
            population.addAll(Population.initial(populationSize - population.size()));

            // sort population for next generation processing
            population = Population.sort(population);

            // add generation
            generation++;
         }

        System.out.println("Finished! Best results are:");
        for (int index = 0; index < 20; index++)
            population.get(index).print();
    }


    public static void main(String[] args) {

        int populationSize = Integer.parseInt(args[0]);
        int maxGenerations = Integer.parseInt(args[1]);
        double mutationProbability = Double.parseDouble(args[2]);
        int printInterval = Integer.parseInt(args[3]);

        run(populationSize, maxGenerations, mutationProbability, printInterval);
    }

} // class Manager
