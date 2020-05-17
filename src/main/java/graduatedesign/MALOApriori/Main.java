package graduatedesign.MALOApriori;

import graduatedesign.utils.Preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {

    public static List<List<String>> record = new ArrayList<List<String>>();
    public static List<List<String>> oneitemset = new ArrayList<>();

    public static HashMap<String, Double> rules = new HashMap<>();

    private static int MIN_SUPPORT_NUM = 1000;

    public static void main(String[] args) {

        record = new Preprocessing().parseAproriData1("DataSetA_8.csv");
        List<List<String>> cItemset = findFirstCandidate();// 获取第一次的备选集
        oneitemset = getSupportedItemset(cItemset);// 获取备选集cItemset满足支持的集合

        MALO alo = new MALO(20, 100, oneitemset.size(), 1, -1);

        long startTime = System.currentTimeMillis();
        alo.searchSolution();
        long endTime = System.currentTimeMillis();
        System.out.println("运行时间:" + (endTime - startTime) + "ms");

    }


    private static List<List<String>> findFirstCandidate() {
        List<List<String>> tableList = new ArrayList<List<String>>();
        List<String> lineList = new ArrayList<String>();

        int size = 0;
        for (int i = 1; i < record.size(); i++) {
            for (int j = 1; j < record.get(i).size(); j++) {
                if (lineList.isEmpty()) {
                    lineList.add(record.get(i).get(j));
                } else {
                    boolean haveThisItem = false;
                    size = lineList.size();
                    for (int k = 0; k < size; k++) {
                        if (lineList.get(k).equals(record.get(i).get(j))) {
                            haveThisItem = true;
                            break;
                        }
                    }
                    if (haveThisItem == false)
                        lineList.add(record.get(i).get(j));
                }
            }
        }
        for (int i = 0; i < lineList.size(); i++) {
            List<String> helpList = new ArrayList<String>();
            helpList.add(lineList.get(i));
            tableList.add(helpList);
        }
        return tableList;
    }

    private static List<List<String>> getSupportedItemset(
            List<List<String>> cItemset) {

        List<List<String>> supportedItemset = new ArrayList<List<String>>();
        for (int i = 0; i < cItemset.size(); i++) {
            int count = countFrequent(cItemset.get(i));//统计记录数

            if (count >= MIN_SUPPORT_NUM) {
                supportedItemset.add(cItemset.get(i));
            }
        }
        return supportedItemset;
    }

    private static int countFrequent(List<String> list) {
        int count = 0;
        for (int i = 1; i < record.size(); i++) {
            boolean notHavaThisList = false;
            for (int k = 0; k < list.size(); k++) {
                boolean thisRecordHave = false;
                for (int j = 1; j < record.get(i).size(); j++) {
                    if (list.get(k).equals(record.get(i).get(j)))
                        thisRecordHave = true;
                }
                if (!thisRecordHave) {// 扫描一遍记录表的一行，发现list.get(i)不在记录表的第j行中，即list不可能在j行中
                    notHavaThisList = true;
                    break;
                }
            }
            if (notHavaThisList == false)
                count++;
        }
        return count;
    }

}
