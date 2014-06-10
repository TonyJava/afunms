package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;
import com.afunms.ipaccounting.model.IpAccounting;
import com.gatherdb.GathersqlListManager;

/**
 * 
 * 
 * �������豸��cpu�ɼ��������sql���
 * 
 * @author �����豸cpu�ɼ��������sqlʵ����
 * 
 */
@SuppressWarnings("unchecked")
public class NetipaccountResultTosql {

	/**
	 * 
	 * ��cpu�Ĳɼ����ݳɳ�sql������ڴ��б���
	 */
	public void CreateResultTosql(Vector ipaccountVector, String ip) {
		IpAccounting ipaccounting = null;
		StringBuffer sBuffer = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (ipaccountVector != null && ipaccountVector.size() > 0) {
			for (int i = 0; i < ipaccountVector.size(); i++) {
				ipaccounting = (IpAccounting) ipaccountVector.elementAt(i);
				Calendar tempCal = (Calendar) ipaccounting.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "nms_ipaccountingdetail";
				sBuffer = new StringBuffer(150);
				sBuffer.append("insert into ");
				sBuffer.append(tablename);
				sBuffer.append("(pkts,byts,baseid,collecttime) ");
				sBuffer.append("values(");
				sBuffer.append(ipaccounting.getPkts());
				sBuffer.append(",");
				sBuffer.append(ipaccounting.getByts());
				sBuffer.append(",");
				sBuffer.append(ipaccounting.getAccountingBaseID());
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sBuffer.append(",'");
					sBuffer.append(time);
					sBuffer.append("')");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sBuffer.append(",");
					sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
					sBuffer.append(")");
				}
				GathersqlListManager.Addsql(sBuffer.toString());
				sBuffer = null;
				tablename = null;
				ipaccounting = null;
			}

		}
		ipaccountVector = null;
	}
}
