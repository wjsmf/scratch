# 解决对集合迭代时的并发修改问题
* 
        var naEles=[];
            var rawBool = true;
            while(rawBool){
                rawBool = false;
                for(var i=0;i<dataa.length;i+=1){
                    if(typeof dataa[i][fie] == "undefined"){
                        Array.prototype.push.apply(naEles,dataa.splice(i,1));
                        rawBool = true;
                        break;
                    }
                }
            }



*
        var naEles=[];
            for(var i=dataa.length-1;i>=0;i-=1){
                if(typeof dataa[i][fie] == "undefined"){
                    Array.prototype.push.apply(naEles,dataa.splice(i,1));
                }
            }
