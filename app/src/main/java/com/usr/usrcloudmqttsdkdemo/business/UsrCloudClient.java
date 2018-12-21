package com.usr.usrcloudmqttsdkdemo.business;

import org.eclipse.paho.client.mqttv3.MqttException;

import cn.usr.Interface.UsrCloudMqttCallback;
import cn.usr.UsrCloudMqttClientAdapter;

/**
 * Created by dyzhou on 2018/8/8.
 */

public class UsrCloudClient extends UsrCloudMqttClientAdapter {

    @Override
    public void Connect(String userName, String passWord) throws MqttException {
        super.Connect(userName, passWord);
    }



    @Override
    public boolean DisConnectUnCheck() throws MqttException {
        return super.DisConnectUnCheck();
    }

    @Override
    public void SubscribeForDevId(String devId) throws MqttException {
        super.SubscribeForDevId(devId);
    }

    @Override
    public void SubscribeForUsername() throws MqttException {
        super.SubscribeForUsername();
    }

    @Override
    public void DisSubscribeforDevId(String devId) throws MqttException {
        super.DisSubscribeforDevId(devId);
    }

    @Override
    public void DisSubscribeforuName() throws MqttException {
        super.DisSubscribeforuName();
    }

    @Override
    public void setUsrCloudMqttCallback(UsrCloudMqttCallback CloudMqttCallback) {
        super.setUsrCloudMqttCallback(CloudMqttCallback);
    }

    @Override
    public void publishForDevId(String devId, byte[] data) throws MqttException {
        super.publishForDevId(devId, data);
    }

    @Override
    public void publishForuName(byte[] data) throws MqttException {
        super.publishForuName(data);
    }
}
