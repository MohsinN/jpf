package gov.nasa.jpf.ltl.Antlr;

// $ANTLR 3.4 C:\\Users\\Michele\\Desktop\\DetectParam.g 2012-03-20 11:21:59

import java.util.Vector;
import java.io.IOException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class DetectParamParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "BYTE", "CHAR", "DOUBLE", "FLOAT", "ID", "INT", "LONG", "SHORT", "STRING", "WS", "'('", "')'", "','"
    };

    public static final int EOF=-1;
    public static final int T__14=14;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int BYTE=4;
    public static final int CHAR=5;
    public static final int DOUBLE=6;
    public static final int FLOAT=7;
    public static final int ID=8;
    public static final int INT=9;
    public static final int LONG=10;
    public static final int SHORT=11;
    public static final int STRING=12;
    public static final int WS=13;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public DetectParamParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public DetectParamParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return DetectParamParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\Users\\Michele\\Desktop\\DetectParam.g"; }



       Vector<String> atoms = new Vector<String>();
              public Vector<String> getAtoms() {
        		return atoms;
      	}


    public static class type_return extends ParserRuleReturnScope {
    };


    // $ANTLR start "type"
    // C:\\Users\\Michele\\Desktop\\DetectParam.g:31:2: type : ( INT | LONG | SHORT | BYTE | CHAR | FLOAT | DOUBLE | STRING | ID );
    public final DetectParamParser.type_return type() throws RecognitionException {
        DetectParamParser.type_return retval = new DetectParamParser.type_return();
        retval.start = input.LT(1);


        try {
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:31:7: ( INT | LONG | SHORT | BYTE | CHAR | FLOAT | DOUBLE | STRING | ID )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:
            {
            if ( (input.LA(1) >= BYTE && input.LA(1) <= STRING) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "type"



    // $ANTLR start "detectParameters"
    // C:\\Users\\Michele\\Desktop\\DetectParam.g:33:1: detectParameters : ID '(' ( exp )* ')' ;
    public final void detectParameters() throws RecognitionException {
        try {
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:34:2: ( ID '(' ( exp )* ')' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:34:4: ID '(' ( exp )* ')'
            {
            match(input,ID,FOLLOW_ID_in_detectParameters173); 

            match(input,14,FOLLOW_14_in_detectParameters175); 

            // C:\\Users\\Michele\\Desktop\\DetectParam.g:34:10: ( exp )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= BYTE && LA1_0 <= STRING)||LA1_0==16) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Users\\Michele\\Desktop\\DetectParam.g:34:10: exp
            	    {
            	    pushFollow(FOLLOW_exp_in_detectParameters176);
            	    exp();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            match(input,15,FOLLOW_15_in_detectParameters178); 

            //System.out.println("trovato exp");

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "detectParameters"



    // $ANTLR start "exp"
    // C:\\Users\\Michele\\Desktop\\DetectParam.g:37:1: exp : ( ',' )? s= type ( ',' )? ;
    public final void exp() throws RecognitionException {
        DetectParamParser.type_return s =null;


        try {
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:37:5: ( ( ',' )? s= type ( ',' )? )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:37:7: ( ',' )? s= type ( ',' )?
            {
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:37:7: ( ',' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==16) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // C:\\Users\\Michele\\Desktop\\DetectParam.g:37:7: ','
                    {
                    match(input,16,FOLLOW_16_in_exp190); 

                    }
                    break;

            }


            pushFollow(FOLLOW_type_in_exp195);
            s=type();

            state._fsp--;


            // C:\\Users\\Michele\\Desktop\\DetectParam.g:37:19: ( ',' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==16) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\Users\\Michele\\Desktop\\DetectParam.g:37:19: ','
                    {
                    match(input,16,FOLLOW_16_in_exp197); 

                    }
                    break;

            }


            atoms.add((s!=null?input.toString(s.start,s.stop):null));System.out.println("trovato token "+(s!=null?input.toString(s.start,s.stop):null));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "exp"

    // Delegated rules


 

    public static final BitSet FOLLOW_ID_in_detectParameters173 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_detectParameters175 = new BitSet(new long[]{0x0000000000019FF0L});
    public static final BitSet FOLLOW_exp_in_detectParameters176 = new BitSet(new long[]{0x0000000000019FF0L});
    public static final BitSet FOLLOW_15_in_detectParameters178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_exp190 = new BitSet(new long[]{0x0000000000001FF0L});
    public static final BitSet FOLLOW_type_in_exp195 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_16_in_exp197 = new BitSet(new long[]{0x0000000000000002L});

}