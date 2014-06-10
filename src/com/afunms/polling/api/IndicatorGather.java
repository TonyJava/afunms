package com.afunms.polling.api;

import com.afunms.common.base.BaseVo;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.node.Result;

@SuppressWarnings("unchecked")
public interface IndicatorGather {

	/**
	 * getValue:
	 * <p>
	 * ��ȡ���
	 * 
	 * @param node -
	 *            �豸
	 * @param nodeGatherIndicators -
	 *            �ɼ�ָ��
	 * @return {@link Result} - ���زɼ����
	 * 
	 * @since v1.01
	 */
	Result getValue(BaseVo node, NodeGatherIndicators nodeGatherIndicators);
}
