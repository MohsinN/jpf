#include <jni.h>
#include "mba/hashmap.h"
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

char* constraint;
int marker;
yices_type intType;
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
  yices_expr exprs[2], expr;

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
    expr = hashmap_get(&vars, token);
    if (expr == NULL){
      expr = yices_mk_var_from_decl(ctx, yices_mk_var_decl(ctx, token, intType));
      char* token_copy = (char*) malloc(sizeof(char)*(strlen(token)+1));
      strcpy(token_copy, token);
      hashmap_put(&vars, token_copy, expr);
    }
    return expr;
  case 'r':
    throwRuntimeException("Reals are not supported with yices interface yet!");
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

jboolean isSatisfiable()
{
  if (constraint[0] == '\0')
    return TRUE;

  int constraintCount = 1;
  char c;
  int i = 0;
  do{
    c = constraint[i++];
    if (c == ',')
      constraintCount++;
  }while(c != '\0');
  
  marker = 0;
  ctx = yices_mk_context();

  //yicesl_read(ctx,"(set-arith-only! true)"); 
  
  intType = yices_mk_type(ctx, "int");
  yices_expr* constraintArray = (yices_expr*) malloc(sizeof(yices_expr)*constraintCount);
  char token[2]; // it must be just a comma and '\0'
  i = 0;
  do{
    constraintArray[i++] = parse();
  }while(readToken(token));

  yices_expr andExpr = yices_mk_and(ctx, constraintArray, constraintCount);

  yices_assert(ctx, andExpr);
  jboolean result = TRUE;
  
  lbool r = yices_check(ctx);
  switch(r){
  case l_true:
    result = TRUE;
    break;
  case l_false:
    result = FALSE;
    break;
  case l_undef:
    throwRuntimeException("Unknown result from Yices!");
    break;
  }
  /*
  if (yices_check(ctx)==l_true)
    result = TRUE;
  else
    if(yices
    result = FALSE;
  */
  //yices_pp_expr(andExpr);
  //clean up
  free(constraintArray);
  freeStuff();
  yices_del_context(ctx);

  return result;
}

JNIEXPORT void JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_initializeYices
  (JNIEnv *env, jclass cls)
{
  // initialize stuff
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  yices_set_arith_only(1);
  //yices_enable_log_file("queries");
}

JNIEXPORT jboolean JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_isSatisfiable
  (JNIEnv *env, jclass cls, jstring constraintString )
{
  constraint = (char*) (*env)->GetStringUTFChars(env,constraintString, NULL);
  if( constraint == NULL ){
    throwRuntimeException("out of memory");
    return FALSE;  // out of memory
  }

  //printf( "query: %s\n", constraint);
  //fflush(stdout);
  jboolean r = isSatisfiable();

  (*env)->ReleaseStringUTFChars(env,constraintString, constraint);
  
  return r;
}

int main(int argc, char** argv)
{
  if(argc < 2)
    throwRuntimeException( "must specify the file name that contains the query" );

  // initialize stuff
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);

  constraint = (char*) malloc(sizeof(char)*10000);

  int k = 0;
  FILE* fpt = fopen(argv[1],"r");
  
  while(1){
    char c = fgetc(fpt);
    if (c == EOF) break;
    constraint[k++]=c;
  }
  constraint[k]='\0';
  

  printf( "query: %s\n", constraint);
  fflush(stdout);

  yices_enable_log_file("tcas");
  //yices_dump_context(ctx);
  
  if (isSatisfiable())
    printf("satisfiable");
  else
    printf("unsatisfiable");

  //clean up
  free(constraint);
  return EXIT_SUCCESS;
}
