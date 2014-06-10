var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#hostServiceGrid").ligerGrid({
						columns : [{
									display : '名称',
									name : 'serviceName',
									minWidth : 60,
									width : 200
								}, {
									display : '组',
									name : 'serviceGroup',
									minWidth : 60,
									width : 300
								}, {
									display : '进程ID',
									name : 'processId',
									minWidth : 100,
									width : 300
								}, {
									display : '状态',
									name : 'state',
									minWidth : 100,
									width : 300
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostServiceDetail("aix"),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'serviceName',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getHostServiceDetail(type) {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostServiceDetail",
				// 参数
				data : {
					ip : getUrlParam("ip"),
					type : type
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
