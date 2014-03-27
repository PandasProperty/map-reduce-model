JC = javac 
JVM = java 
.SUFFIXES: .java .class 
.java.class: 
	$(JC) $*.java

CLASSES = calculateSim.java ProcSimilar.java temaAPD.java 

MAIN = temaAPD
ARGS1 = 4
ARGS2 = input.txt
ARGS3 = out

default: classes

classes: $(CLASSES:.java=.class)

run: $(MAIN).class 
	$(JVM) $(MAIN) $(ARGS1) $(ARGS2) $(ARGS3)

clean: 
	$(RM) *.class