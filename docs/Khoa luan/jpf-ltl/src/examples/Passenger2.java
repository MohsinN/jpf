import gov.nasa.jpf.ltl.LTLSpec;

//@LTLSpec("[](Passenger.wait U (Light.color == 0))")
@LTLSpec("[](!Passenger.wait)")
class MPassenger extends Thread {
  boolean wait = false;
  public void run(){
      try {
        System.out.println("wait = " + wait);
        wait = true;
        Thread.sleep(1000);
        wait = false;
        System.out.println("wait = " + wait);
    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }
}
/*class Light extends Thread{
  int color; // green = 0, yellow = 1,red = 2
  int count = 0;
  public void run(){
      
      try {
        System.out.println("Start");
        while(count < 5){
        Thread.sleep(100);
        color = (color + 1)%3;
        System.out.println("color = " + color);
        
        count++;
        }
        System.out.println("Stop");
        
    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }
}*/
class Passenger2{
  public static void main(String args[]){
   // Thread light = new Light();
    Thread passenger = new MPassenger();
    //light.start();
    passenger.start();
  }
}
