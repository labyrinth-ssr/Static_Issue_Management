package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import SonarConfig.SonarIssues;
import SonarConfig.SonarLocation;
import cn.edu.fudan.issue.core.process.RawIssueMatcher;
import cn.edu.fudan.issue.entity.dbo.Location;
import cn.edu.fudan.issue.entity.dbo.RawIssue;
import cn.edu.fudan.issue.util.AnalyzerUtil;
import cn.edu.fudan.issue.util.AstParserUtil;
import org.example.Entity.Commit;
import org.example.Entity.Iss_case;
import org.example.Entity.Iss_instance;
import org.example.Entity.Iss_match;

public class RawIssueMatch {

    private static final String baseRepoPath = System.getProperty("user.dir");
    private static final String SEPARATOR = System.getProperty("file.separator");

    public static void match(List<Iss_case>iss_caseList, List<Iss_match> iss_matchList, List<SonarIssues> issInstanceListPre, List<SonarIssues> issInstanceListCur, Commit preCommit,Commit curCommit,)throws IOException{
        List<RawIssue> preRawIssueList = new ArrayList<>();

        for (SonarIssues instance:issInstanceListPre) {
            RawIssue preRawIssue1 = new RawIssue();
            preRawIssue1.setUuid(String.valueOf(instance.getId()));
            preRawIssue1.setType(instance.getType());
            preRawIssue1.setFileName(instance.getFilePath());
            preRawIssue1.setDetail(instance.getMessage());
            preRawIssue1.setCommitId(preCommit.getCommit_hash());
                List<Location> locationList = new ArrayList<>();
                for (SonarLocation location:instance.getLocation()) {
                    Location preLocation1 = new Location();
                    preLocation1.setStartLine(Integer.parseInt(location.getStartLine()));
                    preLocation1.setEndLine(Integer.parseInt(location.getEndLine()));
                    preLocation1.setStartToken(Integer.parseInt(location.getStartOffset()));
                    preLocation1.setEndToken(Integer.parseInt(location.getEndOffset()));
                    preLocation1.setCode("");
                    locationList.add(preLocation1);
                }
                if (locationList.isEmpty()){
                    Location preLocation1 = new Location();
                    preLocation1.setStartLine(0);
                    preLocation1.setEndLine(0);
                    preLocation1.setStartToken(0);
                    preLocation1.setCode("");
                    preRawIssue1.setLocations(Collections.singletonList(preLocation1));
                    System.out.println("empty");
                }
                else {
                    preRawIssue1.setLocations(locationList);
                    System.out.println("not empty");
                }
                preRawIssueList.add(preRawIssue1);
        }
        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath);

        List<RawIssue> curRawIssueList = new ArrayList<>();

        for (SonarIssues instance:issInstanceListCur) {
            RawIssue curRawIssue1 = new RawIssue();
            curRawIssue1.setUuid(String.valueOf(instance.getId()));
            curRawIssue1.setType(instance.getType());
            curRawIssue1.setFileName(instance.getFilePath());
            curRawIssue1.setDetail(instance.getMessage());
            curRawIssue1.setCommitId(curCommit.getCommit_hash());
                List<Location> locationList = new ArrayList<>();
                for (SonarLocation location : instance.getLocation()) {
                    Location curLocation1 = new Location();
                    curLocation1.setStartLine(Integer.parseInt(location.getStartLine()));
                    curLocation1.setEndLine(Integer.parseInt(location.getEndLine()));
                    curLocation1.setStartToken(Integer.parseInt(location.getStartOffset()));
                    curLocation1.setEndToken(Integer.parseInt(location.getEndOffset()));
                    curLocation1.setCode("");

                    locationList.add(curLocation1);
                }
                if (locationList.isEmpty()){
                    Location curLocation1 = new Location();
                    curLocation1.setStartLine(0);
                    curLocation1.setEndLine(0);
                    curLocation1.setStartToken(0);
                    curLocation1.setCode("");

                    curRawIssue1.setLocations(Collections.singletonList(curLocation1));
                }
                else {
                    curRawIssue1.setLocations(locationList);
                }
                curRawIssueList.add(curRawIssue1);
        }
        AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, baseRepoPath);
        RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));

        for (RawIssue curRawIssue:curRawIssueList) {
            System.out.println("curRawIssue1:matches " + curRawIssue.getMappedRawIssue().getUuid());
            Iss_match iss_match = new Iss_match();
            iss_match.setInst_id(curRawIssue.getUuid());
            iss_match.setParent_inst_id(curRawIssue.getMappedRawIssue().getUuid());
            iss_match.setCommit_hash(curRawIssue.getCommitId());
            iss_match.setParent_commit_hash(curRawIssue.getMappedRawIssue().getCommitId());
            iss_match.setStatus("new");
            iss_matchList.add(iss_match);

            Iss_case iss_case=new Iss_case();
            iss_case.setType_id(curRawIssue.getType());
            iss_case.setCommit_hash_new(curRawIssue.getCommitId());
            iss_case.setCommit_hash_last(curRawIssue.getMappedRawIssue().getCommitId());
            iss_case.setCommiter_new(curCommit.getCommitter());
            iss_case.setCommit_hash_last(preCommit.getCommit_hash());
        }

    }

    public static void main(String[] args) throws IOException {

        String type = "Math operands should be cast before assignment";

        /**
         * RawIssue 需要字段 type,fileName,detail,Locations,commitId
         * Location 需要字段 startLine,endLine,startToken
         */
        //1. 初始化，写入需要的字段值
        List<RawIssue> preRawIssueList = new ArrayList<>();
        RawIssue preRawIssue1 = new RawIssue();
        preRawIssue1.setUuid("preRawIssue1");
        preRawIssue1.setType(type);
        preRawIssue1.setFileName("src/main/resources/testFile/commit1/test.java");
        preRawIssue1.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        preRawIssue1.setCommitId("commit1");
        Location preLocation1 = new Location();
        preLocation1.setStartLine(10);
        preLocation1.setEndLine(10);
        preLocation1.setStartToken(0);
        preRawIssue1.setLocations(Collections.singletonList(preLocation1));

        RawIssue preRawIssue2 = new RawIssue();
        preRawIssue2.setUuid("preRawIssue2");
        preRawIssue2.setType(type);
        preRawIssue2.setFileName("src/main/resources/testFile/commit1/test.java");
        preRawIssue2.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        preRawIssue2.setCommitId("commit1");
        Location preLocation2 = new Location();
        preLocation2.setStartLine(11);
        preLocation2.setEndLine(11);
        preLocation2.setStartToken(0);
        preRawIssue2.setLocations(Collections.singletonList(preLocation2));

        preRawIssueList.add(preRawIssue1);
        preRawIssueList.add(preRawIssue2);

        //2. 获取缺陷所在方法名 逻辑代码 偏移量
        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath);

        List<RawIssue> curRawIssueList = new ArrayList<>();
        RawIssue curRawIssue1 = new RawIssue();
        curRawIssue1.setUuid("curRawIssue1");
        curRawIssue1.setType(type);
        curRawIssue1.setFileName("src/main/resources/testFile/commit2/test.java");
        curRawIssue1.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        curRawIssue1.setCommitId("commit2");
        Location curLocation1 = new Location();
        curLocation1.setStartLine(10);
        curLocation1.setEndLine(10);
        curLocation1.setStartToken(0);
        curRawIssue1.setLocations(Collections.singletonList(curLocation1));

        RawIssue curRawIssue2 = new RawIssue();
        curRawIssue2.setUuid("curRawIssue2");
        curRawIssue2.setType(type);
        curRawIssue2.setFileName("src/main/resources/testFile/commit2/test.java");
        curRawIssue2.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        curRawIssue2.setCommitId("commit2");
        Location curLocation2 = new Location();
        curLocation2.setStartLine(11);
        curLocation2.setEndLine(11);
        curLocation2.setStartToken(0);
        curRawIssue2.setLocations(Collections.singletonList(curLocation2));

        curRawIssueList.add(curRawIssue1);
        curRawIssueList.add(curRawIssue2);

        AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, baseRepoPath);

        //3. 进行映射
        // 前一个版本的缺陷 后一个版本的缺陷 当前版本的文件中所有方法及变量名
        RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));

        System.out.println("preRawIssue1:matches " + preRawIssue1.getMappedRawIssue().getUuid());
        System.out.println("preRawIssue2:matches " + preRawIssue2.getMappedRawIssue().getUuid());
        System.out.println("curRawIssue1:matches " + curRawIssue1.getMappedRawIssue().getUuid());
        System.out.println("curRawIssue2:matches " + curRawIssue2.getMappedRawIssue().getUuid());

    }

}
