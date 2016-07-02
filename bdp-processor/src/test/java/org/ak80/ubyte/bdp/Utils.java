package org.ak80.ubyte.bdp;


import java.util.HashSet;
import java.util.Set;

public class Utils {

  public static <T> Set<T> setOf(T... elements) {
    HashSet<T> set = new HashSet<>();
    for (T element : elements) {
      set.add(element);
    }
    return set;
  }

}
