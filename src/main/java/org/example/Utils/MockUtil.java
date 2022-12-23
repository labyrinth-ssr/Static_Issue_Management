package org.example.Utils;

public class MockUtil {
    public static Long time;

    public static void MockBegin(){
        time = TimeUtil.begin();
    }
    public static void MockEnd(String msg){
        System.out.println("查询时间: " + TimeUtil.end(time));
        if(msg!=null) System.out.println(msg);
    }
}
