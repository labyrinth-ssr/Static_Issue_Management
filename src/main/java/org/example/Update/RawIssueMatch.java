package org.example.Update;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import org.example.Constant;
import org.example.Entity.Iss_case;
import org.example.SonarConfig.SonarIssues;
import org.example.SonarConfig.SonarLocation;
import cn.edu.fudan.issue.core.process.RawIssueMatcher;
import cn.edu.fudan.issue.entity.dbo.Location;
import cn.edu.fudan.issue.entity.dbo.RawIssue;
import cn.edu.fudan.issue.util.AnalyzerUtil;
import cn.edu.fudan.issue.util.AstParserUtil;
import org.example.Entity.*;
import org.example.Utils.SqlMapping;

public class RawIssueMatch {
    private static final String baseRepoPath1 = Constant.RepoPath;

    public static void myMatch(SqlMapping sqlMapping, List<Matches> matches, HashMap<String,SonarRules> rulesHash, List<SonarIssues> issInstanceListCur, Commit curCommit) throws SQLException, IOException {
        List<Iss_case> caseListUpdate = new ArrayList<>();
        List<Iss_location> locationList = new ArrayList<>();
        List<Instance_location> instanceLocationList = new ArrayList<>();
        List<Iss_case> caseList = new ArrayList<>();
        List<Iss_instance> instanceList = new ArrayList<>();
        List<SonarRules> sonarRules = new ArrayList<>();
        HashMap<String, Matches> hashMap = new HashMap<>();

        List<RawIssue> preRawIssueList = new ArrayList<>();
        matches.forEach(match->{
            Match_Info matchInfo = match.getInfo();
            List<Iss_location> locations = match.getLocation();
            RawIssue preRawIssue = newRawIssue(matchInfo.getInst_id_last(),matchInfo.getType_id(),matchInfo.getMessage(),matchInfo.getCommit_id_last(), matchInfo.getFile_name());
            List<Location> tmplocationList = new ArrayList<>();
            locations.forEach(location -> tmplocationList.add(newLocation(location.getStart_line(),location.getEnd_line(),location.getStart_col(),location.getEnd_col(),location.getCode(),location.getMethod(),location.getClass_())));
            preRawIssue.setLocations(tmplocationList);
            preRawIssueList.add(preRawIssue);
            hashMap.put(matchInfo.getInst_id_last(), match);
        });

        List<RawIssue> curRawIssueList = getRawIssues(issInstanceListCur, curCommit, rulesHash, sonarRules);

        Map<String, List<RawIssue>> preRawIssueMap = preRawIssueList.stream().collect(Collectors.groupingBy(RawIssue::getFileName));
        Map<String, List<RawIssue>> curRawIssueMap = curRawIssueList.stream().collect(Collectors.groupingBy(RawIssue::getFileName));

        Set<String> keyset = curRawIssueMap.keySet();
        for(String file_path : keyset) {
            List<RawIssue> rawIssues = preRawIssueMap.get(file_path);
            if(rawIssues!=null && rawIssues.size() != 0) {
                RawIssueMatcher.match(rawIssues, curRawIssueMap.get(file_path), AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath1 + "/" + file_path));
            }
        }
        //对于没有childMap的issue，设置case disappear
        for (RawIssue curRawIssue : curRawIssueList) {
            Iss_case iss_case;
            List<Iss_location> locations = new ArrayList<>();
            curRawIssue.getLocations().forEach(location -> {
                Iss_location issLocation = newIssLocation(location);
                locations.add(issLocation);
            });
            if (curRawIssue.getMappedRawIssue() == null){
                iss_case = new Iss_case(curRawIssue.getType(),curCommit.getCommit_id(),curCommit.getCommit_id(),null,"NEW");
                caseList.add(iss_case);
                Matches matches_ = newMatches(iss_case, curRawIssue, locations);
                System.out.println("============================+++"+curRawIssue.getStatus());
//                hashMap.put(iss_case.getCase_id(), matches_);
                matches.add(matches_);
            }
            else{
                String case_status = hashMap.get(curRawIssue.getMappedRawIssue().getUuid()).getInfo().getCase_status();
                System.out.println("================================"+curRawIssue.getStatus());
                if(case_status.equals("SOLVED")) case_status = "REOPEN";
                if(case_status.equals("NEW")) case_status = "UNDONE";
                iss_case = new Iss_case(hashMap.get(curRawIssue.getMappedRawIssue().getUuid()).getInfo().getCase_id(), curRawIssue.getType(), curCommit.getCommit_id(), curCommit.getCommit_id(), hashMap.get(curRawIssue.getMappedRawIssue().getUuid()).getInfo().commit_id_disappear, case_status);
                caseListUpdate.add(iss_case);
                Matches matches_ = hashMap.get(curRawIssue.getMappedRawIssue().getUuid());
                setMatches(matches_, iss_case, curRawIssue, locations);
            }

            locationList.addAll(locations);
            locations.forEach(issLocation -> instanceLocationList.add(new Instance_location(curRawIssue.getUuid(),issLocation.getLocation_id())));
            instanceList.add(new Iss_instance(curRawIssue.getUuid(),curRawIssue.getType(), curCommit.getCommit_id(),curRawIssue.getMappedRawIssue() == null ? null:curRawIssue.getMappedRawIssue().getUuid(), iss_case.getCase_id(), curRawIssue.getFileName()));
        }

        for (RawIssue preRawIssue:preRawIssueList) {
            System.out.println("preRawIssue"+hashMap.get(preRawIssue.getUuid()).getInfo().case_status);
            if (preRawIssue.getMappedRawIssue() == null && !hashMap.get(preRawIssue.getUuid()).getInfo().case_status.equals("SOLVED")){
                Iss_case iss_case = new Iss_case(hashMap.get(preRawIssue.getUuid()).getInfo().getCase_id(),preRawIssue.getType(), null, hashMap.get(preRawIssue.getUuid()).getInfo().commit_id_last, curCommit.getCommit_id(), "SOLVED");
                caseListUpdate.add(iss_case);
                Matches matches_ = hashMap.get(preRawIssue.getUuid());
                setMatchesPre(matches_, iss_case, preRawIssue);
                System.out.println("----------------------------"+preRawIssue.getStatus());
            }else{
                System.out.println("--------------------------__"+preRawIssue.getStatus());
            }
        }
        Repos repos = new Repos(curCommit);

        sqlMapping.execute("start transaction");
        try {
//            System.out.println(Collections.singletonList(curCommit));
//            System.out.println(sonarRules.toString());
//            System.out.println("caseListUpdate"+caseListUpdate.toString());
//            System.out.println(caseList.toString());
//            System.out.println(instanceList.toString());
//            System.out.println(locationList.toString());
//            System.out.println(instanceLocationList.toString());
            sqlMapping.updateRepos(repos);
            sqlMapping.save(Collections.singletonList(curCommit));
            sqlMapping.save(sonarRules);
            sqlMapping.updateCase(caseListUpdate);
            sqlMapping.save(caseList);
            sqlMapping.save(instanceList);
            sqlMapping.save(locationList);
            sqlMapping.save(instanceLocationList);
            sqlMapping.execute("commit");
        }catch (Exception e){
            sqlMapping.execute("rollback");
            e.printStackTrace();
        }
    }

    public static List<RawIssue> getRawIssues(List<SonarIssues> sonarIssues, Commit curCommit, HashMap<String, SonarRules> sonarHash, List<SonarRules> sonarRules){
        List<RawIssue> curRawIssueList = new ArrayList<>();
        for (SonarIssues instance : sonarIssues) {
            String type_id = instance.getTypeId();
            if(sonarHash.get(type_id) == null){
                SonarRules sonarRule = new SonarRules(type_id, instance.getMessage(), instance.getSeverity(), "java", instance.getType());
                sonarHash.put(type_id, sonarRule);
                sonarRules.add(sonarRule);
            }
//            System.out.println(sonarIssues.toString());
            RawIssue curRawIssue = newRawIssue(instance.getId(),instance.getTypeId(),instance.getMessage(),curCommit.getCommit_id(), instance.getFilePath().split(":")[1]);
            List<Location> tmplocationList = new ArrayList<>();
            for (SonarLocation location : instance.getLocation()) {
                System.out.println(location.toString());
                tmplocationList.add(newLocation(Integer.parseInt(location.getStartLine()),Integer.parseInt(location.getEndLine()),Integer.parseInt(location.getStartOffset()),Integer.parseInt(location.getEndOffset())));
            }
            curRawIssue.setLocations(tmplocationList);
            curRawIssueList.add(curRawIssue);
        }
        AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, baseRepoPath1);
//        curRawIssueList.forEach(rawIssue -> System.out.println(rawIssue.getLocations().toString()));
        return curRawIssueList;
    }

    public static Location newLocation(Integer startLine, Integer endLine, Integer startToken, Integer endToken, String code,String method, String class_){
        Location location = new Location();
        location.setStartLine(startLine);
        location.setEndLine(endLine);
        location.setStartToken(startToken);
        location.setEndToken(endToken);
        location.setCode(code);
        location.setClassName(class_);
        location.setAnchorName(method);
        return location;
    }

    public static Location newLocation(Integer startLine, Integer endLine, Integer startToken, Integer endToken){
        Location location = new Location();
        location.setStartLine(startLine);
        location.setEndLine(endLine);
        location.setStartToken(startToken);
        location.setEndToken(endToken);
        return location;
    }


    public static Iss_location newIssLocation(Location l){
        return new Iss_location(l.getClassName(), l.getAnchorName(), l.getStartLine(), l.getEndLine(), l.getStartToken(), l.getEndToken(), l.getCode(), l.getFilePath());
    }

    public static void setMatch(Iss_match iss_match, String parent_inst_id, String case_id){
        iss_match.setParent_inst_id(parent_inst_id);
        iss_match.setCase_id(case_id);
    }

    public static void setCase(Iss_case iss_case, String status, String commit_id_last, String commit_id_disappear){
        iss_case.setCase_status(status);
        iss_case.setCommit_id_last(commit_id_last);
        iss_case.setCommit_id_disappear(commit_id_disappear);
    }

    public static RawIssue newRawIssue(String uuid, String type, String detail, String commit_id, String fileName){
        RawIssue rawIssue = new RawIssue();
        rawIssue.setUuid(uuid);
        rawIssue.setType(type);
        rawIssue.setDetail(detail);
        rawIssue.setCommitId(commit_id);
        rawIssue.setFileName(fileName);
        return  rawIssue;
    }

    public static Matches newMatches(Iss_case iss_case, RawIssue rawIssue, List<Iss_location> locations){
        Match_Info matchInfo = new Match_Info(iss_case.getCase_id(), iss_case.getCase_status(), iss_case.getCommit_id_last(),iss_case.getCommit_id_disappear(),rawIssue.getUuid(),rawIssue.getType(),rawIssue.getDetail(),rawIssue.getFileName());
        return new Matches(matchInfo, locations);
    }
    public static void setMatches(Matches match, Iss_case iss_case, RawIssue rawIssue, List<Iss_location> locations){
        Match_Info matchInfo = new Match_Info(iss_case.getCase_id(), iss_case.getCase_status(), iss_case.getCommit_id_last(),iss_case.getCommit_id_disappear(),rawIssue.getUuid(),rawIssue.getType(),rawIssue.getDetail(),rawIssue.getFileName());
        match.setInfo(matchInfo);
        match.setLocation(locations);
    }

    public static void setMatchesPre(Matches match, Iss_case iss_case, RawIssue rawIssue){
        Match_Info matchInfo = new Match_Info(iss_case.getCase_id(), iss_case.getCase_status(), iss_case.getCommit_id_last(),iss_case.getCommit_id_disappear(),rawIssue.getUuid(),rawIssue.getType(),rawIssue.getDetail(),rawIssue.getFileName());
        match.setInfo(matchInfo);
    }
}
