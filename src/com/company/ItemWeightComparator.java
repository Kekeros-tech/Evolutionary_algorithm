package com.company;

import java.util.Comparator;

public class ItemWeightComparator implements Comparator<Item> {

    @Override
    public int compare(Item firstItem, Item secondItem) {
        return secondItem.getPrise() - firstItem.getPrise();
    }

}