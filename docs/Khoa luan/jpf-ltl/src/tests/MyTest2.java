import gov.nasa.jpf.jvm.*;
import gov.nasa.jpf.symbc.Subsumption;
import gov.nasa.jpf.symbc.Subsumption;
class A{
  int x;
  int y;
}
public class MyTest2 {
  A a = new A();
  
  public void test(int x, int y){
    if (x < y){
    	a.x = x;
    	a.y = y;
    }
    else if (x > y){
    	a.x = x;
    	a.y = y;
    }
    else{
    	a.x = 0;
    	a.y = 0;
    }
    Subsumption.storeAndCheckSubsumption();
  }
  
  public static void main(String args[]){
	  MyTest2 mt = new MyTest2();
	  mt.test(0, 0);
	  Subsumption.printAllStates();
  }
}
