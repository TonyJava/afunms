package com.afunms.system.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.common.util.DBManager;
import com.afunms.system.dao.CodedetailDao;
import com.afunms.system.dao.DictionaryDao;
import com.afunms.system.model.CodeType;
import com.afunms.system.model.Codedetail;
import com.afunms.system.model.User;
import com.bpm.design.model.DesignTempModel;
import com.bpm.system.utils.StringUtil;

/**
 * 获取数据字典相关信息
 * 
 * @author HXL nms_codetype表和nms_codedetail表
 */
public class CodeUtil {

	public static String flag = "-->";

	/**
	 * 获取所有模型的类型 key为resourceName
	 * 
	 * @return
	 */
	public static Map<String, String> getCodeModelType() {
		DictionaryDao dao = new DictionaryDao();
		List<DesignTempModel> list = dao.getCodeModelType();
		Map<String, String> map = new HashMap<String, String>();
		for (DesignTempModel vo : list) {
			map.put(vo.getName() + ".bpmn20.xml", vo.getTypename());
		}
		return map;
	}

	/**
	 * 获取所有模型的类型 key为processdefinitionid
	 * 
	 * @return
	 */
	public static Map<String, String> getCodeModeType_Definition() {
		return new DictionaryDao().getCodelModelType();
	}

	/**
	 * 
	 * @param where
	 *            条件子句
	 * @param defaultflag
	 *            是否有默认值
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public static String getLCLX(String where, boolean defaultflag, String defaultValue) {
		DictionaryDao dao = new DictionaryDao();
		List<CodeType> list = dao.loadAll(where);
		StringBuilder sb = new StringBuilder();
		for (CodeType vo : list) {
			if (defaultflag) {
				if (defaultValue.equals(vo.getCode().split("\\|")[0])) {
					sb.append("<option value='" + vo.getCode() + "' selected>").append(vo.getName()).append("</option>");
				} else {
					sb.append("<option value='" + vo.getCode() + "' >").append(vo.getName()).append("</option>");
				}
			} else {
				sb.append("<option value='" + vo.getCode() + "' >").append(vo.getName()).append("</option>");
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param where
	 *            条件子句
	 * @param defaultflag
	 *            是否有默认值
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	public static String getLCLX_TWO(String where, boolean defaultflag, String defaultValue) {
		CodedetailDao dao = new CodedetailDao();
		List<Codedetail> list = dao.loadAll(where);
		StringBuilder sb = new StringBuilder();
		for (Codedetail vo : list) {
			if (defaultflag) {
				if (defaultValue.equals(vo.getCode())) {
					sb.append("<option value='" + vo.getCode() + "' selected>").append(vo.getName()).append("</option>");
				} else {
					sb.append("<option value='" + vo.getCode() + "' >").append(vo.getName()).append("</option>");
				}
			} else {
				sb.append("<option value='" + vo.getCode() + "' >").append(vo.getName()).append("</option>");
			}
		}
		return sb.toString();
	}

	/**
	 * 获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public static User getUserByUserID(String userId) {
		String sql = "select su.user_id as userid  ,su.name,su.sex, " + " sd.dept,sp.name as position,sr.role,aig.name_ as `group`  " + "from system_user su "
				+ "left join system_department sd on su.dept_id = sd.id left join  system_position sp on su.position_id = sp.id left join system_role sr on su.role_id = sr.id "
				+ "left join act_id_membership aim on aim.USER_ID_=su.user_id left join act_id_group aig on aig.ID_=aim.GROUP_ID_ " + "where su.user_id='" + userId
				+ "' order by su.user_id,aig.name_";
		DBManager dbm = new DBManager();
		User user = new User();
		user.setGroup("");
		ResultSet rs = null;
		boolean flag = true;
		try {
			rs = dbm.executeQuery(sql);
			while (rs.next()) {
				if (flag) {
					user.setUserid(rs.getString("userid"));
					user.setName(rs.getString("name"));
					user.setSex(rs.getInt("sex"));
					user.setDeptname(rs.getString("dept"));
					user.setPositionname(rs.getString("position"));
					user.setRolename(rs.getString("role"));
				}
				flag = false;
				if (StringUtil.isBlank(rs.getString("group"))) {
					continue;
				}
				user.setGroup(user.getGroup() + "," + rs.getString("group"));
			}
			user.setGroup(user.getGroup().length() > 1 ? user.getGroup().substring(1) : "");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
					dbm.close();
				} catch (SQLException e) {

				}
			}
		}
		return user;
	}
}
