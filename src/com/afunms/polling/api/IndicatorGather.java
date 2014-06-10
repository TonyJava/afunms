package com.afunms.polling.api;

import com.afunms.common.base.BaseVo;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.node.Result;

@SuppressWarnings("unchecked")
public interface IndicatorGather {

	/**
	 * getValue:
	 * <p>
	 * 获取结果
	 * 
	 * @param node -
	 *            设备
	 * @param nodeGatherIndicators -
	 *            采集指标
	 * @return {@link Result} - 返回采集结果
	 * 
	 * @since v1.01
	 */
	Result getValue(BaseVo node, NodeGatherIndicators nodeGatherIndicators);
}
