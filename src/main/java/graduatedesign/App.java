package graduatedesign;

import java.util.*;

/**
 * @author DongDawei
 *
 *  配置解析及存储，给定一个字符串，里面为K,V的配置信息，请构造程序将其解析。
 *  "abc =b\\n;c=\\x61d;d=234;t=\\n;d=\"test;yes\";"
 */

public class App 
{

    private static final String config = "abc =b\\n;c=\\x61d;d=234;t=\\n;d=\"test;yes\";";

    /**
     * 该方法在以下两个条件下有效：
     *  1）key中不会出现冒号、引号或等于号
     *  2）value中=或者；永远出现在成对的引号里，且引号不会出现单个的情况。
     *
     * 如果说value中引号可以有单个出现的情况的话，则value中不可以有等号，否则无法判断出该键值对的结尾。
     * 这种情况下需要找到下一个key的等号来回退到最近的一个分号。（见userDefineSplit2）
     *
     * @param config
     * @return
     */
    public Map<String, String> parseConfig(String config) {
        if (config == null || config.length() == 0)
            return null;

        Map<String, String> kvResult = new HashMap<>();
        String[] kvPairs = userDefinedSplit(config);
        for (String kv : kvPairs) {
            // key中不会出现等号，所以直接用遇到的第一个等号分界
            String[] k_v = new String[]{kv.substring(0,kv.indexOf('=')), kv.substring(kv.indexOf('=')+1)};

            // 不存在则添加，存在则用 | 进行合并
            if (kvResult.containsKey(k_v[0])) {
                kvResult.put(k_v[0], kvResult.get(k_v[0])+"|"+k_v[1]);
            } else {
                kvResult.put(k_v[0], k_v[1]);
            }
        }

        return kvResult;
    }

    /**
     * 自定义split函数，分解出每个键值对
     * @param str
     * @return
     */
    public String[] userDefinedSplit(String str) {
        if (str == null || str.length() == 0)
            return null;

        List<String> result = new ArrayList<>();

        int begin = 0;
        int tag = 0;
        for (int i = 0; i < str.length(); i++) {
            // 第一次遇到引号
            if (str.charAt(i) == '"' && tag == 0) {
                tag++;
                continue;
            }

            // 第二次遇到引号
            if (str.charAt(i) == '"' && tag == 1) {
                tag--;
                continue;
            }

            // 只有在成对的引号以外遇到 ; 才算有效，双引号内的=或者;都跳过处理
            if (str.charAt(i) == ';' && tag == 0) {
                result.add(str.substring(begin, i));
                begin = i+1;
                tag = 0;
            }
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * value中引号可以有单个出现的情况, value中不能有等号，找到下一个key的等号来回退到最近的一个分号
     * @param str
     * @return
     */
    public String[] userDefinedSplit2(String str) {
        if (str == null || str.length() == 0)
            return null;

        List<String> result = new ArrayList<>();

        int begin = 0;
        int tag = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '=') {
                // tag用来标记等号，遇到一个加一, 遇到第二个等号表示已经到了第二个键值对，开始回退
                tag++;
                if (tag == 2) {
                    while (str.charAt(i) != ';') {
                        i--;
                    }
                    result.add(str.substring(begin, i));
                    begin = i + 1;
                    tag = 0;
                }
                // 最后一个键值对直接取子串
            } else if (i == str.length()-1) {
                result.add(str.substring(begin, i));
            }
        }

        return result.toArray(new String[result.size()]);
    }


    /**
     * 遍历打印
     * @param config
     */
    public void displayKVs(Map<String, String> config) {
        Set<Map.Entry<String, String>> kvs = config.entrySet();
        for (Map.Entry<String, String> kv : kvs) {
            System.out.println(kv.getKey()+"---"+kv.getValue());
        }
    }


    /**
     * test
     */
    public void test() {
        displayKVs(parseConfig(config));
    }

    public static void main( String[] args )
    {
        new App().test();
    }
}
