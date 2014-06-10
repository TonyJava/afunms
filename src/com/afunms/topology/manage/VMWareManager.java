package com.afunms.topology.manage;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsDao;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.VMWareConnectConfig;
import com.afunms.polling.om.VMWareVid;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.abstraction.ExcelReport3;
import com.afunms.system.model.User;
import com.afunms.temp.dao.FdbTempDao;
import com.afunms.topology.dao.VMWareConnectConfigDao;
import com.afunms.topology.dao.VMWareVidDao;
import com.afunms.vmware.vim25.vo.VMWareDao;
import com.lowagie.text.DocumentException;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfEntityMetricCSV;
import com.vmware.vim25.PerfMetricSeriesCSV;

@SuppressWarnings("unchecked")
public class VMWareManager extends BaseManager implements ManagerInterface {

	DateE datemanager = new DateE();
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	I_HostCollectData hostmanager = new HostCollectDataManager();
	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("showresource")) {
			return showresource();
		}
		if (action.equals("showsys")) {
			return showsys();
		}
		if (action.equals("showguesthost")) {
			return showguesthost();
		}
		if (action.equals("showcap")) {
			return showcap();
		}
		if (action.equals("vmwareevent")) {
			return vmwareevent();
		}
		if (action.equals("refresh")) {
			return refresh();
		}
		if (action.equals("updateFlag")) {
			return updateFlag();
		}
		if (action.equals("event")) {
			return listEvent();
		}
		if (action.equals("queryevent")) {
			return vmeventQuery();
		}
		if (action.equals("vmeventdelete")) {
			return vmeventdelete();
		}
		if (action.equalsIgnoreCase("eventReportList")) {
			return eventReportList();
		}
		if (action.equalsIgnoreCase("showPingReport")) {
			return showPingReport();
		}
		if (action.equalsIgnoreCase("downloadEventReport")) {
			return downloadEventReport();
		}
		if (action.equalsIgnoreCase("showResourceReport")) {
			return showresourceReport();
		}
		if (action.equalsIgnoreCase("showVmwareReport")) {
			return showVmwareReport();
		}
		if (action.equalsIgnoreCase("downloadAllReport")) {
			return downloadAllReport();
		}
		if (action.equalsIgnoreCase("showPerReport")) {
			return performenceReport();
		}
		return null;
	}

	public String updateFlag() {
		String[] ids = getParaArrayValue("checkbox");
		String nodeid = getParaValue("nodeid");
		VMWareVidDao vm1 = new VMWareVidDao();
		vm1.updateVidFlag(ids, nodeid);
		String menuflag = getParaValue("menuflag");
		String jsp = "/detail/vmware_configuration.jsp?menuflag=" + menuflag;
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		List list = null;
		VMWareVidDao vm = new VMWareVidDao();
		List list_vid = vm.getbynodeid(Long.parseLong(nodeid));
		NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
		try {

			String isDefalut = "-1";
			list = dao.findByNodeIdAndTypeAndSubtype(nodeid, type, subtype, isDefalut);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		request.setAttribute("list_vid", list_vid);
		request.setAttribute("list", list);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);

		return jsp;
	}

	public String vmwareevent() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		String b_time = "";
		String t_time = "";
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}

		EventListDao el = new EventListDao();
		List list = el.loadAll(" where nodeid = " + host.getId() + "  and subtype = 'virtual' and recordtime  <'" + t_time + " 23:59:00' and recordtime > '" + t_time
				+ " 00:00:00'");
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/detail/vmware_event.jsp";
	}

	public String showcap() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		String ip = SysUtil.doip(host.getIpAddress());
		VMWareConnectConfigDao vmdao = new VMWareConnectConfigDao();
		// 在表里查出用户名和密码
		List list = (List) vmdao.getbynodeid(Long.parseLong(tmp));
		vmdao.close();
		VMWareConnectConfig vm_vo = null;
		String pwd = "";
		if (list != null && list.size() > 0) {
			vm_vo = (VMWareConnectConfig) list.get(0);
			pwd = vm_vo.getPwd();
		}
		if (!"".equals(pwd)) {
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Hashtable vmData = new Hashtable();
		vmData = (Hashtable) ShareData.getVmdata().get(host.getIpAddress());
		VMWareDao vm = new VMWareDao();
		VMWareVidDao viddao = new VMWareVidDao();
		List wu_vid = viddao.queryVidFlag("physical", host.getId() + "", "");
		List<HashMap<String, Object>> wulist = vm.getbyvid(wu_vid, "vm_basephysical", host.getIpAddress());
		List ds_vid = viddao.queryVidFlag("datastore", host.getId() + "", "");
		List<HashMap<String, Object>> dslist = vm.getbyvid(ds_vid, "vm_basedatastore", host.getIpAddress());
		List cr_vid = viddao.queryVidFlag("yun", host.getId() + "", "");
		List<HashMap<String, Object>> crlist = vm.getbyvid(cr_vid, "vm_baseyun", host.getIpAddress());
		List rp_vid = viddao.queryVidFlag("resourcepool", host.getId() + "", "");
		List<HashMap<String, Object>> rplist = vm.getbyvid(rp_vid, "vm_baseresource", host.getIpAddress());
		List dc_vid = viddao.queryVidFlag("datacenter", host.getId() + "", "");
		List<HashMap<String, Object>> dclist = vm.getbyvid(dc_vid, "vm_basedatacenter", host.getIpAddress());
		vm.close();
		HashMap<String, Object> map_host = new HashMap<String, Object>();
		int size = 0;
		try {
			if (wulist.size() > 0 && wulist != null) {
				for (int i = 0; i < wulist.size(); i++) {
					List l = new ArrayList();
					HashMap<String, Object> wuliji = (HashMap<String, Object>) ((HashMap<String, Object>) vmData.get("wuliji")).get("wuliji_per");
					PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) wuliji.get(wulist.get(i).get("vid"));
					for (int j = 0; j < 8; j++) {
						String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[j]).getValue().toString().split(",");
						try {
							if (str != null && str.length > 0) {
								if (str.length < 288) {
									size = str.length - 1;
								} else {
									size = 287;
								}
								l.add(str[size]);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					map_host.put(wulist.get(i).get("vid").toString(), l);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("ip", ip);
		request.setAttribute("map_host", map_host);
		request.setAttribute("rplist", rplist);
		request.setAttribute("dslist", dslist);
		request.setAttribute("wulist", wulist);
		request.setAttribute("dclist", dclist);
		request.setAttribute("crlist", crlist);
		return "/detail/vmware_cap.jsp";
	}

	public String showsys() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		VMWareDao vm1 = new VMWareDao();
		VMWareVidDao viddao = new VMWareVidDao();
		double capacity = 0;
		List l = new ArrayList();
		List list_vid = viddao.queryVidFlag("datastore", host.getId() + "", "");
		List<HashMap<String, Object>> ds_list = vm1.getbyvid(list_vid, "vm_basedatastore", host.getIpAddress());
		vm1.close();
		for (int i = 0; i < ds_list.size(); i++) {
			double num = Double.parseDouble(ds_list.get(i).get("capacity").toString().replaceAll(" GB", ""));
			l.add(num);
		}
		for (int j = 0; j < l.size(); j++) {
			capacity += (Double) l.get(j);
		}
		DecimalFormat df = new DecimalFormat("0.00");
		double num = capacity / 1024;
		VMWareDao vm = new VMWareDao();
		String hostnum = vm.querySize(host.getId() + "", "physical");
		String VMnum = vm.querySize(host.getId() + "", "vmware");
		String Poolnum = vm.querySize(host.getId() + "", "yun");
		vm.close();

		request.setAttribute("dslist", ds_list);
		request.setAttribute("hostnum", hostnum);
		request.setAttribute("VMnum", VMnum);
		request.setAttribute("Poolnum", Poolnum);
		request.setAttribute("num", df.format(num));
		return "/detail/vmware_sys.jsp";
	}

	public String showresource() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		VMWareDao vm = new VMWareDao();
		VMWareVidDao viddao = new VMWareVidDao();
		List wu_vid = viddao.queryAllVid("physical", host.getId() + "");
		List<HashMap<String, Object>> wulist = vm.getbyvid(wu_vid, "vm_basephysical", host.getIpAddress());
		List ds_vid = viddao.queryAllVid("datastore", host.getId() + "");
		List<HashMap<String, Object>> dslist = vm.getbyvid(ds_vid, "vm_basedatastore", host.getIpAddress());
		List cr_vid = viddao.queryAllVid("yun", host.getId() + "");
		List<HashMap<String, Object>> crlist = vm.getbyvid(cr_vid, "vm_baseyun", host.getIpAddress());
		List rp_vid = viddao.queryAllVid("resourcepool", host.getId() + "");
		List<HashMap<String, Object>> rplist = vm.getbyvid(rp_vid, "vm_baseresource", host.getIpAddress());
		List dc_vid = viddao.queryAllVid("datacenter", host.getId() + "");
		List<HashMap<String, Object>> dclist = vm.getbyvid(dc_vid, "vm_basedatacenter", host.getIpAddress());
		vm.close();
		request.setAttribute("id", tmp);
		request.setAttribute("rplist", rplist);
		request.setAttribute("dclist", dclist);
		request.setAttribute("crlist", crlist);
		request.setAttribute("dslist", dslist);
		request.setAttribute("wulist", wulist);
		return "/detail/vmware_resource.jsp";
	}

	public String showguesthost() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		VMWareDao vm = new VMWareDao();
		VMWareVidDao viddao = new VMWareVidDao();
		List wu_vid = viddao.queryVidFlag("physical", host.getId() + "", "");
		List<HashMap<String, Object>> wulist = vm.getbyvid(wu_vid, "vm_basephysical", host.getIpAddress());
		List vm_vid = viddao.queryAllVid("vmware", host.getId() + "");
		List<HashMap<String, Object>> vmlist = vm.getbyvid(vm_vid, "vm_basevmware", host.getIpAddress());
		vm.close();
		request.setAttribute("wulist", wulist);
		request.setAttribute("vmlist", vmlist);
		return "/detail/vmware_guesthost.jsp";
	}

	public String list() {
		String menuflag = getParaValue("menuflag");
		String jsp = "/detail/vmware_configuration.jsp?menuflag=" + menuflag;
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		List list = null;
		VMWareVidDao vm = new VMWareVidDao();
		List<VMWareVid> list_vid = vm.getbynodeid1(Long.parseLong(nodeid));
		vm.close();
		NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
		try {
			String isDefalut = "-1";
			list = dao.findByNodeIdAndTypeAndSubtype(nodeid, type, subtype, isDefalut);
			dao.close();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		request.setAttribute("list_vid", list_vid);
		request.setAttribute("list", list);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		return jsp;
	}

	public String refresh() {
		FdbTempDao dao = new FdbTempDao();
		dao.refresh();
		dao.close();
		return list();
	}

	public HashMap getAvg(String ip, String vid) throws Exception {
		HashMap result = new HashMap();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip;
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb
						.append(" select round(avg(h.cpu),2) as cpu,round(avg(h.cpuuse),2) as cpuuse,round(avg(h.mem),2) as mem,round(avg(h.memin),2) as memin,round(avg(h.memout),2) as memout,round(avg(h.disk),2) as disk,round(avg(h.net),2) as net from vm_guesthost"
								+ sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb
						.append(" select avg(h.cpu) as cpu,avg(h.cpuuse) as cpuuse,avg(h.mem) as mem,avg(h.memin) as memin,avg(h.memout) as memout,avg(h.disk) as disk,avg(h.net) as net from vm_guesthost"
								+ sid + " h where ");
			}

			sb.append(" h.vid='");
			sb.append(vid);
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and date_format(h.collecttime,'%Y-%m-%d') = date_format(now(),'%Y-%m-%d')");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= to_date('");
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			rs = dbmanager.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					String cpu = rs.getString("cpu");
					result.put("cpu", cpu);
					String cpuuse = rs.getString("cpuuse");
					result.put("cpuuse", cpuuse);
					String memin = rs.getString("memin");
					result.put("memin", memin);
					String memout = rs.getString("memout");
					result.put("memout", memout);
					String mem = rs.getString("mem");
					result.put("mem", mem);
					String disk = rs.getString("disk");
					result.put("disk", disk);
					String net = rs.getString("net");
					result.put("net", net);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	public HashMap getMax(String ip, String vid) throws Exception {
		HashMap result = new HashMap();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip;
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb
						.append(" select max(h.cpu) as cpu,max(h.cpuuse) as cpuuse,max(h.mem) as mem,max(h.memin) as memin,max(h.memout) as memout,max(h.disk) as disk,max(h.net) as net from vm_guesthost"
								+ sid + " h where ");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb
						.append(" select max(h.cpu) as cpu,max(h.cpuuse) as cpuuse,max(h.mem) as mem,max(h.memin) as memin,max(h.memout) as memout,max(h.disk) as disk,max(h.net) as net from vm_guesthost"
								+ sid + " h where ");
			}

			sb.append(" h.vid='");
			sb.append(vid);
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and date_format(h.collecttime,'%Y-%m-%d') = date_format(now(),'%Y-%m-%d')");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= to_date('");
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			rs = dbmanager.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					String cpu = rs.getString("cpu");
					result.put("cpu", cpu);
					String cpuuse = rs.getString("cpuuse");
					result.put("cpuuse", cpuuse);
					String memin = rs.getString("memin");
					result.put("memin", memin);
					String memout = rs.getString("memout");
					result.put("memout", memout);
					String mem = rs.getString("mem");
					result.put("mem", mem);
					String disk = rs.getString("disk");
					result.put("disk", disk);
					String net = rs.getString("net");
					result.put("net", net);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	public HashMap getAvgOther(String ip, String vid, String flag) throws Exception {
		HashMap result = new HashMap();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip.replace(".", "_");
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			if (flag.equals("cr")) {
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb
							.append(" select round(avg(h.cpu),0) as cpu,round(avg(h.cputotal),0) as cputotal,round(avg(h.mem),0) as mem,round(avg(h.memtotal),0) as memtotal from vm_cluster"
									+ sid + " h where ");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select avg(h.cpu) as cpu,avg(h.cputotal) as cputotal,avg(h.mem) as mem,avg(h.memtotal) as memtotal from vm_cluster" + sid + " h where ");
				}

			} else if (flag.equals("rp")) {
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select round(avg(h.cpu),0) as cpu from vm_resourcepool" + sid + " h where ");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select avg(h.cpu) as cpu from vm_resourcepool" + sid + " h where ");
				}
			}
			sb.append(" h.vid='");
			sb.append(vid);
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and date_format(h.collecttime,'%Y-%m-%d') = date_format(now(),'%Y-%m-%d')");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= to_date('");
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();
			rs = dbmanager.executeQuery(sql);
			if (rs != null) {
				if (flag.equals("cr")) {
					while (rs.next()) {
						String cpu = rs.getString("cpu");
						result.put("cpu", cpu);
						String cputotal = rs.getString("cputotal");
						result.put("cputotal", cputotal);
						String memtotal = rs.getString("memtotal");
						result.put("memtotal", memtotal);
						String mem = rs.getString("mem");
						result.put("mem", mem);
					}
				} else if (flag.equals("rp")) {
					while (rs.next()) {
						String cpu = rs.getString("cpu");
						result.put("cpu", cpu);
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	public HashMap getMaxOther(String ip, String vid, String flag) throws Exception {
		HashMap result = new HashMap();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String sid = ip.replace(".", "_");
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			if (flag.equals("cr")) {

				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select max(h.cpu) as cpu,max(h.cputotal) as cputotal,max(h.mem) as mem,max(h.memtotal) as memtotal from vm_cluster" + sid + " h where ");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select max(h.cpu) as cpu,max(h.cputotal) as cputotal,max(h.mem) as mem,max(h.memtotal) as memtotal from vm_cluster" + sid + " h where ");
				}

			} else if (flag.equals("rp")) {
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select max(h.cpu) as cpu from vm_resourcepool" + sid + " h where ");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append(" select max(h.cpu) as cpu from vm_resourcepool" + sid + " h where ");
				}
			}
			sb.append(" h.vid='");
			sb.append(vid);
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and date_format(h.collecttime,'%Y-%m-%d') = date_format(now(),'%Y-%m-%d')");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sb.append("' and h.collecttime >= to_date('");
				sb.append("','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('");
				sb.append("','YYYY-MM-DD HH24:MI:SS')");
			}

			sql = sb.toString();

			rs = dbmanager.executeQuery(sql);
			if (rs != null) {
				if (flag.equals("cr")) {
					while (rs.next()) {
						String cpu = rs.getString("cpu");
						result.put("cpu", cpu);
						String cputotal = rs.getString("cputotal");
						result.put("cputotal", cputotal);
						String memtotal = rs.getString("memtotal");
						result.put("memtotal", memtotal);
						String mem = rs.getString("mem");
						result.put("mem", mem);
					}
				} else if (flag.equals("rp")) {
					while (rs.next()) {
						String cpu = rs.getString("cpu");
						result.put("cpu", cpu);
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return result;
	}

	public String listEvent() {
		String type = request.getParameter("type");
		String subtype = request.getParameter("subtype");
		String jsp = "/alarm/indicators/list.jsp";
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		try {
			List list = alarmIndicatorsDao.getByTypeAndSubType(type, subtype);

			request.setAttribute("list", list);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsp;
	}

	private String vmeventQuery() {
		Vector vector = new Vector();
		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		try {

			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();

			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), host.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector", vector);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/detail/vmware_event.jsp";
	}

	private String vmeventdelete() {

		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			EventListDao edao = new EventListDao();
			edao.delete(ids);
			edao.close();

		}

		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventListDao dao = new EventListDao();
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		request.setAttribute("level1", level1);

		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");

		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = "";
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
			if (SystemConstant.DBType.equalsIgnoreCase("mysql")) {
				s.append("where recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "'");
			} else if (SystemConstant.DBType.equalsIgnoreCase("oracle")) {
				s.append("where recordtime>=to_date( '" + starttime1 + "','yyyy-mm-dd hh24:mi:ss') " + "and recordtime<=to_date('" + totime1 + "','yyyy-mm-dd hh24:mi:ss')");
			}
			if (!"99".equals(level1 + "")) {
				s.append(" and level1=" + level1);
			}
			if (!"99".equals(status + "")) {
				s.append(" and managesign=" + status);
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if (businessid != null) {
				if (businessid != "-1") {
					String[] bids = businessid.split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								if (flag == 0) {
									s.append(" and ( businessid = '," + bids[i].trim() + ",' ");
									flag = 1;
								} else {
									s.append(" or businessid = '," + bids[i].trim() + ",' ");
								}
							}
						}
						s.append(") ");
					}

				}
			}
			sql = s.toString();
			sql = sql + " order by id desc";
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);

		setTarget("/vmware.do?action=vmwareevent");
		return list(dao, sql);
	}

	private String showPingReport() {
		Date d = new Date();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String startdate = getParaValue("startdate");
		Hashtable reporthash = new Hashtable();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String newip = "";
		String ip = "";
		Integer queryid = getParaIntValue("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(queryid);

		try {
			ip = host.getIpAddress();
			newip = SysUtil.doip(ip);

			VMWareConnectConfigDao vmware = new VMWareConnectConfigDao();

			Hashtable ConnectUtilizationhash = vmware.getPingDataById(newip, queryid, starttime, totime);
			vmware.close();
			String curPing = "";
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";

			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
			}

			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType, ConnectUtilizationhash, "连通率", newip + "vmwarePingConnect", 740, 150);
			reporthash.put("servicename", host.getAlias());
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", totime);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "tftp");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/vmware/showPingReport.jsp";
	}

	public String eventReportList() {
		Vector vector = new Vector();
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";

		try {
			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), Integer.parseInt(tmp), "vmware");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("vector", vector);
		request.setAttribute("id", Integer.parseInt(tmp));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/vmware/eventReportList.jsp";
	}

	// event 报表
	private String downloadEventReport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String id = request.getParameter("id");

		Hashtable reporthash = new Hashtable();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		String ip = host.getIpAddress();
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
		s.append(" and nodeid=" + host.getId());

		List infolist = eventdao.findByCriteria(s.toString());
		reporthash.put("eventlist", infolist);

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/VMWareEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventDoc(fileName, starttime, totime, "VMWare(" + ip + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/VMWareEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_TomcatEventExc(file, id, starttime, totime, "VMWare(" + ip + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/VMWareEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_midEventPdf(fileName, starttime, totime, "VMWare(" + ip + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/service/download.jsp";
	}

	private String downloadAllReport() {
		Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
		ExcelReport3 report = new ExcelReport3(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		if ("resource".equalsIgnoreCase(type)) {
			if ("0".equals(str)) {
				// excel综合报表
				report.createReportxls_vmware_resource("temp/VMWareResourceReport.xls", id);
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(), reporthash);
				try {
					String file = "temp/VMWareResourceReport.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReportDoc_vmware_resource(fileName, "doc", id);// word综合报表
					request.setAttribute("filename", fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(), reporthash);
				try {
					String file = "temp/VMWareResourceReport.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReportDoc_vmware_resource(fileName, "pdf", id);
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if ("vmware".equalsIgnoreCase(type)) {
			if ("0".equals(str)) {
				// excel综合报表
				report.createReportxls_vmware_vm("temp/VMWareReport.xls", id);
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(), reporthash);
				try {
					String file = "temp/VMWareReport.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReportDoc_vmware_vm(fileName, "doc", id);// word综合报表
					request.setAttribute("filename", fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(), reporthash);
				try {
					String file = "temp/VMWareReport.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReportDoc_vmware_vm(fileName, "pdf", id);
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if ("performance".equalsIgnoreCase(type)) {
			if ("0".equals(str)) {
				// excel综合报表
				report.createReportxls_vmware_performance("temp/VMWarePerformanceReport.xls", id);
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(), reporthash);
				try {
					String file = "temp/VMWarePerformanceReport.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReportDoc_vmware_performance(fileName, "doc", id);// word综合报表
					request.setAttribute("filename", fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("2".equals(str)) {
				ExcelReport3 report1 = new ExcelReport3(new IpResourceReport(), reporthash);
				try {
					String file = "temp/VMWarePerformanceReport.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReportDoc_vmware_performance(fileName, "pdf", id);
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "/capreport/service/download.jsp";

	}

	public String showresourceReport() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		VMWareConnectConfig vm_vo = null;
		VMWareDao vm = new VMWareDao();
		VMWareVidDao viddao = new VMWareVidDao();
		List wu_vid = viddao.queryAllVid("physical", host.getId() + "");
		List<HashMap<String, Object>> wulist = vm.getbyvid(wu_vid, "vm_basephysical", host.getIpAddress());
		List ds_vid = viddao.queryAllVid("datastore", host.getId() + "");
		List<HashMap<String, Object>> dslist = vm.getbyvid(ds_vid, "vm_basedatastore", host.getIpAddress());
		List cr_vid = viddao.queryAllVid("yun", host.getId() + "");
		List<HashMap<String, Object>> crlist = vm.getbyvid(cr_vid, "vm_baseyun", host.getIpAddress());
		List rp_vid = viddao.queryAllVid("resourcepool", host.getId() + "");
		List<HashMap<String, Object>> rplist = vm.getbyvid(rp_vid, "vm_baseresource", host.getIpAddress());
		List dc_vid = viddao.queryAllVid("datacenter", host.getId() + "");
		List<HashMap<String, Object>> dclist = vm.getbyvid(dc_vid, "vm_basedatacenter", host.getIpAddress());

		Hashtable reporthash = new Hashtable();
		reporthash.put("servicename", host.getAlias());
		reporthash.put("wulist", wulist);
		reporthash.put("ip", host.getIpAddress());
		reporthash.put("dslist", dslist);
		reporthash.put("crlist", crlist);
		reporthash.put("rplist", rplist);
		reporthash.put("dclist", dclist);
		session.setAttribute("reporthash", reporthash);

		request.setAttribute("id", tmp);
		request.setAttribute("vo", vm_vo);
		request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("rplist", rplist);
		request.setAttribute("dclist", dclist);
		request.setAttribute("crlist", crlist);
		request.setAttribute("dslist", dslist);
		request.setAttribute("wulist", wulist);

		return "/application/vmware/resourceReport.jsp";
	}

	public String showVmwareReport() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		VMWareConnectConfig vm_vo = null;
		VMWareDao vm = new VMWareDao();
		VMWareVidDao viddao = new VMWareVidDao();
		List wu_vid = viddao.queryAllVid("physical", host.getId() + "");
		List<HashMap<String, Object>> wulist = vm.getbyvid(wu_vid, "vm_basephysical", host.getIpAddress());
		List vm_vid = viddao.queryAllVid("vmware", host.getId() + "");
		List<HashMap<String, Object>> vmlist = vm.getbyvid(vm_vid, "vm_basevmware", host.getIpAddress());
		vm.close();
		Hashtable wutable = new Hashtable();
		for (int j = 0; j < wulist.size(); j++) {
			wutable.put(wulist.get(j).get("vid").toString(), wulist.get(j).get("name").toString());
		}

		Hashtable reporthash = new Hashtable();
		reporthash.put("servicename", host.getAlias());
		reporthash.put("wulist", wulist);
		reporthash.put("ip", host.getIpAddress());
		reporthash.put("vmlist", vmlist);
		reporthash.put("wutable", wutable);
		session.setAttribute("reporthash", reporthash);

		request.setAttribute("id", tmp);
		request.setAttribute("vo", vm_vo);
		request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("vmlist", vmlist);
		request.setAttribute("wutable", wutable);
		return "/application/vmware/vmwareReport.jsp";
	}

	public String performenceReport() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		VMWareConnectConfigDao vmdao = new VMWareConnectConfigDao();
		List list = (List) vmdao.getbynodeid(Long.parseLong(tmp));
		vmdao.close();
		VMWareConnectConfig vm_vo = null;
		if (list != null && list.size() > 0) {
			vm_vo = (VMWareConnectConfig) list.get(0);
		} else {
			vm_vo = new VMWareConnectConfig();
		}
		Hashtable vmData = new Hashtable();
		vmData = (Hashtable) ShareData.getVmdata().get(host.getIpAddress());

		DecimalFormat df = new DecimalFormat("0.0");
		int size = 0;
		// 物理机名称
		VMWareDao vm1 = new VMWareDao();
		VMWareVidDao viddao = new VMWareVidDao();
		List wu_vid = viddao.queryAllVid("physical", host.getId() + "");
		List<HashMap<String, Object>> wulist = vm1.getbyvid(wu_vid, "vm_basephysical", host.getIpAddress());
		Hashtable wutable = new Hashtable();
		for (int j = 0; j < wulist.size(); j++) {
			wutable.put(wulist.get(j).get("vid").toString(), wulist.get(j).get("name").toString());
		}
		viddao.close();
		vm1.close();
		// 虚拟机名称
		VMWareDao vm2 = new VMWareDao();
		VMWareVidDao viddao1 = new VMWareVidDao();
		List vm_vid = viddao1.queryAllVid("vmware", host.getId() + "");
		List<HashMap<String, Object>> vmlist = vm2.getbyvid(vm_vid, "vm_basevmware", host.getIpAddress());
		Hashtable vmtable = new Hashtable();
		for (int j = 0; j < vmlist.size(); j++) {
			vmtable.put(vmlist.get(j).get("vid").toString(), vmlist.get(j).get("name").toString());
		}

		viddao1.close();
		vm2.close();
		VMWareVidDao wudao = new VMWareVidDao();
		List wu_list = wudao.queryVidFlag("physical", host.getId() + "", "");
		wudao.close();
		// 存放物理机的数据的list
		List physical = new ArrayList();

		if (wu_list != null && wu_list.size() > 0) {
			HashMap<String, Object> wuliji = (HashMap<String, Object>) ((HashMap<String, Object>) vmData.get("wuliji")).get("wuliji_per");
			for (int i = 0; i < wu_list.size(); i++) {
				List l = new ArrayList();
				List t = new ArrayList();
				PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) wuliji.get(wu_list.get(i));
				for (int j = 0; j < 8; j++) {
					String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[j]).getValue().toString().split(",");
					if (str != null && str.length > 0) {
						if (str.length < 288) {
							size = str.length - 1;
						} else {
							size = 287;
						}
						l.add(str[size]);
					}
				}
				t.add(0, wutable.get(wu_list.get(i)));
				t.add(1, (df.format(Long.parseLong((String) l.get(0)) / 100) + "%"));
				t.add(2, l.get(2));
				t.add(3, (l.get(4) + "/" + l.get(3)));
				t.add(4, l.get(5));
				t.add(5, (df.format(Long.parseLong((String) l.get(6)) / 100) + "%"));
				t.add(6, (l.get(7)));
				physical.add(t);
			}
		}

		// 虚拟机性能列表
		List vmware_list = new ArrayList();
		HashMap<String, Object> vm = (HashMap<String, Object>) ((HashMap<String, Object>) vmData.get("vmware"));
		VMWareVidDao vmdao1 = new VMWareVidDao();
		List vmwarelist = vmdao1.queryVidFlag("vmware", host.getId() + "", "vmware");
		if (vmwarelist != null && vmwarelist.size() > 0) {
			VMWareVidDao dao = new VMWareVidDao();
			for (int j = 0; j < vmwarelist.size(); j++) {
				List vm_list = new ArrayList();
				List l_vm = new ArrayList();
				PerfEntityMetricBase[] perfEntityMetricBases = (PerfEntityMetricBase[]) vm.get(vmwarelist.get(j).toString());
				for (int i = 0; i < 9; i++) {
					try {
						String[] str = ((PerfMetricSeriesCSV) ((PerfEntityMetricCSV) perfEntityMetricBases[0]).getValue()[i]).getValue().toString().split(",");
						if (str != null && str.length > 0) {
							if (str.length < 288) {
								size = str.length - 1;
							} else {
								size = 287;
							}
							l_vm.add(str[size]);
						}
					} catch (NullPointerException e) {
						l_vm.add("0");
					}
				}
				String hoid = dao.queryPhysicalHoid(tmp, vmwarelist.get(j).toString());
				String physicalName = (String) wutable.get(hoid);
				String cpu = df.format(Long.parseLong((String) l_vm.get(0)) / 100);
				String cpuuse = l_vm.get(5) + "";
				String meminc = l_vm.get(2) + "";
				String memin = l_vm.get(4) + "";
				String memout = l_vm.get(3) + "";
				String mem = df.format(Long.parseLong((String) l_vm.get(6)) / 100);
				String disk = l_vm.get(7) + "";
				String net = l_vm.get(8) + "";
				String vmname = (String) vmtable.get(vmwarelist.get(j).toString());
				vm_list.add(0, physicalName);
				vm_list.add(1, cpu);
				vm_list.add(2, cpuuse);
				vm_list.add(3, meminc);
				vm_list.add(4, memin);
				vm_list.add(5, memout);
				vm_list.add(6, mem);
				vm_list.add(7, disk);
				vm_list.add(8, net);
				vm_list.add(9, vmname);
				vmware_list.add(vm_list);
			}
			dao.close();
		}
		// 资源池
		List resource_list = new ArrayList();
		List l = new ArrayList();
		VMWareVidDao wudao1 = new VMWareVidDao();
		List rp_list = wudao1.queryVidFlag("resourcepool", host.getId() + "", "");
		wudao1.close();
		if (rp_list != null && rp_list.size() > 0) {
			HashMap<String, Object> wuliji = (HashMap<String, Object>) ((HashMap<String, Object>) vmData.get("rp"));
			VMWareVidDao dao = new VMWareVidDao();
			String name = "";
			for (int i = 0; i < rp_list.size(); i++) {
				List t = new ArrayList();
				try {
					l = (List) wuliji.get(rp_list.get(i));
				} catch (Exception e) {

				}
				String value = "0";
				try {
					value = l.get(0) + "";
				} catch (Exception e) {
					e.printStackTrace();
				}
				name = dao.queryName(tmp, rp_list.get(i).toString());
				t.add(0, name);
				t.add(1, value);
				resource_list.add(t);
			}
			dao.close();
		}

		// 存储

		List datacenter = new ArrayList();
		VMWareVidDao dsdao = new VMWareVidDao();
		List vm_list = dsdao.queryVidFlag("datastore", host.getId() + "", "");
		dsdao.close();
		List l1 = new ArrayList();
		if (vm_list != null && vm_list.size() > 0) {
			HashMap<String, Object> wuliji = (HashMap<String, Object>) ((HashMap<String, Object>) vmData.get("ds"));
			VMWareVidDao dao = new VMWareVidDao();
			String name = "";

			for (int i = 0; i < vm_list.size(); i++) {
				List t = new ArrayList();
				l1 = (List) wuliji.get(vm_list.get(i));
				name = dao.queryName(tmp, vm_list.get(i).toString());
				t.add(0, name);
				try {
					t.add(1, (df.format(Double.parseDouble((String) l1.get(1)) / (1024 * 1024)) + "GB"));
					t.add(2, (df.format(Double.parseDouble((String) l1.get(0)) / (1024 * 1024)) + "GB"));
					t.add(3, (df.format(Double.parseDouble((String) l1.get(2)) / (1024 * 1024)) + "GB"));
					t.add(4, (df.format(Long.parseLong((String) l1.get(0)) * 100 / Long.parseLong((String) l1.get(2))) + "%"));
					datacenter.add(t);
				} catch (Exception e) {
					t.add(0, name);
					t.add(1, 0 + "GB");
					t.add(2, 0 + "GB");
					t.add(3, 0 + "GB");
					t.add(4, 0 + "%");
					datacenter.add(t);
				}
			}

			dao.close();
		}
		// 集群
		List yun_list = new ArrayList();
		List l3 = new ArrayList();
		VMWareVidDao yundao = new VMWareVidDao();
		List yun = yundao.queryVidFlag("yun", host.getId() + "", "");
		yundao.close();
		if (yun != null && yun.size() > 0) {
			HashMap<String, Object> wuliji = (HashMap<String, Object>) ((HashMap<String, Object>) vmData.get("cr"));
			VMWareVidDao dao = new VMWareVidDao();
			String name = "";
			for (int i = 0; i < yun.size(); i++) {
				List t = new ArrayList();
				l3 = (List) wuliji.get(yun.get(i));
				name = dao.queryName(tmp, yun.get(i).toString());
				t.add(0, name);
				try {
					t.add(1, l3.get(0));
				} catch (Exception e) {
					t.add(1, "0");
				}
				try {
					t.add(2, l3.get(1));
				} catch (Exception e) {
					t.add(2, "0");
				}
				try {
					t.add(3, l3.get(2));
				} catch (Exception e) {
					t.add(3, "0");
				}
				try {
					t.add(4, l3.get(3));
				} catch (Exception e) {
					t.add(4, "0");
				}

				yun_list.add(t);
			}
			dao.close();
		}

		Hashtable reporthash = new Hashtable();
		reporthash.put("servicename", host.getAlias());
		reporthash.put("physical", physical);
		reporthash.put("ip", host.getIpAddress());
		reporthash.put("vmlist", vmware_list);
		reporthash.put("crlist", yun_list);
		reporthash.put("rplist", resource_list);
		reporthash.put("dslist", datacenter);
		session.setAttribute("reporthash", reporthash);

		request.setAttribute("id", tmp);
		request.setAttribute("vo", vm_vo);
		request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("vmlist", vmware_list);
		request.setAttribute("dslist", datacenter);
		request.setAttribute("crlist", yun_list);
		request.setAttribute("rplist", resource_list);
		request.setAttribute("physical", physical);

		return "/application/vmware/perReport.jsp";
	}
}
