package com.baeldung.algorithms.ga.binary;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private List<Individual> individuals;



    public Population(int size, boolean createNew) {
        individuals = new ArrayList<Individual>();
        if (createNew) {
            createNewPopulation(size);
        }
    }

    protected Individual getIndividual(int index) {
        return individuals.get(index);
    }

    protected Individual getFittest() {
        Individual fittest = individuals.get(0);
        for (int i = 0; i < individuals.size(); i++) {
            if (fittest.getFitness() <= getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    private void createNewPopulation(int size) {
        for (int i = 0; i < size; i++) {
            int geneLength = SimpleGeneticAlgorithm.min_gene_length
                    + (int)(Math.random() * (SimpleGeneticAlgorithm.max_gene_length - SimpleGeneticAlgorithm.min_gene_length + 1));
            Individual newIndividual = new Individual(geneLength);
            //System.out.println("New individual of length " + newIndividual.getGeneLength());
            individuals.add(i, newIndividual);
        }
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public int size() {
        return individuals.size();
    }
}
