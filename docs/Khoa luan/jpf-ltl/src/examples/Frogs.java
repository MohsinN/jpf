import gov.nasa.jpf.jvm.Verify;
/**
 * 
 * @author Tuyen Luu
 * 
 * 
 */

/*Do not use. Not verified yet*/

//@LTLSpec("[]!Frog.success")
class Frog extends Thread{
  int pos;
  int id;
  static final int STONES = 7;
  static boolean success;
  static int stones[] = new int[7];
  static int turnId = -1;
  public static boolean isSuccess(){
    success = (stones[0]==2 && stones[1]==2 && stones[2]==2 && stones[3]==0 
    		&&stones[4]==1 && stones[5]==1 && stones[6]==1);
    return success;
  }
  public synchronized void display(){
    for (int i = 0; i < STONES; i++){
      System.out.print(stones[i]);
    }
    System.out.println();
  }
  
  public Frog(int pos,int id){
	    this.pos = pos;
	    this.id = id;
	    System.out.println(id);
	  }
  public void run(){
	  while(stones[0] != 0 && stones[STONES-1] != 0){
		  System.out.println("move");
		  
		  move();
	  }
    
  }
  public synchronized void move(){
	  System.out.println(turnId + " "  + id);
	  while (turnId!=id){
	    	try{
	    		System.out.println(turnId + " "  + id);
	    		wait();
	    	}
	    	catch(InterruptedException e){
	    		e.printStackTrace();
	    	}
	    }
	    //notifyAll();
	  System.out.println("Frog id move " + turnId);   
	  if (stones[pos] == 1){
		//System.out.println("male frog move " + pos + " " + stones[pos]);  
	    //while(!isSuccess() && stones[0] != 0 && stones[STONES-1] != 0){
	    if (pos < STONES - 1 && stones[pos+1] == 0){
	      stones[pos] = 0;
	      stones[pos+1] = 1;
	      pos = pos + 1;
	      System.out.println("male fog move 1 ");
	      for (int i = 0; i < 7; i++){
	    	System.out.print(stones[i] + "fdsfs");
	      }
	      System.out.println();
	      System.out.println("male fog done 1");
	    }
	    if (pos < STONES -2 && stones[pos+1]!= 0 && stones[pos+2] == 0){
	      stones[pos] = 0;
	      stones[pos+2] = 1;
	      pos = pos + 2;
	      System.out.println("male fog move 2");
	      for (int i = 0; i < STONES; i++){
	        System.out.print(stones[i]);
	      }
	      System.out.println();
	      System.out.println("male fog done 2");	    	   
	    }
	} else if (stones[pos] == 2){
		 // System.out.println("female frog move " + pos + " " + stones[pos]);
	    	//while(!isSuccess() && stones[0] != 0 && stones[STONES-1] != 0){    
	    if (pos > 0 && stones[pos-1] == 0){
	      stones[pos] = 0;
	      stones[pos-1] = 2;
	      pos = pos - 1;
	      System.out.println("female fog move 1");
	      for (int i = 0; i < STONES; i++){
	        System.out.print(stones[i]);
	      }
	      System.out.println();
	      System.out.println("female fog done 1");

	    }
	    if (pos > 1 && stones[pos-1]!= 0 && stones[pos-2] == 0){
	      stones[pos] = 0;
	      stones[pos-2] = 2;
	      pos = pos - 2;
	      System.out.println("female fog move 2");
	      for (int i = 0; i < STONES; i++){
	        System.out.print(stones[i]);
	      }
	      System.out.println();
	      System.out.println("female fog done 2");
	    }
	  }
	
    turnId++;
    System.out.println(turnId);
    if (turnId == 6) turnId = 0;
    notifyAll();
  }	  
}
class Puzzle extends Thread{
	public void run(){
		Frog.turnId = 0;
		 while(Frog.stones[0] != 0 && Frog.stones[Frog.STONES-1] != 0){
			  System.out.println("move");
		  }
	}
}
public  class Frogs {
  
  public static void main(String args[]){
	 // Verify.beginAtomic();
	Thread frogs[] = new Thread[Frog.STONES];
    Frog.stones[Frog.STONES/2] = 0;
    int i = 0;
    while (i != Frog.STONES/2){
      Frog.stones[i] = 1;
      frogs[i] = new Frog(i,i);
      Frog.stones[Frog.STONES - i - 1] = 2;
      frogs[Frog.STONES - i - 1] = new Frog(Frog.STONES - i - 1,Frog.STONES - i - 2);
      i++;
    }
    for (i =0; i < Frog.STONES; i++)
    	System.out.print(Frog.stones[i]);
    System.out.println();
    for (i = 0; i < Frog.STONES; i++)
    	if (frogs[i] != null) frogs[i].start();
   // Verify.endAtomic();
  }
  
}
