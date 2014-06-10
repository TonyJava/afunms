var basePath = null;
var groupicon = null;
var bidDlg = null;
$(function() {
	basePath = $("#basePath").attr("value");
	groupicon = basePath + "css/icons/communication.gif";
	// 创建表单结构
	var mainform = $("form");
	mainform.ligerForm({
				inputWidth : 150,
				labelWidth : 60,
				space : 30,
				fields : [{
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
						}],
				buttons : [{
							text : '添加',
							width : 60,
							click : f_save
						}, {
							text : '关闭',
							width : 60,
							click : closeWindows
						}]
			});

	// 设置业务选择框
	liger.get('bid').set({
				onButtonClick : function() {
					bidDlg = openDlgWindow(basePath + 'jsp/common/bidTreeDlg.jsp', 300, 300, "业务列表");
				}

			});
	// 设置提示文本
	$('#bid').attr('value', '请选择业务')
	// 初始化默认选择
	liger.get('isM').setValue("1");

	$('body').everyTime('100ms', setBidText);

	// 按钮居中
	$(".l-form-buttons").css("padding-left", $(window).width() / 2 - 85);

});

function setBidText() {
	liger.get('bid').setText($('#bid').attr('value'));
}

// 以下方法定义
function f_save() {
	var waitDlg = $.ligerDialog.waitting("正在添加,请等待...");
	$.ajax({
				type : "POST",
				async : true,
				url : basePath + "weblogicPerformanceAjaxManager.ajax?action=addWeblogicNode",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					ip : liger.get('ip').getValue(),
					alias : liger.get('alias').getValue(),
					isM : liger.get('isM').getValue(),
					community : liger.get('community').getValue(),
					port : liger.get('port').getValue(),
					bid : $('#bidValue').attr('value')
				},
				dataType : "text",
				success : function(array) {
					if (waitDlg)
						waitDlg.close();
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
