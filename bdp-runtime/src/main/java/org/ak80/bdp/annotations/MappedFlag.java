package org.ak80.bdp.annotations;

import org.ak80.bdp.Bits;

/**
 * Defines the mapping of one bit flag to a field
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

  /**
   * The bit to map in the byte
   *
   * @return the bit
   */
  Bits bit();

}
