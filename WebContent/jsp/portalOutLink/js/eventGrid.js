Ext.BLANK_IMAGE_URL = '/afunms/ext/resources/images/default/s.gif';
var basePath = null;
Ext.onReady(function() {
	var searchValue = null;
	var eventGrid = null;
	basePath = $("#basePath").attr("value");
	var eventStore = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			method : 'post',
			url : basePath + '/portalEvent'
		}),
		reader : new Ext.data.JsonReader({
			root : 'EventList',
			totalProperty : 'total'
		}, [ {
			name : 'nodeId'
		}, {
			name : 'level'
		}, {
			name : 'eventLocation'
		}, {
			name : 'content'
		}, {
			name : 'times'
		}, {
			name : 'recordTime'
		}, {
			name : 'subentity'
		}, {
			name : 'lastTime'
		} ])

	});

	eventStore.load();
	
	eventStore.on('beforeload', function() {
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
				eventGrid.getStore().load({
					params : {
						start : 0,
						limit : 10,
						searchMsg : searchValue
					}
				});
			}
		} ]
	});

	eventGrid = new Ext.grid.GridPanel({
		renderTo : 'eventList',
		border : false,
		tbar : tb,
		height : Ext.get("eventList").getHeight(),
		autoWidth : true,
		store : eventStore,
		// grid columns
		columns : [ new Ext.grid.RowNumberer(), {
			header : "等级",
			dataIndex : 'level',
			width : 40,
			fixed : true,
			sortable : true,
			renderer : rendererToLevel
		}, {
			header : "告警定位",
			dataIndex : 'eventLocation',
			sortable : true,
			hidden : true
		}, {
			header : "告警描述",
			dataIndex : 'content',
			renderer : rendererToContent
		}, {
			header : "发生次数",
			dataIndex : 'times',
			width : 40,
			fixed : true,
			sortable : true,
			hidden : true
		}, {
			header : "开始时间",
			dataIndex : 'recordTime',
			width : 120,
			fixed : true,
			sortable : true
		}, {
			header : "最新时间",
			dataIndex : 'lastTime',
			sortable : true,
			hidden : true
		}, {
			header : "网元ID",
			dataIndex : 'nodeId',
			sortable : true,
			hidden : true
		}, {
			header : "告警指标",
			dataIndex : 'subentity',
			hidden : true
		} ],

		// customize view config
		viewConfig : {
			forceFit : true,
			scrollOffset : 3,
			stripeRows : true
		},

		// paging bar on the bottom
		bbar : new Ext.PagingToolbar({
			pageSize : 10,
			store : eventStore,
			displayInfo : true,
			displayMsg : '显示{0}-{1}条，共{2}条',
			emptyMsg : "没有数据"
		})
	});
	

	var runner = new Ext.util.TaskRunner();
	var freshListTask = {
		run : function() {
			eventStore.reload({
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

// 数据绑定
function rendererToLevel(value, p, record) {
	var levelname = "";
	var bgcolor = "";
	if ("3" == value) {
		levelname = "紧急";
		bgcolor = "red";
	} else if ("2" == value) {
		levelname = "严重";
		bgcolor = "orange";
	} else if ("1" == value) {
		levelname = "普通";
		bgcolor = "yellow";
	} else if ("0" == value) {
		levelname = "提示";
		bgcolor = "blue";
	}
	return "<div style='width:30px;text-align:center;background:" + bgcolor
			+ "'>" + levelname + "</div>";
}

function rendererToContent(value, p, record) {
	return "<a href='/afunms/index.jsp?portalFlag=alarm' target='_blank'>"
			+ value + "</a>";
}