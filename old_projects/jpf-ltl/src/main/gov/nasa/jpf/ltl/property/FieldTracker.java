/**
 * 
 */
package gov.nasa.jpf.ltl.property;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.symbc.numeric.Expression;

/**
 * @author Khanh Bui
 */
public class FieldTracker {
	private static Hashtable<String, Field> fieldList;
	private static Vector<String> fields;

	public static void reset() {
		fieldList = new Hashtable<String, Field>();
		fields = null;
	}

	/**
	 * Returns the information of a field or a local variable at JPF runtime. This
	 * method is used when we check the guard condition.
	 * 
	 * @param fullName
	 *          Full name of the needed field or variable.
	 * @return A {@link Field} object representing this field, {@code null}
	 *         otherwise.
	 * @see gov.nasa.jpf.ltl.atom.Operand.Var#Var(String)
	 */
	public static Field getField(String fullName) {
		return fieldList.get(fullName);
	}

	public static void setFields(Vector<String> fields) {
		System.err.println("----- setFields: " + fields);
		FieldTracker.fields = fields;
	}

	/**
	 * Extracts the FieldInfo instances of fields which are appeared in the LTL formula
	 * 
	 * @param ci
	 */
	public static void updateFieldList(ClassInfo ci) {
		filterFields(ci.getDeclaredInstanceFields());
		filterFields(ci.getDeclaredStaticFields());
	}

	/**
	 * Extracts the local variable information which appeared in the LTL spec. at
	 * JPF runtime
	 * 
	 * @param sf
	 *          The stack frame which store the local variable
	 */
	public static void extractLocalVar(StackFrame sf) {
		if (sf == null) {
			return;
		}
		MethodInfo mi = sf.getMethodInfo();
		String methodName = mi.getClassName() + "." + mi.getLongName();
		String[] localNames = sf.getLocalVariableNames();
		if (localNames == null || fields == null) {
			return;
		}
		for (String localField: fields) {
			int position = 0; // the index of local variable in its stack frame
			for (String localVarName: localNames) {
				String localFullName = methodName + "." + localVarName;
				if (localField.equals(localFullName)) {
					// if a local variable is already seen formerly, we only need to update its stack frame
					Field field = fieldList.get(localFullName);
					if (field == null) {
						field = new Field(sf, localVarName, sf.getLocalVariableType(localVarName), position);
						fieldList.put(localFullName, field);
					}
					else {
						field.setStackFrame(sf); // stack frame of a local variable is not fixed
					}
				}
				position++;
			}
		}
	}

	/**
	 * Gets the field that appear in the LTL formulae from the {@code FieldInfo}
	 * list
	 * 
	 * @param fieldInfoList
	 *          All the fields in a loaded class
	 */
	protected static void filterFields(FieldInfo[] fieldInfoList) {
		if (fields == null) {
			return;
		}
		for (FieldInfo field: fieldInfoList) {
			if (fields.contains(field.getFullName())) {
				fieldList.put(field.getFullName(), new Field(field));
			}
		}
	}

	public static void objectCreated(JVM vm) {
		ElementInfo ei = vm.getLastElementInfo();

		Enumeration<Field> fields = fieldList.elements();
		while (fields.hasMoreElements()) {
			Field field = fields.nextElement();
			if (field.isLocalVar()) {
				continue;
			}
			if (field.getFieldInfo().getClassInfo().getUniqueId() == ei.getClassInfo().getUniqueId()) {
				field.addElementInfo(ei);
			}
		}
	}

	public static void objectReleased(JVM vm) {
		ElementInfo ei = vm.getLastElementInfo();

		Enumeration<Field> fields = fieldList.elements();
		while (fields.hasMoreElements()) {
			Field field = fields.nextElement();
			if (field.isLocalVar()) {
				continue;
			}
			if (field.getFieldInfo().getClassInfo().getUniqueId() == ei.getClassInfo().getUniqueId()) {
				field.removeElementInfo(ei);
			}
		}
	}

	/*
	 * Just for debugging purpose
	 */
	public static void printFields() {
		Enumeration<Field> values = fieldList.elements();
		System.err.println("printFields: size=" + fieldList.size() + ", values=" + values);
		while (values.hasMoreElements()) {
			System.err.println("printFields: " + values.nextElement());
		}
	}

}
