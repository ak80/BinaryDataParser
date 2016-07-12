package org.ak80.bdp.annotations;

/**
 * Defines the mapping of one byte to a field
 */
public @interface MappedFlag {

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
