package com.afunms.polling.snmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.Procs;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.ProcessCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.polling.om.UtilHdx;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostDatatempProcessRtTosql;
import com.gatherResulttosql.HostDatatempUserRtosql;
import com.gatherResulttosql.HostDatatempiflistRtosql;
import com.gatherResulttosql.HostDatatempinterfaceRtosql;
import com.gatherResulttosql.HostDatatemputilhdxRtosql;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.HostdiskResultosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;

@SuppressWarnings("unchecked")
public class LoadHpUnixFile {

	private String ipaddress;

	private Hashtable sendeddata = ShareData.getProcsendeddata();

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public LoadHpUnixFile() {

	}

	public LoadHpUnixFile(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {

		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		ipaddress = host.getIpAddress();

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipaddress);
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}

		Hashtable returnHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		Vector cpuVector = new Vector();
		Vector systemVector = new Vector();
		Vector userVector = new Vector();
		Vector diskVector = new Vector();
		Vector processVector = new Vector();
		// 修改
		Vector interfaceVector = new Vector();
		Vector utilhdxVector = new Vector();

		CpuCollectEntity cpudata = null;
		SystemCollectEntity systemdata = null;
		Usercollectdata userdata = null;
		ProcessCollectEntity processdata = null;

		try {
			String filename = ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipaddress + ".log";
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String strLine = null;
			// 读入文件内容
			while ((strLine = br.readLine()) != null) {
				fileContent.append(strLine + "\n");
			}
			isr.close();
			fis.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Pattern tmpPt = null;
		Matcher mr = null;
		Calendar date = Calendar.getInstance();

		try {
			String uptimeContent = "";
			tmpPt = Pattern.compile("(cmdbegin:uptime)(.*)(cmdbegin:netstat)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				uptimeContent = mr.group(2);
			}
			if (uptimeContent != null && uptimeContent.length() > 0) {
				systemdata = new SystemCollectEntity();
				systemdata.setIpaddress(ipaddress);
				systemdata.setCollecttime(date);
				systemdata.setCategory("System");
				systemdata.setEntity("SysUptime");
				systemdata.setSubentity("SysUptime");
				systemdata.setRestype("static");
				systemdata.setUnit(" ");
				systemdata.setThevalue(uptimeContent.trim());
				systemVector.addElement(systemdata);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String dateContent = "";
			tmpPt = Pattern.compile("(cmdbegin:date)(.*)(cmdbegin:uptime)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				dateContent = mr.group(2);
			}
			if (dateContent != null && dateContent.length() > 0) {
				systemdata = new SystemCollectEntity();
				systemdata.setIpaddress(ipaddress);
				systemdata.setCollecttime(date);
				systemdata.setCategory("System");
				systemdata.setEntity("Systime");
				systemdata.setSubentity("Systime");
				systemdata.setRestype("static");
				systemdata.setUnit(" ");
				systemdata.setThevalue(dateContent.trim());
				systemVector.addElement(systemdata);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		List iflist = null;
		try {
			iflist = new ArrayList();
			List oldiflist = new ArrayList();
			String netstatContent = "";
			tmpPt = Pattern.compile("(cmdbegin:netstat)(.*)(cmdbegin:end)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				netstatContent = mr.group(2);
			}
			String[] netstatLineArr = netstatContent.trim().split("\n");
			String[] netstat_tmpData = null;
			if (ipAllData != null) {
				oldiflist = (List) ipAllData.get("iflist");
			}

			if (netstatLineArr != null && netstatLineArr.length > 0) {
				Interfacecollectdata interfacedata = null;
				// 开始循环网络接口
				for (int k = 1; k < netstatLineArr.length; k++) {

					String portDesc = ""; // Name
					String mtu = ""; // Mtu
					String network = ""; // Network 未使用
					String address = ""; // Address 未使用
					String inPackets = ""; // Ipkts
					String ierrs = ""; // Ierrs 未使用
					String outPackets = ""; // Opkts
					String oerrs = ""; // Oerrs 未使用
					String coll = ""; // Coll 未使用

					netstat_tmpData = netstatLineArr[k].trim().split("\\s++");

					if (netstat_tmpData != null && netstat_tmpData.length >= 9) {
						portDesc = netstat_tmpData[0].trim();
						mtu = netstat_tmpData[1].trim();
						network = netstat_tmpData[2].trim();
						address = netstat_tmpData[3].trim();
						inPackets = netstat_tmpData[4].trim();
						ierrs = netstat_tmpData[5].trim();
						outPackets = netstat_tmpData[6].trim();
						oerrs = netstat_tmpData[7].trim();
						coll = netstat_tmpData[8].trim();

						Hashtable ifhash = new Hashtable();
						Hashtable oldifhash = null;// 用来保存上次采集结果

						boolean hasOldFlag = false;
						if (oldiflist != null && oldiflist.size() > 0) {
							for (int j = 0; j < oldiflist.size(); j++) {
								Hashtable oldifhash_per = (Hashtable) oldiflist.get(j);
								if (portDesc.equals(oldifhash_per.get("portDesc"))) {
									oldifhash = oldifhash_per;
									hasOldFlag = true;
								}
							}
						}
						if (!hasOldFlag) {
							oldifhash = new Hashtable();
							oldifhash.put("portDesc", portDesc);
							oldifhash.put("mtu", mtu);
							oldifhash.put("network", network);
							oldifhash.put("address", address);
							oldifhash.put("inPackets", inPackets);
							oldifhash.put("ierrs", ierrs);
							oldifhash.put("outPackets", outPackets);
							oldifhash.put("oerrs", oerrs);
							oldifhash.put("coll", coll);
						}

						ifhash.put("portDesc", portDesc);
						ifhash.put("mtu", mtu);
						ifhash.put("network", network);
						ifhash.put("address", address);
						ifhash.put("inPackets", inPackets);
						ifhash.put("ierrs", ierrs);
						ifhash.put("outPackets", outPackets);
						ifhash.put("oerrs", oerrs);
						ifhash.put("coll", coll);

						String oldOutPackets = "0";
						String oldInPackets = "0";

						String endOutBytes = "0";
						String endInBytes = "0";

						if (oldifhash != null && oldifhash.size() > 0) {
							if (oldifhash.containsKey("outPackets")) {
								oldOutPackets = (String) oldifhash.get("outPackets");
							}
							try {
								endOutBytes = (Long.parseLong(outPackets) - Long.parseLong(oldOutPackets)) / 1024 / 300 + "";
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (oldifhash.containsKey("inPackets")) {
								oldInPackets = (String) oldifhash.get("inPackets");
							}
							try {
								endInBytes = (Long.parseLong(inPackets) - Long.parseLong(oldInPackets)) / 1024 / 300 + "";
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						// 端口索引
						interfacedata = new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("index");
						interfacedata.setSubentity(k + "");
						// 端口状态不保存，只作为静态数据放到临时表里
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(k + "");
						interfacedata.setChname("端口索引");
						interfaceVector.addElement(interfacedata);
						// 端口描述
						interfacedata = new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifDescr");
						interfacedata.setSubentity(k + "");
						// 端口状态不保存，只作为静态数据放到临时表里
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(portDesc);
						interfacedata.setChname("端口描述2");
						interfaceVector.addElement(interfacedata);

						// 端口名称
						interfacedata = new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifname");
						interfacedata.setSubentity(k + "");
						// 端口状态不保存，只作为静态数据放到临时表里
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(portDesc);
						interfacedata.setChname("端口描述2");
						interfaceVector.addElement(interfacedata);

						// 端口带宽
						interfacedata = new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifSpeed");
						interfacedata.setSubentity(k + "");
						interfacedata.setRestype("static");
						interfacedata.setUnit("每秒字节数");
						interfacedata.setThevalue(mtu);
						interfacedata.setChname("");
						interfaceVector.addElement(interfacedata);
						// 当前状态
						interfacedata = new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifOperStatus");
						interfacedata.setSubentity(k + "");
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue("up");
						interfacedata.setChname("当前状态");
						interfaceVector.addElement(interfacedata);
						// 当前状态
						interfacedata = new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifOperStatus");
						interfacedata.setSubentity(k + "");
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(1 + "");
						interfacedata.setChname("当前状态");
						interfaceVector.addElement(interfacedata);
						// 端口入口流速
						UtilHdx utilhdx = new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setEntity("InBandwidthUtilHdx");
						utilhdx.setThevalue(endInBytes);
						utilhdx.setSubentity(k + "");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("Kb/秒");
						utilhdx.setChname(k + "端口入口" + "流速");
						utilhdxVector.addElement(utilhdx);

						// 端口出口流速
						utilhdx = new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setEntity("OutBandwidthUtilHdx");
						utilhdx.setThevalue(endOutBytes);
						utilhdx.setSubentity(k + "");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("Kb/秒");
						utilhdx.setChname(k + "端口出口" + "流速");
						utilhdxVector.addElement(utilhdx);

						iflist.add(ifhash);
						ifhash = null;

					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// ----------------解析user内容--创建监控项---------------------
		try {
			String userContent = "";
			tmpPt = Pattern.compile("(cmdbegin:memoryend)(.*)(cmdbegin:user)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				userContent = mr.group(2);
			}
			String[] userLineArr = userContent.split("\n");
			for (int i = 0; i < userLineArr.length; i++) {
				String[] result = userLineArr[i].trim().split(":");
				if (result.length > 0) {
					userdata = new Usercollectdata();
					userdata.setIpaddress(ipaddress);
					userdata.setCollecttime(date);
					userdata.setCategory("User");
					userdata.setEntity("Sysuser");
					userdata.setSubentity(result[0]);
					userdata.setRestype("static");
					userdata.setUnit(" ");
					userdata.setThevalue(result[0]);
					userVector.addElement(userdata);
					continue;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			String unameContent = "";
			tmpPt = Pattern.compile("(cmdbegin:uname)(.*)(cmdbegin:date)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				unameContent = mr.group(2);
			}
			String[] unameLineArr = unameContent.split("\n");
			String[] uname_tmpData = null;
			for (int i = 0; i < unameLineArr.length; i++) {
				uname_tmpData = unameLineArr[i].split("\\s++");
				if (uname_tmpData.length == 2) {
					systemdata = new SystemCollectEntity();
					systemdata.setIpaddress(ipaddress);
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("operatSystem");
					systemdata.setSubentity("operatSystem");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(uname_tmpData[0]);
					systemVector.addElement(systemdata);

					systemdata = new SystemCollectEntity();
					systemdata.setIpaddress(ipaddress);
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("SysName");
					systemdata.setSubentity("SysName");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(uname_tmpData[1]);
					systemVector.addElement(systemdata);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		List pro_list = new ArrayList();
		try {
			String cpuContent = "";
			tmpPt = Pattern.compile("(cmdbegin:proc)(.*)(cmdbegin:vmstat)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				cpuContent = mr.group(2);
			}
			String[] cpuLineArr = cpuContent.split("\n");
			String[] cpu_tmpData = null;
			for (int i = 0; i < cpuLineArr.length; i++) {
				cpu_tmpData = cpuLineArr[i].trim().split("\\s++");
				if (cpu_tmpData.length == 9) {
					String[] _cpudata = new String[4];
					if (cpu_tmpData[0].equalsIgnoreCase("PID")) {
						continue;
					}
					if (cpu_tmpData[1].equalsIgnoreCase("PID")) {
						continue;
					}
					_cpudata[0] = cpu_tmpData[1];// PID
					if (cpu_tmpData[7].indexOf(":") >= 0) {
						_cpudata[1] = cpu_tmpData[8];// CMD
						_cpudata[2] = cpu_tmpData[7];// TIME

					} else {
						_cpudata[1] = cpu_tmpData[7] + " " + cpu_tmpData[8];// CMD
						_cpudata[2] = cpu_tmpData[6];// TIME
					}

					_cpudata[3] = "";
					pro_list.add(_cpudata);
				} else if (cpu_tmpData.length > 9) {
					String[] _cpudata = new String[4];
					if (cpu_tmpData[0].equalsIgnoreCase("PID")) {
						continue;
					}
					if (cpu_tmpData[1].equalsIgnoreCase("PID")) {
						continue;
					}
					_cpudata[0] = cpu_tmpData[1];// PID
					String cmdstr = "";
					if (cpu_tmpData[4].indexOf(":") >= 0) {
						_cpudata[2] = cpu_tmpData[6];// TIME
						for (int k = 7; k < cpu_tmpData.length - 1; k++) {
							cmdstr = cmdstr + " " + cpu_tmpData[k];
						}
						_cpudata[1] = cmdstr.trim();// CMD
					} else {
						_cpudata[2] = cpu_tmpData[7];// TIME
						for (int k = 8; k < cpu_tmpData.length - 1; k++) {
							cmdstr = cmdstr + " " + cpu_tmpData[k];
						}
						_cpudata[1] = cmdstr.trim();// CMD
					}

					_cpudata[3] = "";
					pro_list.add(_cpudata);
				} else if (cpu_tmpData.length == 8) {
					String[] _cpudata = new String[4];
					if (cpu_tmpData[0].equalsIgnoreCase("PID")) {
						continue;
					}
					if (cpu_tmpData[1].equalsIgnoreCase("PID")) {
						continue;
					}
					_cpudata[0] = cpu_tmpData[1];// PID
					String cmdStr = cpu_tmpData[7];// CMD
					if (cmdStr.indexOf("<") >= 0) {
						cmdStr = cmdStr.replaceAll("<", "");
					}
					if (cmdStr.indexOf(">") >= 0) {
						cmdStr = cmdStr.replaceAll(">", "");
					}
					_cpudata[1] = cmdStr;

					_cpudata[2] = cpu_tmpData[6];// TIME
					_cpudata[3] = "";
					pro_list.add(_cpudata);
				}
			}

			// ----------------解析cpu内容--创建监控项---------------------
			cpuContent = "";
			List procslist = new ArrayList();
			ProcsDao procsdaor = new ProcsDao();
			try {
				procslist = procsdaor.loadByIp(ipaddress);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				procsdaor.close();
			}
			Hashtable procshash = new Hashtable();
			Vector procsV = new Vector();
			if (procslist != null && procslist.size() > 0) {
				for (int i = 0; i < procslist.size(); i++) {
					Procs procs = (Procs) procslist.get(i);
					procshash.put(procs.getProcname(), procs);
					procsV.add(procs.getProcname());
				}
			}
			systemdata = new SystemCollectEntity();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("ProcessCount");
			systemdata.setSubentity("ProcessCount");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(cpuLineArr.length - 1 + "");
			systemVector.addElement(systemdata);
			if (pro_list != null && pro_list.size() > 0) {
				for (int i = 0; i < pro_list.size(); i++) {
					String[] pro = (String[]) pro_list.get(i);
					String vbstring0 = pro[0];// pid
					String vbstring1 = pro[1];// command
					String vbstring2 = "应用程序";
					String vbstring3 = "正在运行";
					String vbstring5 = pro[2];// cputime

					processdata = new ProcessCollectEntity();
					processdata.setIpaddress(ipaddress);
					processdata.setCollecttime(date);
					processdata.setCategory("Process");
					processdata.setEntity("MemoryUtilization");
					processdata.setSubentity(vbstring0);
					processdata.setRestype("dynamic");
					processdata.setUnit("%");
					processdata.setThevalue("");
					processVector.addElement(processdata);

					processdata = new ProcessCollectEntity();
					processdata.setIpaddress(ipaddress);
					processdata.setCollecttime(date);
					processdata.setCategory("Process");
					processdata.setEntity("Memory");
					processdata.setSubentity(vbstring0);
					processdata.setRestype("static");
					processdata.setUnit("K");
					processdata.setThevalue(pro[3]);
					processVector.addElement(processdata);

					processdata = new ProcessCollectEntity();
					processdata.setIpaddress(ipaddress);
					processdata.setCollecttime(date);
					processdata.setCategory("Process");
					processdata.setEntity("Type");
					processdata.setSubentity(vbstring0);
					processdata.setRestype("static");
					processdata.setUnit(" ");
					processdata.setThevalue(vbstring2);
					processVector.addElement(processdata);

					processdata = new ProcessCollectEntity();
					processdata.setIpaddress(ipaddress);
					processdata.setCollecttime(date);
					processdata.setCategory("Process");
					processdata.setEntity("Status");
					processdata.setSubentity(vbstring0);
					processdata.setRestype("static");
					processdata.setUnit(" ");
					processdata.setThevalue(vbstring3);
					processVector.addElement(processdata);

					processdata = new ProcessCollectEntity();
					processdata.setIpaddress(ipaddress);
					processdata.setCollecttime(date);
					processdata.setCategory("Process");
					processdata.setEntity("Name");
					processdata.setSubentity(vbstring0);
					processdata.setRestype("static");
					processdata.setUnit(" ");
					processdata.setThevalue(vbstring1);
					processVector.addElement(processdata);

					processdata = new ProcessCollectEntity();
					processdata.setIpaddress(ipaddress);
					processdata.setCollecttime(date);
					processdata.setCategory("Process");
					processdata.setEntity("CpuTime");
					processdata.setSubentity(vbstring0);
					processdata.setRestype("static");
					processdata.setUnit("秒");
					processdata.setThevalue(vbstring5);
					processVector.addElement(processdata);

					// 判断是否有需要监视的进程，若取得的列表里包含监视进程，则从Vector里去掉
					if (procsV != null && procsV.size() > 0) {
						if (procsV.contains(vbstring1)) {
							procsV.remove(vbstring1);
							// 判断已经发送的进程短信列表里是否有该进程,若有,则从已发送列表里去掉该短信信息
							if (sendeddata.containsKey(ipaddress + ":" + vbstring1)) {
								sendeddata.remove(ipaddress + ":" + vbstring1);
							}
							// 判断进程丢失列表里是否有该进程,若有,则从该列表里去掉该信息
							Hashtable iplostprocdata = (Hashtable) ShareData.getLostprocdata(ipaddress);
							if (iplostprocdata == null) {
								iplostprocdata = new Hashtable();
							}
							if (iplostprocdata.containsKey(vbstring1)) {
								iplostprocdata.remove(vbstring1);
								ShareData.setLostprocdata(ipaddress, iplostprocdata);
							}

						}
					}

				}

			}

			// ----------------解析memory内容--创建监控项---------------------

			tmpPt = Pattern.compile("(cmdbegin:memory)(.*)(cmdbegin:vmstat)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			int allPhysicalMemory = 0;
			String vmstat_Content = "";
			tmpPt = Pattern.compile("(cmdbegin:vmstat)(.*)(cmdbegin:mac)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				try {
					vmstat_Content = mr.group(2);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			String[] vmstat_LineArr = null;
			String[] vmstat_tmpData = null;
			int freePhysicalMemory = 0;

			try {
				vmstat_LineArr = vmstat_Content.split("\n");

				for (int i = 1; i < vmstat_LineArr.length; i++) {
					vmstat_tmpData = vmstat_LineArr[i].trim().split("\\s++");
					if ((vmstat_tmpData != null && vmstat_tmpData.length == 18)) {
						if (vmstat_tmpData[0] != null && !vmstat_tmpData[0].equalsIgnoreCase("r")) {
							// freeMemory
							freePhysicalMemory = Integer.parseInt(vmstat_tmpData[4]) * 4 / 1024;
							System.out.println("freePhysicalMemory--------" + freePhysicalMemory);
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// ----------------解析swapinfo内容--创建监控项---------------------

			String swapinfo_Content = "";
			tmpPt = Pattern.compile("(cmdbegin:swapinfo)(.*)(cmdbegin:uname)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				try {
					swapinfo_Content = mr.group(2);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			int allswapMemory = 0;
			int usedswapMemory = 0;
			float usedswapPerc = 0;
			try {
				vmstat_LineArr = swapinfo_Content.split("\n");
				for (int i = 1; i < vmstat_LineArr.length; i++) {
					vmstat_tmpData = vmstat_LineArr[i].trim().split("\\s++");
					if ((vmstat_tmpData != null)) {
						if (vmstat_tmpData[0] != null && vmstat_tmpData[0].equalsIgnoreCase("dev")) {
							// swapMemory
							allswapMemory = Integer.parseInt(vmstat_tmpData[1]) / 1024;
						}
						if (vmstat_tmpData[0] != null && vmstat_tmpData[0].equalsIgnoreCase("reserve")) {
							usedswapMemory = Integer.parseInt(vmstat_tmpData[2]) / 1024;
						}

					}
				}
				usedswapPerc = Float.parseFloat(Integer.toString(usedswapMemory)) * 100 / Float.parseFloat(Integer.toString(allswapMemory));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ----------------解析lsps内容--创建监控项---------------------

			tmpPt = Pattern.compile("(cmdbegin:lsps)(.*)(cmdbegin:mac)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			Vector memoryVector = new Vector();
			String[] tmpData = null;

			MemoryCollectEntity memorydata = null;

			// ----------------解析内存--创建监控项---------------------
			String mContent = "";
			// String diskLabel;
			tmpPt = Pattern.compile("(cmdbegin:end)(.*)(cmdbegin:memory)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				mContent = mr.group(2);
			}
			String[] mLineArr = mContent.split("\n");
			for (int i = 1; i < mLineArr.length; i++) {
				try {
					tmpData = mLineArr[i].trim().split("\\s++");
					if ((tmpData != null)) {
						if (tmpData.length > 3) {

							String memname = tmpData[0];

							if (memname.equalsIgnoreCase("Memory:".toLowerCase())) {
								int oenValue = Integer.parseInt(tmpData[1].substring(0, tmpData[1].length() - 1)) / 1024;
								freePhysicalMemory = (Integer.parseInt(tmpData[7].substring(0, tmpData[7].length() - 1)) + Integer.parseInt(tmpData[4].substring(0, tmpData[4]
										.length() - 1))) / 1024;
								allPhysicalMemory = oenValue + freePhysicalMemory;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			float PhysicalMemUtilization = 0;
			try {
				PhysicalMemUtilization = Float.parseFloat(Integer.toString(allPhysicalMemory - freePhysicalMemory)) * 100 / Float.parseFloat(Integer.toString(allPhysicalMemory));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// ----------------解析内存--创建监控项---------------------
			tmpPt = Pattern.compile("(cmdbegin:memory)(.*)(cmdbegin:memoryend)", Pattern.DOTALL);

			memorydata = new MemoryCollectEntity();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Capability");
			memorydata.setSubentity("PhysicalMemory");
			memorydata.setRestype("static");
			memorydata.setUnit("M");
			memorydata.setThevalue(Float.toString(allPhysicalMemory));
			memoryVector.addElement(memorydata);
			// 物理内存总大小变化告警检测
			memorydata = new MemoryCollectEntity();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("UsedSize");
			memorydata.setSubentity("PhysicalMemory");
			memorydata.setRestype("static");
			memorydata.setUnit("M");// (allPhysicalMemory-freePhysicalMemory)
			memorydata.setThevalue(Integer.toString(allPhysicalMemory - freePhysicalMemory));
			memoryVector.addElement(memorydata);

			memorydata = new MemoryCollectEntity();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Utilization");
			memorydata.setSubentity("PhysicalMemory");
			memorydata.setRestype("dynamic");
			memorydata.setUnit("%");
			memorydata.setThevalue(Float.toString(PhysicalMemUtilization));
			memoryVector.addElement(memorydata);

			// 物理内存阀值比较
			Vector phymemV = new Vector();
			phymemV.add(memorydata);
			Hashtable collectHash = new Hashtable();
			collectHash.put("physicalmem", phymemV);
			collectHash = null;
			phymemV = null;

			memorydata = new MemoryCollectEntity();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Capability");
			memorydata.setSubentity("SwapMemory");
			memorydata.setRestype("static");
			memorydata.setUnit("M");
			memorydata.setThevalue(Integer.toString(allswapMemory));
			memoryVector.addElement(memorydata);

			memorydata = new MemoryCollectEntity();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("UsdeSize");
			memorydata.setSubentity("SwapMemory");
			memorydata.setRestype("static");
			memorydata.setUnit("M");
			memorydata.setThevalue(Integer.toString(usedswapMemory));
			memoryVector.addElement(memorydata);

			memorydata = new MemoryCollectEntity();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Utilization");
			memorydata.setSubentity("SwapMemory");
			memorydata.setRestype("dynamic");
			memorydata.setUnit("%");
			memorydata.setThevalue(Float.toString(usedswapPerc));
			memoryVector.addElement(memorydata);

			// disk数据集合，变化时进行告警检测
			Hashtable<String, Object> diskInfoHash = new Hashtable<String, Object>();
			// 磁盘大小
			float diskSize = 0;
			// 磁盘名称集合
			List<String> diskNameList = new ArrayList<String>();

			// 物理内存阀值比较
			phymemV = new Vector();
			phymemV.add(memorydata);
			collectHash = new Hashtable();
			collectHash.put("swapmem", phymemV);
			collectHash = null;
			phymemV = null;

			// ----------------解析disk内容--创建监控项---------------------
			// System.out.println(ipaddress+"----------------------disk");
			String diskContent = "";
			tmpPt = Pattern.compile("(cmdbegin:disk)(.*)(cmdbegin:cpu)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				diskContent = mr.group(2);
			}
			String[] diskLineArr = diskContent.trim().split("\n");

			String[] tmpData2 = null;
			String[] tmpData3 = null;
			DiskCollectEntity diskdata = null;
			int pi = 0;
			for (int i = 0; i < diskLineArr.length / 4; i++) {
				pi = i;
				try {
					tmpData = diskLineArr[pi * 4].split("\\s++");
					tmpData2 = diskLineArr[pi * 4 + 2].split("\\s++");
					tmpData3 = diskLineArr[pi * 4 + 3].split("\\s++");
					diskdata = new DiskCollectEntity();
					diskdata.setIpaddress(host.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("Utilization");// 利用百分比
					diskdata.setSubentity(tmpData[0]);
					diskdata.setRestype("static");
					diskdata.setUnit("%");

					diskdata.setThevalue(Float.toString(Float.parseFloat(tmpData3[1])));
					diskVector.addElement(diskdata);

					diskdata = new DiskCollectEntity();
					diskdata.setIpaddress(host.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("AllSize");// 总空间
					diskdata.setSubentity(tmpData[0]);
					diskdata.setRestype("static");

					int allblocksize = 0;
					try {
						allblocksize = Integer.parseInt(tmpData[4]);
					} catch (Exception e) {
						try {
							allblocksize = Integer.parseInt(tmpData[3]);
						} catch (Exception ex) {

						}
					}
					float allsize = 0.0f;
					allsize = allblocksize * 1.0f / 1024;
					// 磁盘总大小 单位为M
					diskSize = diskSize + allsize;
					// 磁盘名称放入集合
					if (!diskdata.getSubentity().equals("")) {
						diskNameList.add(diskdata.getSubentity());
					}
					if (allsize >= 1024.0f) {
						allsize = allsize / 1024;
						diskdata.setUnit("G");
					} else {
						diskdata.setUnit("M");
					}

					diskdata.setThevalue(Float.toString(allsize));
					diskVector.addElement(diskdata);

					try {
						String diskinc = "0.0";
						float pastutil = 0.0f;
						Vector disk_v = (Vector) ipAllData.get("disk");
						if (disk_v != null && disk_v.size() > 0) {
							for (int si = 0; si < disk_v.size(); si++) {
								DiskCollectEntity disk_data = (DiskCollectEntity) disk_v.elementAt(si);
								if ((tmpData[0]).equals(disk_data.getSubentity()) && "Utilization".equals(disk_data.getEntity())) {
									pastutil = Float.parseFloat(disk_data.getThevalue());
								}
							}
						} else {
							pastutil = Float.parseFloat(tmpData3[1]);
						}
						if (pastutil == 0) {
							pastutil = Float.parseFloat(tmpData3[1]);
						}
						if (Float.parseFloat(tmpData3[1]) - pastutil > 0) {
							diskinc = (Float.parseFloat(tmpData3[1]) - pastutil) + "";
						}
						diskdata = new DiskCollectEntity();
						diskdata.setIpaddress(host.getIpAddress());
						diskdata.setCollecttime(date);
						diskdata.setCategory("Disk");
						diskdata.setEntity("UtilizationInc");// 利用增长率百分比
						diskdata.setSubentity(tmpData[0]);
						diskdata.setRestype("dynamic");
						diskdata.setUnit("%");
						diskdata.setThevalue(diskinc);
						diskVector.addElement(diskdata);
					} catch (Exception e) {
						e.printStackTrace();
					}

					diskdata = new DiskCollectEntity();
					diskdata.setIpaddress(host.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UsedSize");// 使用大小
					diskdata.setSubentity(tmpData[0]);
					diskdata.setRestype("static");

					int UsedintSize = 0;
					UsedintSize = Integer.parseInt(tmpData2[1]);
					float usedfloatsize = 0.0f;
					usedfloatsize = UsedintSize * 1.0f / 1024;
					if (usedfloatsize >= 1024.0f) {
						usedfloatsize = usedfloatsize / 1024;
						diskdata.setUnit("G");
					} else {
						diskdata.setUnit("M");
					}
					diskdata.setThevalue(Float.toString(usedfloatsize));
					diskVector.addElement(diskdata);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			diskSize = diskSize / 1024;
			diskInfoHash.put("diskSize", diskSize + "G");
			diskInfoHash.put("diskNameList", diskNameList);
			try {
				String cpu_Content = "";
				tmpPt = Pattern.compile("(cmdbegin:cpu)(.*)(cmdbegin:proc)", Pattern.DOTALL);
				mr = tmpPt.matcher(fileContent.toString());
				if (mr.find()) {
					cpu_Content = mr.group(2);
				}
				String[] cpu_LineArr = cpu_Content.split("\n");
				for (int i = 1; i < cpu_LineArr.length; i++) {
					tmpData = cpu_LineArr[i].split("\\s++");
					if ((tmpData != null) && ((tmpData.length == 5) || (tmpData.length == 6))) {
						if (tmpData[0] != null && tmpData[0].equalsIgnoreCase("Average")) {
							cpudata = new CpuCollectEntity();
							cpudata.setIpaddress(ipaddress);
							cpudata.setCollecttime(date);
							cpudata.setCategory("CPU");
							cpudata.setEntity("Utilization");
							cpudata.setSubentity("Utilization");
							cpudata.setRestype("dynamic");
							cpudata.setUnit("%");
							cpudata.setThevalue(Float.toString(100 - Float.parseFloat(tmpData[4])));
							cpuVector.addElement(cpudata);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 对CPU值进行告警检测
			collectHash = new Hashtable();
			collectHash.put("cpu", cpuVector);
			// ----------------解析用户登陆历史内容--创建监控项---------------------
			String syslogContent = "";
			// String diskLabel;
			tmpPt = Pattern.compile("(cmdbegin:end)(.*)(cmdbegin:syslog)", Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				syslogContent = mr.group(2);
			}
			String[] syslogLineArr = syslogContent.split("\n");
			List allSyslogList = new ArrayList();
			for (int i = 1; i < syslogLineArr.length; i++) {
				try {
					tmpData = syslogLineArr[i].split("\\s++");
					if ((tmpData != null)) {
						List sysloglist = new ArrayList();
						if (tmpData.length == 5) {
							// 系统重新启动
							String username = tmpData[0];
							String tools = tmpData[1];
							String timedetail = tmpData[2] + tmpData[3] + tmpData[4];
							sysloglist.add(username);
							sysloglist.add(tools);
							sysloglist.add("");
							sysloglist.add(timedetail);
							allSyslogList.add(sysloglist);
						} else if (tmpData.length == 4) {
							// 系统重新启动
							String username = tmpData[0];
							String tools = tmpData[1];
							String timedetail = tmpData[2] + tmpData[3];
							sysloglist.add(username);
							sysloglist.add(tools);
							sysloglist.add("");
							sysloglist.add(timedetail);
							allSyslogList.add(sysloglist);
						} else if (tmpData.length > 7) {
							// 正常处理
							String username = tmpData[0];
							String tools = tmpData[1];
							String ip = tmpData[2];
							String timedetail = "";
							for (int k = 3; k < tmpData.length; k++) {
								timedetail = timedetail + " " + tmpData[k];
							}
							sysloglist.add(username);
							sysloglist.add(tools);
							sysloglist.add(ip);
							sysloglist.add(timedetail);
							allSyslogList.add(sysloglist);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				// deleteFile(ipaddress);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (diskVector != null && diskVector.size() > 0) {
				returnHash.put("disk", diskVector);
				// 把采集结果生成sql
				HostdiskResultosql tosql = new HostdiskResultosql();
				tosql.CreateResultTosql(returnHash, host.getIpAddress());
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					HostDatatempDiskRttosql temptosql = new HostDatatempDiskRttosql();
					temptosql.CreateResultTosql(returnHash, host);
					temptosql = null;
				}

				tosql = null;

			}
			if (cpuVector != null && cpuVector.size() > 0) {
				returnHash.put("cpu", cpuVector);

				HostcpuResultTosql rtosql = new HostcpuResultTosql();
				rtosql.CreateLinuxResultTosql(returnHash, host.getIpAddress());
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					NetHostDatatempCpuRTosql totempsql = new NetHostDatatempCpuRTosql();
					totempsql.CreateResultTosql(returnHash, host);
					totempsql = null;
				}

			}

			if (memoryVector != null && memoryVector.size() > 0) {
				returnHash.put("memory", memoryVector);

				// 把采集结果生成sql
				HostPhysicalMemoryResulttosql tosql = new HostPhysicalMemoryResulttosql();
				tosql.CreateResultTosql(returnHash, host.getIpAddress());
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					NetHostMemoryRtsql totempsql = new NetHostMemoryRtsql();
					totempsql.CreateResultTosql(returnHash, host);
				}

			}
			if (userVector != null && userVector.size() > 0) {
				returnHash.put("user", userVector);
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					HostDatatempUserRtosql tosql = new HostDatatempUserRtosql();
					tosql.CreateResultTosql(returnHash, host);
				}

			}
			if (processVector != null && processVector.size() > 0) {
				returnHash.put("process", processVector);
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					// 把结果生成sql
					HostDatatempProcessRtTosql temptosql = new HostDatatempProcessRtTosql();
					temptosql.CreateResultTosql(returnHash, host);
				}

			}
			if (systemVector != null && systemVector.size() > 0) {
				returnHash.put("system", systemVector);
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					NetHostDatatempSystemRttosql tosql = new NetHostDatatempSystemRttosql();
					tosql.CreateResultTosql(returnHash, host);
				}

			}
			if (allSyslogList != null && allSyslogList.size() > 0) {
				returnHash.put("syslog", allSyslogList);

			}

			if (iflist != null && iflist.size() > 0) {
				returnHash.put("iflist", iflist);
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					HostDatatempiflistRtosql tosql = new HostDatatempiflistRtosql();
					tosql.CreateResultTosql(returnHash, host);
				}

			}
			if (utilhdxVector != null && utilhdxVector.size() > 0) {
				returnHash.put("utilhdx", utilhdxVector);
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					HostDatatemputilhdxRtosql tosql = new HostDatatemputilhdxRtosql();
					tosql.CreateResultTosql(returnHash, host);
				}

			}
			if (interfaceVector != null && interfaceVector.size() > 0) {
				returnHash.put("interface", interfaceVector);
				String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
				if (!"0".equals(runmodel)) {
					// 采集与访问是分离模式,则不需要将监视数据写入临时表格
					HostDatatempinterfaceRtosql tosql = new HostDatatempinterfaceRtosql();
					tosql.CreateResultTosql(returnHash, host);
				}

			}

			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			try {
				updateLinuxData(nodeDTO, returnHash);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ShareData.getSharedata().put(host.getIpAddress(), returnHash);

			return returnHash;
		} finally {
		}
	}

	public void deleteFile(String ipAddress) {

		try {
			File delFile = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log");
			delFile.delete();
		} catch (Exception e) {
		}
	}

	public String getMaxNum(String ipAddress) {
		String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "/linuxserver/");
		String[] fileList = logFolder.list();

		for (int i = 0; i < fileList.length; i++) // 找一个最新的文件
		{
			if (!fileList[i].startsWith(ipAddress)) {
				continue;
			}

			return ipAddress;
		}
		return maxStr;
	}

	public void updateLinuxData(NodeDTO nodeDTO, Hashtable hashtable) {
		Host host = (Host) PollingEngine.getInstance().getNodeByID(nodeDTO.getId());
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmIndicatorsForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
		if (list == null || list.size() == 0) {
			return;
		}
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		for (int i = 0; i < list.size(); i++) {

			try {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
				if ("file".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + host.getIpAddress() + ".log";
					if (filename != null) {
						File file = new File(filename);
						long lasttime = file.lastModified();
						Date date = new Date(lasttime);
						Date date2 = new Date();
						long btmes = (date2.getTime() - date.getTime()) / 1000;
						if (file.exists()) {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, btmes + "");
						} else {
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, "999999");
						}
					}
				} else if ("cpu".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (hashtable != null && hashtable.size() > 0) {
						Vector cpuVector = (Vector) hashtable.get("cpu");
						if (cpuVector != null) {
							for (int k = 0; k < cpuVector.size(); k++) {
								CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.get(k);
								if ("Utilization".equalsIgnoreCase(cpudata.getEntity()) && "Utilization".equalsIgnoreCase(cpudata.getSubentity())) {
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, cpudata.getThevalue());
								}
							}
						}
					}
				} else if ("physicalmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (hashtable != null && hashtable.size() > 0) {
						Vector memoryVector = (Vector) hashtable.get("memory");
						if (memoryVector != null) {
							for (int k = 0; k < memoryVector.size(); k++) {
								MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(k);
								;
								if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, memorydata.getThevalue());
								}
							}
						}
					}
				} else if ("swapmemory".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (hashtable != null && hashtable.size() > 0) {
						Vector memoryVector = (Vector) hashtable.get("memory");
						if (memoryVector != null) {
							for (int k = 0; k < memoryVector.size(); k++) {
								MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(k);
								;
								if ("SwapMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
									checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, memorydata.getThevalue());
								}
							}
						}
					}
				} else if ("AllInBandwidthUtilHdx".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (hashtable != null && hashtable.size() > 0) {
						Vector inVector = (Vector) hashtable.get("utilhdx");
						int inutil = 0;
						if (inVector != null) {
							for (int k = 0; k < inVector.size(); k++) {
								UtilHdx indata = (UtilHdx) inVector.get(k);
								;
								if ("InBandwidthUtilHdx".equalsIgnoreCase(indata.getEntity())) {
									inutil = inutil + Integer.parseInt(indata.getThevalue());
								}
							}
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, inutil + "");
						}
					}
				} else if ("AllOutBandwidthUtilHdx".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (hashtable != null && hashtable.size() > 0) {
						Vector outVector = (Vector) hashtable.get("utilhdx");
						if (outVector != null) {
							int oututil = 0;
							for (int k = 0; k < outVector.size(); k++) {
								UtilHdx outdata = (UtilHdx) outVector.get(k);
								;
								if ("OutBandwidthUtilHdx".equalsIgnoreCase(outdata.getEntity())) {
									oututil = oututil + Integer.parseInt(outdata.getThevalue());
								}
							}
							checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, oututil + "");
						}
					}
				} else if ("diskperc".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (hashtable != null && hashtable.size() > 0) {
						Vector diskVector = (Vector) hashtable.get("disk");
						if (diskVector != null) {
							checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
						}
					}
				} else if ("diskinc".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (hashtable != null && hashtable.size() > 0) {
						Vector diskVector = (Vector) hashtable.get("disk");
						if (diskVector != null) {
							checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
						}
					}
				} else if ("process".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (hashtable != null && hashtable.size() > 0) {
						Vector processVector = (Vector) hashtable.get("process");
						if (processVector != null) {
							checkEventUtil.createProcessGroupEventList(nodeDTO.getIpaddress(), processVector, alarmIndicatorsNode);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
