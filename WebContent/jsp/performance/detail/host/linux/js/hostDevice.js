var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#hostDeviceGrid").ligerGrid({
						columns : [{
									display : '设备ID',
									name : 'deviceId',
									minWidth : 60,
									width : 80
								}, {
									display : '设备名称',
									name : 'deviceName',
									minWidth : 60
								}, {
									display : '设备类型',
									name : 'deviceType',
									minWidth : 60,
									width : 100
								}, {
									display : '状态',
									name : 'deviceState',
									minWidth : 100,
									width : 200
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostDeviceDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'deviceName',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getHostDeviceDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostDeviceDetail",
				// 鍙傛暟
				data : {
					ip : getUrlParam("ip")
				},
				dataType : "json",
				success : function(array) {
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}
