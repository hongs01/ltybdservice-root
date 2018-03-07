package com.lty.netty.packet;



import com.lty.common.util.BCD8421Operater;
import com.lty.common.util.BitOperator;
import com.lty.common.util.JT808ProtocolUtils;

import java.io.ByteArrayOutputStream;

/**
 * 默认包
 * @author zhouyongbo 2017/12/8
 */
public abstract class DefaultNettyPacket {


    private int informationId; // 消息ID
    private int informationAttribute;//消息体属性
    private String cellPhone; //终端手机号
    private int informationNumber;//消息流水号
    //附加消息
    private AdditionMsg additionId_one;
    private AdditionMsg additionId_two;
    private AdditionMsg additionId_three;
    private AdditionMsg additionId_this; //本次附加消息

    private int checkCode;//校验码


    public DefaultNettyPacket(int informationId, int informationAttribute, String cellPhone, int informationNumber) {
        this.informationId = informationId;
        this.informationAttribute = informationAttribute;
        this.cellPhone = cellPhone;
        this.informationNumber = informationNumber;
    }

    public abstract byte[] getJTData() throws Exception;

    public byte[] getHeard()throws Exception{
        //头部编码
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            // 1. 消息ID word(16)
            baos.write(BitOperator.integerTo2Bytes(informationId));
            // 2. 消息体属性 word(16)
            baos.write(BitOperator.integerTo2Bytes(informationAttribute));
            // 3. 终端手机号 bcd[6]
            if (cellPhone.length() >11){
                cellPhone =cellPhone.substring(cellPhone.length()-11,cellPhone.length());
            }
            baos.write(BCD8421Operater.string2Bcd(cellPhone));
//            // 4. 消息流水号 word(16),按发送顺序从 0 开始循环累加
            baos.write(BitOperator.integerTo2Bytes(informationNumber));
            // 消息包封装项 此处不予考虑
            return baos.toByteArray();
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
    }

    public  byte[] getJtData() throws Exception {


        //消息体编码
        byte[] body = getJTData();
        this.informationAttribute = JT808ProtocolUtils.generateMsgBodyProps(body.length, 0b00, false, 0);
        byte[] heard = getHeard();
        byte[] headerAndBody = BitOperator.concatAll(heard, body);
        //处理附加消息 与校验码
        int checkSum = BitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);
        byte[] jtData = JT808ProtocolUtils.doEncode(headerAndBody, checkSum);
//        int checkSumInPkg = jtData[jtData.length - 2];
//        int calculatedCheckSum = this.bitOperator.getCheckSum4JT808(jtData, 1, jtData.length - 2);

        // 连接并且转义
        return jtData;
    }


    public int getInformationNumber() {
        return informationNumber;
    }

    public void setInformationNumber(int informationNumber) {
        this.informationNumber = informationNumber;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public int getInformationId() {
        return informationId;
    }

    public void setInformationId(int informationId) {
        this.informationId = informationId;
    }

    public int getInformationAttribute() {
        return informationAttribute;
    }

    public void setInformationAttribute(int informationAttribute) {
        this.informationAttribute = informationAttribute;
    }

    public AdditionMsg getAdditionId_one() {
        return additionId_one;
    }

    public void setAdditionId_one(AdditionMsg additionId_one) {
        this.additionId_one = additionId_one;
    }

    public AdditionMsg getAdditionId_two() {
        return additionId_two;
    }

    public void setAdditionId_two(AdditionMsg additionId_two) {
        this.additionId_two = additionId_two;
    }

    public AdditionMsg getAdditionId_three() {
        return additionId_three;
    }

    public void setAdditionId_three(AdditionMsg additionId_three) {
        this.additionId_three = additionId_three;
    }

    public AdditionMsg getAdditionId_this() {
        return additionId_this;
    }

    public void setAdditionId_this(AdditionMsg additionId_this) {
        this.additionId_this = additionId_this;
    }

    public int getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(int checkCode) {
        this.checkCode = checkCode;
    }
}
