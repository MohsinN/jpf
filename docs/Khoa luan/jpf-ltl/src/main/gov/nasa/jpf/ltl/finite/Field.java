/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.Types;

/**
 * This class represent a field. It can be an instance field or a local variable
 * in a method. Its value must be updated whenever an instruction executed
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
public class Field {
  private String fullName; // Include class name with instance field and method
  // name with local variable
  private String name;
  private String methodName;
  private FieldInfo fieldInfo; // FieldInfo instance correspond to this field
  private StackFrame stackFrame; // StackFrame instance correspond to local
  // variable
  private Object attr; // The symbolic value, null if this field is concrete
  private Object value; // The concrete value
  private String type;
  private boolean isLocalVar; // True if this field is a local variable
  private boolean isStatic; // True if this is an static instance field
  private boolean isRef; // True if it is an reference
  private int position; // Index of local variable in the stack frame

  /**
   * Construct a new instance field
   * 
   * @param fi
   *          the FieldInfo correspond to this field
   */
  public Field(FieldInfo fi) {
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
   * Construct a new local variable
   * 
   * @param sf
   *          the stack frame which stores this local variable
   * @param name
   *          name of the variable
   * @param type
   *          type of local variable, it's only a character for primitive types
   *          so we need to convert to the exact type name
   * @param index
   *          index of the local var in the stack frame
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
   * @return The concrete value for this field
   */
  public Object getConcreteValue() {
    if (isLocalVar)
      return getLocalValue();
    return value;
  }

  /**
   * Get the exact type name of a local variable, since it's only a character
   * for primitive type
   * 
   * @param localType
   * @return
   */
  public String getExactLocalType(String localType) {
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

  public FieldInfo getFieldInfo() {
    return fieldInfo;
  }

  public String getFullLocalVarName() {
    return fullName;
  }

  public String getFullName() {
    return fullName;
  }

  /**
   * Get the concrete value of local variable from the stack frame The local
   * variable store in two continuous entry if it have long or double type and
   * one slot for others. If the local var is a reference so we get value from
   * the heap
   * 
   * @return The concrete value
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
   * 
   * @return the symbolic value, null if it is concrete
   */
  Object getSymbolicValue() {
    if (isLocalVar)
      return stackFrame.getLocalAttr(position);
    return attr;
  }

  public String getType() {
    return type;
  }

  public boolean isLocalVar() {
    return isLocalVar;
  }

  public boolean isRef() {
    return isRef;
  }

  public boolean isStatic() {
    return isStatic;
  }

  /**
   * Indicate whether this field is symbolic or not
   * 
   * @return true if this field is symbolic and false if it is concrete
   */
  public boolean isSymbolic() {
    return getSymbolicValue() != null;
  }

  /**
   * Set the symbolic value for instance field
   * 
   * @param attr
   */
  public void setAttr(Object attr) {
    this.attr = attr;
  }

  void setStackFrame(StackFrame sf) {
    stackFrame = sf;
  }

  /**
   * Set the concrete value if this field is an instance field
   * 
   * @param ei
   *          The correspond ElementInfo to extract the value
   * @return
   */
  public Object setValue(ElementInfo ei) {
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

  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return fullName + "  " + type + "  concrete value: " + getConcreteValue()
        + "  symbolic value: " + getSymbolicValue();
  }
}
