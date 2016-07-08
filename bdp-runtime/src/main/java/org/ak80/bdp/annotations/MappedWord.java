package org.ak80.bdp.annotations;

/**
 * Defines the mapping of two bytes to a field
 */
public @interface MappedWord {

  /**
   * Index of the first byte in the data stream
   *
   * @return the index
   */
  int index();

  /**
   * Optional name of the word
   *
   * @return the name
   */
  String name() default "";

  /**
   * Optional endianness, with {@link Endian#BIG_ENDIAN} as the default
   *
   * @return the endian type
   */
  Endian endianess() default Endian.BIG_ENDIAN;

}
