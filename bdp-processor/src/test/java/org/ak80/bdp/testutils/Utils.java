package org.ak80.bdp.testutils;


import org.ak80.bdp.Bit;
import org.ak80.bdp.annotations.Endian;
import org.ak80.bdp.annotations.MappedByte;
import org.ak80.bdp.annotations.MappedFlag;
import org.ak80.bdp.annotations.MappedWord;

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

  public static MappedWord createMappedWord(int index, String name, Endian endian) {
    MappedWord mappedWord = mock(MappedWord.class);
    when(mappedWord.index()).thenReturn(index);
    when(mappedWord.name()).thenReturn(name);
    when(mappedWord.endianess()).thenReturn(endian);
    return mappedWord;
  }

  public static MappedFlag createMappedFlag(int index, Bit bit, String name) {
    MappedFlag mappedFlag = mock(MappedFlag.class);
    when(mappedFlag.index()).thenReturn(index);
    when(mappedFlag.name()).thenReturn(name);
    when(mappedFlag.bit()).thenReturn(bit);
    return mappedFlag;
  }

}
