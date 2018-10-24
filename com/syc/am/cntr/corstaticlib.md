* .o 就是object, 也就相当于windows下编译的obj文件, 俗称目标文件.
* .a 就是archive, 也就相当于windows的VC下编译的lib文件, 俗称静态库文件.


* .o文件是链接文件，.a是静态库文件，靠.o文件生成，作为一个库为外部程序提供函数，接口。

            生成.o文件：
            gcc -c test.o test.c
            生成.a文件:
            ar cqs test.a test.o

* .o 就相当于windows里的obj文件 ，一个.c或.cpp文件对应一个.o文件
* .a 是好多个.o合在一起,用于静态连接 ，即STATIC mode，多个.a可以链接生成一个exe的可执行文件
* .so 是shared object,用于动态连接的,和windows的dll差不多，使用时才载入。