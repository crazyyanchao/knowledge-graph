package data.lab.knowledgegraph.properties;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.properties.Neo4jDataProperties
 * @Description: TODO(图数据库连接池配置)
 * @date 2020/4/30 19:58
 */
@Component
@ConfigurationProperties(prefix = "neo4jdata")
public class Neo4jDataProperties {
    private String url;
    private String userName;
    private String password;
    private String driver;
    private String initialPoolSize;
    private String minPoolSize;
    private String maxPoolSize;
    private String maxStatements;
    private String maxIdleTime;
    private String acquireIncrement;
    private String acquireRetryAttempts;
    private String breakAfterAcquireFailure;
    private String testConnectionOnCheckout;
    private String testConnectionOnCheckin;
    private String idleConnectionTestPeriod;
    private String maxStatementsPerConnection;
    private String checkoutTimeout;

    public void setCheckoutTimeout(String checkoutTimeout) {
        this.checkoutTimeout = checkoutTimeout;
    }

    public String getCheckoutTimeout() {
        return checkoutTimeout;
    }

    public void setMaxStatementsPerConnection(String maxStatementsPerConnection) {
        this.maxStatementsPerConnection = maxStatementsPerConnection;
    }

    public String getMaxStatementsPerConnection() {

        return maxStatementsPerConnection;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

    public String getInitialPoolSize() {
        return initialPoolSize;
    }

    public String getMinPoolSize() {
        return minPoolSize;
    }

    public String getMaxPoolSize() {
        return maxPoolSize;
    }

    public String getMaxStatements() {
        return maxStatements;
    }

    public String getMaxIdleTime() {
        return maxIdleTime;
    }

    public String getTestConnectionOnCheckout() {
        return testConnectionOnCheckout;
    }

    public String getTestConnectionOnCheckin() {
        return testConnectionOnCheckin;
    }

    public String getIdleConnectionTestPeriod() {
        return idleConnectionTestPeriod;
    }

    public String getAcquireIncrement() {
        return acquireIncrement;
    }

    public String getAcquireRetryAttempts() {
        return acquireRetryAttempts;
    }

    public String getBreakAfterAcquireFailure() {
        return breakAfterAcquireFailure;
    }

    public void setAcquireIncrement(String acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }

    public void setAcquireRetryAttempts(String acquireRetryAttempts) {
        this.acquireRetryAttempts = acquireRetryAttempts;
    }

    public void setBreakAfterAcquireFailure(String breakAfterAcquireFailure) {
        this.breakAfterAcquireFailure = breakAfterAcquireFailure;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setInitialPoolSize(String initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public void setMinPoolSize(String minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public void setMaxPoolSize(String maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setMaxStatements(String maxStatements) {
        this.maxStatements = maxStatements;
    }

    public void setMaxIdleTime(String maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public void setTestConnectionOnCheckout(String testConnectionOnCheckout) {
        this.testConnectionOnCheckout = testConnectionOnCheckout;
    }

    public void setTestConnectionOnCheckin(String testConnectionOnCheckin) {
        this.testConnectionOnCheckin = testConnectionOnCheckin;
    }

    public void setIdleConnectionTestPeriod(String idleConnectionTestPeriod) {
        this.idleConnectionTestPeriod = idleConnectionTestPeriod;
    }
}