package com.genetics.surface.genoma;

import com.genetics.surface.Random;


/**
 * This class implements a block gene.
 * A gene has a name stored as a char, a starting position and a length.
 * As performance improvement, the final positions are not stored in the object.
 * Data represents a rectangular (or square) block in a 2D plan.
 *
 */
public class Gene {

    private char name;
    private int  xInitial;
    private int  yInitial;
    private int  xFinal;
    private int  yFinal;
    private int  width;
    private int  height;


    public Gene(char name, int xInitial, int yInitial, int width, int height) {

        this.name     = name;
        this.xInitial = xInitial;
        this.yInitial = yInitial;
        this.width    = width;
        this.height   = height;
        this.xFinal   = xInitial + width; 
        this.yFinal   = yInitial + height;
    }


    public char getName() {
        return name;
    }


    public void setName(char name) {
        this.name = name;
    }


    public int getXInitial() {
        return xInitial;
    }


    public void setXInitial(int xInitial) {
        this.xInitial = xInitial;
    }


    public int getYInitial() {
        return yInitial;
    }


    public void setYInitial(int yInitial) {
        this.yInitial = yInitial;
    }


    public int getHeight() {
        return height;
    }


    public void setHeight(int height) {
        this.height = height;
    }


    public int getWidth() {
        return width;
    }


    public void setWidth(int width) {
        this.width = width;
    }


    public int getXFinal() {
        return xFinal;
    }

    
    private void setXFinal(int xFinal) {
        this.xFinal = xFinal;
    }


    public int getYFinal() {
        return yFinal;
    }


    private void setYFinal(int yFinal) {
        this.yFinal = yFinal;
    }


    /**
     * Executes a mutation of size positions (if valid) to an X position.
     * Randomly executes the mutation forward or backward by 1 position.
     * 
     * @param size - positions to mutate (if valid).
     */
    private void mutateX(int size) {

        if (Random.choice()) {
            if (xInitial + width +  1 < size) {
                setXInitial(xInitial + 1);
                setXFinal(xFinal + 1);
            }
        } else {
            if (xInitial - 1 >= 0) {
                setXInitial(xInitial - 1);
                setXFinal(xFinal - 1);
            }
        }
    }


    /**
     * Executes a mutation of size positions (if valid) to an Y position.
     * Randomly executes the mutation forward or backward by 1 position.
     * 
     * @param size - positions to mutate (if valid).
     */
    private void mutateY(int size) {

        if (Random.choice()) {
            if (yInitial + height + 1 < size) {
                setYInitial(yInitial + 1);
                setYFinal(yFinal + 1);
            }
        } else {
            if (yInitial - 1 >= 0) {
                setYInitial(yInitial - 1);
                setYFinal(yFinal - 1);
            }
        }
    }


    /**
     * Performs a random mutability in a X and Y position based on a probability.
     * 
     * @param mutationProbability - probability to perform a mutation.
     * 
     * @return - new gene, possible mutated
     */
    public Gene mutate(double mutationProbability, int maxX, int maxY) {

        Gene newGene = this.copy();

        if (Random.probability(mutationProbability))
            newGene.mutateX(maxX);

        if (Random.probability(mutationProbability))
            newGene.mutateY(maxY);

         return this;
    }


    /**
     * Executes the deep copy of the current gene to a new object with independent memory.
     * 
     * @return - a new gene, deep copy of current object.
     */
    public Gene copy() {

        return(new Gene(this.name, this.xInitial, this.yInitial, this.width, this.height));
    }


    /**
     * Static builder to create a new gene within the valid boundaries of the
     *  
     * @param name - name defined as a character (used for display);
     * @param xMax - max X position in the grid;
     * @param yMax - max Y position in the grid;
     * @param width - gene width;
     * @param height - gene height.
     * 
     * @return - a new gene with the defined dimensions and randomly (but valid)
     *           created in the grid.
     */
    public static Gene build(char name, int xMax, int yMax, int width, int height) {

        return(new Gene(name, Random.randomize(xMax, width), Random.randomize(yMax, height), width, height));
    }

} // class Gene