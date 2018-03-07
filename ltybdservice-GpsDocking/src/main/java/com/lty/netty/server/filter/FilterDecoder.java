package com.lty.netty.server.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.lty.common.bean.gps.Authentication;
import com.lty.common.bean.gps.EBoardPack;
import com.lty.common.util.Coordinate;
import com.lty.common.util.MyUtils;


/**
 * @author zhouyongbo 2017/12/3
 * 解码
 */
public class FilterDecoder extends ByteToMessageDecoder {
	
	
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
    	
    	if(byteBuf.readableBytes() > 2){  
            // 标记开始位置  
    		byteBuf.markReaderIndex();  
            String headFlag = "$@$@";
            byte[] bodyLengthBytes = new byte[2];
			byteBuf.readBytes(bodyLengthBytes);
			int bodyLength = MyUtils.bytes2int(bodyLengthBytes);//包长
			byte bodyFlagByte = byteBuf.readByte();
			String bodyFlag = MyUtils.convertHexString(new byte[]{bodyFlagByte});//包标记
			if(bodyFlag.equals("80")){
				if(byteBuf.readableBytes() < 57){//总长度58，包标记已经读取1个长度
					byte[] errorByte = new byte[byteBuf.readableBytes()];
            		byteBuf.readBytes(errorByte);
				}else{
					//车辆坐标
					decodeFormalGps(headFlag,bodyLength,bodyFlag,channelHandlerContext,byteBuf,list);
				}
			}else if(bodyFlag.equals("81")){
				if(byteBuf.readableBytes() < 1){//总长度2，包标记已经读取1个长度
					byte[] errorByte = new byte[byteBuf.readableBytes()];
            		byteBuf.readBytes(errorByte);
				}else{
					//身份确认
					decodeAuthentication(headFlag,bodyLength,bodyFlag,channelHandlerContext,byteBuf,list);
				}
			}else{//其他包直接跳过，不处理
				byteBuf.skipBytes(byteBuf.readableBytes());
			}
			//如果到此时包还没有读完，为了不影响下一次解包，读完此次剩余数据
			if(byteBuf.readableBytes() > 0){
				//出错的数据
				byte[] errorByte = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(errorByte);
			}
        }else{//包的长度不满足包头的长度，直接跳过这个包
        	byteBuf.skipBytes(byteBuf.readableBytes());
        }
    }
    
    
   //gps数据车辆坐标包
   private void decodeFormalGps(String headFlag,int bodyLength,String bodyFlag,ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception{
	    EBoardPack pack = new EBoardPack(); 
	    pack.setHeadFlag(headFlag);
	    pack.setBodyLength(bodyLength);
	    pack.setBodyFlag(bodyFlag);
	    byte[] lineBytes = new byte[4];
 		in.readBytes(lineBytes);
 		pack.setLineId(MyUtils.bytes2int(lineBytes));//线路ID
 		byte[] carBytes = new byte[4];
  		in.readBytes(carBytes);
  		pack.setCarId(MyUtils.bytes2int(carBytes));//车辆ID
  		byte[] driverBytes = new byte[4];
  		in.readBytes(driverBytes);
  		pack.setDriverId(MyUtils.bytes2int(driverBytes));//司机ID
  		byte[] stationBytes = new byte[4];
  		in.readBytes(stationBytes);
  		pack.setStationId(MyUtils.bytes2int(stationBytes));//最近经过的站点ID
  		byte[] dateBytes = new byte[7];
  		in.readBytes(dateBytes);
  		pack.setDateTime(MyUtils.convertHexString(dateBytes));//时间
  		byte[] lonBytes = new byte[4];
  		in.readBytes(lonBytes);
  		int lonFlag = ((lonBytes[0] & 0x80) == 128?1:-1);
  		lonBytes[0] = (byte) (lonBytes[0] & 0x7F);
  		int longtitudeNum = MyUtils.bytes2int(lonBytes);
  		int lonDu = longtitudeNum/1000000;
  		double lonFen = (longtitudeNum%1000000)/10000.0;
  		double lonDuReal = lonDu + (lonFen/60.0);//度=度+分/60
  		Coordinate lonCoordinate = new Coordinate(lonDuReal);
  		pack.setLongtitude(""+lonCoordinate.getDoubleValue()*lonFlag);//经度
  		byte[] latBytes = new byte[4];
  		in.readBytes(latBytes); 
  		//16进制数据中最高位为1表示北纬，为0表示南纬 ；
  		//所以flag为1表示北纬  ，为-1表示南纬
  		int latFlag = ((latBytes[0] & 0x80) == 128?1:-1);
  		latBytes[0] = (byte) (latBytes[0] & 0x7F);
  		int latitudeNum = MyUtils.bytes2int(latBytes);
  		int latDu = latitudeNum/1000000;
  		double latFen = (latitudeNum%1000000)/10000.0;
  		double latDuReal = latDu + (latFen/60.0);//度=度+分/60
  		Coordinate latCoordinate = new Coordinate(latDuReal);
  		pack.setLatitude(""+latCoordinate.getDoubleValue()*latFlag);//纬度
  		byte velocityByte = in.readByte();
		pack.setVelocity(MyUtils.bytes2int(new byte[]{velocityByte}));//时速
		byte[] angleBytes = new byte[2];
  		in.readBytes(angleBytes);
  		pack.setAngle(MyUtils.bytes2int(angleBytes));//方位角
  		byte heightByte = in.readByte();
		pack.setHeight(MyUtils.bytes2int(new byte[]{heightByte}));//高度
		byte statusByte = in.readByte();
		pack.setStatus(MyUtils.bytes2int(new byte[]{statusByte}));//车辆状态  0 为上行 2 下行
		byte engineTpByte = in.readByte();
		pack.setEngineTp(MyUtils.bytes2int(new byte[]{engineTpByte}));//发动机温度
		byte carriageTpByte = in.readByte();
		pack.setCarriageTp(MyUtils.bytes2int(new byte[]{carriageTpByte}));//车厢内温度
		byte[] mileageBytes = new byte[3];
  		in.readBytes(mileageBytes);
		pack.setMileage(MyUtils.bytes2int(mileageBytes));//行驶里程
		byte[] realTripBytes = new byte[4];
  		in.readBytes(realTripBytes);
  		pack.setRealTrip(MyUtils.bytes2int(realTripBytes));//实际趟次
  		byte[] planTripBytes = new byte[4];
  		in.readBytes(planTripBytes);
  		pack.setPlanTrip(MyUtils.bytes2int(planTripBytes));//计划趟次
  		byte[] realDateBytes = new byte[7];
  		in.readBytes(realDateBytes);
  		pack.setRealDate(MyUtils.convertHexString(realDateBytes));//实际发车时间
  		byte runFlagByte = in.readByte();
		pack.setRunFlag(MyUtils.bytes2int(new byte[]{runFlagByte}));//运行标记    0 行驶 1 到站 2 离站
  		out.add(pack); // 解析出一条消息  
      }
   
   //gps数据身份确认回应包
   private void decodeAuthentication(String headFlag,int bodyLength,String bodyFlag,ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception{
	   Authentication pack=new Authentication();
	   pack.setHeadFlag(headFlag);
	   pack.setBodyLength(bodyLength);
	   pack.setBodyFlag(bodyFlag);
	   byte loginStatusByte = in.readByte();
	   int loginStatus =MyUtils.bytes2int(new byte[]{loginStatusByte});//0:表示登录成功 1:接入 IP不正确 2:接入密码不正确 3:用户未注册 99 其他
	   pack.setLoginStatus(loginStatus);
	   out.add(pack); // 解析出一条消息  
   }
    
}
