var basePath = null;
var id = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			id = $("#id").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			$("form").ligerForm();
			
			// 设置采集方式下拉值
			liger.get('collecttype').set({
						data : dbCollectType,
						valueField : 'type',
						textField : 'typeName'
					});

			// 设置SNMP下拉值
			liger.get('managed').set({
						data : managed,
						valueField : 'value',
						textField : 'name'
					});


			// 设置业务选择框
			liger.get('bid').set({
				width : 200,
				nullText : "请选择业务",
				onButtonClick : function() {
					openWindow(basePath + 'jsp/common/bidTree.jsp', 300, 300,
							"业务列表");
				}

			});
			
			// 按钮居中
			$(".l-form-buttons")
					.css("padding-left", $(window).width() / 2 - 85);
			
			$("#submit").click(function() {
				f_edit();
			});
			
			$("#closeWindow").click(function() {
				window.close();
			});
			
			
			initData();
			
		});

function initData() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "dbPerformanceAjaxManager.ajax?action=dbReadyEdit",
		// 参数
		data : {
			id : id
		},
		dataType : "json",
		success : function(array) {
			if(array.Rows.length > 0){
				liger.get("alias").setValue(array.Rows[0].alias);
				liger.get("db_name").setValue(array.Rows[0].dbname);
				liger.get("ip_address").setValue(array.Rows[0].ipaddress);
				liger.get("user").setValue(array.Rows[0].user);
				liger.get("password").setValue(array.Rows[0].password);
				liger.get("port").setValue(array.Rows[0].port);
				liger.get("dbuse").setValue(array.Rows[0].dbuse);
				liger.get("managed").setValue(array.Rows[0].managed);
				liger.get("collecttype").setValue(array.Rows[0].collecttype);
				liger.get("bid").setValue(array.Rows[0].bidvalue);
				liger.get("bid").setText(array.Rows[0].bid);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function f_edit() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=dbEdit",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					id : id,
					ip_address : liger.get('ip_address').getValue(),
					alias : liger.get('alias').getValue(),
					db_name : liger.get('db_name').getValue(),
					user : liger.get('user').getValue(),
					password : liger.get('password').getValue(),
					port : liger.get('port').getValue(),
					dbuse : liger.get('dbuse').getValue(),
					bid : $("#bidValue").val(),
					bids : liger.get("bid").getValue(),
					managed : liger.get("managed").getValue(),
					collecttype : liger.get("collecttype").getValue()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								window.opener.refresh();
								window.close();

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

function getIfentityList(){
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
									endId :	this.endId,
									indexStart : this.indexStart,
									indexEnd : this.indexEnd,
									linkName : this.linkName,
									maxSpeed : this.maxSpeed,
									maxPer : this.maxPer,
									linkText : this.linkText,
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

function choseifIndex(string,flag) {
	if (!string) {
		$.ligerDialog.error("获取失败");
		return;
	}
	$.ligerDialog.open({
				url : 'choseifIndex.jsp?id=' + string + "&flag=" + flag,
				height : 400,
				width : 600,
				buttons : [{
							text : '确定',
							onclick : function(item, dialog) {
								// 获取对话框主机列表
								var processGrid = dialog.frame.grid;
								// 获取列表选中项
								var data = processGrid.getSelected();
								// 获得父页面ip地址对象
								$("#" + $(dialog.frame.flag).attr("value")).attr("value",data.ifIndex);
								
								if($(dialog.frame.flag).attr("value").indexOf("indexEnd") > -1){
									liger.get("linkName").setValue($("#startIp").attr("value")+"_"+$("#indexStart").attr("value")+"/"+$("#endIp").attr("value")+"_"+data.ifIndex);
									liger.get("endIndex").setText(data.ifIndex+"("+data.ifDescr+")");
								}else if($(dialog.frame.flag).attr("value").indexOf("indexStart") > -1){
									liger.get("linkName").setValue($("#startIp").attr("value")+"_"+data.ifIndex+"/"+$("#endIp").attr("value")+"_"+$("#indexEnd").attr("value"));
									liger.get("startIndex").setText(data.ifIndex+"("+data.ifDescr+")");
								}
								// 对象赋值
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

function choseifHost() {
	if (!string) {
		$.ligerDialog.error("获取失败");
		return;
	}
	$.ligerDialog.open({
				url : 'choseifIndex.jsp?id=' + string + "&flag=" + flag,
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
								liger.get($(dialog.frame.flag).attr("value")).setValue(data.ifIndex);
								liger.get($(dialog.frame.flag).attr("value")).setText(data.ifIndex+"("+data.ifDescr+")");
								
								if($(dialog.frame.flag).attr("value").indexOf("endIndex") > -1){
									liger.get("linkName").setValue($("#startIp").attr("value")+"_"+$("#indexStart").attr("value")+"/"+$("#endIp").attr("value")+"_"+data.ifIndex);
								}else if($(dialog.frame.flag).attr("value").indexOf("startIndex") > -1){
									liger.get("linkName").setValue($("#startIp").attr("value")+"_"+data.ifIndex+"/"+$("#endIp").attr("value")+"_"+$("#indexEnd").attr("value"));
								}
								// 对象赋值
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

//初始值定义
var managed = [{
			value : '1',
			name : '是'
		}, {
			value : '0',
			name : '否'
		}];


var dbCollectType = [{
			type : '1',
			typeName : 'JDBC'
		}, {
			type : '2',
			typeName : '脚本'
		}];
