 package com.baeldung.algorithms.ga.binary;

 /**
 * Classe principale qui implémente un algorithme génétique simple (AG).
 * L'objectif de ce code est de faire évoluer une population d'individus binaires
 * pour qu'ils se rapprochent le plus possible d'une solution cible donnée.
 */
public class SimpleGeneticAlgorithm
 {

//     private static final double uniformRate = 0.5;
//    private static final double flipMutationRate = 0.025;
//    private static final double addMutationRate = 0.01;
//    private static final double removeMutationRate = 0.01;
//    private static final int tournamentSize = 5;
//    //private static final boolean elitism = true;
//    private static final int elitismCount = 3;
//    public static byte[] solution;
//    private static final int maxGenerations = 1000;
//    static final int min_gene_length = 20;
//    static final int max_gene_length = 100;
//    private static final String selection_method = "roulette"; // "tournament" or "roulette"
//    private Individual lastSolution = new Individual(0);



     // ---------- PARAMÈTRES DE L'ALGORITHME ----------

    // Taux de croisement : probabilité qu'un gène vienne de l'un ou l'autre parent (ex : 0.5 = moitié-moitié)
    private double uniformRate;

    // Taux de mutation : probabilité qu'un gène soit inversé (0 → 1 ou 1 → 0)
    private double flipMutationRate;

    // Taux de mutation structurelle : probabilité d’ajouter un nouveau gène
    private double addMutationRate;

    // Taux de mutation structurelle : probabilité de supprimer un gène
    private double removeMutationRate;

    // Taille du tournoi (pour la sélection par tournoi)
    private int tournamentSize;

    // Nombre d'individus "élites" à conserver d'une génération à l'autre (sans modification)
    private int elitismCount;

    // La solution cible que l'algorithme cherche à atteindre (ex. "1010101")
    public static byte[] solution;

    // Nombre maximum de générations avant arrêt
    private int maxGenerations;

    // Longueur minimale et maximale autorisée pour un chromosome (gènes)
    public static int min_gene_length;
    public static int max_gene_length;

    // Méthode de sélection utilisée : "roulette" ou "tournament"
    private String selection_method;

    // Dernière solution trouvée (le meilleur individu de la dernière génération)
    private Individual lastSolution = new Individual(0);

    // ---------- CONSTRUCTEUR PAR DÉFAUT ----------

    /**
     * Initialise l'algorithme avec des paramètres standards.
     * Ces valeurs peuvent ensuite être modifiées via les "setters".
     */
    public SimpleGeneticAlgorithm() {
        this.uniformRate = 0.5;
        this.flipMutationRate = 0.025;
        this.addMutationRate = 0.01;
        this.removeMutationRate = 0.01;
        this.tournamentSize = 5;
        this.elitismCount = 3;
        this.maxGenerations = 1000;
        this.min_gene_length = 20;
        this.max_gene_length = 100;
        this.selection_method = "roulette";
    }

    // ---------- MÉTHODES DE CONFIGURATION (SETTERS) ----------

    // Ces méthodes permettent d’ajuster dynamiquement les paramètres de l’algorithme.
    public void setUniformRate(double uniformRate) { this.uniformRate = uniformRate; }
    public void setFlipMutationRate(double flipMutationRate) { this.flipMutationRate = flipMutationRate; }
    public void setAddMutationRate(double addMutationRate) { this.addMutationRate = addMutationRate; }
    public void setRemoveMutationRate(double removeMutationRate) { this.removeMutationRate = removeMutationRate; }
    public void setTournamentSize(int tournamentSize) { this.tournamentSize = tournamentSize; }
    public void setElitismCount(int elitismCount) { this.elitismCount = elitismCount; }
    public void setMaxGenerations(int maxGenerations) { this.maxGenerations = maxGenerations; }
    public void setMinGeneLength(int min_gene_length) { this.min_gene_length = min_gene_length; }
    public void setMaxGeneLength(int max_gene_length) { this.max_gene_length = max_gene_length; }
    public void setSelectionMethod(String selection_method) { this.selection_method = selection_method; }

    // ---------- MÉTHODE PRINCIPALE DU PROGRAMME ----------

    /**
     * Lance l’algorithme génétique complet.
     * @param populationSize Taille de la population initiale
     * @param solution Chaîne binaire représentant la solution cible
     */
    public boolean runAlgorithm(int populationSize, String solution) {
        // On définit la solution cible sous forme de tableau de bits
        setSolution(solution);

        // On crée la population initiale (individus aléatoires)
        Population myPop = new Population(populationSize, true);

        int generationCount = 1;

        // Boucle principale : exécution jusqu’à atteindre la solution ou la limite de générations
        while (myPop.getFittest().getFitness() < getMaxFitness() && generationCount <= maxGenerations) {
            Individual fittest = myPop.getFittest();
            System.out.println("Generation: " + generationCount + "  Genes corrects: " + getMatchingBits(fittest) +
                    "  Fitness: " + fittest.getFitness());

            // Évolution vers la génération suivante
            myPop = evolvePopulation(myPop);

            // Sauvegarde du meilleur individu de cette génération
            lastSolution = myPop.getFittest();
            generationCount++;
        }

        // Affichage des résultats finaux
        System.out.println("Solution trouvee !");
        System.out.println("Genes corrects : " + getMatchingBits(lastSolution));
        System.out.println("Generations : " + generationCount);
        System.out.println("Meilleur individu : " + myPop.getFittest());

        return true;
    }

    // ---------- ÉTAPE 1 : ÉVOLUTION DE LA POPULATION ----------

    /**
     * Fait évoluer une population complète :
     * 1. Conserve les élites
     * 2. Sélectionne les parents
     * 3. Applique le croisement
     * 4. Applique les mutations
     */
    public Population evolvePopulation(Population pop) {
        int elitismOffset = 0;

        // Nouvelle population vide
        Population newPopulation = new Population(pop.getIndividuals().size(), false);

        // --- 1. ÉLITISME : on garde les meilleurs individus tels quels ---
        if (elitismCount > 0) {
            pop.getIndividuals().sort((a, b) -> Integer.compare(b.getFitness(), a.getFitness()));

            for (int i = 0; i < elitismCount && i < pop.size(); i++) {
                newPopulation.getIndividuals().add(pop.getIndividuals().get(i));
                elitismOffset++;
            }
        }

        // --- 2. SÉLECTION + CROISEMENT ---
        for (int i = elitismOffset; i < pop.getIndividuals().size(); i++) {
            Individual parent1 = selectParent(pop);
            Individual parent2 = selectParent(pop);
            Individual newIndiv = crossover(parent1, parent2);
            newPopulation.getIndividuals().add(i, newIndiv);
        }

        // --- 3. MUTATION ---
        for (int i = elitismOffset; i < newPopulation.getIndividuals().size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    // ---------- ÉTAPE 2 : CROISEMENT (CROSSOVER) ----------

    /**
     * Crée un nouvel individu en combinant les gènes de deux parents.
     * @param indiv1 Parent 1
     * @param indiv2 Parent 2
     * @return Nouvel individu (enfant)
     */
    private Individual crossover(Individual indiv1, Individual indiv2) {
        int minLength = Math.min(indiv1.getGeneLength(), indiv2.getGeneLength());
        Individual newSol = new Individual(minLength);

        // Chaque gène vient aléatoirement de l’un ou l’autre parent selon uniformRate
        for (int i = 0; i < minLength; i++) {
            if (Math.random() <= uniformRate) {
                newSol.setSingleGene(i, indiv1.getSingleGene(i));
            } else {
                newSol.setSingleGene(i, indiv2.getSingleGene(i));
            }
        }

        // Si un parent est plus long, on garde éventuellement quelques gènes supplémentaires
        Individual longerParent = (indiv1.getGeneLength() > indiv2.getGeneLength()) ? indiv1 : indiv2;
        int extraBits = longerParent.getGeneLength() - minLength;

        for (int i = 0; i < extraBits; i++) {
            if (Math.random() < 0.5) {
                newSol.addGene(newSol.getGeneLength(), longerParent.getSingleGene(minLength + i));
            }
        }

        return newSol;
    }

    // ---------- ÉTAPE 3 : MUTATION ----------

    /**
     * Applique des mutations à un individu :
     * - inversion de bits
     * - ajout de gènes
     * - suppression de gènes
     */
    private void mutate(Individual indiv) {
        // Mutation classique (flip 0 ↔ 1)
        for (int i = 0; i < indiv.getGeneLength(); i++) {
            if (Math.random() <= flipMutationRate) {
                byte gene = (byte) Math.round(Math.random());
                indiv.setSingleGene(i, gene);
            }
        }

        // Ajout d’un nouveau gène (mutation structurelle)
        if (Math.random() <= addMutationRate && indiv.getGeneLength() < max_gene_length) {
            int pos = (int)(Math.random() * (indiv.getGeneLength() + 1));
            byte gene = (byte) Math.round(Math.random());
            indiv.addGene(pos, gene);
        }

        // Suppression d’un gène (mutation structurelle inverse)
        if (Math.random() <= removeMutationRate && indiv.getGeneLength() > min_gene_length) {
            int pos = (int)(Math.random() * indiv.getGeneLength());
            indiv.removeGene(pos);
        }
    }

    // ---------- MÉTHODES DE SÉLECTION ----------

    /** Sélection par tournoi : prend un petit groupe d’individus et choisit le plus apte. */
    private Individual tournamentSelection(Population pop) {
        Population tournament = new Population(tournamentSize, false);

        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.getIndividuals().size());
            tournament.getIndividuals().add(i, pop.getIndividual(randomId));
        }

        return tournament.getFittest();
    }

    /** Sélection par roulette : probabilité proportionnelle à la fitness de chaque individu. */
    private Individual rouletteSelection(Population pop) {
        int totalFitness = pop.getIndividuals().stream().mapToInt(Individual::getFitness).sum();
        int roulettePoint = (int) (Math.random() * totalFitness);
        int runningSum = 0;

        for (Individual indiv : pop.getIndividuals()) {
            runningSum += indiv.getFitness();
            if (runningSum >= roulettePoint) {
                return indiv;
            }
        }
        return pop.getIndividual(pop.size() - 1); // Sécurité (si la somme ne dépasse jamais le point)
    }

    /** Sélectionne un parent en fonction de la méthode choisie. */
    private Individual selectParent(Population pop) {
        if (selection_method.equalsIgnoreCase("tournament")) {
            return tournamentSelection(pop);
        } else {
            return rouletteSelection(pop);
        }
    }

    // ---------- CALCUL DE LA FITNESS ----------

    /**
     * Calcule la "fitness" (aptitude) d’un individu :
     * - compare les gènes avec la solution cible
     * - pénalise les différences de longueur
     */
    protected static int getFitness(Individual individual) {
        int minLength = Math.min(individual.getGeneLength(), solution.length);
        int matchingBits = 0;

        for (int i = 0; i < minLength; i++) {
            if (individual.getSingleGene(i) == solution[i]) {
                matchingBits++;
            }
        }

        int extraBits = Math.abs(individual.getGeneLength() - solution.length);
        double rawFitness = matchingBits - (extraBits / 2.0);

        // Normalisation : on ramène la valeur sur 10 000
        double normalizedFitness = Math.max(0, rawFitness) / solution.length;
        return (int) Math.round(normalizedFitness * 10000);
    }

    // Fitness maximale (100 %)
    protected int getMaxFitness() { return 9000; }

    // Compte le nombre de bits corrects
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

    // ---------- INITIALISATION DE LA SOLUTION CIBLE ----------

    /** Définit la chaîne binaire cible que les individus doivent atteindre. */
    protected void setSolution(String newSolution) {
        solution = new byte[newSolution.length()];
        for (int i = 0; i < newSolution.length(); i++) {
            solution[i] = Byte.parseByte(newSolution.substring(i, i + 1));
        }
    }
}
