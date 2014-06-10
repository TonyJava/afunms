var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("form").ligerForm();

			// 初始化角色
			liger.get('role').set({
						data : getRole(),
						valueField : 'roleId',
						textField : 'role'
					});

			// 初始化部门
			liger.get('dept').set({
						data : getDept(),
						valueField : 'deptId',
						textField : 'dept'
					});

			// 初始化职位
			liger.get('position').set({
						data : getPosition(),
						valueField : 'positionId',
						textField : 'position'
					});

			// 设置业务选择框
			liger.get('bid').set({
						width : 300,
						nullText : "请选择业务",
						onButtonClick : function() {
							openWindow(basePath + 'jsp/common/bidTree.jsp', 300, 300, "业务列表");
						}

					});

			$("#submit").click(function() {
						save();
					});
		});

// 功能函数定义

function save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=add",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					userId : liger.get('userId').getValue(),
					name : liger.get('name').getValue(),
					password : liger.get('password').getValue(),
					email : liger.get('email').getValue(),
					mobile : liger.get('mobile').getValue(),
					phone : liger.get('phone').getValue(),
					role : liger.get('role').getValue(),
					dept : liger.get('dept').getValue(),
					position : liger.get("position").getValue(),
					bid : $("#bidValue").val()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								window.parent.refresh();
								closeDlgWindow();

							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}
function getRole(value) {
	$.ajax({
				type : "get",
				url : basePath + "userAjaxManager.ajax?action=getRole",
				dataType : "json",
				success : function(array) {
					var data = [];
					var combo = liger.get('role');
					if (!combo)
						return;
					$(array.Rows).each(function() {
								data.push({
											// 角色ID
											roleId : this.roleId,
											// 角色名称
											role : this.role
										});
							});
					combo.clear();
					combo.set('data', data);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});

}

function getDept(value) {
	$.ajax({
				type : "get",
				url : basePath + "userAjaxManager.ajax?action=getDept",
				dataType : "json",
				success : function(array) {
					var data = [];
					var combo = liger.get('dept');
					if (!combo)
						return;
					$(array.Rows).each(function() {
								data.push({
											// 部门ID
											deptId : this.deptId,
											// 部门名称
											dept : this.dept
										});
							});
					combo.clear();
					combo.set('data', data);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});

}

function getPosition(value) {
	$.ajax({
				type : "get",
				url : basePath + "userAjaxManager.ajax?action=getPosition",
				dataType : "json",
				success : function(array) {
					var data = [];
					var combo = liger.get('position');
					if (!combo)
						return;
					$(array.Rows).each(function() {
								data.push({
											// 职位ID
											positionId : this.positionId,
											// 职位名称
											position : this.position
										});
							});
					combo.clear();
					combo.set('data', data);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});

}