package com.zhouyue.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成用户工具类
 */
public class UserUtil {

    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user" + i);
            user.setLoginCount(1);
            user.setSalt("1a2b3c");
            user.setRegisterDate(new Date());
            user.setPassword(MD5Util.inputPassToDBPass("zhouyue1998zy", user.getSalt()));
            users.add(user);
        }
        System.out.println("create user");
        //插入数据库
        Connection connection = getConnect();
        String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id) values(?,?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            statement.setInt(1,user.getLoginCount());
            statement.setString(2, user.getNickname());
            statement.setTimestamp(3,new Timestamp(user.getRegisterDate().getTime()));
            statement.setString(4, user.getSalt());
            statement.setString(5, user.getPassword());
            statement.setLong(6, user.getId());
            statement.addBatch();
        }
        statement.executeBatch();
        statement.clearParameters();
        connection.close();
        System.out.println("insert into db");
        //登录，生成userTicket
        String urlString = "http://localhost:8081/login/doLogin";
        File file = new File("C:\\Users\\ASUS\\Desktop\\config2.txt");
        if (file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++){
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("zhouyue1998zy");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0){
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            System.out.println("create userTicket : " + user.getId());
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    private static Connection getConnect() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "zhouyue1998zy";
        String drive = "com.mysql.cj.jdbc.Driver";
        Class.forName(drive);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
