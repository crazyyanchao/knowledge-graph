package data.lab.knowledgegraph.service;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import casia.isiteam.zdr.wltea.analyzer.cfg.Configuration;
import casia.isiteam.zdr.wltea.analyzer.core.IKSegmenter;
import casia.isiteam.zdr.wltea.analyzer.core.Lexeme;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.knowledgegraph.service
 * @Description: TODO
 * @date 2020/5/23 9:35
 */
public class IkAnalyzerTest {
    private List<String> ikAnalyzer(String text) {
        PropertyConfigurator.configureAndWatch("dic" + File.separator + "log4j.properties");
        Configuration cfg = new Configuration(true);

        StringReader input = new StringReader(text.trim());
        IKSegmenter ikSegmenter = new IKSegmenter(input, cfg);

        List<String> results = new ArrayList<>();
        try {
            for (Lexeme lexeme = ikSegmenter.next(); lexeme != null; lexeme = ikSegmenter.next()) {
                results.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Test
    public void ikAnalyzerOptional(){
        List<String> words = ikAnalyzer("傻宝旗舰店售卖温控器吗=QA");
        System.out.println(words);
    }
}
