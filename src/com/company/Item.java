package com.company;

public class Item {
    private int numberOfItem;
    private int prise;
    private int weight;

    Item(int numberOfItem, int prise, int weight) {
        this.numberOfItem = numberOfItem;
        this.prise = prise;
        this.weight = weight;
    }

    public int getWeight() { return weight; }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Предмет №");
        sb.append(numberOfItem);
        sb.append(" Стоимость: ").append(prise);
        sb.append(" Вес: ").append(weight);
        return sb.toString();
    }

    public int getPrise() {
        return prise;
    }
}
