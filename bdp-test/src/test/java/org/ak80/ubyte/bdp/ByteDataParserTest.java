package org.ak80.ubyte.bdp;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ByteDataParserTest {

  @Test
  public void heavyPrototyping() {
    // Given
    Foo foo = new Foo();
    FooParser fooParser = new FooParser(foo);

    // When
    fooParser.setFoo2(23);

    assertThat(foo.getFoo2(), is(23));
  }

}