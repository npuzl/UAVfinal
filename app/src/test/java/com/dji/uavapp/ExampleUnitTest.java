package com.dji.uavapp;

import com.dji.uavapp.util.netutil.HttpClient;
import com.dji.uavapp.util.netutil.ServerService;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testLoginReceiveAndSend() throws Exception {
        System.out.println(ServerService.loginSuccess("3303705211@qq.com"));
        System.out.println(Arrays.toString(ServerService.getUserInfo("3303705211@qq.com")));
        System.out.println(ServerService.getUserInfo("3303705211@qq.com")[3]);
       // System.out.println(ServerService.updateUserInfo("宇宙无敌大帅哥","fly_time","12"));
        System.out.println(new File("C:\\UAVapp\\Project\\app\\src\\main\\java\\com\\dji\\uavapp\\util\\DensityUtil.java").getAbsolutePath());
        //距离  时间  昵称
        ServerService.updateFile("tt","C:\\UAVapp\\Project\\app\\src\\main\\java\\com\\dji\\uavapp\\util\\DensityUtil.java");
    }

    @Test
    public void testUpdate() throws Exception {
        System.out.println(ServerService.updateFile("tt","C:\\UAVapp\\Project\\app\\src\\main\\res\\drawable\\jingling4.jpg"));
    }
    @Test
    public void testPath() throws Exception {
        HttpClient httpClient=new HttpClient();
        httpClient.connect("192.144.227.111");
        String jpgs=httpClient.serverice("fileInform","3303705211@qq.com","JPG");
        System.out.println(jpgs);
        //httpClient.connect("192.144.227.111");
        //Thread.sleep(2000);
        httpClient.connect("192.144.227.111");
        String videos=httpClient.serverice("fileInform","3303705211@qq.com","MOV");
        System.out.println(videos);
    }
}