var basePath = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			id = $("#id").attr("value");
			$("form").ligerForm();

			// 设置SNMP下拉值
			liger.get('snmpversion').set({
						data : snmpVersion,
						valueField : 'value',
						textField : 'name'
					});
			
			// 设置managed下拉值
			liger.get('managed').set({
						data : managedItem,
						valueField : 'value',
						textField : 'text'
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
						update();
					});
			//初始化数据
			initData();
		});

//初始化
function initData() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "nodeHelperAjaxManager.ajax?action=beforeEdit",
		// 参数
		data : {
			id : id
		},
		dataType : "json",
		success : function(array) {
			if(array.Rows.length > 0){
				liger.get("alias").setValue(array.Rows[0].alias);
				liger.get("ip_address").setValue(array.Rows[0].ip_address);
				liger.get("snmpversion").setValue(array.Rows[0].snmpversion);
				liger.get("community").setValue(array.Rows[0].communityRO);
				liger.get("managed").setValue(array.Rows[0].managed);
				liger.get("bid").setValue(array.Rows[0].bid);
				liger.get("bid").setText(array.Rows[0].bidvalue);
				liger.get("sysLogCheckBoxList").setValue(array.Rows[0].syslog);
				setCollecttype(array.Rows[0].collecttype);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}


// 功能函数定义
function update() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "nodeHelperAjaxManager.ajax?action=updateNode",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					id : id,
					ipaddress : liger.get('ip_address').getValue(),
					alias : liger.get('alias').getValue(),
					managed : liger.get('managed').getValue(),
					snmpversion : liger.get('snmpversion').getValue(),
					community : liger.get('community').getValue(),
					bid : $("#bidValue").val(),
					bids : liger.get("bid").getValue(),
					sysLogLevels : liger.get("sysLogCheckBoxList").getValue()
				},
				dataType : "text",
				success : function(array) {
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

function setCollecttype(collecttype){
	if(collecttype == "2"){
		$("#snmpPanel").css("display", "none");
	}
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

var managedItem = [{
	value : '1',
	text : '是'
}, {
	value : '0',
	text : '否'
}];