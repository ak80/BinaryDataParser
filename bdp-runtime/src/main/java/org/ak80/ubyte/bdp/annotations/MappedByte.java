package org.ak80.ubyte.bdp.annotations;

/**
 * Defines the mapping of one byte to a custom field
 */
public @interface MappedByte {

  /**
   * Index of byte in the data stream
   *
   * @return the index
   */
  int index();

  /**
   * optional name of the byte
   *
   * @return the name
   */
  String name() default "";

}
