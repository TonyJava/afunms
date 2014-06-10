package com.afunms.polling.snmp.oracle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * Oracle �����ļ� �ɼ� ʹ��JDBC�ɼ�
 * 
 * @author wupinlong 2012/09/17
 * 
 */

@SuppressWarnings("unchecked")
public class OracleControlFileProxy extends SnmpMonitor {

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		String htKey = "controlfile";
		List dbmonitorlists = new ArrayList();
		dbmonitorlists = ShareData.getDbconfiglist();
		DBVo dbvo = new DBVo();
		if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
			for (int i = 0; i < dbmonitorlists.size(); i++) {
				DBVo vo = (DBVo) dbmonitorlists.get(i);
				if (vo.getId() == Integer.parseInt(nodeGatherIndicators.getNodeid())) {
					dbvo = vo;
					break;
				}
			}
		}
		if (dbvo != null) {
			if (dbvo.getManaged() == 0) {
				// ���δ���������ɼ���user��ϢΪ��
				return returndata;
			}
			String serverip = dbvo.getIpAddress();
			String[] args = new String[] { "status", "name", "is_recovery_dest_file", "block_size", "file_size_blks", "mon_time" };
			returndata = LogParser.parse(this, dbvo, htKey, args);
			// �����ڴ�
			if (!(ShareData.getSharedata().containsKey(dbvo.getIpAddress() + ":" + dbvo.getId()))) {
				ShareData.getSharedata().put(dbvo.getIpAddress() + ":" + dbvo.getId(), returndata);
			} else {
				Hashtable oracleHash = (Hashtable) ShareData.getSharedata().get(dbvo.getIpAddress() + ":" + dbvo.getId());
				oracleHash.put("contrFile_v", returndata.get(htKey));
			}

			Vector contrFile_v = (Vector) returndata.get(htKey);
			if (contrFile_v != null && !contrFile_v.isEmpty()) {
				String hex = IpTranslation.formIpToHex(dbvo.getIpAddress());
				serverip = hex + ":" + dbvo.getId();

				try {
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String montime = sdf.format(cc);

					String deletesql = "delete from nms_oracontrfile where serverip='" + serverip + "'";
					GathersqlListManager.Addsql(deletesql);
					String insertsql = null;
					for (int k = 0; k < contrFile_v.size(); k++) {
						Hashtable ht = (Hashtable) contrFile_v.get(k);
						String status = ht.get("status").toString();
						String name = ht.get("name").toString();
						String is_recovery_dest_file = CommonUtil.getValue(ht, "is_recovery_dest_file", "--");
						String block_size = CommonUtil.getValue(ht, "block_size", "--");
						String file_size_blks = CommonUtil.getValue(ht, "file_size_blks", "--");
						name = name.replaceAll("\\\\", "/");
						insertsql = "insert into nms_oracontrfile(serverip,status,name,is_recovery_dest_file,block_size,file_size_blks,mon_time) " + "values('" + serverip + "','"
								+ status + "','" + name + "','" + is_recovery_dest_file + "','" + block_size + "','" + file_size_blks;
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "','" + montime + "')";
						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							insertsql = insertsql + "',to_date('" + montime + "','YYYY-MM-DD HH24:MI:SS'))";
						}
						GathersqlListManager.Addsql(insertsql);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
		return returndata;
	}

}
