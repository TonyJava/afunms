var basePath = null;
var grid = null;
var hostData = null;
var submitManager = null;
var closeManager = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ipKey = $("#ipKey").ligerTextBox({});
			hostData = f_getBatchIndicatorsNodes();
			submitManager = $("#submit").ligerButton({
						text : '设置',
						click : function() {
							var rows = grid.getSelectedRows();
							var idString = "";
							if (rows.length == 0) {
								$.ligerDialog.error("请选择设置设备");
							} else {
								$(rows).each(function() {
											idString += this.nodeId + ";";
										});
								f_saveBatchIndicatorsNodes(idString);
							}
						}
					});
			
			grid = $("#hostGrid").ligerGrid({
				columns : [ {
							name : 'nodeId',
							hide : true,
							width : 0.1
						}, {
							display : '网元IP',
							name : 'nodeIp',
							width : 150
						},{
							display : '网元别名',
							name : 'nodeAlias',
							width : 150
						}, {
							display : '所属业务',
							name : 'nodeBSname',
							width : 150
						}],
				pageSize : 30,
				checkbox : true,
				rownumbers : true,
				allowHideColumn : false,
				where : f_getBatchIndicatorsNodes(),
				data : $.extend(true, {}, hostData),
				width : '100%',
				height : '80%',
				onReload : function() {
					hostData = f_getBatchIndicatorsNodes();
					grid.set({
								data : $.extend(true, {}, hostData)
							});
				}
			});
			
			$("#ipKey").keyup(function() {
				f_search();

			});

			closeManager = $("#close").ligerButton({
						text : '关闭',
						click : function() {
							var dialog = frameElement.dialog;
							dialog.close();
						}
					});
		});


function f_search() {
	grid.options.data = $.extend(true, {}, hostData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#ipKey").val();
		return rowdata.nodeIp.indexOf(key) > -1;
	};
	return clause;
}

function f_getBatchIndicatorsNodes() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "indicatorAjaxManager.ajax?action=getBatchIndicatorsNodes",
		// 参数
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

function f_saveBatchIndicatorsNodes(idString) {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "indicatorAjaxManager.ajax?action=saveBatchIndicatorNodes",
				// 参数
				data : {
					nodeId : getUrlParam("nodeId"),
					nodeids : idString,
					type : getUrlParam("type"),
					subType : getUrlParam("subType")
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								window.parent.refresh();
								closeWindows();

							});

				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}
function closeWindows() {
	var dialog = frameElement.dialog;
	dialog.close();
}

var waitAddIndicatorsColumns = [{
			header : '指标名称',
			name : 'indicatorName'
		}, {
			header : '子类型',
			name : 'subType'
		}, {
			header : '描述',
			name : 'remark'
		}];

var toAddIndicatorsColumns = [{
			header : '所选指标',
			name : 'indicatorName'
		}];

var nodesColumns = [{
			header : '别名',
			name : 'nodeAlias'
		}, {
			header : 'IP地址',
			name : 'nodeIp',
			width : 110
		}, {
			header : '业务',
			name : 'nodeBSname'
		}];