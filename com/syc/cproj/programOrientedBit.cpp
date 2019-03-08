#include <iostream>
#include <bitset>

using namespace std;

int changeParam(int& a){
    int num=0;
    while (a) {
        a &= (a-1);
        num++;
    }
    return num;
}

int getXthDig(int a, char x) {
    return a >> x  & 1;
}
int& notXdig(int& a, char xTh){
    a ^= (1 << xTh );
    return a;
}
int changeParam(int& a){
    ++a;
    return a;
}


int cal1dig(int a) {
    int count = 0;
    for(; a; a>>=1){
        if(a&1){
            count++;
        }
    }
    return count;
}






int main()
{
    int a = 0b1010'0001;
    std::cout<< a << "    "<< getXthDig(a, 4)<<std::endl;
    std::cout<< a << "    " << bitset<12>(notXdig(a,1))<< "    "<< a <<std::endl;
    std::cout<<a<<std::endl;
    std::cout<<changeParam(a)<<std::endl;
}
