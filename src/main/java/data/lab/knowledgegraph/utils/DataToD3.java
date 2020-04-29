package data.lab.knowledgegraph.utils;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.knowledgegraph.repository.CypherNeo4jOperation;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isiteam.knowledgegraph.utils
 * @Description: TODO
 * @date 2020/4/29 22:21
 */
public class DataToD3 {

    private final static Logger logger = LoggerFactory.getLogger(CypherNeo4jOperation.class);
    private Driver driver;
    private Session session;

    public DataToD3(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    /**
     * @param cypher
     * @param webJsonPath
     * @return
     * @Description: TODO(界面传回操作请求 ， 拼成Match语句查库 ， 查库结果拼成 ( D3.js需要的格式)json格式写json文件)
     */
    public void jointJsonFile(String cypher, String webJsonPath) {
        session = driver.session();

        StatementResult result = session.run(cypher); // Auto-commit transaction are a quick and easy way to wrap a read
        StringBuffer nodes = new StringBuffer();
        StringBuffer links = new StringBuffer();
        List<Long> nodeList = new ArrayList<Long>(); // remove repetition
        List<String> linkList = new ArrayList<String>(); // remove repetition
        String linkStr;

        nodes.append("\"nodes\":[");
        links.append("\"links\":[");

        while (result.hasNext()) {
            Record record = result.next();
            List<Value> list = record.values();
            for (Value v : list) {
                Path p = v.asPath();
                for (Node n : p.nodes()) {
//                    System.out.println(n.labels());

                    if (!nodeList.contains(n.id())) {
                        nodes.append("{");
//                    System.out.println(n.size());
                        int num = 0;
                        for (String k : n.keys()) {
//                        System.out.println(k+"-"+n.get(k));
                            nodes.append("\"" + k + "\":" + n.get(k) + ",");
                            num++;
                            if (num == n.size()) {
                                nodes.append("\"id\":" + n.id());
                                nodeList.add(n.id());
                            }
                        }
                        nodes.append("},");
                    }
                }
                nodes = new StringBuffer(nodes.toString().substring(0, nodes.toString().length() - 1));
//                System.out.println(p);

                for (Relationship r : p.relationships()) {
//                    System.out.println(n.labels());
                    linkStr = r.startNodeId() + r.endNodeId() + r.type();
                    if (!linkList.contains(linkStr)) {
                        links.append("{");
                        System.out.println(r);
                        links.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
                        links.append(",\"type\":\"" + r.type() + "\"");
                        links.append(",\"id\":\"" + r.id() + "\"");
                        links.append("},");
                        linkList.add(linkStr);
                    }
                }
                links = new StringBuffer(links.toString().substring(0, links.toString().length() - 1));
            }

            nodes.append(",");
            links.append(",");
        }
        nodes = new StringBuffer(nodes.toString().substring(0, nodes.toString().length() - 1));
        links = new StringBuffer(links.toString().substring(0, links.toString().length() - 1));

        nodes.append("]");
        links.append("]");
        System.out.println(nodes.toString());
        System.out.println(links.toString());

        String resultJson;
        if (!String.valueOf(links.length()).equals("9")) {
            resultJson = "{" + nodes + "," + links + "}";

            // 将links中的source和target改为对应的索引位的值
            JSONObject object = new JSONObject();
            JSONObject jsonObject;
            JSONObject jsonObject1;
            JSONArray jsonArray;

            // 记录nodes索引位
            jsonObject = JSONObject.parseObject(resultJson);
            jsonArray = jsonObject.getJSONArray("nodes");
            Map<Long, Long> mapNodes = new HashMap<Long, Long>();

            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject1 = jsonArray.getJSONObject(i);
                jsonObject1.put("index", Long.valueOf(i));
                mapNodes.put(jsonObject1.getLong("id"), Long.valueOf(i));
            }
            object.put("nodes", jsonArray);

            // 替换source和target值
            JSONArray jsonArrayEdges = new JSONArray();
            jsonArray = jsonObject.getJSONArray("links");
            long long1;
            long long2;
            String str;
            long lng2;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject3 = new JSONObject();
                jsonObject1 = jsonArray.getJSONObject(i);
                long1 = jsonObject1.getInteger("source");
                long2 = jsonObject1.getInteger("target");
                str = jsonObject1.getString("type");
                lng2 = jsonObject1.getLong("id");
                if (mapNodes.containsKey(long1)) {
                    jsonObject3.put("source", mapNodes.get(long1));
                    jsonObject3.put("sourceId", long1);
                }
                if (mapNodes.containsKey(long2)) {
                    jsonObject3.put("target", mapNodes.get(long2));
                    jsonObject3.put("targetId", long2);
                }
                jsonObject3.put("type", str);
                jsonObject3.put("id", lng2);
                jsonArrayEdges.add(jsonObject3);
            }
            object.put("links", jsonArrayEdges);
            resultJson = object.toString();
        } else {
            resultJson = "{" + nodes + "}";
        }

        System.out.println("Result JSON:");
        System.out.println(resultJson);
//        FileOperate operate = new FileOperate();
//        operate.saveFile(webJsonPath, resultJson, false); // Cover write
    }

    /**
     * @param cypher
     * @return
     * @Description: TODO(界面传回操作请求 ， 拼成Match语句查库 ， 查库结果拼成 ( D3.js需要的格式)json格式)
     */
//    public Object jointJson(String cypher) {
//        session = driver.session();
//        StatementResult result = session.run(cypher); // Auto-commit transaction are a quick and easy way to wrap a read
//        StringBuffer nodes = new StringBuffer();
//        StringBuffer links = new StringBuffer();
//        List<Long> nodeList = new ArrayList<Long>(); // remove repetition
//        List<String> linkList = new ArrayList<String>(); // remove repetition
//        String linkStr;
//
//        nodes.append("\"nodes\":[");
//        links.append("\"links\":[");
//
//        while (result.hasNext()) {
//            Record record = result.next();
//            List<Value> list = record.values();
//            for (Value v : list) {
//                Path p = v.asPath();
//                for (Node n : p.nodes()) {
////                    System.out.println(n.labels());
//
//                    if (!nodeList.contains(n.id())) {
//                        nodes.append("{");
////                    System.out.println(n.size());
//                        int num = 0;
//                        for (String k : n.keys()) {
////                        System.out.println(k+"-"+n.get(k));
//                            nodes.append("\"" + k + "\":" + n.get(k) + ",");
//                            num++;
//                            if (num == n.size()) {
//                                nodes.append("\"id\":" + n.id());
//                                nodeList.add(n.id());
//                            }
//                        }
//                        nodes.append("},");
//                    }
//                }
//                nodes = new StringBuffer(nodes.toString().substring(0, nodes.toString().length() - 1));
////                System.out.println(p);
//
//                for (Relationship r : p.relationships()) {
////                    System.out.println(n.labels());
//                    linkStr = r.startNodeId() + r.endNodeId() + r.type();
//                    if (!linkList.contains(linkStr)) {
//                        links.append("{");
//                        links.append("\"source\":" + r.startNodeId() + "," + "\"target\":" + r.endNodeId());
//                        links.append(",\"type\":\"" + r.type() + "\"");
//                        links.append(",\"id\":\"" + r.id() + "\"");
//                        links.append("},");
//                        linkList.add(linkStr);
//                    }
//                }
//                links = new StringBuffer(links.toString().substring(0, links.toString().length() - 1));
//            }
//
//            nodes.append(",");
//            links.append(",");
//        }
//        nodes = new StringBuffer(nodes.toString().substring(0, nodes.toString().length() - 1));
//        links = new StringBuffer(links.toString().substring(0, links.toString().length() - 1));
//
//        nodes.append("]");
//        links.append("]");
//
//        String resultJson;
//        if (!String.valueOf(links.length()).equals("9")) {
//            resultJson = "{" + nodes + "," + links + "}";
//
//            // 将links中的source和target改为对应的索引位的值
//            JSONObject object = new JSONObject();
//            JSONObject jsonObject;
//            JSONObject jsonObject1;
//            JSONArray jsonArray;
//
//            // 记录nodes索引位
//            jsonObject = JSONObject.parseObject(resultJson);
//            jsonArray = jsonObject.getJSONArray("nodes");
//            Map<Long, Long> mapNodes = new HashMap<Long, Long>();
//
//            for (int i = 0; i < jsonArray.size(); i++) {
//                jsonObject1 = jsonArray.getJSONObject(i);
//                jsonObject1.put("index", Long.valueOf(i));
//                mapNodes.put(jsonObject1.getLong("id"), Long.valueOf(i));
//            }
//            object.put("nodes", jsonArray);
//
//            // 替换source和target值
//            JSONArray jsonArrayEdges = new JSONArray();
//            jsonArray = jsonObject.getJSONArray("links");
//            long long1;
//            long long2;
//            String str;
//            long lng2;
//            for (int i = 0; i < jsonArray.size(); i++) {
//                JSONObject jsonObject3 = new JSONObject();
//                jsonObject1 = jsonArray.getJSONObject(i);
//                long1 = jsonObject1.getInteger("source");
//                long2 = jsonObject1.getInteger("target");
//                str = jsonObject1.getString("type");
//                lng2 = jsonObject1.getLong("id");
//                if (mapNodes.containsKey(long1)) {
//                    jsonObject3.put("source", mapNodes.get(long1));
//                    jsonObject3.put("sourceId", long1);
//                }
//                if (mapNodes.containsKey(long2)) {
//                    jsonObject3.put("target", mapNodes.get(long2));
//                    jsonObject3.put("targetId", long2);
//                }
//                jsonObject3.put("type", str);
//                jsonObject3.put("id", lng2);
//                jsonArrayEdges.add(jsonObject3);
//            }
//            object.put("links", jsonArrayEdges);
//            resultJson = object.toString();
//        } else {
//            resultJson = "{" + nodes + "}";
//        }
//        logger.info("Node and edge result json:");
//        logger.info(resultJson);
//        return resultJson;
//    }

    /**
     * @param
     * @return
     * @Description: TODO(Close driver and session)
     */
    public void close() {
        session.close();
        driver.close();
    }

    /**
     * @param
     * @return
     * @Description: TODO(Test main entrance)
     */
    public static void main(String[] args) {
        DataToD3 dataToD3 = new DataToD3("bolt://localhost:7687", "neo4j", "123456");

//        String webJsonPath = "web/Neo4jSon.json";
        String webJsonPath = "KnowledgeGraphVisual/test.json";

        String cypher = "MATCH p=(n:People:人民的名义)-[]-() RETURN p";
//        dataToD3.jointJsonFile(cypher, webJsonPath);
//        dataToD3.jointJson(cypher);
        dataToD3.close();
    }
}
