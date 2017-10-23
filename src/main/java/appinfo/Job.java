package appinfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import util.DateParser;

import java.util.ArrayList;
import java.util.List;


public class Job {

    private int jobId;
    private String name;
    private String submissionTime;
    private String completionTime;
    private List<Integer> stageIds = new ArrayList<Integer>();

    // 1. SUCCEEDED (without any failed stages/tasks)
    // 2. FINISHED (with failed stages/tasks but completed)
    // 3. FAILED (does not finished with failed stages/tasks)

    private String status;
    private int numTasks;
    private int numActiveTasks;
    private int numCompletedTasks;
    private int numSkippedTasks;
    private int numFailedTasks;
    private int numActiveStages;
    private int numCompletedStages;
    private int numSkippedStages;
    private int numFailedStages;

    private long durationMS;


    public Job(JsonObject jobObject) {
        parse(jobObject);
    }

    /*
    jobObject:
    {
            "jobId" : 16,
            "name" : "aggregate at AreaUnderCurve.scala:45",
            "submissionTime" : "2017-05-30T16:25:43.699GMT",
            "completionTime" : "2017-05-30T16:25:44.455GMT",
            "stageIds" : [ 33, 31, 32 ],
            "status" : "SUCCEEDED",
            "numTasks" : 160,
            "numActiveTasks" : 0,
            "numCompletedTasks" : 33,
            "numSkippedTasks" : 127,
            "numFailedTasks" : 0,
            "numActiveStages" : 0,
            "numCompletedStages" : 1,
            "numSkippedStages" : 2,
            "numFailedStages" : 0
    }
    */

    private void parse(JsonObject jobObject) {
        jobId = jobObject.get("jobId").getAsInt();
        name = jobObject.get("name").getAsString();
        submissionTime = jobObject.get("submissionTime").getAsString();
        if (jobObject.get("completionTime") != null)
            completionTime = jobObject.get("completionTime").getAsString();
        status = jobObject.get("status").getAsString();
        numTasks = jobObject.get("numTasks").getAsInt();
        numActiveTasks = jobObject.get("numActiveTasks").getAsInt();
        numCompletedTasks = jobObject.get("numCompletedTasks").getAsInt();
        numSkippedTasks = jobObject.get("numSkippedTasks").getAsInt();
        numFailedTasks = jobObject.get("numFailedTasks").getAsInt();
        numActiveStages = jobObject.get("numActiveStages").getAsInt();
        numCompletedStages = jobObject.get("numCompletedStages").getAsInt();
        numSkippedStages = jobObject.get("numSkippedStages").getAsInt();
        numFailedStages = jobObject.get("numFailedStages").getAsInt();

        if (jobObject.get("completionTime") != null)
            durationMS = DateParser.durationMS(submissionTime, completionTime);

        JsonArray stageIdsArray = jobObject.get("stageIds").getAsJsonArray();

        for (JsonElement stageIdElem : stageIdsArray) {
            stageIds.add(stageIdElem.getAsInt());
        }

        if (status.equals("SUCCEEDED") && numFailedTasks > 0)
            status = "FINISHED";
    }

    public int getJobId() {
        return jobId;
    }

    public List<Integer> getStageIds() {
        return stageIds;
    }

    public String getStatus() {
        return status;
    }

    public Long getDurationMS() { return durationMS; }
}
