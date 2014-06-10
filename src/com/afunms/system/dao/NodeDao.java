package com.afunms.system.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.system.model.CodeType;
import com.afunms.system.model.Node;

public class NodeDao extends BaseDao implements DaoInterface {
	public NodeDao() {
		super("bpm_node");
	}

	@SuppressWarnings("unchecked")
	public List listByPage(int curpage, int perpage) {
		return listByPage(curpage, "", perpage);
	}

	public BaseVo loadFromRS(ResultSet rs) {
		CodeType vo = new CodeType();
		try {
			vo.setId(rs.getString("ID"));
			vo.setName(rs.getString("NAME"));
			vo.setCode(rs.getString("CODE"));
			vo.setDesp(rs.getString("DESP"));
			vo.setSeq(rs.getInt("SEQ"));
			vo.setType(rs.getString("TYPE"));
		} catch (SQLException e) {
			e.printStackTrace();
			vo = null;
		}

		return vo;
	}

	/**
	 * ��ȡ�������ͽڵ�
	 * 
	 * @return
	 */
	public List<Node> getNodes() {
		String sql = "select ID,NAME,`DESC`,PID from bpm_node order by PID asc ";
		DBManager dbm = new DBManager();
		ResultSet rs = null;
		List<Node> list = new ArrayList<Node>();
		ResultSetHandler<List<Node>> rsh = new BeanListHandler<Node>(Node.class);
		try {
			rs = dbm.executeQuery(sql);
			list = rsh.handle(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
					dbm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * �����ڵ�
	 */
	public boolean save(BaseVo vo) {
		boolean flag = true;
		Node node = (Node) vo;
		String sql = String.format("insert into bpm_node(NAME,`DESC`,PID,ID) values ('%s','%s','%s','%s');", node.getName(), node.getDesc(), node.getPid(), node.getID());
		try {
			conn.executeUpdate(sql);
		} catch (RuntimeException e) {
			flag = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return flag;
	}

	/**
	 * ���½ڵ�
	 */
	public boolean update(BaseVo vo) {
		boolean flag = true;
		Node node = (Node) vo;
		String sql = String.format("update bpm_node set NAME='%s',`DESC`='%s' where ID='%s'", node.getName(), node.getDesc(), node.getID());
		try {
			conn.executeUpdate(sql);
		} catch (RuntimeException e) {
			flag = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return flag;
	}

	/**
	 * ɾ���ڵ�
	 * 
	 * @param ID
	 * @return
	 */
	public boolean delete(String ID) {
		boolean flag = true;
		String sql = String.format("delete from bpm_node where ID = '%s' or PID='%s'", ID, ID);
		String sql2 = String.format("delete from bpm_modeltype where TYPEID='%s'", ID);
		try {
			conn.addBatch(sql);
			conn.addBatch(sql2);
			conn.executeBatch();
		} catch (RuntimeException e) {
			flag = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return flag;
	}

}
