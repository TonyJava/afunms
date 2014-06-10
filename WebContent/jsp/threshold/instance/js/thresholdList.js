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
	grid = $("#thresholdGrid").ligerGrid({
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
					display : '阈值参数',
					columns : [{
								display : '名称',
								name : 'thresHoldName',
								minWidth : 160
							}, {
								display : '类型',
								name : 'thresHoldDataType',
								minWidth : 40,
								render : function(item) {
									if (item.thresHoldDataType == "Number") {
										return "数字";
									} else if (item.thresHoldDataType == "String") {
										return "字符串";
									}
								}
							}, {
								display : '单位',
								name : 'thresHoldUnit',
								minWidth : 10
							}, {
								display : '方式',
								name : 'thresHoldCompareType',
								minWidth : 20
							}, {
								display : '启用',
								name : 'thresHoldIsE',
								minWidth : 10
							}, {
								display : '一级',
								name : 'firstLevelValue',
								minWidth : 30
							}, {
								display : '次数',
								name : 'firstLevelTimes',
								minWidth : 10
							}, {
								display : '启用',
								name : 'firstIsE',
								minWidth : 10
							}, {
								display : '二级',
								name : 'secondLevelValue',
								minWidth : 30
							}, {
								display : '次数',
								name : 'secondLevelTimes',
								minWidth : 10
							}, {
								display : '启用',
								name : 'secondIsE',
								minWidth : 10
							}, {
								display : '三级',
								name : 'thirdLevelValue',
								minWidth : 30
							}, {
								display : '次数',
								name : 'thirdLevelTimes',
								minWidth : 10
							}, {
								display : '启用',
								name : 'thirdIsE',
								minWidth : 10
							}, {
								display : '描述',
								name : 'remark',
								minWidth : 100,
								width : 200,
								render : function(item) {
									if (item.remark.length > 15) {
										return item.remark.substring(0, 15)
												+ "...";
									} else {
										return item.remark;
									}
								}
							}, {
								name : 'thresHoldId',
								hide : true,
								width : 0.1
							}]
				}],
		pageSize : 30,
		checkbox : true,
		data : f_getThresHoldList(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		allowAdjustColWidth : true,
		sortName : 'ip',
		width : '99.8%',
		height : '100%',
		heightDiff :-4,
		onReload : function() {
			grid.set({
						data : f_getThresHoldList()
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
						text : '批量应用阈值',
						click : itemclick,
						icon : 'batch'
					}]
		},
		// 双击
		onDblClickRow : function(data, rowindex, rowobj) {
			openWindows(basePath
							+ "jsp/threshold/instance/edit.jsp?thresHoldId="
							+ data.thresHoldId, 590, 600, "阈值编辑");
		}
	});

});

// 获取数据方法
function f_getThresHoldList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "thresHoldAjaxManager.ajax?action=getThresHoldList",
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
function f_deleteThresHolds(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "thresHoldAjaxManager.ajax?action=deleteThresHolds",
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
			openWindows(basePath + 'jsp/threshold/instance/showAdd.jsp?type='
							+ $("#type").attr("value") + "&subType="
							+ $("#subType").attr("value"), 550, 900, "增加阈值");
		}

	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.thresHoldId + ";";
					});
			$.ligerDialog.success(f_deleteThresHolds(idString));
		}
	} else if (item.text == "查询") {
		refresh();
	}
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getThresHoldList()
			});
}

function itemclick(item, i) {
	refresh();
}