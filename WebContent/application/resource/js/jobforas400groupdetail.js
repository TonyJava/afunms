var rowNum = 0;
var action = "";

function addRow(jobForAS400DetailDivId) {
	var tr = document.getElementById(jobForAS400DetailDivId).insertRow(0);
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
	td00.innerHTML = '<input id="jobId' + str
			+ '" name="jobId' + str + '" type="hidden"  />'
			+ '<span class="must">*</span>作业名称';
	var td01 = tr0.insertCell(tr0.cells.length);
	td01.align = "left";
	td01.innerHTML = '<input type="text" id="jobName' + str + '" name="jobName' + str
			+ '" style="width:100px">'
			+ ' <a href="#" onclick=chooseJobForAS400(' + '"jobName' + str +'")>浏览</a>';
	var td05 = tr0.insertCell(tr0.cells.length);
	td05.className = "lab";
	td05.innerHTML = '<span class="must">*</span>状态';
	var td04 = tr0.insertCell(tr0.cells.length);
	td04.align = "left";
	td04.innerHTML = '<select id="jobStatus' + str + '" name="jobStatus' + str
			+ '" style="width:100px">'
			+ '<option value="1">活动状态</option>'
			+ '<option value="0">非活动状态</option>'
			+ '<option value="-1">不限</option>'
			+ '</select>';
	var td08 = tr0.insertCell(tr0.cells.length);
	td08.className = "lab";
	td08.innerHTML = '<span class="must">*</span>个数';
	var td09 = tr0.insertCell(tr0.cells.length);
	td09.align = "left";
	td09.innerHTML = '<input type="text" id="jobNum' + str + '" name="jobNum' + str
			+ '" style="width:100px">';
	var td02 = tr0.insertCell(tr0.cells.length);
	td02.className = "lab";
	td02.innerHTML = '<span class="must">*</span>活动状态类型';
	var td03 = tr0.insertCell(tr0.cells.length);
	td03.align = "left";
	td03.innerHTML = '<select id="jobActiveStatusType' + str + '" name="jobActiveStatusType' + str
			+ '" style="width:100px">'
			+ '<option value="1">必须出现</option>'
			+ '<option value="0">不允许出现</option>'
			+ '<option value="-1">不限</option>'
			+ '</select>';
	var td07 = tr0.insertCell(tr0.cells.length);
	td07.align = "left";
	td07.innerHTML = '<input type="text" id="jobActiveStatus' + str + '" name="jobActiveStatus' + str
			+ '" style="width:100px">'
			+ ' <a href="#" onclick=chooseJobActiveStatus(' + '"jobActiveStatus' + str +'")>浏览</a>';
	var td06 = tr0.insertCell(tr0.cells.length);
	td06.className = "lab";
	td06.innerHTML = '<a href="javascript:delRow(' + jobForAS400DetailDivId + ','
			+ rowNum + ')">删除</a>';
	document.getElementById("rowNum").value = rowNum;
}


function delRow(jobForAS400DetailDivId, num) {
	
	var str = "" + num;
	while (str.length < 2) {
		str = "0" + str;
	}
	var i = 0;
	
	while (jobForAS400DetailDivId.rows[i].firstChild.firstChild.id != "table"
			+ str) {
		i++;
	}
	
	jobForAS400DetailDivId.deleteRow(i);
}

function jobConfig(jobForAS400DetailDivId, store) {
	
	for (var i = 0; i < store.length; i++) {
		var item = store[i];
		addRow(jobForAS400DetailDivId);
		var str = "" + rowNum;
		if (str.length < 2) {
			str = "0" + str;
		}
		document.getElementById("jobId" + str).value = item.jobId;
		document.getElementById("jobName" + str).value = item.jobName;
		document.getElementById("jobNum" + str).value = item.jobNum;
		document.getElementById("jobStatus" + str).value = item.jobStatus;
		document.getElementById("jobActiveStatusType" + str).value = item.jobActiveStatusType;
		document.getElementById("jobActiveStatus" + str).value = item.jobActiveStatus;
	}
}
