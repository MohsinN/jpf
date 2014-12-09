package gov.nasa.jpf.ltl.ddfs;

/**
 * Questa classe rappresenta l'oggetto contentente la coppia (Stato di JPF,
 * Stato Buchi Automaton)
 * 
 * @author Michele Lombardi
 */
public class Pair {

  private int state;
  private int node;

  Pair(int state, int node) {
    this.state = state;
    this.node = node;
  }

  /**
   * confronta il Pair che chiama la funzione con quello passato per parametro.
   * Ritorna true se i Pair sono uguali (cioè gli ID degli stati corrispondono).
   * @param p
   * @return 
   */
  public boolean equals(Pair p) {
    if (p == null || !(p instanceof Pair)) {
      return false;
    }
    Pair p2 = (Pair) p;
    return state == p2.state && node == p2.node;
  }
  
  /**
   * Ritorna il Pair come stringa rappresentante la coppia degli stati
   * @return 
   */
  @Override
  public String toString() {
    return "(" + state + ", " + node + ")";
  }
  
  /**
   * Ritorna l'intero rappresentate l'ID dello stato del Buchi Automaton
   * @return 
   */
  public int getNode() {
    return node;
  }

  /**
   * Setta un nuovo ID allo stato del Buchi Automaton del Pair che chiama 
   * tale metodo
   * @param node 
   */
  public void setNode(int node) {
    this.node = node;
  }

  /**
   * Ritorna l'intero rappresentate l'ID dello stato di JPF
   * @return 
   */
  public int getState() {
    return state;
  }

  /**
   * Setta un nuovo ID allo stato di JPF del Pair che chiama tale metodo
   * @param state 
   */
  public void setState(int state) {
    this.state = state;
  }
}