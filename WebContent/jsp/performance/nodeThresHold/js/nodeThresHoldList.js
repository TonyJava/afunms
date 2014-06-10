var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#nodeThresholdGrid").ligerGrid({
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
												return item.remark.substring(0, 15) + "...";
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
				heightDiff :-5,
				onReload : function() {
					grid.set({
								data : f_getThresHoldList()
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
							}]
				},
				// 双击
				onDblClickRow : function(data, rowindex, rowobj) {
					openDlgWindow(
							basePath
									+ "jsp/performance/nodeThresHold/nodeThresHoldEdit.jsp?thresHoldId="
									+ data.thresHoldId, 620, 650, "阈值编辑");
				}
			});

		});

// 获取数据方法
function f_getThresHoldList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "thresHoldAjaxManager.ajax?action=getThresHoldList",
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
function f_deleteThresHolds(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "thresHoldAjaxManager.ajax?action=deleteThresHolds",
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
		var thresHoldNameString = "";
		var rows = grid.rows;
		$(rows).each(function() {
					thresHoldNameString += this.thresHoldName + ";";
				});
		addEscapeThesHold(basePath
				+ 'jsp/performance/nodeThresHold/nodeThresHoldShowAdd.jsp?type='
				+ getUrlParam("type") + "&subType=" + getUrlParam("subType")
				+ "&thresHoldNameString=" + thresHoldNameString);
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
	}
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getThresHoldList()
			});
}

function addEscapeThesHold(url) {
	$.ligerDialog.open({
				url : url,
				height : 450,
				width : 550,
				title : "网元阈值添加",
				buttons : [{
							text : '确定',
							onclick : function(item, dialog) {
								var thresHoldGrid = dialog.frame.grid;
								// 获取列表选中项
								var rows = thresHoldGrid.getSelectedRows();
								var thresHoldIdString = "";
								for (var i = 0; i < rows.length; i++) {
									thresHoldIdString += rows[i].thresHoldId + ";";
								}
								addNodeThresHold(thresHoldIdString);
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

function addNodeThresHold(string) {
	$.ajax({
				type : "POST",
				async : true,
				url : basePath + "thresHoldAjaxManager.ajax?action=addNodeThresHold",
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
