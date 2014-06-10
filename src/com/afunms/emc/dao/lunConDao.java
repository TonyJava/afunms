package com.afunms.emc.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.emc.model.Lun;

@SuppressWarnings("unchecked")
public class lunConDao extends BaseDao implements DaoInterface {

	public lunConDao() {
		super("nms_emcluncon");
	}

	public void delete(String nodeid) {
		try {
			conn.executeUpdate("delete from nms_emcluncon where nodeid ='" + nodeid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	// 根据vid查出基础表的信息
	public List<HashMap<String, Object>> getbyvid(List vid, String tablename, String ipaddress) {
		String ip = ipaddress.replace(".", "_");
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < vid.size(); i++) {
			String queryonesql = "select * from " + tablename + ip + " where vid='" + vid.get(i) + "'";
			HashMap map = new HashMap();
			try {
				rs = conn.executeQuery(queryonesql);
				while (rs.next()) {
					// 虚拟机基础信息
					if (tablename.equalsIgnoreCase("vm_basevmware")) {
						String id = rs.getString("vid");
						map.put("vid", id);
						String name = rs.getString("name");
						map.put("name", name);
						String vmpower = rs.getString("vmpower");
						map.put("powerstate", vmpower);
						String fullname = rs.getString("fullname");
						map.put("guestfullname", fullname);
						String cpu = rs.getString("cpu");
						map.put("cpu", cpu);
						String memoryuse = rs.getString("memoryuse");
						map.put("memorysizemb", memoryuse);
						String hoid = rs.getString("hoid");
						map.put("hoid", hoid);
					} else if (tablename.equalsIgnoreCase("vm_basephysical")) {// 物理机基础信息
						String id = rs.getString("vid");
						map.put("vid", id);
						String name = rs.getString("name");
						map.put("name", name);
						String hostpower = rs.getString("hostpower");
						map.put("powerstate", hostpower);
						String model = rs.getString("model");
						map.put("model", model);
						String cpunum = rs.getString("cpunum");
						map.put("numcore", cpunum);
						String netnum = rs.getString("netnum");
						map.put("numnics", netnum);
						String memory = rs.getString("memory");
						map.put("memorysizemb", memory);
						String ghz = rs.getString("ghz");
						map.put("cpumhz", ghz);
					} else if (tablename.equalsIgnoreCase("vm_baseyun")) {// 云资源基础信息
						String id = rs.getString("vid");
						map.put("vid", id);
						String name = rs.getString("name");
						map.put("name", name);
						String disk = rs.getString("disk");
						map.put("totaldssizemb", disk);
						String cpuuse = rs.getString("cpuuse");
						map.put("totalcpu", cpuuse);
						String hostnum = rs.getString("hostnum");
						map.put("numhosts", hostnum);
						String mem = rs.getString("mem");
						map.put("totalmemory", mem);
						String cpunum = rs.getString("cpunum");
						map.put("numcpucores", cpunum);
					} else if (tablename.equalsIgnoreCase("vm_basedatastore")) {// 存储基础信息
						String id = rs.getString("vid");
						map.put("vid", id);
						String name = rs.getString("name");
						map.put("name", name);
						String store = rs.getString("store");
						map.put("capacity", store);
						String unusedstore = rs.getString("unusedstore");
						map.put("freespace", unusedstore);
					} else if (tablename.equalsIgnoreCase("vm_basedatacenter")) {// 数据中心基础信息
						String id = rs.getString("vid");
						map.put("vid", id);
						String name = rs.getString("name");
						map.put("name", name);
						String dcid = rs.getString("dcid");
						map.put("dcid", dcid);
					} else if (tablename.equalsIgnoreCase("vm_baseresource")) {// 资源池基础信息
						String id = rs.getString("vid");
						map.put("vid", id);
						String name = rs.getString("name");
						map.put("name", name);
						String dcid = rs.getString("dcid");
						map.put("dcid", dcid);
						String crid = rs.getString("crid");
						map.put("crid", crid);
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			list.add(map);
		}
		return list;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Lun lun = new Lun();
		try {
			lun.setNodeid(rs.getString("nodeid"));
			lun.setName(rs.getString("name"));
			lun.setRAIDType(rs.getString("raidtype"));
			lun.setRAIDGroupID(rs.getString("raidgroupid"));
			lun.setLUNCapacity(rs.getString("luncapacity"));
			lun.setCurrentOwner(rs.getString("currentowner"));
			lun.setDefaultOwner(rs.getString("defaultowner"));
			lun.setWritecache(rs.getString("writecache"));
			lun.setReadcache(rs.getString("readcache"));
			lun.setPrctRebuilt(rs.getString("prctrebuilt"));
			lun.setPrctBound(rs.getString("prctbound"));
			lun.setDisklistStr(rs.getString("disklist"));
		} catch (Exception e) {
			e.printStackTrace();
			lun = null;
		}
		return lun;
	}

	public List<Lun> query(String nodeid) {
		List<Lun> list = new ArrayList<Lun>();
		String queryonesql = "select * from nms_emcluncon where nodeid='" + nodeid + "'";
		try {
			rs = conn.executeQuery(queryonesql);
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				list.add((Lun) loadFromRS(rs));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}

	// 取性能表里的最后一条记录
	public HashMap queryLast(String tablename, String ipp, String vid) {
		HashMap map = new HashMap();
		String sql = "";
		String ip = ipp.replace(".", "_");
		if (tablename.equalsIgnoreCase("vm_host")) {
			sql = "select cpu,cpuuse,mem,memin,memout,disk,meminc from vm_host" + ip + " where hostid='" + vid + "' group by collecttime desc";
			try {
				rs = conn.executeQuery(sql);
				if (rs.next()) {
					map.put("cpu", rs.getString("cpu"));
					map.put("cpuuse", rs.getString("cpuuse"));
					map.put("mem", rs.getString("mem"));
					map.put("memin", rs.getString("mem"));
					map.put("memout", rs.getString("memout"));
					map.put("disk", rs.getString("disk"));
					map.put("meminc", rs.getString("meminc"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("query cdp vmwareconnectconfig error");
				e.printStackTrace();
			}
		} else if (tablename.equalsIgnoreCase("vm_guesthost")) {
			sql = "select cpu,cpuuse,mem,memin,memout,disk,meminc,net from vm_guesthost" + ip + " where vid='" + vid + "' group by collecttime desc";
			try {
				rs = conn.executeQuery(sql);
				if (rs.next()) {
					map.put("cpu", rs.getString("cpu"));
					map.put("cpuuse", rs.getString("cpuuse"));
					map.put("mem", rs.getString("mem"));
					map.put("memin", rs.getString("mem"));
					map.put("memout", rs.getString("memout"));
					map.put("disk", rs.getString("disk"));
					map.put("meminc", rs.getString("meminc"));
					map.put("net", rs.getString("net"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else if (tablename.equalsIgnoreCase("vm_cluster")) {
			sql = "select cpu,mem from vm_cluster" + ip + " where vid='" + vid + "' group by collecttime desc";
			try {
				rs = conn.executeQuery(sql);
				if (rs.next()) {
					map.put("cpu", rs.getString("cpu"));
					map.put("mem", rs.getString("mem"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else if (tablename.equalsIgnoreCase("vm_resourcepool")) {
			sql = "select cpu from vm_resourcepool" + ip + " where vid='" + vid + "' group by collecttime desc";
			try {
				rs = conn.executeQuery(sql);
				if (rs.next()) {
					map.put("cpu", rs.getString("cpu"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return map;
	}

	public String querySize(String nodeid, String category) {
		String num = "";
		String queryonesql = "select count(*) as num from nms_vmwarevid where category='" + category + "'  and nodeid=" + nodeid;
		try {
			rs = conn.executeQuery(queryonesql);
			while (rs.next()) {
				num = rs.getString("num");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return num;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean save(List sql) {
		// TODO Auto-generated method stub
		if (sql != null && sql.size() > 0) {
			try {
				for (int i = 0; i < sql.size(); i++) {
					conn.addBatch(sql.get(i) + "");
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

	public boolean save(String sql) {
		return saveOrUpdate(sql);
	}

	public boolean saveList(List<Lun> list, String nodeid) {
		if (list != null && list.size() > 0) {
			try {
				StringBuffer addsql = new StringBuffer(100);
				Lun lun = null;
				for (int i = 0; i < list.size(); i++) {
					addsql = new StringBuffer(100);
					lun = (Lun) list.get(i);
					if (lun.getName() == null || "null".equalsIgnoreCase(lun.getName())) {
						continue;
					}
					StringBuffer disklistStr = new StringBuffer(200);
					for (int j = 0; j < lun.getDisksList().size(); j++) {
						disklistStr.append(lun.getDisksList().get(j) + ";");
					}
					addsql.append("insert into nms_emcluncon(nodeid,name,raidtype,raidgroupid,luncapacity,currentowner,defaultowner,writecache,"
							+ "readcache,prctrebuilt,prctbound,disklist)values('");
					addsql.append(nodeid);
					addsql.append("','");
					addsql.append(lun.getName());
					addsql.append("','");
					addsql.append(lun.getRAIDType());
					addsql.append("','");
					addsql.append(lun.getRAIDGroupID());
					addsql.append("','");
					addsql.append(lun.getLUNCapacity());
					addsql.append("','");
					addsql.append(lun.getCurrentOwner());
					addsql.append("','");
					addsql.append(lun.getDefaultOwner());
					addsql.append("','");
					addsql.append(lun.getWritecache());
					addsql.append("','");
					addsql.append(lun.getReadcache());
					addsql.append("','");
					addsql.append(lun.getPrctRebuilt());
					addsql.append("','");
					addsql.append(lun.getPrctBound());
					addsql.append("','");
					addsql.append(disklistStr.toString());
					addsql.append("')");

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

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
