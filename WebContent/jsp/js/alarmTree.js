var alarmTreeData = [{
			isexpand : "true",
			text : "告警内容",
			children : [{
						url : "jsp/alarm/alarmList.jsp",
						text : "告警列表"
					}, {
						url : "dotnetdemos/grid/sortable/server.aspx",
						text : "告警分布"
					}]
		}, {
			isexpand : "true",
			text : "告警方式",
			children : [{
						url : "jsp/alarm/alarmWay/alarmWayList.jsp",
						text : "告警方式配置"
					}, {
						url : "jsp/alarm/smsAlarm/smsAlarmList.jsp",
						text : "短信告警浏览"
					}]
		}, {
			isexpand : "true",
			text : "Trap",
			children : [{
						url : "jsp/alarm/trap/trapList.jsp",
						text : "Trap信息"
					}]
		}, {
			isexpand : "true",
			text : "SysLog",
			children : [{
						url : "jsp/alarm/syslog/syslogList.jsp",
						text : "SysLog列表"
					}, {
						url : "jsp/alarm/syslog/syslogConfigList.jsp",
						text : "过滤规则"
					}, {
						url : "jsp/alarm/syslog/syslogDetailList.jsp",
						text : "SysLog统计"
					}]
		}];
