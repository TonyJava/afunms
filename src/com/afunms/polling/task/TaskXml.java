package com.afunms.polling.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.om.Task;

@SuppressWarnings("unchecked")
public class TaskXml {
	private Logger logger = Logger.getLogger(this.getClass());
	private FileInputStream fi = null;
	private FileOutputStream fo = null;
	private Document doc;
	private String filename;
	private String path;
	private String commonPath = ResourceCenter.getInstance().getSysPath() + "task";
	private Element root;
	private List elements;
	private boolean flag = true;// 指定路径文件不存在时为假，否则为真

	public TaskXml() {
		filename = "/task.xml";
		path = commonPath + filename;
	}

	public void createDir() {
		File dir = new File(commonPath);
		if (!dir.exists()) {// 检查Sub目录是否存在
			dir.mkdir();
		}
	}

	public void init() {
		try {
			setFlag(true);
			fi = new FileInputStream(path);
			SAXBuilder sb = new SAXBuilder();
			doc = sb.build(fi);
			root = doc.getRootElement();
			elements = root.getChildren();
		} catch (Exception e) {
			logger.error("SAXBuilder读取任务配置文件错误",e);
		} finally {
			if (fi != null) {
				try {
					fi.close();
				} catch (IOException e) {
					logger.error("关闭任务配置文件流错误",e);
				}
			}
		}
	}

	public void finish() {
		try {
			Format format = Format.getCompactFormat();
			format.setEncoding("GBK");
			format.setIndent("   ");
			XMLOutputter outer = new XMLOutputter(format);
			fo = new FileOutputStream(path);
			outer.output(doc, fo);
		} catch (Exception e) {
			logger.error("XMLOutputter写入任务配置文件错误",e);
		} finally {
			if (fo != null) {
				try {
					fo.close();
				} catch (IOException e) {
					logger.error("关闭任务配置文件流错误",e);
				}
			}
		}
	}

	public void AddXML(Task t) throws Exception {
		try {
			createDir();
			File file = new File(path);
			// 文件不存在则创建
			if (!file.exists()) {
				file.createNewFile();
				root = new Element("Tasks");
				elements = root.getChildren();
				doc = new Document(root);
			} else {
				// 文件存在则读取
				init();
			}
			Element newElement = new Element("Task");
			Element te = null;

			te = new Element("taskname");
			te.setText(t.getTaskname());
			newElement.addContent(te);

			te = new Element("startsign");
			te.setText(t.getStartsign());
			newElement.addContent(te);

			te = new Element("modify");
			te.setText("0");
			newElement.addContent(te);

			te = new Element("polltime");
			te.setText(t.getPolltime().toString());
			newElement.addContent(te);

			te = new Element("polltimeunit");
			te.setText(t.getPolltimeunit());
			newElement.addContent(te);

			elements.add(newElement);// 增加子元素
			finish();
		} catch (Exception e) {
			logger.error("增加任务XML文件配置节点错误",e);
		}
	}

	public boolean DelXML(String name) {
		try {
			init();
			Integer k;
			if ((k = FindXml(name)) == null) {
				if (!getFlag()) {
					return false;
				}
			} else {
				elements.remove(k.intValue());
				finish();
			}
		} catch (Exception e) {
			logger.error("删除任务配置文件错误",e);
		}
		return true;
	}

	public boolean EditXML(Task t) {
		try {
			Integer k;
			if ((k = FindXml(t.getTaskname())) == null) {
				if (!getFlag()) {
					return false;
				}
			} else {
				init();
				Element editelement = (Element) elements.get(k.intValue());
				Element startsign = editelement.getChild("startsign");
				Element polltime = editelement.getChild("polltime");
				Element polltimeunit = editelement.getChild("polltimeunit");
				Element modify = editelement.getChild("modify");

				if (!(t.getStartsign().equals(startsign.getText())) || !(t.getPolltime().toString().equals(polltime.getText()))
						|| !(t.getPolltimeunit().equals(polltimeunit.getText()))) {
					modify.setText("1");
				}

				startsign.setText(t.getStartsign());

				polltime.setText(t.getPolltime().toString());

				polltimeunit.setText(t.getPolltimeunit());
				finish();
			}
		} catch (Exception e) {
			logger.error("修改任务配置文件错误",e);
		}
		return true;
	}

	private Integer FindXml(String name) {
		Integer k = null;
		File file = new File(path);
		try {
			if (!file.exists()) {
				setFlag(false);
			} else {
				init();
				for (int j = 0; j < elements.size(); j++) {
					Element editElement = (Element) elements.get(j);
					String nametemp = editElement.getChildText("taskname");
					if (name.equals(nametemp)) {
						k = new Integer(j);
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("查找任务配置文件XML节点错误",e);
		}
		return k;// k为空，则没有查找到指定记录。
	}

	public Task GetXml(String name) { 
		Task task = null;
		Integer k;
		File file = new File(path);
		try {
			if (!file.exists()) {
				setFlag(false);
				logger.info("任务配置文件XML不存在");
			} else if ((k = FindXml(name)) != null) {
				init();
				Element element = (Element) elements.get(k.intValue());
				task = new Task();
				task.setTaskname(element.getChildText("taskname"));
				task.setStartsign(element.getChildText("startsign"));
				task.setModify(element.getChildText("modify"));
				task.setPolltime(new Float(element.getChildText("polltime")));
				task.setPolltimeunit(element.getChildText("polltimeunit"));
			}
		} catch (Exception e) {
			logger.error("获取任务对象错误",e);
		}
		return task;
	}

	public List<Task> ListXml() {
		List<Task> list = new ArrayList<Task>();
		try {
			File file = new File(path);
			if (!file.exists()) {
				setFlag(false);
			} else {
				init();
				list = new ArrayList<Task>();
				for (int j = 0; j < elements.size(); j++) {
					Task t = new Task();
					t.setTaskname(((Element) (elements.get(j))).getChildText("taskname"));
					t.setStartsign(((Element) (elements.get(j))).getChildText("startsign"));
					t.setModify(((Element) (elements.get(j))).getChildText("modify"));
					t.setPolltime(new Float(((Element) (elements.get(j))).getChildText("polltime")));
					t.setPolltimeunit(((Element) (elements.get(j))).getChildText("polltimeunit"));
					list.add(t);
				}
			}

		} catch (Exception e) {
			logger.error("列举任务对象错误",e);
		}
		return list;
	}

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean b) {
		flag = b;
	}

	public String getPath() {
		return path;
	}

}
