package org.example;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.example.Utils.HTTPUtil;

import java.io.FileFilter;

public class SonarResult {

    public static void getSonarResut(){
        String result = HTTPUtil.sendGet("http://127.0.0.1:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
        JSONObject sonarData = JSONObject.fromObject(result);
        JSONArray issueRawData = sonarData.getJSONArray("issues");
        for (int j = 0; j < issueRawData.size(); j++) {
            JSONObject sonarIssue = issueRawData.getJSONObject(j);
            //解析location
            System.out.println(sonarIssue);
        }

        String result1 = HTTPUtil.sendGet("http://127.0.0.1:9000/api/qualityprofiles/search?language=java");
        JSONObject sonarConfig = JSONObject.fromObject(result1);
        JSONObject profile = (JSONObject) sonarConfig.getJSONArray("profiles").get(0);
        String key = profile.getString("key");
        System.out.println(key);

        String result2 = HTTPUtil.sendGet("http://127.0.0.1:9000/api/rules/search?qprofile=" + key + "&activation=true");
        JSONArray sonarRules = JSONObject.fromObject(result2).getJSONArray("rules");
        for(int i = 0; i < sonarRules.size(); i++){
            JSONObject jsonObject = (JSONObject) sonarRules.get(i);
            System.out.println(jsonObject);
        }
    }
}
