package com.genetics.surface;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import com.genetics.surface.genoma.Specimen;
import com.genetics.surface.genoma.Specimen.Source;


/**
 * Manages all different phases of a Specimen population lifetime.
 *
 */
public class Population {

    public static void print(int generation, List<Specimen> population, int printSize) {

        System.out.println("--------------------------------------------");
        System.out.print("Generation #: " + generation);
        System.out.println(", Size: " + population.size());
        System.out.println("Best: " + population.subList(0, printSize));
        System.out.println("Worst: " + population.subList(population.size() - printSize, population.size()) +"\n");
        population.get(0).print();
    }


    /**
     * Removes elements from the population with a time to live above a configurable value.
     * Time to live represents the number of generation the object exists.
     * The method uses imutability to keep the same logic as the remaining class. Therefore performance is sacrificed. 
     *  
     * @param population - population under analysis;
     * @param time2Live -  time to live.
     * 
     * @return - a new population with all elements with generation below time to live.
     */
    public static List<Specimen> trim(List<Specimen> population, int time2Live) {

        return population.stream().parallel().filter(x -> x.getSurvival() < time2Live).map(Specimen::copy).collect(Collectors.toList());
    }


    /**
     * Sorts elements from the population based on its fit (heuristic) function.
     * The method uses imutability to keep the same logic as the remaining class. Therefore performance is sacrificed. 
     *  
     * @param population - population under analysis;
     * @param time2Live -  time to live.
     * 
     * @return - a new population with all elements sorted by their fit (heuristic) value.
     */
    public static List<Specimen> sort(List<Specimen> population) {

        return population.stream().sorted(Comparator.comparing(Specimen::getScore)).map(Specimen::copy).collect(Collectors.toList());
    }


    /**
     * Creates an initial population of Specimens of a given size.
     * 
     * @param size - Population size.
     * 
     * @return - A new population with given size.
     */
    public static List<Specimen> initial(int size) {
        
        return sort(IntStream.range(0, size).parallel().boxed().map(x -> Specimen.build(Source.NEW_BLOOD)).collect(Collectors.toList()));

    }


    public static List<Specimen> mutateBest(List<Specimen> population, int maxBest, int maxMutations) {

    	return IntStream.rangeClosed(0, maxBest)
    		    .parallel()
    		    .mapToObj(Integer::valueOf)
    		    .flatMap(indexBest -> IntStream.rangeClosed(0, maxMutations) 
    		            .mapToObj(b -> population.get(indexBest).mutate())).collect(Collectors.toList());    		            
    }
    
    
    /**
     * Performs a crossover of a population, where specimens are paired and crossed to origin another specimen.
     *  
     * @param population - current population; 
     * @param mutationPorbability - probability for a gene to mute during generation; 
     * @param maxX - max X grid;
     * @param maxY -  max Y grid.
     * 
     * @return - a crossed new population, half the size of the original.
     */
    public static List<Specimen> crossover(List<Specimen> population, double mutationPorbability, int maxX, int maxY) {

        List<Specimen> nextGeneration = new ArrayList<Specimen>();
        List<Integer> elements = IntStream.range(0, population.size()).boxed().collect(Collectors.toList());

        do {
            int posElement1 = Random.range(elements.size());
            int currentPos1 = elements.get(posElement1);
            elements.remove(posElement1);

            int posElement2 = Random.range(elements.size());
            int currentPos2 = elements.get(posElement2);
            elements.remove(posElement2);

            Specimen pair1 = population.get(currentPos1);
            Specimen pair2 = population.get(currentPos2);

            nextGeneration.add(pair1.crossover(pair2, mutationPorbability, maxX, maxY));

        } while(elements.size() > 0);

        return nextGeneration;
    }

} // class Population