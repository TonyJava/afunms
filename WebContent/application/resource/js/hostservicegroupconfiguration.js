var rowNum = 0;
var action = "";

function addRow(hostServiceConfigDivId, hostServiceType) {
	var tr = document.getElementById(hostServiceConfigDivId).insertRow(0);
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
	td00.innerHTML = '<input id="hostServiceId' + str
			+ '" name="hostServiceConfigId' + str + '" type="hidden"  />'
			+ '<input id="hostServiceType' + str + '" name="hostServiceType' + str
			+ '" type="hidden"  /><span class="must">*</span>服务名称';
	var td01 = tr0.insertCell(tr0.cells.length);
	td01.align = "left";
	td01.innerHTML = '<input type="text" id="hostServiceName' + str + '" name="hostServiceName' + str
			+ '" style="width:100px">'
			+ ' <a href="#" onclick=chooseHostService(' + '"hostServiceName' + str +'")>浏览</a>';
	var td02 = tr0.insertCell(tr0.cells.length);
	td02.className = "lab";
	td02.innerHTML = '<span class="must">*</span>活动状态';
	var td03 = tr0.insertCell(tr0.cells.length);
	td03.align = "left";
	td03.innerHTML = '<select id="hostServiceStatus' + str + '" name="hostServiceStatus' + str
			+ '" style="width:100px">'
			+ '<option value="1">活动</option><option value="0">不活动</option></select>';
	var td06 = tr0.insertCell(tr0.cells.length);
	td06.className = "lab";
	td06.innerHTML = '<a href="javascript:delRow(' + hostServiceConfigDivId + ','
			+ rowNum + ')">删除</a>';
	document.getElementById("rowNum").value = rowNum;
	document.getElementById("hostServiceType"+str).value = hostServiceType;
}


function delRow(hostServiceConfigDivId, rowNo) {
	
	var str = "" + rowNo;
	while (str.length < 2) {
		str = "0" + str;
	}
	var i = 0;
	
	while (hostServiceConfigDivId.rows[i].firstChild.firstChild.id != "table"
			+ str) {
		i++;
	}
	
	hostServiceConfigDivId.deleteRow(i);
}

function hostServiceConfig(hostServiceConfigDivId, store) {
	for (var i = 0; i < store.length; i++) {
		var item = store[i];
		addRow(hostServiceConfigDivId, item.hostServiceType);
		var str = "" + rowNum;
		if (str.length < 2) {
			str = "0" + str;
		}
		document.getElementById("hostServiceId" + str).value = item.hostServiceId;
		document.getElementById("hostServiceName" + str).value = item.hostServiceName;
		document.getElementById("hostServiceStatus" + str).value = item.hostServiceStatus;
	}
}
