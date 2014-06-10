var grid = null;
var serviceKey = null;
var serviceData = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			serviceData = f_setServiceData();
			serviceKey = $("#serviceKey").ligerTextBox({});
			grid = $("#serviceGrid").ligerGrid({
						columns : [{
									display : '服务名',
									name : 'name',
									align : 'left',
									width : 230
								}, {
									display : '运行状态',
									name : 'state',
									align : 'left'
								}],
						pageSize : 30,
						rownumbers : true,
						allowHideColumn : false,
						where : f_getWhere(),
						data : $.extend(true, {}, serviceData),
						width : '100%',
						height : 380
					});
			$("#serviceKey").keyup(function() {
						f_search();
					});

		});
function f_search() {
	grid.options.data = $.extend(true, {}, serviceData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#serviceKey").val();
		return rowdata.name.indexOf(key) > -1;
	};
	return clause;
}

function f_setServiceData() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "serviceMonitorAjaxManager.ajax?action=getServiceListByIp",
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