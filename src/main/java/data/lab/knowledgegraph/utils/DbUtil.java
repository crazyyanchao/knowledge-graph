package data.lab.knowledgegraph.utils;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.utils
 * @Description: TODO(Mysql operation tool)
 * @date 2020/4/29 23:38
 */
public class DbUtil {

    private final static Logger logger = LoggerFactory.getLogger(DbUtil.class);

    private static Connection conGraph; // 知识图谱预处理库
    private static Connection conZdr;   // ZDR库
    private static Connection conEvent; // 专题事件库

    public static boolean debug;

    public static void setConGraph(Connection conGraph) {
        DbUtil.conGraph = conGraph;
    }

    public static void setConZdr(Connection conZdr) {
        DbUtil.conZdr = conZdr;
    }

    public static void setConEvent(Connection conEvent) {
        DbUtil.conEvent = conEvent;
    }

    public static Connection getConGraph() {
        checkConnection(conGraph);
        return conGraph;
    }

    public static Connection getConZdr() {
        checkConnection(conZdr);
        return conZdr;
    }

    public static Connection getConEvent() {
        checkConnection(conEvent);
        return conEvent;
    }

    /**
     * @param con:连接池connection
     * @return
     * @Description: TODO(检查连接是否关闭或者为空)
     */
    public static void checkConnection(Connection con) {
        if (con != null) {
            try {
                if (con.isClosed()) {
                    ConnectionManager.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            ConnectionManager.close();
        }
    }

    private static ComboPooledDataSource conGraphPool;  // 知识图谱预处理库数据池
    private static ComboPooledDataSource conZdrPool;    // ZDR库数据池
    private static ComboPooledDataSource conEventPool;  // 专题事件库数据池
    private static ComboPooledDataSource conNeo4jPool;  // 图数据库数据池
    private static ComboPooledDataSource pool;

    public static ComboPooledDataSource getPool() {
        return pool;
    }

    public static void setPool(ComboPooledDataSource pool) {
        DbUtil.pool = pool;
    }

    public static void setConNeo4jPool(ComboPooledDataSource conNeo4jPool) {
        DbUtil.conNeo4jPool = conNeo4jPool;
    }

    public static ComboPooledDataSource getConNeo4jPool() {
        try {
            if (debug) {
                logger.info("MaxPoolSize:" + conNeo4jPool.getMaxPoolSize());
                logger.info("MinPoolSize:" + conNeo4jPool.getMinPoolSize());
                logger.info("Num busy connections:" + conNeo4jPool.getNumBusyConnections());
                logger.info("Num idle connections:" + conNeo4jPool.getNumIdleConnections());
                logger.info("Num connections:" + conNeo4jPool.getNumConnections());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conNeo4jPool;
    }

    public static void setConGraphPool(ComboPooledDataSource conGraphPool) {
        DbUtil.conGraphPool = conGraphPool;
    }

    public static void setConZdrPool(ComboPooledDataSource conZdrPool) {
        DbUtil.conZdrPool = conZdrPool;
    }

    public static void setConEventPool(ComboPooledDataSource conEventPool) {
        DbUtil.conEventPool = conEventPool;
    }

    public static ComboPooledDataSource getConGraphPool() {
        return conGraphPool;
    }

    public static ComboPooledDataSource getConZdrPool() {
        return conZdrPool;
    }

    public static ComboPooledDataSource getConEventPool() {
        return conEventPool;
    }

    /**
     * @param sql:SQL
     * @param arr:Para
     * @return
     * @Description: TODO(Save databases)
     */
    public static boolean insertSQL(String sql, Object[] arr, Connection con) {
        boolean bool = false;
        long startTime = System.nanoTime();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(sql);
            for (int i = 0, len = arr.length; i < len; i++) {
                pst.setObject(i + 1, arr[i]);
            }
            pst.executeUpdate();
            long endTime = System.nanoTime();
            System.out.println((endTime - startTime) / 1000000 + "ms");
            System.out.println("Test sql batch--->1.....");
            bool = true;
            System.out.println("------The execution of the insert operation was successful!------");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                logger.error("--------Execute insert sql error!/Data repeated insert ignore this item to continue execution!--------\terror code:" + e.getErrorCode());
            } else {
                e.printStackTrace();
            }
        } finally {
            closeAll(pst, con);
        }
        return bool;
    }

    /**
     * @param
     * @return
     * @Description: TODO(执行cypher语句)
     */
    public static void executeCypher(String cypher) {
        Connection con = null;
        PreparedStatement pre = null;

//        logger.info(cypher);
//        DbUtil.debug = true;

        ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
        if (dataSourcePool != null) {
            try {
                long startTime = System.nanoTime();
                con = dataSourcePool.getConnection();
                pre = con.prepareStatement(cypher);
                pre.executeQuery();
                long endTime = System.nanoTime();
                long consume = (endTime - startTime) / 1000000;
                logger.info("Execute cypher batch--->1 " + consume + "ms");
            } catch (SQLException e) {
                logger.error(cypher);
                int errorCode = e.getErrorCode();
                if (errorCode == 0) {
                    logger.error("errorCode:" + errorCode + ",Merge did not find a matching node n and can not create a new node due to conflicts with existing unique nodes!");
                } else {
                    e.printStackTrace();
                }
            } finally {
                DbUtil.closeAll(pre, con);
            }
        } else {
            logger.error("Data source pool is null,must be init!");
        }
    }

    /**
     * @param cypher:标准的cypher语句
     * @return
     * @Description: TODO(执行cypher语句 - JDBC方式)
     */
    public static JSONObject exetueCypherJDBC(String cypher) {
        logger.info(cypher);
        JSONObject jsonObject = null;
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;
        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            jsonObject = Neo4jDataUtils.getNeo4jResult(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return jsonObject;
    }

    /**
     * @param
     * @return
     * @Description: TODO(替换影响数据入库的特殊字符)
     */
    public static String repalceChars(String entityName) {

        if (entityName != null) {

            // 先替换反斜杠
            entityName = String.valueOf(entityName).replace("\\", "\\\\");

            // 再替换单引号
            entityName = String.valueOf(entityName).replace("'", "\\'");

        }
        return entityName;
    }

    public static String getCypherPropertyResult(String cypher) {
        logger.info(cypher);
        String array = null;
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;
        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            array = getCallResult(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return array;
    }

    public static JSONArray getCypherResult(String cypher) {
        logger.info(cypher);
        JSONArray JSONArray = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;

        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            JSONArray = Neo4jDataUtils.getCallResult(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return JSONArray;
    }

    public static JSONObject getCurrentNodes(String cypher) {
        logger.info(cypher);
        JSONObject object = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;

        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            object = Neo4jDataUtils.getCurrentNodes(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return object;
    }

    public static List<String> jointLabels(String cypher) {
        logger.info(cypher);
        List<String> object = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;

        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            object = Neo4jDataUtils.jointLabe(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return object;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Get call result)
     */
    public static String getCallResult(ResultSet result) {
        String array = null;
        try {
            while (result.next()) {
                String resultString = result.getString(1);
                if (resultString != null) {
                    array = resultString;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * @param sta:会话集
     * @param conn:数据库连接
     * @return
     * @Description: TODO(关闭数据库连接)
     */
    public static void closeAll(PreparedStatement sta, Connection conn) {
        if (sta != null) {
            try {
                sta.close();
                sta = null;
            } catch (SQLException e) {
                logger.error("Close Statement error!");
            }
        }
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                logger.error("Close Connection error!");
            }
        }
    }

    /**
     * @param result:结果集
     * @param pre:会话集
     * @param conn:数据库连接
     * @return
     * @Description: TODO(关闭数据库连接)
     */
    public static void closeAll(ResultSet result, PreparedStatement pre, Connection conn) {
        if (result != null) {
            try {
                result.close();
                result = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pre != null) {
            try {
                pre.close();
                pre = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONObject getCurrentNodeRelationships(String cypher) {
        logger.info(cypher);
        JSONObject object = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;
        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            object = Neo4jDataUtils.getCurrentNodeRelationships(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return object;
    }

    /**
     * @param
     * @return
     * @Description: TODO(交集校友碰撞)
     */
    public static JSONObject getIntersectionAlumniCount(String cypher, String relationshipType, String featureWord) {
        logger.info(cypher);
        JSONObject object = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;
        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            object = Neo4jDataUtils.getIntersectionAlumniCount(result, relationshipType, featureWord);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return object;
    }

    public static JSONObject getCurrentNodeLabelsRelationships(String cypher) {
        logger.info(cypher);
        JSONObject object = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;
        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            object = Neo4jDataUtils.getCurrentNodeLabelsRelationships(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return object;
    }

    public static JSONObject getNodeRelationships(String cypher) {
        logger.info(cypher);
        JSONObject object = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;
        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            object = Neo4jDataUtils.getNodeRelationships(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return object;
    }

    /**
     * @param
     * @return
     * @Description: TODO(同级校友 / 同期同事碰撞)
     */
    public static JSONObject getSameLevelAlumniCount(String cypher, String relationshipType, String featureWord) {
        logger.info(cypher);
        JSONObject object = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;
        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            object = Neo4jDataUtils.getSameLevelAlumniCount(result, relationshipType, featureWord);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return object;
    }

    /**
     * @param
     * @return
     * @Description: TODO(通过最顶层父标签加载标签树)
     */
    public static JSONArray getLabelSpanningTree(String cypher) {
        logger.info(cypher);
        JSONArray JSONArray = null;

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;

        try {
            long startTime = System.nanoTime();
            ComboPooledDataSource dataSourcePool = DbUtil.getConNeo4jPool();
            con = dataSourcePool.getConnection();
            pre = con.prepareStatement(cypher);
            result = pre.executeQuery();
            JSONArray = Neo4jDataUtils.getLabelSpanningTree(result);
            long endTime = System.nanoTime();
            long consume = (endTime - startTime) / 1000000;
            logger.info("Cypher match " + consume + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(result, pre, con);
        }
        return JSONArray;
    }
}
