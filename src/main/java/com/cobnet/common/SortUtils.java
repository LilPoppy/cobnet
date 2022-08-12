package com.cobnet.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class SortUtils {

    public static <T> List<T> topologicalSort(Stream<T> source, Function<T, Iterable<T>> dependencies) {

        List<T> sorted = new ArrayList<>();

        Map<T, Boolean> visited = new HashMap<>();

        source.forEach(item -> SortUtils.topologicalSort(item, dependencies, sorted, visited));

        return sorted;
    }

    private static <T> void topologicalSort(T item, Function<T, Iterable<T>> dependencies, List<T> sorted, Map<T, Boolean> visited) {

        boolean result = visited.getOrDefault(item, false);

        if(!visited.containsKey(item)) {

            visited.put(item, true);

            Iterable<T> nodes = dependencies.apply(item);

            if(nodes != null) {

                for (T node : nodes){

                    SortUtils.topologicalSort(node, dependencies, sorted, visited);
                }
            }

            visited.put(item, false);

            sorted.add(item);

        } else if(result) {

            throw new RuntimeException();
        }
    }
}
