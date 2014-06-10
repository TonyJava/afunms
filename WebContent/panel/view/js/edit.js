// JavaScript Document
var existingIP = new Array();


/**************** Util Function - Begin ****************/

// 判断数组中是否有该元素
Array.prototype.contains = function() {
	for (var i = 0; i < this.length; i++) {
		if (arguments[0] == this[i]) {
			return true;
		}
	}
	return false;
}

// 去除字符串中的空格
String.prototype.trim = function() {													
    return this.replace(/(^\s*)|(\s*$)/g, "");
}

/**************** Util Function - End ****************/

function checkIPAddress(ip) {
	// 检测IP地址格式是否合法
    if (ip.trim() == "") {
		alert("请输入设备IP地址！");
	    return false;
	}
	else {
		var tmpIpList = ip.split(".");
		if (tmpIpList.length != 4) {
			alert("请输入完整的IP地址！");
			return false;
		}
		else {
			if (isNaN(tmpIpList[0]) || isNaN(tmpIpList[1]) || isNaN(tmpIpList[2]) || isNaN(tmpIpList[3])) {
				alert("IP地址必须为数字！");
				return false;
			}
			else if (tmpIpList[0]>255 || tmpIpList[0]<0 || tmpIpList[1]>255 || tmpIpList[1]<0 || tmpIpList[2]>255 || tmpIpList[2]<0 || tmpIpList[3]>255 || tmpIpList[3]<0) {
				alert("非法的IP地址！");
				return false;
			}
		}
	}
	
	// 检测是否为已存在的IP
	for (var i = 0; i < existingIP.length; i++) {
		if (ip == existingIP[i]) {
			alert("IP地址已存在！\r\n请重新输入IP地址。");
			return false;
		}
	}
	
	return true;
}

function checkMask(mask) {
	mask = mask.trim();
	if (mask == "") {
		alert("请输入子网掩码！");
		return false;
	}
	else {
		var tmpMaskList = mask.split(".");
		if (tmpMaskList.length != 4) {
			alert("子网掩码格式不正确！");
			return false;
		}
		else {
			if(isNaN(tmpMaskList[0]) || isNaN(tmpMaskList[1]) || isNaN(tmpMaskList[2]) || isNaN(tmpMaskList[3])) {
				alert("子网掩码必须为数字！");
				return false;
			}
			else if (tmpMaskList[0]>255 || tmpMaskList[0]<0 || tmpMaskList[1]>255 || tmpMaskList[1]<0 || tmpMaskList[2]>255 || tmpMaskList[2]<0 || tmpMaskList[3]>255 || tmpMaskList[3]<0) {
				alert("非法的子网掩码！");
				return false;
			}
		}
	}
	return true;
}


function submitAddDeviceForm() {
	// 检测IP地址
	if (checkIPAddress(addDeviceForm.ipaddress.value)) {
		var ipaddress = addDeviceForm.ipaddress.value;
	}
	else {
		return false;
	}

	var linked = addDeviceForm.linked.value;
	
	var tmpLinked = linked.split("$");
	var msg = "请确认添加设备信息：\r\n";
	msg += "* 设备IP地址：" + ipaddress + "\t\r\n";
	/*msg += "* 与其相连的设备：\r\n";
	if (tmpLinked[0] == "127.0.0.1") {
		msg += "  - 无\r\n"
	}
	else {
		for (var i = 0; i < tmpLinked.length; i++) {
			msg += "  - " + tmpLinked[i] + "\r\n";
		}
	}*/
	msg += "\r\n是否确认以上信息？"
	if (!window.confirm(msg)) {
		return false;
	}
	
	var allValue = ipaddress + "#" + linked;
	
	window.returnValue = allValue;
	window.close();
	return false;
}


function submitDelDeviceForm() {
	var deleted = delDeviceForm.deleted.value;
	if (deleted == null || deleted == "127.0.0.1" || deleted == "") {
		return false;
	}
	
	var tmp = deleted.split("$");
	var msg = "请确认删除设备信息：\r\n";
	msg += "* 您指定删除的设备有：\r\n";
	for (var i = 0; i < tmp.length; i++) {
		msg += "  - " + tmp[i] + "\t\r\n";
	}
	msg += "\r\n是否确认以上信息？";
	if (!window.confirm(msg)) {
		return false;
	}

	window.returnValue = deleted;
	window.close();
	return false;
}


function submitAddLinkForm() {
	var line = addLinkForm.linkData.value;
	if (line == null || line == "127.0.0.1$127.0.0.1" || line == "") {
		return false;
	}
	var interface = addLinkForm.interfaceData.value;
	//if (interface == null || interface == "null$null" || interface == "") {
	//	return false;
	//}
	
	var tmpLine = line.split("#");
	var tmpIf = interface.split("#");
	if (tmpLine.length != tmpIf.length) {
		window.alert("添加的链路信息与设备信息不符，无法添加！");
		return false;
	}
	var msg = "请确认添加链路信息：\r\n";
	msg += "* 您指定添加的链路有：\r\n";
	for (var i = 0; i < tmpLine.length; i++) {
		var ipTeam = tmpLine[i].split("$");
		var ifTeam = tmpIf[i].split("$");
		msg += "  - " + ipTeam[0] + "(" + ifTeam[0] + ") 与 " + ipTeam[1] + "(" + ifTeam[1] + ") 相连的链路\t\r\n";
	}
	msg += "\r\n是否确认以上信息？";
	if (!window.confirm(msg)) {
		return false;
	}
	
	window.returnValue = line + "," + interface;
	window.close();
	return false;
}


function submitDelLinkForm() {
	var deleted = delLinkForm.deleted.value;
	if (deleted == null || deleted == "127.0.0.1$127.0.0.1" || deleted == "") {
		return false;
	}
	
	var tmp;
	var line = deleted.split("#");
	var msg = "请确认删除链路信息：\r\n";
	msg += "* 您指定删除的链路有：\r\n";
	for (var i = 0; i < line.length; i++) {
		tmp = line[i].split("$");
		msg += "  - " + tmp[0] + " 与 " + tmp[1] + " 相连的链路\t\r\n";
	}
	msg += "\r\n是否确认以上信息？";
	if (!window.confirm(msg)) {
		return false;
	}

	window.returnValue = deleted;
	window.close();
	return false;
}
