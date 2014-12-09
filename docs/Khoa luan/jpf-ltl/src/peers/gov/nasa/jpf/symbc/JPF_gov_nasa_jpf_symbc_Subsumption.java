package gov.nasa.jpf.symbc;

import java.util.*;

import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DoubleFieldInfo;
import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.FloatFieldInfo;
import gov.nasa.jpf.jvm.IntegerFieldInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.LongFieldInfo;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.ReferenceFieldInfo;
import gov.nasa.jpf.jvm.StaticArea;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.heap.Helper;
import gov.nasa.jpf.symbc.heap.SymbolicInputHeap;
import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;


public class JPF_gov_nasa_jpf_symbc_Subsumption {
  // store pair of state with its corresponding contraints
  public static HashMap<String,ArrayList<NumericConstraint>> states = new HashMap<String,ArrayList<NumericConstraint>>(); 
  // store all detail states ( just for debug )
  static ArrayList<String> allDetailStates = new ArrayList<String>();
  //count the number of instance variable when travel through the heap
  static int varcount;
  // a contraint is the union of the Path Condition and the valuation ( see S.Anand et al. paper: " Symbolic execution with subsumption checking")
  static PathCondition constraint;
  // instance of general solver class.
  static LTLSymbolicConstraintsGeneral ltlConstraintSolver = new LTLSymbolicConstraintsGeneral();
  
  static String abstractState = new String();
  
  static String detailState = new String();
  
  public static PathCondition getPC(MJIEnv env) {
	JVM vm = env.getVM();
	ChoiceGenerator<?> cg = vm.getChoiceGenerator();
	PathCondition pc = null;
	if (!(cg instanceof PCChoiceGenerator)) {
	  ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
	  while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
	    prev_cg = prev_cg.getPreviousChoiceGenerator();
	  	}
	  cg = prev_cg;
	  }
	  if ((cg instanceof PCChoiceGenerator) && cg != null) {
		pc = ((PCChoiceGenerator) cg).getCurrentPC();
	  }
	return pc;
  }
  /**
   * print all states ( for debug )
   * 
   */
  public static void printAllStates(MJIEnv env, int objRef){
    System.out.println("All details states.................");
	Iterator<String> i = allDetailStates.iterator();
	while (i.hasNext()){
	  System.out.println(i.next());
	}
  }
  
  public static void printPC(MJIEnv env, int objRef, int msgRef) {
    PathCondition pc = getPC(env);
    System.out.println("PC = " + pc);
    if (pc != null) {
      pc.solve();
      System.out.println(env.getStringObject(msgRef) + pc);
	}
	else
	  System.out.println(env.getStringObject(msgRef) + " PC is null");
  }
  
  public static int getSolvedPC(MJIEnv env, int objRef) {
    PathCondition pc = getPC(env);
    if (pc != null) {
      pc.solve();
      return env.newString(pc.toString());
    }
	return env.newString("");
  }
  // the heap shape is captured in the sequence
  private static String sequence;
  // set to keep track of objects already visited; avoids revisiting
  private static HashSet<Integer> discovered;
  // set to keep track of objects already visited; avoids revisiting when travel the second time ( for debug )
  private static HashSet<Integer> discovered2;
  private static HashSet<ClassInfo> discoveredClasses; // to keep track of static fields
  // goes through heap rooted in objvRef in DFS order and prints the symbolic heap
  // does not print the static fields
  
  public static String getSymbolicRef(MJIEnv env, int objRef, int objvRef,int msgRef) {
    discovered2 = new HashSet<Integer>();
    discoveredClasses = new HashSet<ClassInfo>();
    sequence = "";	
    return toStringSymbolicRef(env,objvRef);
  }
  static String toStringSymbolicRef(MJIEnv env, int objvRef) {
    if (objvRef == -1) {
      sequence += "[";
      sequence += "null";
      sequence += "]";
    }
	else {
	  ClassInfo ci = env.getClassInfo(objvRef);
	  ElementInfo ei = DynamicArea.getHeap().get(objvRef);
	  sequence += "["+objvRef+"]";
	  if (!discovered2.contains(new Integer(objvRef))){
        discovered2.add(new Integer(objvRef));
        sequence += "{";
        FieldInfo[] fields = ci.getDeclaredInstanceFields();
        for (int i = 0; i < fields.length; i++) {
          FieldInfo fi = fields[i];
          String fname = fi.getName();
          Object attr = ei.getFieldAttr(fi);
          if (fi instanceof ReferenceFieldInfo) {
            //System.out.println("field name " + fname);
            sequence += fname + ":";
            int ref = env.getReferenceField(objvRef, fname);
			// check if field is symbolic
            if(attr!=null) // we reached a symbolic heap node
              sequence += "*";
			else
			  toStringSymbolicRef(env, ref);
		  }
          else {
            //System.out.println("field name " + fname);
            if(attr!=null) // we reached a symbolic primitive field
              sequence += fname+":"+ (Expression)attr + " ";
            else {
              sequence += fname + ":"+ fi.valueToString(ei.getFields()) + " ";
			 // etc: use FieldInfo.valueToString(fields)
			}
		  }
		}
	    sequence += "}";
	  }
	  FieldInfo[] staticFields = ci.getDeclaredStaticFields();
	  if(staticFields != null && staticFields.length>0) {
	    if (!discoveredClasses.contains(ci)){
	      sequence += "\n STATICS:";
		  discoveredClasses.add(ci);
		  sequence += "{";
		  for (int i = 0; i < staticFields.length; i++) {
            FieldInfo fi = staticFields[i];
		    String fname = fi.getName();
            StaticElementInfo sei = ci.getStaticElementInfo();
		    if (sei == null) {
			  ci.registerClass(env.getVM().getCurrentThread());
		    }
		    Object attr = sei.getFieldAttr(staticFields[i]);
            if (staticFields[i] instanceof ReferenceFieldInfo) {
              //System.out.println("field name " + fname);
		      sequence += fname + ":";
		      int refStatic = env.getStaticReferenceField(ci.getName(), fname);
              if(attr!=null) // we reached a symbolic heap node
			    sequence += "*";
			  else
			    toStringSymbolicRef(env, refStatic);
		    }
            else {
		      if(attr!=null) // we reached a symbolic primitive node
			    sequence += fname+":"+ (Expression)attr + " ";
			  else
			    sequence += fname + ":"+ fi.valueToString(sei.getFields()) + " ";
            }
		  }  
		  sequence += "}";
	    }
	  }
	}
	return sequence;
  }
	/**
	 * @author Mithun Acharya
	 */

	/**
	 * Assumes rooted heap.
	 * A rooted heap is a pair <r, h> of a root object r and
	 * a heap h such that all objects in h are reachable from r
	 *
	 * Performs a DFS over objects reachable from the root, recursively.
	 *
	 * Note:
	 * 	In DFS, discovery and finish time of nodes have parenthesis structure.
	 *
	 *
	 */
  private static String traverseRootedHeapAndGetSequence(MJIEnv env, int n) {
	// lets call the current vertex v
    //System.out.println("n = " + n);
    if (n==-1) { // vertex v is null
	  // for null vertex, discovery and finish time are the same
	  // so open and close the bracket all in one place.
      sequence += "{";
      sequence += "-1";
      sequence += "}";
    }
    else { // vertex v, is not null
      if (!discovered.contains(new Integer(n))) { // vertex v just discovered
	    // discovery time for v - so put v into the hashset and open paranthesis
        discovered.add(new Integer(n));
        sequence += "{";
        sequence += "0";
		// Now start traversing all undiscovered successors of v
        ClassInfo ci = env.getClassInfo(n);
        FieldInfo[] fields = ci.getDeclaredInstanceFields();
		//System.out.println("Field lengs " + fields.length);
        for (int i = 0; i < fields.length; i++)
       	if (fields[i] instanceof ReferenceFieldInfo) {
          String fname = fields[i].getName();
		  //System.out.println(fields[i].toString());
		  //System.out.println(n + " field name " + fname);
          int temp = env.getReferenceField(n, fname);
		  // null (short-circuited) OR successor yet undiscovered
		  if(temp==-1 || !discovered.contains(new Integer(temp))) {
		    traverseRootedHeapAndGetSequence(env, temp);
		  }
       	}
       	else {
       	  String fname = fields[i].getName();
		  ElementInfo ei = env.getElementInfo(n);
		  SymbolicInteger f = (SymbolicInteger)ei.getFieldAttr(fields[i]);
		  if ( f!= null) {
		    //System.out.println(f.getName());
		    varcount++;
		    SymbolicInteger e = new SymbolicInteger("e" + varcount);
		    constraint._addDet(Comparator.EQ,e,f);
		  }
       	}
		// All undiscovered successors of v are discovered. We are finished with v.
		// finish time for v - so, close parenthesis.
        sequence += "}";
      }
	  else { // vertex v is already discovered - do nothing
	  }
    }
    return sequence;
  }
	/**
	 *
	 * Linearizes the heap.
	 *
	 */
  private static String linearizeRootedHeap(MJIEnv env, int rootRef){
    // create a new map to store ids of visited objects
    //storing is done to avoid revisiting.
    //discovered = new HashSet<Integer>();
    // "empty" the sequence
    sequence="";
    // get the sequence for this rooted heap.
    sequence = traverseRootedHeapAndGetSequence(env, rootRef);
    return sequence;
  }
	/**
 	 * Abstraction based on heap shape
 	 */
  private static String getHeapShapeAbstractedState(MJIEnv env, int objvRef){
  // get the sequence for the rooted heap through heap linearization
    String sequence = linearizeRootedHeap(env, objvRef);
	return sequence;
  }

  /**
 	 * Simply gets the abstracted state (as a String sequence)
 	 * depending on user-defined abstraction
 	 */
  private static String getAbstractedState(MJIEnv env, int objvRef){
  // get the abstracted state as a string sequence based on the abstraction
    String abstractedState = getHeapShapeAbstractedState(env, objvRef);
    return abstractedState;
  }

	/**
	 * Perform state subsumption checking:
	 * 	
	 */
  private static void travelHeap(MJIEnv env) {
	detailState = new String();
	abstractState = new String();
    discovered = new HashSet<Integer>();
    varcount = 0;
    constraint = getPC(env);
    //discoveredClasses = new HashSet<ClassInfo>();
    DynamicArea dynamicArea = env.getVM().getDynamicArea();
    gov.nasa.jpf.jvm.Area.Iterator iterator = dynamicArea.iterator();
    //System.out.println("Total element info " + dynamicArea.count());

    while ( iterator.hasNext()) { 
      ElementInfo ei = iterator.next();
	  //System.out.println("class name " +ei.getClassInfo().getName());
	  //int ref = ei.getThisReference();
	  //System.out.println(ref);
      if (!discovered.contains(ei.getIndex()) && !ei.getClassInfo().getName().contains("java.")
	    && !ei.getClassInfo().getName().contains("[")
	    && !ei.getClassInfo().getName().contains("sun.")
	    && !ei.getClassInfo().getName().contains("gov.nasa")){
      //System.out.println("class name " +ei.getClassInfo().getName());
	  int index = ei.getIndex();  
	  detailState += getSymbolicRef(env,ei.getIndex(),ei.getIndex(),0);
	  abstractState += getAbstractedState(env, index);
	  System.out.println("Stored abstracted state: " + abstractState);
	  System.out.println("Stored detail state: " + detailState);
      }
    }
  }
  public static void storeState(MJIEnv env, int objRef) {
    travelHeap(env);
    
    ArrayList<NumericConstraint> constraints = states.get(abstractState);
    if(constraints == null){
      allDetailStates.add(detailState);
	  System.out.println(detailState);
      constraints = new ArrayList<NumericConstraint>();
    }
    else{
      //return true;
	  System.out.println("is old heap shape");
    }
	if ( varcount != 0){
	  NumericConstraint nc = (NumericConstraint)ltlConstraintSolver.getConstraint(constraint,varcount);
	  System.out.println(nc);
	  constraints.add(nc);
	  states.put(abstractState,constraints);
	    
	}
    
  }
  public static boolean storeAndCheckSubsumption(MJIEnv env,int objRef) {
	travelHeap(env);
	
    ArrayList<NumericConstraint> constraints = states.get(abstractState);
    if(constraints == null){
      allDetailStates.add(detailState);
	  System.out.println(detailState);
      constraints = new ArrayList<NumericConstraint>();
      System.out.println("is new heap shape");
      NumericConstraint nc = (NumericConstraint)ltlConstraintSolver.getConstraint(constraint,varcount);
      constraints.add(nc);
      states.put(abstractState,constraints);
      System.out.println(nc);
    }
    else{
      //return true;
	  System.out.println("is old heap shape");
      System.out.println("All constraints have the same heap shape:");
	  for (int i = 0; i < constraints.size(); i++){
      System.out.println(constraints.get(i));
	}
    if ( varcount != 0){
      NumericConstraint nc = (NumericConstraint)ltlConstraintSolver.getConstraint(constraint,varcount);
      System.out.println(nc);
      if (isConstraintSubsumed(nc, constraints)){
        return true;
      }
      else {
        constraints.add(nc);
        states.put(abstractState,constraints);
      }
    }
	else 
      return true;
    }
	return false;
  }
  private static boolean isConstraintSubsumed(NumericConstraint nc, ArrayList<NumericConstraint> constraints){
    for (int i = 0; i < constraints.size(); i++){
  	  System.out.println("Compare with constraint " + i);
  	  NumericConstraint temp = constraints.get(i);
  	  System.out.println(temp);
  	  if (temp.getExpr() != null && LTLSymbolicConstraintsGeneral.isConstraintSubsumed(nc,temp)) {
        System.out.println("IS SUBSUMED");
        return true;
  	  }
  	  else {
  	    System.out.println("IS NOT SUBSUMED");
  	  }
    }
    return false;
  }
}