grammar DetectParam;

tokens {
    INT     ='int';
    LONG    ='long';
    SHORT   ='short';
    BYTE    ='byte';
    CHAR    ='char';
    FLOAT   ='float';
    DOUBLE  ='double';
    STRING  ='String';
}

@header{
import java.util.Vector;
import java.io.IOException;
}
 
@members {

   Vector<String> atoms = new Vector<String>();
          public Vector<String> getAtoms() {
    		return atoms;
  	}
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
 
 type	:	INT | LONG | SHORT | BYTE | CHAR | FLOAT | DOUBLE | STRING | ID;

detectParameters 
	:	ID '('exp*')' {System.out.println("trovato exp");}
	;

exp	:	','? s=type ','?	{atoms.add($s.text);System.out.println("trovato token "+$s.text);};

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
ID :	('a'..'z' | 'A'..'Z')* ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

