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

struct Constraint{
  char* constraint_str;
  struct Constraint* next;
};

struct hashmap id2Constraint;
int pcCount = 1;

/* map from names representing SymbolicIntegers to Expr */
struct hashmap vars;  

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

void toString(char* str, int id)
{
  int count = 0;

  if (id != 1){
    struct Constraint *c = (struct Constraint*) hashmap_get(&id2Constraint, (void*)id);
    while (c != NULL){
      printf("** %s", c->constraint_str);
      strcpy(str+count, c->constraint_str);
      count += strlen(c->constraint_str);
      c = c->next;
      if (c != NULL){ strcpy(str+count, " , "); count += 3; }
    }
  }
  str[count] = '\0'; 
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
  yices_expr exprs[2]; yices_var_decl vd;

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


/** returns 0 if the constraint is unsatisfiable
    else returns the id of the new path condition
    obtained by appending cstr to the pc represented by id
*/

int isSatisfiable(char* cstr, int id)
{
  yices_expr constraintArray[2500];  // bad style

  ctx = yices_mk_context();
  intType = yices_mk_type(ctx, "int");
  realType = yices_mk_type(ctx, "real");

  //printf("%d %s\n", ++count, cstr);
  //printf("% d\n", id);
  //fflush(stdout);
  constraint = cstr; printf("%s", constraint); fflush(stdout);
  marker = 0;
  
  constraintArray[0] = parse();
  int count = 0;

  struct Constraint* parent_constraint = NULL;
  if (id != 1){
    struct Constraint *c = (struct Constraint*) hashmap_get(&id2Constraint, (void*)id);
    
    //if (c == NULL){ 
    //  printf("%d", id);
    //  throwRuntimeException("NULL");
    // }
    
    // if c == NULL parent must have been infeasible!! no way to get here!
    parent_constraint = c;
    
    while (c != NULL){
      constraint = c->constraint_str; //printf(" , %s", constraint);
      marker = 0;
      constraintArray[++count] = parse();
      c = c->next;
    }
  }
  //printf("\n");
  
  yices_expr andExpr = yices_mk_and(ctx, constraintArray, count+1);
  //linkedlist_add(&exprPool, andExpr);

  yices_assert(ctx,andExpr);

  jboolean result = check();
  
  //free(constraintArray);
  freeStuff();
  yices_del_context(ctx);
  
  if (result){
    char* s = (char*) malloc(sizeof(char)*(strlen(cstr)+1));
    strcpy(s, cstr);
    struct Constraint* con = (struct Constraint*) malloc(sizeof(struct Constraint));
    con->constraint_str = s;
    con->next = parent_constraint;
    
    //struct Constraint* oldcon;
    //if(hashmap_remove(&id2Constraint, (void**)&id, (void**)&oldcon) == 0){ 
    //  free(oldcon->constraint_str);
    //  free(oldcon);
    //}
    pcCount++;
    hashmap_put(&id2Constraint, (void*) pcCount, con);
    return pcCount;
  }
  else
    return 0;
}


void initialize()
{
  // initialize stuff
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  hashmap_init(&id2Constraint, 0, NULL, NULL, NULL, NULL);
  yices_set_arith_only(1);
  yices_enable_log_file("queries");
}

JNIEXPORT jstring JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_getStringRep
  (JNIEnv *env, jclass cls, jint id)
{
  char* str = (char*) malloc(sizeof(char)*2000);
  toString(str,id);
  return (*env)->NewStringUTF(env, str);
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
  printf( "%d", isSatisfiable("> x1 1",2));
  printf( "%d", isSatisfiable("< x1 0",4));
  printf( "%d", isSatisfiable(">= x1 0",5));
  printf( "%d", isSatisfiable("<= x2 1",3));
  */

  return EXIT_SUCCESS;
}
