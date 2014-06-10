package montnets;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import com.afunms.alarm.dao.OutBoxDao;
import com.afunms.alarm.model.OutBox;

public class SmsServer {

	public int sendSMS(String num, String msg) {
		mondem Mytest = new mondem(); // 创建一个 mondem 对象， 这个对象最大可以支持64个端口发送
		int rc = -1;
		rc = Mytest.SetThreadMode(1); // 开启线程模式
		if (rc == 0) {
			System.out.println("设置线程模式成功");
		} else {
			System.out.println("设置线程模式失败");
		}
		// 全都设置成单口猫格式
		Mytest.SetModemType(0, 0);
		Mytest.SetModemType(1, 0);
		Mytest.SetModemType(2, 0);
		Mytest.SetModemType(3, 0);
		Mytest.SetModemType(4, 0);
		Mytest.SetModemType(5, 0);
		Mytest.SetModemType(6, 0);
		Mytest.SetModemType(7, 0);

		if ((rc = (Mytest.InitModem(-1))) == 0)// 初始化短信猫
		{
			System.out.println("初始化成功");
			try {
				rc = Mytest.SendMsg(-1, num, msg); // 发送一条信息
			} catch (Exception ex) {
				ex.printStackTrace();
				rc = -1;
			}
			if (rc >= 0) {
				System.out.println("提交成功, rc=" + rc);
			} else {
				System.out.println("提交错误, rc=" + rc);
			}
		} else {
			System.out.println("初始化错误!" + rc);
		}
		return rc;
	}

	public int sendSMS_chuangtian(String num, String msg, String recordtime) {
		OutBoxDao outboxdao = new OutBoxDao();
		OutBox outbox = new OutBox();
		outbox.setMsg(msg);
		outbox.setReceiverMobileNo(num);
		outbox.setSender("网管系统");
		outbox.setSendTime(recordtime);
		int rc = -1;
		try {
			outboxdao.save(outbox);
			rc = 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			outboxdao.close();
		}
		return rc;
	}

	/**
	 * 北京公安局车辆管理所---短信网关调用端口 调用webserice 发送短信告警
	 * 
	 * @param mask
	 *            短信编码号 5701
	 * @param strPhone
	 *            手机号
	 * @param strContent
	 *            短息内容
	 */
	public String SendOneMessageMsgID(String mask, String strPhone, String strContent) {
		String result = "-1011";// 失败
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL("http://10.10.7.239:8009/service.asmx"));
			call.setOperationName(new QName("http://tempuri.org/", "SendOneMessageMsgID"));
			// 添加参数
			call.addParameter(new QName("http://tempuri.org/", "mask"), XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter(new QName("http://tempuri.org/", "strPhone"), XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter(new QName("http://tempuri.org/", "strContent"), XMLType.XSD_STRING, ParameterMode.IN);

			call.setUseSOAPAction(true);
			call.setSOAPActionURI("http://tempuri.org/SendOneMessageMsgID");
			call.setReturnType(XMLType.XSD_STRING);

			result = (String) call.invoke(new Object[] { mask, strPhone, strContent });

			return result;

		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return result;
	}

}
