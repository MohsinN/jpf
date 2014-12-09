#include <jni.h>
#include <omega.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "mba/hashmap.h"
#include "mba/linkedlist.h"

#include "gov_nasa_jpf_symbolic_dp_NativeInterface.h"

#define TRUE 1
#define FALSE 0

struct Constraint{
  char* constraint_str;
  struct Constraint* next;
  int var_count; // cummulative var count. More doc!
};

struct hashmap id2Constraint;
int pcCount = 1;

struct hashmap vars;

/** a map from shape strings to
    linked list of Relations
*/
struct hashmap states;

char* constraint;
int marker;

Relation relation;
F_And *fAnd;
F_Exists *fExists;
Constraint_Handle currentHandle;

int treat_x_as_e = 0; //doc??

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

Variable_ID declareVar(char* token)
{
  //printf("* %s", token); fflush(stdout);
  Variable_ID v = (Variable_ID) hashmap_get(&vars, token);
  if (v != NULL)
    return v;

  if (token[0] == 'e'){
    //printf( "exist %s\n", token); fflush(stdout);
    v = fExists->declare();
  }
  else{
    //printf( "free %s\n", token); fflush(stdout);
    char tmp[10];
    strcpy(tmp, token+1);
    v = relation.set_var(atoi(tmp));
  }
  char* token_copy = new char[strlen(token)+1];
  strcpy(token_copy, token);
  hashmap_put(&vars, token_copy, v);
  return v;
}

void parse(int mult)
{
  char token[10];

  if (!readToken(token))
    return;
  //printf("%s ",token); fflush(stdout);
  switch(token[0]){
  case '+':
    parse(mult);
    parse(mult);
    break;
  case '-':
    if (token[1] == '\0'){
      parse(mult);
      parse(-mult);
    }
    else{
      // token[1] must be a digit
      currentHandle.update_const(mult*atoi(token));
    }
    break;
  case '*':
    // assumption: left hand operand of * is always a number
    readToken(token);
    parse(atoi(token)*mult);
    break;
  case '<':
    currentHandle = fAnd->add_GEQ();
    parse(-1);
    parse(1);
    if (token[1] != '=')
      currentHandle.update_const(-1);
    break;
  case '>':
    currentHandle = fAnd->add_GEQ();
    parse(1);
    parse(-1);
    if (token[1] != '=')
      currentHandle.update_const(-1);
    break;
  case '=':
    currentHandle = fAnd->add_EQ();
    parse(1);
    parse(-1);
    break;
  case '!':
    currentHandle = fAnd->add_not()->add_and()->add_EQ();
    parse(1);
    parse(-1);
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
    currentHandle.update_const(mult*atoi(token));
    break;
  case 'x':
    if (treat_x_as_e)
      token[0] = 'e';
  case 'e':
    currentHandle.update_coef(declareVar(token), mult);
    break;
  default:
    printf( "%s", token);
    throwRuntimeException( "unexpected type of token" );
  }
}

void printPC(int id)
{
  if (id != 1){
    struct Constraint *c = (struct Constraint*) hashmap_get(&id2Constraint, (void*)id);
    while (c != NULL){
      printf("%s", c->constraint_str); fflush(stdout);
      c = c->next;
      if (c != NULL) printf(" , ");
    }
  }
  printf("\n");
}


int assertFormula(char* conjunct, int id)
{
  char token[15];
  int varCount; 

  //printf("assert %s------\n", conjunct); fflush(stdout);

  struct Constraint *parent_constraint = NULL;

  // First need to find out the number of variables
  if (id != 1){
    struct Constraint *c = (struct Constraint*) hashmap_get(&id2Constraint, (void*)id);
    parent_constraint = c;
    varCount = c->var_count;
  }
  else
    varCount = 0;

  constraint = conjunct;
  marker = 0;
  while (readToken(token)){
    if (token[0] == 'x'){
      char tmp[10];
      int i = atoi(strcpy(tmp, token+1));
      // assumes the variables are numbered sequentially start at 1
      if (i > varCount)
	varCount = i;
    }
  }

  //printf("varcount %d\n", varCount); fflush(stdout);
  
  relation = Relation(varCount);
  fAnd = relation.add_and();

  marker = 0;
  parse(1);

  //printf("parsed the conjunct\n"); fflush(stdout);

  struct Constraint *c = parent_constraint;
  while (c != NULL){
    constraint = c->constraint_str; //printf(" , %s", constraint); fflush(stdout);
    marker = 0;
    parse(1);
    c = c->next;
  }
  
  int result = relation.is_upper_bound_satisfiable();

  if (result){
    char* s = (char*) malloc(sizeof(char)*(strlen(conjunct)+1));
    strcpy(s, conjunct);
    struct Constraint* con = (struct Constraint*) malloc(sizeof(struct Constraint));
    con->constraint_str = s;
    con->next = parent_constraint;
    con->var_count = varCount;
    pcCount++;
    hashmap_put(&id2Constraint, (void*) pcCount, con);
    return pcCount;
  }
  else
    return 0;
}

void reconstructPathCondition(int id)
{
  if (id == 1)
    return;

  treat_x_as_e = 1;
  struct Constraint *c = (struct Constraint*) hashmap_get(&id2Constraint, (void*)id);
  //printf( "\nReconstructing %d PC:", id );
  while (c != NULL){

    constraint = c->constraint_str; 
    //printf(" %s,", constraint); fflush(stdout);
    marker = 0;

    parse(1);
    c = c->next;

  }
  treat_x_as_e = 0;
  //printf(" -- \n"); fflush(stdout);
  //printf("\nSC: " );   fflush(stdout);
  //relation.print(stdout);
  //fflush(stdout);


}
 

/** reads from the global character array constraint
    and builds the relation pointed to by global variable relation
*/
void buildRelation(int add_exists)
{
  int varCount = 0;
  marker = 0;
  char token[15];

  while (readToken(token)){
    //if (!token) 
    //  break;
    if (token[0] == 'x'){
      char tmp[10];
      int id = atoi(strcpy(tmp, token+1));
      // assumes the variables are numbered sequentially start at 1
      if (id > varCount)
	varCount = id;
    }
  }

  //printf("varCount: %d", varCount);
  //fflush(stdout);

  relation = Relation(varCount);

  if (add_exists){
    fExists = relation.add_exists();
    fAnd = fExists->add_and();
  }
  else{
    fAnd = relation.add_and();
  }

  marker = 0;
  do{
    parse(1);
  }while(readToken(token));
  
}

jboolean checkImplication(Relation* r1, Relation* r2)
{
  // is !r1 \/ r2 valid? In other words, is r1 /\ !r2 unsatisfiable?

  //r1->print(stdout); r2->print(stdout); fflush(stdout);
  //printf("h0\n");fflush(stdout);
  // Intersection will segfault if number of free vars are not same in its two arguments
  Relation r = Intersection(copy(*r1),Complement(copy(*r2)));
  //printf("h1\n");fflush(stdout);
  if (r.is_upper_bound_satisfiable())
    return FALSE;
  else
    return TRUE;
}
/*
int isSubsumed(char* shape)
{
  relation->simplify();

  //relation->print(stdout);

  struct linkedlist *rs = (struct linkedlist*) hashmap_get(&states,shape);
  if (rs == NULL){
    //new state 
    //printf( "NEW STATE" );
    //fflush(stdout);
    rs = (struct linkedlist*) malloc(sizeof(struct linkedlist));
    linkedlist_init(rs, 0, NULL);
    linkedlist_add(rs, relation);
    hashmap_put(&states, shape, rs);
    return FALSE;
  }

  unsigned int i;
  for(i = 0; i < linkedlist_size(rs); i++){
    Relation* old_r = (Relation*) linkedlist_get(rs,i);
    
    if (checkImplication(relation, old_r)){
      //printf( "TRUE\n" );
      //fflush(stdout);
      return TRUE;
    }
    
    if (checkImplication(old_r,relation)){
      linkedlist_remove(rs,i);
      i--;
    }
  }
  linkedlist_add(rs,relation);
  return FALSE;
}
*/

int isSubsumed(char* shape)
{
  printf("Before simplification: " );relation.print(stdout); fflush(stdout);

  relation.simplify();

  Relation* old_r = (Relation*) hashmap_get(&states,shape);
  if (old_r == NULL){
    Relation* rel_copy = new Relation();
    *rel_copy = copy(relation);
    hashmap_put(&states, shape, rel_copy);
    //printf("new shape"); fflush(stdout);
    return FALSE;
  }
  else{
    jboolean flag = checkImplication(&relation,old_r);
    if (flag){
      return TRUE;
    }
    else{
      Relation* newr = new Relation();
      *newr = Union(relation, *old_r);
      char* tmp_s = shape;
      Relation* tmp_r;
      hashmap_remove(&states, (void**)&tmp_s, (void**)&tmp_r);
      free(tmp_s);
      if (old_r != tmp_r){
	printf( "Memory leak\n" );
	fflush(stdout);
	exit(-1);
      }
      free(tmp_r); 
      
      hashmap_put(&states, shape, newr);
      return FALSE;
    }
  }

}


void initialize()
{
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  hashmap_init(&states, 0, hash_str, cmp_str, NULL, NULL);
  hashmap_init(&id2Constraint, 0, NULL, NULL, NULL, NULL);
}

JNIEXPORT jboolean JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_isSubsumedInc
  (JNIEnv *env, jclass cls, jstring shape_string, jstring valuation, jint pcid)
{
  char* shape = (char*) env->GetStringUTFChars(shape_string, NULL);
  if (shape == NULL)
    throwRuntimeException("Shape is NULL!");

  char* val = (char*) env->GetStringUTFChars(valuation, NULL);
  if (val == NULL)
    throwRuntimeException("Valuation is NULL!");

  constraint = val;
  buildRelation(1);

  reconstructPathCondition(pcid);
  jboolean result = isSubsumed(shape);

  //clean up
  iter_t iter; char* varname;
  hashmap_iterate(&vars, &iter);
  while ((varname = (char*) hashmap_next(&vars, &iter)))
    free(varname);
  hashmap_clear(&vars, NULL, NULL, NULL);
  env->ReleaseStringUTFChars(valuation, val);
  relation = Relation::Null();

  printf("%d\n", result); fflush(stdout);

  return result;
}


JNIEXPORT jboolean JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_isSubsumed
  (JNIEnv *env, jclass cls, jstring shape_string, jstring pathcondition)
{
  char* shape = (char*) env->GetStringUTFChars(shape_string, NULL);
  if (shape == NULL)
    throwRuntimeException("Shape is NULL!");

  constraint = (char*) env->GetStringUTFChars(pathcondition, NULL);
  if (constraint == NULL)
    throwRuntimeException("PC is NULL!");

  //printf("%s\n",constraint);
  //fflush(stdout);

  buildRelation(1);

  jboolean result = isSubsumed(shape);

  //clean up
  iter_t iter; char* varname;
  hashmap_iterate(&vars, &iter);
  while ((varname = (char*) hashmap_next(&vars, &iter)))
    free(varname);
  hashmap_clear(&vars, NULL, NULL, NULL);

  env->ReleaseStringUTFChars(pathcondition, constraint);
  relation = Relation::Null();
  //free(relation);

  //printf("%d\n", result); fflush(stdout);

  return result;
}
 

JNIEXPORT jboolean JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_isSatisfiable
  (JNIEnv *env, jclass cls, jstring constraintString)
{
  constraint = (char*) env->GetStringUTFChars(constraintString, NULL);
  if( constraint == NULL )
    return FALSE;  // out of memory

  //printf( "Assert: %s\n", constraint);
  //fflush(stdout);
  //if (constraint[0] == '\0')
  //  return TRUE;

  buildRelation(0);

  //relation->print(stdout);
  //fflush(stdout);

  jboolean result = relation.is_upper_bound_satisfiable();

  //clean up
  iter_t iter; char* varname;
  hashmap_iterate(&vars, &iter);
  while ((varname = (char*) hashmap_next(&vars, &iter)))
    free(varname);
  hashmap_clear(&vars, NULL, NULL, NULL);

  env->ReleaseStringUTFChars(constraintString, constraint);
  relation = Relation::Null();
  //free(relation);
  
  //printf("* %d\n", result); fflush(stdout);
  
  return result;
}

JNIEXPORT void JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_initializeOmega
  (JNIEnv *env, jclass cls)
{
  initialize();
}

JNIEXPORT jint JNICALL Java_gov_nasa_jpf_symbolic_dp_NativeInterface_assertFormula
  (JNIEnv *env, jclass cls, jstring constraintString, jint id )
{
  char* cstr = (char*) env->GetStringUTFChars(constraintString, NULL);
  if( cstr == NULL ){
    throwRuntimeException( "Out of Memory" );
  }

  //printf("\nAsserting %s on %d: ", cstr, id); printPC(id); fflush(stdout);

  //printf("%s %d\n", cstr, id);

  jint r = assertFormula(cstr, id); 

  //printf("Assert: "); printPC(r); fflush(stdout);
  
  env->ReleaseStringUTFChars(constraintString, cstr);
  iter_t iter; char* varname;
  hashmap_iterate(&vars, &iter);
  while ((varname = (char*) hashmap_next(&vars, &iter)))
    free(varname);
  hashmap_clear(&vars, NULL, NULL, NULL);
  relation = Relation::Null();

  //printf("* %d\n", r>0); fflush(stdout);
  
  return r;
}


int main(int argc, char* argv[])
{
  /*
  constraint = ">= * 2 + x1 1 - + 5 x2 * 2 e3";

  
  relation = new Relation();

  printf("ww\n");
  fflush(stdout);

  relation = new Relation(2);

  printf("ww\n");
  fflush(stdout);
  
  hashmap_init(&vars, 0, hash_str, cmp_str, NULL, NULL);
  hashmap_init(&states, 0, hash_str, cmp_str, NULL, NULL);



  buildRelation(1);
  
  relation.print(stdout);
  
  relation.simplify();

  relation.print(stdout);

  printf("%d", isSubsumed("a"));

  constraint = ">= x1 + x2 2";
  
  buildRelation(1);
  
  printf("%d",relation.is_upper_bound_satisfiable());

  printf("%d", isSubsumed("a"));

  constraint = "> x1 0";
  
  buildRelation(1);
  */

  initialize();
  
  printf( "%d", assertFormula("> x1 1",1));
  printf( "%d", assertFormula("< x1 4",2));
  //printf( "%d", assertFormula(">= x1 0",5));
  //printf( "%d", assertFormula("<= x2 1",3));
}
