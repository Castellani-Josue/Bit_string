package com.baeldung.algorithms.ga.binary;


public class Individual {


    private byte[] genes;
    private int fitness = 0;

    public Individual(int length) {
        genes = new byte[length];
        for (int i = 0; i < genes.length; i++) {
            genes[i] = (byte) Math.round(Math.random());
        }
    }

    public int getGeneLength() {
        return genes.length;
    }

    protected byte getSingleGene(int index) {
        return genes[index];
    }

    protected void setSingleGene(int index, byte value) {
        genes[index] = value;
        fitness = 0;
    }

    public int getFitness() {
        if (fitness == 0) {
            fitness = SimpleGeneticAlgorithm.getFitness(this);
        }
        return fitness;
    }

    @Override
    public String toString() {
        StringBuilder geneString = new StringBuilder();
        for (int i = 0; i < genes.length; i++) {
            geneString.append(getSingleGene(i));
        }
        return geneString.toString();
    }

}
