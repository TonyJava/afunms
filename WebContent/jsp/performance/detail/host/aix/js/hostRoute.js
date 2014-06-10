var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#hostRouteGrid").ligerGrid({
						columns : [{
									display : '路由信息',
									name : 'routeMessage',
									minWidth : 600
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostRouteDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						width : '99.8%',
						height : '99.9%'

					});
		});

function getHostRouteDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostAixRouteDetail",
				// 参数
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
