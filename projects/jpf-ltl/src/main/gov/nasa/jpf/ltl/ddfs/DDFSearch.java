package gov.nasa.jpf.ltl.ddfs;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.search.Search;
import gov.nasa.ltl.graph.Edge;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Literal;
import gov.nasa.ltl.graph.Node;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class of Double Depth First Search strategy
 * 
 * Classe di ricerca di tipo Double Depth First Search
 * 
 * @author Michele Lombardi
 */
public class DDFSearch extends Search {

  protected LinkedList<Pair> dfs1Stack = new LinkedList<Pair>(),
          seenStates = new LinkedList<Pair>(),
          stateVisited1 = new LinkedList<Pair>(),
          statesFlagged = new LinkedList<Pair>();
  protected Node currentNode, previousNode;
  protected Graph<String> spec = null, setSpec = null;
  protected String specText = null, specSource = null;
  protected InfiniteListener l;
  protected LTLProperty prop = null;
  protected SystemState ss;
  protected boolean createdBcg, alreadySet, forceBacktrack, DEBUG=false;

  public DDFSearch(Config config, VM vm) {
    super(config, vm);
    // l = new InfiniteListener(config);
    // addListener(l);
  }

  @Override
  public void search() {
    
    depth = 0;
    
    // At this point InfiniteListener will set up a few things here, in 
    // particular the formula, the automaton and DEBUG.
    notifySearchStarted();

    if (DEBUG) {
        System.out.println("(DEBUG) DDFSearch: search started.");
        System.out.println("(DEBUG) DDFSearch: the formula is "+specText); 
        System.out.println("(DEBUG) DDFSearch: specSource is "+specSource);       
    }
    

    if (setSpec != null) {
      spec = setSpec;
      setSpec = null;
      setPreviousNode(spec.getInit());
      setCurrentNode(spec.getInit(), false);
      
      // Standard stuff: we create a new (JPF) property for the search, see
      // LTLProperty for more info.
      
      prop = new LTLProperty(specText, specSource);
      addProperty(prop);
      if(DEBUG) {
        System.out.println("(DEBUG) DDFSearch, search started: jpf state id = " + vm.getStateId() + ", automaton state id = " + getCurrentNode().getId() + ")");
      }
    }
    
    if (DEBUG) {
        System.out.println("(DEBUG) DDFSearch: pushing to dfs1Stack");
    }
    recordVisit(dfs1Stack);
    
    try {
      dfs1();
      terminate();
      //System.out.println("The property is true");
    } catch (EndException ex) {
      System.out.println(ex.getMessage());
    }
    notifySearchFinished();
  }

  /**
   * ---Depth First Search 1---
   * Depth First Search. In the backtrack checks for possible accepting states.
   * When a state accepts, the dfs2() starts.
   * 
   * Visita in profondità. Nel risalire controlla eventuali stati accettanti.
   * Se accettanti, inizia la dfs2().
   * 
   * @throws EndException 
   */
  private void dfs1() throws EndException {
    if (forward()) {
      
      /*The createBcg variable notifies if InfiniteListener has created a CG or
       * not .
       * 
       * La variabile createBcg notifica se InfiniteListener ha creato o meno
       * un CG
       */
      EdgeCG cg = vm.getCurrentChoiceGenerator("EdgeCG", EdgeCG.class);
      if (cg != null){
        Edge e = (Edge) cg.getNextChoice();
        setPreviousNode(e.getSource());
        setCurrentNode(e.getNext(), false);
      }
      
      if (isEndState()) {
        backtrack();
        depth--;
        setPreviousNode(currentNode);
        setCurrentNode(spec.getNode(dfs1Stack.getLast().getNode()), false);  // breaktrack dell'automa;
        notifyStateBacktracked();
        return;
      }
      createdBcg = false;
      depth++;
      notifyStateAdvanced();
      Pair pair = new Pair(vm.getStateId(), getCurrentNode().getId());
      if (DEBUG)
        System.out.println("depth = " + depth + ", choice = " + vm.getSystemState().getChoiceGenerator().getTotalNumberOfChoices());
      if (!seenBefore(pair, dfs1Stack) && !forceBacktrack) {
        recordVisit(dfs1Stack);
        if(DEBUG)
          System.out.println("(" + vm.getStateId() + ", " + getCurrentNode().getId() + ")");
        dfs1();
        //continue until there are other choices
        //continua finchè hai altre scelte
        while (vm.getSystemState().getChoiceGenerator().hasMoreChoices()) {
          if(DEBUG)
            System.out.println("state = " + vm.getStateId() + ", depth = " + depth
                  + ", choice = " + vm.getSystemState().getChoiceGenerator().getTotalNumberOfChoices());
          dfs1();
        }
        //if accepting state then dfs2();
        //se accettante allora dfs2();
        if (spec.getNode(pair.getNode()).getAttributes().getBoolean("accepting")) {
          //System.out.println("run dfs2()");
          DFS2();
        }
        backtrack();
        depth--;
        dfs1Stack.pollLast();
        setPreviousNode(currentNode);
        setCurrentNode(spec.getNode(dfs1Stack.getLast().getNode()), false);  // breaktrack dell'automa;
        notifyStateBacktracked();
        return;
      } else {
        forceBacktrack = false;
        backtrack();
        depth--;
        notifyStateBacktracked();
      }
    } else {
      if (DEBUG)
        System.out.println("No forward");
      notifyStateProcessed();
    }
  }
  
  private void DFS2() throws EndException {
     /**
     * DFS2() runs when a state, after has been completely explored, is also an
     * accepting state.
     * PROBLEM: The forward() will always be false precisely because it 
     * considers that the state has already been fully explored
     * SOLUTION: reset Choice Generators for this tree level.
     * 
     * La DFS2() viene lanciata quando uno stato, dopo esser stato completamente
     * esplorato, risulta anche essere uno stato accettante.
     * PROBLEMA: la forward() sarà sempre falsa appunto perchè considera che lo 
     * stato è già stato completamente esplorato
     * SOLUZIONE:resettiamo i Choice Generators per questo livello dell'albero.
     */
    vm.getSystemState().getChoiceGenerator().reset();
    dfs2();
    while (vm.getSystemState().getChoiceGenerator().hasMoreChoices()) {
      if (DEBUG)
        System.out.println("dfs2: state = " + vm.getStateId() + ", depth = " + depth
              + ", choice = " + vm.getSystemState().getChoiceGenerator().getTotalNumberOfChoices());
      dfs2();
    }
    statesFlagged.clear();
  }

  /**
   * ---Depth First Search 2---
   * Depth First Search. If encounter an already visited state from the dfs1()
   * then the property is false. End.
   * 
   * Visita in profondità. Se incontra uno stato già visitato dalla dfs1() la 
   * proprietà è falsa. Fine.
   * 
   * @throws EndException 
   */
  private void dfs2() throws EndException {
    if (forward()) {
      if (!createdBcg) {
        checkTrueOrNegatedEdges();
      }
      if (isEndState()) {
        backtrack();
        depth--;
        return;
      }
      createdBcg = false;
      depth++;
      notifyStateAdvanced();
      System.out.println(getDepth());
      Pair pair = new Pair(vm.getStateId(), getCurrentNode().getId());
      if (!seenBefore(pair, dfs1Stack)){
        if (!seenBefore(pair, statesFlagged) && !forceBacktrack) {
          recordVisit(statesFlagged);
          if(DEBUG)
            System.out.println("dfs2: (" + vm.getStateId() + ", " + getCurrentNode().getId() + ")");
          dfs2();
          //continue until there are other choices
          //continua finchè hai altre scelte
          while (vm.getSystemState().getChoiceGenerator().hasMoreChoices()) {
            if(DEBUG)
              System.out.println("dfs2: state = " + vm.getStateId() + ", depth = " + depth
                    + ", choice = " + vm.getSystemState().getChoiceGenerator().getTotalNumberOfChoices());
            dfs2();
          }
        } else {
          forceBacktrack = false;
          backtrack();
          depth--;
          notifyStateBacktracked();
        }
      } else {
        prop.setViolated();
        checkPropertyViolation();
        throw new EndException("The property is false");
      }
    } else {
      if(DEBUG)
        System.out.println("No Forward");
      notifyStateProcessed();
    }
  }

  /**
   * Returning from the forward() this boolean variable is checked to see if the
   * Buchi Automaton has progressed. 
   * Otherwise the method checkTrueOrNegateEdges() will be called 
   * 
   * Al ritorno dalla forward() viene controllata questa variabile booleana
   * per sapere se il Buchi Automaton ha progredito. In caso contrario verrà
   * chiamato il metodo checkTrueOrNegateEdges()
   * 
   * @param createdBcg 
   */
  public void setCreatedBcg(boolean createdBcg) {
    this.createdBcg = createdBcg;
  }
  
  /**
   * Refered to the Buchi Automaton.
   * The currentNode parameter is set as current Node and createBcg will be set
   * true if the current transition is stopped from the InfiniteListener.
   * Practically this boolean var is checked after the forward() instruction to
   * know if the Buchi Automaton has pregressed. 
   * Otherwise the method checkTrueOrNegateEdges() will be called
   * 
   * Riferito al Buchi Automaton
   * Setta il Node passato per parametro come Nodo corrente e mette a true la 
   * var createBcg se è stata interrotta una transizione all'interno di 
   * InfiniteListener.
   * Praticamente al ritorno dalla forward() viene controllata questa variabile
   * booleana per sapere se il Buchi Automaton ha progredito. In caso contrario
   * verrà chiamato il metodo checkTrueOrNegateEdges()
   * 
   * @param currentNode
   * @param createdBcg
   * @return 
   */
  protected Node<String> setCurrentNode(Node currentNode, boolean createdBcg) {
    this.createdBcg = createdBcg;
    return this.currentNode = currentNode;
  }

  /**
   * The current Node of the Buchi Automaton is returned.
   * 
   * Ritorna il Node corrente del Buchi Automaton.
   * 
   * @return 
   */
  protected Node<String> getCurrentNode() {
    return currentNode;
  }
  
  /**
   * The previousNode parameter is set as previous Node of the Buchi Automaton
   * 
   * 
   * Setta Node previousNode come Node precedente del Buchi Automaton.
   * 
   * @param previousNode
   * @return 
   */
  protected Node<String> setPreviousNode(Node previousNode) {
    return this.previousNode = previousNode;
  }
  
  /**
   * The previous Node of the Buchi Automaton is returned.
   * 
   * Ritorna il Node precedente del Buchi Automaton.
   * 
   * @return 
   */
  protected Node<String> getPreviousNode() {
    return previousNode;
  }

  /**
   * Record the pair Pair (JPF state, Buchi Automaton state) passed as a 
   * parameter in the LinkedList.
   * 
   * Registra la coppia Pair(Stato di JPF, Stato Buchi Automaton) nella 
   * LinkedList passata come parametro.
   * 
   * @param set 
   */
  protected void recordVisit(LinkedList<Pair> set) {
    Node<String> n = getCurrentNode();
    if (n != null) {
      Pair p = new Pair(vm.getStateId(), n.getId());
      set.add(p);
      if (DEBUG) {
        System.out.println("(DEBUG) DDFSearch: recording ("+vm.getStateId()+","+n.getId()+")");
      }
    } else {
      Pair p = new Pair(vm.getStateId(), 0);
      System.out.println("(DEBUG) DDFSearch: recording ("+vm.getStateId()+",ZERO)");
      set.add(p);
    }
  }

  /**
   * This method gets several information from the Listener
   * 
   * Questo metodo prende alcune informazioni da the Listener
   * 
   * @param deb //If "true" shows comments -- se "true" mostra commenti
   * @param g //Graph structure -- struttura grafo Graph
   * @param ltl //LTL formula from @LTLSpec --
   *              formula catturata dall'annotazione @LTLSpec
   * @param location //It contains the subpath of the executed file
   *                   contine il subpath del file in esecuzione.
   */
  void setSpec(Graph<String> g, String ltl, String location, boolean deb) {
    assert spec == null : "attempted to set Buchi spec but it was already set";
    setSpec = g;
    specText = ltl;
    specSource = location;
    DEBUG=deb;
  }

  /**
   * The Graph structure is returned.
   * 
   * Ritorna la struttura grafo Graph spec.
   * 
   * @return 
   */
  Graph<String> getSpec() {
    return spec;
  }

  /**
   * If after the forward the automaton state isn't modified, it means:
   * -it visited an edge that back in itself
   * -there were not match between code and guard.
   * In the second case it should change state if there is a negate or true 
   * guard. 
   * 
   * Se dopo la forward() non ho modificato lo stato dell'automa vuol dire che:
   * - ho incontrato un arco che mi ritorna nello stesso stato
   * - non ho trovato nessun match tra codice e guardia.
   * Nel secondo caso potrei comunque cambiare stato se ho una guardia negata
   * o un guardia a True.
   */
  public void checkTrueOrNegatedEdges() {
    List<Edge<String>> outgoingEdges = getCurrentNode().getOutgoingEdges();
    if (!outgoingEdges.isEmpty()) {

      /*
       * The alreadySet condition is an optimization of the number of cycles.
       * If it skip and there are more outgoing edges with denied guards then 
       * it will take last rather than first.
       * 
       * la condizione alreadySet è un'ottimizzazione al numero di cicli.
       * Se si omette e ci sono più archi uscenti con guardie negate allora
       * verrà presa l'ultima piuttosto che la prima.
       * 
       */
      for (int k = 0; k < outgoingEdges.size() && !alreadySet; k++) {
        if (!outgoingEdges.get(k).getGuard().isEmpty()) {
          Iterator<Literal<String>> itr = outgoingEdges.get(k).getGuard().iterator();

          while (itr.hasNext()) {
            Literal<String> a = itr.next();
            if (a.isNegated()) {
              if(DEBUG)
                System.out.println("match found with negation_2 "+a.getAtom().toString());
              setPreviousNode(currentNode);
              setCurrentNode(outgoingEdges.get(k).getNext(), false);
              alreadySet = true;
              break;
            }
          }
        }
      }
      if (!alreadySet) {
        for (int k = 0; k < outgoingEdges.size(); k++) {
          if (outgoingEdges.get(k).getGuard().isEmpty()) {
            if(DEBUG)
              System.out.println("match found with edge TRUE_2");
            setPreviousNode(currentNode);
            setCurrentNode(outgoingEdges.get(k).getNext(), false);
          }
        }
      } else {
        alreadySet = false;
      }
    }
  }

  /**
   * 
   * @param pair
   * @param lkl
   * @return 
   */
  private boolean seenBefore(Pair pair, LinkedList<Pair> lkl) {
    Iterator<Pair> itr = lkl.iterator();
    Pair p;
    while (itr.hasNext()) {
      p = itr.next();
      if(DEBUG)
        System.out.println("###" + p.toString() +"###");
      if (pair.equals(p)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * 
   * @param b 
   */
  void forceBacktrack(boolean b) {
    this.forceBacktrack = b;
  }
}
