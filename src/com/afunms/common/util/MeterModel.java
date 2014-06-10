package com.afunms.common.util;

import java.util.ArrayList;
import java.util.List;

public class MeterModel {

	private int picx;// 图片长度
	private int picy;// 图片宽度
	private int meterX;// 仪表盘宽度
	private int meterY;// 仪表盘高度

	private String picName;// 图片名称

	private int innerRoundColor;// 内圆颜色
	private int outRingColor;// 外环颜色
	private int bgColor;// 背景色

	private int meterSize;// 仪表盘大小

	private String title;// 图标
	private double value;// 值

	private int fontSize;// 字体大小

	private int outPointerColor;// 指针外部颜色
	private int inPointerColor;// 指针内部颜色

	private int titleY;// 标题离左边的距离
	private int titleTop;// 标题离上边的距离

	private int valueY;// 值离左边的距离
	private int valueTop;// 值离上边的距离

	private List<StageColor> stagelist = new ArrayList<StageColor>();

	public int getBgColor() {
		return bgColor;
	}

	public int getFontSize() {
		return fontSize;
	}

	public int getInnerRoundColor() {
		return innerRoundColor;
	}

	public int getInPointerColor() {
		return inPointerColor;
	}

	public List<StageColor> getList() {
		return stagelist;
	}

	public int getMeterSize() {
		return meterSize;
	}

	public int getMeterX() {
		return meterX;
	}

	public int getMeterY() {
		return meterY;
	}

	public int getOutPointerColor() {
		return outPointerColor;
	}

	public int getOutRingColor() {
		return outRingColor;
	}

	public String getPicName() {
		return picName;
	}

	public int getPicx() {
		return picx;
	}

	public int getPicy() {
		return picy;
	}

	public String getTitle() {
		return title;
	}

	public int getTitleTop() {
		return titleTop;
	}

	public int getTitleY() {
		return titleY;
	}

	public double getValue() {
		return value;
	}

	public int getValueTop() {
		return valueTop;
	}

	public int getValueY() {
		return valueY;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setInnerRoundColor(int innerRoundColor) {
		this.innerRoundColor = innerRoundColor;
	}

	public void setInPointerColor(int inPointerColor) {
		this.inPointerColor = inPointerColor;
	}

	public void setList(List<StageColor> stagelist) {
		this.stagelist = stagelist;
	}

	public void setMeterSize(int meterSize) {
		this.meterSize = meterSize;
	}

	public void setMeterX(int meterX) {
		this.meterX = meterX;
	}

	public void setMeterY(int meterY) {
		this.meterY = meterY;
	}

	public void setOutPointerColor(int outPointerColor) {
		this.outPointerColor = outPointerColor;
	}

	public void setOutRingColor(int outRingColor) {
		this.outRingColor = outRingColor;
	}

	public void setPicName(String picName) {
		this.picName = picName;
	}

	public void setPicx(int picx) {
		this.picx = picx;
	}

	public void setPicy(int picy) {
		this.picy = picy;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitleTop(int titleTop) {
		this.titleTop = titleTop;
	}

	public void setTitleY(int titleY) {
		this.titleY = titleY;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setValueTop(int valueTop) {
		this.valueTop = valueTop;
	}

	public void setValueY(int valueY) {
		this.valueY = valueY;
	}

}
