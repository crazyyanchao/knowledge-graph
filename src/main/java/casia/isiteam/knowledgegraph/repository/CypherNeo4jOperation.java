package casia.isiteam.knowledgegraph.repository;
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
import org.apache.log4j.PropertyConfigurator;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class CypherNeo4jOperation {
    private final static Logger logger = LoggerFactory.getLogger(CypherNeo4jOperation.class);

    // NEo4j driver properties
    private String URI;
    private String USER;
    private String PASSWORD;
    private Driver driver;
    private Session session;
    private StatementResult result;

    private static Map<String, CypherNeo4jOperation> managerMap = new HashMap<String, CypherNeo4jOperation>();

    public CypherNeo4jOperation() {
    }

    /**
     * @param
     * @return
     * @Description: TODO(Use the default neo4j database - neo4j.conf)
     */
    public CypherNeo4jOperation(String uri, String user, String password) {
        this.URI = uri.trim();
        this.USER = user.trim();
        this.PASSWORD = password.trim();
        this.driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
        this.session = driver.session();
    }

    /**
     * @param
     * @return
     * @Description: TODO(To ensure that there is only one instance)
     */
    public synchronized static final CypherNeo4jOperation getInstance(String bolt, String username, String password) {
        CypherNeo4jOperation instance = managerMap.get("neo4j");
        if (instance == null) {
            instance = new CypherNeo4jOperation(bolt, username, password);
            managerMap.put("neo4j", instance);
        }
        logger.info("Neo4j status " + instance.toString());
        return instance;
    }

    /**
     * @param cypher
     * @return
     * @Description: TODO(界面传回操作请求 ， 拼成Match语句查库 ， 查库结果拼成 ( D3.js需要的格式)json格式)
     */
    public Object jointJson(String cypher) {
        logger.info(cypher);
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
                        Iterable<String> iterable = n.labels();
                        Iterator<String> iterator = iterable.iterator();
                        StringBuilder stringBuilder = new StringBuilder();
                        while (iterator.hasNext()) {
                            String s = iterator.next();
                            stringBuilder.append(s);
                            if (iterator.hasNext()) {
                                stringBuilder.append(",");
                            }
                        }
                        nodes.append("\"labels\":" + "\"" + stringBuilder.toString() + "\"" + ",");
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
        logger.info("Node and edge result json:");
        logger.info(resultJson);
        return resultJson;
    }

    /**
     * @param cypher
     * @return
     * @Description: TODO(界面传回操作请求 ， 拼成Match语句查库 ， 查库结果拼成 ( D3.js需要的格式)json格式)
     */
    public Object jointJson(String cypher, long index) {
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

            // nodeIndex用来与构图时节点索引值保持一致
            long nodeIndex = index + 1;
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject1 = jsonArray.getJSONObject(i);
                jsonObject1.put("index", nodeIndex);
                mapNodes.put(jsonObject1.getLong("id"), nodeIndex);
                nodeIndex++;
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
        logger.info("Node and edge result json:");
        logger.info(resultJson);
        return resultJson;
    }

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
     * @Description: TODO(通过节点ID查询返回json)
     */
    public Object findByNodeId(Long id) {
        Object reObj;
        String str = "MATCH (n) WHERE id(n)=" + id + " RETURN n;";
        logger.info(str);
        result = session.run(str);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArrayNode = new JSONArray();

        Map<String, Object> mapNodePro;
        Record record;
        List<Value> list;

        while (result.hasNext()) {
            record = result.next();
            list = record.values();
            JSONObject jsonObjectNode = new JSONObject();
            for (Value value : list) {
                mapNodePro = value.asMap();
                Node node = value.asNode();
                Iterable<String> iterable = node.labels();
                Iterator<String> iterator = iterable.iterator();
                StringBuilder stringBuilder = new StringBuilder();
                while (iterator.hasNext()) {
                    String s = iterator.next();
                    stringBuilder.append(s);
                    if (iterator.hasNext()) {
                        stringBuilder.append(",");
                    }
                }
                jsonObjectNode.put("labels", stringBuilder.toString());
                for (Map.Entry entry : mapNodePro.entrySet()) {
                    jsonObjectNode.put(entry.getKey().toString(), entry.getValue());
                }
                jsonArrayNode.add(jsonObjectNode);
            }
        }
        jsonObject.put("node", jsonArrayNode);
        reObj = jsonObject.get("node");
        logger.info(reObj.toString());
        return reObj;
    }

    /**
     * @param
     * @return
     * @Description: TODO(通过节点ID查询返回json)
     */
    public Object findByNodeName(String name) {
        Object reObj;
        String str = "MATCH (n) WHERE n.name=\"" + name + "\" RETURN n;";
        logger.info(str);
        result = session.run(str);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArrayNode = new JSONArray();

        Map<String, Object> mapNodePro;
        Record record;
        List<Value> list;

        while (result.hasNext()) {
            record = result.next();
            list = record.values();
            JSONObject jsonObjectNode = new JSONObject();
            for (Value value : list) {
                Node node = value.asNode();
                Iterable<String> iterable = node.labels();
                Iterator<String> iterator = iterable.iterator();
                StringBuilder stringBuilder = new StringBuilder();
                while (iterator.hasNext()) {
                    String s = iterator.next();
                    stringBuilder.append(s);
                    if (iterator.hasNext()) {
                        stringBuilder.append(",");
                    }
                }
                jsonObjectNode.put("labels", stringBuilder.toString());
                mapNodePro = value.asMap();
                for (Map.Entry entry : mapNodePro.entrySet()) {
                    jsonObjectNode.put(entry.getKey().toString(), entry.getValue());
                }
                jsonArrayNode.add(jsonObjectNode);
            }
        }
        jsonObject.put("node", jsonArrayNode);
        reObj = jsonObject.get("node");
        logger.info(reObj.toString());
        return reObj;
    }

    /**
     * @param name:实体名称
     * @return 实体标签，用","分隔
     * @Description: TODO(获取节点的标签)
     */
    public String getNodeLabels(String name) {
        String cypher = "match (n {name:'" + name + "'}) return n;";
        logger.info(cypher);
        result = session.run(cypher);
        StringBuilder stringBuilder = new StringBuilder();
        while (result.hasNext()) {
            Record record = result.next();
            List<Value> valueList = record.values();
            for (int i = 0; i < valueList.size(); i++) {
                Value value = valueList.get(i);
                Node node = value.asNode();
                Iterable<String> iterable = node.labels();
                Iterator<String> it = iterable.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    stringBuilder.append(next + ",");
                }
            }
        }
        String labels = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        return labels;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Test main entrance)
     */
    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");

        String[] pro = {"bolt://localhost:7687", "neo4j", "123456"};

        CypherNeo4jOperation operationV4 = CypherNeo4jOperation.getInstance(pro[0], pro[1], pro[2]);
        String cypher = "MATCH p=()-[r]->() RETURN p"; // ...
        Object obj = operationV4.jointJson(cypher); // D3.js需要的格式)json格式
        System.out.println(obj);
        operationV4.close();
    }
}
