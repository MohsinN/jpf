/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infinite;

/**
 *
 * @author Michele
 */


import gov.nasa.jpf.ltl.LTLSpec;

@LTLSpec("[](<> \"done()\")")
public class Methods {

  public static void main(String[] args) {
    int a=0; long b=0; short c=0; byte d=0; char e='e'; float f=0; double g=1.5; String h="hello";
    Prova p = new Prova();
    done(); done(a); done(b); done(c); done(d); done(e); done(f); done(g); done(h); done(p); done(a,p);
  }

  public static void done() {}
  public static int done(int a) {return a;}
  public static long done(long a) {return a;}
  public static short done(short a) {return a;}
  public static byte done(byte a) {return a;}
  public static char done(char a) {return a;}
  public static float done(float a) {return a;}
  public static double done(double a) {return a;}
  public static String done(String a) {return a;}
  public static Prova done(Prova a) {return a;}
  public static int done(int a, Prova b) {return a;}
  

}
