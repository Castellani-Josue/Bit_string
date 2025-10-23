package com.baeldung.algorithms.ga.binary;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

public class GeneticAlgorithmUI extends JFrame {

    private JTextField populationSizeField;
    private JTextField solutionField;
    private JTextField uniformRateField;
    private JTextField flipMutationField;
    private JTextField addMutationField;
    private JTextField removeMutationField;
    private JTextField tournamentSizeField;
    private JTextField elitismCountField;
    private JTextField maxGenerationsField;
    private JTextField minGeneLengthField;
    private JTextField maxGeneLengthField;
    private JComboBox<String> selectionMethodBox;

    private JTextArea outputArea;
    private JButton runButton;
    private JButton resetButton;
    private JProgressBar progressBar;

    public GeneticAlgorithmUI() {
        setTitle("Algorithme Genetique - Configuration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initComponents();
        redirectSystemOut();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 250));

        JPanel leftPanel = createFormPanel();
        JPanel rightPanel = createOutputPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setPreferredSize(new Dimension(500, 1000));
        panel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setPreferredSize(new Dimension(520, 0)); // un peu plus large pour la scrollbar


        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Configuration des Parametres");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 100));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createSectionLabel("Population"));
        populationSizeField = addFormField(panel, "Taille de la population:", "50");
        solutionField = addFormField(panel, "Solution cible:", "1011000100000100010000100000100111001000000100000100000000001111");
        panel.add(Box.createVerticalStrut(15));

        panel.add(createSectionLabel("Taux de Mutation"));
        flipMutationField = addFormField(panel, "Flip mutation:", "0.025");
        addMutationField = addFormField(panel, "Ajout de gene:", "0.01");
        removeMutationField = addFormField(panel, "Suppression de gène:", "0.01");
        panel.add(Box.createVerticalStrut(15));

        panel.add(createSectionLabel("Crossover & Selection"));
        uniformRateField = addFormField(panel, "Taux de crossover:", "0.5");

        JPanel selectionPanel = new JPanel(new BorderLayout(10, 5));
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel selectionLabel = new JLabel("Methode de sélection:");
        selectionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        String[] methods = {"roulette", "tournament"};
        selectionMethodBox = new JComboBox<>(methods);
        selectionMethodBox.setFont(new Font("Arial", Font.PLAIN, 13));
        selectionPanel.add(selectionLabel, BorderLayout.NORTH);
        selectionPanel.add(selectionMethodBox, BorderLayout.CENTER);
        panel.add(selectionPanel);

        tournamentSizeField = addFormField(panel, "Taille du tournoi:", "5");
        panel.add(Box.createVerticalStrut(15));

        panel.add(createSectionLabel("l'Eitisme & Generations"));
        elitismCountField = addFormField(panel, "Nombre d'elites:", "3");
        maxGenerationsField = addFormField(panel, "Generations max:", "1000");
        panel.add(Box.createVerticalStrut(15));

        panel.add(createSectionLabel("Longueur des Genes"));
        minGeneLengthField = addFormField(panel, "Longueur minimale:", "20");
        maxGeneLengthField = addFormField(panel, "Longueur maximale:", "100");
        panel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        runButton = new JButton("Executer");
        runButton.setFont(new Font("Arial", Font.BOLD, 14));
        runButton.setBackground(new Color(70, 130, 180));
        runButton.setForeground(Color.BLACK);
        runButton.setFocusPainted(false);
        runButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        runButton.addActionListener(e -> runAlgorithm());

        resetButton = new JButton("Réinitialiser");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        resetButton.setBackground(new Color(220, 220, 230));
        resetButton.setFocusPainted(false);
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetFields());

        buttonPanel.add(runButton);
        buttonPanel.add(resetButton);
        panel.add(buttonPanel);

        return wrapperPanel;

    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(70, 70, 120));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField addFormField(JPanel panel, String labelText, String defaultValue) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 13));

        JTextField textField = new JTextField(defaultValue);
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 220), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(textField, BorderLayout.CENTER);
        panel.add(fieldPanel);

        return textField;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Résultats de l'Exécution");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 100));
        panel.add(titleLabel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        outputArea.setBackground(new Color(250, 250, 252));
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setString("Prêt");
        panel.add(progressBar, BorderLayout.SOUTH);

        return panel;
    }

    private void redirectSystemOut() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                outputArea.append(String.valueOf((char) b));
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    private void runAlgorithm() {
        try {
            // Validation des entrées
            int populationSize = Integer.parseInt(populationSizeField.getText());
            String solution = solutionField.getText();
            double uniformRate = Double.parseDouble(uniformRateField.getText());
            double flipMutation = Double.parseDouble(flipMutationField.getText());
            double addMutation = Double.parseDouble(addMutationField.getText());
            double removeMutation = Double.parseDouble(removeMutationField.getText());
            int tournamentSize = Integer.parseInt(tournamentSizeField.getText());
            int elitismCount = Integer.parseInt(elitismCountField.getText());
            int maxGenerations = Integer.parseInt(maxGenerationsField.getText());
            int minGeneLength = Integer.parseInt(minGeneLengthField.getText());
            int maxGeneLength = Integer.parseInt(maxGeneLengthField.getText());
            String selectionMethod = (String) selectionMethodBox.getSelectedItem();

            // Validation de la solution (seulement 0 et 1)
            if (!solution.matches("[01]+")) {
                JOptionPane.showMessageDialog(this,
                        "La solution doit contenir uniquement des 0 et des 1",
                        "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            runButton.setEnabled(false);
            resetButton.setEnabled(false);
            progressBar.setIndeterminate(true);
            progressBar.setString("Exécution en cours...");
            outputArea.setText("");

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    try {
                        SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm();

                        // Configuration de tous les paramètres
                        ga.setUniformRate(uniformRate);
                        ga.setFlipMutationRate(flipMutation);
                        ga.setAddMutationRate(addMutation);
                        ga.setRemoveMutationRate(removeMutation);
                        ga.setTournamentSize(tournamentSize);
                        ga.setElitismCount(elitismCount);
                        ga.setMaxGenerations(maxGenerations);
                        ga.setMinGeneLength(minGeneLength);
                        ga.setMaxGeneLength(maxGeneLength);
                        ga.setSelectionMethod(selectionMethod);

                        System.out.println("=== Demarrage de l'algorithme genetique ===");
                        System.out.println("Parametres:");
                        System.out.println("  - Taille de population: " + populationSize);
                        System.out.println("  - Solution cible: " + solution);
                        System.out.println("  - Methode de selection: " + selectionMethod);
                        System.out.println("  - Taux de crossover: " + uniformRate);
                        System.out.println("  - Taux de mutation (flip): " + flipMutation);
                        System.out.println("  - Elitisme: " + elitismCount + " individus");
                        System.out.println("  - Generations max: " + maxGenerations);
                        System.out.println("==========================================\n");

                        ga.runAlgorithm(populationSize, solution);

                    } catch (Exception ex) {
                        System.out.println("\n=== ERREUR ===");
                        System.out.println("Message: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    runButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    progressBar.setString("Terminé");
                }
            };

            worker.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: Veuillez entrer des valeurs numériques valides",
                    "Erreur de saisie",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        populationSizeField.setText("50");
        solutionField.setText("1011000100000100010000100000100111001000000100000100000000001111");
        uniformRateField.setText("0.5");
        flipMutationField.setText("0.025");
        addMutationField.setText("0.01");
        removeMutationField.setText("0.01");
        tournamentSizeField.setText("5");
        elitismCountField.setText("3");
        maxGenerationsField.setText("1000");
        minGeneLengthField.setText("20");
        maxGeneLengthField.setText("100");
        selectionMethodBox.setSelectedIndex(0);
        outputArea.setText("");
        progressBar.setValue(0);
        progressBar.setString("Prêt");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            GeneticAlgorithmUI ui = new GeneticAlgorithmUI();
            ui.setVisible(true);
        });
    }
}