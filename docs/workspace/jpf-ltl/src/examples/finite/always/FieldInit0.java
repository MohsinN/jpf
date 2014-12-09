package finite.always;

import gov.nasa.jpf.ltl.*;

@LTLSpec("[]k==0")
class A0 {
  int k = 0;
}

public class FieldInit0 {
  public static void main(String[] args) {
    new A0();
  }
}
