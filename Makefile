all:
	find . -name "*.java" | xargs javac 
clean:
	find . -name "*.class" | xargs rm 
