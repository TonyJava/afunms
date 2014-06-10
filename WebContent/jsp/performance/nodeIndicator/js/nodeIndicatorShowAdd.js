var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#nodeIndicatorGrid").ligerGrid({
						columns : [{
									display : '指标名称',
									name : 'indicatorName',
									minWidth : 80
								}, {
									display : '描述',
									name : 'remark',
									minWidth : 100
								}, {
									name : 'indicatorId',
									hide : true,
									width : 0.1
								}],
						pageSize : 30,
						checkbox : true,
						data : f_getIndicatorList(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'ip',
						width : '99.5%',
						height : '99.7%',
						onReload : function() {
							grid.set({
										data : f_getIndicatorList()
									});
						}
					});

		});

// 获取数据方法
function f_getIndicatorList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "indicatorAjaxManager.ajax?action=getIndicatorEscapeList",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					type : getUrlParam("type"),
					subType : getUrlParam("subType"),
					indicatorNameString : $("#indicatorNameString").attr("value")
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
