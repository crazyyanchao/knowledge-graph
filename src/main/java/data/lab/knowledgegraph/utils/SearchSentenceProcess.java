package data.lab.knowledgegraph.utils;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.knowledgegraph.utils
 * @Description: TODO(Search sentence process)
 * @date 2018/5/26 9:27
 */
public class SearchSentenceProcess {

    private String[] perLocEventThingOrgArr = new String[]{""};

    // 问答举例：
    // 1、小明和吴良述有共同参与的组织吗？
    // 2、小明和吴良述有共同参与的事吗？
    // 3、小明都参与过哪些组织？
    // 4、小明都参与过哪些事？
    // 5、小明有哪些虚拟账号？

    // 问答举例：
    // 1、小明住哪里？
    // 2、小明家在哪里？
    // 3、小明的微信是什么？
    // 4、小明从哪儿毕业的？
    // 5、小明去过南湖路20号吗？
    // 6、小明和吴良述有一起去过的地方吗？
    // 7、小明和吴良述是什么关系？
    // 8、小明最近qq上线是什么时候？
    // 9、小明最近有啥异常行为吗？

    public String extractionCypher(String askString, JSONArray sysIds, String startTime, String stopTime) {
        String cypher = null;
        StringBuilder builder = new StringBuilder();
        if (askString.indexOf("哪里") != -1) {
            String entityOne = askString.substring(0, 2);
            builder.append("match p=(n:人)-[r:居住地]-(m:地) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            builder.append("match p=(n:地)-[r:居住地]-(m:人) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            builder.append("match p=(n:地)-[r:爆发地]-(m:事) where n.name=~'.*" + entityOne + ".*' and m.start_time>='" + startTime + "' and m.start_time<='" + stopTime + "' return p limit 50 union all ");
            builder.append("match p=(n:事)-[r:爆发地]-(m:地) where n.name=~'.*" + entityOne + ".*' and n.start_time>='" + startTime + "' and n.start_time<='" + stopTime + "'  return p limit 50 union all ");
            builder.append("match p=(n:人)-[r:截获位置]-(m:地) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            builder.append("match p=(n:地)-[r:截获位置]-(m:人) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            builder.append("match p=(n:人)-[r:身份证归属地]-(m:地) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            builder.append("match p=(n:地)-[r:身份证归属地]-(m:人) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            builder.append("match p=(n:地)-[r:隶属地]-(m:地) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);

        } else if (askString.indexOf("qq") != -1) {
            String entityOne = askString.substring(0, 2);
            builder.append("match p=(n:人)-[r:QQ号]-(m:物) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1]-(k:物)-[r2:QQ号]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);

        } else if (askString.indexOf("微信") != -1) {
            String entityOne = askString.substring(0, 2);
            builder.append("match p=(n:人)-[r:WeChat号]-(m:物) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            builder.append("match p=(n:物)-[r:WeChat号]-(m:人) where n.name=~'.*" + entityOne + ".*'  return p limit 50 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);

        } else if (askString.indexOf("毕业") != -1) {
            String entityOne = askString.substring(0, 2);
            builder.append("match p=(n:人)-[r:毕业]-(m:地) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            builder.append("match p=(n:地)-[r:毕业]-(m:人) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);

        } else if (askString.indexOf("行为") != -1) {
            String entityOne = askString.substring(0, 2);
            builder.append("match p=(n:人)-[r:参与事]-(m:事) where n.name=~'.*" + entityOne + ".*' and n.sysuser_id in " + sysIds + " and m.start_time>='" + startTime + "' and m.start_time<='" + stopTime + "' return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1:手机号]-(k:物)-[r2:换手机设备]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1:手机号]-(k:物)-[r2:换手机卡]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1:QQ号]-(k:物)-[r2:增加好友]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1:QQ号]-(k:物)-[r2:删除好友]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1:QQ号]-(k:物)-[r2:上线]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1:QQ号]-(k:物)-[r2:下线]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);

        } else if (askString.indexOf("关系") != -1) {
            String entityOne = askString.substring(0, 2);
            String entityTwo = askString.substring(4, 6);
            for (int i = 0; i < perLocEventThingOrgArr.length; i++) {
                String label = perLocEventThingOrgArr[i];
                builder.append("match (n:人),(m:人) ");
                builder.append("where n.name=~'.*" + entityOne + ".*' and n.sysuser_id in " + sysIds + " and m.name=~'.*" + entityTwo + ".*' and m.sysuser_id in " + sysIds + " ");
                builder.append("match (f:" + label + ") ");
                builder.append("match p1=(n)-[r1*1]-(f),p2=(m)-[r2*1]-(f) ");
                builder.append("return p1,p2 limit 200 union all ");
            }
            cypher = builder.toString().substring(0, builder.length() - 10);

        } else if (askString.indexOf("去过") != -1) {
            String entityOne = askString.substring(0, 2);
            String entityTwo = askString.substring(5, 7);
            builder.append("match p=(n:人)-[*1]-(m:地) where n.name=~'.*" + entityOne + ".*' and m.name=~'.*" + entityTwo + ".*' return p limit 50 union all ");
            builder.append("match p=(n:地)-[*1]-(m:人) where n.name=~'.*" + entityOne + ".*' and m.name=~'.*" + entityTwo + ".*' return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1:截获位置]-(k:地)-[r2:截获位置]-(m:人) where n.name=~'.*" + entityOne + ".*' and m.name=~'.*" + entityTwo + ".*' return p limit 50 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);

        } else if (askString.indexOf("上线") != -1) {
            String entityOne = askString.substring(0, 2);
            builder.append("match p=(n:人)-[r1:QQ号]-(k:物)-[r2:上线]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            builder.append("match p=(n:人)-[r1:WeChat号]-(k:物)-[r2:上线]-(m:物) where n.name=~'.*" + entityOne + ".*' return p limit 50 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);

        } else if (askString.indexOf("共同参与") != -1 && askString.indexOf("组织") != -1) {
            String entityOne = askString.substring(0, 2);
            String entityTwo = askString.substring(4, 6);
            builder.append("match (n:人),(m:人) ");
            builder.append("where n.name=~'.*" + entityOne + ".*' and n.sysuser_id in " + sysIds + " and m.name=~'.*" + entityTwo + ".*' and m.sysuser_id in " + sysIds + " ");
            builder.append("match (f:组织) ");
            builder.append("match p1=(n)-[r1]-(f),p2=(m)-[r2]-(f) ");
            builder.append("return p1,p2 limit 200 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);
        } else if (askString.indexOf("共同参与") != -1 && askString.indexOf("事") != -1) {
            String entityOne = askString.substring(0, 2);
            String entityTwo = askString.substring(4, 6);
            builder.append("match (n:人),(m:人) ");
            builder.append("where n.name=~'.*" + entityOne + ".*' and n.sysuser_id in " + sysIds + " and m.name=~'.*" + entityTwo + ".*' and m.sysuser_id in " + sysIds + " ");
            builder.append("match (f:事) ");
            builder.append("where f.start_time>='" + startTime + "' and f.start_time<='" + stopTime + "' ");
            builder.append("match p1=(n)-[r1:参与事]-(f),p2=(m)-[r2:参与事]-(f) ");
            builder.append("return p1,p2 limit 200 union all ");
            cypher = builder.toString().substring(0, builder.length() - 10);
        } else if (askString.indexOf("参与过") != -1 && askString.indexOf("组织") != -1) {
            String entityOne = askString.substring(0, 2);
            cypher = "match p=(n:人)-[r]-(m:组织) where n.name=~'.*" + entityOne + ".*' and n.sysuser_id in " + sysIds + " return p limit 100";
        } else if (askString.indexOf("参与过") != -1 && askString.indexOf("事") != -1) {
            String entityOne = askString.substring(0, 2);
            cypher = "match p=(n:人)-[r:参与事]-(m:事) where n.name=~'.*" + entityOne + ".*' and n.sysuser_id in " + sysIds + " and m.start_time>='" + startTime + "' and m.start_time<='" + stopTime + "' return p limit 100";
        } else if (askString.indexOf("哪些虚拟账号") != -1) {
            String entityOne = askString.substring(0, 2);
            cypher = "match p=(n:人)-[r]-(m:物) where n.name=~'.*" + entityOne + ".*' and n.sysuser_id in " + sysIds + " return p limit 100";
        }
        return cypher;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Test main entrance)
     */
    public static void main(String[] args) {
        SearchSentenceProcess process = new SearchSentenceProcess();
//        process.extractionCypher(askString, sysIds, startTime, "小明最近有啥异常行为吗？");
    }
}

