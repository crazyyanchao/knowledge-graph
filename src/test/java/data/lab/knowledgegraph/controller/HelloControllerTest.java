package data.lab.knowledgegraph.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Test;

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