var grid = null;
var basePath = null;
var id = null;
$(function() {
	basePath = $("#basePath").attr("value");
	id = $("#id").attr("value");
	$("#systemDiv").ligerPanel({
		title : '基础信息',
		width : 550,
		height : 405
	});
	$("#pingLinePanel").ligerPanel({
		title : '连通率',
		width : 500,
		height : 400
	});
	$('#systemTable tbody tr:odd').css("backgroundColor", "#F0F0F0");

	f_getPingLine();
	getSqlserverConfig();

});

function getSqlserverConfig() {
	$.ajax({
				type : "POST",
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getSqlserverConfig",
				// 参数
				data : {
					id : id
				},
				dataType : "json",
				success : function(array) {
					if (array.Rows.length > 0) {
						$("#nodeIp").text(array.Rows[0].nodeIp);
						$("#nodeAlias").text(array.Rows[0].nodeAlias);
						$("#isM").text(array.Rows[0].isM);
						$("#status").text(array.Rows[0].status);
						$("#hostName").text(array.Rows[0].hostName);
						$("#version").text(array.Rows[0].version);
						$("#servicePackage").text(array.Rows[0].servicePackage);
						$("#processId").text(array.Rows[0].processId);
						$("#userMode").text(array.Rows[0].userMode);
						$("#securityMode").text(array.Rows[0].securityMode);
						$("#integratedInstance").text(
								array.Rows[0].integratedInstance);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function f_getPingLine() {
	var so = new SWFObject(basePath + "flex/Oracle_Ping.swf?ipadress=" + id
			+ "&category=SQLPing", "Oracle_Ping", "500", "350", "8", "#ffffff");
	so.write("pingLine");
}
