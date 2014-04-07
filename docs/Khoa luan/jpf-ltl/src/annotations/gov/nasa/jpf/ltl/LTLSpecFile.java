/**
 * 
 */
package gov.nasa.jpf.ltl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate the path to LTL formulae file. A LTL formula
 * file must have the extension .ltl
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LTLSpecFile {
  String value();
}
