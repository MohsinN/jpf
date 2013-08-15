/**
 * 
 * @author Tuyen Luu
 * 
 *         Count example from SPIN Increment a variable in two processes. Check
 *         that final value can be two!!
 */

/* Do not use. Not verified yet */

// @LTLSpec("[](Process.n > 2)")
class Shared {

}

class Process extends Thread {
  static int n = 0;
  static int finished = 0;
  int id;
  static int TIMES = 10;

  public Process(int id) {
    this.id = id;
  }

  public void run() {
    int i = 1;
    int temp;
    while (i <= TIMES) {
      temp = n;
      n = temp + 1;
      i++;
      System.out.println("Id = " + id + " i = " + i + " n = " + n
          + " Finished = " + finished);
    }
    finished++;
    notify();
  }
}

class Finish extends Thread {
  public void run() {
    while (Process.finished != 2) {
      try {
        System.out.println(Process.finished);
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println("Finished! n = " + Process.n);
    Process.n = 0;

    // assert(n > 2);
  }
}

public class Count {
  public static void main(String args[]) {
    Thread p1 = new Process(1);
    Thread p2 = new Process(2);
    Thread f = new Finish();
    f.start();
    p2.start();
    p1.start();
  }
}
