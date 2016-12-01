package org.ak80.bdp.annotations;

import org.ak80.bdp.Bit;

/**
 * Defines the mapping of a range of bits to an enum
 */
public @interface MappedEnum {

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
   * The start of the bit range to map in the byte
   *
   * @return the bit
   */
  Bit from();

  /**
   * The end of the bit range to map in the byte
   *
   * @return the bit
   */
  Bit to();

}
