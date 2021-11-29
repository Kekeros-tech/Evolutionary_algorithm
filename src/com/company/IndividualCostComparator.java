package com.company;

import java.util.Comparator;

public class IndividualCostComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual first, Individual second) {
        return second.getCostOfCurrentIndividual() - first.getCostOfCurrentIndividual();
    }

}