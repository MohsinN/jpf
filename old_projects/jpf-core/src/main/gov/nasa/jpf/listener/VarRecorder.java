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
package gov.nasa.jpf.listener;

import gov.nasa.jpf.*;
import gov.nasa.jpf.jvm.*;
import gov.nasa.jpf.jvm.bytecode.*;
import gov.nasa.jpf.util.*;
import java.util.*;

/**
 * Simple listener tool to record the values of variables as they are accessed.
 * Records the information in Step.setComment() so that when the trace is
 * written the values can be written too.
 */
public class VarRecorder extends ListenerAdapter {

  private final HashMap<ThreadInfo, String> pendingComment = new HashMap<ThreadInfo, String>();

  private final StringSetMatcher includes;
  private final StringSetMatcher excludes;
  private final boolean recordFields;
  private final boolean recordLocals;
  private final boolean recordArrays;

  private ClassInfo lastClass;
  private boolean recordClass;

  public VarRecorder(Config config) {
    includes = StringSetMatcher.getNonEmpty(config.getStringArray("var_recorder.include"));
    excludes = StringSetMatcher.getNonEmpty(config.getStringArray("var_recorder.exclude"));
    recordFields = config.getBoolean("var_recorder.fields", true);
    recordLocals = config.getBoolean("var_recorder.locals", true);
    recordArrays = config.getBoolean("var_recorder.arrays", true);
  }

  public void executeInstruction(JVM jvm) {
    Instruction inst;
    String name, value;
    byte type;

    if (!canRecord(jvm))
      return;

    inst = jvm.getLastInstruction();
    if (!(inst instanceof StoreInstruction))
      if (!(inst instanceof ArrayLoadInstruction))
        return;

    type = getType(jvm);
    name = getName(jvm, type);

    if (inst instanceof ArrayLoadInstruction) {
      setComment(jvm, name, "", "", true);
      saveVariableType(jvm, type);
    } else {
      value = getValue(jvm, type);
      setComment(jvm, name, " <- ", value, true);
    }
  }

  public void instructionExecuted(JVM jvm) {
    Instruction inst;
    String name, value;
    byte type;

    if (!canRecord(jvm))
      return;

    inst = jvm.getLastInstruction();
    if (inst instanceof StoreInstruction) {
      name = pendingComment.remove(jvm.getLastThreadInfo());
      setComment(jvm, name, "", "", false);
      return;
    }

    type  = getType(jvm);
    value = getValue(jvm, type);

    if (inst instanceof ArrayLoadInstruction)
      name = pendingComment.remove(jvm.getLastThreadInfo());
    else
      name = getName(jvm, type);

    if (isArrayReference(jvm))
      saveVariableName(jvm, name);
    else
      saveVariableType(jvm, type);

    setComment(jvm, name, " -> ", value, false);
  }

  private final void setComment(JVM jvm, String name, String operator, String value, boolean pending) {
    ThreadInfo ti;
    Step step;
    String comment;

    if (name == null)
      return;

    if (value == null)
      return;

    comment = name + operator + value;

    if (pending) {
      ti = jvm.getLastThreadInfo();
      pendingComment.put(ti, comment);
    } else {
      step = jvm.getLastStep();
      step.setComment(name + operator + value);
    }
  }

  private final boolean canRecord(JVM jvm) {
    ClassInfo ci;
    MethodInfo mi;
    Instruction inst;

    if (jvm.getLastStep() == null)
      return(false);

    inst = jvm.getLastInstruction();

    if (!(inst instanceof VariableAccessor))
      if (!(inst instanceof ArrayInstruction))
        return(false);

    mi   = inst.getMethodInfo();
    if (mi == null)
      return(false);

    ci = mi.getClassInfo();
    if (ci == null)
      return(false);

    if (lastClass != ci) {
      lastClass   = ci;
      recordClass = StringSetMatcher.isMatch(ci.getName(), includes, excludes);
    }

    return(recordClass);
  }

  private final void saveVariableName(JVM jvm, String name) {
    ThreadInfo ti;

    ti = jvm.getLastThreadInfo();
    ti.setOperandAttr(name);
  }

  private final void saveVariableType(JVM jvm, byte type) {
    ThreadInfo ti;
    StackFrame frame;
    String str;

    ti = jvm.getLastThreadInfo();
    frame = ti.getTopFrame();
    if (frame.getTopPos() < 0)
      return;

    str = encodeType(type);
    frame.setOperandAttr(str);
  }

  private final boolean isArrayReference(JVM jvm) {
    ThreadInfo ti;
    StackFrame frame;
    ElementInfo ei;
    int objRef;

    ti    = jvm.getLastThreadInfo();
    frame = ti.getTopFrame();

    if (frame.getTopPos() < 0)
      return(false);

    if (!frame.isOperandRef())
      return(false);

    objRef = frame.peek();
    if (objRef == -1)
      return(false);

    ei = DynamicArea.getHeap().get(objRef);
    if (ei == null)
      return(false);

    return(ei.isArray());
  }

  private byte getType(JVM jvm) {
    ThreadInfo ti;
    StackFrame frame;
    FieldInfo fi;
    Instruction inst;
    String type;

    ti    = jvm.getLastThreadInfo();
    frame = ti.getTopFrame();
    if ((frame.getTopPos() >= 0) && (frame.isOperandRef()))
      return(Types.T_OBJECT);

    type  = null;
    inst  = jvm.getLastInstruction();

    if (((recordLocals) && (inst instanceof LocalVariableInstruction)) ||
        ((recordFields) && (inst instanceof FieldInstruction)))
    {
       if (inst instanceof LocalVariableInstruction)
         type = ((LocalVariableInstruction) inst).getLocalVariableType();
       else {
         fi   = ((FieldInstruction) inst).getFieldInfo();
         type = fi.getType();
       }
    }

    if ((recordArrays) && (inst instanceof ArrayInstruction))
      return(getTypeFromInstruction(inst));

    if (type == null)
      return(Types.T_VOID);

    return(decodeType(type));
  }

  private final static byte getTypeFromInstruction(Instruction inst) {
    if (inst instanceof ArrayInstruction)
      return(getTypeFromInstruction((ArrayInstruction) inst));

    return(Types.T_VOID);
  }

  private final static byte getTypeFromInstruction(ArrayInstruction inst) {
    String name;

    name = inst.getClass().getName();
    name = name.substring(name.lastIndexOf('.') + 1);

    switch (name.charAt(0)) {
      case 'A': return(Types.T_OBJECT);
      case 'B': return(Types.T_BYTE);      // Could be a boolean but it is better to assume a byte.
      case 'C': return(Types.T_CHAR);
      case 'F': return(Types.T_FLOAT);
      case 'I': return(Types.T_INT);
      case 'S': return(Types.T_SHORT);
      case 'D': return(Types.T_DOUBLE);
      case 'L': return(Types.T_LONG);
    }

    return(Types.T_VOID);
  }

  private final static String encodeType(byte type) {
    switch (type) {
      case Types.T_BYTE:    return("B");
      case Types.T_CHAR:    return("C");
      case Types.T_DOUBLE:  return("D");
      case Types.T_FLOAT:   return("F");
      case Types.T_INT:     return("I");
      case Types.T_LONG:    return("J");
      case Types.T_OBJECT:  return("L");
      case Types.T_SHORT:   return("S");
      case Types.T_VOID:    return("V");
      case Types.T_BOOLEAN: return("Z");
      case Types.T_ARRAY:   return("[");
    }

    return("?");
  }

  private final static byte decodeType(String type) {
     if (type.length() != 1)
       return(Types.T_OBJECT);

     if (type.charAt(0) == '?')
       return(Types.T_OBJECT);

     return(Types.getBaseType(type));
  }

  private String getName(JVM jvm, byte type) {
    Instruction inst;
    String name;
    int index;
    boolean store;

    inst = jvm.getLastInstruction();

    if (((recordLocals) && (inst instanceof LocalVariableInstruction)) ||
        ((recordFields) && (inst instanceof FieldInstruction))) {
      name = ((VariableAccessor) inst).getVariableId();
      name = name.substring(name.lastIndexOf('.') + 1);

      return(name);
    }

    if ((recordArrays) && (inst instanceof ArrayInstruction)) {
      store  = inst instanceof StoreInstruction;
      name   = getArrayName(jvm, type, store);
      index  = getArrayIndex(jvm, type, store);
      return(name + '[' + index + ']');
    }

    return(null);
  }

  private String getValue(JVM jvm, byte type) {
    ThreadInfo ti;
    StackFrame frame;
    Instruction inst;
    int lo, hi;

    ti    = jvm.getLastThreadInfo();
    frame = ti.getTopFrame();
    inst  = jvm.getLastInstruction();

    if (((recordLocals) && (inst instanceof LocalVariableInstruction)) ||
        ((recordFields) && (inst instanceof FieldInstruction)))
    {
       if (frame.getTopPos() < 0)
         return(null);

       lo = frame.peek();
       hi = frame.getTopPos() >= 1 ? frame.peek(1) : 0;

       return(decodeValue(type, lo, hi));
    }

    if ((recordArrays) && (inst instanceof ArrayInstruction))
      return(getArrayValue(jvm, type));

    return(null);
  }

  private String getArrayName(JVM jvm, byte type, boolean store) {
    ThreadInfo ti;
    Object attr;
    int offset;

    ti     = jvm.getLastThreadInfo();
    offset = calcOffset(type, store) + 1;
    attr   = ti.getOperandAttr(offset);

    if (attr != null)
      return(attr.toString());

    return("?");
  }

  private int getArrayIndex(JVM jvm, byte type, boolean store) {
    ThreadInfo ti;
    int offset;

    ti     = jvm.getLastThreadInfo();
    offset = calcOffset(type, store);

    return(ti.peek(offset));
  }

  private final static int calcOffset(byte type, boolean store) {
    if (!store)
      return(0);

    return(Types.getTypeSize(type));
  }

  private String getArrayValue(JVM jvm, byte type) {
    ThreadInfo ti;
    StackFrame frame;
    int lo, hi;

    ti    = jvm.getLastThreadInfo();
    frame = ti.getTopFrame();
    lo    = frame.peek();
    hi    = frame.getTopPos() >= 1 ? frame.peek(1) : 0;

    return(decodeValue(type, lo, hi));
  }

  private final static String decodeValue(byte type, int lo, int hi) {
    switch (type) {
      case Types.T_ARRAY:   return(null);
      case Types.T_VOID:    return(null);

      case Types.T_BOOLEAN: return(String.valueOf(Types.intToBoolean(lo)));
      case Types.T_BYTE:    return(String.valueOf(lo));
      case Types.T_CHAR:    return(String.valueOf((char) lo));
      case Types.T_DOUBLE:  return(String.valueOf(Types.intsToDouble(lo, hi)));
      case Types.T_FLOAT:   return(String.valueOf(Types.intToFloat(lo)));
      case Types.T_INT:     return(String.valueOf(lo));
      case Types.T_LONG:    return(String.valueOf(Types.intsToLong(lo, hi)));
      case Types.T_SHORT:   return(String.valueOf(lo));

      case Types.T_OBJECT:
        ElementInfo ei = DynamicArea.getHeap().get(lo);
        if (ei == null)
          return(null);

        ClassInfo ci = ei.getClassInfo();
        if (ci == null)
          return(null);

        if (ci.getName().equals("java.lang.String"))
          return('"' + ei.asString() + '"');

        return(ei.toString());

      default:
        System.err.println("Unknown type: " + type);
        return(null);
     }
  }
}
