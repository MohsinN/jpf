//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.jvm;

import java.util.Random;


/**
 * Verify is the programmatic interface of JPF that can be used from inside of
 * applications. In order to enable programs to run outside of the JPF
 * environment, we provide (mostly empty) bodies for the methods that are
 * otherwise intercepted by the native peer class
 */
public class Verify {
  static final int MAX_COUNTERS = 10;
  static int[] counter;  // only here so that we don't pull in all JPF classes at RT

  private static Random random;

  /*
   * only set if this was used from within a JPF context. This is mainly to
   * enable encapsulation of JPF specific types so that they only get
   * pulled in on demand, and we otherwise can still use the same Verify class
   * for JPF-external execution. We use a class object to make sure it doesn't
   * get recycled once JPF is terminated.
   */
  static Class<?> peer;

  private static Random getRandom() {
    if (random == null) {
      random = new Random(42);
    }
    return random;
  }

  /*
   * register the peer class, which is only done from within a JPF execution
   * context. Be aware of that this migh actually load the real Verify class.
   * The sequence usually is
   *   JPF(Verify) -> JVM(JPF_gov_nasa_jpf_jvm_Verify) -> JVM(Verify)
   */
  public static void setPeerClass (Class<?> cls) {
    peer = cls;
  }

  // note this is NOT marked native because we might also call it from host VM code
  // (beware that Verify is a different class there!). When executed by JPF,
  // this is an MJI method
  public static int getCounter (int id) {
    if (peer != null) {
      // this is executed if we are in a JPF context
      return JPF_gov_nasa_jpf_jvm_Verify.getCounter__I__I(null, 0, id);
    } else {
      if (counter == null) {
        counter = new int[id >= MAX_COUNTERS ? (id+1) : MAX_COUNTERS];
      }
      if ((id < 0) || (id >= counter.length)) {
        return 0;
      }

      return counter[id];
    }
  }

  public static void resetCounter (int id) {
    if (peer != null){
      JPF_gov_nasa_jpf_jvm_Verify.resetCounter__I__V(null, 0, id);
    } else {
      if ((counter != null) && (id >= 0) && (id < counter.length)) {
        counter[id] = 0;
      }
    }
  }

  public static int incrementCounter (int id) {
    if (peer != null){
      return JPF_gov_nasa_jpf_jvm_Verify.incrementCounter__I__I(null, 0, id);
    } else {
      if (counter == null) {
        counter = new int[(id >= MAX_COUNTERS) ? id+1 : MAX_COUNTERS];
      } else if (id >= counter.length) {
        int[] newCounter = new int[id+1];
        System.arraycopy(counter, 0, newCounter, 0, counter.length);
        counter = newCounter;
      }

      if ((id >= 0) && (id < counter.length)) {
        return ++counter[id];
      }

      return 0;
    }
  }


  //  Backwards compatibility END

  /**
   * Adds a comment to the error trace, which will be printed and saved.
   */
  public static void addComment (String s) {}

  /**
   * Backwards compatibility START
   * @deprecated use "assert cond : msg"
   */
  @Deprecated
  public static void assertTrue (String s, boolean cond) {
    if (!cond) {
      System.out.println(s);
      assertTrue(cond);
    }
  }

  /**
   * Checks that the condition is true.
   * @deprecated use 'assert' directly
   */
  @Deprecated
  public static void assertTrue (boolean cond) {
    if (!cond) {
      throw new AssertionError("Verify.assertTrue failed");
    }
  }

  public static void atLabel (String label) {}

  public static void atLabel (int label) {}

  /**
   * Marks the beginning of an atomic block.
   * THIS IS EVIL, DON'T USE IT FOR OPTIMIZATION - THAT'S WHAT POR IS FOR!
   * (it's mostly here to support model classes that need to execute atomic)
   */
  public static void beginAtomic () {}

  /**
   * Marks the end of an atomic block.
   * EVIL - see beginAtomic()
   */
  public static void endAtomic () {}

  public static void boring (boolean cond) {}

  public static void busyWait (long duration) {
    // this gets only executed outside of JPF
    while (duration > 0) {
      duration--;
    }
  }

  public static boolean isCalledFromClass (String refClsName) {
    Throwable t = new Throwable();
    StackTraceElement[] st = t.getStackTrace();

    if (st.length < 3) {
      // main() or run()
      return false;
    }

    try {
      Class<?> refClazz = Class.forName(refClsName);
      Class<?> callClazz = Class.forName(st[2].getClassName());

      return (refClazz.isAssignableFrom(callClazz));

    } catch (ClassNotFoundException cfnx) {
      return false;
    }
  }

  public static void ignoreIf (boolean cond) {}

  public static void instrumentPoint (String label) {}

  public static void instrumentPointDeep (String label) {}

  public static void instrumentPointDeepRecur (String label, int depth) {}

  public static void interesting (boolean cond) {}

  public static void breakTransition () {}

  /**
   * simple debugging aids to imperatively print the current path output of the SUT
   * (to be used with vm.path_output)
   */
  public static void printPathOutput(String msg) {}
  public static void printPathOutput(boolean cond, String msg) {}

  public static void print (String s) {
    System.out.print(s);
  }

  public static void print (String s, int i) {
    System.out.print(s + " : " + i);
  }

  public static void print (String s, boolean b) {
    System.out.print(s + " : " + b);
  }

  public static void println() {
    System.out.println();
  }

  /**
   * this is to avoid StringBuilders
   */
  public static void print (String... args){
    for (String s : args){
      System.out.print(s);
    }
  }

  /**
   * note - these are mostly for debugging purposes (to see if attributes get
   * propagated correctly, w/o having to write a listener), since attributes are
   * supposed to be created at the native side, and hence can't be accessed from
   * the application
   */
  public static void setFieldAttribute (Object o, String fieldName, int val) {}
  public static int getFieldAttribute (Object o, String fieldName) { return 0; }

  public static void setLocalAttribute (String varName, int val) {}
  public static int getLocalAttribute (String varName) { return 0; }

  public static void setElementAttribute (Object arr, int idx, int val){}
  public static int getElementAttribute (Object arr, int idx) { return 0; }

  public static void setObjectAttribute (Object o, int val) {}
  public static int getObjectAttribute (Object o) { return 0; }

  /**
   * this is the new boolean choice generator. Since there's no real
   * heuristic involved with boolean values, we skip the id (it's a
   * hardwired "boolean")
   */
  public static boolean getBoolean () {
    // just executed when not running inside JPF, native otherwise
    return ((System.currentTimeMillis() & 1) != 0);
  }

  /**
   * new boolean choice generator that also tells jpf which value to
   * use first by default in a search.
   */
  public static boolean getBoolean (boolean falseFirst) {
    // this is only executed when not running JPF
    return getBoolean();
  }


  /**
   * Returns int nondeterministically between (and including) min and max.
   */
  public static int getInt (int min, int max) {
    // this is only executed when not running JPF, native otherwise
    return getRandom().nextInt((max-min+1)) + min;
  }

  public static int getIntFromSet (int... values){
    if (values != null && values.length > 0) {
      int i = getRandom().nextInt(values.length);
      return values[i];
    } else {
      return getRandom().nextInt();
    }
  }

  public static Object getObject (String key) {
    return "?";
  }

  /**
   * this is the API for int value choice generators. 'id' is used to identify
   * both the corresponding ChoiceGenerator subclass, and the application specific
   * ctor parameters from the normal JPF configuration mechanism
   */
  public static int getInt (String key){
    // this is only executed when not running JPF, native otherwise
    return getRandom().nextInt();
  }

  /**
   * this is the API for double value choice generators. 'id' is used to identify
   * both the corresponding ChoiceGenerator subclass, and the application specific
   * ctor parameters from the normal JPF configuration mechanism
   */
  public static double getDouble (String key){
    // this is only executed when not running JPF, native otherwise
    return getRandom().nextDouble();
  }

  public static double getDoubleFromSet (double... values){
    if (values != null && values.length > 0) {
      int i = getRandom().nextInt(values.length);
      return values[i];
    } else {
      return getRandom().nextDouble();
    }
  }

  /**
   * Returns a random number between 0 and max inclusive.
   */
  public static int random (int max) {
    // this is only executed when not running JPF
    return getRandom().nextInt(max + 1);
  }

  /**
   * Returns a random boolean value, true or false. Note this gets
   * handled by the native peer, and is just here to enable running
   * instrumented applications w/o JPF
   */
  public static boolean randomBool () {
    // this is only executed when not running JPF
    return getRandom().nextBoolean();
  }

  public static long currentTimeMillis () {
    return System.currentTimeMillis();
  }

  // Backwards compatibility START
  public static Object randomObject (String type) {
    return null;
  }

  public static boolean isRunningInJPF() {
    return false;
  }

  /**
   * USE CAREFULLY - returns true if the virtual machine this code is
   * running under is doing state matching.  This can be used as a hint
   * as to whether data structures (that are known to be correct!)
   * should be configured to use a canonical representation.  For example,
   * <pre><code>
   * Vector v = new Vector();
   * v.add(obj1);
   * if (Verify.getBoolean()) {
   *     v.addAll(eleventyBillionObjectCollection);
   *     v.setSize(1);
   * }
   * // compare states here
   * </code></pre>
   * To the programmer, the states are (almost certainly) the same.  To
   * the VM, they could be different (capacity inside the Vector).  For
   * the sake of speed, Vector does not store things canonically, but this
   * can cause (probably mild) state explosion if matching states.  If
   * you know whether states are being matched, you can choose the right
   * structure--as long as those structures aren't what you're looking for
   * bugs in!
   */
  public static boolean vmIsMatchingStates() {
    return false;
  }

  public static void storeTrace (String fileName, String comment) {
    // intercepted in NativePeer
  }

  public static void storeTraceIf (boolean cond, String fileName, String comment) {
    if (cond) {
      storeTrace(fileName, comment);
    }
  }

  public static void storeTraceAndTerminate (String fileName, String comment) {
    storeTrace(fileName, comment);
    terminateSearch();
  }

  public static void storeTraceAndTerminateIf (boolean cond, String fileName, String comment) {
    if (cond) {
      storeTrace(fileName, comment);
      terminateSearch();
    }
  }

  public static boolean isTraceReplay () {
    return false; // native
  }

  public static void terminateSearch () {
    // native
  }

  public static void setProperties (String... p) {
    // native
  }

  public static String getProperty (String key) {
    // native
    return null;
  }
}
