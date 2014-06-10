var manager = null;
var bid = null;
var pid = null;
var tree = null;
var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");
	 function itemclick(item, i)
     {
		 if(item.text == "增加"){
			 $("#showNodeDiv").show();
			 $("#type").attr("value","add");
		 }else if(item.text == "删除"){
			 $.ligerDialog.success(f_deteleBusinessNode(bid));
		 }
     }
	menu = $.ligerMenu({ top: 100, left: 100, width: 120, items:
        [
        { text: '增加', click: itemclick, icon: 'add' },
        { text: '删除', click: itemclick, icon: 'delete' }
        ]
        });
	tree = $("#bidTree").ligerTree({
		data : f_getBids(),
		idFieldName : 'id',
		checkbox : false,
		 onContextmenu: function (node, e)
         { 
			 bid = node.data.id;
             menu.show({ top: e.pageY, left: e.pageX });
             return false;
         },
         onclick : function(node){
        	 bid = node.data.id;
        	 pid = node.data.pid;
        	 $("#showNodeDiv").show();
        	 $("#name").attr("value",node.data.text);
        	 $("#descr").attr("value",node.data.descr);
        	 $("#type").attr("value","update");
         }
	});
	
	$("#save").click(function() {
		var type = $("#type").attr("value");
		if(type == "add"){
			save();
		}if(type == "update"){
			update();
		}
	});
});

function save(){
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "nodeHelperAjaxManager.ajax?action=addBusiness",
		// 参数
		contentType : "application/x-www-form-urlencoded; charset=UTF-8",
		data : {
			pid : bid,
			name : $("#name").attr("value"),
			descr : $("#descr").attr("value")
		},
		dataType : "text",
		success : function(array) {
			$.ligerDialog.success(array, '提示', function(yes) {
						// 刷新列表
					refresh();
					$("#showNodeDiv").hide();
					});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}

function update(){
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "nodeHelperAjaxManager.ajax?action=updateBusiness",
		// 参数
		contentType : "application/x-www-form-urlencoded; charset=UTF-8",
		data : {
			bid : bid,
			pid : pid,
			name : $("#name").attr("value"),
			descr : $("#descr").attr("value")
		},
		dataType : "text",
		success : function(array) {
			$.ligerDialog.success(array, '提示', function(yes) {
						// 刷新列表
					refresh();
					$("#showNodeDiv").hide();
					});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			 (errorThrown);
		}
	});
}

function f_getBids() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "nodeHelperAjaxManager.ajax?action=getBids",
		dataType : "json",
		success : function(array) {
			rs = array;
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
	return rs;
}

function f_deteleBusinessNode(id) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "nodeHelperAjaxManager.ajax?action=deleteBusinessNode",
				// 参数
				data : {
					id : id
				},
				dataType : "text",
				success : function(array) {
					// 成功删除则更新表格行
					refresh();
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}

function refresh(){
	tree.setData(f_getBids());
}