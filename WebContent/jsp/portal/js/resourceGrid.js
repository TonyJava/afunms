Ext.BLANK_IMAGE_URL = '/afunms/ext/resources/images/default/s.gif';
var basePath = null;
Ext.onReady(function() {
	basePath = $("#basePath").attr("value");
	var searchValue = null;
	var nodeGrid = null;
	var rsStore = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			method : 'post',
			url : basePath + '/serverAjaxManager.ajax?action=ajaxHostInfolist'
		}),
		reader : new Ext.data.JsonReader({
			root : 'monitorNodeList',
			totalProperty : 'total'
		}, [ {
			name : 'nodeId',
			mapping : 'nodeId'
		}, {
			name : 'ipAddress',
			mapping : 'ipAddress'
		}, {
			name : 'alias',
			mapping : 'alias'
		}, {
			name : 'status',
			mapping : 'status'
		}, {
			name : 'category',
			mapping : 'category'
		}, {
			name : 'pingValue',
			mapping : 'pingValue'
		}, {
			name : 'cpuValue',
			mapping : 'cpuValue'
		}, {
			name : 'memoryValue',
			mapping : 'memoryValue'
		}, {
			name : 'inutilhdxValue',
			mapping : 'inutilhdxValue'
		}, {
			name : 'oututilhdxValue',
			mapping : 'oututilhdxValue'
		}, {
			name : 'type',
			mapping : 'type'
		}, {
			name : 'dbType',
			mapping : 'dbType'
		} ])
	});

	rsStore.load();

	rsStore.on('beforeload', function() {
		this.baseParams = {
			searchMsg : searchValue,
			start : 0,
			limit : 10
		};
	});

	var tb = new Ext.Toolbar({
		width : 400,
		items : [ {
			xtype : 'tbtext',
			text : '查询条件'
		}, {
			xtype : 'textfield',
			id : 'searchMsg',
			name : 'searchMsg',
			width : 200
		}, '-', {
			xtype : 'button',
			text : '查询',
			handler : function() {
				searchValue = Ext.getCmp("searchMsg").getValue();
				nodeGrid.getStore().load({
					params : {
						start : 0,
						limit : 10,
						searchMsg : searchValue
					}
				});
			}
		} ]
	});

	nodeGrid = new Ext.grid.GridPanel({
		renderTo : 'nodeGrid',
		border : false,
		tbar : tb,
		height : Ext.get("nodeGrid").getHeight(),
		autoWidth : true,
		ds : rsStore,
		loadMask:true,
		// grid columns
		columns : [ new Ext.grid.RowNumberer(), {
			header : 'ip地址',
			sortable : true,
			dataIndex : 'ipAddress'
		}, {
			header : '名称',
			dataIndex : 'alias',
			renderer : rendererToAlias
		}, {
			header : '类别',
			dataIndex : 'category',
			width : 70
		}, {
			header : '可用性(%)',
			sortable : true,
			width : 70,
			dataIndex : 'pingValue'
		}, {
			header : 'CPU(%)',
			sortable : true,
			width : 70,
			dataIndex : 'cpuValue'
		}, {
			header : '内存(%)',
			sortable : true,
			width : 70,
			dataIndex : 'memoryValue'
		}, {
			header : '入口流速(KB/S)',
			sortable : true,
			dataIndex : 'inutilhdxValue',
			hidden : true
		}, {
			header : '出口流速(KB/S)',
			sortable : true,
			dataIndex : 'oututilhdxValue',
			hidden : true
		} ],

		// customize view config
		viewConfig : {
			forceFit : true,
			scrollOffset : 3,
			stripeRows : true,
			getRowClass : function(record, index) {
				var flag = record.get('status');
				if (flag == 1) {
					return 'grid-yellow';
				} else if (flag == 2) {
					return 'grid-orange';
				} else if (flag == 3) {
					return 'grid-red';
				}
			}
		},

		// paging bar on the bottom
		bbar : new Ext.PagingToolbar({
			pageSize : 10,
			store : rsStore,
			displayInfo : true,
			displayMsg : '显示{0}-{1}条，共{2}条',
			emptyMsg : "没有数据"
		})
	});

	var runner = new Ext.util.TaskRunner();
	var freshListTask = {
		run : function() {
			nodeGrid.getStore().reload({
				params : {
					start : 0,
					limit : 10,
					searchMsg : searchValue
				}
			});
		},
		interval : 1000 * 60
	};
	runner.start(freshListTask);

});
function rendererToAlias(value, p, record) {
	var typeString = record.data.type.toLowerCase();
	var dbTypeString = record.data.dbType.toLowerCase();
	var categoryString = record.data.category.toLowerCase();
	if (typeString.indexOf("windows") > -1) {
		return "<a href='/afunms/jsp/performance/detail/windowsPerformanceDetail.jsp?nodeId="
				+ record.data.nodeId
				+ "&alias="
				+ record.data.alias
				+ "&type="
				+ record.data.type
				+ "&subType="
				+ record.data.dbType
				+ "&ip="
				+ record.data.ipAddress + "'  target=_blank>" + value + "</a>";
	} else if (typeString.indexOf("linux") > -1) {
		return "<a href='/afunms/jsp/performance/detail/linuxPerformanceDetail.jsp?nodeId="
				+ record.data.nodeId
				+ "&alias="
				+ record.data.alias
				+ "&ip="
				+ record.data.ipAddress + "'  target=_blank>" + value + "</a>";
	} else if (dbTypeString.indexOf("mysql") > -1) {
		return "<a href='/afunms/jsp/performance/detail/mysqlPerformanceDetail.jsp?id="
				+ record.data.nodeId
				+ "&alias="
				+ record.data.alias
				+ "&type="
				+ record.data.type
				+ "&subType="
				+ record.data.dbType
				+ "&ip="
				+ record.data.ipAddress + "'  target=_blank>" + value + "</a>";
	} else if (dbTypeString.indexOf("oracle") > -1) {
		return "<a href='/afunms/jsp/performance/detail/oraclePerformanceDetail.jsp?id="
				+ record.data.nodeId
				+ "&alias="
				+ record.data.alias
				+ "&type="
				+ record.data.type
				+ "&subType="
				+ record.data.dbType
				+ "&ip="
				+ record.data.ipAddress + "'  target=_blank>" + value + "</a>";
	}  else if (dbTypeString.indexOf("sqlserver") > -1) {
		return "<a href='/afunms/jsp/performance/detail/sqlserverPerformanceDetail.jsp?id="
		+ record.data.nodeId
		+ "&alias="
		+ record.data.alias
		+ "&type="
		+ record.data.type
		+ "&subType="
		+ record.data.dbType
		+ "&ip="
		+ record.data.ipAddress + "'  target=_blank>" + value + "</a>";
} else if (categoryString.indexOf("交换机") > -1
			|| categoryString.indexOf("路由器") > -1) {
		return "<a href='/afunms/jsp/performance/detail/netPerformanceDetail.jsp?nodeId="
				+ record.data.nodeId
				+ "&alias="
				+ record.data.alias
				+ "&type="
				+ record.data.type
				+ "&subType="
				+ record.data.dbType
				+ "&ip="
				+ record.data.ipAddress + "'  target=_blank>" + value + "</a>";
	}
}