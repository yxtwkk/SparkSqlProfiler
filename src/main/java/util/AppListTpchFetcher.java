package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

/**
 * Created by lh on 2017/10/19.
 */
public class AppListTpchFetcher {

    public static List<String> fetchLocalFile(String siteURL, String appIdStart, String appIdEnd) {
        List<String> appList = new ArrayList<String>();
        List<String> lines = JsonFileReader.readFileLines(siteURL);

        for (String line: lines) {
            if (line.trim().contains("http://47.92.71.43:18080/history")&&line.trim().contains("/jobs")) {
                //  <a href="http://alyuntpch:18080/history/app-20171013113611-0013/1/jobs/">app-20171013113611-0013</a>
                Pattern p = Pattern.compile("<a.*?>(.+?)</a>");
                Matcher m = p.matcher(line);
                while (m.find()) {
                    // System.out.println("appId:"+m.group(1)); app-20171013113611-0013
                    appList.add(m.group(1));
                }
            }
        }
        System.out.println("recordWhole:"+appList.size());// whole data of the local html
        //appIdStart = "app-20171012232943-0005";  appIdEnd = "app-20171013113611-0013";
        for (int a = 0; a < appList.size(); a++) {
            int res = appList.get(a).compareTo(appIdStart);
            int resEnd = appList.get(a).compareTo(appIdEnd);
            if (res < 0 || resEnd > 0) { //selected time should be removed
                appList.remove(a);
                a--;
            }
        }
        System.out.println("recordPart:"+appList.size()); //given the range of appIDs
        for(int s=0;s<appList.size();s++){
            System.out.println(appList.get(s));
        }
        return appList;
    }

    public static void main(String[] args) {
        String url = "";
        url = "file:///Users/yxtwkk/Desktop/History%20Server.htm";
        fetchLocalFile(url, "app-20171003161609-0500", "app-20171001095827-0145\t");
    }
}
