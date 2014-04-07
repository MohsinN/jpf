#include <jni.h>
#include "mba/linkedlist.h"
#include "mba/hashmap.h"
#include <c_interface.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <sys/resource.h>

#include "gov_nasa_jpf_symbolic_dp_NativeInterface.h"

#define TRUE 1
#define FALSE 0

/* map from names representing SymbolicIntegers to Expr */
struct hashmap vars;  
/* stores all exprs that we allocate. so that we can deallocate after we are done. */
struct linkedlist exprPool;  

float toptimes[10];
char* constraint;
int marker;
Type intType;
VC vc;
char* buf[1024];


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

Expr parse()
{
  char token[15];
  Expr expr, leftExpr, rightExpr, eqExpr;
  int constant;

  readToken(token);
  //printf("token: %s\n", token);
  switch(token[0]){
  case '+':
    leftExpr = parse();
    rightExpr = parse();
    expr = vc_bv32PlusExpr(vc, leftExpr, rightExpr);
    break;
  case '-':
    if(token[1] == '\0'){
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_bv32MinusExpr(vc, leftExpr, rightExpr);
    }
    else{
      // assert token[1] is a digit
      constant = atoi(token);
      if (constant < 0)
	expr = vc_bvUMinusExpr(vc, vc_bv32ConstExprFromInt(vc, -constant));
      else
	expr = vc_bv32ConstExprFromInt(vc, constant);
    }
    break;
  case '*':
    leftExpr = parse();
    rightExpr = parse();
    expr = vc_bv32MultExpr(vc, leftExpr, rightExpr);
    break;
  case '/':
    leftExpr = parse();
    rightExpr = parse();
    expr = vc_bvDivExpr(vc, 32, leftExpr, rightExpr);
    break;
  case '<':
    if (token[1] == '='){
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_sbvLeExpr(vc, leftExpr, rightExpr);
    }
    else{
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_sbvLtExpr(vc, leftExpr, rightExpr);
    }
    break;
  case '>':
    if (token[1] == '='){
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_sbvGeExpr(vc, leftExpr, rightExpr);
    }
    else{
      leftExpr = parse();
      rightExpr = parse();
      expr = vc_sbvGtExpr(vc, leftExpr, rightExpr);
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
    expr = vc_bv32ConstExprFromInt(vc, atoi(token));
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
  //while ((varname = (char*) hashmap_next(&vars, &iter)))
  //  free(varname);
  //hashmap_clear(&vars, NULL, NULL, NULL);
  Expr el;
  linkedlist_iterate(&exprPool, &iter);
  while ((el = linkedlist_next(&exprPool, &iter))){
    //vc_deleteExpr(el);
  }
  linkedlist_clear(&exprPool, NULL, NULL);
}

/*
void check_error(char* msg) {
  if(get_error_status() < 0) {
    printf("%s\n", msg);
    printf("%s\n", get_error_string());
    exit(1);
  }
}
*/

int current_cpu_time(void) {
  struct rusage ru;
  getrusage(RUSAGE_SELF, &ru);
  return ( ru.ru_utime.tv_sec*1000 +
	   ru.ru_utime.tv_usec/1000+
	   ru.ru_stime.tv_sec*1000 +
	   ru.ru_stime.tv_usec/1000 );
}

float elapsed_cpu_time(int starttime) {
  int current = current_cpu_time();
  int diff = current - starttime;
  return diff/1000.0;
}

int isExpensive(float t)
{
  int i,j;
  for (i = 0; i<10; i++)
    if (toptimes[i] < t){
      for (j = 9; j>i; j--)
	toptimes[j] = toptimes[j-1];
      toptimes[i] = t;
      printf("10th time: %f ",toptimes[9]);
      return 1;
    }
  return 0;
}

jboolean check(VC vc, Expr e)
{
  //printf("Query: \n");
  Expr notExpr = vc_notExpr(vc, e);
  linkedlist_add(&exprPool, notExpr);
  //vc_printExpr(vc,notExpr);
  //vc_printQuery(vc);
  //fflush(stdout);
  //check_error("Error occured during query");
  
  //int starttime = current_cpu_time();
  int r = vc_query(vc, notExpr);
  //float elapsedtime = elapsed_cpu_time(starttime);

  //isExpensive(elapsedtime));
  /*
  if (elapsedtime > 0.059){
    unsigned long length;
    vc_printQueryStateToBuffer(vc, notExpr, buf, &length);
    printf( "%f\n", elapsedtime );
    for(unsigned long i=0; i<length;i++)
      printf("%s",buf[i]);
    fflush(stdout);
  }
  */

  switch (r) {
    case 0:
      //printf("Invalid\n\n");
      return TRUE;
    case 1:
      //printf("Valid\n\n");
      return FALSE;
  default:
    throwRuntimeException("Unknown output from STP");
  } 
}

JNIEXPORT void JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_initializeSTP
  (JNIEnv *env, jclass cls)
{
  // initialize stuff
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  linkedlist_init(&exprPool, 0, NULL);
  
  //flags = vc_createFlags();
  //vc_setStringFlag(flags, "dump-log", "test1.cvc");
  //vc = vc_createValidityChecker(flags);
  vc = vc_createValidityChecker();
  intType = vc_bv32Type(vc);

  /*
  //debug
  for(int i = 0; i < 10; i++)
    toptimes[i] = -1.0;

  for(int i = 0; i < 1024; i++)
    buf[i] = (char*) malloc(1024*sizeof(char));
  */
}

JNIEXPORT jboolean JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_isSatisfiable
  (JNIEnv *env, jclass cls, jstring constraintString )
{
  constraint = (char*) env->GetStringUTFChars(constraintString, NULL);
  if (constraint == NULL){
    throwRuntimeException( "out of memory?" );
  }

  //printf( "query: %s\n", constraint);

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

  jboolean result;
  if (constraintCount > 1){
    Expr andExpr = vc_andExprN(vc, constraintArray, constraintCount);
    linkedlist_add(&exprPool, andExpr);
    result = check(vc, andExpr);
  }
  else{
    result = check(vc,constraintArray[0]);
  }

  //clean up
  env->ReleaseStringUTFChars(constraintString, constraint);
  free(constraintArray);
  freeStuff();
  vc_pop(vc);



  return result;
}

int main(int argc, char** argv)
{
  if(argc < 2)
    throwRuntimeException( "must specify the file name that contains the query" );

  // initialize stuff
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  linkedlist_init(&exprPool, 0, NULL);
  
  // debug
  int i;
  for(i = 0; i < 1024; i++)
    buf[i] = (char*) malloc(1024*sizeof(char));
  for(i = 0; i < 10; i++)
    toptimes[i] = 1000.0;
  
  vc = vc_createValidityChecker();
  intType = vc_bv32Type(vc);

  constraint = (char*) malloc(sizeof(char)*10000);

  int k = 0;
  FILE* fpt = fopen(argv[1],"r");

  while(!feof(fpt)){
    char c = fgetc(fpt);
    constraint[k++]=c;
  }
  if (k>0) 
    constraint[k-1]='\0';
  else
    throwRuntimeException( "Unexpected!" );

  if (constraint[0] == '\0')
    return TRUE;

  printf("constraint: %s\n", constraint);
  
  vc_push(vc);
  marker = 0;
  int constraintCount = 1;
  char c;
  i = 0;
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


  jboolean result;
  if (constraintCount > 1){
    Expr andExpr = vc_andExprN(vc, constraintArray, constraintCount);
    linkedlist_add(&exprPool, andExpr);
    result = check(vc, andExpr);
  }
  else{
    result = check(vc,constraintArray[0]);
  }

  //clean up
  free(constraintArray);
  freeStuff();
  vc_pop(vc);

  if(result)
    printf("satisfiable\n");
  else
    printf("unsatisfiable\n");

}
