package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class QueueInfo extends BaseVo {
	private int id;
	private String entity;
	private int inputSize;
	private int inputMax;
	private int inputDrops;
	private int inputFlushes;
	private int outputSize;
	private int outputMax;
	private int outputDrops;
	private int outputFlushes;
	private int outputThreshold;
	private int availBandwidth;
	private String collecttime;

	public int getAvailBandwidth() {
		return availBandwidth;
	}

	public String getCollecttime() {
		return collecttime;
	}

	public String getEntity() {
		return entity;
	}

	public int getId() {
		return id;
	}

	public int getInputDrops() {
		return inputDrops;
	}

	public int getInputFlushes() {
		return inputFlushes;
	}

	public int getInputMax() {
		return inputMax;
	}

	public int getInputSize() {
		return inputSize;
	}

	public int getOutputDrops() {
		return outputDrops;
	}

	public int getOutputFlushes() {
		return outputFlushes;
	}

	public int getOutputMax() {
		return outputMax;
	}

	public int getOutputSize() {
		return outputSize;
	}

	public int getOutputThreshold() {
		return outputThreshold;
	}

	public void setAvailBandwidth(int availBandwidth) {
		this.availBandwidth = availBandwidth;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInputDrops(int intputDrops) {
		this.inputDrops = intputDrops;
	}

	public void setInputFlushes(int inputFlushes) {
		this.inputFlushes = inputFlushes;
	}

	public void setInputMax(int inputMax) {
		this.inputMax = inputMax;
	}

	public void setInputSize(int inputSize) {
		this.inputSize = inputSize;
	}

	public void setOutputDrops(int outputDrops) {
		this.outputDrops = outputDrops;
	}

	public void setOutputFlushes(int outputFlushes) {
		this.outputFlushes = outputFlushes;
	}

	public void setOutputMax(int outputMax) {
		this.outputMax = outputMax;
	}

	public void setOutputSize(int outputSize) {
		this.outputSize = outputSize;
	}

	public void setOutputThreshold(int outputThreshold) {
		this.outputThreshold = outputThreshold;
	}

}
