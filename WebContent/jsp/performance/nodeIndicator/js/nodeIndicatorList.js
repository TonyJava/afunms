var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");

			// grid
			grid = $("#indicatorGrid").ligerGrid({
				columns : [{
							display : '网元名称',
							name : 'alias',
							align : 'left',
							minWidth : 200
						}, {
							display : '网元IP',
							name : 'ip',
							minWidth : 100
						}, {
							display : '类型',
							name : 'type',
							minWidth : 80,
							width : 100
						}, {
							display : '子类型',
							name : 'subType',
							minWidth : 80,
							width : 100
						}, {
							display : '指标名称',
							name : 'indicatorName',
							minWidth : 80
						}, {
							display : '采集',
							name : 'isC',
							minWidth : 60,
							width : 80
						}, {
							display : '间隔',
							name : 'interval',
							minWidth : 60,
							width : 80
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
				width : '99.8%',
				height : '100%',
				heightDiff :-4,
				onReload : function() {
					grid.set({
								data : f_getIndicatorList()
							});
				},
				// 工具栏
				toolbar : {
					items : [{
								text : '增加',
								click : tbItemclick,
								icon : 'add'
							}, {
								line : true
							}, {
								text : '删除',
								click : tbItemclick,
								icon : 'delete'
							}, {
								line : true
							}]
				},
				// 双击
				onDblClickRow : function(data, rowindex, rowobj) {
					openDlgWindow(
							basePath
									+ "jsp/performance/nodeIndicator/nodeIndicatorEdit.jsp?indicatorId="
									+ data.indicatorId, 280, 650, "指标编辑");
				}
			});

		});

// 获取数据方法
function f_getIndicatorList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "indicatorAjaxManager.ajax?action=getIndicatorList",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					type : getUrlParam("type"),
					subType : getUrlParam("subType"),
					nodeId : getUrlParam("nodeId")
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

// 删除指标方法
function f_deleteIndicators(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "indicatorAjaxManager.ajax?action=deleteIndicators",
				// 参数
				data : {
					string : string
				},
				dataType : "text",
				success : function(array) {
					// 成功删除则更新表格行
					grid.deleteSelectedRow();
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "增加") {
		var indicatorNameString = "";
		var rows = grid.rows;
		$(rows).each(function() {
					indicatorNameString += this.indicatorName + ";";
				});
		addEscapeIndicator(basePath
				+ 'jsp/performance/nodeIndicator/nodeIndicatorShowAdd.jsp?type='
				+ getUrlParam("type") + "&subType=" + getUrlParam("subType")
				+ "&indicatorNameString=" + indicatorNameString);
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.indicatorId + ";";
					});
			$.ligerDialog.success(f_deleteIndicators(idString));
		}
	}
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getIndicatorList()
			});
}

function addEscapeIndicator(url) {
	$.ligerDialog.open({
				url : url,
				height : 450,
				width : 550,
				title : "采集指标添加",
				buttons : [{
							text : '确定',
							onclick : function(item, dialog) {
								var indicatorGrid = dialog.frame.grid;
								// 获取列表选中项
								var rows = indicatorGrid.getSelectedRows();
								var indicatorIdString = "";
								for (var i = 0; i < rows.length; i++) {
									indicatorIdString += rows[i].indicatorId + ";";
								}
								addNodeIndicator(indicatorIdString);
								dialog.close();
							},
							cls : 'l-dialog-btn-highlight'
						}, {
							text : '取消',
							onclick : function(item, dialog) {
								dialog.close();
							}
						}],
				isResize : true
			});
}

function addNodeIndicator(string) {
	$.ajax({
				type : "POST",
				async : true,
				url : basePath + "indicatorAjaxManager.ajax?action=addNodeIndicator",
				// 参数
				data : {
					string : string,
					nodeId : getUrlParam("nodeId")
				},
				dataType : "text",
				success : function(array) {
					refresh();
					$.ligerDialog.success(array);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}