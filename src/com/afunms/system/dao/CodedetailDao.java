package com.afunms.system.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.system.model.Codedetail;
import com.bpm.system.utils.StringUtil;

@SuppressWarnings("unchecked")
public class CodedetailDao extends BaseDao implements DaoInterface {
	public CodedetailDao() {
		super("nms_codedetail");
	}

	public List listByPage(int curpage, int perpage) {
		return listByPage(curpage, "", perpage);
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Codedetail vo = new Codedetail();
		try {
			vo.setId(rs.getString("ID"));
			vo.setName(rs.getString("NAME"));
			vo.setCode(rs.getString("CODE"));
			vo.setDesp(rs.getString("DESP"));
			vo.setSeq(rs.getInt("SEQ"));
			vo.setTypeid(rs.getString("TYPEID"));
		} catch (SQLException e) {
			e.printStackTrace();
			vo = null;
		}

		return vo;
	}

	public boolean save(BaseVo baseVo) {
		boolean flag = true;
		Codedetail vo = (Codedetail) baseVo;
		StringBuilder sb = new StringBuilder();
		sb.append("insert into nms_codedetail (ID,NAME,CODE,DESP,SEQ,TYPEID) values ('").append(vo.getId()).append("','").append(vo.getName()).append("','").append(vo.getCode())
				.append("','").append(vo.getDesp()).append("',").append(vo.getSeq()).append(",'").append(vo.getTypeid()).append("'").append(")");
		try {
			conn.executeUpdate(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public boolean update(BaseVo baseVo) {
		boolean flag = true;
		Codedetail vo = (Codedetail) baseVo;
		StringBuilder sb = new StringBuilder();
		sb.append("update nms_codedetail set NAME='").append(vo.getName()).append("',DESP='").append(vo.getDesp()).append("',SEQ=").append(vo.getSeq()).append(" where ID='")
				.append(vo.getId()).append("'");
		try {
			conn.executeUpdate(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public boolean delete(String[] checkbox) {
		boolean flag = true;
		StringBuilder sb = new StringBuilder();// 删除nms_codedetail
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();// 删除bpm_modeltype
		sb.append("delete from nms_codedetail where ID in ('");
		for (String str : checkbox) {
			sb1.append(str).append("','");
		}
		sb1.delete(sb1.length() - 3, sb1.length() - 1);
		sb.append(sb1.toString());
		sb.append(")");
		conn.addBatch(sb.toString());
		sb2.append("delete from bpm_modeltype where CDID in ('");
		sb2.append(sb1.toString());
		sb2.append(")");
		conn.addBatch(sb2.toString());
		try {
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取所有LCLX数据二级字典
	 */
	public List<Codedetail> loadAll(String where) {
		StringBuilder sb = new StringBuilder();
		List<Codedetail> list = new ArrayList<Codedetail>();
		sb.append("select ID,NAME,CODE from nms_codedetail where 1=1");
		if (StringUtil.isNotBlank(where)) {
			sb.append(where);
		}
		ResultSet rs = conn.executeQuery(sb.toString());
		Codedetail vo = null;
		try {
			while (rs.next()) {
				vo = new Codedetail();
				vo.setName(rs.getString("NAME"));
				vo.setCode(rs.getString("CODE") + "|" + rs.getString("ID"));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
