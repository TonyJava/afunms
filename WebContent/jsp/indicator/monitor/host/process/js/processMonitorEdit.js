var basePath = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			var mainform = $("form").ligerForm({
						inputWidth : 170,
						labelWidth : 90,
						space : 40,
						fields : [{
									display : "网元IP",
									name : "ip_address",
									newline : true,
									type : "popup",
									group : "基础信息",
									groupicon : groupicon
								}, {
									display : "进程组名",
									name : "processGroup",
									newline : false,
									type : "text"
								}, {
									display : "监视 ",
									name : "isM",
									newline : true,
									type : "select",
									comboboxName : "isM",
									options : {
										valueField : "value",
										textField : 'name',
										data : isMColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "告警等级 ",
									name : "level",
									newline : false,
									type : "select",
									comboboxName : "level",
									options : {
										valueField : "value",
										textField : 'name',
										data : levelColums,
										selectBoxHeight : 70
									}
								}, {
									display : "进程名",
									name : "process",
									newline : true,
									type : "popup",
									group : "详细信息",
									groupicon : groupicon
								}, {
									display : "状态 ",
									name : "state",
									newline : true,
									type : "select",
									comboboxName : "state",
									options : {
										valueField : "value",
										textField : 'name',
										data : stateColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "进程数",
									name : "processNumber",
									newline : false,
									type : "text"
								}, {
									name : 'nodeId',
									hide : true,
									width : 0.1
								}, {
									name : 'processGroupId',
									hide : true,
									width : 0.1
								}],
						buttons : [{
									text : '修改',
									width : 60,
									click : f_save
								}, {
									text : '关闭',
									width : 60,
									click : closeWindows
								}]
					});

			// 设置进程选择框
			liger.get('process').set({
						onButtonClick : function() {
							choseProcess();
						}

					});


			initData();
		});

// 以下方法定义

function initData() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "processMonitorAjaxManager.ajax?action=beforeEditProcessMonitor",
		// 参数
		data : {
			groupId : getUrlParam("groupId")
		},
		dataType : "json",
		success : function(array) {
			liger.get("ip_address").setDisabled();
			$(array.Rows).each(function() {
						var form = liger.get("form");
						form.setData({
									processGroupId : this.processGroupId,
									processGroup : this.processGroup,
									isM : this.isM,
									level : this.level,
									state : this.state,
									processNumber : this.processNumber,
									nodeId : this.nodeId
								});
						// popu需要单独赋值
						liger.get("ip_address").setValue(this.ip_address);
						liger.get("ip_address").setText(this.ip_address);
						liger.get("process").setValue(this.process);
						liger.get("process").setText(this.process);
					});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function f_save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "processMonitorAjaxManager.ajax?action=editProcessMonitor",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					ip_address : liger.get('ip_address').getValue(),
					processGroupId : liger.get('processGroupId').getValue(),
					processGroup : liger.get('processGroup').getValue(),
					isM : liger.get('isM').getValue(),
					level : liger.get('level').getValue(),
					state : liger.get('state').getValue(),
					nodeId : liger.get('nodeId').getValue(),
					process : liger.get('process').getValue(),
					state : liger.get('state').getValue(),
					processNumber : liger.get('processNumber').getValue()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								opener.refresh();
								closeWindows();

							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}
function closeWindows() {
	window.close();
}

function choseProcess() {
	var ip = liger.get('ip_address').getValue()
	if (!ip) {
		$.ligerDialog.error("请先选择ip");
		return;
	}
	$.ligerDialog.open({
				url : 'choseProcess.jsp?ip=' + ip,
				height : 480,
				width : 550,
				buttons : [{
							text : '确定',
							onclick : function(item, dialog) {
								// 获取对话框主机列表
								var processGrid = dialog.frame.grid;
								// 获取列表选中项
								var data = processGrid.getSelected();
								// 获得父页面ip地址对象
								var processDom = liger.get('process');
								// 对象赋值
								processDom.setValue(data.name);
								// 对象显示文字赋值
								processDom.setText(data.name);
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

// 数据结构定义
var isMColumns = [{
			value : '0',
			name : '否'
		}, {
			value : '1',
			name : '是'
		}];

var levelColums = [{
			value : "1",
			name : '普通告警'
		}, {
			value : "2",
			name : '严重告警'
		}, {
			value : "3",
			name : '紧急告警'
		}];

var stateColumns = [{
			value : '0',
			name : '白名单'
		}, {
			value : '1',
			name : '黑名单'
		}];
