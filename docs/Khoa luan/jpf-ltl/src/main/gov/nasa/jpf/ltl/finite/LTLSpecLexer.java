// $ANTLR 3.2 Sep 23, 2009 12:02:23 LTLSpec.g 2010-08-09 16:41:13
package gov.nasa.jpf.ltl.finite;
import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class LTLSpecLexer extends Lexer {
    public static final int EVENTUALLY=10;
    public static final int EXPONENT=22;
    public static final int OCTAL_ESC=29;
    public static final int FLOAT=7;
    public static final int ROBBYJO_AND=15;
    public static final int NOT=8;
    public static final int AND=14;
    public static final int ID=4;
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

    public LTLSpecLexer() {;} 
    public LTLSpecLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public LTLSpecLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "LTLSpec.g"; }

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:7:7: ( '==' )
            // LTLSpec.g:7:9: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:8:7: ( '!=' )
            // LTLSpec.g:8:9: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:9:7: ( '>=' )
            // LTLSpec.g:9:9: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:10:7: ( '<=' )
            // LTLSpec.g:10:9: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:11:7: ( '>' )
            // LTLSpec.g:11:9: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:12:7: ( '<' )
            // LTLSpec.g:12:9: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:13:7: ( '.' )
            // LTLSpec.g:13:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:14:7: ( '(' )
            // LTLSpec.g:14:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:15:7: ( ',' )
            // LTLSpec.g:15:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:16:7: ( ')' )
            // LTLSpec.g:16:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:17:7: ( '+' )
            // LTLSpec.g:17:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:18:7: ( '-' )
            // LTLSpec.g:18:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:19:7: ( '*' )
            // LTLSpec.g:19:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:20:7: ( '/' )
            // LTLSpec.g:20:9: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:21:7: ( '[' )
            // LTLSpec.g:21:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:22:7: ( ']' )
            // LTLSpec.g:22:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:115:10: ( '/\\\\' )
            // LTLSpec.g:115:12: '/\\\\'
            {
            match("/\\"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "ROBBYJO_AND"
    public final void mROBBYJO_AND() throws RecognitionException {
        try {
            int _type = ROBBYJO_AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:116:14: ( '&&' )
            // LTLSpec.g:116:16: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ROBBYJO_AND"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:117:9: ( '\\\\/' )
            // LTLSpec.g:117:11: '\\\\/'
            {
            match("\\/"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "ROBBYJO_OR"
    public final void mROBBYJO_OR() throws RecognitionException {
        try {
            int _type = ROBBYJO_OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:118:13: ( '||' )
            // LTLSpec.g:118:15: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ROBBYJO_OR"

    // $ANTLR start "UNTIL"
    public final void mUNTIL() throws RecognitionException {
        try {
            int _type = UNTIL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:119:11: ( 'U' )
            // LTLSpec.g:119:13: 'U'
            {
            match('U'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNTIL"

    // $ANTLR start "WEAK_UNTIL"
    public final void mWEAK_UNTIL() throws RecognitionException {
        try {
            int _type = WEAK_UNTIL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:120:13: ( 'W' )
            // LTLSpec.g:120:15: 'W'
            {
            match('W'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WEAK_UNTIL"

    // $ANTLR start "RELEASE"
    public final void mRELEASE() throws RecognitionException {
        try {
            int _type = RELEASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:121:12: ( 'V' )
            // LTLSpec.g:121:14: 'V'
            {
            match('V'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RELEASE"

    // $ANTLR start "WEAK_RELEASE"
    public final void mWEAK_RELEASE() throws RecognitionException {
        try {
            int _type = WEAK_RELEASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:122:14: ( 'M' )
            // LTLSpec.g:122:16: 'M'
            {
            match('M'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WEAK_RELEASE"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:123:10: ( '!' )
            // LTLSpec.g:123:12: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "NEXT"
    public final void mNEXT() throws RecognitionException {
        try {
            int _type = NEXT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:124:10: ( 'X' )
            // LTLSpec.g:124:12: 'X'
            {
            match('X'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEXT"

    // $ANTLR start "ALWAYS"
    public final void mALWAYS() throws RecognitionException {
        try {
            int _type = ALWAYS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:125:11: ( '[]' )
            // LTLSpec.g:125:13: '[]'
            {
            match("[]"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ALWAYS"

    // $ANTLR start "EVENTUALLY"
    public final void mEVENTUALLY() throws RecognitionException {
        try {
            int _type = EVENTUALLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:126:13: ( '<>' )
            // LTLSpec.g:126:15: '<>'
            {
            match("<>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EVENTUALLY"

    // $ANTLR start "IMPLIES"
    public final void mIMPLIES() throws RecognitionException {
        try {
            int _type = IMPLIES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:127:12: ( '->' )
            // LTLSpec.g:127:14: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IMPLIES"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:129:10: ( 'true' )
            // LTLSpec.g:129:12: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:130:11: ( 'false' )
            // LTLSpec.g:130:12: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:132:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // LTLSpec.g:132:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // LTLSpec.g:132:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // LTLSpec.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:135:5: ( ( '0' .. '9' )+ )
            // LTLSpec.g:135:7: ( '0' .. '9' )+
            {
            // LTLSpec.g:135:7: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // LTLSpec.g:135:7: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:139:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
            int alt9=3;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    // LTLSpec.g:139:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // LTLSpec.g:139:9: ( '0' .. '9' )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // LTLSpec.g:139:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);

                    match('.'); 
                    // LTLSpec.g:139:25: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // LTLSpec.g:139:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    // LTLSpec.g:139:37: ( EXPONENT )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='E'||LA5_0=='e') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // LTLSpec.g:139:37: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // LTLSpec.g:140:9: '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    match('.'); 
                    // LTLSpec.g:140:13: ( '0' .. '9' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // LTLSpec.g:140:14: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    // LTLSpec.g:140:25: ( EXPONENT )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='E'||LA7_0=='e') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // LTLSpec.g:140:25: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // LTLSpec.g:141:9: ( '0' .. '9' )+ EXPONENT
                    {
                    // LTLSpec.g:141:9: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // LTLSpec.g:141:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:145:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='/') ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1=='/') ) {
                    alt13=1;
                }
                else if ( (LA13_1=='*') ) {
                    alt13=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // LTLSpec.g:145:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 

                    // LTLSpec.g:145:14: (~ ( '\\n' | '\\r' ) )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>='\u0000' && LA10_0<='\t')||(LA10_0>='\u000B' && LA10_0<='\f')||(LA10_0>='\u000E' && LA10_0<='\uFFFF')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // LTLSpec.g:145:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    // LTLSpec.g:145:28: ( '\\r' )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0=='\r') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // LTLSpec.g:145:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    match('\n'); 
                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // LTLSpec.g:146:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // LTLSpec.g:146:14: ( options {greedy=false; } : . )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0=='*') ) {
                            int LA12_1 = input.LA(2);

                            if ( (LA12_1=='/') ) {
                                alt12=2;
                            }
                            else if ( ((LA12_1>='\u0000' && LA12_1<='.')||(LA12_1>='0' && LA12_1<='\uFFFF')) ) {
                                alt12=1;
                            }


                        }
                        else if ( ((LA12_0>='\u0000' && LA12_0<=')')||(LA12_0>='+' && LA12_0<='\uFFFF')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // LTLSpec.g:146:42: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);

                    match("*/"); 

                    _channel=HIDDEN;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:149:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // LTLSpec.g:149:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // LTLSpec.g:157:5: ( '\"' ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )* '\"' )
            // LTLSpec.g:157:8: '\"' ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // LTLSpec.g:157:12: ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )*
            loop14:
            do {
                int alt14=3;
                int LA14_0 = input.LA(1);

                if ( (LA14_0=='\\') ) {
                    alt14=1;
                }
                else if ( ((LA14_0>='\u0000' && LA14_0<='!')||(LA14_0>='#' && LA14_0<='[')||(LA14_0>=']' && LA14_0<='\uFFFF')) ) {
                    alt14=2;
                }


                switch (alt14) {
            	case 1 :
            	    // LTLSpec.g:157:14: ESC_SEQ
            	    {
            	    mESC_SEQ(); 

            	    }
            	    break;
            	case 2 :
            	    // LTLSpec.g:157:24: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // LTLSpec.g:161:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // LTLSpec.g:161:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // LTLSpec.g:161:22: ( '+' | '-' )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0=='+'||LA15_0=='-') ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // LTLSpec.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // LTLSpec.g:161:33: ( '0' .. '9' )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>='0' && LA16_0<='9')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // LTLSpec.g:161:34: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // LTLSpec.g:164:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // LTLSpec.g:164:13: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // LTLSpec.g:168:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt17=3;
            int LA17_0 = input.LA(1);

            if ( (LA17_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt17=1;
                    }
                    break;
                case 'u':
                    {
                    alt17=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt17=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // LTLSpec.g:168:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // LTLSpec.g:169:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 

                    }
                    break;
                case 3 :
                    // LTLSpec.g:170:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // LTLSpec.g:175:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt18=3;
            int LA18_0 = input.LA(1);

            if ( (LA18_0=='\\') ) {
                int LA18_1 = input.LA(2);

                if ( ((LA18_1>='0' && LA18_1<='3')) ) {
                    int LA18_2 = input.LA(3);

                    if ( ((LA18_2>='0' && LA18_2<='7')) ) {
                        int LA18_4 = input.LA(4);

                        if ( ((LA18_4>='0' && LA18_4<='7')) ) {
                            alt18=1;
                        }
                        else {
                            alt18=2;}
                    }
                    else {
                        alt18=3;}
                }
                else if ( ((LA18_1>='4' && LA18_1<='7')) ) {
                    int LA18_3 = input.LA(3);

                    if ( ((LA18_3>='0' && LA18_3<='7')) ) {
                        alt18=2;
                    }
                    else {
                        alt18=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // LTLSpec.g:175:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // LTLSpec.g:175:14: ( '0' .. '3' )
                    // LTLSpec.g:175:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // LTLSpec.g:175:25: ( '0' .. '7' )
                    // LTLSpec.g:175:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // LTLSpec.g:175:36: ( '0' .. '7' )
                    // LTLSpec.g:175:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // LTLSpec.g:176:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // LTLSpec.g:176:14: ( '0' .. '7' )
                    // LTLSpec.g:176:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // LTLSpec.g:176:25: ( '0' .. '7' )
                    // LTLSpec.g:176:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // LTLSpec.g:177:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // LTLSpec.g:177:14: ( '0' .. '7' )
                    // LTLSpec.g:177:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OCTAL_ESC"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // LTLSpec.g:182:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // LTLSpec.g:182:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 
            match('u'); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "UNICODE_ESC"

    public void mTokens() throws RecognitionException {
        // LTLSpec.g:1:8: ( T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | AND | ROBBYJO_AND | OR | ROBBYJO_OR | UNTIL | WEAK_UNTIL | RELEASE | WEAK_RELEASE | NOT | NEXT | ALWAYS | EVENTUALLY | IMPLIES | TRUE | FALSE | ID | INT | FLOAT | COMMENT | WS | STRING )
        int alt19=37;
        alt19 = dfa19.predict(input);
        switch (alt19) {
            case 1 :
                // LTLSpec.g:1:10: T__30
                {
                mT__30(); 

                }
                break;
            case 2 :
                // LTLSpec.g:1:16: T__31
                {
                mT__31(); 

                }
                break;
            case 3 :
                // LTLSpec.g:1:22: T__32
                {
                mT__32(); 

                }
                break;
            case 4 :
                // LTLSpec.g:1:28: T__33
                {
                mT__33(); 

                }
                break;
            case 5 :
                // LTLSpec.g:1:34: T__34
                {
                mT__34(); 

                }
                break;
            case 6 :
                // LTLSpec.g:1:40: T__35
                {
                mT__35(); 

                }
                break;
            case 7 :
                // LTLSpec.g:1:46: T__36
                {
                mT__36(); 

                }
                break;
            case 8 :
                // LTLSpec.g:1:52: T__37
                {
                mT__37(); 

                }
                break;
            case 9 :
                // LTLSpec.g:1:58: T__38
                {
                mT__38(); 

                }
                break;
            case 10 :
                // LTLSpec.g:1:64: T__39
                {
                mT__39(); 

                }
                break;
            case 11 :
                // LTLSpec.g:1:70: T__40
                {
                mT__40(); 

                }
                break;
            case 12 :
                // LTLSpec.g:1:76: T__41
                {
                mT__41(); 

                }
                break;
            case 13 :
                // LTLSpec.g:1:82: T__42
                {
                mT__42(); 

                }
                break;
            case 14 :
                // LTLSpec.g:1:88: T__43
                {
                mT__43(); 

                }
                break;
            case 15 :
                // LTLSpec.g:1:94: T__44
                {
                mT__44(); 

                }
                break;
            case 16 :
                // LTLSpec.g:1:100: T__45
                {
                mT__45(); 

                }
                break;
            case 17 :
                // LTLSpec.g:1:106: AND
                {
                mAND(); 

                }
                break;
            case 18 :
                // LTLSpec.g:1:110: ROBBYJO_AND
                {
                mROBBYJO_AND(); 

                }
                break;
            case 19 :
                // LTLSpec.g:1:122: OR
                {
                mOR(); 

                }
                break;
            case 20 :
                // LTLSpec.g:1:125: ROBBYJO_OR
                {
                mROBBYJO_OR(); 

                }
                break;
            case 21 :
                // LTLSpec.g:1:136: UNTIL
                {
                mUNTIL(); 

                }
                break;
            case 22 :
                // LTLSpec.g:1:142: WEAK_UNTIL
                {
                mWEAK_UNTIL(); 

                }
                break;
            case 23 :
                // LTLSpec.g:1:153: RELEASE
                {
                mRELEASE(); 

                }
                break;
            case 24 :
                // LTLSpec.g:1:161: WEAK_RELEASE
                {
                mWEAK_RELEASE(); 

                }
                break;
            case 25 :
                // LTLSpec.g:1:174: NOT
                {
                mNOT(); 

                }
                break;
            case 26 :
                // LTLSpec.g:1:178: NEXT
                {
                mNEXT(); 

                }
                break;
            case 27 :
                // LTLSpec.g:1:183: ALWAYS
                {
                mALWAYS(); 

                }
                break;
            case 28 :
                // LTLSpec.g:1:190: EVENTUALLY
                {
                mEVENTUALLY(); 

                }
                break;
            case 29 :
                // LTLSpec.g:1:201: IMPLIES
                {
                mIMPLIES(); 

                }
                break;
            case 30 :
                // LTLSpec.g:1:209: TRUE
                {
                mTRUE(); 

                }
                break;
            case 31 :
                // LTLSpec.g:1:214: FALSE
                {
                mFALSE(); 

                }
                break;
            case 32 :
                // LTLSpec.g:1:220: ID
                {
                mID(); 

                }
                break;
            case 33 :
                // LTLSpec.g:1:223: INT
                {
                mINT(); 

                }
                break;
            case 34 :
                // LTLSpec.g:1:227: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 35 :
                // LTLSpec.g:1:233: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 36 :
                // LTLSpec.g:1:241: WS
                {
                mWS(); 

                }
                break;
            case 37 :
                // LTLSpec.g:1:244: STRING
                {
                mSTRING(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    protected DFA19 dfa19 = new DFA19(this);
    static final String DFA9_eotS =
        "\5\uffff";
    static final String DFA9_eofS =
        "\5\uffff";
    static final String DFA9_minS =
        "\2\56\3\uffff";
    static final String DFA9_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA9_acceptS =
        "\2\uffff\1\2\1\3\1\1";
    static final String DFA9_specialS =
        "\5\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\4\1\uffff\12\1\13\uffff\1\3\37\uffff\1\3",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "138:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
        }
    }
    static final String DFA19_eotS =
        "\2\uffff\1\36\1\40\1\43\1\45\4\uffff\1\47\1\uffff\1\52\1\54\4\uffff"+
        "\1\55\1\56\1\57\1\60\1\61\2\31\1\uffff\1\64\27\uffff\2\31\1\uffff"+
        "\2\31\1\71\1\31\1\uffff\1\73\1\uffff";
    static final String DFA19_eofS =
        "\74\uffff";
    static final String DFA19_minS =
        "\1\11\1\uffff\3\75\1\60\4\uffff\1\76\1\uffff\1\52\1\135\4\uffff"+
        "\5\60\1\162\1\141\1\uffff\1\56\27\uffff\1\165\1\154\1\uffff\1\145"+
        "\1\163\1\60\1\145\1\uffff\1\60\1\uffff";
    static final String DFA19_maxS =
        "\1\174\1\uffff\2\75\1\76\1\71\4\uffff\1\76\1\uffff\1\134\1\135"+
        "\4\uffff\5\172\1\162\1\141\1\uffff\1\145\27\uffff\1\165\1\154\1"+
        "\uffff\1\145\1\163\1\172\1\145\1\uffff\1\172\1\uffff";
    static final String DFA19_acceptS =
        "\1\uffff\1\1\4\uffff\1\10\1\11\1\12\1\13\1\uffff\1\15\2\uffff\1"+
        "\20\1\22\1\23\1\24\7\uffff\1\40\1\uffff\1\44\1\45\1\2\1\31\1\3\1"+
        "\5\1\4\1\34\1\6\1\42\1\7\1\35\1\14\1\21\1\43\1\16\1\33\1\17\1\25"+
        "\1\26\1\27\1\30\1\32\2\uffff\1\41\4\uffff\1\36\1\uffff\1\37";
    static final String DFA19_specialS =
        "\74\uffff}>";
    static final String[] DFA19_transitionS = {
            "\2\33\2\uffff\1\33\22\uffff\1\33\1\2\1\34\3\uffff\1\17\1\uffff"+
            "\1\6\1\10\1\13\1\11\1\7\1\12\1\5\1\14\12\32\2\uffff\1\4\1\1"+
            "\1\3\2\uffff\14\31\1\25\7\31\1\22\1\24\1\23\1\26\2\31\1\15\1"+
            "\20\1\16\1\uffff\1\31\1\uffff\5\31\1\30\15\31\1\27\6\31\1\uffff"+
            "\1\21",
            "",
            "\1\35",
            "\1\37",
            "\1\41\1\42",
            "\12\44",
            "",
            "",
            "",
            "",
            "\1\46",
            "",
            "\1\51\4\uffff\1\51\54\uffff\1\50",
            "\1\53",
            "",
            "",
            "",
            "",
            "\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
            "\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
            "\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
            "\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
            "\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
            "\1\62",
            "\1\63",
            "",
            "\1\44\1\uffff\12\32\13\uffff\1\44\37\uffff\1\44",
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
            "\1\65",
            "\1\66",
            "",
            "\1\67",
            "\1\70",
            "\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
            "\1\72",
            "",
            "\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | AND | ROBBYJO_AND | OR | ROBBYJO_OR | UNTIL | WEAK_UNTIL | RELEASE | WEAK_RELEASE | NOT | NEXT | ALWAYS | EVENTUALLY | IMPLIES | TRUE | FALSE | ID | INT | FLOAT | COMMENT | WS | STRING );";
        }
    }
 

}