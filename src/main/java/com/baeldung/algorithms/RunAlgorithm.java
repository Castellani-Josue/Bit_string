package com.baeldung.algorithms;

import com.baeldung.algorithms.ga.binary.SimpleGeneticAlgorithm;

public class RunAlgorithm {

	public static void main(String[] args) {
		SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm();
		//ga.runAlgorithm(50, "1011000100000100010000100000100111001000000100000100000000001111");
        ga.runAlgorithm(50, "10011001101010100001000100001000001001110");
	}
}
