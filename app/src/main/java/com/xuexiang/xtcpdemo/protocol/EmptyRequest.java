package com.xuexiang.xtcpdemo.protocol;

import com.xuexiang.xtcp.annotation.Protocol;
import com.xuexiang.xtcp.core.model.XProtocolItem;

/**
 * @author xuexiang
 * @since 2019/5/24 17:02
 */
@Protocol(name = "空请求", opCode = 0x22, resCode = 0x22, desc = "空请求！")
public class EmptyRequest extends XProtocolItem {


}
