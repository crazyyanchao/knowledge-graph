package casia.isiteam.knowledgegraph.controller;
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

import casia.isiteam.knowledgegraph.utils.FileOperate;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@Controller
@RequestMapping("/hello")
@CrossOrigin(origins = "*", maxAge = 3600) // 为了支持跨源请求添加注解
public class HelloController {

    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    // 访问页面：http://localhost:7476/knowledge-graph/hello/index

    /**
     * @param
     * @return
     * @Description: TODO(页面访问Index入口)
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Ajax 示例");
        return "index";
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
}



