#include <jni.h>
#include <mba/linkedlist.h>
#include <mba/hashmap.h>
#include <mba/stack.h>
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
int pcCount = 0;

/* map from names representing SymbolicIntegers to Expr */
struct hashmap vars;  
/* stores all exprs that we allocate. so that we can deallocate after we are done. */
struct linkedlist exprPool;  

/* stores the PC ids */
struct stack pcids;

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

void check_error(char* msg) 
{
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

  //fflush(stdout);
  int r = vc_query(vc, notExpr);
  check_error("Error occured during query");
  //vc_printExpr(vc,notExpr);
  switch (r) {
    case 0:
      //printf("Invalid\n\n");
      break;
    case 1:
      //printf("Valid\n\n");
      return FALSE;
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

  //printf("%d %s\n", ++count, cstr);
  //printf("%d %d\n", id, currentID); fflush(stdout);
  if(id > currentID)
    throwRuntimeException("id > currentID. ...Not expected!");

  if(id < currentID){
    //printf( "Popping to %d\n", id);
    int pcid;
    do{
      pcid = (int) stack_pop(&pcids);
      vc_pop(vc);
    }while (pcid != id);
    currentID = id;
  }

  //fflush(stdout);

  vc_push(vc);  

  constraint = cstr; //printf("%s", constraint);
  marker = 0;

  Expr query = parse();

  jboolean result = check(vc, query);

  if(result){
    // satisfiable. Also means negation is invalid.
    // meaning current context changed.
    // So Restore the current context.
    vc_pop(vc);
    vc_push(vc);
    vc_assertFormula(vc,query);
    stack_push(&pcids, (void*) currentID);
    currentID++;
    return currentID;
  }
  else{
    // unsatifiable. Also means negation is valid.
    // context must be unchanged.
    vc_pop(vc);
    return 0;
  }
}

void initialize()
{
  //hashmap_init(&id2Constraint, 0, NULL, NULL, NULL, NULL);
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  linkedlist_init(&exprPool, 0, NULL);
  stack_init(&pcids, 0, NULL);

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
  /*
  isSatisfiable("> x1 1", 1);
  isSatisfiable("<= x1 1", 1);
  */
  return EXIT_SUCCESS;
}
