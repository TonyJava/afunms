var basePath = null;
var line = null;
var type = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			type = $("#type").attr("value");
			line = $("#line").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			initData();
			f_getLinkLinePic(type);
		});

function initData() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "linkPerformanceAjaxManager.ajax?action=getLinkLine",
		// 参数
		data : {
			line : line
		},
		dataType : "json",
		success : function(array) {
			$(array.Rows).each(function() {
				$("#startAlias").text(array.Rows[0].startAlias);
				$("#endAlias").text(array.Rows[0].endAlias);
				$("#startIp").text(array.Rows[0].startIp);
				$("#endIp").text(array.Rows[0].endIp);
				$("#startLinkIp").text(array.Rows[0].startLinkIp);
				$("#endLinkIp").text(array.Rows[0].endLinkIp);
				$("#startIndex").text(array.Rows[0].startIndex);
				$("#endIndex").text(array.Rows[0].endIndex);
				$("#startDescr").text(array.Rows[0].startDescr);
				$("#endDescr").text(array.Rows[0].endDescr);
			});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function closeWindows() {
	var dialog = frameElement.dialog;
	dialog.close();
}

function f_getLinkLinePic(type) {
	var so = new SWFObject(basePath + "flex/" + type + ".swf?linkid=" +line, type, "800", "250", "8", "#ffffff");
	so.write("linePic");
}