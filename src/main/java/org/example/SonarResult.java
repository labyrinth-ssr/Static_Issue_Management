package org.example;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.example.Utils.HTTPUtil;

import java.util.List;

public class SonarResult {

    public static List<Issues> getSonarIssues() {
        String result = HTTPUtil.sendGet("http://127.0.0.1:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
        JSONObject sonarData = JSONObject.parseObject(result);
        JSONArray issueRawData = sonarData.getJSONArray("issues");
        return JSON.parseArray(String.valueOf(issueRawData), Issues.class);
    }

    public static List<Rules> getSonartype(){
        String result1 = HTTPUtil.sendGet("http://127.0.0.1:9000/api/qualityprofiles/search?language=java");
        JSONObject sonarConfig = JSONObject.parseObject(result1);
        JSONObject profile = (JSONObject) sonarConfig.getJSONArray("profiles").get(0);
        String key = profile.getString("key");
        System.out.println(key);

        String result2 = HTTPUtil.sendGet("http://127.0.0.1:9000/api/rules/search?qprofile=" + key + "&activation=true");
        JSONArray sonarRules = JSONObject.parseObject(result2).getJSONArray("rules");
        return JSON.parseArray(String.valueOf(sonarRules), Rules.class);
    }
}
