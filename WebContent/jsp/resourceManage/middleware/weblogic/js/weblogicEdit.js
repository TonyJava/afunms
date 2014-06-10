var basePath = null;
var groupicon = null;
var nodeId = null;
var bidDlg = null;
$(function() {
			basePath = $("#basePath").attr("value");
			nodeId = $("#nodeId").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						inputWidth : 150,
						labelWidth : 60,
						space : 30,
						fields : [{
									name : "nodeId",
									type : "hidden"
								},{
									display : "网元别名",
									name : "alias",
									newline : false,
									type : "text",
									group : "基础信息",
									groupicon : groupicon
								}, {
									display : "IP地址",
									name : "ip",
									newline : false,
									type : "text"
								}, {
									display : "团体名",
									name : "community",
									newline : true,
									type : "text"
								}, {
									display : "端口",
									name : "port",
									newline : false,
									type : "text"
								}, {
									display : "监视 ",
									name : "isM",
									newline : true,
									type : "select",
									validate : {
										required : true
									},
									comboboxName : "isM",
									options : {
										valueField : "value",
										textField : 'name',
										data : isColumns,
										selectBoxHeight : 50
									}
								}, {
									display : "业务",
									name : "bid",
									newline : true,
									type : "popup",
									width : 300,
									group : "所属业务",
									groupicon : groupicon
								}]
					});

			// 设置业务选择框
			liger.get('bid').set({
				onButtonClick : function() {
					bidDlg = openDlgWindow(basePath
									+ 'jsp/common/bidTreeDlg.jsp', 300, 300,
							"业务列表");
				}

			});
			// 设置提示文本
			$('#bid').attr('value', '请选择业务')
			// 初始化默认选择
			$('body').everyTime('100ms', setBidText);
			
			$("#submit").click(function() {
				update();
			});
			
			$("#closeWindow").click(function() {
				closeWindows();
			});

			initData();
		});

function setBidText() {
	liger.get('bid').setText($('#bid').attr('value'));
}

function initData() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "weblogicPerformanceAjaxManager.ajax?action=beforeEditWeblogicNode",
		// 参数
		data : {
			nodeId : nodeId
		},
		dataType : "json",
		success : function(array) {
			liger.get("ip").setDisabled();
			$(array.Rows).each(function() {
						var form = liger.get("form");
						form.setData({
									nodeId : this.nodeId,
									ip : this.ip,
									alias : this.alias,
									isM : this.isM,
									community : this.community,
									port : this.port,
									bid : this.bid
								});
						$("#bidValue").attr("value", this.bidValue);
					});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}
// 以下方法定义
function update() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "weblogicPerformanceAjaxManager.ajax?action=updateWeblogicNode",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					nodeId : $('#nodeId').attr('value'),
					ip : liger.get('ip').getValue(),
					alias : liger.get('alias').getValue(),
					mon_flag : liger.get('isM').getValue(),
					community : liger.get('community').getValue(),
					portnum : liger.get('port').getValue(),
					bid : $('#bidValue').attr('value'),
					bids : liger.get("bid").getValue()
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

function closeDlg() {
	bidDlg.close();
}

var isColumns = [{
			value : '0',
			name : '否'
		}, {
			value : '1',
			name : '是'
		}];
