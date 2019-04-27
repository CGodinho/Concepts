# Daimler Trucks & Buses Tech and Data Hub

This repo contains the answer to the **Daimler :truck: & :bus: Tech and Data Hub Big Data technical challenge** created by Carlos Godinho (cma.godinho@gmail.com) in April 2019.

## Introduction

The main doc, with results and guidelines, is located [in doc/results.pdf](https://github.com/CGodinho/Concepts/tree/master/DTB_Challenge_BigData/doc/results.pdf).
The doc holds the answers and explanations to the proposed tasks. Please follow the document.

## Directories

Repo structure:

* **doc** - documentation, pictures and test results;
* **code** - code with Spark Application (*Consolidation*) and exploratory data analysis R script (*data_analysis.R*)
*  **test** - test environment details and results.

## Conclusion

Thanks for the opportunity!
Hope you enjoy it as I did :+1: 


# Update

Unfortunately the solution was not accepted by Daimler Truck & Buses, being one of the reasons the option to use RDDs.
As an exercise, the most basic code for the Spark SQL API is avaialable at /code/Consolidation_Alternative.
This code is taking 3 minutes to parse, although it misses:

* Complete data validation
* Complete trim of fields
* Handling of directories
* Logging, error and duplicate handling

The new SQL API seems ato be a nice surprise :grin:
