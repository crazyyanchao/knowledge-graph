package data.lab.knowledgegraph.repository;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.lab.knowledgegraph.register.Neo4jProperties;
import data.lab.knowledgegraph.utils.FileOperate;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.repository
 * @Description: TODO(Build knowledge graph template)
 * @date 2020/4/29 23:12
 */
@Service
public class CypherNeo4jOperationV3 {

    @Autowired
    private Neo4jProperties neo4jProperties;

    private final static Logger logger = LoggerFactory.getLogger(CypherNeo4jOperationV3.class);
    private FileOperate operate;

    // NEo4j driver properties
    private String URI;
    private String USER;
    private String PASSWORD;
    private Driver driver;
    private Session session;
    private StatementResult result;

    private Map<String, String> rawEntityLabelList;  // Load raw entity labels
    private Map<String, Map<String, String>> rawEntityPropertyMap; // Load raw entity properties
    private List<String[]> rawEntityTritupleList; // Load entity relationship trituple (entity1,entity2,relationships)

    private static Map<String, CypherNeo4jOperationV3> managerMap = new HashMap<String, CypherNeo4jOperationV3>();

    public CypherNeo4jOperationV3() {
    }

    /**
     * @param
     * @return
     * @Description: TODO(Use the default neo4j database - neo4j.conf)
     */
    public CypherNeo4jOperationV3(String uri, String user, String password) {
        this.URI = uri.trim();
        this.USER = user.trim();
        this.PASSWORD = password.trim();
        this.driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
        this.session = driver.session();
    }

    public CypherNeo4jOperationV3(String labelPath, String propertyPath, String trituplePath, String uri, String user, String password) {
        this.rawEntityLabelList = loadEntityLabels(labelPath.trim());
        this.rawEntityPropertyMap = loadEntityProperty(propertyPath.trim());
        this.rawEntityTritupleList = loadEntityTrituple(trituplePath.trim());
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
    public synchronized static final CypherNeo4jOperationV3 getInstance() {
        CypherNeo4jOperationV3 instance = managerMap.get("neo4j");
//        if (instance == null) {
//            instance = new CypherNeo4jOperationV3(neo4jProperties.getBolt(), neo4jProperties.getUsername(), neo4jProperties.getPassword());
//            managerMap.put("neo4j", instance);
//        }
        return instance;
    }

    /**
     * @param
     * @return
     * @Description: TODO(To ensure that there is only one instance)
     */
    public synchronized static final CypherNeo4jOperationV3 getInstance(String bolt, String username, String password) {
        CypherNeo4jOperationV3 instance = managerMap.get("neo4j");
        if (instance == null) {
            instance = new CypherNeo4jOperationV3(bolt, username, password);
            managerMap.put("neo4j", instance);
        }
        return instance;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Load entities relationship trituple)
     */
    public List<String[]> loadEntityTrituple(String trituplePath) {
        List<String[]> list = new ArrayList<String[]>();
        operate = new FileOperate();
        String str = operate.readAllLine(trituplePath, "UTF-8");
        String[] arr = str.split("\r\n");
        String[] arrS;
        List<String> stringList = new ArrayList<String>();
        for (String s : arr) {
            arrS = s.split(" ");
            if (arrS.length == 3) {
                str = arrS[0] + "," + arrS[1] + "," + arrS[2];
                if (!stringList.contains(str)) {
                    stringList.add(str);
                }
            }
        }
        for (String s : stringList) {
            arrS = s.split(",");
            list.add(arrS);
        }
        return list;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Load entity property)
     */
    public Map<String, Map<String, String>> loadEntityProperty(String propertyPath) {
        Map<String, Map<String, String>> reMap = new HashMap<>();
        operate = new FileOperate();
        String str = operate.readAllLine(propertyPath, "UTF-8");
        String[] arr = str.split("\r\n");
        String[] arrS;
        Map<String, String> map;
        String[] arrMap;
        for (String s : arr) {
            map = new HashMap<>();
            arrS = s.split(" ");
            if (arrS.length == 2) {
                arrMap = arrS[1].split(",");
                for (String s1 : arrMap) {
                    String[] re = s1.split("=");
                    map.put(re[0], re[1]);
                }
                reMap.put(arrS[0], map);
            }
        }
        return reMap;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Load entity labels)
     */
    public Map<String, String> loadEntityLabels(String labelPath) {
        List<String[]> list = new ArrayList<String[]>();
        Map<String, String> map = new HashMap<>();
        operate = new FileOperate();
        String str = operate.readAllLine(labelPath, "UTF-8");
        String[] arr = str.split("\r\n");
        String[] arrS;
        List<String> filterList = new ArrayList<>();
        for (String s : arr) {
            arrS = s.split(" ");
            if (arrS.length == 2) {
                if (!filterList.contains(arrS[0])) {
                    filterList.add(arrS[0]);
                }
                list.add(arrS);
            }
        }
        for (String s : filterList) {
            for (String[] strings : list) {
                if (s.equals(strings[0])) {
                    map.put(strings[0], strings[1]);
                    break;
                }
            }
        }
        return map;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Build entities knowledge graph / vault)
     */
    public void run() {

        String aLabel;
        String bLabel;
        String aEntityName;
        StringBuilder aEntityProperties;
        String bEntityName;
        StringBuilder bEntityProperties;
        String relationship;
        StringBuilder relationshipPro;

        Map<String, String> map;
        String[] arr;
        String[] sarr;
        String[] ssr;
        List<String> list;
        for (String[] strings : rawEntityTritupleList) {

            aLabel = rawEntityLabelList.get(strings[0]);
            bLabel = rawEntityLabelList.get(strings[1]);

            aEntityName = strings[0];
            map = rawEntityPropertyMap.get(aEntityName);
            aEntityProperties = new StringBuilder();
            // Add entity name
            aEntityProperties.append("name:" + "'" + aEntityName + "'");
            int i = 0;
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    i++;
                    if (i <= map.size()) {
                        aEntityProperties.append(",");
                    }
                    aEntityProperties.append(entry.getKey() + ":" + "'" + entry.getValue() + "'");
                }
            }

            bEntityName = strings[1];
            map = rawEntityPropertyMap.get(bEntityName);
            bEntityProperties = new StringBuilder();
            // Add entity name
            bEntityProperties.append("name:" + "'" + bEntityName + "'");
            int j = 0;
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    i++;
                    if (j <= map.size()) {
                        bEntityProperties.append(",");
                    }
                    bEntityProperties.append(entry.getKey() + ":" + "'" + entry.getValue() + "'");
                }
            }

            arr = strings[2].split(",,,,");
            relationship = arr[0];
            relationshipPro = new StringBuilder();
            relationshipPro.append("name:" + "'" + relationship + "'");
            if (arr.length >= 2) {
                sarr = arr[1].split(",");
                int k = 0;
                for (String s : sarr) {
                    k++;
                    ssr = s.split("=");
                    if (k <= ssr.length) {
                        relationshipPro.append(",");
                    }
                    relationshipPro.append(ssr[0] + ":" + "'" + ssr[1] + "'");
                }
            }

            list = new ArrayList<>();
            list.add(aLabel);
            list.add(bLabel);
            list.add(aEntityName);
            list.add(aEntityProperties.toString());
            list.add(bEntityName);
            list.add(bEntityProperties.toString());
            list.add(relationship);
            list.add(relationshipPro.toString());
            if (list.size() == 8) {
                //logger.info(list.get(0)+","+list.get(1)+","+list.get(2)+","+list.get(3)+","+list.get(4)+","+list.get(5)+","+list.get(6)+","+list.get(7));
                if (list.get(3).equals("周玲英")) {
                    System.out.println();
                }
                buildGraph(list);
            }
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(Build knowledge graph by cypher)
     */
    public void buildGraph(List<String> list) {

        String aLabel = list.get(0).replace(",", ":");
        String bLabel = list.get(1).replace(",", ":");
        String aEntityName = list.get(2);
        String aEntityProperties = list.get(3);
        String bEntityName = list.get(4);
        String bEntityPropertis = list.get(5);
        String relationship = list.get(6);
        String relationshipPro = list.get(7);

        String str;
        // 1.Build two entities
        // Build entity one
        if (!"".equals(aLabel) && !"".equals(aEntityProperties)) {
            str = "MERGE (a:" + aLabel + " {" + aEntityProperties + "}) RETURN a.name AS name;";
            System.out.println(str);
            session.run(str);
        }

        // Build entity two
        if (!"".equals(bLabel) && !"".equals(bEntityPropertis)) {
            str = "MERGE (a:" + bLabel + " {" + bEntityPropertis + "}) RETURN a.name AS name;";
            System.out.println(str);
            session.run(str);
        }

        // 2.Build relationships
        if (!"".equals(aLabel) && !"".equals(bLabel) &&
                !"".equals(aEntityName) && !"".equals(bEntityName) &&
                !"".equals(relationship) && !"".equals(relationshipPro)) {
            str = "MATCH (a:" + aLabel + " {name: '" + aEntityName + "' }),(b:" + bLabel + " {name: '" + bEntityName + "'})\n" +
                    "MERGE (a)-[r:" + relationship + " {" + relationshipPro + "}]->(b)\n" +
                    "RETURN a.name,type(r) AS a,b.name;";
            System.out.println(str.replace("\n", " "));
            session.run(str);
        }
    }

    /**
     * @param cypher
     * @return
     * @Description: TODO(界面传回操作请求 ， 拼成Match语句查库 ， 查库结果拼成 ( D3.js需要的格式)json格式)
     */
    public Object jointJson(String cypher) {
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
     * @Description: TODO(Clear neo4j DB)
     */
    public void clearNeo4j() {
        session.run("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r");
        session.close();
    }

    /**
     * @param
     * @return
     * @Description: TODO(通过节点ID查询返回json)
     */
    public Object findByNodeId(Integer id) {
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
                for (Map.Entry entry : mapNodePro.entrySet()) {
                    jsonObjectNode.put(entry.getKey().toString(), entry.getValue());
                }
                jsonArrayNode.add(jsonObjectNode);
            }
        }
        jsonObject.put("node", jsonArrayNode);
        return jsonObject.get("node");
    }

    /**
     * @param
     * @return
     * @Description: TODO(通过节点ID查询返回json)
     */
    public Object findByNodeName(String name) {
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
                mapNodePro = value.asMap();
                for (Map.Entry entry : mapNodePro.entrySet()) {
                    jsonObjectNode.put(entry.getKey().toString(), entry.getValue());
                }
                jsonArrayNode.add(jsonObjectNode);
            }
        }
        jsonObject.put("node", jsonArrayNode);
        return jsonObject.get("node");
    }

    /**
     * @param
     * @return
     * @Description: TODO(Test main entrance)
     */
    public static void main(String[] args) {
//        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");

        //String labelPath = "data/neo4j_knowledge_graph/temp_test/bsid/filter_repetition/entities_labels.txt";
        //String propertyPath = "data/neo4j_knowledge_graph/temp_test/bsid/filter_repetition/entities_properties.txt";
        //String trituplePath = "data/neo4j_knowledge_graph/temp_test/bsid/filter_repetition/entities_relationship_trituple.txt";

        // Filter street business
        //String labelPath = "data/neo4j_knowledge_graph/temp_test/location/entities_labels.txt";
        //String propertyPath = "data/neo4j_knowledge_graph/temp_test/location/entities_properties.txt";
        //String trituplePath = "data/neo4j_knowledge_graph/temp_test/location/entities_relationship_trituple.txt";

        String labelPath = "data/neo4j_knowledge_graph/entities_labels.txt";
        String propertyPath = "data/neo4j_knowledge_graph/entities_properties.txt";
        String trituplePath = "data/neo4j_knowledge_graph/entities_relationship_trituple.txt";

        String[] pro = {"bolt://localhost:7687", "neo4j", "123456"};
        //String[] pro = {"bolt://192.168.61.130:7687", "neo4j", "123456"};
//        CypherNeo4jOperationV3 operationV2 = new CypherNeo4jOperationV3(labelPath, propertyPath, trituplePath, pro[0], pro[1], pro[2]);

        // Start build knowledge graph/vault
//        operationV2.run();

//        CypherNeo4jOperationV3 operationV3 = new CypherNeo4jOperationV3(pro[0], pro[1], pro[2]);

        CypherNeo4jOperationV3 operationV3 = CypherNeo4jOperationV3.getInstance(pro[0], pro[1], pro[2]);

//        System.out.println(operationV2.findByNodeId(99815));
//        System.out.println(operationV2.findByNodeName("王大路"));

        //operationV3.clearNeo4j();

        String cypher = "MATCH p=(n)-[*1]-() WHERE id(n)=99815 RETURN p;";
        operationV3.jointJson(cypher);

//        CypherNeo4jOperationV3 operationV4 = new CypherNeo4jOperationV3(pro[0], pro[1], pro[2]);
        CypherNeo4jOperationV3 operationV4 = CypherNeo4jOperationV3.getInstance(pro[0], pro[1], pro[2]);
        String cypher2 = "MATCH p=(n)-[*1]-() WHERE id(n)=99817 RETURN p;";
        operationV4.jointJson(cypher2);
//        operationV3.close();
    }
}
