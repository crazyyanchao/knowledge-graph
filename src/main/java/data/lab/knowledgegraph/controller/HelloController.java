package data.lab.knowledgegraph.controller;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import data.lab.knowledgegraph.model.UserJson;
import data.lab.knowledgegraph.service.DataServiceImpl;
import data.lab.knowledgegraph.utils.FileOperate;
import com.alibaba.fastjson.JSONObject;
import data.lab.knowledgegraph.utils.SearchSentenceProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isiteam.knowledgegraph.controller
 * @Description: TODO
 * @date 2020/4/29 22:22
 */
@Controller
@RequestMapping("/hello")
@CrossOrigin(origins = "*", maxAge = 3600) // 为了支持跨源请求添加注解
public class HelloController {

    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private DataServiceImpl dataService;

    /**
     * @param
     * @return http://localhost:7476/knowledge-graph/hello/index
     * @Description: TODO(页面访问Index入口)
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Ajax");
        return "index";
    }

    /**
     * @param
     * @return http://localhost:7476/knowledge-graph/hello/search
     * @Description: TODO(页面访问search d3.js show入口)
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchMyc(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Ajax");
        return "search";
    }

    /**
     * @param
     * @return
     * @Description: TODO(通过数据Type id加载圆形分区图数据和测试知识图谱构图数据)
     */
    @RequestMapping(value = "/dataSource/type/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String dataSource(@PathVariable("id") Integer id) {
        JSONObject jsonObject;
        String str = null;
        if (id == 1) {
            str = "CircularPartition.json";
        } else if (id == 2) {
            str = "test.json";
        }
        System.out.println(str);
        InputStream inputStream = HelloController.class.getClassLoader().getResourceAsStream("static/data/" + str);
        FileOperate operate = new FileOperate();
        str = operate.convertStreamToString(inputStream);
        jsonObject = JSONObject.parseObject(str);
        return jsonObject.toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(通过实体名模糊推荐与搜索关联信息 ( 带权限过滤与无权限过滤))
     */
    @RequestMapping(value = "/dataSource/zdrSearch", method = RequestMethod.POST)
    @ResponseBody
    public String getGraphEntityInfoZdrSearch(@RequestBody UserJson userJson) {
        String name = userJson.getName();
        JSONObject graphData = dataService.searchZdrInfo(name);
        return graphData.toJSONString();
    }
    /**
     * @param
     * @return
     * @Description: TODO(搜索问答句处理响应)
     */
    @RequestMapping(value = "/dataSource/answer", method = RequestMethod.POST)
    @ResponseBody
    public String getAnswer(@RequestBody UserJson userJson) {
        String askString = userJson.getAskString();
        JSONArray sysIds = userJson.getSysTemUserId();
        String startTime = userJson.getStartTime();
        String stopTime = userJson.getStopTime();
        SearchSentenceProcess sentenceProcess = new SearchSentenceProcess();
        String cypher = sentenceProcess.extractionCypher(askString, sysIds, startTime, stopTime);

        JSONObject graphData = null;
        if (cypher != null) {
            graphData = dataService.searchAsk(cypher);
        }

        return graphData.toJSONString();
    }
    /**
     * @param
     * @return
     * @Description: TODO(Super_Zzzz)
     */
    @RequestMapping(value = "/dataSource/search", method = RequestMethod.POST)
    @ResponseBody
    public String getGraphEntityInfoSearch(@RequestBody UserJson userJson) {
        String name = userJson.getName();
        JSONArray sysIds = userJson.getSysTemUserId();
        JSONObject graphData = dataService.searchInfo(name, sysIds);
        return graphData.toJSONString();
    }
    /**
     * @param
     * @return
     * @Description: TODO(通过多个实体名模糊搜索实体间关系 （ 带权限过滤与无权限过滤 ）)
     */
    @RequestMapping(value = "/dataSource/zdrRealSearch", method = RequestMethod.POST)
    @ResponseBody
    public String getRelationshipInfoZdrSearch(@RequestBody UserJson userJson) {
        JSONArray nameArray = userJson.getNameArray();
        JSONArray sysIds = userJson.getSysTemUserId();
        JSONObject graphData = dataService.searchZdrRelaInfo(nameArray, sysIds);
        return graphData.toJSONString();
    }
}



