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

    protected void addGene(int index, byte value) {
        byte[] newGenes = new byte[genes.length + 1];

        // copie avant la position
        for (int i = 0; i < index; i++) {
            newGenes[i] = genes[i];
        }

        // insère le nouveau
        newGenes[index] = value;

        // copie le reste
        for (int i = index; i < genes.length; i++) {
            newGenes[i + 1] = genes[i];
        }

        genes = newGenes;
        fitness = 0;
    }

    protected void removeGene(int index)
    {
        byte[] newGenes = new byte[genes.length - 1];
        for (int i = 0, j = 0; i < genes.length; i++) {
            if (i != index) {
                newGenes[j++] = genes[i];
            }
        }
        genes = newGenes;
        fitness = 0;
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
