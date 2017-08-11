package com.thinkgem.jeesite.java8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForEach {

    // Normal way to loop a Map
    public void loopForMap() {
        Map<String, Integer> items = new HashMap<>();

        items.put("A", 10);
        items.put("B", 20);
        items.put("C", 30);
        items.put("D", 40);
        items.put("E", 50);
        items.put("F", 60);

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            System.out.println("Item: " + entry.getKey() + " Count: " + entry.getValue());
        }
    }

    public void java8LoopForMap() {
        Map<String, Integer> items = new HashMap<>();
        items.put("A", 10);
        items.put("B", 20);
        items.put("C", 30);
        items.put("D", 40);
        items.put("E", 50);
        items.put("F", 60);

        items.forEach( (k, v) -> System.out.println("Item : " + k + " Count : " + v) );

        items.forEach( (k, v) -> {
            System.out.println("Item : " + k + " Count : " + v);
            if("E".equals(k)){
                System.out.println("Hello E");
            }
        });
    }

    public void java8List() {
        List<String> items = new ArrayList<>();
        items.add("A");
        items.add("B");
        items.add("C");
        items.add("D");
        items.add("E");

        //lambda
        //Output : A,B,C,D,E
        items.forEach(item->System.out.println(item));

        //Output : C
        items.forEach(item->{
            if("C".equals(item)){
                System.out.println(item);
            }
        });

        //method reference
        //Output : A,B,C,D,E
        items.forEach(System.out::println);

        //Stream and filter
        //Output : B
        items.stream()
                .filter(s->s.contains("B"))
                .forEach(System.out::println);

        // List list =  items.stream().filter(s -> s.contains("B"));
    }

    public static void main(String[] args) {
        ForEach forEach = new ForEach();
        // forEach.java8LoopForMap();
        forEach.java8List();
    }
}
