var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#hostArpGrid").ligerGrid({
						columns : [{
									display : '端口描述',
									name : 'ifName',
									minWidth : 200
								}, {
									display : 'IP',
									name : 'ip',
									minWidth : 150,
									width : 180
								}, {
									display : 'MAC',
									name : 'mac',
									minWidth : 150,
									width : 180
								}, {
									display : '扫描时间',
									name : 'cTime',
									minWidth : 100,
									width : 200
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostArpDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'mac',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getHostArpDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostArpDetail",
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
