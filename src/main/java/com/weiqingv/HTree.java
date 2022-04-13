package com.weiqingv;

import com.weiqingv.util.SubscriptionStore;

import java.util.*;

public class HTree {
    private final List<String> attributes;
    private final Map<String, List<String>> buckets;
    private final Map<String, List<Cell>> cells;

    public HTree(List<String> attributes, List<Integer> cellNums) {
        assert attributes.size() == cellNums.size();

        this.attributes = attributes;
        this.cells = new HashMap<>();
        this.buckets = new HashMap<>();

        for (int i = 0; i < attributes.size(); i++) {
            String attribute = attributes.get(i);
            int cellNum = cellNums.get(i);
            double cellStep = 1.0 / cellNum;
            double range = 2 * cellStep;
            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < cellNum - 1; j++) {
                double center = j * cellStep + cellStep;
                cellList.add(new Cell(center, range));
            }
            // for subscriptions with no constraints
            cellList.add(new Cell(0.0, 1.0));

            cells.put(attribute, cellList);
        }
    }

    public void insert(Subscription sub) {
        sub.sortConstraints();
        StringBuilder routeBuilder = new StringBuilder();
        for (Subscription.Constraint constraint : sub.getConstraints()) {
            String attribute = constraint.getAttribute();
            List<Cell> cellList = cells.get(attribute);

            // for attribute that without constraint
            if (constraint.getHighValue() == 1.0 && constraint.getLowValue() == 0.0) {
                routeBuilder.append(cellList.size() - 1);
                continue;
            }

            double centerValue = (constraint.getHighValue() + constraint.getLowValue()) * 0.5;
            for (int i = 0; i < cellList.size(); i++) {
                if (!cellList.get(i).contains(centerValue))
                    continue;
                int cellIndex = judgeCellAttribution(attribute, centerValue, i);
                routeBuilder.append(cellIndex);
                break;
            }
        }

        String route = routeBuilder.toString();
        List<String> bucketList;
        if (buckets.containsKey(route)) {
            bucketList = buckets.get(route);
        } else {
            bucketList = new LinkedList<>();
        }
        bucketList.add(sub.getId());
        buckets.put(route, bucketList);
    }

    // judge the value belongs to this cell or the next cell
    public int judgeCellAttribution(String attribute, double value, int index) {
        List<Cell> cellList = cells.get(attribute);
        if (index + 1 >= cellList.size() - 1)
            return index;    // no next index

        if (cellList.get(index).getDistance(value) <= cellList.get(index + 1).getDistance(value))
            return index;
        else
            return index + 1;   // overlap
    }

    public List<String> match(Publication pub) {
        List<String> matchedIDs = new LinkedList<>();
        Map<String, Double> attributes = pub.getAttributes();

        // matched indexes for an attribute
        Map<String, List<Integer>> matchedAttribute = new HashMap<>();
        for (String attribute : attributes.keySet()) {
            double value = attributes.get(attribute);
            List<Cell> cellList = cells.get(attribute);

            List<Integer> indexList = new ArrayList<>();
            for (int i = 0; i < cellList.size(); i++) {
                if (cellList.get(i).contains(value)) {
                    indexList.add(i);
                }
            }
            matchedAttribute.put(attribute, indexList);
        }

        List<StringBuilder> routeList = new LinkedList<>();
        for (Integer index : matchedAttribute.get(this.attributes.get(0))) {
            routeList.add(new StringBuilder(index.toString()));
        }

        for (int i = 1; i < this.attributes.size(); i++) {
            List<StringBuilder> newRouteList = new LinkedList<>();
            for (StringBuilder routeBuilder : routeList) {
                for (Integer index : matchedAttribute.get(this.attributes.get(i))) {
                    routeBuilder.append(index.toString());
                }
                newRouteList.add(routeBuilder);
            }
            routeList = newRouteList;
        }

        for (StringBuilder routeBuilder : routeList) {
            String route = routeBuilder.toString();
            List<String> idList = buckets.get(route);
            if (idList != null)
                matchedIDs.addAll(idList);
        }

        matchedIDs = filterMatchedIDs(matchedIDs, pub);

        return matchedIDs;
    }

    private List<String> filterMatchedIDs(List<String> ids, Publication pub) {
        List<String> matchedIDs = new LinkedList<>();
        for (String id : ids) {
            boolean flag = true;    // indicate whether the value meets all constraints
            Subscription sub = SubscriptionStore.subscriptionMap.get(id);
            for (Subscription.Constraint constraint : sub.getConstraints()) {
                String attribute = constraint.getAttribute();
                double value = pub.getValueByName(attribute);
                if (value < constraint.getLowValue() || value > constraint.getHighValue()) {
                    flag = false;
                    break;
                }
            }
            if (flag) matchedIDs.add(id);
        }
        return matchedIDs;
    }

    public static class Cell {
        private final double center;
        private final double range;

        public Cell(double center, double range) {
            this.center = center;
            this.range = range;
        }

        public boolean contains(double value) {
            return getDistance(value) < 0.5 * range;
        }

        public double getDistance(double value) {
            return Math.abs(center - value);
        }
    }
}
