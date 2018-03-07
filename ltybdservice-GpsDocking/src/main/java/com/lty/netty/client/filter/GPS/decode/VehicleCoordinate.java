package com.lty.netty.client.filter.GPS.decode;

import com.lty.common.bean.gps.EBoardPack;
import com.lty.common.bean.gps.GpsPacket;
import com.lty.common.util.Coordinate;
import com.lty.common.util.MyUtils;
import com.lty.netty.client.DecodeParameter;
import com.lty.netty.client.filter.GPS.IGpsDecodeFilter;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 车辆坐标
 * @author zhouyongbo
 */
@Component
public class VehicleCoordinate implements IGpsDecodeFilter {


    @Override
    public void doDecode(DecodeParameter decodeParameter) throws Exception {
        ByteBuf byteBuf = decodeParameter.getByteBuf();
        List<Object> list = decodeParameter.getList();
        if(byteBuf.readableBytes() < 58 -1 ){//总长度58，包标记已经读取1个长度
            byte[] errorByte = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(errorByte);
        }else{
            //车辆坐标
//            EBoardPack pack = new EBoardPack();
            GpsPacket gpsPacket = new GpsPacket();
            gpsPacket.setPacketType("gps");
            gpsPacket.setProtocolVersion("HDBS");

//            pack.setHeadFlag(decodeParameter.getHeadFlag());
//            pack.setBodyLength(decodeParameter.getBodyLength());
//            pack.setBodyFlag(decodeParameter.getBodyFlag());
            byte[] lineBytes = new byte[4];
            byteBuf.readBytes(lineBytes);
//            pack.setLineId(MyUtils.bytes2int(lineBytes));//线路ID
            gpsPacket.setGprsId(MyUtils.bytes2int(lineBytes));
            byte[] carBytes = new byte[4];
            byteBuf.readBytes(carBytes);
//            pack.setCarId(MyUtils.bytes2int(carBytes));//车辆ID
            gpsPacket.setVehicleId(MyUtils.bytes2int(carBytes));
            byte[] driverBytes = new byte[4];
            byteBuf.readBytes(driverBytes);
            //todo
//            pack.setDriverId(MyUtils.bytes2int(driverBytes));//司机ID
            byte[] stationBytes = new byte[4];
            byteBuf.readBytes(stationBytes);
            //todo
//            pack.setStationId(MyUtils.bytes2int(stationBytes));//最近经过的站点ID

//            pack.setDateTime(getSimpleDate(byteBuf));//时间
            gpsPacket.setEventTime(getSimpleDate(byteBuf));

//            pack.setLongtitude(getLonAndLat(byteBuf));//经度
            gpsPacket.setLongitude(getLonAndLat(byteBuf));

//      		pack.setLatitude(getLonAndLat(byteBuf));//纬度
            gpsPacket.setLatitude(getLonAndLat(byteBuf));

            byte velocityByte = byteBuf.readByte();
//            pack.setVelocity(MyUtils.bytes2int(new byte[]{velocityByte}));//时速
            gpsPacket.setSpeed(MyUtils.bytes2int(new byte[]{velocityByte}));

            byte[] angleBytes = new byte[2];
            byteBuf.readBytes(angleBytes);
//            pack.setAngle(MyUtils.bytes2int(angleBytes));//方位角
            gpsPacket.setAzimuth(MyUtils.bytes2int(angleBytes));

            byte heightByte = byteBuf.readByte();
//            pack.setHeight(MyUtils.bytes2int(new byte[]{heightByte}));//高度
            gpsPacket.setHeight(MyUtils.bytes2int(new byte[]{heightByte}));

            byte statusByte = byteBuf.readByte();
//            pack.setStatus(MyUtils.bytes2int(new byte[]{statusByte}));//车辆状态  0 为上行 2 下行
            int i = MyUtils.bytes2int(new byte[]{statusByte});
            if (i == 2){i =1;};
            gpsPacket.setDirection(i);

            byte engineTpByte = byteBuf.readByte();
            //todo
//            pack.setEngineTp(MyUtils.bytes2int(new byte[]{engineTpByte}));//发动机温度

            byte carriageTpByte = byteBuf.readByte();
            //todo
//            pack.setCarriageTp(MyUtils.bytes2int(new byte[]{carriageTpByte}));//车厢内温度

            byte[] mileageBytes = new byte[3];
            byteBuf.readBytes(mileageBytes);
//            pack.setMileage(MyUtils.bytes2int(mileageBytes));//日行驶里程
            gpsPacket.setGpsKm(MyUtils.bytes2int(mileageBytes));

            byte[] realTripBytes = new byte[4];
            byteBuf.readBytes(realTripBytes);
            //todo
//            pack.setRealTrip(MyUtils.bytes2int(realTripBytes));//实际趟次

            byte[] planTripBytes = new byte[4];
            byteBuf.readBytes(planTripBytes);
//            todo
//            pack.setPlanTrip(MyUtils.bytes2int(planTripBytes));//计划趟次

            //todo
            getSimpleDate(byteBuf);
//            pack.setRealDate(getSimpleDate(byteBuf));//实际发车时间
            byte runFlagByte = byteBuf.readByte();
            MyUtils.bytes2int(new byte[]{runFlagByte});
//            pack.setRunFlag(MyUtils.bytes2int(new byte[]{runFlagByte}));//运行标记    0 行驶 1 到站 2 离站
            gpsPacket.setRunstatus(1);
            list.add(gpsPacket); // 解析出一条消息
        }
    }
    
    
    public static String getSimpleDate(ByteBuf byteBuf){
    	String datetime="";
    	byte[] dateBytes = new byte[2];
        byteBuf.readBytes(dateBytes);
        int year=MyUtils.bytes2int(dateBytes);
        if(year!=0){
        	byte monthByte = byteBuf.readByte();
            String month=MyUtils.dateHex(MyUtils.bytes2int(new byte[]{monthByte}));
            byte dayByte = byteBuf.readByte();
            String day=MyUtils.dateHex(MyUtils.bytes2int(new byte[]{dayByte}));
            byte hourByte = byteBuf.readByte();
            String hour=MyUtils.dateHex(MyUtils.bytes2int(new byte[]{hourByte}));
            byte fenByte = byteBuf.readByte();
            String fen=MyUtils.dateHex(MyUtils.bytes2int(new byte[]{fenByte}));
            byte miaoByte = byteBuf.readByte();
            String miao=MyUtils.dateHex(MyUtils.bytes2int(new byte[]{miaoByte}));
            datetime=year+"-"+month+"-"+day+" "+hour+":"+fen+":"+miao;
        }
        return datetime;
    }
    
    public static String getLonAndLat(ByteBuf byteBuf){
    	int du=MyUtils.bytes2int(new byte[]{byteBuf.readByte()});
        int fen=MyUtils.bytes2int(new byte[]{byteBuf.readByte()});
        byte[] numBytes = new byte[2];
        byteBuf.readBytes(numBytes);
        double num=MyUtils.bytes2int(numBytes)/10000d;
        String result=(du+(fen+num)/60)+"";
        return result;
    }
    
}
