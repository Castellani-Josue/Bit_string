package com.baeldung.algorithms.ga.binary;

public class SimpleGeneticAlgorithm {

    private static final double uniformRate = 0.5;
    private static final double flipMutationRate = 0.025;
    private static final double addMutationRate = 0.01;
    private static final double removeMutationRate = 0.01;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;
    public static byte[] solution;
    private static final int maxGenerations = 200;
    static final int min_gene_length = 20;
    static final int max_gene_length = 100;
    private static final String selection_method = "roulette"; // "tournament" or "roulette"


    public boolean runAlgorithm(int populationSize, String solution) {
//        if (solution.length() != SimpleGeneticAlgorithm.solution.length) {
//            throw new RuntimeException("The solution needs to have " + SimpleGeneticAlgorithm.solution.length + " bytes");
//        }
        setSolution(solution);
        Population myPop = new Population(populationSize, true);

        int generationCount = 1;
        while (myPop.getFittest().getFitness() < getMaxFitness() && generationCount <= maxGenerations)
        {
            Individual fittest = myPop.getFittest();
            System.out.println("Generation: " + generationCount + " Correct genes found: " + getMatchingBits(fittest));
            myPop = evolvePopulation(myPop);
            System.out.println(myPop.getFittest());
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
        int minLength = Math.min(indiv1.getGeneLength(), indiv2.getGeneLength());
        Individual newSol = new Individual(minLength);
        for (int i = 0; i < minLength; i++) {
            if (Math.random() <= uniformRate) {
                newSol.setSingleGene(i, indiv1.getSingleGene(i));
            } else {
                newSol.setSingleGene(i, indiv2.getSingleGene(i));
            }
        }

        // gérer les bits supplémentaires du parent le plus long
        Individual longerParent = (indiv1.getGeneLength() > indiv2.getGeneLength()) ? indiv1 : indiv2;
        int extraBits = longerParent.getGeneLength() - minLength;

        for (int i = 0; i < extraBits; i++) {
            if (Math.random() < 0.5) { // 50% chance de garder le bit
                newSol.addGene(newSol.getGeneLength(), longerParent.getSingleGene(minLength + i));
            }
        }

        return newSol;
    }

    private void mutate(Individual indiv) {
        for (int i = 0; i < indiv.getGeneLength(); i++) {
            if (Math.random() <= flipMutationRate) {
                byte gene = (byte) Math.round(Math.random());
                indiv.setSingleGene(i, gene);
            }
        }

        // Ajout d'un gène
       if (Math.random() <= addMutationRate && indiv.getGeneLength() < max_gene_length)
        {
            int pos = (int)(Math.random() * (indiv.getGeneLength() + 1));
            byte gene = (byte) Math.round(Math.random());
            indiv.addGene(pos, gene); // à coder dans Individual
        }

        // Suppression d'un gène
        if (Math.random() <= removeMutationRate && indiv.getGeneLength() > min_gene_length)
        {
            int pos = (int)(Math.random() * (indiv.getGeneLength()));
            indiv.removeGene(pos); // à coder dans Individual

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


    protected static int getFitness(Individual individual)
    {

        int minLength = Math.min(individual.getGeneLength(), solution.length);
        //System.out.println("minLength: " + minLength);

        int matchingBits = 0;
        for (int i = 0; i < minLength; i++)
        {
            Individual individual1 = individual;
            //System.out.println(i + " " + individual1.getGeneLength());
            if (individual.getSingleGene(i) == solution[i])
            {
                matchingBits++;
            }
        }

        int extraBits = Math.abs(individual.getGeneLength() - solution.length);
        double rawFitness = matchingBits - (extraBits / 2.0);

        // Normalisation en pourcentage
        double normalizedFitness = Math.max(0, rawFitness) / solution.length;

        return (int) Math.round(normalizedFitness * 100);
    }

    protected int getMaxFitness() {
        return 100; // puisque maintenant la fitness est un pourcentage
    }

    protected static int getMatchingBits(Individual individual) {
        int minLength = Math.min(individual.getGeneLength(), solution.length);
        int matchingBits = 0;
        for (int i = 0; i < minLength; i++) {
            if (individual.getSingleGene(i) == solution[i]) {
                matchingBits++;
            }
        }
        return matchingBits;
    }


    protected void setSolution(String newSolution) {
        solution = new byte[newSolution.length()];
        for (int i = 0; i < newSolution.length(); i++) {
            solution[i] = Byte.parseByte(newSolution.substring(i, i + 1));
        }
    }

    private Individual selectParent(Population pop)
    {

        if (selection_method.equalsIgnoreCase("tournament"))
        {
            return tournamentSelection(pop);
        } else {
            return rouletteSelection(pop);
        }
    }


}
