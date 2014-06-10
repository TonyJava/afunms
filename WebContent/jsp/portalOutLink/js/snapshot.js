var basePath = null;
Ext.BLANK_IMAGE_URL = '/afunms/ext/resources/images/default/s.gif';
Ext.onReady(function() {
			basePath = $("#basePath").attr("value");
			var snapshorStore = new Ext.data.Store({
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
			var tpl = new Ext.XTemplate(
					'<tpl for=".">',
					'<div class="thumb-wrap" id="{name}">',
					'<div class="thumb"><img src="/afunms/jsp/portalOutLink/css/img/{url}"></div>',
					'<span class="x-editable">{name}</span></div>', '</tpl>',
					'<div class="x-clear"></div>');
			setTimeout(function() {
				Ext.Ajax.request({
					url : basePath + "/portalSnapshot",
					method : 'POST',
					async : false,
					success : function(response) {
						var localData = Ext.decode(response.responseText);
						snapshorStore.proxy = new Ext.data.PagingMemoryProxy(
								localData);
						snapshorStore.load({
							params : {
								start : 0,
								limit : 7
							}
						});
					},
					failure : function() {
						Ext.Msg.alert("设备获取失败");
					}
				});
			}, 500);

			var panel = new Ext.Panel({
				border : false,
				id : 'images-view',
				frame : true,
				renderTo : 'snapshot',
				height : Ext.get("snapshot").getHeight(),
				items : new Ext.DataView({
					id : 'view',
					store : snapshorStore,
					tpl : tpl,
					overClass : 'x-view-over',
					itemSelector : 'div.thumb-wrap',
					emptyText : '没有数据',
					listeners : {
						click : function(dv, index, node, e) {
							openCommonFullWindow(
									"/afunms/index.jsp?portalFlag=snapshot",
									"设备列表");
						}
					}
				}),
				bbar : new Ext.PagingToolbar({
					pageSize : 7,
					store : snapshorStore,
					displayInfo : true,
					displayMsg : '显示{0}-{1}条，共{2}条',
					emptyMsg : "没有数据"
				})
			});
		});
