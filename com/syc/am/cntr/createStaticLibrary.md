* First, create separate source files(imaginatively call fred.c and bill.c) for each function.
Here's the first:
   
        #include <stdio.h>
         
        void fred(int arg)
        {
            printf("fred: we passed %d\n", arg);
        }

And here's the second:

    
        #include <stdio.h>
         
        void bill(char *arg)
        {
            printf("bill: we passed %s\n", arg);
        }
   
- You can compile these functions individually to produce object files ready for inclusion into a library.
Do this by invoking the C compiler with the `-c` option, which prevents the compiler from trying to create a complete program. Trying to create a complete program would fail because you haven't defined function main.

        $ gcc -c bill.c fred.c
        $ ls *.o
        bill.o  fred.o
* Now write a program that calls the function bill.
First, it's a good idea to create a header file for your library.
This will declare the functions in your library and should be included by all applications that want to use your library.
It's a good idea to include the header file in the files `fred.c` and `bill.c` too.
This will help the compiler pick up any errors.

        /*
          This is lib.h. It declares the functions fred and bill for users
        */
         
        void bill(char *);
        void fred(int);

- The calling program(`program.c`) can be very simple.
It includes the library header file and calls one of the functions from the library.

        #include <stdlib.h>
        #include "lib.h"
         
        int main()
        {
            bill("Hello World");
            fred(10);
            exit(0);
        }
        
- You can now compile the program and test it.
For now, specify the object files explicitly to the complier, asking it to
compile your file and link it with the previously compiled object module
bill.o and fred.o .


        $ gcc -c program.c
        $ gcc -o program program.o bill.o fred.o
        $ ./program 
        bill: we passed Hello World
        fred: we passed 10

* Now you'll create and use a library.

Use the ar program to create the archive and add your object files to it.
The program is called ar because it creates archives, or collections, of individual files placed together in one large file.

Note that you can also use ar to create archives of files of any type. (Like many UNIX utilities, ar is a generic tool)

        
        $ ar crv libfoo.a bill.o fred.o
        a - bill.o
        a - fred.o
        
- The library is created and the two object files added.
To use the library successfully, some systems, notably those derived from Berkeley UNIX, require that table of contents be created for the library.

Do this with the ranlib commmand. In Linux, this step isn't necessary (but it is harmless) when you're using the GNU software development tools.

                
                $ ranlib libfoo.a 
                $ 
                

Your library is now ready to use. You can add to the list of files to be used by the compiler to create your program like this:

                
                $ gcc -o program program.o libfoo.a 
                $ ./program 
                bill: we passed Hello World
                fred: we passed 10
                $ 
                

You could also use the -l option to access the library, but because it is not in any of the standard places, you have to tell the compiler where to find it by using the -L option like this:

                
                $ gcc -o program program.o -L. -lfoo
                $ ./program 
                bill: we passed Hello World
                fred: we passed 10
                $ 
                


The -L. option tells the compiler to look in the current directory (.) for libraries. The -lfoo option tells the compiler to use a library called libfoo.a (or a shared library, libfoo.so, if one is present).

To see which function are included in an object file, library, or executable program, you can use the nm command.

If you take a look at program and lib.a, you see that the library contains both fred and bill. When the program is created, it inlcudes only functions from the library that it actually needs.

Including the header file, which contains declarations for all of the functions in the library, doesn't cause the entire library to be included in the final program.

[dll vs. static library](https://stackoverflow.com/questions/140061/when-to-use-dynamic-vs-static-libraries)