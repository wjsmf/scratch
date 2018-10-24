<h1>Hello ${param["name"]}</h1>
<h1>Hello ${cookie["sid"].value}</h1>
<script>
	var selectedPanel=$('#portfolio_funds_table'),
	selectedFunds = [], analysisBtn;
	$(function () {
		analysisBtn = $("#analysis_funds");
		analysisBtn.attr('disabled', true);
	
	});
	
	function renderSelectFund(rowDatas,mainId) {
			var table = selectedPanel.get(0);
			var trClass = table.firstElementChild.lastElementChild.classList;
			for (var j = 0, leng = rowDatas.length; j < leng; j = j + 1) {
				var rowData = rowDatas[j];
				if (selectedFunds.indexOf(rowData.id) <= -1) {
					var row = table.insertRow();
					row.id = 'fund-s-' + rowData.id;
					if (trClass.contains("datagrid-row-selected")) {
					} else {
						row.classList.add("datagrid-row-selected");
					}
					row.insertCell().textContent = rowData.id;
					var flagMainId = row.insertCell();
					flagMainId.textContent = mainId;
					flagMainId.classList.add("hide");
					var deleteCell = row.insertCell();
					deleteCell.role = "button";
					deleteCell.innerHTML = $('<a role="button" href="javascript:void(0)" />')
						.html("<i class='fa fa-minus-square text-danger font-16'></i>&nbsp;")
						.get(0).outerHTML;
					var id = rowData.id;
					selectedFunds.push(id);
				}
			}
			analysisBtn.attr('disabled', false);
	}
	
	portfolio_funds_table.addEventListener("click",function(evt) {
			var srcEle = evt.target;
			var tgName = srcEle.nodeName;
			if (tgName == "A" || tgName == "I") {
				var tr = tgName == "A" ? srcEle.parentElement.parentElement : srcEle.parentElement.parentElement.parentElement;
				var mainIdAtthisTr = tr.querySelector(".hide").innerText.trim(); // current line on which deletion event happened
				selectedFunds.splice(selectedFunds.indexOf(parseInt(tr.firstElementChild.innerText.trim(), 10)), 1);
				portfolio_funds_table.firstElementChild.removeChild(tr);
				var hides = portfolio_funds_table.querySelectorAll(".hide");
				//            var isTheOnlyMainId = true;
				//            for(var i=0,leng=hides.length;i<leng;i=i+1){
				//                var eachMainId = hides[i].innerText.trim();
				//                if(eachMainId == mainIdAtthisTr){
				//                    isTheOnlyMainId = false;
				//                    break;
				//                }
				//            }
				//            if(isTheOnlyMainId){
				//                rearrangeBgColor();
				//            }else{}
				for (var i = 0, leng = hides.length; i < leng; i = i + 1) {
					var eachMainId = hides[i].innerText.trim();
					if (eachMainId == mainIdAtthisTr) {
						rearrangeBgColor();
						break;
					}
				}
			}
			var funds = portfolio_funds_table.querySelectorAll(".sFund");
			var arrs = [];
			for (var i = 0, len = funds.length; i < len; i = i + 1) {
				var cbox = funds[i];
				if (cbox.checked) {
					arrs.push(cbox.id)
				}
			}
			//	    if(arrs.length>=1){
			//		    analysisBtn.attr('disabled', false);
			//	    }else{
			//		    analysisBtn.attr('disabled', true);
			//	    }

			analysisBtn.attr('disabled', arrs.length < 1);

	});
	
	function unSelectFund(rowIndex, rowData) {
			if (!rowData || !rowData.id) {
				return;
			}
			var hides = portfolio_funds_table.querySelectorAll(".hide");
			for (var i = hides.length - 1; i >= 0; i = i - 1) {
				var hideTd = hides[i];
				var hideMainId = hideTd.innerText.trim();
				if (rowData.id == hideMainId) {
					var tr2 = hideTd.parentElement;
					portfolio_funds_table.firstElementChild.removeChild(tr2);
					selectedFunds.splice(selectedFunds.indexOf(parseInt(tr2.firstElementChild.innerText.trim())), 1);
				}
			}

			rearrangeBgColor();
			if (selectedFunds.length <= 0) {
				analysisBtn.attr('disabled', true);
			}
	}
	
	function rearrangeBgColor() {
			//        var hides = portfolio_funds_table.querySelectorAll(".hide");
			//        var hideMainId = null;
			//        var hasColor = null;
			//        for(var i=0,leng=hides.length;i<leng;i=i+1){
			//            var td=hides[i];
			//            var tr=td.parentElement;
			//            var mainId=td.innerText.trim();
			//            if(hideMainId){
			//                if(hideMainId == mainId){
			//                    hasColor ? tr.classList.add("datagrid-row-selected"):
			//                        tr.classList.remove("datagrid-row-selected"); //follow the previous line's bgColor
			//                }else{  // key point , need to change the bgColor
			//                    hideMainId = mainId;
			//                    hasColor ? tr.classList.remove("datagrid-row-selected"):
			//                        tr.classList.add("datagrid-row-selected");
			//                    hasColor = tr.classList.contains("datagrid-row-selected");
			//                }
			//            }else{ //initial
			//                hideMainId = mainId;
			//                hasColor = tr.classList.contains("datagrid-row-selected");//remember the initial line's bgColor
			//            }
			//        }
			var hides = portfolio_funds_table.querySelectorAll(".hide");
			if (hides.length < 2) {
				return;
			}
			var hideMainId = hides[0].innerText.trim();
			var hasColor = hides[0].parentElement.classList.contains("data-sepe");//remember the initial line's bgColor;
			for (var i = 1, leng = hides.length; i < leng; i = i + 1) {
				var td = hides[i];
				var tr = td.parentElement;
				var mainId = td.innerText.trim();
				if (hideMainId == mainId) {
					hasColor ? tr.classList.add("data-sepe") :
						tr.classList.remove("data-sepe"); //follow the previous line's bgColor
				} else {  // key point , need to change the bgColor
					hideMainId = mainId;
					hasColor ? tr.classList.remove("data-sepe") :
						tr.classList.add("data-sepe");
					hasColor = tr.classList.contains("data-sepe");
				}
			}
	}
document.cookie="sid=3fafrq4w;"
</script>
<% Cookie ck=new Cookie("xid","999999");response.addCookie(ck); %>
<h1>Hello ${cookie["xid"].value}</h1>

<!--
	 jsp的内置对象cookie, 既可以获取request也可以获取response上的cookie
	 只是在获取某个cookie项的值时要使用 ${cookie["xxx"].value} ,
	 一定不能少了那个.value,否则获得的值是javax.servlet.http.Cookie@6219af72
	 这样毫无用处的内容


-->

