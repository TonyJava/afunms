package com.afunms.ipaccounting.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.ipaccounting.model.IpAccountingBase;

@SuppressWarnings("unchecked")
public class IpAccountingDao extends BaseDao {

	public IpAccountingDao() {
		super("nms_ipaccountingdetail");
	}

	public boolean save(BaseVo baseVo) {
		IpAccountingBase vo = (IpAccountingBase) baseVo;
		StringBuffer sql = new StringBuffer(100);
		vo.setId(getNextID());
		sql.append("insert into nms_ipaccountingdetail(id,nodeid,srcip,destip,protocol)values(");
		sql.append(vo.getId());
		sql.append(",");
		sql.append(vo.getNodeid());
		sql.append(",'");
		sql.append(vo.getSrcip());
		sql.append("','");
		sql.append(vo.getDestip());
		sql.append("','");
		sql.append(vo.getProtocol());
		sql.append("')");
		try {
			conn.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public List getNetFLow() {
		IpAccountingBaseDao base = new IpAccountingBaseDao();
		List baselist = new ArrayList();
		try {
			baselist = base.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			base.close();
		}
		Hashtable baseHash = new Hashtable();
		if (baselist != null && baselist.size() > 0) {
			for (int i = 0; i < baselist.size(); i++) {
				IpAccountingBase vo = (IpAccountingBase) baselist.get(i);
				baseHash.put(vo.getId(), vo);
			}
		}
		List list = new ArrayList();
		String sql = "select sum(h.byts) as thevalue,h.baseid as baseid from  nms_ipaccountingdetail h group by h.baseid";
		try {
			rs = conn.executeQuery(sql);
			Vector vec = new Vector();
			while (rs.next()) {
				vec = new Vector();
				vec.add(0, rs.getDouble("thevalue") / 1000);
				int baseid = rs.getInt("baseid");
				if (baseHash != null && baseHash.containsKey(baseid)) {
					IpAccountingBase vo = (IpAccountingBase) baseHash.get(baseid);
					vec.add(1, vo);
				} else {
					vec.add(1, null);
				}

				list.add(vec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();

		}
		return list;
	}

	/**
	 * 根据节点ID获取其流量信息
	 * 
	 * @param nodeid
	 * @return
	 */
	public List getNetFLow(String nodeid, String starttime, String totime, String srcip) {
		IpAccountingBaseDao base = new IpAccountingBaseDao();
		List baselist = new ArrayList();
		try {
			baselist = base.findByCondition(" where nodeid = '" + nodeid + "' and srcip like '%" + srcip + "%'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			base.close();
		}
		Hashtable baseHash = new Hashtable();
		if (baselist != null && baselist.size() > 0) {
			for (int i = 0; i < baselist.size(); i++) {
				IpAccountingBase vo = (IpAccountingBase) baselist.get(i);
				baseHash.put(vo.getId(), vo);
			}
		}
		if (baseHash.isEmpty()) {
			return null;
		}
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("select sum(h.byts) as thevalue,h.baseid as baseid from  nms_ipaccountingdetail h where collecttime between '");
			sql.append(starttime);
			sql.append("' and '");
			sql.append(totime);
			sql.append("'");
			sql.append(" group by h.baseid");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("select sum(h.byts) as thevalue,h.baseid as baseid from  nms_ipaccountingdetail h where collecttime >= to_date('" + starttime
					+ "','YYYY-MM-DD HH24:MI:SS') and collecttime <= to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS') group by h.baseid");
		}

		try {
			rs = conn.executeQuery(sql.toString());
			Vector vec = new Vector();
			while (rs.next()) {
				vec = new Vector();
				double thevalue = rs.getDouble("thevalue") / 1000;
				thevalue = Double.valueOf(CommonUtil.format(thevalue, 3));
				vec.add(0, thevalue);
				int baseid = rs.getInt("baseid");
				if (baseHash != null && baseHash.containsKey(baseid)) {
					IpAccountingBase vo = (IpAccountingBase) baseHash.get(baseid);
					vec.add(1, vo);
				} else {
					vec.add(1, null);
				}

				list.add(vec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
		}
		return list;
	}

	/**
	 * 获取详细流速信息
	 * 
	 * @param nodeid
	 * @param srcip
	 * @return
	 */
	public List getNetFLowDetail(String nodeid, String srcip, String startdate, String todate) {
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select ipsd.id, sum(ipsd.byts) thevalue, ipsd.baseid, ips.srcip, ips.destip, ipsd.collecttime");
		sql.append(" from nms_ipaccountingdetail ipsd, nms_ipaccountips ips");
		sql.append(" where ipsd.baseid = ips.id ");
		sql.append(" and ips.srcip = '");
		sql.append(srcip);
		sql.append("' and ips.nodeid = '");
		sql.append(nodeid);

		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("' and ipsd.collecttime between '");
			sql.append(startdate);
			sql.append("' and '");
			sql.append(todate);
			sql.append("' group by destip");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("' and ipsd.collecttime >= to_date('" + startdate + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + todate
					+ "','YYYY-MM-DD HH24:MI:SS') group by destip");
		}

		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				double thevalue = rs.getDouble("thevalue") / 1000;
				String destip = rs.getString("destip");
				Vector vec = new Vector();
				vec.add(0, srcip);
				vec.add(1, destip);
				vec.add(2, CommonUtil.format(thevalue, 3));
				list.add(vec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return list;
	}

	/**
	 * 获取流量数据集合
	 * 
	 * @param nodeid
	 * @param startdate
	 * @param todate
	 * @return
	 */
	public List getNetFlowLineData(String nodeid, String startdate, String todate) {
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select ips.srcip,ipsd.collecttime, sum(ipsd.byts) thevalue");
		sql.append(" from nms_ipaccountingdetail ipsd, nms_ipaccountips ips");
		sql.append(" where ipsd.baseid = ips.id and ips.nodeid = '");
		sql.append(nodeid);
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("' and ipsd.collecttime between '");
			sql.append(startdate);
			sql.append("' and '");
			sql.append(todate);
			sql.append("' group by ips.srcip,collecttime  order by collecttime");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("' and ipsd.collecttime >= to_date('" + startdate + "','YYYY-MM-DD HH24:MI:SS')  and ipsd.collecttime <= to_date('" + todate
					+ "','YYYY-MM-DD HH24:MI:SS') group by ipsd.id,ips.srcip,collecttime  order by collecttime");
		}

		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				Vector vec = new Vector();
				double thevalue = rs.getDouble("thevalue") / 1000;
				vec.add(0, rs.getString("srcip"));// 源地址
				vec.add(1, CommonUtil.format(thevalue, 3));// 流量大小 KBytes
				vec.add(2, rs.getString("collecttime"));// 采集时间
				list.add(vec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return list;
	}

	public boolean save(List list) {
		boolean result = false;
		try {
			for (int i = 0; i < list.size(); i++) {
				IpAccountingBase vo = (IpAccountingBase) list.get(i);
				StringBuffer sql = new StringBuffer(100);
				sql.append("insert into nms_ipaccountingdetail(id,nodeid,srcip,destip,protocol)values(");
				sql.append(getNextID());
				sql.append(",");
				sql.append(vo.getNodeid());
				sql.append(",'");
				sql.append(vo.getSrcip());
				sql.append("','");
				sql.append(vo.getDestip());
				sql.append("','");
				sql.append(vo.getProtocol());
				sql.append("')");
				conn.addBatch(sql.toString());
			}
			conn.executeBatch();
			return true;
		} catch (Exception ex) {
			conn.rollback();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean delete(String[] nodeid) {
		boolean result = false;
		try {
			for (int i = 0; i < nodeid.length; i++) {
				conn.addBatch("delete from nms_ipaccountingdetail where nodeid=" + nodeid[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		IpAccountingBase vo = new IpAccountingBase();
		try {
			vo.setId(rs.getInt("id"));
			vo.setNodeid(rs.getInt("nodeid"));
			vo.setSrcip(rs.getString("srcip"));
			vo.setDestip(rs.getString("destip"));
			vo.setProtocol(rs.getString("protocol"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

}
