mkdir -p bin/
javac -sourcepath src -classpath "lib/*:bin/" src/pt/uminho/haslab/echo/Echo.java -d bin
ant
