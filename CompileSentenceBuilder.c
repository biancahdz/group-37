// C Program to illustrate the system function
#include <stdio.h>
#include <stdlib.h>

int main()
{
    //src/logic_layer/*.java src/ui_layer/*.java
    system("javac -d bin src/data_layer/*.java src/*.java");
    system("echo Project Compiled");

    system("java -cp bin;lib/mysql-connector-j-9.6.0.jar SentenceBuilder");
    return 0;
}