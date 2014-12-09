import gov.nasa.jpf.ltl.LTLSpec;

/**
 * 
 * @author Anh Cuong
 */
@LTLSpec("[]((Passenger.checkIn())->(X(<>(Passenger.aboard()))))")
public class Passenger {

  public static int passengerLeft;

  static public void checkIn() {
    passengerLeft--;
    System.out.println("checkin");
  }

  static public void aboard() {
    System.out.println("aboard");
  }

  static public void waitAndAboard() {
    while (passengerLeft != 0) { /* wait */
    }
    aboard();
  }

  public static void main(String[] args) {
    passengerLeft = 1;

    new Thread(new Runnable() {
      public void run() {
        checkIn();
      }
    }).start();

    new Thread(new Runnable() {
      public void run() {
        aboard();
        waitAndAboard();
      }
    }).start();
  }
}
