package gov.nasa.jpf.ltl.Antlr;

// $ANTLR 3.4 C:\\Users\\Michele\\Desktop\\DetectParam.g 2012-03-20 11:21:59

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class DetectParamLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public DetectParamLexer() {} 
    public DetectParamLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public DetectParamLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "C:\\Users\\Michele\\Desktop\\DetectParam.g"; }

    // $ANTLR start "BYTE"
    public final void mBYTE() throws RecognitionException {
        try {
            int _type = BYTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:2:6: ( 'byte' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:2:8: 'byte'
            {
            match("byte"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BYTE"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            int _type = CHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:3:6: ( 'char' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:3:8: 'char'
            {
            match("char"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CHAR"

    // $ANTLR start "DOUBLE"
    public final void mDOUBLE() throws RecognitionException {
        try {
            int _type = DOUBLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:4:8: ( 'double' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:4:10: 'double'
            {
            match("double"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOUBLE"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:5:7: ( 'float' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:5:9: 'float'
            {
            match("float"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:6:5: ( 'int' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:6:7: 'int'
            {
            match("int"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "LONG"
    public final void mLONG() throws RecognitionException {
        try {
            int _type = LONG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:7:6: ( 'long' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:7:8: 'long'
            {
            match("long"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LONG"

    // $ANTLR start "SHORT"
    public final void mSHORT() throws RecognitionException {
        try {
            int _type = SHORT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:8:7: ( 'short' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:8:9: 'short'
            {
            match("short"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SHORT"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:9:8: ( 'String' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:9:10: 'String'
            {
            match("String"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:10:7: ( '(' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:10:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:11:7: ( ')' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:11:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:12:7: ( ',' )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:12:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:42:4: ( ( 'a' .. 'z' | 'A' .. 'Z' )* )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:42:6: ( 'a' .. 'z' | 'A' .. 'Z' )*
            {
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:42:6: ( 'a' .. 'z' | 'A' .. 'Z' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= 'A' && LA1_0 <= 'Z')||(LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Users\\Michele\\Desktop\\DetectParam.g:
            	    {
            	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


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
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:44:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // C:\\Users\\Michele\\Desktop\\DetectParam.g:44:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:8: ( BYTE | CHAR | DOUBLE | FLOAT | INT | LONG | SHORT | STRING | T__14 | T__15 | T__16 | ID | WS )
        int alt2=13;
        switch ( input.LA(1) ) {
        case 'b':
            {
            int LA2_1 = input.LA(2);

            if ( (LA2_1=='y') ) {
                int LA2_14 = input.LA(3);

                if ( (LA2_14=='t') ) {
                    int LA2_22 = input.LA(4);

                    if ( (LA2_22=='e') ) {
                        int LA2_30 = input.LA(5);

                        if ( ((LA2_30 >= 'A' && LA2_30 <= 'Z')||(LA2_30 >= 'a' && LA2_30 <= 'z')) ) {
                            alt2=12;
                        }
                        else {
                            alt2=1;
                        }
                    }
                    else {
                        alt2=12;
                    }
                }
                else {
                    alt2=12;
                }
            }
            else {
                alt2=12;
            }
            }
            break;
        case 'c':
            {
            int LA2_2 = input.LA(2);

            if ( (LA2_2=='h') ) {
                int LA2_15 = input.LA(3);

                if ( (LA2_15=='a') ) {
                    int LA2_23 = input.LA(4);

                    if ( (LA2_23=='r') ) {
                        int LA2_31 = input.LA(5);

                        if ( ((LA2_31 >= 'A' && LA2_31 <= 'Z')||(LA2_31 >= 'a' && LA2_31 <= 'z')) ) {
                            alt2=12;
                        }
                        else {
                            alt2=2;
                        }
                    }
                    else {
                        alt2=12;
                    }
                }
                else {
                    alt2=12;
                }
            }
            else {
                alt2=12;
            }
            }
            break;
        case 'd':
            {
            int LA2_3 = input.LA(2);

            if ( (LA2_3=='o') ) {
                int LA2_16 = input.LA(3);

                if ( (LA2_16=='u') ) {
                    int LA2_24 = input.LA(4);

                    if ( (LA2_24=='b') ) {
                        int LA2_32 = input.LA(5);

                        if ( (LA2_32=='l') ) {
                            int LA2_40 = input.LA(6);

                            if ( (LA2_40=='e') ) {
                                int LA2_45 = input.LA(7);

                                if ( ((LA2_45 >= 'A' && LA2_45 <= 'Z')||(LA2_45 >= 'a' && LA2_45 <= 'z')) ) {
                                    alt2=12;
                                }
                                else {
                                    alt2=3;
                                }
                            }
                            else {
                                alt2=12;
                            }
                        }
                        else {
                            alt2=12;
                        }
                    }
                    else {
                        alt2=12;
                    }
                }
                else {
                    alt2=12;
                }
            }
            else {
                alt2=12;
            }
            }
            break;
        case 'f':
            {
            int LA2_4 = input.LA(2);

            if ( (LA2_4=='l') ) {
                int LA2_17 = input.LA(3);

                if ( (LA2_17=='o') ) {
                    int LA2_25 = input.LA(4);

                    if ( (LA2_25=='a') ) {
                        int LA2_33 = input.LA(5);

                        if ( (LA2_33=='t') ) {
                            int LA2_41 = input.LA(6);

                            if ( ((LA2_41 >= 'A' && LA2_41 <= 'Z')||(LA2_41 >= 'a' && LA2_41 <= 'z')) ) {
                                alt2=12;
                            }
                            else {
                                alt2=4;
                            }
                        }
                        else {
                            alt2=12;
                        }
                    }
                    else {
                        alt2=12;
                    }
                }
                else {
                    alt2=12;
                }
            }
            else {
                alt2=12;
            }
            }
            break;
        case 'i':
            {
            int LA2_5 = input.LA(2);

            if ( (LA2_5=='n') ) {
                int LA2_18 = input.LA(3);

                if ( (LA2_18=='t') ) {
                    int LA2_26 = input.LA(4);

                    if ( ((LA2_26 >= 'A' && LA2_26 <= 'Z')||(LA2_26 >= 'a' && LA2_26 <= 'z')) ) {
                        alt2=12;
                    }
                    else {
                        alt2=5;
                    }
                }
                else {
                    alt2=12;
                }
            }
            else {
                alt2=12;
            }
            }
            break;
        case 'l':
            {
            int LA2_6 = input.LA(2);

            if ( (LA2_6=='o') ) {
                int LA2_19 = input.LA(3);

                if ( (LA2_19=='n') ) {
                    int LA2_27 = input.LA(4);

                    if ( (LA2_27=='g') ) {
                        int LA2_35 = input.LA(5);

                        if ( ((LA2_35 >= 'A' && LA2_35 <= 'Z')||(LA2_35 >= 'a' && LA2_35 <= 'z')) ) {
                            alt2=12;
                        }
                        else {
                            alt2=6;
                        }
                    }
                    else {
                        alt2=12;
                    }
                }
                else {
                    alt2=12;
                }
            }
            else {
                alt2=12;
            }
            }
            break;
        case 's':
            {
            int LA2_7 = input.LA(2);

            if ( (LA2_7=='h') ) {
                int LA2_20 = input.LA(3);

                if ( (LA2_20=='o') ) {
                    int LA2_28 = input.LA(4);

                    if ( (LA2_28=='r') ) {
                        int LA2_36 = input.LA(5);

                        if ( (LA2_36=='t') ) {
                            int LA2_43 = input.LA(6);

                            if ( ((LA2_43 >= 'A' && LA2_43 <= 'Z')||(LA2_43 >= 'a' && LA2_43 <= 'z')) ) {
                                alt2=12;
                            }
                            else {
                                alt2=7;
                            }
                        }
                        else {
                            alt2=12;
                        }
                    }
                    else {
                        alt2=12;
                    }
                }
                else {
                    alt2=12;
                }
            }
            else {
                alt2=12;
            }
            }
            break;
        case 'S':
            {
            int LA2_8 = input.LA(2);

            if ( (LA2_8=='t') ) {
                int LA2_21 = input.LA(3);

                if ( (LA2_21=='r') ) {
                    int LA2_29 = input.LA(4);

                    if ( (LA2_29=='i') ) {
                        int LA2_37 = input.LA(5);

                        if ( (LA2_37=='n') ) {
                            int LA2_44 = input.LA(6);

                            if ( (LA2_44=='g') ) {
                                int LA2_48 = input.LA(7);

                                if ( ((LA2_48 >= 'A' && LA2_48 <= 'Z')||(LA2_48 >= 'a' && LA2_48 <= 'z')) ) {
                                    alt2=12;
                                }
                                else {
                                    alt2=8;
                                }
                            }
                            else {
                                alt2=12;
                            }
                        }
                        else {
                            alt2=12;
                        }
                    }
                    else {
                        alt2=12;
                    }
                }
                else {
                    alt2=12;
                }
            }
            else {
                alt2=12;
            }
            }
            break;
        case '(':
            {
            alt2=9;
            }
            break;
        case ')':
            {
            alt2=10;
            }
            break;
        case ',':
            {
            alt2=11;
            }
            break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
            {
            alt2=13;
            }
            break;
        default:
            alt2=12;
        }

        switch (alt2) {
            case 1 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:10: BYTE
                {
                mBYTE(); 


                }
                break;
            case 2 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:15: CHAR
                {
                mCHAR(); 


                }
                break;
            case 3 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:20: DOUBLE
                {
                mDOUBLE(); 


                }
                break;
            case 4 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:27: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 5 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:33: INT
                {
                mINT(); 


                }
                break;
            case 6 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:37: LONG
                {
                mLONG(); 


                }
                break;
            case 7 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:42: SHORT
                {
                mSHORT(); 


                }
                break;
            case 8 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:48: STRING
                {
                mSTRING(); 


                }
                break;
            case 9 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:55: T__14
                {
                mT__14(); 


                }
                break;
            case 10 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:61: T__15
                {
                mT__15(); 


                }
                break;
            case 11 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:67: T__16
                {
                mT__16(); 


                }
                break;
            case 12 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:73: ID
                {
                mID(); 


                }
                break;
            case 13 :
                // C:\\Users\\Michele\\Desktop\\DetectParam.g:1:76: WS
                {
                mWS(); 


                }
                break;

        }

    }


 

}