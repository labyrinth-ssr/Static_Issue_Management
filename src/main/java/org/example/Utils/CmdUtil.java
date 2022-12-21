package org.example.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CmdUtil {
    private static void printLines(String cmd, InputStream ins, boolean out) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            if(out) System.out.println(cmd + " " + line);
        }
    }
    public static void runProcess(String path, String command) throws Exception {


        System.out.print(", Sonarqube scanning ...");
        TimeUtil.begin();
        Process pro = Runtime.getRuntime().exec("cmd /c "+ command, null, new File(path));
        printLines(command + " stdout:", pro.getInputStream(), false);
        printLines(command + " stderr:", pro.getErrorStream(),true);
        pro.waitFor();
        System.out.println(command + " " + (pro.exitValue()==0?"DONE!!":"FAIL!!") + ", time:"+TimeUtil.end());
    }
}
