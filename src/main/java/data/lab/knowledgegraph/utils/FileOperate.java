package data.lab.knowledgegraph.utils;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isiteam.knowledgegraph.utils
 * @Description: TODO
 * @date 2020/4/29 22:21
 */
public class FileOperate {

    /**
     * @param
     * @return
     * @Description: TODO(InputStream to String)
     */
    public String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(Read one line)
     */
    public String readOneLine(String filePath) {
        File file = new File(filePath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Read all line)
     */
    public String readAllLine(String filePath, String encoding) {
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(fileContent);

            return new String(fileContent, encoding);

        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Save text)
     */
    public boolean saveFile(String filePath, String content, boolean bool) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, bool), "UTF-8"));
            out.write(content);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Save text)
     */
    public boolean saveFileNoCover(String filePath, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
            out.write(content);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Filter text)
     */
    public String filterText(String content, String breakWordsPath) {
        String breakWordsPathStr = readAllLine(breakWordsPath, "UTF-8").trim();
        JSONArray arrBreakWords = JSONArray.parseArray(breakWordsPathStr);
        for (Object arrBreakWord : arrBreakWords) {
            content = content.replace((CharSequence) arrBreakWord, "");
        }
        return content;
    }

    /**
     * @param
     * @return
     * @Description: TODO(DOM4J XML writer)
     */
    public void writerXml(List<String> elementList, String xmlFilePath, String two_word) {

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("words");
        Element book = root.addElement(two_word);

        int i = 0;
        for (String fragment : elementList) {
            // 为book节点添加子节点
            Element name = book.addElement("fragment");
            // 设置节点的值
            name.addAttribute("id", String.valueOf(i++));
            name.setText(fragment);
        }

        // 创建输出格式的对象，规定输出的格式为带换行和缩进的格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            // 创建输出对象
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File(xmlFilePath)), format);
            // 设置输出，这里设置输出的内容不将特殊字符转义，例如<符号就输出<，如果不设置，系统默认会将特殊字符转义
            writer.setEscapeText(false);

            // 输出xml文件
            writer.write(document);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(DOM4J XML writer)
     */
    public void writerXml(List<String> elementList, String xmlFilePath, String two_word, int i) {

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("words");
        Element book = root.addElement(two_word);
        for (String fragment : elementList) {
            // 为book节点添加子节点
            Element name = book.addElement("fragment");
            // 设置节点的值
            name.addAttribute("id", String.valueOf(i++));
            name.setText(fragment);
        }

        // 创建输出格式的对象，规定输出的格式为带换行和缩进的格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            // 创建输出对象
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File(xmlFilePath)), format);
            // 设置输出，这里设置输出的内容不将特殊字符转义，例如<符号就输出<，如果不设置，系统默认会将特殊字符转义
            writer.setEscapeText(false);

            // 输出xml文件
            writer.write(document);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(DOM4J XML read)
     */
    public Set<String> readXML(String xmlFilePath, String nodeName) {
        Set<String> list = new HashSet<String>();
        File file = new File(xmlFilePath);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            Element data = rootElement.element(nodeName);
            Element element;
            String str;
            for (Iterator<Element> iterator = data.elementIterator("fragment"); iterator.hasNext(); ) {
                element = iterator.next();
                str = element.getText();
                if (str != null && !"".equals(str) && !list.contains(str)) {
                    list.add(str.trim());
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Does the string all in Chinese ?)
     */
    public boolean isAllChinese(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); i++) {
            n = (int) string.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param path txt file path
     * @return
     * @Description: TODO(Read txt)
     */
    public Set<String> readFilterTxt(String path) {
        Set<String> set = new HashSet<String>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 1)
                    if (!set.contains(line)) {
                        set.add(line);
                    }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Get file size)
     */
    public long getFileSzie(String filePath) {
        long fileSzie = 0;
        File file = new File(filePath);
        if (!file.isDirectory()) {
            System.out.println(file.isDirectory());
            System.out.println("Folder");
            System.out.println("Path = " + file.getPath());
            System.out.println("Name = " + file.getName());
            if (file.exists() && file.isFile()) {
                fileSzie = file.length();
                System.out.println("File " + file.getName() + " size is " + fileSzie);
            }
        } else if (file.isDirectory()) {
            System.out.println(file.isDirectory());
            String[] fileList = file.list();
            for (String s : fileList) {
                File f = new File(filePath, s);
                fileSzie += f.length();
            }
        }
        return fileSzie;
    }

    public static void getFileSize1(File file) {
        if (file.exists() && file.isFile()) {
            String fileName = file.getName();
            System.out.println("文件" + fileName + "的大小是：" + file.length());
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(Test main entrance)
     */
    public static void main(String[] args) {
        FileOperate fileOperate = new FileOperate();
        //String filePath = "C:\\\\Users\\\\11416\\\\PycharmProjects\\\\TextSummary\\\\news.txt";
        String filePath = "data" + File.separator + "金庸小说全集\\神雕侠侣.txt";

        String breakWordsPath = "data" + File.separator + "news.txt";

        //System.out.println(fileOperate.readOneLine(filePath));
        //System.out.println(fileOperate.readOneLine(breakWordsPath));
        //String s = fileOperate.readAllLine(breakWordsPath, "UTF-8");
        //System.out.println(fileOperate.filterText(s));
        //System.out.println(fileOperate.filterText(s).length());

        //String filePathtt = "data" + File.separator + "Neologism" + File.separator+ "FilterBreakWords.txt"; // Save current filter content
        //
        //System.out.println(fileOperate.saveFile(filePathtt,"测试！！！！！！"));

        //String text = "“中央派来了一个沙瑞金（省委书记），又派来了一个田国富（纪委书记）”，这是《人民的名义》里的一个情节，以此说明推动从严治党的迫切性。\n";
        //System.out.println(new FileOperate().filterText(text));

        // Handle dictionaries
        String s = fileOperate.readAllLine("data/Neologism/backup_dropword.dic", "UTF-8");
        String[] arr = s.split("\r\n");
        List<String> englishList = new ArrayList<String>();
        List<String> twoWordList = new ArrayList<String>();
        List<String> threeWordList = new ArrayList<String>();
        List<String> fourWordList = new ArrayList<String>();
        List<String> fiveWordList = new ArrayList<String>();

        for (int i = 0; i < arr.length; i++) {
            String sf = arr[i];
            if (fileOperate.isAllChinese(sf)) {
                if (arr[i].length() == 2) {
                    twoWordList.add(arr[i]);
                    fileOperate.writerXml(twoWordList, "data/Neologism/drop_word/two_word_fragments.xml", "two_word");
                } else if (arr[i].length() == 3) {
                    threeWordList.add(arr[i]);
                    fileOperate.writerXml(threeWordList, "data/Neologism/drop_word/three_word_fragments.xml", "three_word");
                } else if (arr[i].length() == 4) {
                    fourWordList.add(arr[i]);
                    fileOperate.writerXml(fourWordList, "data/Neologism/drop_word/four_word_fragments.xml", "four_word");
                } else if (arr[i].length() == 5) {
                    fiveWordList.add(arr[i]);
                    fileOperate.writerXml(fiveWordList, "data/Neologism/drop_word/five_word_fragments.xml", "five_word");
                }
            } else {
                englishList.add(arr[i]);
                fileOperate.writerXml(englishList, "data/Neologism/drop_word/english_fragments.xml", "english_word");
            }
        }
    }
}
