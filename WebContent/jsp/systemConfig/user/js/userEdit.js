var basePath = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			id = $("#id").attr("value");
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

			initData();
			$("#submit").click(function() {
						save();
					});
		});

// 功能函数定义
function initData() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=beforeEditUser",
				// 参数
				data : {
					id : getUrlParam("id")
				},
				dataType : "json",
				success : function(array) {
					liger.get("userId").setDisabled();
					$(array.Rows).each(function() {
								liger.get("userId").setValue(this.userId);
								liger.get("name").setValue(this.name);
								liger.get("email").setValue(this.email);
								liger.get("mobile").setValue(this.mobile);
								liger.get("phone").setValue(this.phone);
								liger.get("role").setValue(this.roleId);
								liger.get("dept").setValue(this.deptId);
								liger.get("position").setValue(this.positionId);
								liger.get("bid").setValue(this.businessId);

								liger.get("position").setText(this.position);
								liger.get("dept").setText(this.dept);
								liger.get("role").setText(this.role);
								liger.get("bid").setText(this.business);
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + " from userEdit.js");
				}
			});
}

function save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=editUser",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					id : id,
					userId : liger.get('userId').getValue(),
					name : liger.get('name').getValue(),
					password : liger.get('password').getValue(),
					email : liger.get('email').getValue(),
					mobile : liger.get('mobile').getValue(),
					phone : liger.get('phone').getValue(),
					role : liger.get('role').getValue(),
					dept : liger.get('dept').getValue(),
					position : liger.get("position").getValue(),
					bid : $("#bidValue").val(),
					bids : liger.get("bid").getValue()
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

function getRole() {
	var data = [];
	$.ajax({
				type : "get",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=getRole",
				dataType : "json",
				success : function(array) {
					$(array.Rows).each(function() {
								data.push({
											// 角色ID
											roleId : this.roleId,
											// 角色名称
											role : this.role
										});
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return data;

}

function getDept() {
	var data = [];
	$.ajax({
				type : "get",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=getDept",
				dataType : "json",
				success : function(array) {
					$(array.Rows).each(function() {
								data.push({
											// 部门ID
											deptId : this.deptId,
											// 部门名称
											dept : this.dept
										});
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return data;
}

function getPosition() {
	var data = [];
	$.ajax({
				type : "get",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=getPosition",
				dataType : "json",
				success : function(array) {
					$(array.Rows).each(function() {
								data.push({
											// 职位ID
											positionId : this.positionId,
											// 职位名称
											position : this.position
										});
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return data;
}