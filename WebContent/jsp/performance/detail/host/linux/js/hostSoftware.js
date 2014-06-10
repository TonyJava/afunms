var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#hostSoftwareGrid").ligerGrid({
						columns : [{
									display : '软件ID',
									name : 'softwareId',
									minWidth : 60,
									width : 80
								}, {
									display : '软件名称',
									name : 'softwareName',
									minWidth : 60
								}, {
									display : '软件类型',
									name : 'softwareType',
									minWidth : 60,
									width : 100
								}, {
									display : '安装日期',
									name : 'softwareInstallDate',
									minWidth : 100,
									width : 200
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostSoftwareDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'softwareName',
						width : '99.8%',
						height : '99.9%',
						onReload : function() {
							grid.set({
										data : getHostSoftwareDetail()
									});
						}
					});
		});

function getHostSoftwareDetail() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "hostPerformanceAjaxManager.ajax?action=getHostSoftwareDetail",
		// 鍙傛暟
		data : {
			ip : getUrlParam("ip")
		},
		dataType : "json",
		success : function(array) {
			if (array.Rows.length > 0) {
				rs = array;
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
	return rs;
}
