package org.example.Query;

import org.apache.commons.lang.StringUtils;
import org.example.Entity.Commit;
import org.example.Query.Value.String2Value;
import org.example.Utils.SqlConnect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Cmd {
    public static void run() throws Exception {run("sonarissue");}
    public static void run(String table_name) throws Exception {
        SqlConnect mysqlConnect = new SqlConnect();
        mysqlConnect.useDataBase(table_name);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = null;
        List<String> my_args = null;

        QueryMappingById queryMappingById = new QueryMappingById(mysqlConnect);
        QueryMappingByTime queryMappingByTime = new QueryMappingByTime(mysqlConnect);
        QueryMappingByDev queryMappingByDev = new QueryMappingByDev(mysqlConnect);

        String REPO_PATH;
        List<String2Value> repos = queryMappingById.getRepo();
        System.out.println("当前存储仓库: ");
        System.out.println("==================================================================");
        for(String2Value repo : repos) System.out.println(repo.getStringValue1() + ", " + repo.getStringValue2());
        System.out.println("==================================================================");
        while(true) {
            System.out.print("请选择要查询的仓库路径# ");
            REPO_PATH = br.readLine();
            REPO_PATH = REPO_PATH.trim();
            if(queryMappingById.repoIn(REPO_PATH)) break;
        }

        while(true) {
            System.out.println("" +
                    "\n-----------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                    "| latest_defect 最新版本中，静态缺陷数量的分类统计和详细列表                                                                                                \t|\n" +
                    "| defect -c [commit_hash] 指定版本代码快照中静态缺陷引入、消除情况的分类统计和详细列表                                                                         \t|\n" +
                    "| defect -t [begin_time=end_time](如2022-12-21 12:12:12=) 指定时间段内，静态缺陷引入、消除情况的分类统计和详细列表                                             \t|\n" +
                    "| analysis [begin_time=end_time](如2022-12-21 12:12:12=) 指定时间段内引入静态缺陷的数量，解决情况,包括解决率、解决所用的时间，按总量以及分各个缺陷大类和具体类型统计     \t|\n" +
                    "| duration [min_duration](如21天12时30分18秒) 现存静态缺陷中，已经存续超过指定时长的分类情况统计                                                               \t|\n" +
                    "| devs [committer_name] 某个开发人员引入缺陷、解决他人引入缺陷、自己引入且尚未解决缺陷、自己引入且被他人解决缺陷的分类统计，存活周期统计                                 \t|\n" +
                    "| (辅助命令) commits 查看版本信息                                                                                                                      \t|\n" +
                    "| (辅助命令) index 删除或添加索引                                                                                                                      \t|\n" +
                    "| (辅助命令) quit 退出查询                                                                                                                            \t|\n" +
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------\n");
            System.out.print("输入命令# ");
            str = br.readLine();
            if(str.startsWith("latest_defect")){
                String latest = queryMappingById.getCommitLatest(REPO_PATH);
                System.out.println(latest);
                queryMappingById.getCountInByCommit_id(latest,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingById.getGCountInTypeByCommit_id(latest,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingById.getListInByCommit_id(latest,false);
                System.out.println("\n");


                queryMappingById.getCountDoneByCommit_id(latest,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingById.getCountDoneInTypeByCommit_id(latest,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingById.getListDoneByCommit_id(latest,false);
                System.out.println("\n");

                queryMappingById.getGCountUnsolvedByCommit_id(latest,false);
            }else if(str.startsWith("defect -c")){
                String commit_hash = queryMappingById.getCommit_idByCommit_hashAndRepo(str.substring(9).trim(), REPO_PATH);
                queryMappingById.getCountInByCommit_id(commit_hash,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingById.getGCountInTypeByCommit_id(commit_hash,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingById.getListInByCommit_id(commit_hash,false);
                System.out.println("\n");

                queryMappingById.getCountDoneByCommit_id(commit_hash,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingById.getCountDoneInTypeByCommit_id(commit_hash,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingById.getListDoneByCommit_id(commit_hash,false);
            }else if(str.startsWith("defect -t")){
                str = str.substring(9).trim();
                String[] s = str.split("=");
                if(!checkTimeString(s)) continue;
                String begin_time = s[0];
                String end_time = getString2(s);
                System.out.println(begin_time+" "+end_time);
                queryMappingByTime.getCountInByCommit_time(begin_time,end_time,REPO_PATH,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingByTime.getGCountInTypeByCommit_time(begin_time,end_time,REPO_PATH,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingByTime.getListInByCommit_time(begin_time,end_time,REPO_PATH,false);
                System.out.println("\n");
                queryMappingByTime.getCountDoneByCommit_time(begin_time,end_time,REPO_PATH,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingByTime.getCountDoneInTypeByCommit_time(begin_time,end_time,REPO_PATH,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingByTime.getListDoneByCommit_time(begin_time,end_time,REPO_PATH,false);
            }else if(str.startsWith("analysis")){
                str = str.substring(8).trim();
                String[] s = str.split("=");
                if(!checkTimeString(s)) continue;
                String begin_time = s[0];
                String end_time = getString2(s);
                queryMappingByTime.getAnalysisCountByCommit_time(begin_time,end_time,REPO_PATH,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingByTime.getAnalysisCountByTypeByCommit_time(begin_time,end_time,REPO_PATH,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingByTime.getAnalysisCountByType_idByCommit_time(begin_time,end_time,REPO_PATH,false);
            }else if(str.startsWith("duration")){
                str = str.substring(8).trim();
                queryMappingByTime.getDefectListMoreThanDuration(str,REPO_PATH,false);
            }else if(str.startsWith("devs")){
                str = str.substring(4).trim();
                if(str.equals("")) continue;
                queryMappingByDev.getDevCountByDevs(str,REPO_PATH,false);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
                queryMappingByDev.getDevTypeCountByDevs(str,REPO_PATH,false);
            }else if(str.startsWith("commits")){
                queryMappingById.getCommits(REPO_PATH);
            }else if(str.startsWith("quit")){
                queryMappingById.quit();
                break;
            }else if(str.startsWith("index")){
                queryMappingById.index();
            }
        }
    }

    public static String getString2(String[] s){
        if(s.length<2) return "";
        return s[1];
    }

    public static boolean checkTimeString(String[] str){
        for(String s : str) {
            s = s.trim();
            if(s.equals("")) continue;
            String[] sl = s.split(" ");
            if(!sl[0].contains("-")) return false;
            String s1 = sl[0];
            if (sl.length > 2) return false;
            if (s1.split("-").length != 3) return false;
            else
                for (String sd : s1.split("-")) {
                    if (!StringUtils.isNumeric(sd)) return false;
                }
            if (sl.length == 2 && (s1 = sl[1]).length() != 0) {
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
