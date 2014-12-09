JPF-LTL model checker: 
----------------------

I. Introduction
This JPF extension is a model checker for linear temporal properties. 
Currently, it can verify the safety properties which adopts the linear 
temporal logic to describe the semantics of the finite model checking.
The LTL to Buchi automata translation module used in this model checker
is the existing LTL2Buchi extension which is presently modifying by 
Estar. You can get the LTL2Buchi source from the following reposity:
http://bitbucket.org/estar/ltl2buchi  

This model checker supports symbolic and concrete mixed execution of the 
system under test (SUT).

This project's reposity: http://bitbucket.org/francoraimondi/jpf-ltl

II. Installation
1. Prerequisites
This project includes all required the following projects:

  jpf-core: 	http://babelfish.arc.nasa.gov/trac/jpf/wiki/projects/jpf-core
  jpf-symbc: 	http://babelfish.arc.nasa.gov/trac/jpf/wiki/projects/jpf-symbc

2. Install IDE plugin   
 The above three projects and this jpf-ltl should be placed in one folder.
 
 jpf-home
 	jpf-core
 	jpf-symbc
 	jpf-ltl
 	
Suppose all projects are located with the above layout and you have installed 
the corresponding IDE plugin , you have to change the site.properties accordingly.
Yet, if you have not install these plugins, please follow the following links
http://babelfish.arc.nasa.gov/trac/jpf/wiki/projects/eclipse-jpf
http://babelfish.arc.nasa.gov/trac/jpf/wiki/projects/netbeans-jpf

And follow http://babelfish.arc.nasa.gov/trac/jpf/wiki/install/site-properties 
For more information about the site.properties file.

*NOTE*: 
-If you choose to have your own located JPF projects folder layout, make sure to 
change the site.properties file accordingly and go to the jpf-ltl/build.xml file
and edit the following line:

	<property name="jpf-symbc" value = "../jpf-symbc"/>
  	<property name="jpf-core" value = "../jpf-core"/>
  	
 where "../jpf-symbc" and "../jpf-core" shoule be the path of
 jpf-core, jpf-symbc project
 
 III. Building 
 You can build projects by means of Ant build.xml scripts. If you have any problem with
 this Ant builder in Eclipse please edit it by:
 	- In Eclipse go to Project -> Properties
 	- Select Builders
 	- Pick Ant_Builder -> click Edit
 	- Click on the JRE tab
 	- Select Separate JRE -> Installed JREs
 	- On Windows and Unix-based systems pick JDK1.6xxx. 
 	If it is not listed under the installed JREs, click on Add, 
 	browse your file system to where JDK1.6xxx resides and select. 
 	On OSx systems pick the JVM 1.6.0.
 
 IV. Running
 - Using eclipse plugin
 	+ Right click on a .jpf file. Examples can be found in the src\examples directory in jpf-ltl
 	+ If the eclipse plugin is correctly installed, a Verify option will appear.
 	+ Select the Verify option and the verification process of the system specified in the .jpf file begins
 - Using eclipse Run configuration
 	+ Click Run -> Run Configurations
 	+ In the Main tab, specify jpf-ltl as your project name and
 	 gov.nasa.jpf.tool.RunJPF 			as your Main class
 	+ Switch to Arguments tab, provide the target class name and jpf-arguments if any
 		For instance, we type the following text in the Arguments tab where RandomEnumeration is the target class
 		
 		+cg.enumerate_random=true
		RandomEnumeration

V. Specifying the LTL properties

You must annotate your target class with the @LTLSpec or @LTLSpecFile annotation which
the first one is used to annotate a LTL formulae String; the second one is used to annotate
the path to the *.ltl file
You can also add comment to your *.ltl file by // or /* ... */
 
1. Supported LTL operator
AND						:	/\ &&
OR						:	\/ ||
UNTIL					:	U
WEAK_UNTIL				:	W
RELEASE					:	V
WEAK_RELEASE			:	M
NOT						:	!
NEXT					:	X
ALWAYS					:	[]
EVENTUALLY				:	<>
IMPLIES					:	->

2. Supported atomic proposition type
	- Method call e.g. 	
 		Your.TargetClass.yourMethod(int, String[] [], float)
 	- true
 	- false
 	- boolean variable 
 		+ Class field e.g. Your.TargetClass.boolField
 		+ LocalVariable e.g. Your.TargetClass.yourMethod(int, String[], long).boolVar
 	- Relation e.g.:
 		(var1 + var2) * var 3 - 4 <= 5.3 - var3
 	where varx is the variable may be Class field or local variable which follows 
 	the writing syntax as a boolean variable. 
 
 For example:
 
 !<>(FormulaSample.declared + FormulaSample.test(float ,String).localInit > 3.0 
/\ 
<> (FormulaSample.test(float ,String).local1 > (3.2))) 

VI. Visualization of the *negated* Buchi automata
You can visualize the negated Buchi automata which translated from the negated LTL formula 
by means of specify in the *.jpf file

show_buchi=true

or add the plus symbol if you run in the eclipse run configuration or command lines

+show_buchi=true 

VII. Deal with the infinite and finite trace
If you know that the system under test is infinite, you should change the following 
option in the *.jpf file

finite=false

or in the command line as:

+finite=false