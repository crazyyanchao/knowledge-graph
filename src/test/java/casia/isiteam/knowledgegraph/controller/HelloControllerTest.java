package casia.isiteam.knowledgegraph.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.Assert.*;

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

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class HelloControllerTest {

//    @Autowired
//    private MockMvc mvc;

//    @Test
//    public void getNodeByName() {
//        try {
//            mvc.perform(MockMvcRequestBuilders.get("/dataSource/node/name/{name}").param("王大路"))
//                    .andExpect(MockMvcResultMatchers.status().isOk());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void getTest() {
        String str = "[\n" +
                "  \"user_1\",\n" +
                "  \"user_2\",\n" +
                "  \"user_3\" \n" +
                "]";
        JSONArray jsonArray = JSON.parseArray(str);
        System.out.println(jsonArray);
    }

    @Test
    public void getCypher(){
        StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("match p=(n {name:'name'})-[*1]-(m:user_id) return p union all ");
        String cypher = stringBuilder.toString().substring(0, stringBuilder.length() - 10);
        System.out.println(cypher);
    }
}