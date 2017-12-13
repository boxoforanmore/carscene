rm ./*.class
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo
echo "------------------------------------- Syntax errors (if any):"
echo
javac -cp .:../LWJGL/lwjgl.jar:../LWJGL/lwjgl-opengl.jar:../LWJGL/lwjgl-glfw.jar $1.java 
echo
echo "------------------------------------- Runtime errors (if any):"
echo
java -XstartOnFirstThread -cp .:../LWJGL/lwjgl.jar:../LWJGL/lwjgl-opengl.jar:../LWJGL/lwjgl-glfw.jar -Djava.library.path=../LWJGL $1 $2 $3 $4 $5 $6 $7 $8 $9
