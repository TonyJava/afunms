var basePath = null;
var groupicon = null;
$(function() {
	basePath = $("#basePath").attr("value");
	groupicon = basePath + "css/icons/communication.gif";
	// 创建表单结构
	var mainform = $("form");
	var formTemp = mainform.ligerForm({
				inputWidth : 170,
				labelWidth : 110,
				labelAlign : "right",
				height : 600,
				space : 40,
				fields : [{
							display : "起点设备",
							name : "startName",
							newline : true,
							width : 250,
							type : "popup"
						}, {
							display : "起点端口索引",
							name : "startIndex",
							newline : true,
							width : 250,
							type : "popup"
						}, {
							display : "终点设备",
							name : "endName",
							newline : true,
							width : 250,
							type : "popup"
						}, {
							display : "终点端口索引",
							name : "endIndex",
							newline : true,
							width : 250,
							type : "popup"
						}, {
							display : "流速阀值(KB/s)",
							name : "maxSpeed",
							newline : true,
							type : "text",
							validate : {
								required : true
							}
						}, {
							display : "宽带利用率(%)",
							name : "maxPer",
							newline : false,
							type : "text",
							validate : {
								required : true
							}
						}, {
							display : "链路显示 ",
							name : "linkText",
							newline : true,
							type : "select",
							comboboxName : "linkText",
							options : {
								valueField : "value",
								textField : 'name',
								data : linkShow,
								selectBoxHeight : 120
							}
						}, {
							display : "接口显示",
							name : "interfShow",
							newline : false,
							type : "select",
							comboboxName : "interfShow",
							options : {
								valueField : "value",
								textField : 'name',
								data : interfShowColumns,
								selectBoxHeight : 50
							}
						}, {
							name : "startId",
							hide : true,
							width : 0.1
						}, {
							name : "endId",
							hide : true,
							width : 0.1
						}, {
							name : "startAlias",
							hide : true,
							width : 0.1
						}, {
							name : "endAlias",
							hide : true,
							width : 0.1
						}],
				buttons : [{
							text : '增加',
							width : 60,
							click : f_save
						}, {
							text : '关闭',
							width : 60,
							click : closeWindows
						}]
			});
	liger.get('startName').set({
				onButtonClick : function() {
					choseHost("startId");
				}

			});
	liger.get('endName').set({
				onButtonClick : function() {
					choseHost("endId");
				}

			});

	liger.get('startIndex').set({
				onButtonClick : function() {
					choseifIndex(liger.get("startId").getValue(), "startIndex");
				}

			});
	liger.get('endIndex').set({
				onButtonClick : function() {
					choseifIndex(liger.get("endId").getValue(), "endIndex");
				}

			});

	formTemp.setData({
				maxSpeed : "2000",
				maxPer : "50",
				interfShow : "0",
				linkText : "-1"
			});

});

function f_save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "thresHoldAjaxManager.ajax?action=addLink",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					startId : liger.get('startId').getValue(),
					endId : liger.get('endId').getValue(),
					startAlias : liger.get('startAlias').getValue(),
					endAlias : liger.get('endAlias').getValue(),
					startName : liger.get('startName').getValue(),
					endName : liger.get('endName').getValue(),
					maxSpeed : liger.get("maxSpeed").getValue(),
					maxPer : liger.get("maxPer").getValue(),
					linkText : liger.get("linkText").getValue(),
					startIndex : liger.get("startIndex").getValue(),
					endIndex : liger.get('endIndex').getValue(),
					interfShow : liger.get('interfShow').getValue()
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

var linkShow = [{
			value : '-1',
			name : '无'
		}, {
			value : '1',
			name : "上行带宽利用率(%)"
		}, {
			value : '2',
			name : "下行带宽利用率(%)"
		}, {
			value : '3',
			name : "上行流速(KB/s)"
		}, {
			value : '4',
			name : '下行流速(KB/s)'
		}];
var interfShowColumns = [{
			value : '0',
			name : '不显示'
		}, {
			value : '1',
			name : "显示"
		}];

var startIdColumns = [{
			value : '0',
			name : '不显示'
		}, {
			value : '1',
			name : "显示"
		}];
var startIndexColumns = [{
			value : '0',
			name : '不显示'
		}, {
			value : '1',
			name : "显示"
		}];
var endIdColumns = [{
			value : '0',
			name : '不显示'
		}, {
			value : '1',
			name : "显示"
		}];
var endIndexColumns = [{
			value : '0',
			name : '不显示'
		}, {
			value : '1',
			name : "显示"
		}];
var testData = [{
			startIndex : '0tttt',
			indexName : '不显示vfggf'
		}];

function getIfentityList() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "thresHoldAjaxManager.ajax?action=getLinkIfentityList",
		// 参数
		data : {
			linkId : getUrlParam("linkId")
		},
		dataType : "json",
		success : function(array) {
			liger.get("startName").setDisabled();
			liger.get("endName").setDisabled();
			$(array.Rows).each(function() {
						var form = liger.get("form");
						form.setData({
									linkId : this.linkId,
									startId : this.startId,
									endId : this.endId,
									indexStart : this.indexStart,
									indexEnd : this.indexEnd,
									linkName : this.linkName,
									maxSpeed : this.maxSpeed,
									maxPer : this.maxPer,
									linkText : this.lineText,
									startName : this.startName,
									startIndex : this.startIndex,
									endName : this.endName,
									endIndex : this.endIndex,
									interfShow : this.interfShow
								});
						liger.get('startIndex').setText(this.startIndex);
						liger.get('endIndex').setText(this.endIndex);
					});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function choseifIndex(string, flag) {
	if (!string) {
		$.ligerDialog.error("获取失败");
		return;
	}
	$.ligerDialog.open({
				url : 'choseifIndex.jsp?id=' + string + "&flag=" + flag,
				height : 400,
				width : 550,
				buttons : [{
					text : '确定',
					onclick : function(item, dialog) {
						// 获取对话框主机列表
						var processGrid = dialog.frame.grid;
						// 获取列表选中项
						var data = processGrid.getSelected();
						liger.get($(dialog.frame.flag).attr("value"))
								.setValue(data.ifIndex);
						liger.get($(dialog.frame.flag).attr("value"))
								.setText(data.ifIndex + "(" + data.ifDescr
										+ ")");
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

function choseHost(flag) {
	$.ligerDialog.open({
		url : 'common/choseHost.jsp?flag=' + flag,
		height : 400,
		width : 550,
		buttons : [{
			text : '确定',
			onclick : function(item, dialog) {
				// 获取对话框主机列表
				var hostGrid = dialog.frame.grid;
				// 获取列表选中项
				var data = hostGrid.getSelected();
				// 获得父页面ip地址对象
				if ($(dialog.frame.flag).attr("value").indexOf("startId") > -1) {
					liger.get("startName").setValue(data.ip);
					liger.get("startName").setText(data.alias + "(" + data.ip
							+ ")");
					liger.get("startId").setValue(data.nodeId);
					liger.get("startAlias").setValue(data.alias);
				} else if ($(dialog.frame.flag).attr("value").indexOf("endId") > -1) {
					liger.get("endName").setValue(data.ip);
					liger.get("endName").setText(data.alias + "(" + data.ip
							+ ")");
					liger.get("endId").setValue(data.nodeId);
					liger.get("endAlias").setValue(data.alias);
				}
				// 关闭对话框
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