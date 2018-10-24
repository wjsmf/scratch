    (function(){

        /** 返回一行 */
        function generateRelativeFundsRow(fund){
            return  '<tr>\
        <td><a href="/investor/fund/{id}" title="{fundName}" target="_blank">{fundName}</td>\
        <td title="{strategy}">{strategy}</td>\
        <td><span class="relative-fund-scoring">{latestScoring}</span></td>\
        <td>{inceptionDate}</td>\
        <td>{cumret_6m}</td>\
        <td>{cumret_12m}</td>\
        <td>{rar_6m}</td>\
        <td>{rar_12m}</td>\
        <td>{maxdd_6m}</td>\
        <td>{maxdd_12m}</td>\
        </tr>'
                    .replace('{id}',fund.id)
                    .replace(/{fundName}/g,fund.fundName)
                    .replace(/{strategy}/g,fund.strategy && fund.strategy.descCN || fund.originalSyStrategy)
                    .replace('{latestScoring}',fund.latestScoringType && fund.latestScoringType.value+1)
                    .replace('{inceptionDate}',fund.inceptionDate)
                    .replace('{cumret_6m}',formateNumber(fund.cumret_6m , 100))
                    .replace('{cumret_12m}',formateNumber(fund.cumret_12m , 100))
                    .replace('{rar_6m}',formateNumber(fund.rar_6m , 100))
                    .replace('{rar_12m}',formateNumber(fund.rar_12m , 100))
                    .replace('{maxdd_6m}',formateNumber(fund.maxdd_6m , 100))
                    .replace('{maxdd_12m}',formateNumber(fund.maxdd_12m , 100));
        }

        function sorter(ele,tbId,data,gorderBy,previousEle){
            if(ele.target.nodeName != "I"){
                return;
            }
            var fie = ele.target.dataset.field;
            var orderBy = gorderBy[fie];
            if(orderBy){ // 已经排过序
                gorderBy[fie] = -gorderBy[fie];
            }else{ //第一次以该字段排序
                gorderBy[fie]=1;
            }

            if(previousEle && previousEle.cList){
                previousEle.cList.add("fa-sort");
                previousEle.cList.remove("fa-sort-asc");
                previousEle.cList.remove("fa-sort-desc");
            }

            var tcl = ele.target.classList;
            tcl.remove("fa-sort");
            tcl.remove("fa-sort-asc");
            tcl.remove("fa-sort-desc");
            if(gorderBy[fie]==1){
                tcl.add("fa-sort-asc");
            }else{
                tcl.add("fa-sort-desc");
            }
            previousEle.cList = tcl;


            var naEles=[];
            for(var i=data.length-1;i>=0;i-=1){
              if(typeof data[i][fie] == "undefined"){
                Array.prototype.push.apply(naEles,data.splice(i,1));
              }
            }
            if (fie == "fundManager") {
            /* 
                按照中文拼音顺序排序 
            */
                dataa.sort(function(a,b){
                    return a.fundManager.localeCompare(b.fundManager,"zh")*gorderBy[fie];
                });
            } else {
                data.sort(function (lo, hi) {
                    if (lo[fie] > hi[fie]) {
                        return gorderBy[fie];
                    } else if (lo[fie] < hi[fie]) {
                        return -gorderBy[fie];
                    } else {
                        return 0;
                    }
                });
            }
           Array.prototype.push.apply(data,naEles);
            renderRelativeFunds(tbId,data);
        }

        function renderRelativeFunds(tBodyId,data){
            var html = [];
            $.each(data,function(index){
                html.push(generateRelativeFundsRow(this));
            });
            tBodyId.innerHTML=html.join('');
            $('.relative-fund-scoring').scoringStar();
        }

        $.ajax('/investor/fund/similarFundsQuery/'+fundId)
            .done(function(data){
              if(data && data.length && data.length > 0 && data[0].id){
            var gorderBy={};
            var previousCList={};
            renderRelativeFunds(relativeFunds,data);
            relativeFundsHead.addEventListener("click",function(ele){
              sorter(ele,relativeFunds,data,gorderBy,previousCList);
            });
              }else{
                    relativeFunds.parentElement.parentElement.classList.add("hide");
                }

                $.ajax('/investor/fund/similarNavFundsQuery/'+fundId).done(function(data){
                    if(data && data.length && data.length > 0 && data[0].id){
                        renderRelativeFunds(navSimilarFunds,data);
                        var gorderBy1={};
                        var previousCList1={};
                        navSimilarFundsHead.addEventListener("click",function(ele){
                            sorter(ele,navSimilarFunds,data,gorderBy1,previousCList1);
                        });
                    }else{
                        navSimilarFunds.parentElement.parentElement.classList.add("hide");
                    }

                }).fail(function(data){

                });
            })
            .fail(function(data){
//                $('#tablecolumns').hide();
            });

        
        /*
	     * 看data中是否还有数据
	     *
	     */
	    function isDataBlank(data) {
		    var keyArr = Object.keys(data);
		    return keyArr.length == 0;
	    }
	    
	    function iteratorOnObject(){
	    	Object.keys(b).forEach(function(k){
			console.log(k,",",b[k]);
		});
	    }
	    
	    function iteratorOnObject2(){ // iteratorOnObject和iteratorOnObject2 是一样的形式,但iteratorOnObject不需要for...in..循环,
	    	for(var k in b){  //iteratorOnObject2需要for...in...循环,性能较差;还需要用hasOwnProperty()过滤掉继承来的属性
		  if(b.hasOwnProperty(k)){
		    console.log(key,", ",b[k]);
		  }
		}
	    }

	

        function createSubTbl(sid) {
            var ntr = document.createElement("tr");
            var td = document.createElement("td");
            $.get("/factor/ucore/{sid}".replace("{sid}",sid)).done(function (data) {
                var newTable = ["<table class='littleTbl'><thead><tr>"];
                if(!data || JSON.stringify(data) == "{}"){
                    newTable.push("<td>No data</td></tr></thead></table>");
                }else{
                    for(var key in data){
                        if(data.hasOwnProperty(key)){
                            newTable.push("<td class='text-center'>");
                            newTable.push(key);
                            newTable.push("</td><td></td>");
                        }
                    }
                    newTable.push("</tr></thead><tbody><tr>");
                    var firstKey = null;
                    var stopFlag = false;
                    while(true){
                        for(var key in data) {
                            if (data.hasOwnProperty(key)) {
                                if(firstKey == key){
                                    newTable.push("</tr>");
                                    if(isDataBlank(data)){
                                        stopFlag = true;
                                        break;
                                    }else{
                                        newTable.push("<tr>");
                                    }
                                }else{}
                                var subMap = data[key];
                                if(JSON.stringify(subMap) == "{}"){
                                    newTable.push("<td></td><td></td>");
                                }else{
                                    for(var subKey in subMap){
                                        if(subMap.hasOwnProperty(subKey)){
                                            newTable.push("<td class='text-center'>");
                                            newTable.push(subKey);
                                            newTable.push("</td><td>")
                                            newTable.push(subMap[subKey]);
                                            newTable.push("</td>");
                                            delete subMap[subKey];
                                            break;
                                        }
                                    }
                                }
                                if(!firstKey){
                                    firstKey = key;
                                }
                            }
                        }
                        if(stopFlag){
                            break;
                        }
                    }
                    newTable.push("</tbody></table>");
                }
                td.align = "center";
                td.valign = "middle";
                td.innerHTML = newTable.join("");
            }).fail(function () {
 
             });
            td.colSpan = ucore_table.firstElementChild.firstElementChild.childElementCount;
            ntr.appendChild(td);
            return ntr;
        }
        
        
        
        
        
        
    })();




