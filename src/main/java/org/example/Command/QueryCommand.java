package org.example.Command;

import org.example.Entity.Commit;
import org.example.Entity.Iss_case;
import org.example.Entity.Iss_match;
import org.example.Entity.Repository;
import org.example.QueryUseEntity.*;
import org.example.Utils.SqlConnect;
import org.example.Utils.SqlQuery;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
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
    public void ShowDefect(List<String> args) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        System.out.println(args.toString());
        boolean all_version = false;
        boolean all_time = true;
        boolean all_type = true;
        String commit_id = null;
        String begin_time = null, end_time = null;
        String type_id = null;
        for(String arg : args){
            arg = arg.trim();
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
            }else if(arg.equals("")){
                continue;
            }else{
                System.out.println("arg" + arg);
                ShowHelp.defect();
                return;
            }
        }

        if(!all_version && commit_id == null) commit_id = sqlQuery.getLatestCommitId();
        List<DefectCommitEntity> defectCommitEntities = sqlQuery.getDefetcCommit(commit_id, begin_time, end_time);
        for(DefectCommitEntity defectCommitEntity: defectCommitEntities){
            System.out.println(defectCommitEntity.toString());
            List<DefectTypeEntity> defectTypeEntities = null;
            if(all_type) {
                defectTypeEntities = sqlQuery.getDefectType(defectCommitEntity.getCommit_hash());
            }
            List<DefectEntity> defectEntities = null;
            if(defectTypeEntities != null){
                for(DefectTypeEntity defectType : defectTypeEntities) {
                    System.out.println(defectType.toString());
                    defectEntities = sqlQuery.getDefectEntity(defectCommitEntity.getCommit_hash(), defectType.getType_id());
                    for(DefectEntity defect : defectEntities) System.out.println(defect.toString());
                }
            }else{
                defectEntities = sqlQuery.getDefectEntity(defectCommitEntity.getCommit_hash(), type_id);
                for(DefectEntity defect : defectEntities) System.out.println(defect.toString());
            }
        }

    }

    /**
     * 数据分析统计
     * 应使用命令：analysis
     * 默认情况：所有时间内引入静态缺陷的数量、解决数量、解决率，按总量以及各个缺陷大类和具体类型统计
     * -u [user_name](user) 指定开发人员，根据引入缺陷、解决他人引入缺陷、自己引入且尚未解决缺陷、自己引入且被他人解决缺陷的分类统计，平均存活时间（存活周期）统计。
     * -t [time_begin--time_end](time) 指定时间段
     * -c [commit_hash](commit) 指定版本
     * -d [type_id](type_id) 指定缺陷类型
     * -md [duration](min duration) 指定最小存续时长，按时长从大到小排序
     * -l (list) 显示详细列表
     * */
    public void ShowAnalysis(List<String> args){
        String user = null;
        String begin_time = null;
        String end_time =null;
        String min_duration = null;
        boolean is_list = false;
        String commit_hash = null;
        String type_id = null;
        for(String arg : args){
            arg = arg.trim();
            if(arg.equals("")) continue;
            else if(arg.startsWith("-u")){
                user = arg.substring(2).trim();
            }else if(arg.startsWith("-t")){
                String twoTime = arg.substring(2).trim();
                begin_time = twoTime.substring(0, twoTime.indexOf("--")).trim();
                end_time = twoTime.substring(twoTime.indexOf("--")+2).trim();
            }else if(arg.startsWith("-md")){
                min_duration = arg.substring(3).trim();
            }else if(arg.startsWith("-l")){
                is_list = true;
            }else if(arg.startsWith("-c")){
                commit_hash = arg.substring(2).trim();
            }else if(arg.startsWith("-d")){
                type_id = arg.substring(2).trim();
            }else{
                ShowHelp.analysis();
            }
        }

        List<AnalysisEntity> analysisEntities = sqlQuery.getAnalysisEntities(commit_hash, type_id, begin_time, end_time, min_duration);

    }

    /**
     * 显示开发人员信息
     * 应使用命令：devs
     * */
    public void ShowDevelopers() throws Exception {
        List<developer> developers = sqlQuery.getDevelopers();
        for (developer dvlp : developers){
            System.out.println(dvlp.toString());
        }
    }

    /**
     * 显示所有仓库信息
     * 应使用命令：repos
     * */
    public void ShowRepos() throws Exception {
        List<Repository> repositories = sqlQuery.getRepositories();
        for (Repository repository : repositories) {
            System.out.println(repository.toString());
        }
    }

    /**
     * 指定当前仓库环境，除repos其他所有命令使用前必须指定
     * 应使用命令：use [repo_id]
     * */
    public void UseRepo(List<String> args){

    }

    /**
     * 显示所有commit信息，按照提交顺序排序
     * 应使用命令：commits
     */
    public void ShowCommits() throws Exception {
        List<Commit> commits = sqlQuery.getCommitsInfo();
        for(Commit commit : commits){
            System.out.println(commit.toString());
        }
    }

    /**
     * 显示某一缺陷的历史变化图
     * 应使用命令：display
     * 必要参数：
     * -c [case_id]
     * -i [instance_id] 指定实例，显示所属缺陷历史
     * -ui [instance_id] 向上显示缺陷历史
     * -di [instance_id] 向下显示缺陷历史
     * */
    public void DisplayCase(List<String> args) throws Exception {
        int case_id = -1;
        String inst_id = null;
        boolean is_case = false;
        boolean is_inst = false;
        boolean all_search = false;
        boolean up_search = false;
        boolean down_search = false;
        for(String arg:args){
            arg = arg.trim();
            if(arg.startsWith("-c")){
                if(is_inst){
                    ShowHelp.display();
                    return;
                }
                is_case = true;
                case_id = Integer.parseInt(arg.substring(2).trim());
            }else if(arg.startsWith("-i")){
                if(is_case || up_search || down_search){
                    ShowHelp.display();
                    return;
                }
                is_inst = true;
                all_search = true;
                inst_id = arg.substring(2).trim();
            }else if(arg.startsWith("-ui")){
                if(is_case || all_search || down_search){
                    ShowHelp.display();
                    return;
                }
                is_inst = true;
                up_search = true;
                inst_id = arg.substring(3).trim();
            }else  if(arg.startsWith("-di")){
                if(is_case || all_search || up_search){
                    ShowHelp.display();
                    return;
                }
                is_inst = true;
                down_search = true;
                inst_id = arg.substring(3).trim();
            }else{
                ShowHelp.display();
                return;
            }
        }

        if (is_inst) case_id = sqlQuery.getCase_idByInst_id(inst_id);
        if(case_id <= 0) {ShowHelp.display();return;}
        Iss_case iss_case = sqlQuery.getCaseByCase_id(case_id);
        List<Iss_match> matches = sqlQuery.getMatches(case_id);
        String original_commit = iss_case.getCommit_hash_new();
        String final_commit = iss_case.getCommit_hash_last();
//        String inst_begin = down_search ? inst_id : final_commit;
//        String inst_end = up_search ? inst_id : original_commit;
        //need I do it ?

    }

    /**
     * 显示具体信息
     * 应使用命令：show
     * 必要参数：
     * -i [instance_id]
     * -m [commit_id]
     * -c [case_id]
     * */
    public void ShowInfo(List<String> args){

    }

}
