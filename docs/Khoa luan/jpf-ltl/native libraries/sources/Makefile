#Common stuff
JDK_HOME = /home/saswat/j2sdk1.4.2_12

#Omega
OMEGA_HOME = /home/saswat/work/omega

# CVCLite 
CVCL_HOME = /home/saswat/work/cvcl

# Yices
YICES_HOME = /home/saswat/work/yices-1.0

#STP
STP_HOME = /home/saswat/work/stp-dist

# Hopefully you dont have to change anything beyond this point

cvcl: cvcl.c
	gcc -Wall -O3 -I$(CVCL_HOME)/include/cvcl -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux -I. -c cvcl.c 
	ld -static cvcl.o -L. -lmba -L$(CVCL_HOME)/lib/ -lcvclite -lgmp -L/usr/lib/gcc/i486-linux-gnu/4.0.3 -L/usr/lib -lstdc++ -lc -lgcc_eh  -shared -o libcvcl-interface.so

cvcl-inc:
	gcc -Wall -O3 -I$(CVCL_HOME)/include/cvcl -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux -I. -c cvcl-inc.c 
	ld -static cvcl-inc.o -L. -lmba -L$(CVCL_HOME)/lib/ -lcvclite -lgmp -L/usr/lib/gcc/i486-linux-gnu/4.0.3 -L/usr/lib -lstdc++ -lc -lgcc_eh  -shared -o libcvcl-interface-inc.so

cvcl-incsolve:
	gcc -Wall -O3 -I$(CVCL_HOME)/include/cvcl -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux -I. -c cvcl-incsolve.c 
	ld -static cvcl-incsolve.o -L. -lmba -L$(CVCL_HOME)/lib/ -lcvclite -lgmp -L/usr/lib/gcc/i486-linux-gnu/4.0.3 -L/usr/lib -lstdc++ -lc -lgcc_eh  -shared -o libcvcl-interface-incsolve.so

omega: omega-subsumption.c
	g++-2.95 -O3 -static -Wall -I$(OMEGA_HOME)/omega_lib/include/ -I$(OMEGA_HOME)/basic/include/ -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux -I. -fno-implicit-templates -DDONT_INCLUDE_TEMPLATE_CODE omega-subsumption.c $(OMEGA_HOME)/omega_lib/obj/libomega.a $(OMEGA_HOME)/omega_calc/obj/Exit.o -L. -lmba -shared -o libomega-interface.so

yices: yices.c
	gcc -Wall -O3 -static -Wall -I$(YICES_HOME)/include -I. -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux yices.c $(YICES_HOME)/lib/libyices.a libmba.a -lgmp -lstdc++ -shared -o libyices-interface.so

yices-inc: yices-inc.c
	gcc -Wall -O3 -static -Wall -I$(YICES_HOME)/include -I. -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux yices-inc.c $(YICES_HOME)/lib/libyices.a libmba.a -lgmp -lstdc++ -shared -o libyices-interface-inc.so

yices-incsolve: yices-incsolve.c
	gcc -Wall -O3 -static -Wall -I$(YICES_HOME)/include -I. -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux yices-incsolve.c $(YICES_HOME)/lib/libyices.a libmba.a -lgmp -lstdc++ -shared -o libyices-interface-incsolve.so

stp: stp.c
	g++ -O3 -Wall -static -I$(STP_HOME) -I. -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux stp.c -L$(STP_HOME) -lstp -L. -lmba -lgmp -shared -o libstp-interface.so

