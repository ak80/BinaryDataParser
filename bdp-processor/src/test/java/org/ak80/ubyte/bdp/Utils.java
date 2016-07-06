package org.ak80.ubyte.bdp;


import org.ak80.ubyte.bdp.annotations.MappedByte;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Utils {

  public static <T> Set<T> setOf(T... elements) {
    HashSet<T> set = new HashSet<>();
    for (T element : elements) {
      set.add(element);
    }
    return set;
  }


  public static MappedByte createMappedByte(int index, String name) {
    MappedByte mappedByte = mock(MappedByte.class);
    when(mappedByte.index()).thenReturn(index);
    when(mappedByte.name()).thenReturn(name);
    return mappedByte;
  }


}
