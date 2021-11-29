package com.company;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Individual {
    private boolean[] takenItems;
    private int costOfCurrentIndividual;
    private int weightOfCurrentIndividual;

    public Individual(int encoding) {
        takenItems = new boolean[encoding];
        costOfCurrentIndividual = 0;
        weightOfCurrentIndividual = 0;
    }

    public Individual(Individual copy) {
        takenItems = copy.takenItems.clone();
        costOfCurrentIndividual = copy.costOfCurrentIndividual;
        weightOfCurrentIndividual = copy.weightOfCurrentIndividual;
    }

    public Individual(ArrayList<Item> itemsToChoose, int maxWeight) {
        takenItems = new boolean[itemsToChoose.size()];
        costOfCurrentIndividual = 0;
        weightOfCurrentIndividual = 0;

        int currentWeight = 0;
        for(int i = 0; i < itemsToChoose.size(); i++) {
            if(currentWeight + itemsToChoose.get(i).getWeight() < maxWeight) {
                currentWeight += itemsToChoose.get(i).getWeight();
                takenItems[i] = true;
                costOfCurrentIndividual += itemsToChoose.get(i).getPrise();
                weightOfCurrentIndividual += itemsToChoose.get(i).getWeight();
            }
        }
    }



    public void setCostOfCurrentIndividual(int costOfCurrentIndividual) {
        this.costOfCurrentIndividual = costOfCurrentIndividual;
    }

    public boolean[] getTakenItems() {
        return takenItems;
    }

    public int getCostOfCurrentIndividual() {
        return costOfCurrentIndividual;
    }

    public int getWeightOfCurrentIndividual() { return weightOfCurrentIndividual; }

    public void updateCostAndWeight(ArrayList<Item> itemsToChoose){
        costOfCurrentIndividual = 0;
        weightOfCurrentIndividual = 0;
        for(int i = 0; i < takenItems.length; i++) {
            if(takenItems[i]) {
                costOfCurrentIndividual += itemsToChoose.get(i).getPrise();
                weightOfCurrentIndividual += itemsToChoose.get(i).getWeight();
            }
        }
    }

    public void setValue(Individual otherIndividual) {
        takenItems = otherIndividual.takenItems;
        costOfCurrentIndividual = otherIndividual.costOfCurrentIndividual;
        weightOfCurrentIndividual = otherIndividual.weightOfCurrentIndividual;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("");
        if (takenItems == null) sb.append("null");
        else {
            sb.append(' ');
            for (int i = 0; i < takenItems.length; ++i)
                sb.append(takenItems[i] == true ? 1 : 0);
        }
        sb.append(", Стоимость-").append(costOfCurrentIndividual);
        sb.append(", Вес-").append(weightOfCurrentIndividual);
        return sb.toString();
    }

    public void randomCreation(ArrayList<Item> itemsToChoose) {
        for(int i = 0; i < takenItems.length; i++) {
            takenItems[i] = ((int)(Math.random()*2) == 1) ? true : false;
            if(takenItems[i] == true) {
                costOfCurrentIndividual += itemsToChoose.get(i).getPrise();
                weightOfCurrentIndividual +=itemsToChoose.get(i).getWeight();
            }
        }
    }

    public ArrayList<Item> convertToTakenItems(ArrayList<Item> itemsToChoose) {
        ArrayList<Item> itemsToTake = new ArrayList<>();
        for(int i = 0; i < takenItems.length; i++) {
            if(takenItems[i]) {
                itemsToTake.add(itemsToChoose.get(i));
            }
        }
        return itemsToTake;
    }

    public boolean fitsInPopulation(int maxWeight){
        if(weightOfCurrentIndividual <= maxWeight) {
            return true;
        }
        return false;
    }

    public Individual singlePointCrossover(Individual ofterIndividual) {
        int pointPermutations = (int)(Math.random() * (takenItems.length));
        Individual buffer = new Individual(this);
        System.arraycopy(ofterIndividual.takenItems, pointPermutations, buffer.takenItems, pointPermutations,takenItems.length - pointPermutations);
        return buffer;
    }

    public Individual homogeneousCrossover(Individual ofterIndividual) {
        Individual buffer = new Individual(this);
        for(int i = 0; i < this.takenItems.length; i++) {
            if(buffer.takenItems[i] != ofterIndividual.takenItems[i]) {
                buffer.takenItems[i] = ((int)(Math.random()*2) == 1) ? true : false;
            }
        }
        return buffer;
    }

    public Individual pointMutation() {
        int pointMutation = (int)(Math.random() * (takenItems.length));
        Individual buffer = new Individual(this);
        buffer.takenItems[pointMutation] = !buffer.takenItems[pointMutation];
        return buffer;
    }

    public Individual twoPointInversionMutation(int firstPoint, int secondPoint) {
        Individual buffer = new Individual(this);
        for(int i = firstPoint; i < secondPoint; i++) {
            buffer.takenItems[i] = !buffer.takenItems[i];
        }
        return  buffer;
    }

}
