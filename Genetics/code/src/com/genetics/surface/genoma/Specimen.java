package com.genetics.surface.genoma;

import java.util.ArrayList;
import com.genetics.surface.Random;
import com.genetics.surface.Constants;



/**
 * This class implements a Specimen.
 * A specimen us an instance of a population with its own genes.
 *
 */
public class Specimen extends ArrayList<Gene> {

    public enum Source {
        NEW_BLOOD,
        CROSSOVER
    }

    private static final long serialVersionUID = 7535381394105154187L;
    // The value of the fit (heuristic) applied to the specimen 
    private int score;
    // how many times the specimen has been mutated
    private int mutate;
    // how many generations the specimen has survie
    private int survival;
    // Source creation
    private Source source; 


    public int getSurvival() {
        return survival;
    }


    public void setSurvival(int generations) {
        this.survival = generations;
    }


    public void incrementSurvival() {
        this.survival++;
    }


    public int getMutate() {
        return mutate;
    }


    public void setMutate(int mutate) {
        this.mutate = mutate;
    }


    public void incrementMutate(int mutate) {
        this.mutate += mutate++;
    }


    public int getScore() {
        return score;
    }


    public void setScore(int score) {
        this.score = score;
    }


    public Source getSource() {
        return source;
    }


    public void setSource(Source source) {
        this.source = source;
    }


	/**
     * Prints a specimen representation in an array of chars.
     * 
     * @param specimen - A specimen representation.
     */
    private void dump(char[][] specimen) {

        for(int y = Constants.MAX_Y - 1; y >= 0; y--) {
            for(int x = 0; x < Constants.MAX_X; x++) 
                System.out.print(specimen[x][y] + " ");
            System.out.println("");
        }
        System.out.println("");
    }


    /**
     * Updates the print array in positions where an overload occurs. 
     * 
     * @param current in char in current position
     * 
     * @return a returns new char which is the position increment
     */
    private char over(char current) {
        
        if ((current >= 'A') && (current <= 'Z')) return '1';
        return (char) ((int)(current - '0') + 1 + (int) '0');
    }
    
    
    /**
     * Creates a basic matrix representation of the specimen.
     * If a position is occupied by 2 or more genes, it shows
     *  the number of overloaded genes (more than 9 is not supported).
     */
    public void print() {

       char[][] output = new char[Constants.MAX_X] [Constants.MAX_Y];

       for(int x = 0; x < Constants.MAX_X; x ++)
           for(int y = 0; y < Constants.MAX_Y; y++) 
               output[x][y] = '_';

       for(Gene gene: this) {
           char name      = gene.getName();
           int xInitial   = gene.getXInitial();
           int yInitial   = gene.getYInitial();
           int width      = gene.getWidth();
           int height     = gene.getHeight();

           for(int x = xInitial; x < xInitial + width; x++)
               for(int y = yInitial; y < yInitial + height; y++)
                   if (output[x][y] == '_')
                       output[x][y] = name;
                   else
                       output[x][y] = over(output[x][y]);
       }
       dump(output);

       System.out.println("Score:    " + this.getScore());
       System.out.println("Source:   " + this.getSource());
       System.out.println("Survival: " + this.getSurvival());
       System.out.println("Mutate:   " + this.getMutate());
       System.out.println("");
    }


    @Override
    public String toString() {
        return String.valueOf(this.score);
    }

	 
	/**
     * Calculates the overlapping area between 2 genes.
     * 
     * @param genePosition1 - Position for gene 1  i specimen.
     * @param genePosition2 - Position for gene 2 in specimen.
     *  
     * @return - the overlapped area.
     */
    private int calculateOverload(int genePosition1, int genePosition2) {

        Gene gene1 = this.get(genePosition1);
        Gene gene2 = this.get(genePosition2);

        int xIntersection = 0;
        int yIntersection = 0;

        if(gene1.getXInitial() <= gene2.getXInitial()) {
            if (gene1.getXFinal() > gene2.getXInitial())
                xIntersection = Math.min(gene1.getXFinal(), gene2.getXFinal()) - gene2.getXInitial();
        } else {
            if (gene2.getXFinal() > gene1.getXInitial())
                xIntersection = Math.min(gene1.getXFinal(), gene2.getXFinal()) - gene1.getXInitial();
        }

        if(gene1.getYInitial() <= gene2.getYInitial()) {
            if (gene1.getYFinal() > gene2.getYInitial())
                yIntersection = Math.min(gene1.getYFinal(), gene2.getYFinal()) - gene2.getYInitial();
        } else {
            if (gene2.getYFinal() > gene1.getYInitial())
                yIntersection = Math.min(gene1.getYFinal(), gene2.getYFinal()) - gene1.getYInitial();
        }

        return (xIntersection * yIntersection) * ((gene1.getHeight() * gene1.getWidth()) + (gene2.getHeight() * gene2.getWidth()));
    }


    /**
     * Calculates how fit is this Specimen relatively to a final solution.
     * If function return is 0, it means the specimen is a valid solution.
     * As the return value increases, it means the Specimen is further away from a solution. 
     * This particular heuristics calculates overlaps between genes. If so, 
     * the overlap is returned plus the size of gene areas.
     * 
     * @return - fit value (0 is valid solution, as value increase less fit).
     */
    public int heuristic() {

        int genesNumber = this.size();

        int accumulator = 0;
        int overload    = 0;

        for (int outterIndex = 0; outterIndex < genesNumber; outterIndex++) {
            for (int innerIndex = outterIndex + 1; innerIndex < genesNumber; innerIndex++) {

                 int result = calculateOverload(outterIndex, innerIndex);
                 if (result > 0) {
                     overload++;
                     accumulator += result;
                 }
            }
        }

        return accumulator + overload;
    }


    /**
     * Executes the deep copy of the current specimen into a new object with independent memory.
     * 
     * @return - a new specimen, deep copy of current object.
     */
    public Specimen copy() {

        Specimen newSpecimen = new Specimen();

        newSpecimen.setScore(this.score);
        newSpecimen.setSource(this.source);
        newSpecimen.setMutate(this.mutate);
        newSpecimen.setSurvival(this.survival);
        // Deep copy each gene
        this.stream().forEach(gene -> newSpecimen.add(gene.copy()));

        return newSpecimen;
    }


    /**
     * Mutates the specimen, by randomly picking and changing a set of genes
     *  
     * @param specimen - to be mutated.
     * 
     * @return - mutated copy for the specimen.
     */
    public Specimen mutate() {

        Specimen newSpecimen = this.copy();
        int mutations = Random.range(Constants.MAX_GENE_MUTATIONS) + 1;

        for (int index = 0; index < mutations; index++) {
            int randomGene = Random.range(this.size()); 
            newSpecimen.incrementMutate(mutations);
            Gene gene = newSpecimen.get(randomGene);
            newSpecimen.set(randomGene, Gene.build(gene.getName(), Constants.MAX_X, Constants.MAX_Y, gene.getWidth(), gene.getHeight()));
         }

        newSpecimen.incrementSurvival();
        newSpecimen.setScore(newSpecimen.heuristic());
        return newSpecimen;
    }


    /**
     * Crossover of 2 Specimens, generating a child Specimen. 
     * The new specimen receives randomly genes from one of the parents.
     *  
     * @param specimenB - 2nd specimen, the first is the object itself;
     * @param mutationPorbability - probability to introduce mutation during gene copy.
     * 
     * @return - a crossed new Specimen, with possible mutations.
     */
    public Specimen crossover(Specimen specimenB, double mutationPorbability, int maxX, int maxY) {

        int numberGenes = this.size();

        Specimen newSpecimen = new Specimen();

        for(int index = 0; index < numberGenes; index++) {
            if (Random.choice())
                newSpecimen.add(this.get(index).mutate(mutationPorbability, maxX, maxY));
            else
                newSpecimen.add(specimenB.get(index).mutate(mutationPorbability, maxX, maxY));
        }

        newSpecimen.setSurvival(0);
        newSpecimen.setSource(Source.CROSSOVER);
        newSpecimen.setScore(newSpecimen.heuristic());
        return newSpecimen;
    }


    /**
     * Static builder to create a new specimen for the specific grid elements.
     * TODO: In a realistic scenario it is advisable to receive a list with the rectangle figures to generate.
     *  
     * 
     * @return - a new specimen with the required figures. The figures are randomly distributed, 
     * but valid and with an high probability of overlap. 
     */
    public static Specimen build(Source source) {

        Specimen newSpecimen = new Specimen();

        newSpecimen.add(Gene.build('A', Constants.MAX_X, Constants.MAX_Y, 2, 2));
        newSpecimen.add(Gene.build('B', Constants.MAX_X, Constants.MAX_Y, 3, 3));
        newSpecimen.add(Gene.build('C', Constants.MAX_X, Constants.MAX_Y, 2, 1));
        newSpecimen.add(Gene.build('D', Constants.MAX_X, Constants.MAX_Y, 5, 2));
        newSpecimen.add(Gene.build('E', Constants.MAX_X, Constants.MAX_Y, 2, 4)); //  5
        newSpecimen.add(Gene.build('F', Constants.MAX_X, Constants.MAX_Y, 2, 2)); 
        newSpecimen.add(Gene.build('G', Constants.MAX_X, Constants.MAX_Y, 2, 6));
        newSpecimen.add(Gene.build('H', Constants.MAX_X, Constants.MAX_Y, 4, 1));
        newSpecimen.add(Gene.build('I', Constants.MAX_X, Constants.MAX_Y, 2, 2));
        newSpecimen.add(Gene.build('J', Constants.MAX_X, Constants.MAX_Y, 1, 4)); // 10
        newSpecimen.add(Gene.build('K', Constants.MAX_X, Constants.MAX_Y, 2, 3));
        newSpecimen.add(Gene.build('L', Constants.MAX_X, Constants.MAX_Y, 4, 1));
        newSpecimen.add(Gene.build('M', Constants.MAX_X, Constants.MAX_Y, 1, 2));
        newSpecimen.add(Gene.build('N', Constants.MAX_X, Constants.MAX_Y, 3, 2)); 
        newSpecimen.add(Gene.build('O', Constants.MAX_X, Constants.MAX_Y, 1, 5)); // 15
        newSpecimen.add(Gene.build('P', Constants.MAX_X, Constants.MAX_Y, 1, 2));
        newSpecimen.add(Gene.build('Q', Constants.MAX_X, Constants.MAX_Y, 1, 1));
        newSpecimen.add(Gene.build('R', Constants.MAX_X, Constants.MAX_Y, 2, 2));
        newSpecimen.add(Gene.build('S', Constants.MAX_X, Constants.MAX_Y, 1, 1));
        newSpecimen.add(Gene.build('T', Constants.MAX_X, Constants.MAX_Y, 4, 2)); // 20

        newSpecimen.setSource(source);
        newSpecimen.setSurvival(0);
        newSpecimen.setMutate(0);
        newSpecimen.setScore(newSpecimen.heuristic());

        return newSpecimen;
    }

} // class Specimen
