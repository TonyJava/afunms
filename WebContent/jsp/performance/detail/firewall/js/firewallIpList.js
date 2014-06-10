var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#firewallIpListGrid").ligerGrid({
						columns : [{
									display : '端口索引',
									name : 'ifIndex',
									minWidth : 200
								}, {
									display : '端口描述',
									name : 'ifDescr',
									minWidth : 150,
									width : 180
								}, {
									display : '每秒字节数',
									name : 'ifSpeed',
									minWidth : 150,
									width : 180
								}, {
									display : 'ip',
									name : 'ipAdEntAddr',
									minWidth : 100,
									width : 200
								}, {
									display : '类型',
									name : 'ifType',
									minWidth : 100,
									width : 200
								}],
						pageSize : 22,
						checkbox : true,
						data : getFirewallIpListDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'ifIndex',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getFirewallIpListDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "netPerformanceAjaxManager.ajax?action=getNetIpListDetail",
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
