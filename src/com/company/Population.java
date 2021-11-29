package com.company;

import java.util.*;

public class Population {
    private int backpackWeight;
    private ArrayList<Item> itemsToChoose;
    private Individual[] individualsInPopulation;

    public Population(int totalWeight, int populationSize) {
        backpackWeight = totalWeight;
        itemsToChoose = new ArrayList<>();
        individualsInPopulation = new Individual[populationSize];
    }

    public void setItemsToChoose(ArrayList<Item> itemsToChoose) {
        this.itemsToChoose = itemsToChoose;
    }


    public Individual[] getIndividualsInPopulation() {
        return individualsInPopulation;
    }




    public void proportionalSelectionScheme(ArrayList<Individual> potentialPopulation){
        float overlapRatio = 0.5f;

        ArrayList<Float> probabilities = getProbabilitiesByWeightCoefficient(potentialPopulation);

        ArrayList<Individual> swapParents = getSwapParentIndividual(overlapRatio);

        for(int i = 0; i < (int)(overlapRatio * individualsInPopulation.length); i++) {
            swapParents.get(i).setValue(getProportionalChild(probabilities, potentialPopulation));
        }
    }

    public void betaTournamentSimulation(ArrayList<Individual> potentialPopulation) {
        float overlapRatio = 0.5f;

        ArrayList<Individual> tournamentWinners = new ArrayList<>();
        while(tournamentWinners.size() != (int)(overlapRatio * individualsInPopulation.length))
        {
            ArrayList<Individual> tournamentParticipants = getIndividualForSwaps(0.5f, potentialPopulation);
            tournamentWinners.add(getMaxIndividualByWeightCoefficient(tournamentParticipants, tournamentWinners));
        }

        ArrayList<Individual> swapParents = getSwapParentIndividual(overlapRatio);

        int iteration = 0;
        for(Individual tournamentWinner: tournamentWinners){
            swapParents.get(iteration).setValue(tournamentWinner);
            iteration++;
        }
    }




    public static float getCostOfPotentialPopulation(ArrayList<Individual> potentialPopulation){
        float costOfPotentialPopulation = 0 ;
        for(int i = 0; i < potentialPopulation.size(); i++) {
            costOfPotentialPopulation += potentialPopulation.get(i).getCostOfCurrentIndividual();
        }
        return costOfPotentialPopulation;
    }

    public float getWeightCoefficient(ArrayList<Individual> potentialPopulation) {
        float weightCoefficient = 0;
        for(Individual currentIndividual: potentialPopulation) {
            float difference = Math.abs(backpackWeight - currentIndividual.getWeightOfCurrentIndividual());
            difference = (difference == 0) ? 0.5f : difference;
            if(!Arrays.asList(individualsInPopulation).contains(currentIndividual)) {
                weightCoefficient += Math.pow(currentIndividual.getCostOfCurrentIndividual(), 2) / difference;
            }
        }
        return weightCoefficient;
    }



    public ArrayList<Float> getProbabilitiesByWeightCoefficient(ArrayList<Individual> potentialPopulation) {
        ArrayList<Float> probabilities = new ArrayList();
        float weightCoefficient = getWeightCoefficient(potentialPopulation);
        float proportion = 0;

        for(Individual currentIndividual : potentialPopulation){
            float difference = Math.abs(backpackWeight - currentIndividual.getWeightOfCurrentIndividual());
            difference = (difference == 0) ? 0.5f : difference;
            if(!Arrays.asList(individualsInPopulation).contains(currentIndividual)){
                proportion += (Math.pow(currentIndividual.getCostOfCurrentIndividual(), 2) / difference) / weightCoefficient;
                probabilities.add(proportion);
            }
        }
        return probabilities;
    }

    public Individual getProportionalChild(ArrayList<Float> probabilities, ArrayList<Individual> potentialPopulation){
        double probabilityValue = Math.random();
        for(int i = 0; i < probabilities.size() - 1; i++) {
            if(probabilities.get(i) <= probabilityValue && probabilityValue < probabilities.get(i + 1)) {
                return potentialPopulation.get(i);
            }
        }
        return potentialPopulation.get(potentialPopulation.size() - 1);
    }




    public ArrayList<Individual> getIndividualForSwaps(float selectionRate, ArrayList<Individual> potentialPopulation) {
        ArrayList<Individual> individualsForSwap = new ArrayList<>();
        int kolindex = 0;
        while(kolindex != (int)(selectionRate * individualsInPopulation.length)) {
            int index = (int) (Math.random() * individualsInPopulation.length);
            if(individualsForSwap.contains(potentialPopulation.get(index))) {
                continue;
            }
            individualsForSwap.add(potentialPopulation.get(index));
            kolindex++;
        }
        return individualsForSwap;
    }

    public ArrayList<Individual> getSwapParentIndividual(float selectionRate) {
        ArrayList<Individual> individualsForSwap = new ArrayList<>();
        int kolindex = 0;
        Individual maxIndividual = getMaxIndividualWhichCanFitInBackpack(individualsInPopulation);
        while(kolindex != (int)(selectionRate * individualsInPopulation.length)) {
            int index = (int) (Math.random() * individualsInPopulation.length);
            if(individualsForSwap.contains(individualsInPopulation[index]) || maxIndividual == individualsInPopulation[index]) {
                continue;
            }
            individualsForSwap.add(individualsInPopulation[index]);
            kolindex++;
        }
        return individualsForSwap;
    }




    public Individual getMaxIndividual(Individual[] searchPopulation) {
        Individual maxIndividual = new Individual(itemsToChoose.size());
        maxIndividual.setCostOfCurrentIndividual(Integer.MIN_VALUE);
        for(int i = 1; i < searchPopulation.length; i++) {
            if(searchPopulation[i].getCostOfCurrentIndividual() > maxIndividual.getCostOfCurrentIndividual() ) {
                maxIndividual = searchPopulation[i];
            }
        }
        return maxIndividual;
    }

    public Individual getMaxIndividualWhichCanFitInBackpack(Individual[] searchPopulation) {
        Individual maxIndividual = new Individual(itemsToChoose.size());
        maxIndividual.setCostOfCurrentIndividual(Integer.MIN_VALUE);
        for(int i = 0; i < searchPopulation.length; i++) {
            if(searchPopulation[i].getCostOfCurrentIndividual() > maxIndividual.getCostOfCurrentIndividual() && searchPopulation[i].fitsInPopulation(backpackWeight) ) {
                maxIndividual = searchPopulation[i];
            }
        }
        return maxIndividual;
    }

    public Individual getMaxIndividualByWeightCoefficient(ArrayList<Individual> searchPopulation, ArrayList<Individual> previousWinners) {
        Individual maxIndividual = null;
        float maxWeightCoefficient = 0;
        float weightCoefficient = getWeightCoefficient(searchPopulation);

        for(Individual currentIndividual: searchPopulation) {
            if(!previousWinners.contains(currentIndividual) && !Arrays.asList(individualsInPopulation).contains(currentIndividual)) {
                float difference = Math.abs(backpackWeight - currentIndividual.getWeightOfCurrentIndividual());
                difference = (difference == 0) ? 0.5f : difference;
                float buffer = ((float) Math.pow(currentIndividual.getCostOfCurrentIndividual(), 2) / difference) / weightCoefficient;
                if(maxWeightCoefficient < buffer ) {
                    maxIndividual = currentIndividual;
                    maxWeightCoefficient = buffer;
                }
            }
        }
        return maxIndividual;
    }



    //2
    public ArrayList<Individual[]> formParentsNegativeAssociativeCrossing() {
        ArrayList<Individual[]> parentCouples = new ArrayList<>();
        Individual[] individuals = individualsInPopulation.clone();
        Arrays.sort(individuals, new IndividualCostComparator());
        int size = individualsInPopulation.length;
        for(int i = 0; i < ((size % 2 == 0) ? size / 2 : size / 2 + 1); i++) {
            Individual[] parents = {individualsInPopulation[i], individualsInPopulation[size - i - 1]};
            parentCouples.add(parents);
        }
        return parentCouples;
    }


    public ArrayList<Individual[]> formParentsRandomSelection() {
        ArrayList<Individual[]> parentСouples = new ArrayList<>();
        for(int i = 0; i < ((individualsInPopulation.length % 2 == 0) ? individualsInPopulation.length / 2 : individualsInPopulation.length / 2 + 1); i++){
            Individual firstParent = individualsInPopulation[(int) (Math.random() * individualsInPopulation.length)];
            Individual secondParent = individualsInPopulation[(int) (Math.random() * individualsInPopulation.length)];
            Individual[] parents = {firstParent, secondParent};
            parentСouples.add(parents);
        }
        return parentСouples;
    }


    //2
    public ArrayList<Individual> formApplicantsSinglePointCrossover(ArrayList<Individual[]> parentCouples) {
        ArrayList<Individual> potentialPopulation = new ArrayList<>();
        for(int i = 0; i < parentCouples.size(); i++) {
            Individual[] parents = parentCouples.get(i);

            Individual child = parents[0].singlePointCrossover(parents[1]);
            child.updateCostAndWeight(itemsToChoose);

            potentialPopulation.add(child);
        }
        return potentialPopulation;
    }

    public ArrayList<Individual> formApplicantsHomogeneousCrossover(ArrayList<Individual[]> parentCouples) {
        ArrayList<Individual> potentialPopulation = new ArrayList<>();
        for(int i = 0; i < parentCouples.size(); i++) {
            Individual[] parents = parentCouples.get(i);

            Individual child = parents[0].homogeneousCrossover(parents[1]);
            child.updateCostAndWeight(itemsToChoose);

            potentialPopulation.add(child);
        }
        return potentialPopulation;
    }



    public void formMutantsSinglePointMutation(ArrayList<Individual> potentialPopulation) {
        for(int i = 0; i < individualsInPopulation.length / 2; i++) {
            Individual mutant = potentialPopulation.get(i).pointMutation();
            mutant.updateCostAndWeight(itemsToChoose);
            potentialPopulation.add(mutant);
        }
    }

    public void formMutantsTwoPointInversion(ArrayList<Individual> potentialPopulation) {
        int firstPoint = (int) (Math.random() * itemsToChoose.size());
        int secondPoint = (int) (Math.random() * (itemsToChoose.size() - firstPoint) + firstPoint);
        for(int i = 0; i < individualsInPopulation.length / 2; i++) {
            Individual mutant = potentialPopulation.get(i).twoPointInversionMutation(firstPoint, secondPoint);
            mutant.updateCostAndWeight(itemsToChoose);
            potentialPopulation.add(mutant);
        }
    }


    //2.0
    public void randomFormInitialPopulation() {
        for(int i = 0; i < individualsInPopulation.length; i++) {
            individualsInPopulation[i] = new Individual(itemsToChoose.size());
            individualsInPopulation[i].randomCreation(itemsToChoose);
        }
    }

    public void greedyShapingFormInitialPopulation() {
        itemsToChoose.sort(new ItemWeightComparator());
        int index = (int)(Math.random() * (itemsToChoose.size()));
        for(int i = 0; i < individualsInPopulation.length; i++) {
            if(i == index) {
                individualsInPopulation[i] = new Individual(itemsToChoose, backpackWeight);
            }
            else
            {
                individualsInPopulation[i] = new Individual(itemsToChoose.size());
                individualsInPopulation[i].randomCreation(itemsToChoose);
            }
        }
    }
}
