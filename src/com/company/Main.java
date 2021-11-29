package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static int getPopulationWeight(File currentFile)
    {
        try {
            Scanner weightScanner = new Scanner(currentFile);
            String weight = weightScanner.nextLine();
            return Integer.parseInt(weight);
        } catch (FileNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
        return 0;
    }

    public static ArrayList<Item> formItemsToAdd(File currentFile) {
        ArrayList<Item> itemsToAdd = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new  FileInputStream(currentFile)))) {
            br.readLine();
            String buffer;
            while((buffer = br.readLine()) != null) {
                String[] characteristic = buffer.split(" ");
                Item currentItem = new Item(Integer.parseInt(characteristic[0]), Integer.parseInt(characteristic[1]),Integer.parseInt(characteristic[2]));
                itemsToAdd.add(currentItem);
            }
            br.close();
            return itemsToAdd;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getControlParameters() {
        System.out.println("Необходимо выбрать сделующие параметры: ");
        System.out.println("Формирование начальной популяции: 0 - случайное задание, 1 - одно решение будет найдено жадным методом");
        System.out.println("Выбор родительских пар: 0 - рандомная генерация, 1 - отрицательное ассоциативное скрещивание");
        System.out.println("Выбор вариантов кроссовера: 0 - одноточеный кроссовер, 1 - однородный кроссовер");
        System.out.println("Выбор варианта мутации: 0 - точечная мутация, 1 - инверсия");
        System.out.println("Выбор варианта селекции: 0 - пропорциональная схема, 1 - бетта - турнир");

        String controlParameters;
        do {
            Scanner input = new Scanner(System.in);
            controlParameters = input.nextLine();
        }while (controlParameters.length() != 5);

        return controlParameters;
    }

    public static void printCurrentPopulation(Population currentPopulation, int order) {
        System.out.println();
        System.out.println((order == -1 ? "Начальная популяция" : order + " поколение") + "----------");
        for(int i = 0; i < currentPopulation.getIndividualsInPopulation().length; i++) {
            System.out.println(currentPopulation.getIndividualsInPopulation()[i]);
        }
    }

    public static Individual findMaximumPrintReasoning(Population currentPopulation) {
        System.out.println("Особь с лучшей приспособленность: " +
                currentPopulation.getMaxIndividual(currentPopulation.getIndividualsInPopulation()));

        Individual maxIndividual = currentPopulation.getMaxIndividualWhichCanFitInBackpack(currentPopulation.getIndividualsInPopulation());
        System.out.println("Особь с лучше приспособленностью, которая помещается в рюкзак" + maxIndividual);

        return maxIndividual;
    }

    public static void printResultOfWork(ArrayList<Item> itemsInBackpack) {
        System.out.println();
        System.out.println("Состав рюкзака: ");
        for (Item currentItem: itemsInBackpack) {
            System.out.println(currentItem);
        }
    }

    public static Individual algorithmWork(Population population, int countOfRepetitions, String controlParameters) {
        if(controlParameters.charAt(0) == '0'){
            population.randomFormInitialPopulation();
        }
        else {
            population.greedyShapingFormInitialPopulation();
        }
        System.out.println("Начальная популяция: ");
        printCurrentPopulation(population,-1);

        Individual resultOfWork = findMaximumPrintReasoning(population);;

        for(int i = 0; i < countOfRepetitions; i++) {

            //Формирование родительских пар
            ArrayList<Individual[]> parentCouples;
            if(controlParameters.charAt(1) == '0'){
                parentCouples = population.formParentsRandomSelection();
            }
            else {
                parentCouples = population.formParentsNegativeAssociativeCrossing();
            }

            //Формирование потомков из родительских пар
            ArrayList<Individual> futurePopulation;
            if(controlParameters.charAt(2) == '0') {
                futurePopulation = population.formApplicantsSinglePointCrossover(parentCouples);
            }
            else {
                futurePopulation = population.formApplicantsHomogeneousCrossover(parentCouples);
            }

            //Добавление мутантов
            if(controlParameters.charAt(3) == '0') {
                population.formMutantsSinglePointMutation(futurePopulation);
            }
            else {
                population.formMutantsTwoPointInversion(futurePopulation);
            }

            //Селекция
            if(controlParameters.charAt(4) == '0') {
                population.proportionalSelectionScheme(futurePopulation);
            }
            else {
                population.betaTournamentSimulation(futurePopulation);
            }

            printCurrentPopulation(population, i);
            resultOfWork = findMaximumPrintReasoning(population);
        }
        return resultOfWork;
    }

    public static void main(String[] args) {
        String fileName = "test.txt";
        File currentFile = new File(fileName);

        System.out.println("Какой будет размер популяции? ");
        Scanner input = new Scanner(System.in);
        int populationSize = Integer.parseInt(input.nextLine());

        Population mypopulation = new Population(getPopulationWeight(currentFile), populationSize);

        ArrayList<Item> itemsToAdd = formItemsToAdd(currentFile);
        mypopulation.setItemsToChoose(itemsToAdd);

        String controlParameters = getControlParameters();
        System.out.println("Сколько раз повторять алгоритм? ");
        int countOfRepetition = Integer.parseInt(input.nextLine());

        Individual maxIndividual = algorithmWork(mypopulation, countOfRepetition, controlParameters);

        ArrayList<Item> takenItems = maxIndividual.convertToTakenItems(itemsToAdd);

        printResultOfWork(takenItems);
    }
}