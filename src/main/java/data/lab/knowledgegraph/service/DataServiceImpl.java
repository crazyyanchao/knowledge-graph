package data.lab.knowledgegraph.service;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import casia.isi.neo4j.common.CRUD;
import casia.isi.neo4j.search.NeoSearcher;
import casia.isi.neo4j.util.JSONTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.lab.knowledgegraph.register.Neo4jProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.service
 * @Description: TODO(可视化数据接口实现)
 * @date 2020/4/29 22:58
 */
@Service
public class DataServiceImpl {

    private final static Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);

    @Autowired
    private Neo4jProperties neo4jProperties;

    private NeoSearcher neoSearcher;

    /**
     * @param
     * @return
     * @Description: TODO(初始化)
     */
    public void initLoad() {
        if (neoSearcher == null)
            logger.info("SERVER:" + neo4jProperties.getBolt() + " USER:" + neo4jProperties.getUsername() + " PWD:" + neo4jProperties.getPassword());
        neoSearcher = new NeoSearcher(neo4jProperties.getBolt(), neo4jProperties.getUsername(), neo4jProperties.getPassword());
    }

    /**
     * @param
     * @return
     * @Description: TODO(通过最顶层父标签加载标签树)
     */
    public JSONArray getLabelSpanningTree() {
        initLoad();
        String cypher = "MATCH (k:LabelsTree) WHERE k.hierarchy='1' OR  k.hierarchy=1\n" +
                "CALL apoc.path.spanningTree(k,{labelFilter:'+LabelsTree', maxLevel:3, optional:true, filterStartNode:true}) yield path return path";
//        return DbUtil.getLabelSpanningTree(cypher);
        return Objects.requireNonNull(null);
    }

    /**
     * @param
     * @return
     * @Description: TODO(关键词搜索)
     */
    public JSONObject searchZdrInfo(String name) {
        initLoad();
        StringBuilder builder = new StringBuilder();

        builder.append("match p=(n)-[]-(m) where n.name=~'.*" + name + ".*' return p limit 300 ");

        String cypher = builder.toString().substring(0, builder.length() - 10);
        JSONObject result = neoSearcher.execute(cypher, CRUD.RETRIEVE);
        return JSONTool.transferToOtherD3(result);
    }

    /**
     * @param
     * @return
     * @Description: TODO(搜索的问答处理响应)
     */
    public JSONObject searchAsk(String cypher) {
        initLoad();
        JSONObject result = neoSearcher.execute(cypher, CRUD.RETRIEVE);
        return JSONTool.transferToOtherD3(result);
    }

    /**
     * @param
     * @return
     * @Description: TODO(多实体之间关系搜索)
     */
    public JSONObject searchZdrRelaInfo(JSONArray nameArray, JSONArray sysIds) {
        initLoad();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < nameArray.size(); i++) {
            Object entityOne = nameArray.get(i);
            for (int j = i + 1; j < nameArray.size(); j++) {
                Object entityTwo = nameArray.get(j);

                builder.append("match p=(n)-[]-(m) where n.name=~'.*" + entityOne + ".*' and m.name=~'.*" + entityTwo + ".*' return p limit 50 union all ");
            }
        }

        String cypher = builder.toString().substring(0, builder.length() - 10);
        JSONObject result = neoSearcher.execute(cypher, CRUD.RETRIEVE);
        return JSONTool.transferToOtherD3(result);
    }

    /**
     * @param name:实体名称的碎片
     * @param sysIds:系统用户ID
     * @Description: TODO(通过实体名模糊推荐与搜索关联信息)
     */
    public JSONObject searchInfo(String name, JSONArray sysIds) {
        initLoad();
        StringBuilder builder = new StringBuilder();

        builder.append("match p=(n)-[]-(m) where n.name CONTAINS '" + name + "' return p limit 50 union all ");
        String cypher = builder.toString().substring(0, builder.length() - 10);

        if ("load-all".equals(name)) cypher = "MATCH p=()-[]->() RETURN p LIMIT 3000";

        JSONObject result = neoSearcher.execute(cypher, CRUD.RETRIEVE);
        return JSONTool.transferToOtherD3(result);
    }
}

