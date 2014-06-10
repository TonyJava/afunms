package com.bpm.system.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.util.DBManager;

public class MessageSendUtil {

	/**
	 * 
	 * @param identityType
	 *            候选人类型: ASSIGNEE CANDIDATE OWNER
	 * @param identityGroupId
	 *            岗位ID
	 * @param userId
	 *            用户ID
	 * @param taskName
	 *            任务名
	 * @param processDefinitionName
	 *            流程定义实体名称
	 * @param starter
	 *            流程发起人
	 * @param flag
	 *            是否发送短信
	 * @return
	 */
	public static String sendMessage(String identityType, String identityGroupId, String identityUserId, String taskName, String processDefinitionName, String starter, String flag) {
		if ("0".equals(flag)) {
			// 不对下一环节执行人发短信，仅向流程发起人发送回执短信。
			return "";
		} else {
			String sqlString = "";// 获取下一环节任务执行人手机号
			if ("ASSIGNEE".equals(identityType.toUpperCase())) {
				sqlString = String.format("select mobile from system_user where user_id ='%s'", identityUserId);
			} else if ("CANDIDATE".equals(identityType.toUpperCase())) {
				sqlString = String.format("select mobile from system_user where user_id in (select USER_ID_ from act_id_membership where GROUP_ID_='%s')", identityGroupId);
			}
			DBManager dbm = new DBManager();
			ResultSet rs = null;
			List<String> mobiles = new ArrayList<String>();
			if (!"".equals(sqlString)) {
				try {
					rs = dbm.executeQuery(sqlString);
					while (rs.next()) {
						mobiles.add(rs.getString("mobile"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return "";
	}
}
