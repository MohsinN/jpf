// $ANTLR 3.2 Sep 23, 2009 12:02:23 LTLSpec.g 2010-08-09 16:41:13

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
public class LTLSpecParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "ALWAYS", "INT", "FLOAT", "NOT", "NEXT", "EVENTUALLY", "IMPLIES", "OR", "ROBBYJO_OR", "AND", "ROBBYJO_AND", "UNTIL", "WEAK_UNTIL", "RELEASE", "WEAK_RELEASE", "TRUE", "FALSE", "EXPONENT", "COMMENT", "WS", "ESC_SEQ", "STRING", "HEX_DIGIT", "UNICODE_ESC", "OCTAL_ESC", "'=='", "'!='", "'>='", "'<='", "'>'", "'<'", "'.'", "'('", "','", "')'", "'+'", "'-'", "'*'", "'/'", "'['", "']'"
    };
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
    public LTLSpec_SymbolicAtom gSymbolicAtom;
    // delegators


        public LTLSpecParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public LTLSpecParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            gSymbolicAtom = new LTLSpec_SymbolicAtom(input, state, this);         
        }
        

    public String[] getTokenNames() { return LTLSpecParser.tokenNames; }
    public String getGrammarFileName() { return "LTLSpec.g"; }


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



    // $ANTLR start "ltlSpec"
    // LTLSpec.g:58:1: ltlSpec returns [Formula<String> f] : b= binaryFormula ;
    public final Formula<String> ltlSpec() throws RecognitionException {
        Formula<String> f = null;

        Formula<String> b = null;


        try {
            // LTLSpec.g:62:2: (b= binaryFormula )
            // LTLSpec.g:62:4: b= binaryFormula
            {
            pushFollow(FOLLOW_binaryFormula_in_ltlSpec59);
            b=binaryFormula();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
              f = b; 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return f;
    }
    // $ANTLR end "ltlSpec"


    // $ANTLR start "unaryOperator"
    // LTLSpec.g:65:1: unaryOperator returns [String operator] : ( NOT | NEXT | ALWAYS | EVENTUALLY );
    public final String unaryOperator() throws RecognitionException {
        String operator = null;

        try {
            // LTLSpec.g:66:2: ( NOT | NEXT | ALWAYS | EVENTUALLY )
            int alt1=4;
            switch ( input.LA(1) ) {
            case NOT:
                {
                alt1=1;
                }
                break;
            case NEXT:
                {
                alt1=2;
                }
                break;
            case ALWAYS:
                {
                alt1=3;
                }
                break;
            case EVENTUALLY:
                {
                alt1=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return operator;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // LTLSpec.g:66:4: NOT
                    {
                    match(input,NOT,FOLLOW_NOT_in_unaryOperator81); if (state.failed) return operator;
                    if ( state.backtracking==0 ) {
                      operator = "!"; 
                    }

                    }
                    break;
                case 2 :
                    // LTLSpec.g:67:4: NEXT
                    {
                    match(input,NEXT,FOLLOW_NEXT_in_unaryOperator92); if (state.failed) return operator;
                    if ( state.backtracking==0 ) {
                      operator = "X";	
                    }

                    }
                    break;
                case 3 :
                    // LTLSpec.g:68:4: ALWAYS
                    {
                    match(input,ALWAYS,FOLLOW_ALWAYS_in_unaryOperator102); if (state.failed) return operator;
                    if ( state.backtracking==0 ) {
                      operator = "[]"; 
                    }

                    }
                    break;
                case 4 :
                    // LTLSpec.g:69:4: EVENTUALLY
                    {
                    match(input,EVENTUALLY,FOLLOW_EVENTUALLY_in_unaryOperator111); if (state.failed) return operator;
                    if ( state.backtracking==0 ) {
                      operator = "<>"; 
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
        return operator;
    }
    // $ANTLR end "unaryOperator"


    // $ANTLR start "binaryFormula"
    // LTLSpec.g:72:1: binaryFormula returns [Formula<String> f] : l1= logicalFormula (i= IMPLIES l2= binaryFormula )? ;
    public final Formula<String> binaryFormula() throws RecognitionException {
        Formula<String> f = null;

        Token i=null;
        Formula<String> l1 = null;

        Formula<String> l2 = null;


        try {
            // LTLSpec.g:73:2: (l1= logicalFormula (i= IMPLIES l2= binaryFormula )? )
            // LTLSpec.g:73:4: l1= logicalFormula (i= IMPLIES l2= binaryFormula )?
            {
            pushFollow(FOLLOW_logicalFormula_in_binaryFormula130);
            l1=logicalFormula();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
              f = l1; 
            }
            // LTLSpec.g:74:2: (i= IMPLIES l2= binaryFormula )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==IMPLIES) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // LTLSpec.g:74:3: i= IMPLIES l2= binaryFormula
                    {
                    i=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_binaryFormula139); if (state.failed) return f;
                    pushFollow(FOLLOW_binaryFormula_in_binaryFormula144);
                    l2=binaryFormula();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = Formula.Implies(f, l2); 
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
        return f;
    }
    // $ANTLR end "binaryFormula"


    // $ANTLR start "logicalFormula"
    // LTLSpec.g:79:1: logicalFormula returns [Formula<String> f] : t1= andFormula ( ( OR | ROBBYJO_OR ) t2= logicalFormula )? ;
    public final Formula<String> logicalFormula() throws RecognitionException {
        Formula<String> f = null;

        Formula<String> t1 = null;

        Formula<String> t2 = null;


        try {
            // LTLSpec.g:80:2: (t1= andFormula ( ( OR | ROBBYJO_OR ) t2= logicalFormula )? )
            // LTLSpec.g:80:4: t1= andFormula ( ( OR | ROBBYJO_OR ) t2= logicalFormula )?
            {
            pushFollow(FOLLOW_andFormula_in_logicalFormula169);
            t1=andFormula();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
              f = t1; 
            }
            // LTLSpec.g:81:2: ( ( OR | ROBBYJO_OR ) t2= logicalFormula )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=OR && LA3_0<=ROBBYJO_OR)) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // LTLSpec.g:81:3: ( OR | ROBBYJO_OR ) t2= logicalFormula
                    {
                    if ( (input.LA(1)>=OR && input.LA(1)<=ROBBYJO_OR) ) {
                        input.consume();
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return f;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_logicalFormula_in_logicalFormula197);
                    t2=logicalFormula();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = binFormula(f, "||", t2); 
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
        return f;
    }
    // $ANTLR end "logicalFormula"


    // $ANTLR start "andFormula"
    // LTLSpec.g:85:1: andFormula returns [Formula<String> f] : t1= temporalFormula ( ( AND | ROBBYJO_AND ) t2= andFormula )? ;
    public final Formula<String> andFormula() throws RecognitionException {
        Formula<String> f = null;

        Formula<String> t1 = null;

        Formula<String> t2 = null;


        try {
            // LTLSpec.g:86:2: (t1= temporalFormula ( ( AND | ROBBYJO_AND ) t2= andFormula )? )
            // LTLSpec.g:86:4: t1= temporalFormula ( ( AND | ROBBYJO_AND ) t2= andFormula )?
            {
            pushFollow(FOLLOW_temporalFormula_in_andFormula222);
            t1=temporalFormula();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
              f = t1; 
            }
            // LTLSpec.g:87:2: ( ( AND | ROBBYJO_AND ) t2= andFormula )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>=AND && LA4_0<=ROBBYJO_AND)) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // LTLSpec.g:87:3: ( AND | ROBBYJO_AND ) t2= andFormula
                    {
                    if ( (input.LA(1)>=AND && input.LA(1)<=ROBBYJO_AND) ) {
                        input.consume();
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return f;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_andFormula_in_andFormula249);
                    t2=andFormula();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = binFormula(f, "&&", t2); 
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
        return f;
    }
    // $ANTLR end "andFormula"


    // $ANTLR start "temporalFormula"
    // LTLSpec.g:91:1: temporalFormula returns [Formula<String> f] : p1= releaseFormula ( UNTIL p2= temporalFormula | WEAK_UNTIL p3= temporalFormula )? ;
    public final Formula<String> temporalFormula() throws RecognitionException {
        Formula<String> f = null;

        Formula<String> p1 = null;

        Formula<String> p2 = null;

        Formula<String> p3 = null;


        try {
            // LTLSpec.g:92:2: (p1= releaseFormula ( UNTIL p2= temporalFormula | WEAK_UNTIL p3= temporalFormula )? )
            // LTLSpec.g:92:4: p1= releaseFormula ( UNTIL p2= temporalFormula | WEAK_UNTIL p3= temporalFormula )?
            {
            pushFollow(FOLLOW_releaseFormula_in_temporalFormula274);
            p1=releaseFormula();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
              f = p1; 
            }
            // LTLSpec.g:93:2: ( UNTIL p2= temporalFormula | WEAK_UNTIL p3= temporalFormula )?
            int alt5=3;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==UNTIL) ) {
                alt5=1;
            }
            else if ( (LA5_0==WEAK_UNTIL) ) {
                alt5=2;
            }
            switch (alt5) {
                case 1 :
                    // LTLSpec.g:93:4: UNTIL p2= temporalFormula
                    {
                    match(input,UNTIL,FOLLOW_UNTIL_in_temporalFormula295); if (state.failed) return f;
                    pushFollow(FOLLOW_temporalFormula_in_temporalFormula299);
                    p2=temporalFormula();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = binFormula(f, "U", p2); 
                    }

                    }
                    break;
                case 2 :
                    // LTLSpec.g:94:4: WEAK_UNTIL p3= temporalFormula
                    {
                    match(input,WEAK_UNTIL,FOLLOW_WEAK_UNTIL_in_temporalFormula316); if (state.failed) return f;
                    pushFollow(FOLLOW_temporalFormula_in_temporalFormula320);
                    p3=temporalFormula();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = binFormula(f, "W", p3); 
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
        return f;
    }
    // $ANTLR end "temporalFormula"


    // $ANTLR start "releaseFormula"
    // LTLSpec.g:98:1: releaseFormula returns [Formula<String> f] : p1= proposition ( RELEASE p2= releaseFormula | WEAK_RELEASE p3= releaseFormula )? ;
    public final Formula<String> releaseFormula() throws RecognitionException {
        Formula<String> f = null;

        Formula<String> p1 = null;

        Formula<String> p2 = null;

        Formula<String> p3 = null;


        try {
            // LTLSpec.g:99:2: (p1= proposition ( RELEASE p2= releaseFormula | WEAK_RELEASE p3= releaseFormula )? )
            // LTLSpec.g:99:4: p1= proposition ( RELEASE p2= releaseFormula | WEAK_RELEASE p3= releaseFormula )?
            {
            pushFollow(FOLLOW_proposition_in_releaseFormula351);
            p1=proposition();

            state._fsp--;
            if (state.failed) return f;
            if ( state.backtracking==0 ) {
              f = p1; 
            }
            // LTLSpec.g:100:2: ( RELEASE p2= releaseFormula | WEAK_RELEASE p3= releaseFormula )?
            int alt6=3;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RELEASE) ) {
                alt6=1;
            }
            else if ( (LA6_0==WEAK_RELEASE) ) {
                alt6=2;
            }
            switch (alt6) {
                case 1 :
                    // LTLSpec.g:100:4: RELEASE p2= releaseFormula
                    {
                    match(input,RELEASE,FOLLOW_RELEASE_in_releaseFormula363); if (state.failed) return f;
                    pushFollow(FOLLOW_releaseFormula_in_releaseFormula367);
                    p2=releaseFormula();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = binFormula(f, "V", p2); 
                    }

                    }
                    break;
                case 2 :
                    // LTLSpec.g:101:4: WEAK_RELEASE p3= releaseFormula
                    {
                    match(input,WEAK_RELEASE,FOLLOW_WEAK_RELEASE_in_releaseFormula377); if (state.failed) return f;
                    pushFollow(FOLLOW_releaseFormula_in_releaseFormula381);
                    p3=releaseFormula();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = binFormula(f, "M", p3); 
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
        return f;
    }
    // $ANTLR end "releaseFormula"


    // $ANTLR start "proposition"
    // LTLSpec.g:105:1: proposition returns [Formula<String> f] : ( TRUE | FALSE | s= atom | u1= unaryOperator l3= proposition | '(' l4= ltlSpec ')' );
    public final Formula<String> proposition() throws RecognitionException {
        Formula<String> f = null;

        Atom s = null;

        String u1 = null;

        Formula<String> l3 = null;

        Formula<String> l4 = null;


        try {
            // LTLSpec.g:106:2: ( TRUE | FALSE | s= atom | u1= unaryOperator l3= proposition | '(' l4= ltlSpec ')' )
            int alt7=5;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // LTLSpec.g:106:4: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_proposition404); if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = Formula.True(); 
                    }

                    }
                    break;
                case 2 :
                    // LTLSpec.g:107:4: FALSE
                    {
                    match(input,FALSE,FOLLOW_FALSE_in_proposition426); if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = Formula.False(); 
                    }

                    }
                    break;
                case 3 :
                    // LTLSpec.g:108:4: s= atom
                    {
                    pushFollow(FOLLOW_atom_in_proposition450);
                    s=atom();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = Formula.Proposition(s.getText()); atoms.add(s.getText()); 
                    }

                    }
                    break;
                case 4 :
                    // LTLSpec.g:109:4: u1= unaryOperator l3= proposition
                    {
                    pushFollow(FOLLOW_unaryOperator_in_proposition473);
                    u1=unaryOperator();

                    state._fsp--;
                    if (state.failed) return f;
                    pushFollow(FOLLOW_proposition_in_proposition477);
                    l3=proposition();

                    state._fsp--;
                    if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = unaryFormula(u1, l3); 
                    }

                    }
                    break;
                case 5 :
                    // LTLSpec.g:110:4: '(' l4= ltlSpec ')'
                    {
                    match(input,37,FOLLOW_37_in_proposition488); if (state.failed) return f;
                    pushFollow(FOLLOW_ltlSpec_in_proposition492);
                    l4=ltlSpec();

                    state._fsp--;
                    if (state.failed) return f;
                    match(input,39,FOLLOW_39_in_proposition494); if (state.failed) return f;
                    if ( state.backtracking==0 ) {
                      f = l4; 
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
        return f;
    }
    // $ANTLR end "proposition"

    // $ANTLR start synpred15_LTLSpec
    public final void synpred15_LTLSpec_fragment() throws RecognitionException {   
        Atom s = null;


        // LTLSpec.g:108:4: (s= atom )
        // LTLSpec.g:108:4: s= atom
        {
        pushFollow(FOLLOW_atom_in_synpred15_LTLSpec450);
        s=atom();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_LTLSpec

    // Delegated rules
    public String var() throws RecognitionException { return gSymbolicAtom.var(); }
    public Operand expr() throws RecognitionException { return gSymbolicAtom.expr(); }
    public Operand mult() throws RecognitionException { return gSymbolicAtom.mult(); }
    public String type() throws RecognitionException { return gSymbolicAtom.type(); }
    public Atom atom() throws RecognitionException { return gSymbolicAtom.atom(); }
    public Operand factor() throws RecognitionException { return gSymbolicAtom.factor(); }
    public String method() throws RecognitionException { return gSymbolicAtom.method(); }

    public final boolean synpred15_LTLSpec() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_LTLSpec_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\25\uffff";
    static final String DFA7_eofS =
        "\1\3\24\uffff";
    static final String DFA7_minS =
        "\1\4\14\uffff\1\0\7\uffff";
    static final String DFA7_maxS =
        "\1\47\14\uffff\1\0\7\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\14\uffff\1\4\3\uffff\1\5";
    static final String DFA7_specialS =
        "\15\uffff\1\0\7\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\3\1\20\2\3\3\20\11\3\1\1\1\2\17\uffff\1\15\1\uffff\1\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "105:1: proposition returns [Formula<String> f] : ( TRUE | FALSE | s= atom | u1= unaryOperator l3= proposition | '(' l4= ltlSpec ')' );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA7_13 = input.LA(1);

                         
                        int index7_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_LTLSpec()) ) {s = 3;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index7_13);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 7, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_binaryFormula_in_ltlSpec59 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_unaryOperator81 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEXT_in_unaryOperator92 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALWAYS_in_unaryOperator102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVENTUALLY_in_unaryOperator111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalFormula_in_binaryFormula130 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_IMPLIES_in_binaryFormula139 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_binaryFormula_in_binaryFormula144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andFormula_in_logicalFormula169 = new BitSet(new long[]{0x0000000000003002L});
    public static final BitSet FOLLOW_set_in_logicalFormula187 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_logicalFormula_in_logicalFormula197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_temporalFormula_in_andFormula222 = new BitSet(new long[]{0x000000000000C002L});
    public static final BitSet FOLLOW_set_in_andFormula239 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_andFormula_in_andFormula249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_releaseFormula_in_temporalFormula274 = new BitSet(new long[]{0x0000000000030002L});
    public static final BitSet FOLLOW_UNTIL_in_temporalFormula295 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_temporalFormula_in_temporalFormula299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WEAK_UNTIL_in_temporalFormula316 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_temporalFormula_in_temporalFormula320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_proposition_in_releaseFormula351 = new BitSet(new long[]{0x00000000000C0002L});
    public static final BitSet FOLLOW_RELEASE_in_releaseFormula363 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_releaseFormula_in_releaseFormula367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WEAK_RELEASE_in_releaseFormula377 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_releaseFormula_in_releaseFormula381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_proposition404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_proposition426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_proposition450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryOperator_in_proposition473 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_proposition_in_proposition477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_proposition488 = new BitSet(new long[]{0x00000020003007F0L});
    public static final BitSet FOLLOW_ltlSpec_in_proposition492 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_proposition494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_synpred15_LTLSpec450 = new BitSet(new long[]{0x0000000000000002L});

}