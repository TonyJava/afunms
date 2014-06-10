package com.afunms.vmware.vim25.vo;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;

@SuppressWarnings("unchecked")
public class VMWareDao extends BaseDao {

	public boolean save(String sql) {
		return saveOrUpdate(sql);
	}

	public boolean save(List sql) {
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

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		VMWareVO vmware = new VMWareVO();
		try {
			vmware.setId(rs.getLong("id"));
			vmware.setHoid(rs.getString("hoid"));
			vmware.setVid(rs.getString("vid"));
			vmware.setCpu(rs.getString("cpu"));
			vmware.setCpunum(rs.getString("cpunum"));
			vmware.setCpuuse(rs.getString("cpuuse"));
			vmware.setCrid(rs.getString("crid"));
			vmware.setDcid(rs.getString("dcid"));
			vmware.setDisk(rs.getString("disk"));
			vmware.setFullname(rs.getString("fullname"));
			vmware.setGhz(rs.getString("ghz"));
			vmware.setHostnum(rs.getString("hostnum"));
			vmware.setHostpower(rs.getString("hostpower"));
			vmware.setMem(rs.getString("mem"));
			vmware.setMemory(rs.getString("memory"));
			vmware.setMemoryuse(rs.getString("memoryuse"));
			vmware.setModel(rs.getString("model"));
			vmware.setName(rs.getString("name"));
			vmware.setNetnum(rs.getString("netnum"));
			vmware.setStore(rs.getString("store"));
			vmware.setVmpower(rs.getString("vmpower"));
			vmware.setUnusedstore(rs.getString("unusedstore"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vmware;
	}

	public boolean savePhysical(BaseVo vo, String ip) {
		VMWareVO vmware = (VMWareVO) vo;
		String ipall = ip.replace(".", "_");
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into vm_basephysical" + ipall + "(name,vid,model,cpunum,netnum,memory,ghz,hostpower)values('");
		addsql.append(vmware.getName());
		addsql.append("','");
		addsql.append(vmware.getVid());
		addsql.append("','");
		addsql.append(vmware.getModel());
		addsql.append("','");
		addsql.append(vmware.getCpunum());
		addsql.append("','");
		addsql.append(vmware.getNetnum());
		addsql.append("','");
		addsql.append(vmware.getMemory());
		addsql.append("','");
		addsql.append(vmware.getGhz());
		addsql.append("','");
		addsql.append(vmware.getHostpower());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}

	public boolean savePhysical(List vidList, String ip) {
		String ipall = ip.replace(".", "_");
		if (vidList != null && vidList.size() > 0) {
			try {
				StringBuffer addsql = new StringBuffer(100);
				for (int i = 0; i < vidList.size(); i++) {
					addsql = new StringBuffer(100);
					VMWareVO vmware = (VMWareVO) vidList.get(i);
					addsql.append("insert into vm_basephysical" + ipall + "(name,vid,model,cpunum,netnum,memory,ghz,hostpower)values('");
					addsql.append(vmware.getName());
					addsql.append("','");
					addsql.append(vmware.getVid());
					addsql.append("','");
					addsql.append(vmware.getModel());
					addsql.append("','");
					addsql.append(vmware.getCpunum());
					addsql.append("','");
					addsql.append(vmware.getNetnum());
					addsql.append("','");
					addsql.append(vmware.getMemory());
					addsql.append("','");
					addsql.append(vmware.getGhz());
					addsql.append("','");
					addsql.append(vmware.getHostpower());
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

	public boolean saveVmware(BaseVo vo, String ip) {
		VMWareVO vmware = (VMWareVO) vo;
		String ipall = ip.replace(".", "_");
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into vm_basevmware" + ipall + "(name,vid,cpu,memoryuse,fullname,hoid,vmpower)values('");
		addsql.append(vmware.getName());
		addsql.append("','");
		addsql.append(vmware.getVid());
		addsql.append("','");
		addsql.append(vmware.getCpu());
		addsql.append("','");
		addsql.append(vmware.getMemoryuse());
		addsql.append("','");
		addsql.append(vmware.getFullname());
		addsql.append("','");
		addsql.append(vmware.getHoid());
		addsql.append("','");
		addsql.append(vmware.getVmpower());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}

	public boolean saveVmware(List vidList, String ip) {
		String ipall = ip.replace(".", "_");
		if (vidList != null && vidList.size() > 0) {
			try {
				StringBuffer addsql = new StringBuffer(100);
				for (int i = 0; i < vidList.size(); i++) {
					addsql = new StringBuffer(100);
					VMWareVO vmware = (VMWareVO) vidList.get(i);
					addsql.append("insert into vm_basevmware" + ipall + "(name,vid,cpu,memoryuse,fullname,hoid,vmpower)values('");
					addsql.append(vmware.getName());
					addsql.append("','");
					addsql.append(vmware.getVid());
					addsql.append("','");
					addsql.append(vmware.getCpu());
					addsql.append("','");
					addsql.append(vmware.getMemoryuse());
					addsql.append("','");
					addsql.append(vmware.getFullname());
					addsql.append("','");
					addsql.append(vmware.getHoid());
					addsql.append("','");
					addsql.append(vmware.getVmpower());
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

	public boolean saveYun(BaseVo vo, String ip) {
		VMWareVO vmware = (VMWareVO) vo;
		String ipall = ip.replace(".", "_");
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into vm_baseyun" + ipall + "(name,vid,cpuuse,hostnum,mem,cpunum)values('");
		addsql.append(vmware.getName());
		addsql.append("','");
		addsql.append(vmware.getVid());
		addsql.append("','");
		addsql.append(vmware.getCpuuse());
		addsql.append("','");
		addsql.append(vmware.getHostnum());
		addsql.append("','");
		addsql.append(vmware.getMem());
		addsql.append("','");
		addsql.append(vmware.getCpunum());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}

	public boolean saveYun(List vidList, String ip) {
		String ipall = ip.replace(".", "_");
		if (vidList != null && vidList.size() > 0) {
			try {
				StringBuffer addsql = new StringBuffer(100);
				for (int i = 0; i < vidList.size(); i++) {
					addsql = new StringBuffer(100);
					VMWareVO vmware = (VMWareVO) vidList.get(i);
					addsql.append("insert into vm_baseyun" + ipall + "(name,vid,cpuuse,hostnum,mem,cpunum,disk)values('");
					addsql.append(vmware.getName());
					addsql.append("','");
					addsql.append(vmware.getVid());
					addsql.append("','");
					addsql.append(vmware.getCpuuse());
					addsql.append("','");
					addsql.append(vmware.getHostnum());
					addsql.append("','");
					addsql.append(vmware.getMem());
					addsql.append("','");
					addsql.append(vmware.getCpunum());
					addsql.append("','");
					addsql.append(vmware.getDisk());
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

	public boolean saveDatastore(BaseVo vo, String ip) {
		VMWareVO vmware = (VMWareVO) vo;
		String ipall = ip.replace(".", "_");
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into vm_basedatastore" + ipall + "(name,vid,store,unusedstore)values('");
		addsql.append(vmware.getName());
		addsql.append("','");
		addsql.append(vmware.getVid());
		addsql.append("','");
		addsql.append(vmware.getStore());
		addsql.append("','");
		addsql.append(vmware.getUnusedstore());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}

	public boolean saveDatastore(List vidList, String ip) {
		String ipall = ip.replace(".", "_");
		if (vidList != null && vidList.size() > 0) {
			try {
				StringBuffer addsql = new StringBuffer(100);
				for (int i = 0; i < vidList.size(); i++) {
					addsql = new StringBuffer(100);
					VMWareVO vmware = (VMWareVO) vidList.get(i);
					addsql.append("insert into vm_basedatastore" + ipall + "(name,vid,store,unusedstore)values('");
					addsql.append(vmware.getName());
					addsql.append("','");
					addsql.append(vmware.getVid());
					addsql.append("','");
					addsql.append(vmware.getStore());
					addsql.append("','");
					addsql.append(vmware.getUnusedstore());
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

	public boolean saveResourcepool(BaseVo vo, String ip) {
		VMWareVO vmware = (VMWareVO) vo;
		String ipall = ip.replace(".", "_");
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into vm_baseresouce" + ipall + "(name,vid,dcid,crid)values('");
		addsql.append(vmware.getName());
		addsql.append("','");
		addsql.append(vmware.getVid());
		addsql.append("','");
		addsql.append(vmware.getDcid());
		addsql.append("','");
		addsql.append(vmware.getCrid());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}

	public boolean saveResourcepool(List vidList, String ip) {
		String ipall = ip.replace(".", "_");
		if (vidList != null && vidList.size() > 0) {
			try {
				StringBuffer addsql = new StringBuffer(100);
				for (int i = 0; i < vidList.size(); i++) {
					addsql = new StringBuffer(100);
					VMWareVO vmware = (VMWareVO) vidList.get(i);
					addsql.append("insert into vm_baseresource" + ipall + "(name,vid,dcid,crid)values('");
					addsql.append(vmware.getName());
					addsql.append("','");
					addsql.append(vmware.getVid());
					addsql.append("','");
					addsql.append(vmware.getDcid());
					addsql.append("','");
					addsql.append(vmware.getCrid());
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

	public boolean saveDatacenter(BaseVo vo, String ip) {
		VMWareVO vmware = (VMWareVO) vo;
		String ipall = ip.replace(".", "_");
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into vm_basedatacenter" + ipall + "(name,vid,dcid)values('");
		addsql.append(vmware.getName());
		addsql.append("','");
		addsql.append(vmware.getVid());
		addsql.append("','");
		addsql.append(vmware.getDcid());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}

	public boolean saveDatacenter(List vidList, String ip) {
		String ipall = ip.replace(".", "_");
		if (vidList != null && vidList.size() > 0) {
			try {
				StringBuffer addsql = new StringBuffer(100);
				for (int i = 0; i < vidList.size(); i++) {
					addsql = new StringBuffer(100);
					VMWareVO vmware = (VMWareVO) vidList.get(i);
					addsql.append("insert into vm_basedatacenter" + ipall + "(name,vid,dcid)values('");
					addsql.append(vmware.getName());
					addsql.append("','");
					addsql.append(vmware.getVid());
					addsql.append("','");
					addsql.append(vmware.getDcid());
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

	// 根据vid查出基础表的信息
	public List<HashMap<String, Object>> getbyvid(List vid, String tablename, String ipaddress) {
		String ip = ipaddress.replace(".", "_");
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < vid.size(); i++) {
			String queryonesql = "select * from " + tablename + ip + " where vid='" + vid.get(i) + "'";
			HashMap map = new HashMap();
			DecimalFormat formater = new DecimalFormat("#0.##");
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
						if (disk != null) {
							int diskInt = Integer.parseInt(disk);
							disk = formater.format(diskInt / 1024f);
						}
						map.put("totaldssizemb", disk);
						String cpuuse = rs.getString("cpuuse");
						map.put("totalcpu", cpuuse);
						String hostnum = rs.getString("hostnum");
						map.put("numhosts", hostnum);
						String mem = rs.getString("mem");
						if (mem != null) {
							int memInt = Integer.parseInt(mem);
							mem = formater.format(memInt / 1024f);
						}
						map.put("totalmemory", mem);
						String cpunum = rs.getString("cpunum");
						map.put("numcpucores", cpunum);
					} else if (tablename.equalsIgnoreCase("vm_basedatastore")) {// 存储基础信息
						String id = rs.getString("vid");
						map.put("vid", id);
						String name = rs.getString("name");
						map.put("name", name);
						String store = rs.getString("store");
						String unusedstore = rs.getString("unusedstore");
						int usedstore = 0;
						String usedstoreg = "";
						String used = "";
						if (store != null && unusedstore != null) {
							float usedPer = 0f;
							int capacityInt = Integer.parseInt(store);
							int freespace = Integer.parseInt(unusedstore);
							usedstore = capacityInt - freespace;
							usedPer = usedstore * 100f / capacityInt;
							used = formater.format(usedPer);
							usedstoreg = formater.format(usedstore / 1024f);
							unusedstore = formater.format(freespace / 1024f);
						}
						if (store != null) {
							int capacityInt = Integer.parseInt(store);
							store = formater.format(capacityInt / 1024f);
						}

						map.put("capacity", store);
						map.put("freespace", unusedstore);
						map.put("usedstore", usedstoreg);
						map.put("userPer", used);
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
				e.printStackTrace();
			}
			list.add(map);
		}
		return list;
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
			System.out.println("query cdp vmwareconnectconfig error");
			e.printStackTrace();
		}
		return num;
	}

	public HashMap<String, String> queryAllSize(String category) {
		String queryonesql = "select count(*) as num,nodeid from nms_vmwarevid where category='" + category + "' group by nodeid";
		HashMap hm = new HashMap();
		String num = "";
		int nodeid = 0;
		try {
			rs = conn.executeQuery(queryonesql);
			if (rs != null) {
				while (rs.next()) {
					num = rs.getString("num");
					nodeid = rs.getInt("nodeid");
					hm.put(nodeid + "", num + "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
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
				e.printStackTrace();
			}
		}
		return map;
	}

}
