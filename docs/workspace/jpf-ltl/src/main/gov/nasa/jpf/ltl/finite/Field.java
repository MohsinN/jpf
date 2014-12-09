/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.symbc.numeric.Expression;

/**
 * This class represents a field in a class or a local variable in a method. Its
 * value must be updated whenever an instruction gets executed.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class Field {
  private String fullName; // Include class name with instance field and method
  // name with local variable
  private String name;
  private String methodName;
  private FieldInfo fieldInfo; // FieldInfo instance correspond to this field
  private Vector<ElementInfo> eis;
  private StackFrame stackFrame; // StackFrame instance correspond to local
  // variable
  private String type;
  private boolean isLocalVar; // True if this field is a local variable
  private boolean isStatic; // True if this is an static instance field
  private boolean isRef; // True if it is an reference
  private int position; // Index of local variable in the stack frame

  /**
   * Constructs a new instance field.
   * 
   * @param fi
   *          The {@code FieldInfo} object corresponds to this class's field.
   */
  public Field(FieldInfo fi) {
    eis = new Vector<ElementInfo>();
    fieldInfo = fi;
    fullName = fi.getFullName();
    name = fi.getName();
    isStatic = fi.isStatic();
    isRef = fi.isReference();
    isLocalVar = false;
    type = fi.getType();
    if (type.equals("java.lang.String"))
      type = "String";
    if (type.equals("java.lang.Object"))
      type = "Object";
  }

  /**
   * Constructs a new local variable.
   * 
   * @param sf
   *          The stack frame which stores this local variable.
   * @param name
   *          Name of the local variable.
   * @param type
   *          Type of the local variable. It's only a character for primitive
   *          types so we need to convert to the exact type name.
   * @param index
   *          The position of this in the stack frame.
   */
  public Field(StackFrame sf, String name, String type, int index) {
    stackFrame = sf;
    this.name = name;
    methodName = sf.getMethodInfo().getClassName() + "."
        + sf.getMethodInfo().getLongName();
    fullName = methodName + "." + name;
    this.type = getExactLocalType(type);
    isLocalVar = true;
    position = index;
  }


  /**
   * Gets the exact type name of a local variable, since it's only a character
   * for primitive types.
   * 
   * @param localType
   *          The type name returned by JPF.
   * @return The full name of variable's type.
   */
  private String getExactLocalType(String localType) {
    if (localType.equals("I"))
      return "int";
    else if (localType.equals("S"))
      return "short";
    else if (localType.equals("B"))
      return "byte";
    else if (localType.equals("J"))
      return "long";
    else if (localType.equals("D"))
      return "double";
    else if (localType.equals("F"))
      return "float";
    else if (localType.equals("C"))
      return "char";
    else if (localType.equals("Z"))
      return "boolean";
    else if (localType.equals("Ljava.lang.String"))
      return "String";
    else if (localType.equals("Ljava.lang.Object"))
      return "Object";
    return localType.substring(1);
  }

  FieldInfo getFieldInfo() {
    return fieldInfo;
  }

  /**
   * Retrieves the concrete value of local variable from its stack frame. The
   * local variable is stored in two continuous entry if its type is {@code
   * long} or {@code double} and one slot for others. If the local variable is a
   * reference so we get the value from the heap.
   * 
   * @return The concrete value of the local variable
   */
  Object getLocalValue() {
    if (!isLocalVar)
      return null;
    int value = stackFrame.getLocalVariable(position);
    if (type.equals("int") || type.equals("short") || type.equals("byte")
        || type.equals("boolean") || type.equals("char")) {
      return new Long(value);
    } else if ("float".equals(type)) {
      return new Double(Float.intBitsToFloat(value));
    } else if ("long".equals(type)) {
      return new Long(Types.intsToLong(value, stackFrame
          .getLocalVariable(position + 1)));
    } else if ("double".equals(type)) {
      return new Double(Double.longBitsToDouble(Types.intsToLong(value,
          stackFrame.getLocalVariable(position + 1))));
    } else { // reference
      if (value != -1) {
        return DynamicArea.getHeap().get(value);
      }
    }
    return null;

  }
 
  /**
   * Retrieves the type name of this.
   * 
   * @return Full type name, not the short name extracted from JPF.
   */
  public String getType() {
    return type;
  }

  /**
   * Indicates whether this is a field in a class or a local variable.
   * 
   * @return {@code true} if this is a local variable, {@code false} if this is
   *         an instance field.
   */
  public boolean isLocalVar() {
    return isLocalVar;
  }
  
  public Set<Object> getValues() {
    TreeSet<Object> result = new TreeSet<Object>();
    Object symbolicValue = null;
    if(isLocalVar) {
      symbolicValue = stackFrame.getLocalAttr(position);
      if(symbolicValue != null && symbolicValue instanceof Expression) 
        result.add(symbolicValue);
      else
        result.add(getLocalValue());
    }
    else if(isStatic()) {
      StaticElementInfo ei = fieldInfo.getClassInfo().getStaticElementInfo(); 
      symbolicValue = ei.getFieldAttr(fieldInfo);
      if(symbolicValue != null && symbolicValue instanceof Expression)
        result.add(symbolicValue);
      else 
        result.add(getConcreteValue(ei));
    }
    else {
      for(ElementInfo ei: eis) {
        symbolicValue = ei.getFieldAttr(fieldInfo);
        if(symbolicValue != null && symbolicValue instanceof Expression)
          result.add(symbolicValue);
        else 
          result.add(getConcreteValue(ei));
      }
    }
    return result;
  }
  
  public Object getValue(int instanceOrder) {
    if(isStatic())
      return getConcreteValue(fieldInfo.getClassInfo().getStaticElementInfo());
    
    if(instanceOrder < 1 || instanceOrder > eis.size())
      return null;
    else
      return getConcreteValue(eis.get(instanceOrder - 1));
  }
  
  void addElementInfo(ElementInfo ei) {
    eis.add(ei);
  }
  
  void removeElementInfo(ElementInfo ei) {
    int i = eis.indexOf(ei);
    if(i != -1) {
      eis.remove(ei);
      eis.add(i, null);
    }
  }

  /**
   * Checks if this is a reference field.
   * 
   * @return {@code true} if this is an array or a reference to an existing
   *         object, {@code false} otherwise.
   */
  public boolean isRef() {
    return isRef;
  }

  /**
   * Indicates whether this field is static or not.
   * 
   * @return {@code true} if this is a static field, {@code false} otherwise.
   */
  public boolean isStatic() {
    return isStatic;
  }

  /**
   * Updates the stack frame if this is a local variable after executing an
   * instruction.
   * 
   * @param sf
   *          The new stack frame.
   */
  void setStackFrame(StackFrame sf) {
    stackFrame = sf;
  }

  /**
   * Updates the concrete value if this is an instance field. All real values
   * are then converted to {@code double}, boolean and others integer values are
   * then converted to {@code long}. The {@code false} value is considered as
   * {@code 0} and {@code true} as not equals to {@code 0} respectively.
   * 
   * @param ei
   *          The corresponding {@code ElementInfo} object which holds the
   *          concrete value of this instance field
   * @return The concrete value after updated.
   */
  private Object getConcreteValue(ElementInfo ei) {
    Object value = null;
    if (type.equals("int"))
      value = new Long(ei.getIntField(fieldInfo));
    else if (type.equals("short"))
      value = new Long(ei.getShortField(name));
    else if (type.equals("byte"))
      value = new Long(ei.getByteField(name));
    else if (type.equals("long"))
      value = new Long(ei.getLongField(fieldInfo));
    else if (type.equals("float"))
      value = new Double(ei.getFloatField(fieldInfo));
    else if (type.equals("double"))
      value = new Double(ei.getDoubleField(fieldInfo));
    else if (type.equals("String"))
      value = ei.getStringField(name);
    else if (type.equals("boolean"))
      value = new Long(ei.getBooleanField(fieldInfo) == false ? 0 : 1);
    else if (type.equals("char"))
      value = new Long(ei.getCharField(name));
    else
      value = ei.getReferenceField(fieldInfo);
    return value;
  }

  @Override
  public String toString() {
    return fullName + "  " + type + "  " + getValues();
  }
}
