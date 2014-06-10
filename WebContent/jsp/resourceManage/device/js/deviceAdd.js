var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("form").ligerForm();

			// 设置采集方式下拉值
			liger.get('collecttype').set({
						data : deviceCollectType,
						valueField : 'type',
						textField : 'typeName',
						onSelected : function(value) {
							if (value == 2) {
								$("#snmpPanel").css("display", "none");
							} else if (value == 1) {
								$("#snmpPanel").css("display", "block");
							}
						}

					});
			// 设置SNMP下拉值
			liger.get('snmpversion').set({
						data : snmpVersion,
						valueField : 'value',
						textField : 'name'
					});

			// 设置类型下拉值
			liger.get('type').set({
						data : getTypeDataForNodeAdd(),
						valueField : 'typeValue',
						textField : 'typeDescr',
						onSelected : function(value) {
							f_onTypeChangedForNodeAdd(value)
						}
					});
			// 设置类型下拉值
			liger.get('subType').set({
						valueField : 'osValue',
						textField : 'osDescr'
					});

			// 设置业务选择框
			liger.get('bid').set({
				width : 300,
				nullText : "请选择业务",
				onButtonClick : function() {
					openWindow(basePath + 'jsp/common/bidTree.jsp', 300, 300,
							"业务列表");
				}

			});
			// 设置SysLog等级列表
			$("#sysLogCheckBoxList").ligerCheckBoxList({
						rowSize : 9,
						data : sysLogLevel,
						textField : 'name'
					});

			$("#submit").click(function() {
						save();
					});
		});

// 功能函数定义

function save() {
	var waitDlg = $.ligerDialog.waitting("正在添加,请等待...");
	$.ajax({
				type : "POST",
				async : true,
				url : basePath + "nodeHelperAjaxManager.ajax?action=addNode",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					ip_address : liger.get('ip_address').getValue(),
					alias : liger.get('alias').getValue(),
					snmpversion : liger.get('snmpversion').getValue(),
					community : liger.get('communityRO').getValue(),
					writecommunity : liger.get('communityRW').getValue(),
					type : liger.get('type').getValue(),
					ostype : liger.get('subType').getValue(),
					collecttype : liger.get('collecttype').getValue(),
					bid : $("#bidValue").val(),
					sysLogLevels : liger.get("sysLogCheckBoxList").getValue()
				},
				dataType : "text",
				success : function(array) {
					if (waitDlg)
						waitDlg.close();
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								refreshParent();
								window.close();
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function refreshParent() {
	window.opener.refresh();
}
// 初始值定义
var snmpVersion = [{
			value : '0',
			name : 'V1'
		}, {
			value : '1',
			name : 'V2'
		}];

var sysLogLevel = [{
			id : 0,
			name : '紧急'
		}, {
			id : 1,
			name : '报警'
		}, {
			id : 2,
			name : '关键'
		}, {
			id : 3,
			name : '错误'
		}, {
			id : 4,
			name : '警告'
		}, {
			id : 5,
			name : '通知'
		}, {
			id : 6,
			name : '提示'
		}, {
			id : 7,
			name : '调试'
		}];

var deviceCollectType = [{
			type : '1',
			typeName : 'SNMP'
		}, {
			type : '2',
			typeName : '代理'
		}];