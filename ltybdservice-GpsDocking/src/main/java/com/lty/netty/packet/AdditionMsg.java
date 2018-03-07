package com.lty.netty.packet;

/**
 * 附加消息
 * @author zhouyongbo 2017/12/8
 */
public class AdditionMsg {
    private int additionId; // 附加消息ID
    private int additionLength;// 附加消息长度
    private String additionContxt;// 附加消息内容

    public int getAdditionId() {
        return additionId;
    }

    public void setAdditionId(int additionId) {
        this.additionId = additionId;
    }

    public int getAdditionLength() {
        return additionLength;
    }

    public void setAdditionLength(int additionLength) {
        this.additionLength = additionLength;
    }

    public String getAdditionContxt() {
        return additionContxt;
    }

    public void setAdditionContxt(String additionContxt) {
        this.additionContxt = additionContxt;
    }
}
