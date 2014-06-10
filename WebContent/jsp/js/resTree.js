var resTreeData = [{
			isexpand : "true",
			text : "监视配置",
			children : [{
						url : "jsp/indicator/instance/list.jsp",
						text : "采集指标"
					}, {
						url : "jsp/indicator/monitor/host/service/serviceMonitorList.jsp",
						text : "主机服务"
					}, {
						url : "jsp/indicator/monitor/host/process/processMonitorList.jsp",
						text : "主机进程"
					}, {
						url : "jsp/threshold/interface/interfaceThresHoldList.jsp",
						text : "端口监视"
					}]
		}, {
			isexpand : "true",
			text : "阈值配置",
			children : [{
						url : "jsp/threshold/instance/list.jsp",
						text : "指标阈值"
					}, {
						url : "jsp/threshold/disk/diskThresHoldList.jsp",
						text : "磁盘阈值"
					}, {
						url : "jsp/threshold/link/linkThresHoldList.jsp",
						text : "链路阈值"
					}, {
						isexpand : "false",
						text : "数据库",
						children : [{
									url : "jsp/threshold/tableSpace/tableSpaceThresHoldList.jsp",
									text : "表空间阈值"
								}]
					}]
		}];
