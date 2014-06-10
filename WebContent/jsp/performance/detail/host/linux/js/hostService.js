var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#hostServiceGrid").ligerGrid({
						columns : [{
									display : '服务名称',
									name : 'serviceName',
									minWidth : 500,
									width : 550
								}, {
									display : '当前状态',
									name : 'operatingState',
									minWidth : 500,
									width : 550
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostServiceDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'serviceName',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getHostServiceDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostServiceDetail",
				// 参数
				data : {
					ip : getUrlParam("ip"),
					type : "linux"
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
