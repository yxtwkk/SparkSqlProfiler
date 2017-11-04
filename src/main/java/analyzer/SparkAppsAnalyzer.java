package analyzer;

import appinfo.*;
//import appinfo.Executor;
import statistics.ApplicationStatistics;
import util.FileTextWriter;
import util.JsonFileReader;

import java.io.File;
import java.util.*;


public class SparkAppsAnalyzer {

    // Key: AppName, Value: the same app that runs multiple times
    // Key = RDDJoin-CMS-1-7G-0.5, Value = [app-20170630121954-0025, app-20170630122434-0026, ...]
    private Map<String, List<Application>> appNameToIdsMap = new HashMap<String, List<Application>>();
    private Map<String, ApplicationStatistics> appStatisticsMap = new HashMap<String, ApplicationStatistics>();
    private Map<String, Application> selectedAppNameToIdsMap = new HashMap<String, Application>();

    // profiledApps = [apps with different names in the appList]
    public SparkAppsAnalyzer(List<Application> profiledApps) {
        for (Application app : profiledApps) {
            String appName = app.getName();

            if (!appNameToIdsMap.containsKey(appName)) {
                List<Application> appList = new ArrayList<Application>();
                appList.add(app);
                appNameToIdsMap.put(appName, appList);
            } else {
                List<Application> appList = appNameToIdsMap.get(appName);
                appList.add(app);
            }
        }
    }

    public void analyzeAppStatistics(Integer[] stageIdsToMerge) {
        for (Map.Entry<String, List<Application>> app : appNameToIdsMap.entrySet()) {
            ApplicationStatistics appStatistics = new ApplicationStatistics(app.getValue(), stageIdsToMerge);
            appStatisticsMap.put(app.getKey(), appStatistics);
        }
    }

    public void outputStatistics(String statisticsDir) {
        for (Map.Entry<String, ApplicationStatistics> appEntry : appStatisticsMap.entrySet()) {
            String appName = appEntry.getKey();
            ApplicationStatistics appStatistics = appEntry.getValue();
            appStatistics.setAppName(appName);

            String appStatisticsFile = statisticsDir + File.separatorChar + appName + "-stat.txt";
            FileTextWriter.write(appStatisticsFile, appStatistics.toString());

            System.out.println("[Done] The statistics of " + appName + " has been computed!");
        }
    }

    public void outputAppInfo(String appDir, String dirName){

        Map<String,Long> AppStatistics = new HashMap<String,Long>();
        Map<String,List<Long>> AllApp = new HashMap<String, List<Long>>();
        Map<Application,Stage> StageStatistics = new HashMap<Application, Stage>();
        long max = 0;
        StringBuilder appInfo = new StringBuilder();
        String test = null;

        Application appTmp = null;
        for (Map.Entry<String, List<Application>> appEntry : appNameToIdsMap.entrySet()){
            String appName = appEntry.getKey();
            String outputAppInfoFile = appDir + File.separatorChar + dirName + File.separatorChar + appName + "-Apps.txt";

            StringBuilder sb = new StringBuilder();
            sb.append("[appName = " + appName + "]\n");
            //Long[] DurationTmp = null;
            Map<Long,Application> DurationTmp = new HashMap<Long, Application>();
            List<Long> AllDuration = new ArrayList<Long>();
            for(Application app : appEntry.getValue()) {
                if (app.getStatus().equals("SUCCEEDED")) {
                    //System.out.println("111\n");
                    //System.out.println("!!!!"+app.getAppId());
                    sb.append("[appId = " + app.getAppId() + "]\n");
                    sb.append("[" + app.getAppId() + ".app.duration] " + app.getDuration() + "\n");
                   // DurationTmp.add
                    AllDuration.add(app.getDuration());
                    DurationTmp.put(app.getDuration(),app);
                 /*   if(app.getDuration()>max)
                    {
                        appTmp=app;
                        max=app.getDuration();
                        test = app.getAppId();
                    }*/
                }

            }
            Object[] Duration = DurationTmp.keySet().toArray();
            Arrays.sort(Duration);
            Object tmp = Duration[Duration.length/2];
            Application app = DurationTmp.get(tmp);
            System.out.println(app.getName() +":"+ app.getDuration());

            selectedAppNameToIdsMap.put(appName,app);
            AllApp.put(appName,AllDuration);
            FileTextWriter.write(outputAppInfoFile, sb.toString());
            AppStatistics.put(appName,app.getDuration());
            //appInfo.append("[appId="+appId+"]\n");
            //appInfo.append(appName+" "+max+"\n");
            max=0;
            //System.out.println(test);

        }
        Object[] key_arr = AppStatistics.keySet().toArray();
        Arrays.sort(key_arr);
        for  (Object key : key_arr) {
            Object value = AppStatistics.get(key);
            appInfo.append(key+" "+value+"\n");
        }

        String output = appDir + File.separatorChar + dirName + File.separatorChar  + "Apps.txt";
        FileTextWriter.write(output, appInfo.toString());

        key_arr = AllApp.keySet().toArray();
        Arrays.sort(key_arr);
        StringBuilder allAppInfo = new StringBuilder();
        for  (Object key : key_arr) {
            int i=0;
            List<Long> value = AllApp.get(key);
            allAppInfo.append(key+"\n");
            for(Long tmp :value){
                allAppInfo.append(i+" "+tmp+"\n");
                i++;
            }

        }
        output = appDir + File.separatorChar + dirName + File.separatorChar  + "AllApps.txt";
        FileTextWriter.write(output, allAppInfo.toString());




    }


    public void outputJobInfo(String appDir, String dirName){

        for (Map.Entry<String, List<Application>> appEntry : appNameToIdsMap.entrySet()){
            String appName = appEntry.getKey();
            String outputJobInfoFile = appDir + File.separatorChar + dirName + File.separatorChar + appName + "-Jobs.txt";

            StringBuilder sb = new StringBuilder();
            sb.append("[appName = " + appName + "]\n");
            for(Application app : appEntry.getValue()) {
                if(app.getStatus().equals("SUCCEEDED")){
                    sb.append("[appId = " + app.getAppId() + "]\n");
                    for(Map.Entry<Integer, Job> jobEntry : app.getJobMap().entrySet()){
                        Integer jobId = jobEntry.getKey();
                        Job job = jobEntry.getValue();
                        sb.append("[" + jobId + ".job.duration] " + job.getDurationMS()+ "\n");
                    }

                }

                FileTextWriter.write(outputJobInfoFile, sb.toString());
            }

        }
    }

    public void outputStageInfo(String appDir, String dirName){

        for(Map.Entry<String, List<Application>> appEntry : appNameToIdsMap.entrySet()){

            String appName = appEntry.getKey();
            String outputStageInfoFile = appDir + File.separatorChar + dirName + File.separatorChar + appName + "-Stage.txt";

            StringBuilder sb = new StringBuilder();
            sb.append("[appName = " + appName +"]\n");
            for(Application app : appEntry.getValue()){
                if(app.getStatus().equals("SUCCEEDED")){
                    sb.append("[appId = " + app.getAppId() + "]\n");
                    for(Map.Entry<Integer , Stage> stageEntry : app.getStageMap().entrySet()){

                        Integer stageId = stageEntry.getKey();
                        Stage stage = stageEntry.getValue();
                        sb.append("[stageId = " + stage.getStageId() + "]\n");
                        for(Map.Entry<Integer, StageAttempt> stageAttemptEntry : stage.getStageAttemptMap().entrySet()){
                            StageAttempt stageAttempt = stageAttemptEntry.getValue();
                            Integer stageAttemptId = stageAttemptEntry.getKey();
                            sb.append("[" + stageAttemptId + ".stage.duration] " + stageAttempt.getDuration()+ "\n");
                        }

                    }
                }
                FileTextWriter.write(outputStageInfoFile, sb.toString());
            }

        }
    }

    public void outputSelectedAppStageInfo(String appDir, String dirName){

        StringBuilder sb = new StringBuilder();
        Map<String,Long> stageDuration = new HashMap<String, Long>();
        Map<String,String> tasksInfo = new HashMap<String, String>();
        for(Map.Entry<String,Application> appEntry : selectedAppNameToIdsMap.entrySet()){

            String appName = appEntry.getKey();
            //sb.append("[appName = " + appName + "]");
            Application app = appEntry.getValue();

            long max=0;
            Integer tmpId=0;
            Integer tmpStageId=0;

            for (Map.Entry<Integer, Stage> stageEntry : app.getStageMap().entrySet()) {

                Integer stageId = stageEntry.getKey();
                Stage stage = stageEntry.getValue();

                for (Map.Entry<Integer, StageAttempt> stageAttemptEntry : stage.getStageAttemptMap().entrySet()) {

                    StageAttempt stageAttempt = stageAttemptEntry.getValue();
                    if(stageAttempt.getDuration()>max){
                        max = stageAttempt.getDuration();
                        Integer stageAttemptId = stageAttemptEntry.getKey();
                        tmpId = stageAttemptId;
                        tmpStageId = stageId;
                    }
                }
            }
            stageDuration.put(appName,max);
            //sb.append("[appId = " + app.getAppId() + "]\n");
            //sb.append("[stageId = " + tmpStageId + "]\n");
            //sb.append("[ "+ tmpStageId + tmpId + ".stage.duration] " + max + "\n");

            Set<Integer> stageIds = new HashSet<Integer>();
            stageIds.add(tmpStageId);
            String taskInfo = app.getTaskDuration(tmpStageId);
            //sb.append(taskInfo);
            tasksInfo.put(appName,taskInfo);
        }
        Object[] key_arr = tasksInfo.keySet().toArray();
        Arrays.sort(key_arr);
        for  (Object key : key_arr) {
            Object value = tasksInfo.get(key);
            sb.append(key+"\n");
            sb.append(value);
        }

        String outputFile = appDir + File.separatorChar + dirName + File.separatorChar + "Stage.txt";
        FileTextWriter.write(outputFile, sb.toString());

        sb = new StringBuilder();
        key_arr = stageDuration.keySet().toArray();
        Arrays.sort(key_arr);
        for  (Object key : key_arr) {
            Object value = stageDuration.get(key);
            sb.append(key+" ");
            sb.append(value+"\n");
        }



        outputFile = appDir + File.separatorChar + dirName + File.separatorChar + "StageDuration.txt";
        FileTextWriter.write(outputFile, sb.toString());
    }

    public void outputTaskInStage(String appDir, String dirName, int[] selectedStageIds) {
        Set<Integer> stageIds = new HashSet<Integer>();
        for (int id : selectedStageIds)
            stageIds.add(id);

        for (Map.Entry<String, List<Application>> appEntry : appNameToIdsMap.entrySet()) {
            // appName = RDDJoin-CMS-1-7G-0.5
            String appName = appEntry.getKey();
            String outputTaskInfoFile = appDir + File.separatorChar + dirName + File.separatorChar + appName + "-tasks.txt";

            StringBuilder sb = new StringBuilder();
            sb.append("[appName = " + appName + "]\n");

            for (Application app : appEntry.getValue()) {
                if (app.getStatus().equals("SUCCEEDED")) {
                    sb.append("[appId = " + app.getAppId() + "]\n");
                    String taskInfo = app.getTaskInfosInStage(stageIds);
                    sb.append(taskInfo);
                }
            }

            FileTextWriter.write(outputTaskInfoFile, sb.toString());
        }
    }


    public void outputSlowestTask(String appDir, String dirName, int[] selectedStageIds) {

        Set<Integer> stageIds = new HashSet<Integer>();
        for (int id : selectedStageIds)
            stageIds.add(id);

        for (Map.Entry<String, ApplicationStatistics> appEntry : appStatisticsMap.entrySet()) {
            String appName = appEntry.getKey();
            ApplicationStatistics appStatistics = appEntry.getValue();
            appStatistics.setAppName(appName);

            String appStatisticsFile = appDir + File.separatorChar + dirName + File.separatorChar + appName + "-slowestTask.txt";
            TaskAttempt slowestTask = appStatistics.getSlowestTask(stageIds);

            if (slowestTask != null) {

                String appId = slowestTask.getAppId();
                int executorId = slowestTask.getExecutorId();

                Application appWithSlowestTask = null;

                for (Application app : appNameToIdsMap.get(appName)) {
                    if (app.getAppId().equalsIgnoreCase(appId))
                        appWithSlowestTask = app;
                }
               // Executor executor = appWithSlowestTask.getExecutor(executorId + "");

               /* String stderr = appDir + File.separatorChar + appName + "_" + appId + File.separatorChar +
                        "executors" + File.separatorChar + executorId + File.separatorChar + "stderr";
                String excutorLog = JsonFileReader.readFile(stderr);*/

                StringBuilder sb = new StringBuilder();
                sb.append("======================= Slowest Task in " + appName + "_" + appId + " stage" + stageIds + " =======================\n");

               /* sb.append(slowestTask.jsonString() + "\n\n");
                sb.append("======================= Slowest Task Executor GC Metrics " + " =======================\n");
               // sb.append(executor + "\n\n");
                sb.append("======================= Slowest Task Execution Log " + " =======================\n");
              //  sb.append(excutorLog);
*/


                FileTextWriter.write(appStatisticsFile, sb.toString());
            }

            System.out.println("[Done] The slowestTask of " + appName + " has been computed!");
        }
    }

}
