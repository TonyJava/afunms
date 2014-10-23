var rowNum = 0;
var action = "";

function addRow(processConfigDivId, processType) {
	var tr = document.getElementById(processConfigDivId).insertRow(0);
	rowNum = rowNum + 1;
	var str = "" + rowNum;
	while (str.length < 2) {
		str = "0" + str;
	}
	var hours = "";
	for (i = 0; i <= 24; i++) {
		hours = hours + '<OPTION value="' + i + '">' + i + '</OPTION>';
	}
	var td = tr.insertCell(tr.cells.length);

	var elemTable = document.createElement("table");
	var elemTBody = document.createElement("tBody");

	elemTable.id = "table" + str;
	elemTable.width = "100%";
	elemTable.align = "left";
	elemTable.cellPadding = "1";
	elemTable.cellSpacing = "1";
	elemTable.bgColor = "black";
	elemTBody.bgColor = "white";
	elemTable.appendChild(elemTBody);
	td.appendChild(elemTable);

	var tr0 = document.createElement("tr");
	elemTBody.appendChild(tr0);
	var td00 = tr0.insertCell(tr0.cells.length);
	td00.className = "lab";
	td00.innerHTML = '<input id="processId' + str
			+ '" name="processConfigId' + str + '" type="hidden"  />'
			+ '<input id="processType' + str + '" name="processType' + str
			+ '" type="hidden"  /><span class="must">*</span>进程名称';
	var td01 = tr0.insertCell(tr0.cells.length);
	td01.align = "left";
	td01.innerHTML = '<input type="text" id="processName' + str + '" name="processName' + str
			+ '" style="width:100px">'
			+ ' <a href="#" onclick=chooseProcess(' + '"processName' + str +'")>浏览</a>';
	var td05 = tr0.insertCell(tr0.cells.length);
	td05.className = "lab";
	td05.innerHTML = '<span class="must">*</span>状态';
	var td04 = tr0.insertCell(tr0.cells.length);
	td04.align = "left";
	td04.innerHTML = '<select id="processStatus' + str + '" name="processStatus' + str
			+ '" style="width:100px">'
			+ '<option value="1">黑名单</option>'
			+ '<option value="0">白名单</option>'
			+ '</select>';
	var td02 = tr0.insertCell(tr0.cells.length);
	td02.className = "lab";
	td02.innerHTML = '<span class="must">*</span>进程数';
	var td03 = tr0.insertCell(tr0.cells.length);
	td03.align = "left";
	td03.innerHTML = '<input id="processTimes' + str + '" name="processTimes' + str
			+ '" style="width:100px">';
	var td06 = tr0.insertCell(tr0.cells.length);
	td06.className = "lab";
	td06.innerHTML = '<a href="javascript:delRow(' + processConfigDivId + ','
			+ rowNum + ')">删除</a>';
	document.getElementById("rowNum").value = rowNum;
	document.getElementById("processType"+str).value = processType;
}


function delRow(processConfigDivId, rowNo) {
	
	var str = "" + rowNo;
	while (str.length < 2) {
		str = "0" + str;
	}
	var i = 0;
	
	while (processConfigDivId.rows[i].firstChild.firstChild.id != "table"
			+ str) {
		i++;
	}
	
	processConfigDivId.deleteRow(i);
}

function processConfig(processConfigDivId, store) {
	for (var i = 0; i < store.length; i++) {
		var item = store[i];
		addRow(processConfigDivId, item.processType);
		var str = "" + rowNum;
		if (str.length < 2) {
			str = "0" + str;
		}
		document.getElementById("processId" + str).value = item.processId;
		document.getElementById("processName" + str).value = item.processName;
		document.getElementById("processTimes" + str).value = item.processTimes;
		document.getElementById("processStatus" + str).value = item.processStatus;
	}
}
