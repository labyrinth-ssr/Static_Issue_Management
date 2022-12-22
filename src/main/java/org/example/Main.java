package org.example;

import org.apache.commons.lang.StringUtils;
import org.example.Command.QueryCommand;
import org.example.Command.QueryMappingByDev;
import org.example.Command.QueryMappingById;
import org.example.Command.QueryMappingByTime;
import org.example.SonarConfig.SonarResult;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.useDataBase("sonarissue");
        QueryCommand queryCommand = new QueryCommand(mysqlConnect);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = null;
        List<String> my_args = null;

        QueryMappingById queryMappingById = new QueryMappingById(mysqlConnect);
        QueryMappingByTime queryMappingByTime = new QueryMappingByTime(mysqlConnect);
        QueryMappingByDev queryMappingByDev = new QueryMappingByDev(mysqlConnect);
        while(true) {
            System.out.println("" +
                    "\n-----------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                    "| latest_defect 最新版本中，静态缺陷数量的分类统计和详细列表                                                                                                \t|\n" +
                    "| defect -c [commit_hash] 指定版本代码快照中静态缺陷引入、消除情况的分类统计和详细列表                                                                         \t|\n" +
                    "| defect -t [begin_time=end_time](如2022-12-21 12:12:12=) 指定时间段内，静态缺陷引入、消除情况的分类统计和详细列表                                             \t|\n" +
                    "| analysis [begin_time=end_time](如2022-12-21 12:12:12=) 指定时间段内引入静态缺陷的数量，解决情况,包括解决率、解决所用的时间，按总量以及分各个缺陷大类和具体类型统计     \t|\n" +
                    "| duration [min_duration](如21天12时30分18秒) 现存静态缺陷中，已经存续超过指定时长的分类情况统计                                                               \t|\n" +
                    "| devs [committer_name] 某个开发人员引入缺陷、解决他人引入缺陷、自己引入且尚未解决缺陷、自己引入且被他人解决缺陷的分类统计，存活周期统计                                 \t|\n" +
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
            System.out.print("输入命令# ");
            str = br.readLine();
            if(str.startsWith("latest_defect")){
                String latest = queryMappingById.getCommitLatest();
                queryMappingById.getCountInByCommit_id(latest);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingById.getGCountInTypeByCommit_id(latest);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingById.getListInByCommit_id(latest);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");


                queryMappingById.getCountDoneByCommit_id(latest);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingById.getCountDoneInTypeByCommit_id(latest);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingById.getListDoneByCommit_id(latest);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");

                queryMappingById.getGCountUnsolvedByCommit_id(latest);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");

            }else if(str.startsWith("defect -c")){
                String commit_hash = queryMappingById.getCommit_idByCommit_hashAndRepo(str.substring(9).trim(), Constant.RepoPath);
                queryMappingById.getCountInByCommit_id(commit_hash);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingById.getGCountInTypeByCommit_id(commit_hash);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingById.getListInByCommit_id(commit_hash);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");

                queryMappingById.getCountDoneByCommit_id(commit_hash);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingById.getCountDoneInTypeByCommit_id(commit_hash);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingById.getListDoneByCommit_id(commit_hash);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");

                queryMappingById.getGCountUnsolvedByCommit_id(commit_hash);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
            }else if(str.startsWith("defect -t")){
                str = str.substring(9).trim();
                String[] s = str.split("=");
                if(!checkTimeString(s)) continue;
                String begin_time = s[0];
                String end_time = getString2(s);
                System.out.println(begin_time+" "+end_time);
                queryMappingByTime.getCountInByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByTime.getGCountInTypeByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByTime.getListInByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByTime.getCountDoneByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByTime.getCountDoneInTypeByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByTime.getListDoneByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByTime.getGCountUnsolvedByCommit_time(begin_time,end_time,Constant.RepoPath);

            }else if(str.startsWith("analysis")){
                str = str.substring(8).trim();
                String[] s = str.split("=");
                if(!checkTimeString(s)) continue;
                String begin_time = s[0];
                String end_time = getString2(s);
                queryMappingByTime.getAnalysisCountByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByTime.getAnalysisCountByTypeByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByTime.getAnalysisCountByType_idByCommit_time(begin_time,end_time,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
            }else if(str.startsWith("duration")){
                str = str.substring(8).trim();
                queryMappingByTime.getDefectListMoreThanDuration(str,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
            }else if(str.startsWith("devs")){
                str = str.substring(4).trim();
                queryMappingByDev.getDevCountByDevs(str,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
                queryMappingByDev.getDevTypeCountByDevs(str,Constant.RepoPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
            }
        }
    }

    public static String getString2(String[] s){
        if(s.length<2) return "";
        return s[1];
    }

    public static boolean checkTimeString(String[] str){
        for(String s : str) {
            if(s.trim()=="") continue;
            String[] sl = s.split(" ");
            boolean i = sl[0].contains("-");
            String s1 = sl[0];
            if (sl.length > 2) return false;
            if (i && s1.split("-").length != 3) return false;
            else
                for (String sd : s1.split("-")) {
                    if (!StringUtils.isNumeric(sd)) return false;
                }
            if (!i || (sl.length == 2 && (s1 = sl[1]).length() != 0)) {
                if (s1.split(":").length != 3) return false;
                else {
                    for (String sd : s1.split(":")) {
                        if (!StringUtils.isNumeric(sd)) return false;
                    }
                }
            }
        }
        return true;
    }
}