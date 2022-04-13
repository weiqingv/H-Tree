package com.weiqingv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Subscription {
    String id;
    int size;
    Set<String> attributeNames;
    List<Constraint> constraints;

    public Subscription(String id) {
        this.id = id;
        this.constraints = new ArrayList<>();
        this.attributeNames = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public Constraint getConstraint(int index) {
        return constraints.get(index);
    }

    public void addConstraint(Constraint constraint) {
        String name = constraint.getAttribute();
        if (attributeNames.contains(name))
            throw new IllegalArgumentException("Attribute: " + name + " has been added twice.");
        attributeNames.add(constraint.getAttribute());
        constraints.add(constraint);
    }

    public void addConstraint(String attribute, double lowValue, double highValue) {
        addConstraint(new Constraint(attribute, lowValue, highValue));
    }

    // sort based on attribute name
    public void sortConstraints() {
        constraints.sort((x, y) -> {
            String a = x.attribute;
            String b = y.attribute;
            if (a.equals(b))
                return 0;
            else if (a.length() > b.length()) {
                return 1;
            } else if (a.length() < b.length()) {
                return -1;
            } else {
                return a.compareTo(b);
            }
        });
    }

    public static class Constraint {
        private String attribute;
        private double lowValue;
        private double highValue;

        public Constraint(String attribute, double lowValue, double highValue) {
            this.attribute = attribute;
            this.lowValue = lowValue;
            this.highValue = highValue;
            assert lowValue <= highValue;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public double getLowValue() {
            return lowValue;
        }

        public void setLowValue(double lowValue) {
            this.lowValue = lowValue;
        }

        public double getHighValue() {
            return highValue;
        }

        public void setHighValue(double highValue) {
            this.highValue = highValue;
        }
    }
}
