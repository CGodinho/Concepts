package com.genetics.surface;


/**
 * Multiple random creation of numbers and choices used during processing.
 *
 */
public class Random {

    /**
     * Randomly generates 2 possible states, which are returned as a boolean.
     * 
     * @return - a boolean value with equal distribution.
     */
    public static boolean choice() {

        if (Math.random() < 0.5) return true;
        return false;
    }


    /**
     * Returns true according to a randomly generated probability value. 
     *
     * @param propability - probability [0 .. 1].
     * 
     * @return - boolean representing the probability.
     */
    public static boolean probability(double propability) {
        
        if (Math.random() < propability) return true;
        return false;
    }


     /**
      * Generates a random integer between 0 and the given value - 1.
      * 
     * @param max - given value.
     * 
     * @return - value [0 .. given value - 1].
     */
    public static int range(int max) {

         return (int) (Math.random() * max);
    }


    /**
     * Generates a number, that summed to a given value (size),
     * does not goes over a maximum value (max).
     * 
     * @param max - max have for sum.
     * @param size - the size to fulfill.
     * 
     * @return - value.
     */
    public static int randomize(int max, int size) {
        
        int result;
        do
            result = (int) (Math.random() * max - size + 1);
        while(result < 0);
            return result;
    }


} // class Random
