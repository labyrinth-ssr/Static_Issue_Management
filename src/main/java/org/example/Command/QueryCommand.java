package org.example.Command;

import org.example.Utils.SqlConnect;
import org.example.Utils.SqlQuery;

import java.util.List;

public class QueryCommand {

    public SqlConnect connect;
    public SqlQuery sqlQuery;
    public QueryCommand(SqlConnect connect) {
        this.connect = connect;
        this.sqlQuery = new SqlQuery(connect);
    }

    public String repo_id;
    /**
     * 显示静态缺陷数量的分类统计和详细列表：
     * 应使用命令：defect
     * 默认情况：最新版本，按类型统计（显示存续时长平均值和中位数），按存续时长排序
     * options:
     * -v [commit_id](version) 指定版本
     * -av (all version) 所有版本
     * -t [time_begin(yyyy-MM-dd)--time_end(yyyy-MM-dd)](time) 指定时间段，可以只使用一部分
     * -d [type_id] 指定缺陷
     * 显示类型：
     * commit_id, committer, commit_time, commit_message -- DefectCommitEntity
     *      type_id, type_msg
     *                  inst_id, case_id, exist_duration
     *                  file_path, start_line, String end_line,start_col, end_col
     *                  class_, method
     *                  code -- DefectEntity
    */
    public void ShowDefect(List<String> args){
        boolean all_version = false;
        boolean all_time = true;
        boolean all_type = true;
        String commit_id = null;
        String begin_time, end_time;
        String type_id;
        for(String arg : args){
            if(arg.startsWith("-av")){
                if(commit_id == null) all_version = true;
                else{
                    ShowHelp.defect();
                    return;
                }
            }else if(arg.startsWith("-v")){
                if(all_version) {
                    ShowHelp.defect();
                    return;
                }else commit_id = arg.substring(2).trim();
            }else if(arg.startsWith("-t")){
                String twoTime = arg.substring(2).trim();
                begin_time = twoTime.substring(0, twoTime.indexOf("--")).trim();
                end_time = twoTime.substring(twoTime.indexOf("--")+2).trim();
            }else if(arg.startsWith("-d")){
                all_type = false;
                type_id = arg.substring(2).trim();
            }
        }

    }

    /**
     * 数据分析统计
     * 应使用命令：analysis
     * 默认情况：所有时间内引入静态缺陷的数量、解决数量、解决率，按总量以及各个缺陷大类和具体类型统计
     * -u [user_name](user) 指定开发人员，根据引入缺陷、解决他人引入缺陷、自己引入且尚未解决缺陷、自己引入且被他人解决缺陷的分类统计，平均存活时间（存活周期）统计。
     * -t [time_begin--time_end](time) 指定时间段
     * -tmt [duration](time more than) 指定最小存续时长，按时长从大到小排序
     * -l (list) 显示详细列表
     * */
    public void ShowAnalysis(List<String> args){}

    /**
     * 显示开发人员信息
     * 应使用命令：devs
     * */
    public void ShowDevelopers(){}

    /**
     * 显示所有仓库信息
     * 应使用命令：repos
     * */
    public void ShowRepos(){}

    /**
     * 指定当前仓库环境，除repos其他所有命令使用前必须指定
     * 应使用命令：use [repo_id]
     * */
    public void UseRepo(List<String> args){}

    /**
     * 显示所有commit信息，按照提交顺序排序
     * 应使用命令：commits
     */
    public void ShowCommits(){}

    /**
     * 显示某一缺陷的历史变化图
     * 应使用命令：display
     * 必要参数：
     * -c [case_id]
     * -i [instance_id] 指定实例，显示所属缺陷历史
     * -ui [instance_id] 向上显示缺陷历史
     * -di [instance_id] 向下显示缺陷历史
     * */
    public void DisplayCase(List<String> args){}

    /**
     * 显示具体信息
     * 应使用命令：show
     * 必要参数：
     * -i [instance_id]
     * -m [commit_id]
     * -c [case_id]
     * */
    public void ShowInfo(List<String> args){}

}
