package com.afunms.ipresource.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.JspPage;
import com.afunms.ipresource.model.IpResource;


@SuppressWarnings("unchecked")
public class IpResourceDao extends BaseDao {
	public IpResourceDao() {
		super("ip_resource");
	}

	public IpResource find(String key, String value) {
		String sql = "select * from ip_resource where " + key + "='" + value + "'";
		System.out.println("sql==" + sql);
		List list = findByCriteria(sql);
		if (list != null && list.size() > 0) {
			return (IpResource) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * ��ҳ��ʾ,ÿҳ�г�25����¼
	 */
	public List listByPage(int curpage) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select count(*) from ip_resource");
			if (rs.next()) {
				jspPage = new JspPage(25, curpage, rs.getInt(1));
			}
			rs = conn.executeQuery("select * from ip_resource order by ip_long");
			int loop = 0;
			while (rs.next()) {
				loop++;
				if (loop < jspPage.getMinNum()) {
					continue;
				}
				list.add(loadFromRS(rs));
				if (loop == jspPage.getMaxNum()) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	/**
	 * �����ip���յ�ipѡ������������м�¼
	 * 
	 * @param beginip
	 *            ���ip
	 * @param endip
	 *            long �յ�ip
	 */
	public Hashtable loadByIPRange(long beginip, long endip) {
		Hashtable voHash = new Hashtable();
		try {
			// �������ڵ�ѡ�������õ�ip,��Ϊ�����õ�ip�ǲ�������,������hashtable�ȽϷ���
			String sql = "select * from ip_resource where ip_long>=" + beginip + " and ip_long<=" + endip + " order by ip_long";
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				voHash.put(new Long(rs.getLong("ip_long")), loadFromRS(rs));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return voHash;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		IpResource vo = new IpResource();
		try {
			vo.setNode(rs.getString("node"));
			vo.setMac(rs.getString("mac"));
			vo.setIfDescr(rs.getString("if_descr"));
			vo.setIfIndex(rs.getString("if_index"));
			vo.setIpAddress(rs.getString("ip_address"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}


	public void update(List<IpResource> list) {
		try {
			conn.executeUpdate("delete from ip_resource");
			int id = 1;
			for (IpResource ipr : list) {
				conn.addBatch("insert into ip_resource(id,ip_address,ip_long,mac,if_index,if_descr,node)values(" + id + ",'" + ipr.getIpAddress() + "','" + ipr.getIpLong() + "','"
						+ ipr.getMac() + "','" + ipr.getIfIndex() + "','" + ipr.getIfDescr() + "','" + ipr.getNode() + "')");
				id++;
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

	}
}