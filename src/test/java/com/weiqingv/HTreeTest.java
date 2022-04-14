package com.weiqingv;

import com.weiqingv.util.SubscriptionStore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HTreeTest {

    public HTree buildHTree() {
        List<String> attributes = new ArrayList<>();
        attributes.add("1");
        attributes.add("2");
        List<Integer> cellNums = new ArrayList<>();
        cellNums.add(4);
        cellNums.add(4);

        HTree tree = new HTree(attributes, cellNums);

        Subscription sub0 = new Subscription("sub0");
        sub0.addConstraint("1", 0.0, 0.1);
        sub0.addConstraint("2", 0.2, 0.3);

        Subscription sub1 = new Subscription("sub1");
        sub1.addConstraint("1", 0.2, 0.3);
        sub1.addConstraint("2", 0.8, 0.9);

        Subscription sub2 = new Subscription("sub2");
        sub2.addConstraint("1", 0.2, 0.3);
        sub2.addConstraint("2", 0.1, 0.2);

        Subscription sub3 = new Subscription("sub3");
        sub3.addConstraint("1", 0.7, 0.8);
        sub3.addConstraint("2", 0.3, 0.4);

        Subscription sub4 = new Subscription("sub4");
        sub4.addConstraint("1", 0.5, 0.6);
        sub4.addConstraint("2", 0.4, 0.5);

        Subscription sub5 = new Subscription("sub5");
        sub5.addConstraint("1", 0.1, 0.2);
        sub5.addConstraint("2", 0.8, 0.9);

        Subscription sub6 = new Subscription("sub6");
        sub6.addConstraint("1", 0.4, 0.5);
        sub6.addConstraint("2", 0.6, 0.7);

        Subscription sub7 = new Subscription("sub7");
        sub7.addConstraint("1", 0.9, 1.0);
        sub7.addConstraint("2", 0.9, 1.0);

        Subscription sub8 = new Subscription("sub8");
        sub8.addConstraint("1", 0.6, 0.7);
        sub8.addConstraint("2", 0.5, 0.6);

        Subscription sub9 = new Subscription("sub9");
        sub9.addConstraint("1", 0.8, 0.9);
        sub9.addConstraint("2", 0.3, 0.4);

        SubscriptionStore.subscriptionMap.put("sub1", sub1);
        SubscriptionStore.subscriptionMap.put("sub2", sub2);
        SubscriptionStore.subscriptionMap.put("sub3", sub3);
        SubscriptionStore.subscriptionMap.put("sub4", sub4);
        SubscriptionStore.subscriptionMap.put("sub5", sub5);
        SubscriptionStore.subscriptionMap.put("sub6", sub6);
        SubscriptionStore.subscriptionMap.put("sub7", sub7);
        SubscriptionStore.subscriptionMap.put("sub8", sub8);
        SubscriptionStore.subscriptionMap.put("sub9", sub9);

        tree.insert(sub1);
        tree.insert(sub2);
        tree.insert(sub3);
        tree.insert(sub4);
        tree.insert(sub5);
        tree.insert(sub6);
        tree.insert(sub7);
        tree.insert(sub8);
        tree.insert(sub9);

        return tree;
    }

    @Test
    public void testInsertAndMatch() {
        HTree tree = buildHTree();

        Publication pub1 = new Publication();
        pub1.addAttribute("1", 0.24);
        pub1.addAttribute("2", 0.82);

        List<String> matchedIDs = tree.match(pub1);

        assert matchedIDs.size() == 1;
        assert matchedIDs.get(0).equals("sub1");
    }

    @Test
    public void testDelete() {
        HTree tree = buildHTree();

        Publication pub1 = new Publication();
        pub1.addAttribute("1", 0.24);
        pub1.addAttribute("2", 0.82);

        tree.delete("sub1");

        List<String> matchedIDs = tree.match(pub1);

        assert matchedIDs.size() == 0;
    }
}