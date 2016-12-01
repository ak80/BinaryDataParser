package org.ak80.bdp;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BinaryUtilsTest {

  @Test
  public void getRangeMask_allValid() {

    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_0), is(0b11111111));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_6, Bit.BIT_0), is(0b01111111));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_5, Bit.BIT_0), is(0b00111111));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_4, Bit.BIT_0), is(0b00011111));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_3, Bit.BIT_0), is(0b00001111));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_2, Bit.BIT_0), is(0b00000111));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_1, Bit.BIT_0), is(0b00000011));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_0, Bit.BIT_0), is(0b00000001));

    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_7), is(0b10000000));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_6), is(0b11000000));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_5), is(0b11100000));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_4), is(0b11110000));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_3), is(0b11111000));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_2), is(0b11111100));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_1), is(0b11111110));
    assertThat(BinaryUtils.getRangeMask(Bit.BIT_7, Bit.BIT_0), is(0b11111111));

  }
}