var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");
	$(window).resize(function() {
		location.reload();
	});
	
	var h = $(parent.window).height() - 155;
	var w = $(parent.window).width();
	g = $("#portalMain").ligerPortal({
		columns : [ {
			width : w / 2 - 15,
			panels : [ {
				title : '设备快照',
				width : w / 2 - 15,
				height : h / 2,
				url : basePath + 'jsp/portal/snapshot.jsp'
			}, {
				title : '设备列表',
				width : w / 2 - 15,
				height : h / 2,
				url : basePath + 'jsp/portal/resourceGrid.jsp'
			} ]
		}, {
			width : w / 2 - 10,
			panels : [ {
				title : '拓扑',
				width : w / 2 - 10,
				height : h / 2,
				url : '/afunms/topology/network/networkForPortal.jsp'
			}, {
				title : '告警列表',
				width : w / 2 - 10,
				height : h / 2,
				url : basePath + 'jsp/portal/eventGrid.jsp'
			} ]
		} ]
	});
});
