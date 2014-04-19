package infinite.symbolic;

import gov.nasa.jpf.ltl.LTLSpec;

/**
 * 
 */

/**
 * @author Ewgenij Starostin
 * 
 */
@LTLSpec("[]!(" + "w0 &&" + "w1 &&"
    + "w2 &&" + "w3 &&" + "w4)")
public class Dining {
  static final int philosophers = 5;
  // Fork busy?
  static boolean[] f = new boolean[philosophers];
  // Waiting?
  static boolean[] w = new boolean[philosophers];
  static boolean w0, w1, w2, w3, w4;

  static void arrayToSingles() {
    w0 = w[0];
    w1 = w[1];
    w2 = w[2];
    w3 = w[3];
    w4 = w[4];
  }

  static void get(int i) {
    synchronized (f) {
      while (f[i])
        try {
          f.wait();
        } catch (InterruptedException e) {
        }
      f[i] = true;
    }
  }

  static void rls(int i) {
    synchronized (f) {
      f[i] = false;
      f.notifyAll();
    }
  }

  static void wat(int i) {
    synchronized (w) {
      w[i] = true;
      arrayToSingles();
    }
  }

  static void nom(int i) {
    synchronized (w) {
      w[i] = false;
      arrayToSingles();
    }
  }

  static void phil(int i, int j) {
    while (true) {
      wat(i);
      get(i);
      Thread.yield();
      get(j);
      Thread.yield();
      nom(i);
      rls(i);
      rls(j);
    }
  }

  public static void main(String[] args) {
    for (int i = 0; i < philosophers; i++) {
      final int x = i;
      new Thread() {
        @Override
        public void run() {
          Dining.phil(x, (x + 1) % Dining.philosophers);
        }
      }.start();
    }
    while (true)
      Thread.yield();
  }
}