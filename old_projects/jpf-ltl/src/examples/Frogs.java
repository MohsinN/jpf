import gov.nasa.jpf.jvm.Verify;
import gov.nasa.jpf.ltl.LTLSpec;

import java.util.Random;

/**
 * (taken from jSpin)
 Frogs puzzle:
    Seven stones
  Three male frogs at right facing left
  Three female frogs at left facing right
  F-> F-> F-> [EMPTY] <-M <-M <-M

  Frogs can move in the direction it is facing to an empty stone:
  That is adjacent
  That is reached by jumping over a frog on an adjacent stone

  Is there a sequence of moves that will exchange the positions
  of the male and female frogs?
  Solution: try to Verify/Safety []!success; 
    when it fails the trail gives the set of moves.
 */

@LTLSpec("[]!success")
class Frogs extends Thread {
  
  final static int STONES = 5;
  
  final static boolean DEBUG = true;
  
  static boolean success;
  static boolean moreSteps = true;
  static int stones[] = new int[STONES];
  static int Spos;
  private int Fpos;

  static void init() {
    Spos = STONES / 2;
    for (int i = 0; i < STONES / 2; i++) {
      stones[i] = 1; // frogs
    }
    stones[STONES / 2] = 0; // space
    for (int i = STONES / 2 + 1; i < STONES; i++) {
      stones[i] = 2; // toads
    }
  }

  public Frogs(int pos) {
    Fpos = pos;
  }

  public static void display() {
    for (int i = 0; i < STONES; i++) {
      System.out.print(stones[i]);
    }
    System.out.println("");
  }

  public static boolean isSuccess() {
    boolean check = true;
    for (int i = 0; i < STONES / 2; i++)
      check &= (stones[i] == 2);
    for (int i = STONES / 2 + 1; i < STONES; i++)
      check &= (stones[i] == 1);
    if (!success)
      success = check;

    // assert !success;
    return success;

  }

  public static void checkCanMove() {
    boolean canMove = false;
    if (Spos >= 1)
      if (stones[Spos - 1] == 1) {
        canMove = true;

      }
    if (Spos >= 2)
      if (stones[Spos - 2] == 1) {
        canMove = true;

      }
    if (Spos <= STONES - 2)
      if (stones[Spos + 1] == 2) {
        canMove = true;
      }
    if (Spos <= STONES - 3)
      if (stones[Spos + 2] == 2) {
        canMove = true;
      }
    if (isSuccess())
      canMove = false;
    moreSteps = canMove;

  }

  private synchronized void move() {
    Verify.beginAtomic();
    int newPos = Fpos;
    if (DEBUG) {
      System.out.println("\n\n***Before move " + Thread.currentThread());
      display();
      System.out.println("Move the " + ((stones[Fpos]==1)?"frog":"toad") + " at position " + (Fpos+1));
      System.out.println("to space position at " + (Spos+1));
    }
   
    if (stones[Fpos] == 1) {
      if (Fpos + 1 == Spos) {     
        //if (DEBUG) display();
        stones[Spos] = 1;
        stones[Fpos] = 0;
        newPos = Spos;
        Spos = Fpos;
      } else if (Fpos + 2 == Spos) {
        //if (DEBUG) display();
        stones[Spos] = 1;
        stones[Fpos] = 0;
        newPos = Spos;
        Spos = Fpos;
      }
    } else if (stones[Fpos] == 2) {
      if (Fpos - 1 == Spos) {
        //if (DEBUG) display();
        stones[Spos] = 2;
        stones[Fpos] = 0;
        newPos = Spos;
        Spos = Fpos;
      } else if (Fpos - 2 == Spos) {
        //if (DEBUG) display();
        stones[Spos] = 2;
        stones[Fpos] = 0;
        newPos = Spos;
        Spos = Fpos;
      }
    }

    if (DEBUG) {
      System.out.println("After move");
      display();
    }

    checkCanMove();

    Fpos = newPos;
    Verify.endAtomic();
  }

  public void run() {
    while (true) {
      move();
    }
  }

  public static void main(String args[]) {
    Frogs.init();
    for (int i = 0; i < STONES; i++) {
      if (i != Frogs.STONES / 2) {
        final int Fpos = i;
        (new Frogs(Fpos)).start();
      }
    }

  }
}
