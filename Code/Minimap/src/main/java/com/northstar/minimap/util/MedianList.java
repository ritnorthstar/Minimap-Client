package com.northstar.minimap.util;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by Chris on 4/19/14.
 */
public class MedianList extends LinkedList<Double> {
    private int range;

    public MedianList(int range) {
        this.range = range;
    }

    public boolean add(double d) {
        if (this.size() == range) {
            this.removeFirst();
        }

        this.addLast(d);
        return true;
    }

    public double getMedian() {
        LinkedList<Double> sortedList = new LinkedList<Double>();

        for (double d: this) {
            sortedList.add(d);
        }

        Collections.sort(sortedList);

        int center = (int) Math.floor(this.size() / 2);

        if (this.size() % 2 == 0) {
            return (sortedList.get(center - 1) + sortedList.get(center)) / 2;
        } else {
            return sortedList.get(center);
        }
    }
}
