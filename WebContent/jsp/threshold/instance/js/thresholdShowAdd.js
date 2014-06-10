var basePath = null;
var submitManager = null;
var closeManager = null;
$(function() {
			basePath = $("#basePath").attr("value");

			submitManager = $("#submit").ligerButton({
						text : '提交',
						click : function() {
							f_saveAddThresHoldsAndNodes();
						}
					});

			closeManager = $("#close").ligerButton({
						text : '关闭',
						click : function() {
							var dialog = frameElement.dialog;
							dialog.close();
						}
					});

			$("#hasAddThresHolds").ligerListBox({
						valueField : 'thresHoldId',
						columns : toAddThresHoldsColumns,
						isShowCheckBox : true,
						isMultiSelect : true,
						valueFieldID : 'toAddThresHoldsIds',// 将被添加到数据库的重要标识
						width : 200,
						height : 135
					});

			$("#waitAddThresHolds").ligerListBox({
						valueField : 'thresHoldId',
						columns : waitAddThresHoldsColumns,
						isMultiSelect : true,
						isShowCheckBox : true,
						valueFieldID : 'waitAddThresHoldsIds',
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
			f_getCanAddThresHoldsAndNodes();
		});

// ThresHold部分
function moveThresHoldsToLeft() {
	var waitAddThresHoldsBox = liger.get("waitAddThresHolds"), hasAddThresHoldsBox = liger
			.get("hasAddThresHolds");
	var selecteds = hasAddThresHoldsBox.getSelectedItems();
	if (!selecteds || !selecteds.length)
		return;
	hasAddThresHoldsBox.removeItems(selecteds);
	waitAddThresHoldsBox.addItems(selecteds);
}
function moveThresHoldsToRight() {
	var waitAddThresHoldsBox = liger.get("waitAddThresHolds"), hasAddThresHoldsBox = liger
			.get("hasAddThresHolds");
	var selecteds = waitAddThresHoldsBox.getSelectedItems();
	if (!selecteds || !selecteds.length)
		return;
	waitAddThresHoldsBox.removeItems(selecteds);
	hasAddThresHoldsBox.addItems(selecteds);

}
function moveAllThresHoldsToLeft() {
	var waitAddThresHoldsBox = liger.get("waitAddThresHolds"), hasAddThresHoldsBox = liger
			.get("hasAddThresHolds");
	var selecteds = hasAddThresHoldsBox.data;
	if (!selecteds || !selecteds.length)
		return;
	waitAddThresHoldsBox.addItems(selecteds);
	hasAddThresHoldsBox.removeItems(selecteds);
}
function moveAllThresHoldsToRight() {
	var waitAddThresHoldsBox = liger.get("waitAddThresHolds"), hasAddThresHoldsBox = liger
			.get("hasAddThresHolds");
	var selecteds = waitAddThresHoldsBox.data;
	if (!selecteds || !selecteds.length)
		return;
	hasAddThresHoldsBox.addItems(selecteds);
	waitAddThresHoldsBox.removeItems(selecteds);
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

function f_getCanAddThresHoldsAndNodes() {
	$.ajax({
		type : "POST",
		url : basePath
				+ "thresHoldAjaxManager.ajax?action=getCanAddThresHoldsAndNodes",
		// 参数
		data : {
			type : getUrlParam("type"),
			subType : getUrlParam("subType")
		},
		dataType : "json",
		success : function(array) {
			liger.get("waitAddThresHolds").setData(array.ThresHoldRows);
			liger.get("waitAddNodes").setData(array.NodeRows);

		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function f_saveAddThresHoldsAndNodes() {
	liger.get("hasAddThresHolds").selectAll();
	liger.get("hasAddNodes").selectAll()
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "thresHoldAjaxManager.ajax?action=saveAddThresHoldsAndNodes",
				// 参数
				data : {
					toAddThresHoldsIds : $("#toAddThresHoldsIds").val(),
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

var waitAddThresHoldsColumns = [{
			header : '阈值名称',
			name : 'thresHoldName'
		}, {
			header : '子类型',
			name : 'subType'
		}, {
			header : '描述',
			name : 'remark'
		}];

var toAddThresHoldsColumns = [{
			header : '所选阈值',
			name : 'thresHoldName'
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