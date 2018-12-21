package com.unicom.autoship.net;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;



import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_battery_status;
import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_gps_raw_int;
import com.MAVLink.common.msg_rc_channels;
import com.MAVLink.common.msg_vfr_hud;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;



public class SocketClient implements Runnable{

	//private static final Logger logger  = LoggerFactory.getLogger(SocketClient.class);

	public boolean isRunning=true;
	private Socket socket ;
	
	private String boatNo;
	
	//private Executor executor ;
	private Handler handler;
	
	//private BoatLocationsService boatLocationsService ;
	
	//private Socket socket;
	
	private String ip;
	
	private int port;

	InputStream is;
	OutputStream out;
	
	public SocketClient(String ip , int port , String boatNo ,  Handler handler//Executor executor //,// BoatLocationsService boatLocationsService ,
						//SocketIOServer socketIOServer
    ) throws //UnknownHostException,
			IOException {
		this.boatNo = boatNo;
		this.ip = ip;
		this.port = port;
		this.handler = handler;
		//this.executor = executor;
		//this.boatLocationsService = boatLocationsService;
		//this.socketIOServer = socketIOServer;
		connectServer();
		//executor.execute(this);
		Logger.i("已连接设备{"+boatNo+"} , ip:{"+ip+"} , port:{"+port+"}");
	}

	public boolean isConnected(){
		return socket.isConnected();
	}
	
	//连接船只
	private void connectServer() throws IOException {

		isRunning=true;
		socket = new Socket(InetAddress.getByName(ip), port);
		socket.setSoTimeout(60000);

		if(socket.isConnected()){
			Logger.i("socket is Connected!!!!!!!!!!!!!!!!");
		}
	}
	
	//通补发送船控指令
	public void send(MAVLinkMessage message) throws IOException {
		if(socket.isConnected() && !socket.isClosed()) {
			out = socket.getOutputStream();
			out.write(message.pack().encodePacket());
			out.flush();
		}else {
			throw new SocketException("与服务端失去连接");
		}
	}

	//线程异步读取数据
	@Override
	public void run() {
		while(isRunning) {
			if(!socket.isConnected() || socket.isClosed()) {
                Logger.i("与服务端失去连接10秒后重试");
				try {
					Thread.currentThread();
					Thread.sleep(10000);
					connectServer();
				} catch (Exception e) {
                    Logger.i("连接异常" , e);
				}
				continue;
			}
	    	is = null;
				try {
					is = socket.getInputStream();
			    	Parser parser = new Parser();
			    	int i;
			        while((i=is.read())!=-1) {
						MAVLinkPacket receivedPacket = parser.mavlink_parse_char(i & 0x00ff);//每次解析1位
						if(receivedPacket!=null ) {
							MAVLinkMessage message = receivedPacket.unpack();
							if(message!=null && message instanceof msg_gps_raw_int) {//经纬度
								Logger.i("经纬度Response:"+message+",设备号:"+boatNo+",经度:"+((msg_gps_raw_int)message).lon);
		                		/*BoatLocations boatLocation = ((msg_gps_raw_int)message).boatLocation;
		                		boatLocation.setTimeStamp(new LocalDateTime());
		                		boatLocation.setBoatNo(boatNo);
		                		BoatLocations _from = boatLocationsService.findLastLocation(boatNo);
		                		if(_from!=null && new DateTime(boatLocation.getTimeStampLong()).compareTo(new DateTime(_from.getTimeStampLong()).plusSeconds(1))<0) {//时间间隔1秒以内丢弃
		    						continue;
		    					}
		                		executor.execute(new Runnable() {
		        					public void run() {
		        						boatLocationsService.addLocation(boatLocation);
		        					}
		        				});*/
		                		Message msg = handler.obtainMessage();
		                		msg.what=0x001;
		                		msg.obj=message;
		                		handler.sendMessage(msg);

		                	}else if(message!=null && message instanceof msg_vfr_hud ){//速度和航行方向
		                		msg_vfr_hud vfrHud = (msg_vfr_hud)message;
								Logger.i("速度和航行方向Response:"+message+",设备号:"+boatNo);
		                		//socketIOServer.getBroadcastOperations().sendEvent("speedEvent"+boatNo, JSONObject.parse("{\"speed\":"+vfrHud.groundspeed+",\"heading\":"+vfrHud.heading+"}"));
								Message msg = handler.obtainMessage();
								msg.what=0x002;
								msg.obj=message;
								handler.sendMessage(msg);
							}else if(message!=null && message instanceof msg_battery_status) {//电量
		                		msg_battery_status batteryStatus  = (msg_battery_status)message;
								Logger.i("电量Response:"+message+",设备号:"+boatNo);
		                		//socketIOServer.getBroadcastOperations().sendEvent("batteryEvent"+boatNo, JSONObject.parse("{\"batteryRemaining\":"+batteryStatus.battery_remaining+"}"));
								Message msg = handler.obtainMessage();
								msg.what=0x003;
								msg.obj=message;
								handler.sendMessage(msg);
							}else if(message!=null && message instanceof msg_rc_channels) {//水质数据
		                		/*ProbeInfo probeInfo = ((msg_rc_channels) message).probeInfo;
		                		probeInfo.setBoatNo(boatNo);
		                		ProbeInfo _from = boatLocationsService.findLastProbe(boatNo);
		                		if(_from!=null && new DateTime(probeInfo.getTimeStampLong()).compareTo(new DateTime(_from.getTimeStampLong()).plusSeconds(2))<0) {//时间间隔2秒以内丢弃
		        					continue;
		        				}
		                		executor.execute(new Runnable() {
		        					public void run() {
		        						boatLocationsService.addProbeInfo(probeInfo);
		        					}
		        				});*/
		                	}else if(message!=null && (message instanceof msg_command_ack) ) {
                                Logger.i("收到命令执行应答:"+message+",设备号:"+boatNo);
		                		msg_command_ack result = (msg_command_ack) message;
		                		//socketIOServer.getBroadcastOperations().sendEvent("changeModeResultEvent"+boatNo , JSONObject.parse("{\"result\":"+result.result+"}"));
								Message msg = handler.obtainMessage();
								msg.what=0x005;
								msg.obj=message;
								handler.sendMessage(msg);
							}
						}
					}
			} catch (Exception e) {
                    Logger.w("读取服务端数据异常,等待重新连接,ip:{} , port:{}" , ip , port);
				try {
					if(is!=null)
						is.close();
					if(!socket.isClosed())
						socket.close();
				} catch (Exception e1) {
                    Logger.e("关闭连接异常,ip:{} , port:{}" , ip , port, e);
				}
			}
		}
	}

	public void disConnectToServer(){
        try
        {

        	isRunning=false;
            if (is != null)
                is.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }finally {
        	isRunning=false;
		}
    }
}
