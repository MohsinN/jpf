/*
* @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
*/

grammar LTLSpec;

options	{
	language=Java;
	backtrack=true;
}

import SymbolicAtom;

@header {
	package gov.nasa.jpf.ltl.finite;
	import java.util.Vector;
	import gov.nasa.ltl.trans.Formula;
	import gov.nasa.jpf.symbc.numeric.RelationAtom;
}

@members {
	Vector<String> atoms = new Vector<String>();
	public Vector<String> getAtoms() {
		return atoms;
		}
		
	public Vector<String> getAttributes() {
		return gSymbolicAtom.getAttributes();
	}
	
	public Formula<String> unaryFormula(String oper, Formula<String> f) {
		if(oper.equals("!"))
			return Formula.Not(f);
		else if(oper.equals("[]"))
			return Formula.Always(f);
		else if(oper.equals("X"))
			return Formula.Next(f);
		else
			return Formula.Eventually(f);		
	}
		
	public Formula<String> binFormula(Formula<String> f1, String oper, Formula<String> f2) {
		if(oper.equals("/\\") || oper.equals("&&"))	
			return Formula.And(f1, f2);
		else if(oper.equals("\\/") || oper.equals("||"))
			return Formula.Or(f1, f2);
		else if(oper.equals("U"))
			return Formula.Until(f1, f2);
		else if(oper.equals("V"))
			return Formula.Release(f1, f2);
		else if(oper.equals("W"))
			return Formula.WUntil(f1, f2);
		else
			return Formula.WRelease(f1, f2);
		}
}

/** parser rules *****/


ltlSpec returns [Formula<String> f]
	: b=binaryFormula						{f = $b.f; }
	;

unaryOperator returns [String operator]
	:	NOT					{operator = "!"; }
	| NEXT				{operator = "X";	}
	| ALWAYS			{operator = "[]"; }
	| EVENTUALLY	{operator = "<>"; }
	;

binaryFormula returns [Formula<String> f]
	:	l1=logicalFormula 	{f = $l1.f; }
	(i=IMPLIES
	l2=binaryFormula			{f = Formula.Implies(f, $l2.f); }
	)?
	;

logicalFormula returns [Formula<String> f]
	:	t1=andFormula													{f = $t1.f; }
	((OR | ROBBYJO_OR)	t2=logicalFormula 	{f = binFormula(f, "||", $t2.f); }
	)?
	;
	
andFormula returns [Formula<String> f]
	: t1=temporalFormula 											{f = $t1.f; }
	((AND | ROBBYJO_AND) t2=andFormula 	{f = binFormula(f, "&&", $t2.f); }
	)?
	;
	
temporalFormula returns [Formula<String> f]
	:	p1=releaseFormula														{f = $p1.f; } 
	( UNTIL	p2=temporalFormula											{f = binFormula(f, "U", $p2.f); }
	|	WEAK_UNTIL p3=temporalFormula 								{f = binFormula(f, "W", $p3.f); }
	)?
	;

releaseFormula returns [Formula<String> f]
	: p1=proposition						{f = $p1.f; }
	( RELEASE p2=releaseFormula				{f = binFormula(f, "V", $p2.f); }
	| WEAK_RELEASE p3=releaseFormula		{f = binFormula(f, "M", $p3.f); }
	)?
	;
	
proposition	returns [Formula<String> f]
	:	TRUE																{f = Formula.True(); }
	| FALSE																{f = Formula.False(); }
	| s=atom															{f = Formula.Proposition($s.s.getText()); atoms.add($s.s.getText()); }
	| u1=unaryOperator l3=proposition					{f = unaryFormula($u1.operator, $l3.f); }
	| '(' l4=ltlSpec ')'									{f = $l4.f; }
	;
	
/** lexer rules **/

AND						:	'/\\';
ROBBYJO_AND		:	'&&';
OR						:	'\\/';
ROBBYJO_OR		:	'||';
UNTIL					:	'U';
WEAK_UNTIL		:	'W';
RELEASE				:	'V';
WEAK_RELEASE	:	'M';
NOT						:	'!';
NEXT					:	'X';
ALWAYS				:	'[]';
EVENTUALLY		:	'<>';
IMPLIES				:	'->';

TRUE					:	'true';
FALSE					:'false';

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;
	
INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
