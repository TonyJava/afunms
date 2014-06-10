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
	 *            ��ѡ������: ASSIGNEE CANDIDATE OWNER
	 * @param identityGroupId
	 *            ��λID
	 * @param userId
	 *            �û�ID
	 * @param taskName
	 *            ������
	 * @param processDefinitionName
	 *            ���̶���ʵ������
	 * @param starter
	 *            ���̷�����
	 * @param flag
	 *            �Ƿ��Ͷ���
	 * @return
	 */
	public static String sendMessage(String identityType, String identityGroupId, String identityUserId, String taskName, String processDefinitionName, String starter, String flag) {
		if ("0".equals(flag)) {
			// ������һ����ִ���˷����ţ��������̷����˷��ͻ�ִ���š�
			return "";
		} else {
			String sqlString = "";// ��ȡ��һ��������ִ�����ֻ���
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
