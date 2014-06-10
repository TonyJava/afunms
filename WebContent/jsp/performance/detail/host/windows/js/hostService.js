var grid = null;
var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");
	// grid
	grid = $("#hostServiceGrid").ligerGrid({
		columns : [ {
			display : '服务名称',
			name : 'serviceName',
			minWidth : 60
		}, {
			display : '当前状态',
			name : 'operatingState',
			minWidth : 60,
			width : 60
		}, {
			display : '安装状态',
			name : 'installedState',
			minWidth : 100,
			width : 150
		}, {
			display : '是否可卸载',
			name : 'canBeUninstalled',
			minWidth : 100,
			width : 150
		}, {
			display : '是否可停止',
			name : 'canBePaused',
			minWidth : 100,
			width : 150
		} ],
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
	$
			.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostServiceDetail",
				// 参数
				data : {
					ip : getUrlParam("ip"),
					type : "windows"
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
