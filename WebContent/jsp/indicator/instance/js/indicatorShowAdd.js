var basePath = null;
var submitManager = null;
var closeManager = null;
$(function() {
			basePath = $("#basePath").attr("value");

			submitManager = $("#submit").ligerButton({
						text : '提交',
						click : function() {
							f_saveAddIndicatorsAndNodes();
						}
					});

			closeManager = $("#close").ligerButton({
						text : '关闭',
						click : function() {
							var dialog = frameElement.dialog;
							dialog.close();
						}
					});

			$("#hasAddIndicators").ligerListBox({
						valueField : 'indicatorId',
						columns : toAddIndicatorsColumns,
						isShowCheckBox : true,
						isMultiSelect : true,
						valueFieldID : 'toAddIndicatorsIds',// 将被添加到数据库的重要标识
						width : 200,
						height : 135
					});

			$("#waitAddIndicators").ligerListBox({
						valueField : 'indicatorId',
						columns : waitAddIndicatorsColumns,
						isMultiSelect : true,
						isShowCheckBox : true,
						valueFieldID : 'waitAddIndicatorsIds',
						width : 400,
						height : 135
					});

			$("#hasAddNodes").ligerListBox({
						valueField : 'nodeId',
						columns : nodesColumns,
						isShowCheckBox : true,
						isMultiSelect : true,
						valueFieldID : 'toAddNodesValues',
						width : 400,
						height : 200
					});

			$("#waitAddNodes").ligerListBox({
						valueField : 'nodeId',
						columns : nodesColumns,
						isMultiSelect : true,
						isShowCheckBox : true,
						valueFieldID : 'waitAddNodesValues',
						width : 400,
						height : 200
					});
			f_getCanAddIndicatorsAndNodes();
		});

// Indicator部分
function moveIndicatorsToLeft() {
	var waitAddIndicatorsBox = liger.get("waitAddIndicators"), hasAddIndicatorsBox = liger
			.get("hasAddIndicators");
	var selecteds = hasAddIndicatorsBox.getSelectedItems();
	if (!selecteds || !selecteds.length)
		return;
	hasAddIndicatorsBox.removeItems(selecteds);
	waitAddIndicatorsBox.addItems(selecteds);
}
function moveIndicatorsToRight() {
	var waitAddIndicatorsBox = liger.get("waitAddIndicators"), hasAddIndicatorsBox = liger
			.get("hasAddIndicators");
	var selecteds = waitAddIndicatorsBox.getSelectedItems();
	if (!selecteds || !selecteds.length)
		return;
	waitAddIndicatorsBox.removeItems(selecteds);
	hasAddIndicatorsBox.addItems(selecteds);

}
function moveAllIndicatorsToLeft() {
	var waitAddIndicatorsBox = liger.get("waitAddIndicators"), hasAddIndicatorsBox = liger
			.get("hasAddIndicators");
	var selecteds = hasAddIndicatorsBox.data;
	if (!selecteds || !selecteds.length)
		return;
	waitAddIndicatorsBox.addItems(selecteds);
	hasAddIndicatorsBox.removeItems(selecteds);
}
function moveAllIndicatorsToRight() {
	var waitAddIndicatorsBox = liger.get("waitAddIndicators"), hasAddIndicatorsBox = liger
			.get("hasAddIndicators");
	var selecteds = waitAddIndicatorsBox.data;
	if (!selecteds || !selecteds.length)
		return;
	hasAddIndicatorsBox.addItems(selecteds);
	waitAddIndicatorsBox.removeItems(selecteds);
}

// Node部分
function moveNodesToLeft() {
	var waitAddNodesBox = liger.get("waitAddNodes"), hasAddNodesBox = liger
			.get("hasAddNodes");
	var selecteds = hasAddNodesBox.getSelectedItems();
	if (!selecteds || !selecteds.length)
		return;
	hasAddNodesBox.removeItems(selecteds);
	waitAddNodesBox.addItems(selecteds);
}
function moveNodesToRight() {
	var waitAddNodesBox = liger.get("waitAddNodes"), hasAddNodesBox = liger
			.get("hasAddNodes");
	var selecteds = waitAddNodesBox.getSelectedItems();
	if (!selecteds || !selecteds.length)
		return;
	waitAddNodesBox.removeItems(selecteds);
	hasAddNodesBox.addItems(selecteds);

}
function moveAllNodesToLeft() {
	var waitAddNodesBox = liger.get("waitAddNodes"), hasAddNodesBox = liger
			.get("hasAddNodes");
	var selecteds = hasAddNodesBox.data;
	if (!selecteds || !selecteds.length)
		return;
	waitAddNodesBox.addItems(selecteds);
	hasAddNodesBox.removeItems(selecteds);
}
function moveAllNodesToRight() {
	var waitAddNodesBox = liger.get("waitAddNodes"), hasAddNodesBox = liger
			.get("hasAddNodes");
	var selecteds = waitAddNodesBox.data;
	if (!selecteds || !selecteds.length)
		return;
	hasAddNodesBox.addItems(selecteds);
	waitAddNodesBox.removeItems(selecteds);
}

function f_getCanAddIndicatorsAndNodes() {
	$.ajax({
		type : "POST",
		url : basePath
				+ "indicatorAjaxManager.ajax?action=getCanAddIndicatorsAndNodes",
		// 参数
		data : {
			type : getUrlParam("type"),
			subType : getUrlParam("subType")
		},
		dataType : "json",
		success : function(array) {
			liger.get("waitAddIndicators").setData(array.IndicatorRows);
			liger.get("waitAddNodes").setData(array.NodeRows);

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function f_saveAddIndicatorsAndNodes() {
	liger.get("hasAddIndicators").selectAll();
	liger.get("hasAddNodes").selectAll()
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "indicatorAjaxManager.ajax?action=saveAddIndicatorsAndNodes",
				// 参数
				data : {
					toAddIndicatorsIds : $("#toAddIndicatorsIds").val(),
					toAddNodesValues : $("#toAddNodesValues").val(),
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