package data.lab.knowledgegraph.service;

import casia.isi.neo4j.common.CRUD;
import casia.isi.neo4j.common.Field;
import casia.isi.neo4j.compose.NeoComposer;
import casia.isi.neo4j.model.Label;
import casia.isi.neo4j.model.RelationshipType;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.service
 * @Description: TODO
 * @date 2020/5/1 15:15
 */
public class DataServiceImplTest {

    private static final String ipPorts = "localhost:7687";
    private static final NeoComposer composer = new NeoComposer(ipPorts, "neo4j", "123456");

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configureAndWatch("conf" + File.separator + "log4j.properties");
    }

    @Test
    public void importCsvFileNodesAndRelations() {

        String nodesCsvName = "node-test.csv";
        String relationsCsvName = "relation-test.csv";
        composer.deleteCsv(nodesCsvName);
        composer.deleteCsv(relationsCsvName);

        // =======================================================生成NODE=======================================================
        /**
         * 勿必保证CSV文件字段列的写入顺序和执行导入时传入字段的顺序一致
         * **/
        String[] nodeRows = new String[]{"asdsad32423,john,这是导入的CSV,10\r\n",
                "345ssadsadsa,peter,这是导入的CSV,120324\r\n",
                "dsadsad,刘备,这是导入的CSV,3425\r\n",
                "sadsadasda,诸葛亮,这是导入的CSV,56465\r\n",
                "sadsadasdasd3v,司马懿,90870\r\n"
        };
//        String[] nodeRows = new String[4000000];
//        for (int i = 0; i <100000; i++) {
//            nodeRows[i]="asdsad32423"+i+",john,这是导入的CSV,10\r\n";
//            nodeRows[i+1]="345ssadsadsa"+i+",peter,这是导入的CSV,120324\r\n";
//            nodeRows[i+2]="dsadsad"+i+",刘备,这是导入的CSV,3425\r\n";
//            nodeRows[i+3]="asdsad32423"+i+",john,这是导入的CSV,10\r\n";
//            nodeRows[i+4]="sadsadasda"+i+",诸葛亮,这是导入的CSV,56465\r\n";
//            nodeRows[i+5]="sadsadasdasd3v"+i+",司马懿,90870\r\n";
//        }

        for (int i = 0; i < nodeRows.length; i++) {
            String datum = nodeRows[i];
            composer.writeCsvBody(nodesCsvName, datum);
        }

        /**
         * @param csvName:CSV文件名（数据写入CSV的顺序需要和方法传入参数顺序保持一致）String _uniqueField, String... _key
         * @param label:节点标签
         * @param _uniqueField:合并的唯一字段
         * @param _key:MERGE的属性字段
         * @return
         * @Description: TODO(导入节点CSV)
         */
        System.out.println(composer.executeImportCsv(1000, nodesCsvName, Label.label("Person"), Field.UNIQUEUUID.getSymbolValue(),
                Field.ENTITYNAME.getSymbolValue(), "comment", "count"));

        // 所有PERSON节点设置name属性
        composer.execute("MATCH (n:Person) SET n.name=n._entity_name", CRUD.UPDATE);

        // =======================================================生成关系=======================================================
        /**
         * 勿必保证CSV文件字段列的写入顺序和执行导入时传入字段的顺序一致
         * **/
        String[] relationRows = new String[]{"asdsad32423,345ssadsadsa," + System.currentTimeMillis() + ",这是导入的CSV\r\n",
                "sadsadasda,asdsad32423," + System.currentTimeMillis() + ",这是导入的CSV\r\n",
                "345ssadsadsa,sadsadasdasd3v," + System.currentTimeMillis() + ",这是导入的CSV\r\n"
        };
        for (int i = 0; i < relationRows.length; i++) {
            String datum = relationRows[i];
            composer.writeCsvBody(relationsCsvName, datum);
        }
        /**
         * @param csvName:CSV文件名（数据写入CSV的顺序需要和方法传入参数顺序保持一致）String _uniqueFieldStart,String _uniqueFieldEnd, String... _key
         * @param relationshipType:生成的关系名
         * @param startNodeLabel:起始节点的标签
         * @param _uniqueFieldStart:开始节点
         * @param endNodeLabel:结束节点的标签
         * @param _uniqueFieldEnd:结束节点
         * @param _key:MERGE的属性字段
         * @return
         * @Description: TODO(导入关系CSV)
         */
        System.out.println(composer.executeImportCsv(1000, relationsCsvName, RelationshipType.withName("好友"), Label.label("Person"),
                Label.label("Person"), Field.UNIQUEUUID.getSymbolValue(), Field.UNIQUEUUID.getSymbolValue(), "current_time", "comment"));
    }
}

