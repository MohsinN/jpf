package finite.always;

import gov.nasa.jpf.ltl.*;

@LTLSpec("[]k==1")
class A1 {
  int k = 1;
}

public class FieldInit1 {
  public static void main(String[] args) {
    new A1();
  }
}
