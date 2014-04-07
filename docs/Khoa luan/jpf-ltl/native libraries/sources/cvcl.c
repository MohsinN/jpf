#include <jni.h>
#include "mba/linkedlist.h"
#include "mba/hashmap.h"
#include <c_interface.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "gov_nasa_jpf_symbolic_dp_NativeInterface.h"

#define TRUE 1
#define FALSE 0

/* map from names representing SymbolicIntegers to Expr */
struct hashmap vars;  
/* stores all exprs that we allocate. so that we can deallocate after we are done. */
struct linkedlist exprPool;  

char* constraint;
int marker;
Type intType, realType;
VC vc;
Flags flags;

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
  iter_t iter; char* varname;
  hashmap_iterate(&vars, &iter);
  while ((varname = (char*) hashmap_next(&vars, &iter)))
    free(varname);
  hashmap_clear(&vars, NULL, NULL, NULL);
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

jboolean check(VC vc, Expr e)
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

JNIEXPORT void JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_initializeCVCL
  (JNIEnv *env, jclass cls)
{
  // initialize stuff
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  linkedlist_init(&exprPool, 0, NULL);
  
  //flags = vc_createFlags();
  //vc_setStringFlag(flags, "dump-log", "test1.cvc");
  //vc = vc_createValidityChecker(flags);
  vc = vc_createValidityChecker(NULL);
  intType = vc_intType(vc);
  realType = vc_realType(vc);
}

JNIEXPORT jboolean JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_isSatisfiable
  (JNIEnv *env, jclass cls, jstring constraintString )
{
  constraint = (char*) (*env)->GetStringUTFChars(env,constraintString, NULL);
  if( constraint == NULL ){
    throwRuntimeException( "out of memory?" );
  }

  //printf( "query: %s\n", constraint);
  //fflush(stdout);
  if (constraint[0] == '\0')
    return TRUE;

  vc_push(vc);
  marker = 0;
  int constraintCount = 1;
  char c;
  int i = 0;
  do{
    c = constraint[i++];
    if (c == ',')
      constraintCount++;
  }while(c != '\0');

  Expr* constraintArray = (Expr*) malloc(sizeof(Expr)*constraintCount);
  i = 0;
  char token[2]; // it must be just a comma and '\0'
  do{
    constraintArray[i++] = parse();
  }while(readToken(token));

  Expr andExpr = vc_andExprN(vc, constraintArray, constraintCount);
  linkedlist_add(&exprPool, andExpr);
  
  jboolean result = check(vc, andExpr);

  //fflush(stdout);

  //clean up
  (*env)->ReleaseStringUTFChars(env,constraintString, constraint);
  free(constraintArray);
  freeStuff();
  vc_pop(vc);

  return result;
}

int main(int argc, char** argv)
{
  if(argc < 2)
    throwRuntimeException( "must specify the file name that contains the query" );

  printf( "argc: %d\n", argc);

  printf("%s\n",argv[1]);


  // initialize stuff
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  linkedlist_init(&exprPool, 0, NULL);
  
  //flags = vc_createFlags();
  //vc_setStringFlag(flags, "dump-log", "test1.cvc");
  //vc = vc_createValidityChecker(flags);
  vc = vc_createValidityChecker(NULL);
  intType = vc_intType(vc);
  realType = vc_realType(vc);

  constraint = (char*) malloc(sizeof(char)*10000);

  int k = 0;
  FILE* fpt = fopen(argv[1],"r");

  while(1){
    char c = fgetc(fpt);
    if (c == EOF) break;
    constraint[k++]=c;
  }
  constraint[k]='\0';


  if (constraint[0] == '\0')
    return TRUE;

  vc_push(vc);
  marker = 0;
  int constraintCount = 1;
  char c;
  int i = 0;
  do{
    c = constraint[i++];
    if (c == ',')
      constraintCount++;
  }while(c != '\0');
  
  Expr* constraintArray = (Expr*) malloc(sizeof(Expr)*constraintCount);
  i = 0;
  char token[2]; // it must be just a comma and '\0'
  do{
    constraintArray[i++] = parse();
  }while(readToken(token));

  Expr andExpr = vc_andExprN(vc, constraintArray, constraintCount);
  linkedlist_add(&exprPool, andExpr);

  jboolean result = check(vc, andExpr);

  //clean up
  free(constraintArray);
  freeStuff();
  vc_pop(vc);

  if(result)
    printf("satisfiable");
  else
    printf("unsatisfiable");

  return EXIT_SUCCESS;
}
