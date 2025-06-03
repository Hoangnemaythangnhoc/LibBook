package com.example.libbook.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectUtils {
    String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String url="jdbc:sqlserver://127.0.0.1:1433;databaseName=LibBook;encrypt=true;trustServerCertificate=true;";
    String user="sa";
    String password = "1234";

    private static ConnectUtils instance;

    public ConnectUtils(){

    }

    public Connection openConection() throws Exception{
        Class.forName(driverName);
        Connection con = DriverManager.getConnection(url,user,password);
        return con;
    }
    public static ConnectUtils getInstance(){
        if (instance == null){
            instance = new ConnectUtils();
        }
        return  instance;
    }
}
