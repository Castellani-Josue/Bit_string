package com.baeldung.algorithms.ga.binary;

public class SimpleGeneticAlgorithm {

    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.025;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;
    public static byte[] solution;
    private static final int maxGenerations = 100;

    private static final String SELECTION_METHOD = "tournament"; // "tournament" or "roulette"


    public boolean runAlgorithm(int populationSize, String solution) {
//        if (solution.length() != SimpleGeneticAlgorithm.solution.length) {
//            throw new RuntimeException("The solution needs to have " + SimpleGeneticAlgorithm.solution.length + " bytes");
//        }
        setSolution(solution);
        Population myPop = new Population(populationSize, true);

        int generationCount = 1;
        while (myPop.getFittest().getFitness() < getMaxFitness() && generationCount <= maxGenerations)
        {
            System.out.println("Generation: " + generationCount + " Correct genes found: " + myPop.getFittest().getFitness());
            myPop = evolvePopulation(myPop);
            generationCount++;
        }
        System.out.println("Solution found!");
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes: ");
        System.out.println(myPop.getFittest());
        return true;
    }

    public Population evolvePopulation(Population pop)
    {
        int elitismOffset;
        Population newPopulation = new Population(pop.getIndividuals().size(), false);

        if (elitism) {
            newPopulation.getIndividuals().add(0, pop.getFittest());
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }

        for (int i = elitismOffset; i < pop.getIndividuals().size(); i++) {
//            Individual indiv1 = tournamentSelection(pop);
//            Individual indiv2 = tournamentSelection(pop);
            Individual indiv1 = selectParent(pop);
            Individual indiv2 = selectParent(pop);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.getIndividuals().add(i, newIndiv);
        }

        for (int i = elitismOffset; i < newPopulation.getIndividuals().size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    private Individual crossover(Individual indiv1, Individual indiv2) {
        int length = indiv1.getGeneLength();
        Individual newSol = new Individual(length);
        for (int i = 0; i < length; i++) {
            if (Math.random() <= uniformRate) {
                newSol.setSingleGene(i, indiv1.getSingleGene(i));
            } else {
                newSol.setSingleGene(i, indiv2.getSingleGene(i));
            }
        }
        return newSol;
    }

    private void mutate(Individual indiv) {
        for (int i = 0; i < indiv.getGeneLength(); i++) {
            if (Math.random() <= mutationRate) {
                byte gene = (byte) Math.round(Math.random());
                indiv.setSingleGene(i, gene);
            }
        }
    }

    private Individual tournamentSelection(Population pop) {
        Population tournament = new Population(tournamentSize, false);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.getIndividuals().size());
            tournament.getIndividuals().add(i, pop.getIndividual(randomId));
        }
        Individual fittest = tournament.getFittest();
        return fittest;
    }

    private Individual rouletteSelection(Population pop)
    {
        int totalFitness = pop.getIndividuals().stream().mapToInt(Individual::getFitness).sum();
        int roulettePoint = (int) (Math.random() * totalFitness);
        int runningSum = 0;

        for (Individual indiv : pop.getIndividuals()) {
            runningSum += indiv.getFitness();
            if (runningSum >= roulettePoint) {
                return indiv;
            }
        }
        return pop.getIndividual(pop.size() - 1);
    }


    protected static int getFitness(Individual individual) {
        int fitness = 0;
        int minLength = Math.min(individual.getGeneLength(), solution.length);
        for (int i = 0; i < minLength; i++) {
            if (individual.getSingleGene(i) == solution[i]) {
                fitness++;
            }
        }
        return fitness;
    }

    protected int getMaxFitness() {
        return solution.length;
    }


    protected void setSolution(String newSolution) {
        solution = new byte[newSolution.length()];
        for (int i = 0; i < newSolution.length(); i++) {
            solution[i] = Byte.parseByte(newSolution.substring(i, i + 1));
        }
    }

    private Individual selectParent(Population pop)
    {

        if (SELECTION_METHOD.equalsIgnoreCase("tournament"))
        {
            return tournamentSelection(pop);
        } else {
            return rouletteSelection(pop);
        }
    }


}
