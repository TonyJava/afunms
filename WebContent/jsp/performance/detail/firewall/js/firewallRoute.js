var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#firewallRouteGrid").ligerGrid({
						columns : [{
									display : '端口索引',
									name : 'ipRouteIfIndex',
									minWidth : 200
								}, {
									display : '目标IP',
									name : 'ipRouteDest',
									minWidth : 150,
									width : 180
								}, {
									display : '下一跳',
									name : 'ipRouteNextHop',
									minWidth : 150,
									width : 180
								}, {
									display : '路由类型',
									name : 'ipRouteType',
									minWidth : 100,
									width : 200
								}, {
									display : '路由协议',
									name : 'ipRouteProto',
									minWidth : 100,
									width : 200
								}, {
									display : '子网掩码',
									name : 'ipRouteMask',
									minWidth : 100,
									width : 200
								}, {
									display : '扫描时间',
									name : 'cTime',
									minWidth : 100,
									width : 200
								}],
						pageSize : 22,
						checkbox : true,
						data : getFirewallRouteDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'ipRouteIfIndex',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getFirewallRouteDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "netPerformanceAjaxManager.ajax?action=getNetRouteDetail",
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
