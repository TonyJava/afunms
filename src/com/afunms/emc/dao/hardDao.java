package com.afunms.emc.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.emc.model.Crus;
import com.afunms.emc.model.HardCrus;
import com.afunms.ip.stationtype.model.alltype;

@SuppressWarnings("unchecked")
public class hardDao extends BaseDao implements DaoInterface {

	public hardDao() {
		super("nms_emchard");
	}

	public void delete(String nodeid) {
		try {
			conn.executeUpdate("delete from nms_emchard where nodeid ='" + nodeid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Crus hard = new Crus();
		try {
			hard.setNodeid(rs.getString("nodeid"));
			hard.setName(rs.getString("name"));
			hard.setSpStateStr(rs.getString("spstatestr"));
			hard.setPowerState(rs.getString("powerstate"));
			hard.setBusLCC(rs.getString("buslcc"));
			hard.setBussps(rs.getString("bussps"));
			hard.setBusCabling(rs.getString("buscabling"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hard = null;
		}
		return hard;
	}

	public Crus query(String nodeid) {
		Crus hard = new Crus();
		String queryonesql = "select * from nms_emchard where nodeid='" + nodeid + "'";
		try {
			rs = conn.executeQuery(queryonesql);
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				hard = (Crus) loadFromRS(rs);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return hard;
	}

	public List<Crus> queryList(String nodeid) {
		List list = new ArrayList();
		Crus hard = new Crus();
		String queryonesql = "select * from nms_emchard where nodeid='" + nodeid + "'";
		try {
			rs = conn.executeQuery(queryonesql);
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				hard = (Crus) loadFromRS(rs);
				list.add(hard);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean save(List<HardCrus> list, String nodeid) {
		if (list != null && list.size() > 0) {
			try {
				StringBuffer addsql = new StringBuffer(100);
				for (int i = 0; i < list.size(); i++) {
					addsql = new StringBuffer(100);
					HardCrus hard = list.get(i);
					addsql.append("insert into nms_emchard(nodeid,name,spstatestr,powerstate,bussps,buslcc,buscabling)values('");
					addsql.append(nodeid);
					addsql.append("','");
					addsql.append(hard.getName());
					addsql.append("','");
					addsql.append(hard.getSpState());
					addsql.append("','");
					addsql.append(hard.getPowerState());
					addsql.append("','");
					addsql.append(hard.getSpsState());
					addsql.append("','");
					addsql.append(hard.getLccState());
					addsql.append("','");
					addsql.append(hard.getSpsCablingState());
					addsql.append("')");
					System.out.println(addsql.toString());
					conn.addBatch(addsql.toString());
				}
				conn.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
		}
		return true;
	}

	public boolean saveCZ(BaseVo baseVo) {
		alltype vo = (alltype) baseVo;

		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into ip_alltype (backbone_name,loopback_begin,loopback_end,pe_begin,pe_end,pe_ce_begin,pe_ce_end,bus_begin,bus_end) values(");
		sql.append("'");
		sql.append(vo.getBackbone_name());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getLoopback_begin());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getLoopback_end());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getPe_begin());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getPe_end());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getPe_ce_begin());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getPe_ce_end());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getBus_begin());
		sql.append("',");
		sql.append("'");
		sql.append(vo.getBus_end());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
