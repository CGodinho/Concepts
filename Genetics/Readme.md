# Genetics

## Introduction

This concept defines a genetic algorithm used to find the best solution to fill a surface with different rectangular tiles.

## Technology

Java version 10, with use of collections and streams.


## Default parametrization

The algorithm runs in a surface of 10 x 10 with 20 different tiles.

Each tile works as a gene.

The following parameters are passed during execution in the following order:

 * **population size** - total number of specimens, per generation;
 * **max generations** - maximum number of generation produced;
 * **mutation probability** - Probability of making a mutation in a gene when a crossover is executed;
 * **print interval** - intervals between printing the best element found, plus the score of the best and worst elements.

 
 ## Genetics Concepts
 
 The algoritm uses the following concepts:
 
 * **Population Generation** - A generation creates specimens within the valid surface with the expected size per tile;
 * **Crossover** - New specimens are crosses, generating a new element with genes from each of the fathers;
 * **Mutation** - A mutation changes a gene. There are mutations performed during crossovers and mutations applied to single Specimens;
 * **Best fit** - Best elements are mutated in order to improve fitness. The number of fitted bets elements is variable in order to allow diversity and avoid over-fit.
 * **Time to live** - Elements mutated have a maximum time to live until are removed from the population.s