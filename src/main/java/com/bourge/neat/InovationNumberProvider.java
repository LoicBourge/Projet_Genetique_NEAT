package com.bourge.neat;

import java.util.HashMap;

public class InovationNumberProvider {
    private static final HashMap<String, Integer> inovations = new HashMap<>();
    private static int counter = 0;


    // enregistre un nouvel inovation number pour une connection
    public static void register(Connection connection) {
        String id = connection.calculateId();
        Integer in = inovations.putIfAbsent(id, ++counter);
        connection.setInovationNumber(in != null ? in : inovations.get(id));
    }

    public static void print() {
        inovations.forEach((key, value) -> System.out.println(key + " = " + value));
    }
}
