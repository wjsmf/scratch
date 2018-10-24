
            #include <stdio.h>
            #include <stdarg.h>

            #define END -1

            int va_sum (int first_num, ...)
            {
                // (1) 定义参数列表
                va_list ap;
                // (2) 初始化参数列表
                va_start(ap, first_num);

                int result = first_num;
                int temp = 0;
                // 获取参数值
                while ((temp = va_arg(ap, int)) != END)
                {
                    result += temp;
                }

                // 关闭参数列表
                va_end(ap);

                return result;
            }

            int main ()
            {
                int sum_val = va_sum(1, 2, 3, 4, 5, END);
                printf ("%d", sum_val);
                return 0;
            }
