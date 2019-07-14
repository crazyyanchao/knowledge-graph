package casia.isiteam.knowledgegraph.model;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class UserJson {

    private String name;
    private String entityName;
    private String nameRid;
    private JSONArray sysTemUserId;
    private JSONArray insert_time;
    private JSONArray ids;  // 数组中id参数无引号
    private JSONArray nameArray;    // 多实体名数组
    private String askString;   // 问句 XXX住哪里？
    private JSONObject timeZone;    // 时区 包含开始结束时间

    private int realType; // 0 ： 虚 1：实
    private int behaviorType; // 0 ： 动作 1：状态
    private int actionType; // 0 ： 正常 1：异常 （动作类才有）
    private String latelyTime; // 最近时间
    private String earliestTime; // 最早时间

    private long clusterTimeSizeMill;   // 聚集的时间粒度

    private JSONObject keyValue;    // 属性与属性值
    private String labels;  // 多个实体标签使用逗号分割

    private String entityOne;   // 实体一
    private String entityOneLabels;
    private String entityTwo;   // 实体二
    private String entityTwoLabels;
    private String relationship;    // 实体间关系

    private JSONArray imeiArray;
    private JSONArray phoneArray;
    private JSONArray bsidArray;

    private int relationshipNum;
    private int crossAnalysisModelType;

    private long vid;
    private long gid;
    private long rid;
    private long sysUserId;

    private String startTime;
    private String stopTime;

    private JSONArray labelsArray;

    private JSONObject nameObject;  // （模糊实体名，标签）

    private String siteName;

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setLabelsArray(JSONArray labelsArray) {
        this.labelsArray = labelsArray;
    }

    public JSONArray getLabelsArray() {
        return labelsArray;
    }

    public void setCrossAnalysisModelType(int crossAnalysisModelType) {
        this.crossAnalysisModelType = crossAnalysisModelType;
    }

    public int getCrossAnalysisModelType() {
        return crossAnalysisModelType;
    }

    public void setNameObject(JSONObject nameObject) {
        this.nameObject = nameObject;
    }

    public JSONObject getNameObject() {
        return nameObject;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public void setSysUserId(long sysUserId) {
        this.sysUserId = sysUserId;
    }

    public long getGid() {
        return gid;
    }

    public long getRid() {
        return rid;
    }

    public long getSysUserId() {
        return sysUserId;
    }

    public int getRelationshipNum() {
        return relationshipNum;
    }

    public void setRelationshipNum(int relationshipNum) {
        this.relationshipNum = relationshipNum;
    }

    public void setImeiArray(JSONArray imeiArray) {
        this.imeiArray = imeiArray;
    }

    public void setPhoneArray(JSONArray phoneArray) {
        this.phoneArray = phoneArray;
    }

    public void setBsidArray(JSONArray bsidArray) {
        this.bsidArray = bsidArray;
    }

    public JSONArray getImeiArray() {
        return imeiArray;
    }

    public JSONArray getPhoneArray() {
        return phoneArray;
    }

    public JSONArray getBsidArray() {
        return bsidArray;
    }

    public void setEntityTwoLabels(String entityTwoLabels) {
        this.entityTwoLabels = entityTwoLabels;
    }

    public void setEntityOneLabels(String entityOneLabels) {

        this.entityOneLabels = entityOneLabels;
    }

    public String getEntityOneLabels() {
        return entityOneLabels;
    }

    public String getEntityTwoLabels() {
        return entityTwoLabels;
    }

    public void setEntityOne(String entityOne) {
        this.entityOne = entityOne;
    }

    public void setEntityTwo(String entityTwo) {
        this.entityTwo = entityTwo;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getEntityOne() {
        return entityOne;
    }

    public String getEntityTwo() {
        return entityTwo;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getLabels() {

        return labels;
    }

    public JSONObject getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(JSONObject keyValue) {
        this.keyValue = keyValue;
    }

    public void setClusterTimeSizeMill(long clusterTimeSizeMill) {
        this.clusterTimeSizeMill = clusterTimeSizeMill;
    }

    public long getClusterTimeSizeMill() {

        return clusterTimeSizeMill;
    }

    public void setTimeZone(JSONObject timeZone) {
        this.timeZone = timeZone;
    }

    public JSONObject getTimeZone() {

        return timeZone;
    }

    public void setAskString(String askString) {
        this.askString = askString;
    }

    public String getAskString() {

        return askString;
    }

    public void setNameArray(JSONArray nameArray) {
        this.nameArray = nameArray;
    }

    public JSONArray getNameArray() {

        return nameArray;
    }

    public void setIds(JSONArray ids) {
        this.ids = ids;
    }

    public JSONArray getIds() {

        return ids;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setInsert_time(JSONArray insert_time) {
        this.insert_time = insert_time;
    }

    public JSONArray getInsert_time() {

        return insert_time;
    }

    public void setSysTemUserId(JSONArray sysTemUserId) {
        this.sysTemUserId = sysTemUserId;
    }

    public void setNameRid(String nameRid) {

        this.nameRid = nameRid;
    }

    public JSONArray getSysTemUserId() {
        return sysTemUserId;
    }

    public String getNameRid() {

        return nameRid;
    }

    public int getRealType() {
        return realType;
    }

    public int getBehaviorType() {
        return behaviorType;
    }

    public int getActionType() {
        return actionType;
    }

    public String getLatelyTime() {
        return latelyTime;
    }

    public String getEarliestTime() {
        return earliestTime;
    }

    public void setRealType(int realType) {
        this.realType = realType;
    }

    public void setBehaviorType(int behaviorType) {
        this.behaviorType = behaviorType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public void setLatelyTime(String latelyTime) {
        this.latelyTime = latelyTime;
    }

    public void setEarliestTime(String earliestTime) {
        this.earliestTime = earliestTime;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}

