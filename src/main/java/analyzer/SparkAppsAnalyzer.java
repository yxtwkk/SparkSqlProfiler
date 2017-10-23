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

        for (Map.Entry<String, List<Application>> appEntry : appNameToIdsMap.entrySet()){
            String appName = appEntry.getKey();
            String outputAppInfoFile = appDir + File.separatorChar + dirName + File.separatorChar + appName + "-Apps.txt";

            StringBuilder sb = new StringBuilder();
            sb.append("[appName = " + appName + "]\n");
            for(Application app : appEntry.getValue()) {
                if (app.getStatus().equals("SUCCEEDED")) {
                    //System.out.println("111\n");
                    System.out.println(app.getStatus());
                    sb.append("[appId = " + app.getAppId() + "]\n");
                    sb.append("[" + app.getAppId() + ".app.duration] " + app.getDuration() + "\n");
                }
            }
            FileTextWriter.write(outputAppInfoFile, sb.toString());
        }

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
