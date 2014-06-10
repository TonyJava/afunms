var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						inputWidth : 170,
						space : 40,
						fields : [{
									display : "类型 ",
									labelWidth : 60,
									newline : false,
									name : "type",
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "type",
									options : {
										data : getTypeData(),
										valueField : 'type',
										textField : 'tName',
										onSelected : function(value) {
											f_onTypeChanged(value);
										}
									}

								}, {
									display : "子类型 ",
									labelWidth : 60,
									newline : false,
									name : "subType",
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "subType",
									options : {
										valueField : 'subType',
										textField : 'stName',
										onSelected : function(value) {
											f_onSubTypeChanged(value);
										}
									}
								}, {
									display : "网元 ",
									labelWidth : 60,
									newline : false,
									name : "nodeId",
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "nodeId",
									options : {
										valueField : 'nodeId',
										textField : 'alias'
									}
								}]

					});

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
				heightDiff :-5,
				onReload : function() {
					grid.set({
								data : f_getIndicatorList()
							});
				},
				// 工具栏
				toolbar : {
					items : [{
								text : '查询',
								click : tbItemclick,
								icon : 'search'
							}, {
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
							}, {
								text : '批量应用指标',
								click : tbItemclick,
								icon : 'batch'
							}]
				},
				// 双击
				onDblClickRow : function(data, rowindex, rowobj) {
					openWindows(basePath + "jsp/indicator/instance/edit.jsp?indicatorId="
									+ data.indicatorId, 280, 600, "指标编辑");
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
				data : {
					type : $("#type").attr("value"),
					subType : $("#subType").attr("value"),
					nodeId : $("#nodeId").attr("value")
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

// 打开新的窗口方法
function openWindows(href, h, w, t) {
	var win = $.ligerDialog.open({
				title : t,
				height : h,
				url : href,
				width : w,
				slide : false
			});
}

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "增加") {
		if ($("#subType").attr("value").length == 0) {
			$.ligerDialog.error("子类型不能为空");
		} else {
			openWindows(basePath + 'jsp/indicator/instance/showAdd.jsp?type='
							+ $("#type").attr("value") + "&subType=" + $("#subType").attr("value"),
					550, 900, "增加指标");
		}

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
	} else if (item.text == "批量应用指标") {
		if ($("#nodeId").attr("value").length == 0) {
			$.ligerDialog.error("网元不能为空");
		} else {
			openWindows(basePath + 'jsp/indicator/instance/indicatorBatchEdit.jsp?type='
							+ $("#type").attr("value") + "&subType=" + $("#subType").attr("value")
							+ "&nodeId=" + $("#nodeId").attr("value"), 430, 550, "批量应用指标");
		}
	} else if (item.text == "查询") {
		refresh();
	}
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getIndicatorList()
			});
}

function itemclick(item, i) {
	refresh();
}