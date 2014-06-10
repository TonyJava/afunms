var basePath = null;
var grid = null;
var linkData = null;
var key = null;
$(function() {
			basePath = $("#basePath").attr("value");
			linkData = f_getLinkThresHoldList();
			key = $("#Key").ligerTextBox({});
			// grid
			grid = $("#linkThresHoldGrid").ligerGrid({
				columns : [{
							display : 'id',
							name : 'linkId',
							minWidth : 50,
							width : 60
						},{
							display : '起点设备',
							name : 'startAlias',
							minWidth : 100
						}, {
							display : '起点端口',
							name : 'startPort',
							minWidth : 100
						}, {
							display : '终点设备',
							name : 'endAlias',
							minWidth : 80
						}, {
							display : '终点端口',
							name : 'endPort',
							minWidth : 80
						}, {
							display : '流量阀值(KB/s)',
							name : 'maxSpeed',
							minWidth : 80,
							width : 100
						}, {
							display : '带宽利用率(%)',
							name : 'maxPer',
							minWidth : 80,
							width : 100
						}],
				pageSize : 30,
				checkbox : true,
				data : $.extend(true, {}, linkData),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'startAlias',
				width : '99.8%',
				height : '100%',
				heightDiff :-4,
				onReload : function() {
					refresh();
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
							}, {
								text : '批量应用阈值',
								click : itemclick,
								icon : 'batch'
							}]
				},
				// 双击
				onDblClickRow : function(data, rowindex, rowobj) {
					openWindows(
							basePath
									+ "jsp/threshold/link/edit.jsp?linkId="
									+ data.linkId, 480, 650, "指标编辑");
				}
			});
			$("#Key").keyup(function() {
				f_search();

			});
		});

function f_search() {
	grid.options.data = $.extend(true, {}, linkData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#Key").val();
		if(rowdata.startAlias.indexOf(key) > -1 || rowdata.startPort.indexOf(key) > -1 || rowdata.endAlias.indexOf(key) > -1 || rowdata.endPort.indexOf(key) > -1){
			return true;
		}
	};
	return clause;
}

// 获取数据方法
function f_getLinkThresHoldList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "thresHoldAjaxManager.ajax?action=getLinkThresHoldList",
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
						+ "thresHoldAjaxManager.ajax?action=deleteLinkThresHolds",
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
			openWindows(basePath + 'jsp/threshold/link/linkAdd.jsp?', 500, 680, "增加阈值");
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.linkId + ";";
					});
			$.ligerDialog.success(f_deleteThresHolds(idString));
		}
	}
}

//刷新列表
function refresh() {
	linkData = f_getLinkThresHoldList();
	grid.set({
				data : $.extend(true, {}, linkData)
			});
	f_search();
}


function itemclick() {
	var rows = grid.getSelectedRows();
	var idString = "";
	if (rows.length == 0) {
		$.ligerDialog.error("请选择要批量修改的链路");
	} else {
		$(rows).each(function() {
			idString += this.linkId + ";";
		});
		openWindows(basePath + 'jsp/threshold/link/linkEditAll.jsp?linkIdString='
				+ idString, 250, 550, "批量修改链路阈值");
	}
}

