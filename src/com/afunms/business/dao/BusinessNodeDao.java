package com.afunms.business.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.business.model.BusinessNode;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;


@SuppressWarnings("unchecked")
public class BusinessNodeDao extends BaseDao implements DaoInterface {

	public BusinessNodeDao() {
		super("nms_businessnode");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		BusinessNode vo = new BusinessNode();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBid(rs.getInt("bid"));
			vo.setName(rs.getString("name"));
			vo.setDesc(rs.getString("bn_desc"));
			vo.setCollecttype(rs.getInt("collecttype"));
			vo.setFlag(rs.getInt("flag"));
			vo.setMethod(rs.getString("method"));
		}

		catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	/**
	 * 列出所有方法
	 */
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_businessnode order by id");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public List findByBid(String bid) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_businessnode where bid=" + bid + " order by id");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	/**
	 * 修改方法
	 */
	public boolean update(BaseVo baseVo) {
		boolean flag = true;
		BusinessNode vo = (BusinessNode) baseVo;
		BusinessNode pvo = (BusinessNode) findByID(vo.getId() + "");
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_businessnode set bid =");
		sql.append(vo.getBid());
		sql.append(",name='");
		sql.append(vo.getName());
		sql.append("',bn_desc='");
		sql.append(vo.getDesc());
		sql.append("',collecttype=");
		sql.append(vo.getCollecttype());
		sql.append(",flag=");
		sql.append(vo.getFlag());
		sql.append(",method='");
		sql.append(vo.getMethod());
		sql.append("' where id=" + vo.getId());
		System.out.println(sql);
		String allipstr = pvo.getBid() + "_" + pvo.getId();
		try {
			saveOrUpdate(sql.toString());
			if (!String.valueOf(vo.getBid()).equals(String.valueOf(pvo.getBid()))) {
				CreateTableManager ctable = new CreateTableManager();
				try {
					ctable.deleteTable("bnode", allipstr, "bnode");// Ping
					ctable.deleteTable("bnodehour", allipstr, "bnodehour");// Ping
					ctable.deleteTable("bnodeday", allipstr, "bnodeday");// Ping
				} catch (Exception e) {
					e.printStackTrace();
				} 
				allipstr = "";
				String newallipstr = vo.getBid() + "_" + vo.getId();
				try {
					ctable.createBNodeTable(conn, "bnode", newallipstr, "bnode");// Ping
					ctable.createBNodeTable(conn, "bnodehour", newallipstr, "bnodehour");// Ping
					ctable.createBNodeTable(conn, "bnodeday", newallipstr, "bnodeday");// Ping
				} catch (Exception e) {
					e.printStackTrace();
				}  
			}

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;

	}

	/**
	 * 添加方法
	 */
	public boolean save(BaseVo basevo) {
		boolean flag = true;
		BusinessNode vo = (BusinessNode) basevo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_businessnode(id,bid,name,flag,bn_desc,collecttype,method)values(");
		sql.append(vo.getId());
		sql.append(",");
		sql.append(vo.getBid());
		sql.append(",'");
		sql.append(vo.getName());
		sql.append("',");
		sql.append(vo.getFlag());
		sql.append(",'");
		sql.append(vo.getDesc());
		sql.append("',");
		sql.append(vo.getCollecttype());
		sql.append(",'");
		sql.append(vo.getMethod());
		sql.append("')");
		String allipstr = vo.getBid() + "_" + vo.getId();
		try {
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			conn = new DBManager();
			try {
				ctable.createBNodeTable(conn, "bnode", allipstr, "bnode");// Ping
				ctable.createBNodeTable(conn, "bnodehour", allipstr, "bnodehour");// Ping
				ctable.createBNodeTable(conn, "bnodeday", allipstr, "bnodeday");// Ping
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 根据id删除这条记录
	 * 
	 * @param id
	 * @return
	 */
	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from nms_businessnode where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public List findByCondition(String view, String value) {
		return findByCriteria("select * from nms_businessnode where name like '%" + value + "%' and bid = " + view + "");

	}

	public List getBussinessByFlag(int flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_businessnode where flag = " + flag);
		return findByCriteria(sql.toString());
	}
}
