import gov.nasa.jpf.jvm.*;
import gov.nasa.jpf.symbc.Subsumption;
class B{
  int x;
  int y;
  int z;
}
public class MyTest1 {
  B a = new B();
  
  public void test(int x, int y,int z){
    if (x < y && y < z){
    	a.x = x;
    	a.y = y;
    	a.z = z;
    }
    else if (x > y && y > z){
    	a.x = z;
    	a.y = y;
    	a.z = x;
    }
    else{
    	a.x = 0;
    	a.y = 0;
    	a.z = 0;
    }
    Subsumption.storeAndCheckSubsumption();
  }
  
  public static void main(String args[]){
	  MyTest1 mt = new MyTest1();
	  mt.test(0, 0, 0);
	  Subsumption.printAllStates();
  }
}
