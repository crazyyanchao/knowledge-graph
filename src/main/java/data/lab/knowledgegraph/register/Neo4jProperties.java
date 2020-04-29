package data.lab.knowledgegraph.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.register
 * @Description: TODO(配置读入类)
 * @date 2020/4/29 23:05
 */
@Component
@ConfigurationProperties(prefix = "neo4j")
public class Neo4jProperties {
    private String bolt;
    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public String getUsername() {

        return username;
    }

    public String getBolt() {

        return bolt;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public void setBolt(String bolt) {

        this.bolt = bolt;
    }
}
