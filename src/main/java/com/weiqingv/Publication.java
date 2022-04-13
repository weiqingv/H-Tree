package com.weiqingv;

import java.util.*;

public class Publication {
    private final Map<String, Double> attributes;

    public Publication() {
        this.attributes = new HashMap<>();
    }

    public void addAttribute(String name, Double value) {
        attributes.put(name, value);
    }

    public Map<String, Double> getAttributes() {
        return attributes;
    }

    public double getValueByName(String name) {
        if (attributes.containsKey(name))
            return attributes.get(name);
        else
            throw new NoSuchElementException();
    }
}
