package com.xuexiang.xtcpdemo.protocol;

import com.xuexiang.xtcp.annotation.Protocol;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.core.entity.ArrayItem;
import com.xuexiang.xtcp.core.entity.XProtocolItem;

/**
 * @author xuexiang
 * @since 2018/12/11 上午10:15
 */
@Protocol(name = "参数设置请求", opcode = 0x12, resCode = 0x33, desc = "注意重启下位机后生效！")
public class SettingRequest extends XProtocolItem {

    @ProtocolField(index = 0)
    ArrayItem<Integer> list;


    public SettingRequest() {
        list.data = new Integer[]{11,22};
    }
}
