var systemTreeData = [{
	isexpand : "true",
	text : "用户管理",
	children : [{
				url : "user/userList.jsp",
				text : "用户"
			},{
				url : "userRole/roleList.jsp",
				text : "角色"
			}, {
				url : "dept/deptList.jsp",
				text : "部门"
			},{
				url : "position/positionList.jsp",
				text : "职位"
			},{
				url : "userAudit/userAuditList.jsp",
				text : "用户操作审计"
			}]
}
//, {
//	isexpand : "true",
//	text : "权限管理",
//	children : [{
//				url : "jsp/threshold/instance/list.jsp",
//				text : "权限"
//			}]
//}
, {
	isexpand : "true",
	text : "日志管理",
	children : [{
				url : "syslog/systemSyslogList.jsp",
				text : "操作日志"
			}]
}, {
	isexpand : "true",
	text : "系统配置",
	children : [{
				url : "email/emailList.jsp",
				text : "邮箱设置"
			},{
				url : "business/bidTree.jsp",
				text : "业务分类"
			}]
}];
