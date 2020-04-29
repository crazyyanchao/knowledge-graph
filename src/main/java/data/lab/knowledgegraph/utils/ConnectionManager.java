package data.lab.knowledgegraph.utils;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.mchange.v2.c3p0.ComboPooledDataSource;
import data.lab.knowledgegraph.model.Dbproperties;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.utils
 * @Description: TODO(Database connection pool util class)
 * @date 2020/4/29 23:37
 */
public class ConnectionManager {

    private static ConnectionManager instance;
    private static ComboPooledDataSource dataSource;

    // Database connection pool storage management
    private static Map<String, ConnectionManager> managerMap = new HashMap<>();

    /**
     * 创建一个新的实例 ConnectionManager. Load properties.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws PropertyVetoException
     */
    private ConnectionManager(Dbproperties dbproperties) throws FileNotFoundException, IOException, PropertyVetoException {
        dataSource = new ComboPooledDataSource();
        dataSource.setUser(dbproperties.getUserName());
        dataSource.setPassword(dbproperties.getPassword());
        dataSource.setJdbcUrl(dbproperties.getUrl() + "?useSSL=false");    //	关闭安全套接加密层
        dataSource.setDriverClass(dbproperties.getDriver());
        dataSource.setInitialPoolSize(Integer.parseInt(dbproperties.getInitialPoolSize()));
        dataSource.setMaxPoolSize(Integer.parseInt(dbproperties.getMaxPoolSize()));    //	最大连接数
        dataSource.setMinPoolSize(Integer.parseInt(dbproperties.getMinPoolSize()));    //	最小连接数
        dataSource.setMaxStatements(Integer.parseInt(dbproperties.getMaxStatements()));    //	最长等待时间
        dataSource.setMaxIdleTime(Integer.parseInt(dbproperties.getMaxIdleTime()));    //	最大空闲时间，单位毫秒
        dataSource.setTestConnectionOnCheckout(Boolean.valueOf(dbproperties.getTestConnectionOnCheckout()));
        dataSource.setTestConnectionOnCheckin(Boolean.valueOf(dbproperties.getTestConnectionOnCheckin()));
        dataSource.setIdleConnectionTestPeriod(Integer.valueOf(dbproperties.getIdleConnectionTestPeriod()));
    }

    /**
     * @param @return dbName -- The unique identity of the database.(name+ip)common_192.168.7.190
     * @return ConnectionManager    返回类型
     * @throws
     * @Title: getInstance
     * @Description: TODO(To ensure that there is only one instance)
     */
    public synchronized static final ConnectionManager getInstance(Dbproperties dbproperties) {
        ConnectionManager instance = managerMap.get(dbproperties.getUrl());
        if (instance == null) {
            try {
                instance = new ConnectionManager(dbproperties);
                managerMap.put(dbproperties.getUrl(), instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * @param @return 参数
     * @return Connection    返回类型
     * @throws
     * @Title: getConnection
     * @Description: TODO(Sync lock)
     */
    public final Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * @param
     * @return mysql connection pool
     * @Description: TODO(Sync lock)
     */
    public final ComboPooledDataSource getDataPool() {
        return dataSource;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Close connection pool)
     */
    public static void close() {
        dataSource.close();
        instance = null;
        managerMap = null;
        managerMap = new HashMap<String, ConnectionManager>();
    }
}
