package data.lab.knowledgegraph.utils;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.types.Type;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.utils
 * @Description: TODO(neo4j数据操作工具)
 * @date 2020/4/29 23:15
 */
public class Neo4jDataUtils {
    // 获取D3所需的数据格式方法
    // node:   getNodeObj(Node n)
    //return : {
    //		    "id": "5",
    //		    "labels": ["人","律师"],
    //		    "properties": {
    //		        "name": "覃"
    //		        "value": "123456"
    //		    }
    //		}
    // relationship ： getRelationShipsObj(Relationship r)
    //return : {
    //		    "id": "1",
    //		    "type": "参与",
    //		    "startNode": "1",
    //		    "endNode": "2",
    //		    "properties": {
    //		    	"name": "参与"
    //		    	"level": "5"
    //		    }
    //		}

    /**
     * @param search
     * @return
     * @Description: TODO(直接获取查询最终结果对象 ： 无参数)--driver
     */
    public static JSONObject getNeo4jResult(StatementResult search) {
        // 遍历结果
        JSONObject results = new JSONObject(); //最终结果
        JSONArray neoResults = new JSONArray();  //data array
        JSONObject result = new JSONObject(); //结果
        JSONArray data = new JSONArray();  //data array
        JSONObject graph = new JSONObject(); //graph object
        JSONObject datas = new JSONObject(); //datas object
        JSONArray nodes = new JSONArray();
        JSONArray relationShips = new JSONArray();

        while (search.hasNext()) {
            Record next = search.next();
            List<Value> list = next.values();
            JSONObject resultList = new JSONObject();
            for (Value v : list) {
                // NODE_TyCon PATH_TyCon
                Type type = v.type();
                if ("NODE".equals(type.name())) {
                    // 返回节点
                    resultList = Neo4jDataUtils.getNodeData(v);
                    if (!nodes.contains(resultList)) {
                        nodes.add(resultList);
                    }
                } else if ("PATH".equals(type.name())) {
                    // 返回路径
                    resultList = Neo4jDataUtils.getPathData(v);
                    for (Object value : resultList.getJSONArray("nodes")) {
                        if (!nodes.contains(value)) {
                            nodes.add(value);
                        }
                    }
                    for (Object value : resultList.getJSONArray("relationships")) {
                        if (!relationShips.contains(value)) {
                            relationShips.add(value);
                        }
                    }
                }
            }
        }
        datas.put("nodes", nodes);
        datas.put("relationships", relationShips);
        graph.put("graph", datas);
        data.add(graph);
        result.put("columns", "");
        result.put("data", data);
        neoResults.add(result);
        results.put("results", neoResults);
        return results;
    }

    /**
     * @param search
     * @return
     * @Description: TODO(Superman_Zzz直接获取查询最终结果对象 ： 无参数)--jdbc
     */
    public static JSONObject getNeo4jResult(ResultSet search) {
        // 遍历结果
        JSONObject results = new JSONObject(); //最终结果
        JSONArray neoResults = new JSONArray();  //data array
        JSONObject result = new JSONObject(); //结果
        JSONArray data = new JSONArray();  //data array
        JSONObject graph = new JSONObject(); //graph object
        JSONObject datas = new JSONObject(); //datas object

        JSONArray nodes = new JSONArray();
        JSONArray relationShips = new JSONArray();
        try {

            int columnNum;
            List<String> stringList = new ArrayList<String>();
            int stringListSize;
            String str;
            Object jsonData = null;

            while (search.next()) {
                columnNum = search.getMetaData().getColumnCount();
                for (int i = 1; i <= columnNum; i++) {  // 一列返回多个关系/多个节点(p1,p2,p3,p4,p5...n1,n2,n3,n4,n5...)
                    stringList.add(search.getString(i));
                }
                stringListSize = stringList.size();
                for (int i = 0; i < stringListSize; i++) {
                    str = stringList.get(i);
                    if (str != null) {
                        //结果转换成array
                        try {
                            jsonData = JSON.parse(str);
                        } catch (Exception e) {
                            System.out.println("返回数据时错误的str：" + str);
                        }
                        //遍历结果
                        if (jsonData != null) {
                            JSONObject resultList = new JSONObject();
                            //判断结果为点还是线
                            // 返回路径
                            if (jsonData instanceof JSONArray) { //如果是 jsonArray 则为线
                                resultList = Neo4jDataUtils.getPathData(jsonData);
                                for (Object value : resultList.getJSONArray("nodes")) {
                                    if (!nodes.contains(value)) {
                                        nodes.add(value);
                                    }
                                }
                                for (Object value : resultList.getJSONArray("relationships")) {
                                    if (!relationShips.contains(value)) {
                                        relationShips.add(value);
                                    }
                                }
                            } else if (jsonData instanceof JSONObject) { //否则为点
                                // 返回节点
                                resultList = Neo4jDataUtils.getNodeData(jsonData);
                                if (!nodes.contains(resultList)) {
                                    nodes.add(resultList);
                                }
                            }
                        }
                    }
                }
                stringList.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        datas.put("nodes", nodes);
        datas.put("relationships", relationShips);
        graph.put("graph", datas);
        data.add(graph);
        result.put("columns", "");
        result.put("data", data);
        neoResults.add(result);
        results.put("results", neoResults);
        return results;
    }

//    /**
//     * @param search
//     * @return
//     * @Description: TODO(Superman_Zzz直接获取查询最终结果对象 ： 无参数)--jdbc
//     */
//    public static JSONObject getNeo4jResultCount(ResultSet search) {
//        List<String> list = LabelList.getLabelList();
//        // 遍历结果
//        JSONObject results = new JSONObject(); //最终结果
//        try {
//            int columnNum;
//            List<String> stringList = new ArrayList<String>();
//            int index = 0;
//            while (search.next()) {
//                columnNum = search.getMetaData().getColumnCount();
//                for (int i = 1; i <= columnNum; i++) {  // 一列返回多个关系/多个节点(p1,p2,p3,p4,p5...n1,n2,n3,n4,n5...)
//                    stringList.add(search.getString(i));
//                }
//                if (!stringList.get(0).equals("0")) {
//                    results.put(list.get(index), stringList.get(0));
//                }
//                stringList.clear();
//                index++;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return results;
//    }

    /**
     * @return
     * @Description: TODO(获取JSONObject格式的node数据)
     */
    public static JSONObject getNodeData(Value value) {
        // 转为node
        Node n = value.asNode();
        JSONObject NodeData = Neo4jDataUtils.getNodeObj(n);
        return NodeData;
    }

    /**
     * @return
     * @Description: TODO(获取JsonObject格式的Object)
     */
    public static JSONObject getNodeData(Object value) {
        // 转为node
        JSONObject NodeData = Neo4jDataUtils.getNodeObj(value);
        return NodeData;
    }

    /**
     * @return
     * @Description: TODO(获取JSONObject格式的node + relationship数据)
     */
    public static JSONObject getPathData(Value value) {
        // 转换路径
        Path p = value.asPath();
        // 结果obj
        JSONObject result = new JSONObject();
        // 节点arr
        JSONArray nodesArr = Neo4jDataUtils.getNodeArr(p.nodes());
        // 关系arr
        JSONArray relationshipsArr = Neo4jDataUtils.getRelationShipsArr(p.relationships());
        result.put("nodes", nodesArr);
        result.put("relationships", relationshipsArr);
        return result;
    }

    /**
     * @return
     * @Description: TODO(获取JSONObject格式的node + relationship数据)
     */
    public static JSONObject getPathData(Object value) {
        // 结果obj
        JSONObject result = new JSONObject();
        JSONArray nodesArr = new JSONArray();
        JSONArray relationshipsArr = new JSONArray();
        for (Object o : JSONArray.parseArray(value.toString())) { //转换为jsonarray 同时遍历结果
            //判断结果 是点 还是 线
            if (JSONObject.parseObject(o.toString()).containsKey("type")) { //如果属性 有type 则为线
                // 关系arr
                JSONObject relationship = Neo4jDataUtils.getRelationShipsObj(JSONObject.parseObject(o.toString()));
                relationshipsArr.add(relationship);
            } else { //反之 则为点
                // 节点arr
                JSONObject node = Neo4jDataUtils.getNodeData(JSONObject.parseObject(o.toString()));
                nodesArr.add(node);
            }
        }
        result.put("relationships", relationshipsArr);
        result.put("nodes", nodesArr);
        return result;
    }

    /**
     * @param n
     * @return
     * @Description: TODO(获取节点对象)
     */
    public static JSONObject getNodeObj(Node n) {
        // 创建node对象
        JSONObject NodeData = new JSONObject();
        // id
        NodeData.put("id", n.id());
        // labels
        Iterable<String> labels = n.labels();
        JSONArray NodeLabels = new JSONArray();
        for (String label : labels) {
            NodeLabels.add(label);
        }
        NodeData.put("labels", NodeLabels);
        // properties
        JSONObject NodeProperties = new JSONObject();
        Map<String, Object> mapNodePro = n.asMap();
        for (Entry<String, Object> entry : mapNodePro.entrySet()) {
            NodeProperties.put(entry.getKey().toString(), entry.getValue());
        }
        NodeData.put("properties", NodeProperties);
        return NodeData;
    }

    /**
     * @param n 格式 参数
     * @return
     * @Description: TODO(获取节点对象 :)
     */
    public static JSONObject getNodeObj(Object n) {
        JSONObject ja = JSONObject.parseObject(n.toString());
        // 创建node对象
        JSONObject NodeData = new JSONObject();
        // id
        NodeData.put("id", ja.get("id"));
        // labels
        JSONArray labels = ja.getJSONArray("labels");
        JSONArray NodeLabels = new JSONArray();
        for (Object label : labels) {
            NodeLabels.add(label.toString());
        }
        NodeData.put("labels", NodeLabels);
        // properties
        JSONObject NodeProperties = new JSONObject();
        Map<String, Object> mapNodePro = ja.getInnerMap();
        for (Entry<String, Object> entry : mapNodePro.entrySet()) {
            if (entry.getKey().toString() != "id" && entry.getKey().toString() != "labels") {
                NodeProperties.put(entry.getKey().toString(), entry.getValue());
            }
        }
        NodeData.put("properties", NodeProperties);
        return NodeData;
    }

    /**
     * @param relationShips
     * @return
     * @Description: TODO(获取关系数组)
     */
    public static JSONArray getRelationShipsArr(Iterable<Relationship> relationShips) {
        JSONArray RelationShipsArr = new JSONArray();
        for (Relationship relationship : relationShips) {
            JSONObject relationShipsObj = Neo4jDataUtils.getRelationShipsObj(relationship);
            RelationShipsArr.add(relationShipsObj);
        }
        return RelationShipsArr;
    }

    /**
     * @param r
     * @return
     * @Description: TODO(获取关系对象)
     */
    public static JSONObject getRelationShipsObj(Relationship r) {
        // 关系对象
        JSONObject relationShipObj = new JSONObject();
        // 关系id
        relationShipObj.put("id", r.id());
        // 关系类型名称
        relationShipObj.put("type", r.type());
        // 关系开始节点
        relationShipObj.put("startNode", r.startNodeId());
        // 关系结束节点
        relationShipObj.put("endNode", r.endNodeId());
        // 关系属性
        // properties
        JSONObject RelationShipProperties = new JSONObject();
        Map<String, Object> mapRelationShipPro = r.asMap();
        for (Entry<String, Object> entry : mapRelationShipPro.entrySet()) {
            RelationShipProperties.put(entry.getKey().toString(), entry.getValue());
        }
        relationShipObj.put("properties", RelationShipProperties);
        return relationShipObj;
    }

    /**
     * @param r
     * @return
     * @Description: TODO(获取关系对象)
     */
    public static JSONObject getRelationShipsObj(Object r) {
        JSONObject jo = JSONObject.parseObject(r.toString());
        // 关系对象
        JSONObject relationShipObj = new JSONObject();
        // 关系id
        relationShipObj.put("id", jo.get("id"));
        // 关系类型名称
        relationShipObj.put("type", jo.get("type"));
        // 关系开始节点
        relationShipObj.put("startNode", jo.get("startId"));
        // 关系结束节点
        relationShipObj.put("endNode", jo.get("endId"));
        // 关系属性
        // properties
        JSONObject RelationShipProperties = new JSONObject();
        Map<String, Object> mapRelationShipPro = jo.getInnerMap();
        for (Entry<String, Object> entry : mapRelationShipPro.entrySet()) {
            if (entry.getKey().toString() != "id" && entry.getKey().toString() != "type" && entry.getKey().toString() != "startId" && entry.getKey().toString() != "endId") {
                RelationShipProperties.put(entry.getKey().toString(), entry.getValue());
            }
        }
        relationShipObj.put("properties", RelationShipProperties);
        return relationShipObj;
    }

    /**
     * @param nodes
     * @return
     * @Description: TODO(获取Nodes节点array)
     */
    public static JSONArray getNodeArr(Iterable<Node> nodes) {
        JSONArray NodesArr = new JSONArray();
        // 遍历节点
        for (Node n : nodes) {
            JSONObject NodeObj = getNodeObj(n);
            NodesArr.add(NodeObj);
        }
        return NodesArr;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Get call result)
     */
    public static JSONArray getCallResult(ResultSet result) {
        JSONArray data = new JSONArray();  //data array
        try {
            while (result.next()) {
                String resultString = result.getString(1);
                if (resultString != null) {
                    data.add(resultString);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject getCurrentNodes(ResultSet result) {
        JSONObject data = new JSONObject();  //data object
        int total = 0;
        try {
            while (result.next()) {
                Array array = result.getArray(1);
                Object object = array.getArray();
                Object[] arrayLabels = (Object[]) object;
                String label = (String) arrayLabels[arrayLabels.length - 1];  // 尽量设置为最底层节点标签
                int count = result.getInt(2);
                total += count;
                if (data.containsKey(label)) {
                    data.put(label, data.getIntValue(label) + count);
                } else {
                    data.put(label, count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        data.put("total", total);
        return data;
    }

    /**
     * @param
     * @return
     * @Description: TODO(标签拼接)
     */
    public static List<String> jointLabe(ResultSet result) {
        List<String> data = new ArrayList<>();  //data object
        try {
            while (result.next()) {
                Array array = result.getArray(1);
                Object object = array.getArray();

                Object[] arrayLabels = (Object[]) object;
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < arrayLabels.length; i++) {
                    InternalNode internalNode = (InternalNode) arrayLabels[i];
                    Map<String, Object> map = internalNode.asMap();
                    String label = (String) map.get("labelName");

                    builder.append(label + "_");

                }
                data.add(builder.substring(0, builder.length() - 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * @param
     * @return
     * @Description: TODO(关系列表)
     */
    public static JSONObject getCurrentNodeRelationships(ResultSet result) {
        JSONObject data = new JSONObject();  //data object
        int total = 0;
        try {
            while (result.next()) {
                String relationshipName = result.getString(1);
                int count = result.getInt(2);
                total += count;
                data.put(relationshipName, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        data.put("total", total);
        return data;
    }

    /**
     * @param
     * @return
     * @Description: TODO(交集校友碰撞)
     */
    public static JSONObject getIntersectionAlumniCount(ResultSet result, String relationshipType, String featureWord) {
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        int total = 0;
        try {
            while (result.next()) {
                ArrayList labels = (ArrayList) result.getObject(1);
                String label = fiterOrgGraphModelLabel(labels);

                String master = splitString(result.getString(3), 0);
                String slave = splitString(result.getString(4), 0);

                String r1StartTime = splitString(result.getString(5), 0);
                String r1StopTime = splitString(result.getString(6), 0);
                String r2StartTime = splitString(result.getString(7), 0);
                String r2StopTime = splitString(result.getString(8), 0);

                String school = result.getString(9);

                array.add(master + "和" + slave + "存在" + relationshipType + "关系，" + master + "在" + r1StartTime + "到" + r1StopTime + "期间在" + school + "" + featureWord + "，" +
                        "" + slave + "在" + r2StartTime + "到" + r2StopTime + "期间在" + school + "" + featureWord + "\n");

                int count = result.getInt(2);
                total += count;
                data.put(label, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        data.put("detail", array);
        data.put("total", total);
        return data;
    }

    /**
     * @param
     * @return
     * @Description: TODO(分割字符串通过索引位获取值)
     */
    private static String splitString(String string, int i) {
        if (string != null) {
            String[] array = string.split("_");
            if (array.length >= i) {
                return array[i];
            }
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(组织图模型标签)
     */
    private static String fiterOrgGraphModelLabel(ArrayList labels) {
        // 认证机构,学校,公司,颁发机构,志愿机构,其它组织
        String[] orgGraphModelLabels = {"认证机构", "学校", "公司", "颁发机构", "志愿机构", "其它组织"};
        for (int i = 0; i < orgGraphModelLabels.length; i++) {
            String orgGraphModelLabel = orgGraphModelLabels[i];
            if (labels.contains(orgGraphModelLabel)) {
                return orgGraphModelLabel;
            }
        }
        // 只包含组织的时候返回其它组织
        if (labels.size() == 1) {
            if ("组织".equals(labels.get(0))) {
                return "其它组织";
            }
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(组织维度分解标签列表)
     */
    public static JSONObject getCurrentNodeLabelsRelationships(ResultSet result) {
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        int total = 0;
        try {
            while (result.next()) {
                ArrayList labels = (ArrayList) result.getObject(1);
                String label = fiterOrgGraphModelLabel(labels);

                String master = result.getString(3).split("_")[0];
                String slave = result.getString(4).split("_")[0];

                array.add(master + "和" + slave + "存在" + label + "关系\n");

                int count = result.getInt(2);
                total += count;
                data.put(label, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        data.put("detail", array);
        data.put("total", total);
        return data;
    }

    /**
     * @param
     * @return
     * @Description: TODO(关系列表)
     */
    public static JSONObject getNodeRelationships(ResultSet result) {
        JSONObject data = new JSONObject();  //data object
        JSONArray array = new JSONArray();
        int total = 0;
        try {
            while (result.next()) {
                String relationshipName = result.getString(1);
                int count = result.getInt(2);
                total += count;
                data.put(relationshipName, count);

                String master = result.getString(3).split("_")[0];
                String slave = result.getString(4).split("_")[0];

                array.add(master + "和" + slave + "存在" + relationshipName + "关系\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        data.put("detail", array);
        data.put("total", total);
        return data;
    }

    /**
     * @param
     * @return
     * @Description: TODO(同级校友碰撞)
     */
    public static JSONObject getSameLevelAlumniCount(ResultSet result, String relationshipType, String featureWord) {
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        int total = 0;
        try {
            while (result.next()) {
                ArrayList labels = (ArrayList) result.getObject(1);
                String label = fiterOrgGraphModelLabel(labels);

                String master = splitString(result.getString(3), 0);
                String slave = splitString(result.getString(4), 0);
                String startTime = splitString(result.getString(5), 0);
                String school = result.getString(6);

                array.add(master + "和" + slave + "存在" + relationshipType + "关系，他们在" + startTime + "年共同进入" + school + "" + featureWord + "\n");

                int count = result.getInt(2);
                total += count;
                data.put(label, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        data.put("detail", array);
        data.put("total", total);
        return data;
    }

    /**
     * @param
     * @return
     * @Description: TODO(通过最顶层父标签加载标签树)
     */
    public static JSONArray getLabelSpanningTree(ResultSet result) throws SQLException {

        // 存放N个最顶层标签JSONObject
        JSONArray treeArray = new JSONArray();

        // Relationship String
        String pString;

        // Relationship JsonObject
        JSONObject pJsonObject;

        // 存放2层暂存节点以及一个更顶级索引节点
        JSONArray pauseSave2HierarchyNode = new JSONArray();

        // 存放3层暂存节点以及一个更顶级索引节点
        JSONArray pauseSave3HierarchyNode = new JSONArray();

        JSONArray relationshipArray;
        JSONArray nodeArray;
        long startNode = 0;
        JSONObject node;
        JSONObject boolMap;
        boolean bool;
        List<Long> markFatherHierarchyNodeList = new ArrayList<Long>();
        List<Long> markFather2HierarchyNodeList = new ArrayList<Long>();
        List<Long> markFather3HierarchyNodeList = new ArrayList<Long>();


        while (result.next()) {
            pString = result.getString(1);
            pJsonObject = getPathData(JSONArray.parseArray(pString));

            relationshipArray = pJsonObject.getJSONArray("relationships");
            if (!relationshipArray.isEmpty()) {
                nodeArray = pJsonObject.getJSONArray("nodes");

                if (nodeArray.size() == 2) {
                    startNode = relationshipArray.getJSONObject(0).getLong("startNode");
                } else if (nodeArray.size() == 3) {
                    startNode = relationshipArray.getJSONObject(1).getLong("startNode");
                }
                for (int i = 0; i < nodeArray.size(); i++) {
                    node = nodeArray.getJSONObject(i);
                    if (node.getJSONObject("properties").getIntValue("hierarchy") == 1) {
                        if (!markFatherHierarchyNodeList.contains(node.getLong("id"))) {
                            treeArray.add(node);
                            markFatherHierarchyNodeList.add(node.getLong("id"));
                        }
                    } else if (node.getJSONObject("properties").getIntValue("hierarchy") == 2) {

                        // 将2层节点放入1层节点的Child
                        boolMap = putHierarchy(treeArray, startNode, node, 2, markFather2HierarchyNodeList);
                        bool = boolMap.getBoolean("bool");

                        // 如果返回放入失败则将二层节点暂存起来
                        if (!bool) {
                            JSONObject nodeMap = nodeMap(node, startNode);
                            pauseSave2HierarchyNode.add(nodeMap);
                        }
                    } else if (node.getJSONObject("properties").getIntValue("hierarchy") == 3) {

                        // 将3层节点放入2层节点的Child
                        boolMap = putHierarchy(treeArray, startNode, node, 3, markFather3HierarchyNodeList);
                        bool = boolMap.getBoolean("bool");

                        // 如果返回放入失败则将三层节点暂存起来
                        if (!bool) {
                            JSONObject nodeMap = nodeMap(node, startNode);
                            pauseSave3HierarchyNode.add(nodeMap);
                        }
                    }
                }

            }
        }
        // 处理暂存的2层和3层节点
        markFather2HierarchyNodeList.clear();
        putPauseHierarchyNode(treeArray, pauseSave2HierarchyNode, 2, markFather2HierarchyNodeList);
        markFather3HierarchyNodeList.clear();
        putPauseHierarchyNode(treeArray, pauseSave3HierarchyNode, 3, markFather3HierarchyNodeList);
        return treeArray;
    }

    /**
     * @param
     * @return
     * @Description: TODO(将N层节点放入N - 1层节点的Child)
     */
    private static JSONObject putHierarchy(JSONArray treeArray, long startNode, JSONObject node, int hierarchy, List<Long> markList) {
        JSONObject returnObject = new JSONObject();
        if (hierarchy == 2) {
            for (int i = 0; i < treeArray.size(); i++) {
                JSONObject object = treeArray.getJSONObject(i);
                if (object.getLong("id") == startNode) {
                    putChildNodeDefineHierarchy(object, node, markList);
                }
            }
            returnObject.put("array", treeArray);
            returnObject.put("bool", true);
        } else if (hierarchy == 3) {
            for (int i = 0; i < treeArray.size(); i++) {
                JSONObject object = treeArray.getJSONObject(i);
                JSONArray childArray = object.getJSONArray("child");
                if (childArray != null) {
                    for (int j = 0; j < childArray.size(); j++) {
                        JSONObject nodeObject = childArray.getJSONObject(j);
                        if (nodeObject.getLong("id") == startNode) {
                            putChildNodeDefineHierarchy(nodeObject, node, markList);
                        }
                    }
                }
            }
            returnObject.put("array", treeArray);
            returnObject.put("bool", true);
        }
        return returnObject;
    }

    /**
     * @param
     * @return
     * @Description: TODO(构造孩子ARRAY并将已存在的孩子节点放入CHILD ARRAY)
     */
    private static void putChildNodeDefineHierarchy(JSONObject object, JSONObject node, List<Long> markList) {
        JSONArray childArray = object.getJSONArray("child");
        if (childArray == null) {
            JSONArray newChildArray = new JSONArray();
            newChildArray.add(node);
            object.put("child", newChildArray);
            markList.add(node.getLong("id"));
        } else {
            if (!markList.contains(node.getLong("id"))) {
                childArray.add(node);
                markList.add(node.getLong("id"));
            }
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(构造暂存NODE对象)
     */
    private static JSONObject nodeMap(JSONObject node, long startNode) {
        JSONObject nodeMap = new JSONObject();
        nodeMap.put("node", node);
        nodeMap.put("start", startNode);
        return nodeMap;
    }

    /**
     * @param
     * @param pauseSaveHierarchyNode
     * @return
     * @Description: TODO(处理暂存的2层和3层节点)
     */
    private static void putPauseHierarchyNode(JSONArray treeArray, JSONArray pauseSaveHierarchyNode, int hierarchy, List<Long> markList) {
        for (int i = 0; i < pauseSaveHierarchyNode.size(); i++) {
            JSONObject object = pauseSaveHierarchyNode.getJSONObject(i);
            putHierarchy(treeArray, object.getLong("start"), object.getJSONObject("node"), hierarchy, markList);
        }
    }
}
