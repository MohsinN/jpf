/*
* @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
*/
parser grammar SymbolicAtom;

options	{
	language=Java;
	backtrack=true;
}

@members {
	private Vector<String> attributes = new Vector<String>();
  private String methodName;
    
  public LTLSpec_SymbolicAtom(TokenStream input, String methodName) {
   	this(input, null, null);
    this.methodName = methodName;
  }
    
  public Vector<String> getAttributes() {
    return attributes;
  }
}
atom returns [Atom s]
	:
	| m=method									{s = new MethodAtom($m.s, methodName);}
	| v=var									
		{											
		attributes.add($v.value);
		s = new RelationAtom.NE(new Operand.Var($v.value), new Operand.Const(new Long(0)));
	}
	| e1=expr	
	(
	'==' e2=expr							{s = new RelationAtom.EQ($e1.o, $e2.o); }
	| '!=' e3=expr						{s = new RelationAtom.NE($e1.o, $e3.o); }
	| '>=' e4=expr						{s = new RelationAtom.GE($e1.o, $e4.o); }
	| '<=' e5=expr						{s = new RelationAtom.LE($e1.o, $e5.o); }
	| '>' e6=expr							{s = new RelationAtom.GT($e1.o, $e6.o); }
	| '<' e7=expr							{s = new RelationAtom.LT($e1.o, $e7.o); }
	)
	;

method returns [String s]
	:
	i1=ID											{s = $i1.text; }
	('.' i2=ID 								{s += "." + $i2.text; }
	)+
	(
	(	'(' 										{s += "("; }
	(i3=type ','							{s += $i3.s + ","; }
	)*
	i4=type ')')							{s += $i4.s + ")"; s = s.replaceAll("null,", ""); }
	|
	'(' ')'										{s += "()"; }
	)
	;
	
type returns [String s]
	: i=ID 										{s = $i.text; }
	 (ALWAYS									{s += "[]"; }
	 )*								
	 ;
	
expr returns [Operand o]
	:
	 m1=mult									{o = $m1.o; }
	( '+' m2=mult							{o = new BinaryExpression.Plus(o, $m2.o); }
	| '-' m3=mult							{o = new BinaryExpression.Minus(o, $m3.o); }
	)*
	;
	
mult returns [Operand o]
	:
	f1=factor									{o = $f1.o; }			
	( '*' f2=factor						{o = new BinaryExpression.Mult(o, $f2.o); }	
	| '/' f3=factor						{o = new BinaryExpression.Div(o, $f3.o); }
	)*
	;

factor	returns [Operand o]
	:	
	'(' e=expr ')'						{o = $e.o; o.setText("(" + $e.o.getText() + ")"); }															
	| v=var											{o = new Operand.Var($v.value); attributes.add($v.value); } 
	| i=INT										{o = new Operand.Const(Long.valueOf($i.text)); }
	| f=FLOAT									{o = new Operand.Const(Double.valueOf($f.text)); }				
	;
	
var returns [String value]
	:
	((
	var1=ID										{value = $var1.text; }
	( '.' var2=ID							{value += "." + $var2.text; }
	)+
	)
	|
	(
	m1=method 								{value = $m1.s; }
	'.' var3=ID								{value += "." + $var3.text; }
	))
	(
	'[' i2=INT ']'								{value += "[" + $i2.text + "]"; }
	)*
	;
