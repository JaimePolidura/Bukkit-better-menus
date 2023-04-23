package es.bukkitbettermenus.utils;

import java.util.ArrayList;
import java.util.List;

public final class CollectionUtils {
    public static <T> List<T> sublist(List<T> source, int from, int to){
        return to + 1 >= source.size() ?
                source.subList(from, source.size() - 1) :
                source.subList(from, to);
    }

    public static List<Integer> bidimensionalArrayToLinearArray(int[][] array){
        List<Integer> toReturn = new ArrayList<>(array.length * array[0].length);

        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array[i].length; j++)
                toReturn.add(array[i][j]);

        return toReturn;
    }
}
