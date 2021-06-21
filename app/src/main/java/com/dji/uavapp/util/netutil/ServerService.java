package com.dji.uavapp.util.netutil;

import android.util.Log;

import java.io.File;

public class ServerService {

    public static boolean loginSuccess(String username) {
        ;
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.connect("192.144.227.111");
            if (httpClient.serverice("login", username, "password").equals("成功")) {
                httpClient.close();
                return true;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
        //TODO 登录后发送到服务器请求用户信息和其他信息
    }

    public static String[] getUserInfo(String username) throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.connect("192.144.227.111");
        String s = httpClient.serverice("query", username, "").replace("\n", "");
        return s.split(" ");
    }

    public static boolean updateUserInfo(String username, String para, String newPara) throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.connect("192.144.227.111");
        String str = httpClient.serverice("update " + username, para, newPara);
        if (str == "成功")
            return true;
        else return false;
    }

    public static boolean updateFile(String username, String filepath) throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.connect("192.144.227.111");

        String[] name = filepath.split("/");
        httpClient.serverice("login",username, "password");
        System.out.println(name[name.length - 1]);
        if (httpClient.serverice("put", name[name.length - 1], filepath) == "成功") {
            return true;
        }
        return false;
    }
    public static boolean fetchAll(String username,String smallPath) throws Exception {
        HttpClient httpClient=new HttpClient();
        httpClient.connect("192.144.227.111");
        //httpClient.serverice("login",username, "password");
        String jpgs=httpClient.serverice("FileInfro",username,"JPG");
        httpClient.connect("192.144.227.111");
        String videos=httpClient.serverice("FileInfro",username,"MOV");
        String[] jpgssp=jpgs.split(" ");
        String [] videosp=videos.split(" ");
        int nums=Integer.parseInt(jpgssp[0])+Integer.parseInt(videosp[0]);
        if(jpgssp.length!=1){
            for(int i=1;i< jpgssp.length;i++){
                if(!httpClient.serverice("fileData "+username,jpgssp[i],smallPath+jpgssp[i]).equals("成功")){
                    Log.d("Cloud",jpgssp[i]+" download failed");
                }
            }
        }
        if(videosp.length!=1) {
            for (int i = 1; i < videosp.length; i++) {
                if (!httpClient.serverice("fileData " + username, videosp[i], smallPath + videosp[i]).equals("成功")) {
                    Log.d("Cloud", videosp[i] + " download failed");
                }
            }
        }
        return true;
    }
    public static boolean download(String username,String filepath) throws Exception {
        HttpClient httpClient=new HttpClient();
        httpClient.connect("192.144.227.111");
        String []filename=filepath.split("/");
        File f=new File(filepath);
        String parent=f.getParentFile().getParentFile().getAbsolutePath()+filename[filename.length-1];
        httpClient.serverice("get",filename[filename.length-1],parent);
        return true;
    }

}

//        send("username "+username+" login");
//        receive("是否成功");
//        return true;
//    }
//    public static boolean sendPic(String user, File file){
//        send("username "+user+" picture "+file.size());
//        receive("是否成功")
//    }
//    sendVideo(String user,File file){
//        send("username "+user+" video "+file.size());
//        receive("是否成功")
//    }
//
//    receiveCloud(Sting user){
//        send("username "+user+"allfilename");
//        receive();
//        /**
//         * 10
//         * 6
//         * 4
//         *
//         *
//         *
//         *
//         *
//         *
//         *
//         *
//         *
//         *
//         */
//    }
//    receive(String user ,String filename){
//        send("username "+user+" "+filename);
//        receive();

