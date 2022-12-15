package org.example.Command;

public class ShowHelp {
    public static void defect(){
        System.out.println("" +
                "     * 显示静态缺陷数量的分类统计和详细列表：\n" +
                "     * 应使用命令：defect\n" +
                "     * 默认情况：最新版本，按类型统计（显示存续时长平均值和中位数），按存续时长排序\n" +
                "     * options:\n" +
                "     * -v [commit_id](version) 指定版本\n" +
                "     * -av (all version) 所有版本\n" +
                "     * -t [time_begin(yyyy-MM-dd)--time_end(yyyy-MM-dd)](time) 指定时间段，可以只使用一部分\n" +
                "     * -d [type_id] 指定缺陷\n" +
                "     * 显示类型：\n" +
                "     * commit_id, committer, commit_time, commit_message -- DefectCommitEntity\n" +
                "     *      type_id, type_msg\n" +
                "     *                  inst_id, case_id, exist_duration\n" +
                "     *                  file_path, start_line, String end_line,start_col, end_col\n" +
                "     *                  class_, method\n" +
                "     *                  code -- DefectEntity");
    }
    public static void analysis(){
        System.out.println("" +
                "    * 数据分析统计\n" +
                "     * 应使用命令：analysis\n" +
                "     * 默认情况：所有时间内引入静态缺陷的数量、解决数量、解决率，按总量以及各个缺陷大类和具体类型统计\n" +
                "     * -u [user_name](user) 指定开发人员，根据引入缺陷、解决他人引入缺陷、自己引入且尚未解决缺陷、自己引入且被他人解决缺陷的分类统计，平均存活时间（存活周期）统计。\n" +
                "     * -t [time_begin--time_end](time) 指定时间段\n" +
                "     * -c [commit_hash](commit) 指定版本\n" +
                "     * -d [type_id](type_id) 指定缺陷类型\n" +
                "     * -f [defect-type](defect) 指定缺陷大类\n" +
                "     * -md [duration](min duration) 指定最小存续时长，按时长从大到小排序");
    }
    public static void devs(){}
    public static void repos(){}
    public static void use(){}
    public static void commits(){}
    public static void display(){}
    public static void show(){}
}
