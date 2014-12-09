#include <jni.h>
#include <mba/hashmap.h>
#include <mba/stack.h>
#include <yices_c.h>
#include <yicesl_c.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "gov_nasa_jpf_symbolic_dp_NativeInterface.h"

#define TRUE 1
#define FALSE 0

/* map from names representing SymbolicIntegers to Expr */
struct hashmap vars;  

/* stores the PC ids */
struct stack pcids;

/* stores the ids of the assertions added corresponding to each pc id */
struct stack assids;

char* constraint;
int marker;
yices_type intType;
yices_type realType;
yices_context ctx;

void throwRuntimeException(char* msg)
{
  printf("%s\n", msg);
  exit(1);
}

char* readToken(char* token)
{
  char ch;
  int k = 0;

  if (constraint[marker] == '\0'){
    // end of constraint string
    return NULL;
  }

  do{
    ch = constraint[marker++];
    token[k++] = ch;
  }while ((ch != ' ') && (ch != '\0'));
  
  if (ch == '\0')
    marker--;
  else
    token[k-1] = '\0';

  return token;
}

yices_expr parse()
{
  char token[15];
  yices_expr exprs[2];
  yices_var_decl vd;

  readToken(token);

  switch(token[0]){
  case '+':
    exprs[0] = parse();
    exprs[1] = parse();
    return yices_mk_sum(ctx, exprs, 2);
  case '-':
    if(token[1] == '\0'){
      exprs[0] = parse();
      exprs[1] = parse();
      return yices_mk_sub(ctx, exprs, 2);
    }
    else{
      // assert token[1] is a digit
      return yices_mk_num_from_string(ctx, token);
    }
  case '*':
    exprs[0] = parse();
    exprs[1] = parse();
    return yices_mk_mul(ctx, exprs, 2);
  case '<':
    exprs[0] = parse();
    exprs[1] = parse();
    if (token[1] == '='){
      return yices_mk_le(ctx, exprs[0], exprs[1]);
    }
    else{
      return yices_mk_lt(ctx, exprs[0], exprs[1]);
    }
  case '>':
    exprs[0] = parse();
    exprs[1] = parse();
    if (token[1] == '='){
      return yices_mk_ge(ctx, exprs[0], exprs[1]);
    }
    else{
      return yices_mk_gt(ctx, exprs[0], exprs[1]);
    }
  case '=':
    exprs[0] = parse();
    exprs[1] = parse();
    return yices_mk_eq(ctx, exprs[0], exprs[1]);
  case '!':
    // assert token[1] == '=';
    exprs[0] = parse();
    exprs[1] = parse();
    return yices_mk_diseq(ctx, exprs[0], exprs[1]);
  case '.':
  case '0':
  case '1':
  case '2':
  case '3':
  case '4':
  case '5':
  case '6':
  case '7':
  case '8':
  case '9':
    return yices_mk_num_from_string(ctx, token);
  case 'x':
    /*
    expr = hashmap_get(&vars, token);
    if (expr == NULL){
      expr = yices_mk_var_from_decl(ctx, yices_mk_var_decl(ctx, token, intType));
      char* token_copy = (char*) malloc(sizeof(char)*(strlen(token)+1));
      strcpy(token_copy, token);
      hashmap_put(&vars, token_copy, expr);
    }
    return expr;
    */
    vd = yices_get_var_decl_from_name(ctx,token);
    if (vd == 0)
      vd = yices_mk_var_decl(ctx, token, intType);
    return yices_mk_var_from_decl(ctx,vd);
  case 'r':
    vd = yices_get_var_decl_from_name(ctx,token);
    if (vd == 0)
      vd = yices_mk_var_decl(ctx, token, realType);
    return yices_mk_var_from_decl(ctx,vd);
  default:
    printf( "%s", token);
    throwRuntimeException( "unexpected type of token" );
  }
  return NULL;
}

void freeStuff()
{
  iter_t iter; char* varname;
  hashmap_iterate(&vars, &iter);
  while ((varname = (char*) hashmap_next(&vars, &iter)))
    free(varname);
  hashmap_clear(&vars, NULL, NULL, NULL);
}

jboolean check()
{
  lbool r = yices_check(ctx);
  switch(r){
  case l_true:
    return TRUE;
  case l_false:
    return FALSE;
  case l_undef:
    throwRuntimeException("Unknown result from Yices!");
  }
  return TRUE;
}

int currentID = 1;

//int count = 0;

/** returns 0 if the constraint is unsatisfiable
    else returns the id of the new path condition
    obtained by appending cstr to the pc represented by id
*/
int isSatisfiable(char* cstr, int id)
{
  // case 1:
  // if id is equal to current ID
  // then query the constraint in the current context

  // case 2:
  // if id is less than the current ID then "pop to the depth".

  //printf("%s\n", cstr);
  //printf("%d %d\n", id, currentID); 
  //fflush(stdout);

  if(id > currentID)
    throwRuntimeException("id > currentID. ...Not expected! Are you not using DFS Search?");

  if(id < currentID){
    //printf( "Popping to %d\n", id);
    int pcid;
    do{
      pcid = (int) stack_pop(&pcids);
      yices_pop(ctx);
      //yices_retract(ctx, (int) stack_pop(&assids));
    }while (pcid != id);
    currentID = id;
  }

  constraint = cstr; //printf("%s", constraint);
  marker = 0;

  yices_push(ctx);

  yices_expr query = parse();
  //yices_pp_expr(query); printf("\n");
  //yices_dump_context(ctx);
  //flush(stdout);

  yices_assert(ctx, query);
  if (check()){
    stack_push(&pcids, (void*) currentID);
    currentID++;
    return currentID;
  }
  else{
    yices_pop(ctx);
    return 0;
  }
    
  /*
  assertion_id aid = yices_assert_retractable(ctx, parse());

  jboolean result = check();

  if (result){
    stack_push(&assids, (void*) aid);
    stack_push(&pcids, (void*) currentID);
    currentID++;
    return currentID;
  }
  else{
    yices_retract(ctx, aid);
    return 0;
  }
  */
}

void initialize()
{
  // initialize stuff
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  stack_init(&pcids, 0, NULL);
  stack_init(&assids, 0, NULL);
  yices_set_arith_only(1);
  //yices_enable_log_file("queries");
  
  ctx = yices_mk_context();
  intType = yices_mk_type(ctx, "int");
  realType = yices_mk_type(ctx, "real");
}

JNIEXPORT void JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_initializeYices
  (JNIEnv *env, jclass cls)
{
  initialize();
}

JNIEXPORT jint JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_assertFormula
  (JNIEnv *env, jclass cls, jstring constraintString, jint id )
{
  char* cstr = (char*) (*env)->GetStringUTFChars(env, constraintString, NULL);
  if( cstr == NULL ){
    throwRuntimeException( "Out of Memory" );
    return -1;  // out of memory
  }
  jint r = isSatisfiable(cstr, id); 
  (*env)->ReleaseStringUTFChars(env, constraintString, cstr);
  
  return r;
}

int main()
{
    initialize();

    printf("%d", isSatisfiable("> r1 1.4", 1));
    printf("%d", isSatisfiable("<= r1 .2", 2));
    

    /*
  printf("Initial scope: %d\n", currentID);

  
  printf( "%d", isSatisfiable("> x1 1", 1));
  printf("scope: %d\n",currentID);

  printf( "%d", isSatisfiable("< x1 0", 2));
  printf("scope: %d\n",currentID);

  printf( "%d", isSatisfiable(">= x1 0", 2));
  printf("scope: %d\n",currentID);

  printf( "%d", isSatisfiable("<= x1 2", 3));
  printf("scope: %d\n",currentID);
    
  printf( "%d", isSatisfiable("> x1 2", 3));
  printf("scope: %d\n",currentID);

  printf( "%d", isSatisfiable("<= x1 2", 2));
  printf("scope: %d\n",currentID);
    */
  
  /*
  isSatisfiable("> x1 1", 1);
  isSatisfiable("<= x2 1", 1);
  */

    return EXIT_SUCCESS;
}
