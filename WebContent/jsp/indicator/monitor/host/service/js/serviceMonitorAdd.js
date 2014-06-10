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
									display : "服务组名",
									name : "serviceGroup",
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
									display : "服务名",
									name : "service",
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
									name : 'nodeId',
									hide : true,
									width : 0.1
								}],
						buttons : [{
									text : '应用',
									width : 60,
									click : f_save
								}, {
									text : '关闭',
									width : 60,
									click : closeWindows
								}]
					});

			// 设置表单初始值
			mainform.setData({
						isM : '1',
						level : '1',
						state : '0'
					});

			// 设置ip选择框
			liger.get('ip_address').set({
						onButtonClick : function() {
							liger.get('service').clear();
							liger.get('service').setText("请选择服务");
							choseHost();
						}

					});

			// 设置进程选择框
			liger.get('service').set({
						onButtonClick : function() {
							choseService();
						}

					});

			// 设置友好提示
			liger.get('ip_address').setText("请选择主机");

		});

// 以下方法定义
function f_save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "serviceMonitorAjaxManager.ajax?action=addServiceMonitor",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					ip_address : liger.get('ip_address').getValue(),
					serviceGroup : liger.get('serviceGroup').getValue(),
					isM : liger.get('isM').getValue(),
					level : liger.get('level').getValue(),
					state : liger.get('state').getValue(),
					nodeId : liger.get('nodeId').getValue(),
					service : liger.get('service').getValue(),
					state : liger.get('state').getValue()
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

function choseHost() {
	$.ligerDialog.open({
				url : '../common/choseHost.jsp',
				height : 480,
				width : 550,
				buttons : [{
							text : '确定',
							onclick : function(item, dialog) {
								// 获取对话框主机列表
								var hostGrid = dialog.frame.grid;
								// 获取列表选中项
								var data = hostGrid.getSelected();
								// 获得父页面ip地址对象
								var ipAddressDom = liger.get('ip_address');
								// 对象赋值
								ipAddressDom.setValue(data.ip);
								// 对象显示文字赋值
								ipAddressDom.setText(data.ip);
								// 赋值隐藏域
								liger.get('nodeId').setValue(data.nodeId);
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

function choseService() {
	var ip = liger.get('ip_address').getValue();
	if (!ip) {
		$.ligerDialog.error("请先选择ip");
		return;
	}
	$.ligerDialog.open({
				url : 'choseService.jsp?ip=' + ip,
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
								var processDom = liger.get('service');
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
			name : '不活动'
		}, {
			value : '1',
			name : '活动'
		}];
