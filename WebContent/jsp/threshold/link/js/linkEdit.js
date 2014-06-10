var basePath = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						inputWidth : 170,
						labelWidth : 90,
						height : 650,
						space : 40,
						fields : [{
									name : "linkedId",
									type : "hidden"
								}, {
									name : "startId",
									type : "hidden"
								}, {
									name : "endId",
									type : "hidden"
								},{
									name : "indexStart",
									type : "hidden"
								}, {
									name : "indexEnd",
									type : "hidden"
								},{
									name : "startIp",
									type : "hidden"
								}, {
									name : "endIp",
									type : "hidden"
								},{
									display : "链路名称",
									name : "linkName",
									newline : false,
									type :	"text",
									validate : {
										required : true
									}
								}, {
									display : "链路流量阀值",
									name : "maxSpeed",
									newline : false,
									type : "text",
									validate : {
										required : true
									}
								}, {
									display : "宽带利用率",
									name : "maxPer",
									newline : true,
									type : "text",
									validate : {
										required : true
									}
								}, {
									display : "链路显示 ",
									name : "linkText",
									newline : false,
									type : "select",
									comboboxName : "linkText",
									options : {
										valueField : "value",
										textField : 'name',
										data : linkShow,
										selectBoxHeight : 100
									}
								}, {
									display : "起点设备",
									name : "startName",
									newline : true,
									type : "text"
								}, {
									display : "起点端口索引",
									name : "startIndex",
									newline : true,
									width : 300,
									type : "popup"
								}, {
									display : "终点设备",
									name : "endName",
									newline : true,
									type : "text"
								}, {
									display : "终点端口索引",
									name : "endIndex",
									newline : true,
									width : 300,
									type : "popup"
								}, {
									display : "接口显示",
									name : "interfShow",
									newline : true,
									type : "select",
									comboboxName : "interfShow",
									options : {
										valueField : "value",
										textField : 'name',
										data : interfShowColumns,
										selectBoxHeight : 50
									}
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
			
			liger.get('startIndex').set({
				onButtonClick : function() {
					choseifIndex($("#startId").attr("value"),"indexStart");
				}

			});
			liger.get('endIndex').set({
				onButtonClick : function() {
					choseifIndex($("#endId").attr("value"),"indexEnd");
				}

			});

			initData();
			
		});

function initData() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "thresHoldAjaxManager.ajax?action=beforeEditLink",
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
									linkedId : this.linkedId,
									startId : this.startId,
									endId :	this.endId,
									indexStart : this.indexStart,
									indexEnd : this.indexEnd,
									startIp : this.startIp,
									endIp : this.endIp,
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

function f_save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "thresHoldAjaxManager.ajax?action=editLink",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					linkedId : $("#linkedId").attr("value"),
					startId : $("#startId").attr("value"),
					endId :	$("#endId").attr("value"),
					indexStart : $("#indexStart").attr("value"),
					indexEnd :	$("#indexEnd").attr("value"),
					linkName : liger.get("linkName").getValue(),
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