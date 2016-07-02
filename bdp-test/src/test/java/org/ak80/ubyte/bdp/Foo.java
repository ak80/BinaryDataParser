package org.ak80.ubyte.bdp;


import org.ak80.ubyte.bdp.annotations.MappedByte;

public class Foo {

  @MappedByte(index = 0, name = "foo")
  private int foo2;


  @MappedByte(index = 2, name = "foos")
  private int foo3;


  public int getFoo2() {
    return foo2;
  }

  public void setFoo2(int foo2) {
    this.foo2 = foo2;
  }

  public int getFoo3() {
    return foo3;
  }

  public void setFoo3(int foo3) {
    this.foo3 = foo3;
  }
}
