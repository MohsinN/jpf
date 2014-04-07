#include <jni.h>
#include <mba/linkedlist.h>
#include <mba/hashmap.h>
#include <c_interface.h>
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
/* stores all exprs that we allocate. so that we can deallocate after we are done. */
struct linkedlist exprPool;  

char* constraint;
int marker;
Type intType, realType;
VC vc;

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

Expr parseNumber(char* token)
{
  char ds[15];
  char* f = strchr(token, '.');
  if (f != NULL){
    //printf("%s\n",token);
    int p = strlen(f+1);
    strncpy(ds, token, strlen(token) - p - 1);
    
    int powOf10 = 1, i;
    for (i=0; i < p; i++)
      powOf10 *= 10;

    int num;
    if (token[0] == '-')
      num = -(powOf10 * atoi(ds+1) + atoi(f+1));
    else
      num = powOf10 * atoi(ds) + atoi(f+1);
    //printf("%d/%d\n",num,powOf10);
    return vc_ratExpr(vc, num, powOf10);
  }
  else{
    return vc_ratExpr(vc, atoi(token), 1);
  }
}

Expr parse()
{
  char token[15];
  Expr expr = NULL, leftExpr, rightExpr, eqExpr;

  readToken(token);

  switch(token[0]){
  case '+':
    leftExpr = parse();
    rightExpr = parse();
    expr = vc_plusExpr(vc, leftExpr, rightExpr);
    break;
  case '-':
    if(token[1] == '\0'){
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_minusExpr(vc, leftExpr, rightExpr);
    }
    else{
      // assert token[1] is a digit
      expr = parseNumber(token);
    }
    break;
  case '*':
    leftExpr = parse();
    rightExpr = parse();
    expr = vc_multExpr(vc, leftExpr, rightExpr);
    break;
  case '<':
    if (token[1] == '='){
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_leExpr(vc, leftExpr, rightExpr);
    }
    else{
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_ltExpr(vc, leftExpr, rightExpr);
    }
    break;
  case '>':
    if (token[1] == '='){
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_geExpr(vc, leftExpr, rightExpr);
    }
    else{
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_gtExpr(vc, leftExpr, rightExpr);
    }
    break;
  case '=':
    leftExpr = parse();
    rightExpr = parse();
    expr = vc_eqExpr(vc, leftExpr, rightExpr);
    break;
  case '!':
    // assert token[1] == '=';
    leftExpr = parse();
    rightExpr = parse();
    eqExpr = vc_eqExpr(vc, leftExpr, rightExpr);
    linkedlist_add(&exprPool, eqExpr);
    expr = vc_notExpr(vc, eqExpr);
    break;
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
    expr = parseNumber(token);
    break;
  case 'x':
    expr = hashmap_get(&vars, token);
    if (expr == NULL){
      expr = vc_varExpr(vc, token, intType);
      char* token_copy = (char*) malloc(sizeof(char)*(strlen(token)+1));
      strcpy(token_copy, token);
      hashmap_put(&vars, token_copy, expr);
      linkedlist_add(&exprPool, expr);
    }
    return expr;
  case 'r':
    expr = hashmap_get(&vars, token);
    if (expr == NULL){
      expr = vc_varExpr(vc, token, realType);
      char* token_copy = (char*) malloc(sizeof(char)*(strlen(token)+1));
      strcpy(token_copy, token);
      hashmap_put(&vars, token_copy, expr);
      linkedlist_add(&exprPool, expr);
    }
    return expr;
  default:
    printf( "%s", token);
    throwRuntimeException( "unexpected type of token" );
  }

  linkedlist_add(&exprPool, expr);
  return expr;
}


void freeStuff()
{
  iter_t iter;
  Expr el;
  linkedlist_iterate(&exprPool, &iter);
  while ((el = linkedlist_next(&exprPool, &iter))){
    vc_deleteExpr(el);
  }
  linkedlist_clear(&exprPool, NULL, NULL);
}

void check_error(char* msg) {
  if(get_error_status() < 0) {
    printf("%s\n", msg);
    printf("%s\n", get_error_string());
    exit(1);
  }
}

unsigned int check(VC vc, Expr e)
{
  //printf("Query: \n");
  Expr notExpr = vc_notExpr(vc, e);
  linkedlist_add(&exprPool, notExpr);
  //vc_printExpr(vc,notExpr);
  //fflush(stdout);
  check_error("Error occured during query");
  switch (vc_query(vc, notExpr)) {
    case 0:
      //printf("Invalid\n\n");
      break;
    case 1:
      //printf("Valid\n\n");
      return FALSE;
  }
  return TRUE;
}

int count = 0;

/** returns 0 if the constraint is unsatisfiable
    else returns the id of the new path condition
    obtained by appending cstr to the pc represented by id
*/

int isSatisfiable(char* cstr, int id)
{
  Expr constraintArray[2500];  // bad style

  //printf("%d %s\n", ++count, cstr);
  //printf("% d\n", id);
  //fflush(stdout);
  constraint = cstr; //printf("%s", constraint);
  marker = 0;
  
  constraintArray[0] = parse();
  int count = 0;

  struct Constraint* parent_constraint = NULL;
  if (id != 1){
    struct Constraint *c = (struct Constraint*) hashmap_get(&id2Constraint, (void*)id);
    
    if (c == NULL){ 
      printf("id = %d ", id);
      throwRuntimeException("NULL");
     }
    
    // if c == NULL parent must have been infeasible!! no way to get here!
    parent_constraint = c;
    
    while (c != NULL){
      constraint = c->constraint_str; //printf(" , %s", constraint);
      marker = 0;
      constraintArray[++count] = parse();
      c = c->next;
    }
  }
  //printf("\n");  fflush(stdout);
  
  Expr andExpr = vc_andExprN(vc, constraintArray, count+1);
  linkedlist_add(&exprPool, andExpr);

  vc_push(vc);

  jboolean result = check(vc, andExpr);
  
  //free(constraintArray);
  freeStuff();
  vc_pop(vc);
  
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
    //printf("Putting %s for %d", s, pcCount); fflush(stdout);
    hashmap_put(&id2Constraint, (void*) pcCount, con);
    return pcCount;
  }
  else
    return 0;
}

void initialize()
{
  hashmap_init(&id2Constraint, 0, NULL, NULL, NULL, NULL);
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  linkedlist_init(&exprPool, 0, NULL);

  vc = vc_createValidityChecker(NULL);
  intType = vc_intType(vc);
  realType = vc_realType(vc);
}

JNIEXPORT void JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_initializeCVCL
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

int main(int argc, char** argv)
{
  initialize();
  printf( "%d", isSatisfiable("> x1 1",2));
  printf( "%d", isSatisfiable("< x1 0",4));
  printf( "%d", isSatisfiable(">= x1 0",5));
  printf( "%d", isSatisfiable("<= x2 1",3));
  return EXIT_SUCCESS;
}
