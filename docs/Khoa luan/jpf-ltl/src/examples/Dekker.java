import gov.nasa.jpf.ltl.LTLSpec;
/**
 * 
 * @author Tuyen Luu
 * 
 * Dekker's algorithm
 */

@LTLSpec("[]<>process0.cs0 && []<>proces1.cs1")

class proc extends Thread {
  static boolean flag0 = false; //flag of the process 0
  static boolean flag1 = false; //flag of the process 1
  static boolean cs0 = false; 
  static boolean cs1 = false;
  static int turn = 0;
  void critical(){}; // critical section
  void remainder(){}; // remainder section
}

class process0 extends proc {
  public void run(){
    flag0 = true;
    while (flag1 == true){
      if (turn != 0){
        flag0 = false;
        while (turn != 0){
          // do nothing, waiting...
        }
        flag0 = true;
      }
    }
    critical(); // go to critical section
    turn = 1;
    flag0 = false;
    remainder(); // go to remainder section
  }
  void critical(){
    System.out.println("Process 0 go to critical section"); 
	cs0 = true;
	assert(!(cs0&&cs1));
	cs0 = false;
  }
  void remainder(){
	  System.out.println("Process 0 go to remainder section");
  }
}
class process1 extends proc {
  public void run(){
    flag1 = true;
	while (flag0 == true){
	  if (turn != 1){
	    flag1 = false;
	    while (turn != 1){
	     // do nothing, waiting...
	    }
	    flag1 = true;
	  }
  }
  critical(); // go to critical section
  turn = 0;
  flag1 = false;
  remainder(); // go to remainder section
  }
  void critical(){
    System.out.println("Process 1 go to critical section");
    cs1 = true;
	assert(!(cs0&&cs1));
    cs1 = false;
  }
  void remainder(){
    System.out.println("Process 1 go to remainder section");
  }
}

public class Dekker{
  public static void main(String args[]){
    Thread p0 = new process0();
    Thread p1 = new process1();
    p0.start();
    p1.start(); 
  }
}
  