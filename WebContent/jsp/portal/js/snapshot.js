var basePath = null;
var data = null;
Ext.BLANK_IMAGE_URL = '/afunms/ext/resources/images/default/s.gif';
Ext.onReady(function() {
	basePath = $("#basePath").attr("value");
	data = getData();
	var store = new Ext.data.Store({
		proxy : new Ext.ux.data.PagingMemoryProxy(data),
		reader : new Ext.data.JsonReader({
			root : 'Rows',
			totalProperty : 'total'
		}, [ {
			name : 'name'
		}, {
			name : 'url'
		}, {
			name : 'href'
		} ])
	});
	store.load({
		params : {
			start : 0,
			limit : 7
		}
	});

	var tpl = new Ext.XTemplate('<tpl for=".">',
			'<div class="thumb-wrap" id="{name}">',
			'<div class="thumb"><img src="css/img/{url}"></div>',
			'<span class="x-editable">{name}</span></div>', '</tpl>',
			'<div class="x-clear"></div>');

	var panel = new Ext.Panel({
		border : false,
		id : 'images-view',
		frame : true,
		renderTo : 'snapshot',
		height : Ext.get("snapshot").getHeight(),
		items : new Ext.DataView({
			id : 'view',
			store : store,
			loadMask:true,
			tpl : tpl,
			overClass : 'x-view-over',
			itemSelector : 'div.thumb-wrap',
			emptyText : '没有数据',
			listeners : {
				click : function(dv, index, node, e) {
					var record = dv.store.getAt(index);
					top.hFrame.location.href = "/afunms/jsp/performance/performance.jsp?portalFlag="+record.data.name;
				}
			}
		}),
		bbar : new Ext.PagingToolbar({
			pageSize : 7,
			store : store,
			displayInfo : true,
			displayMsg : '显示{0}-{1}条，共{2}条',
			emptyMsg : "没有数据"
		})
	});

	var runner = new Ext.util.TaskRunner();
	var freshListTask = {
		run : function() {
			data = getData();
			store.load({
				params : {
					start : 0,
					limit : 7
				}
			});
			Ext.getCmp('view').refresh();
		},
		interval : 1000 * 30
	};
	runner.start(freshListTask);
});
function getData() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "snapshotAjaxManager.ajax?action=getSnapshotData",
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