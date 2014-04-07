// $ANTLR 3.2 Sep 23, 2009 12:02:23 SymbolicAtom.g 2010-08-09 16:41:13

	package gov.nasa.jpf.ltl.finite;
	import java.util.Vector;
	import gov.nasa.ltl.trans.Formula;
	import gov.nasa.jpf.symbc.numeric.RelationAtom;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class LTLSpec_SymbolicAtom extends Parser {
    public static final int EVENTUALLY=10;
    public static final int EXPONENT=22;
    public static final int OCTAL_ESC=29;
    public static final int FLOAT=7;
    public static final int NOT=8;
    public static final int ROBBYJO_AND=15;
    public static final int ID=4;
    public static final int AND=14;
    public static final int EOF=-1;
    public static final int RELEASE=18;
    public static final int ESC_SEQ=25;
    public static final int ALWAYS=5;
    public static final int NEXT=9;
    public static final int COMMENT=23;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int IMPLIES=11;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int UNICODE_ESC=28;
    public static final int HEX_DIGIT=27;
    public static final int INT=6;
    public static final int TRUE=20;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=24;
    public static final int T__34=34;
    public static final int WEAK_UNTIL=17;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int UNTIL=16;
    public static final int OR=12;
    public static final int WEAK_RELEASE=19;
    public static final int ROBBYJO_OR=13;
    public static final int FALSE=21;
    public static final int STRING=26;

    // delegates
    // delegators
    public LTLSpecParser gLTLSpec;
    public LTLSpecParser gParent;


        public LTLSpec_SymbolicAtom(TokenStream input, LTLSpecParser gLTLSpec) {
            this(input, new RecognizerSharedState(), gLTLSpec);
        }
        public LTLSpec_SymbolicAtom(TokenStream input, RecognizerSharedState state, LTLSpecParser gLTLSpec) {
            super(input, state);
            this.gLTLSpec = gLTLSpec;
             
            gParent = gLTLSpec;
        }
        

    public String[] getTokenNames() { return LTLSpecParser.tokenNames; }
    public String getGrammarFileName() { return "SymbolicAtom.g"; }


    	private Vector<String> attributes = new Vector<String>();
      private String methodName;
        
      public LTLSpec_SymbolicAtom(TokenStream input, String methodName) {
       	this(input, null, null);
        this.methodName = methodName;
      }
        
      public Vector<String> getAttributes() {
        return attributes;
      }



    // $ANTLR start "atom"
    // SymbolicAtom.g:24:1: atom returns [Atom s] : ( | m= method | v= var | e1= expr ( '==' e2= expr | '!=' e3= expr | '>=' e4= expr | '<=' e5= expr | '>' e6= expr | '<' e7= expr ) );
    public final Atom atom() throws RecognitionException {
        Atom s = null;

        String m = null;

        String v = null;

        Operand e1 = null;

        Operand e2 = null;

        Operand e3 = null;

        Operand e4 = null;

        Operand e5 = null;

        Operand e6 = null;

        Operand e7 = null;


        try {
            // SymbolicAtom.g:25:2: ( | m= method | v= var | e1= expr ( '==' e2= expr | '!=' e3= expr | '>=' e4= expr | '<=' e5= expr | '>' e6= expr | '<' e7= expr ) )
            int alt2=4;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // SymbolicAtom.g:26:2: 
                    {
                    }
                    break;
                case 2 :
                    // SymbolicAtom.g:26:4: m= method
                    {
                    pushFollow(FOLLOW_method_in_atom46);
                    m=method();

                    state._fsp--;
                    if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                      s = new MethodAtom(m, methodName);
                    }

                    }
                    break;
                case 3 :
                    // SymbolicAtom.g:27:4: v= var
                    {
                    pushFollow(FOLLOW_var_in_atom63);
                    v=var();

                    state._fsp--;
                    if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                      											
                      		attributes.add(v);
                      		s = new RelationAtom.NE(new Operand.Var(v), new Operand.Const(new Long(0)));
                      	
                    }

                    }
                    break;
                case 4 :
                    // SymbolicAtom.g:32:4: e1= expr ( '==' e2= expr | '!=' e3= expr | '>=' e4= expr | '<=' e5= expr | '>' e6= expr | '<' e7= expr )
                    {
                    pushFollow(FOLLOW_expr_in_atom83);
                    e1=expr();

                    state._fsp--;
                    if (state.failed) return s;
                    // SymbolicAtom.g:33:2: ( '==' e2= expr | '!=' e3= expr | '>=' e4= expr | '<=' e5= expr | '>' e6= expr | '<' e7= expr )
                    int alt1=6;
                    switch ( input.LA(1) ) {
                    case 30:
                        {
                        alt1=1;
                        }
                        break;
                    case 31:
                        {
                        alt1=2;
                        }
                        break;
                    case 32:
                        {
                        alt1=3;
                        }
                        break;
                    case 33:
                        {
                        alt1=4;
                        }
                        break;
                    case 34:
                        {
                        alt1=5;
                        }
                        break;
                    case 35:
                        {
                        alt1=6;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return s;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 1, 0, input);

                        throw nvae;
                    }

                    switch (alt1) {
                        case 1 :
                            // SymbolicAtom.g:34:2: '==' e2= expr
                            {
                            match(input,30,FOLLOW_30_in_atom90); if (state.failed) return s;
                            pushFollow(FOLLOW_expr_in_atom94);
                            e2=expr();

                            state._fsp--;
                            if (state.failed) return s;
                            if ( state.backtracking==0 ) {
                              s = new RelationAtom.EQ(e1, e2); 
                            }

                            }
                            break;
                        case 2 :
                            // SymbolicAtom.g:35:4: '!=' e3= expr
                            {
                            match(input,31,FOLLOW_31_in_atom107); if (state.failed) return s;
                            pushFollow(FOLLOW_expr_in_atom111);
                            e3=expr();

                            state._fsp--;
                            if (state.failed) return s;
                            if ( state.backtracking==0 ) {
                              s = new RelationAtom.NE(e1, e3); 
                            }

                            }
                            break;
                        case 3 :
                            // SymbolicAtom.g:36:4: '>=' e4= expr
                            {
                            match(input,32,FOLLOW_32_in_atom123); if (state.failed) return s;
                            pushFollow(FOLLOW_expr_in_atom127);
                            e4=expr();

                            state._fsp--;
                            if (state.failed) return s;
                            if ( state.backtracking==0 ) {
                              s = new RelationAtom.GE(e1, e4); 
                            }

                            }
                            break;
                        case 4 :
                            // SymbolicAtom.g:37:4: '<=' e5= expr
                            {
                            match(input,33,FOLLOW_33_in_atom139); if (state.failed) return s;
                            pushFollow(FOLLOW_expr_in_atom143);
                            e5=expr();

                            state._fsp--;
                            if (state.failed) return s;
                            if ( state.backtracking==0 ) {
                              s = new RelationAtom.LE(e1, e5); 
                            }

                            }
                            break;
                        case 5 :
                            // SymbolicAtom.g:38:4: '>' e6= expr
                            {
                            match(input,34,FOLLOW_34_in_atom155); if (state.failed) return s;
                            pushFollow(FOLLOW_expr_in_atom159);
                            e6=expr();

                            state._fsp--;
                            if (state.failed) return s;
                            if ( state.backtracking==0 ) {
                              s = new RelationAtom.GT(e1, e6); 
                            }

                            }
                            break;
                        case 6 :
                            // SymbolicAtom.g:39:4: '<' e7= expr
                            {
                            match(input,35,FOLLOW_35_in_atom172); if (state.failed) return s;
                            pushFollow(FOLLOW_expr_in_atom176);
                            e7=expr();

                            state._fsp--;
                            if (state.failed) return s;
                            if ( state.backtracking==0 ) {
                              s = new RelationAtom.LT(e1, e7); 
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return s;
    }
    // $ANTLR end "atom"


    // $ANTLR start "method"
    // SymbolicAtom.g:43:1: method returns [String s] : i1= ID ( '.' i2= ID )+ ( ( '(' (i3= type ',' )* i4= type ')' ) | '(' ')' ) ;
    public final String method() throws RecognitionException {
        String s = null;

        Token i1=null;
        Token i2=null;
        String i3 = null;

        String i4 = null;


        try {
            // SymbolicAtom.g:44:2: (i1= ID ( '.' i2= ID )+ ( ( '(' (i3= type ',' )* i4= type ')' ) | '(' ')' ) )
            // SymbolicAtom.g:45:2: i1= ID ( '.' i2= ID )+ ( ( '(' (i3= type ',' )* i4= type ')' ) | '(' ')' )
            {
            i1=(Token)match(input,ID,FOLLOW_ID_in_method205); if (state.failed) return s;
            if ( state.backtracking==0 ) {
              s = (i1!=null?i1.getText():null); 
            }
            // SymbolicAtom.g:46:2: ( '.' i2= ID )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==36) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // SymbolicAtom.g:46:3: '.' i2= ID
            	    {
            	    match(input,36,FOLLOW_36_in_method221); if (state.failed) return s;
            	    i2=(Token)match(input,ID,FOLLOW_ID_in_method225); if (state.failed) return s;
            	    if ( state.backtracking==0 ) {
            	      s += "." + (i2!=null?i2.getText():null); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
            	    if (state.backtracking>0) {state.failed=true; return s;}
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            // SymbolicAtom.g:48:2: ( ( '(' (i3= type ',' )* i4= type ')' ) | '(' ')' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==37) ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1==39) ) {
                    alt5=2;
                }
                else if ( (LA5_1==ID) ) {
                    alt5=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return s;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // SymbolicAtom.g:49:2: ( '(' (i3= type ',' )* i4= type ')' )
                    {
                    // SymbolicAtom.g:49:2: ( '(' (i3= type ',' )* i4= type ')' )
                    // SymbolicAtom.g:49:4: '(' (i3= type ',' )* i4= type ')'
                    {
                    match(input,37,FOLLOW_37_in_method247); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                      s += "("; 
                    }
                    // SymbolicAtom.g:50:2: (i3= type ',' )*
                    loop4:
                    do {
                        int alt4=2;
                        alt4 = dfa4.predict(input);
                        switch (alt4) {
                    	case 1 :
                    	    // SymbolicAtom.g:50:3: i3= type ','
                    	    {
                    	    pushFollow(FOLLOW_type_in_method265);
                    	    i3=type();

                    	    state._fsp--;
                    	    if (state.failed) return s;
                    	    match(input,38,FOLLOW_38_in_method267); if (state.failed) return s;
                    	    if ( state.backtracking==0 ) {
                    	      s += i3 + ","; 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    pushFollow(FOLLOW_type_in_method284);
                    i4=type();

                    state._fsp--;
                    if (state.failed) return s;
                    match(input,39,FOLLOW_39_in_method286); if (state.failed) return s;

                    }

                    if ( state.backtracking==0 ) {
                      s += i4 + ")"; s = s.replaceAll("null,", ""); 
                    }

                    }
                    break;
                case 2 :
                    // SymbolicAtom.g:54:2: '(' ')'
                    {
                    match(input,37,FOLLOW_37_in_method301); if (state.failed) return s;
                    match(input,39,FOLLOW_39_in_method303); if (state.failed) return s;
                    if ( state.backtracking==0 ) {
                      s += "()"; 
                    }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return s;
    }
    // $ANTLR end "method"


    // $ANTLR start "type"
    // SymbolicAtom.g:58:1: type returns [String s] : i= ID ( ALWAYS )* ;
    public final String type() throws RecognitionException {
        String s = null;

        Token i=null;

        try {
            // SymbolicAtom.g:59:2: (i= ID ( ALWAYS )* )
            // SymbolicAtom.g:59:4: i= ID ( ALWAYS )*
            {
            i=(Token)match(input,ID,FOLLOW_ID_in_type335); if (state.failed) return s;
            if ( state.backtracking==0 ) {
              s = (i!=null?i.getText():null); 
            }
            // SymbolicAtom.g:60:3: ( ALWAYS )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==ALWAYS) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // SymbolicAtom.g:60:4: ALWAYS
            	    {
            	    match(input,ALWAYS,FOLLOW_ALWAYS_in_type352); if (state.failed) return s;
            	    if ( state.backtracking==0 ) {
            	      s += "[]"; 
            	    }

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return s;
    }
    // $ANTLR end "type"


    // $ANTLR start "expr"
    // SymbolicAtom.g:64:1: expr returns [Operand o] : m1= mult ( '+' m2= mult | '-' m3= mult )* ;
    public final Operand expr() throws RecognitionException {
        Operand o = null;

        Operand m1 = null;

        Operand m2 = null;

        Operand m3 = null;


        try {
            // SymbolicAtom.g:65:2: (m1= mult ( '+' m2= mult | '-' m3= mult )* )
            // SymbolicAtom.g:66:3: m1= mult ( '+' m2= mult | '-' m3= mult )*
            {
            pushFollow(FOLLOW_mult_in_expr396);
            m1=mult();

            state._fsp--;
            if (state.failed) return o;
            if ( state.backtracking==0 ) {
              o = m1; 
            }
            // SymbolicAtom.g:67:2: ( '+' m2= mult | '-' m3= mult )*
            loop7:
            do {
                int alt7=3;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==40) ) {
                    alt7=1;
                }
                else if ( (LA7_0==41) ) {
                    alt7=2;
                }


                switch (alt7) {
            	case 1 :
            	    // SymbolicAtom.g:67:4: '+' m2= mult
            	    {
            	    match(input,40,FOLLOW_40_in_expr411); if (state.failed) return o;
            	    pushFollow(FOLLOW_mult_in_expr415);
            	    m2=mult();

            	    state._fsp--;
            	    if (state.failed) return o;
            	    if ( state.backtracking==0 ) {
            	      o = new BinaryExpression.Plus(o, m2); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // SymbolicAtom.g:68:4: '-' m3= mult
            	    {
            	    match(input,41,FOLLOW_41_in_expr428); if (state.failed) return o;
            	    pushFollow(FOLLOW_mult_in_expr432);
            	    m3=mult();

            	    state._fsp--;
            	    if (state.failed) return o;
            	    if ( state.backtracking==0 ) {
            	      o = new BinaryExpression.Minus(o, m3); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "expr"


    // $ANTLR start "mult"
    // SymbolicAtom.g:72:1: mult returns [Operand o] : f1= factor ( '*' f2= factor | '/' f3= factor )* ;
    public final Operand mult() throws RecognitionException {
        Operand o = null;

        Operand f1 = null;

        Operand f2 = null;

        Operand f3 = null;


        try {
            // SymbolicAtom.g:73:2: (f1= factor ( '*' f2= factor | '/' f3= factor )* )
            // SymbolicAtom.g:74:2: f1= factor ( '*' f2= factor | '/' f3= factor )*
            {
            pushFollow(FOLLOW_factor_in_mult463);
            f1=factor();

            state._fsp--;
            if (state.failed) return o;
            if ( state.backtracking==0 ) {
              o = f1; 
            }
            // SymbolicAtom.g:75:2: ( '*' f2= factor | '/' f3= factor )*
            loop8:
            do {
                int alt8=3;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==42) ) {
                    alt8=1;
                }
                else if ( (LA8_0==43) ) {
                    alt8=2;
                }


                switch (alt8) {
            	case 1 :
            	    // SymbolicAtom.g:75:4: '*' f2= factor
            	    {
            	    match(input,42,FOLLOW_42_in_mult481); if (state.failed) return o;
            	    pushFollow(FOLLOW_factor_in_mult485);
            	    f2=factor();

            	    state._fsp--;
            	    if (state.failed) return o;
            	    if ( state.backtracking==0 ) {
            	      o = new BinaryExpression.Mult(o, f2); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // SymbolicAtom.g:76:4: '/' f3= factor
            	    {
            	    match(input,43,FOLLOW_43_in_mult498); if (state.failed) return o;
            	    pushFollow(FOLLOW_factor_in_mult502);
            	    f3=factor();

            	    state._fsp--;
            	    if (state.failed) return o;
            	    if ( state.backtracking==0 ) {
            	      o = new BinaryExpression.Div(o, f3); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "mult"


    // $ANTLR start "factor"
    // SymbolicAtom.g:80:1: factor returns [Operand o] : ( '(' e= expr ')' | v= var | i= INT | f= FLOAT );
    public final Operand factor() throws RecognitionException {
        Operand o = null;

        Token i=null;
        Token f=null;
        Operand e = null;

        String v = null;


        try {
            // SymbolicAtom.g:81:2: ( '(' e= expr ')' | v= var | i= INT | f= FLOAT )
            int alt9=4;
            switch ( input.LA(1) ) {
            case 37:
                {
                alt9=1;
                }
                break;
            case ID:
                {
                alt9=2;
                }
                break;
            case INT:
                {
                alt9=3;
                }
                break;
            case FLOAT:
                {
                alt9=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return o;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // SymbolicAtom.g:82:2: '(' e= expr ')'
                    {
                    match(input,37,FOLLOW_37_in_factor530); if (state.failed) return o;
                    pushFollow(FOLLOW_expr_in_factor534);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return o;
                    match(input,39,FOLLOW_39_in_factor536); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                      o = e; o.setText("(" + e.getText() + ")"); 
                    }

                    }
                    break;
                case 2 :
                    // SymbolicAtom.g:83:4: v= var
                    {
                    pushFollow(FOLLOW_var_in_factor565);
                    v=var();

                    state._fsp--;
                    if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                      o = new Operand.Var(v); attributes.add(v); 
                    }

                    }
                    break;
                case 3 :
                    // SymbolicAtom.g:84:4: i= INT
                    {
                    i=(Token)match(input,INT,FOLLOW_INT_in_factor585); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                      o = new Operand.Const(Long.valueOf((i!=null?i.getText():null))); 
                    }

                    }
                    break;
                case 4 :
                    // SymbolicAtom.g:85:4: f= FLOAT
                    {
                    f=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_factor603); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                      o = new Operand.Const(Double.valueOf((f!=null?f.getText():null))); 
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "factor"


    // $ANTLR start "var"
    // SymbolicAtom.g:88:1: var returns [String value] : ( (var1= ID ( '.' var2= ID )+ ) | (m1= method '.' var3= ID ) ) ( '[' i2= INT ']' )* ;
    public final String var() throws RecognitionException {
        String value = null;

        Token var1=null;
        Token var2=null;
        Token var3=null;
        Token i2=null;
        String m1 = null;


        try {
            // SymbolicAtom.g:89:2: ( ( (var1= ID ( '.' var2= ID )+ ) | (m1= method '.' var3= ID ) ) ( '[' i2= INT ']' )* )
            // SymbolicAtom.g:90:2: ( (var1= ID ( '.' var2= ID )+ ) | (m1= method '.' var3= ID ) ) ( '[' i2= INT ']' )*
            {
            // SymbolicAtom.g:90:2: ( (var1= ID ( '.' var2= ID )+ ) | (m1= method '.' var3= ID ) )
            int alt11=2;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // SymbolicAtom.g:90:3: (var1= ID ( '.' var2= ID )+ )
                    {
                    // SymbolicAtom.g:90:3: (var1= ID ( '.' var2= ID )+ )
                    // SymbolicAtom.g:91:2: var1= ID ( '.' var2= ID )+
                    {
                    var1=(Token)match(input,ID,FOLLOW_ID_in_var640); if (state.failed) return value;
                    if ( state.backtracking==0 ) {
                      value = (var1!=null?var1.getText():null); 
                    }
                    // SymbolicAtom.g:92:2: ( '.' var2= ID )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==36) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // SymbolicAtom.g:92:4: '.' var2= ID
                    	    {
                    	    match(input,36,FOLLOW_36_in_var656); if (state.failed) return value;
                    	    var2=(Token)match(input,ID,FOLLOW_ID_in_var660); if (state.failed) return value;
                    	    if ( state.backtracking==0 ) {
                    	      value += "." + (var2!=null?var2.getText():null); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                    	    if (state.backtracking>0) {state.failed=true; return value;}
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // SymbolicAtom.g:96:2: (m1= method '.' var3= ID )
                    {
                    // SymbolicAtom.g:96:2: (m1= method '.' var3= ID )
                    // SymbolicAtom.g:97:2: m1= method '.' var3= ID
                    {
                    pushFollow(FOLLOW_method_in_var686);
                    m1=method();

                    state._fsp--;
                    if (state.failed) return value;
                    if ( state.backtracking==0 ) {
                      value = m1; 
                    }
                    match(input,36,FOLLOW_36_in_var699); if (state.failed) return value;
                    var3=(Token)match(input,ID,FOLLOW_ID_in_var703); if (state.failed) return value;
                    if ( state.backtracking==0 ) {
                      value += "." + (var3!=null?var3.getText():null); 
                    }

                    }


                    }
                    break;

            }

            // SymbolicAtom.g:100:2: ( '[' i2= INT ']' )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==44) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // SymbolicAtom.g:101:2: '[' i2= INT ']'
            	    {
            	    match(input,44,FOLLOW_44_in_var722); if (state.failed) return value;
            	    i2=(Token)match(input,INT,FOLLOW_INT_in_var726); if (state.failed) return value;
            	    match(input,45,FOLLOW_45_in_var728); if (state.failed) return value;
            	    if ( state.backtracking==0 ) {
            	      value += "[" + (i2!=null?i2.getText():null) + "]"; 
            	    }

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "var"

    // Delegated rules


    protected DFA2 dfa2 = new DFA2(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA11 dfa11 = new DFA11(this);
    static final String DFA2_eotS =
        "\23\uffff";
    static final String DFA2_eofS =
        "\1\1\4\uffff\1\7\4\uffff\1\16\1\uffff\1\7\3\uffff\1\16\1\uffff"+
        "\1\7";
    static final String DFA2_minS =
        "\1\4\1\uffff\1\44\1\uffff\1\4\1\13\1\6\1\uffff\1\4\1\55\1\13\1"+
        "\5\1\13\1\4\1\uffff\1\5\1\13\1\4\1\13";
    static final String DFA2_maxS =
        "\1\47\1\uffff\1\44\1\uffff\1\4\1\54\1\6\1\uffff\1\47\1\55\2\47"+
        "\1\54\1\4\1\uffff\2\47\1\4\1\54";
    static final String DFA2_acceptS =
        "\1\uffff\1\1\1\uffff\1\4\3\uffff\1\3\6\uffff\1\2\4\uffff";
    static final String DFA2_specialS =
        "\23\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\2\1\uffff\2\3\3\uffff\11\1\21\uffff\1\3\1\uffff\1\1",
            "",
            "\1\4",
            "",
            "\1\5",
            "\11\7\12\uffff\6\3\1\4\1\10\1\uffff\1\7\4\3\1\6",
            "\1\11",
            "",
            "\1\13\42\uffff\1\12",
            "\1\14",
            "\11\16\20\uffff\1\15\2\uffff\1\16",
            "\1\17\40\uffff\1\21\1\20",
            "\11\7\12\uffff\6\3\3\uffff\1\7\4\3\1\6",
            "\1\22",
            "",
            "\1\17\40\uffff\1\21\1\20",
            "\11\16\20\uffff\1\15\2\uffff\1\16",
            "\1\13",
            "\11\7\12\uffff\6\3\3\uffff\1\7\4\3\1\6"
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "24:1: atom returns [Atom s] : ( | m= method | v= var | e1= expr ( '==' e2= expr | '!=' e3= expr | '>=' e4= expr | '<=' e5= expr | '>' e6= expr | '<' e7= expr ) );";
        }
    }
    static final String DFA4_eotS =
        "\5\uffff";
    static final String DFA4_eofS =
        "\5\uffff";
    static final String DFA4_minS =
        "\1\4\2\5\2\uffff";
    static final String DFA4_maxS =
        "\1\4\2\47\2\uffff";
    static final String DFA4_acceptS =
        "\3\uffff\1\2\1\1";
    static final String DFA4_specialS =
        "\5\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\1",
            "\1\2\40\uffff\1\4\1\3",
            "\1\2\40\uffff\1\4\1\3",
            "",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "()* loopback of 50:2: (i3= type ',' )*";
        }
    }
    static final String DFA11_eotS =
        "\6\uffff";
    static final String DFA11_eofS =
        "\3\uffff\1\4\2\uffff";
    static final String DFA11_minS =
        "\1\4\1\44\1\4\1\13\2\uffff";
    static final String DFA11_maxS =
        "\1\4\1\44\1\4\1\54\2\uffff";
    static final String DFA11_acceptS =
        "\4\uffff\1\1\1\2";
    static final String DFA11_specialS =
        "\6\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\1",
            "\1\2",
            "\1\3",
            "\11\4\12\uffff\6\4\1\2\1\5\1\uffff\6\4",
            "",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "90:2: ( (var1= ID ( '.' var2= ID )+ ) | (m1= method '.' var3= ID ) )";
        }
    }
 

    public static final BitSet FOLLOW_method_in_atom46 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_atom63 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_atom83 = new BitSet(new long[]{0x0000000FC0000000L});
    public static final BitSet FOLLOW_30_in_atom90 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_expr_in_atom94 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_atom107 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_expr_in_atom111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_atom123 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_expr_in_atom127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_atom139 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_expr_in_atom143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_atom155 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_expr_in_atom159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_atom172 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_expr_in_atom176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_method205 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_method221 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_method225 = new BitSet(new long[]{0x0000003000000000L});
    public static final BitSet FOLLOW_37_in_method247 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_method265 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_method267 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_method284 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_method286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_method301 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_method303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_type335 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_ALWAYS_in_type352 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_mult_in_expr396 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_40_in_expr411 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_mult_in_expr415 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_41_in_expr428 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_mult_in_expr432 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_factor_in_mult463 = new BitSet(new long[]{0x00000C0000000002L});
    public static final BitSet FOLLOW_42_in_mult481 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_factor_in_mult485 = new BitSet(new long[]{0x00000C0000000002L});
    public static final BitSet FOLLOW_43_in_mult498 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_factor_in_mult502 = new BitSet(new long[]{0x00000C0000000002L});
    public static final BitSet FOLLOW_37_in_factor530 = new BitSet(new long[]{0x00000020000000D0L});
    public static final BitSet FOLLOW_expr_in_factor534 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_factor536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_factor565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_factor585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_factor603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_var640 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_var656 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_var660 = new BitSet(new long[]{0x0000101000000002L});
    public static final BitSet FOLLOW_method_in_var686 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_var699 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_var703 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_44_in_var722 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_var726 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_var728 = new BitSet(new long[]{0x0000100000000002L});

}