package montnets;

public class MsgServer {
	private mondem MyMondem = null;

	int rc = 0;

	public void Instance() { // ����Mondem�����ҿ����߳�ģʽ
		try {
			MyMondem = new mondem();
			rc = MyMondem.SetThreadMode(1);
			System.out.println("�����߳�ģʽ");
			if (rc == 0) {
				System.out.println("�����߳�ģʽ�ɹ�");
			} else {
				System.out.println("�����߳�ģʽʧ��");
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean SendMsg(String tophone, String msg) {
		Instance();
		if ((rc = (MyMondem.InitModem(-1))) == 0)// ��ʼ������è
		{
			rc = MyMondem.SendMsg(-1, tophone, msg); // ����һ����Ϣ
			if (rc == 0) {
				return true;
			}
		}
		return false;
	}

	public static void smsServer(String tophone, String msg) {
		mondem Mytest = new mondem(); // ����һ�� mondem ���� �������������֧��64���˿ڷ���
		int rc;

		rc = Mytest.SetThreadMode(1); // �����߳�ģʽ
		if (rc == 0) {
			System.out.println("�����߳�ģʽ�ɹ�");
		} else {
			System.out.println("�����߳�ģʽʧ��");
			return;
		}
		if ((rc = (Mytest.InitModem(-1))) == 0)// ��ʼ������è
		{
			System.out.println("��ʼ���ɹ�");
			rc = Mytest.SendMsg(-1, tophone, msg); // ����һ����Ϣ
			if (rc == 0) {
				System.out.println("�ύ�ɹ�, rc=" + rc);

				while (true) // ѭ���ȴ����ͳɹ�,����ʾ������Ϣ, Ctrl-C �˳�ѭ��
				{
					String[] s = Mytest.ReadMsgEx(-1);
					if (s[0] == "-1") {
						System.out.println("-����Ϣ-----");
					} else {
						System.out.println(s[0]);
						System.out.println(s[1]);
						System.out.println(s[2]);
					}
					System.out.println("...."
							+ // ��ʾ�����˿ڵ�״̬
							Mytest.GetStatus(0) + Mytest.GetStatus(1) + Mytest.GetStatus(2) + Mytest.GetStatus(3) + Mytest.GetStatus(4) + Mytest.GetStatus(5) + Mytest.GetStatus(6)
							+ Mytest.GetStatus(7) + "....");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					} // ��ʱ�ȴ�
				}
			} else {
				System.out.println("�ύ����, rc=" + rc);
			}
		} else {
			System.out.println("��ʼ������!" + rc);
		}
	}

}
