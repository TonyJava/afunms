package montnets;

public class MsgServer {
	private mondem MyMondem = null;

	int rc = 0;

	public void Instance() { // 创建Mondem，并且开启线程模式
		try {
			MyMondem = new mondem();
			rc = MyMondem.SetThreadMode(1);
			System.out.println("设置线程模式");
			if (rc == 0) {
				System.out.println("设置线程模式成功");
			} else {
				System.out.println("设置线程模式失败");
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean SendMsg(String tophone, String msg) {
		Instance();
		if ((rc = (MyMondem.InitModem(-1))) == 0)// 初始化短信猫
		{
			rc = MyMondem.SendMsg(-1, tophone, msg); // 发送一条信息
			if (rc == 0) {
				return true;
			}
		}
		return false;
	}

	public static void smsServer(String tophone, String msg) {
		mondem Mytest = new mondem(); // 创建一个 mondem 对象， 这个对象最大可以支持64个端口发送
		int rc;

		rc = Mytest.SetThreadMode(1); // 开启线程模式
		if (rc == 0) {
			System.out.println("设置线程模式成功");
		} else {
			System.out.println("设置线程模式失败");
			return;
		}
		if ((rc = (Mytest.InitModem(-1))) == 0)// 初始化短信猫
		{
			System.out.println("初始化成功");
			rc = Mytest.SendMsg(-1, tophone, msg); // 发送一条信息
			if (rc == 0) {
				System.out.println("提交成功, rc=" + rc);

				while (true) // 循环等待发送成功,并显示接收信息, Ctrl-C 退出循环
				{
					String[] s = Mytest.ReadMsgEx(-1);
					if (s[0] == "-1") {
						System.out.println("-无信息-----");
					} else {
						System.out.println(s[0]);
						System.out.println(s[1]);
						System.out.println(s[2]);
					}
					System.out.println("...."
							+ // 显示各个端口的状态
							Mytest.GetStatus(0) + Mytest.GetStatus(1) + Mytest.GetStatus(2) + Mytest.GetStatus(3) + Mytest.GetStatus(4) + Mytest.GetStatus(5) + Mytest.GetStatus(6)
							+ Mytest.GetStatus(7) + "....");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					} // 延时等待
				}
			} else {
				System.out.println("提交错误, rc=" + rc);
			}
		} else {
			System.out.println("初始化错误!" + rc);
		}
	}

}
