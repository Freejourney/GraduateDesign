package graduatedesign.PMALOApriori;


import graduatedesign.utils.Preprocessing;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

public class SparkApriori implements Serializable {
    private String outputPath;
    //	输出路径
    private long minSuport;
    //    最小置信度*总事务数，默认置信度85%
    private double confidence = 0.1;
    //    置信度
    private long numOfItems;
    //    总事务数
    private int K=8;
    //    从1频繁项集挖掘到K频繁项集为止
    JavaRDD<String> transactions;
    //    第一次加载的原始事务集的RDD，统计总事务数
    List<HashSet<String>> lastList;
    //    上一轮得到的频繁项集
    List<HashSet<String>> K_ciList;
    //    K频繁项集
    String formatString;
    //    规范输出格式
    double formatDouble;
    //    规范输出格式
    String formatDoubleToString;
    //    规范输出格式
    List<String> re = new ArrayList();
    //	跳过第一个字符

    private List<List<String>> finalResult = new ArrayList<>();


    public SparkApriori(){

    }

    //    初始化全局参数，并读入原始事务集
    public SparkApriori( JavaRDD<String> transactions,String outputPath){
        this.outputPath = outputPath;
        this.transactions = transactions.cache();
        this.numOfItems = this.transactions.count();
        this.minSuport = (long) Math.ceil(numOfItems * confidence);
    }

    //    挖掘1-8维频繁项
    public void start() {
        for(int i=1;i<=this.K;i++){
            if(i ==1)
                run();
            else{
                run(lastList,i);
            }
        }
    }

    /**
     * @author zby
     * @des 挖掘一维频繁项集
     */
    public void run() {

        System.out.println("TestPoint: 1 -------------------------- ");
        JavaRDD<String> Items = transactions.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String line) throws Exception {
                StringTokenizer st = new StringTokenizer(line, "\t");
                st.nextToken();
                re.clear();
                while (st.hasMoreTokens()) {
                    re.add(st.nextToken());
                }
                return re.iterator();
            }
        });

        List<String> collect = Items.collect();

        JavaPairRDD<String, Integer> ItemsPair = Items.mapToPair(new PairFunction<String, String, Integer>() {
            public Tuple2<String, Integer> call(String s) {
                return new Tuple2<String, Integer>(s, 1);
            }
        });

        List<Tuple2<String, Integer>> collect1 = ItemsPair.collect();

        JavaPairRDD<String, Integer> reduceItemsPair = ItemsPair.reduceByKey(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer i1, Integer i2) {
                return i1 + i2;
            }
        });

        List<Tuple2<String, Integer>> collect2 = reduceItemsPair.collect();

        JavaPairRDD<String, Integer> resultItemsPair = reduceItemsPair.filter(new Function<Tuple2<String,Integer>, Boolean>() {
            public Boolean call(Tuple2<String, Integer> result) throws Exception {
                if(result._2>=minSuport)
                    return true;
                else
                    return false;
            }
        });

        List<Tuple2<String, Integer>> collect3 = resultItemsPair.collect();

        /**
         * @des 规范输出格式为 a,b:0.85
         */
        JavaRDD<String> save = resultItemsPair.flatMap(new FlatMapFunction<Tuple2<String,Integer>,String>(){
            public Iterator<String> call(Tuple2<String,Integer> line) {
                formatString = line._1;
                formatDouble = line._2.doubleValue()/numOfItems;
                formatDoubleToString = String.format("%.2f",formatDouble);
                return Arrays.asList((formatString+":"+formatDoubleToString)).iterator();
            }
        });

        List<String> collect4 = save.collect();
        finalResult.add(collect4);

//        save.saveAsTextFile(outputPath+"result-1");

        lastList  = resultItemsPair.map(new Function<Tuple2<String,Integer>, HashSet<String>>() {
            public HashSet call(Tuple2<String,Integer> t) {
                HashSet s = new HashSet();
                s.add(t._1);
                return s;
            }
        }).collect();
    }

    /**
     * @author zby
     * @des 挖掘多维频繁项集
     */
    public void run(List<HashSet<String>> ciList, int i) {
        K_ciList = apriori_gen(ciList);

        System.out.println("TestPoint: 2 -------------------------- ");
        JavaRDD<String> KItems = transactions.flatMap(new FlatMapFunction<String,String>(){
            @Override
            public Iterator<String> call(String line) throws Exception {
                return getItems(K_ciList, line).iterator();
            }
        });

        List<String> collect = KItems.collect();

        JavaPairRDD<String, Integer> KItemsPair = KItems.mapToPair(new PairFunction<String, String, Integer>() {
            public Tuple2<String, Integer> call(String s) {
                return new Tuple2<String, Integer>(s, 1);
            }
        });

        List<Tuple2<String, Integer>> collect1 = KItemsPair.collect();

        JavaPairRDD<String, Integer> KReduceItemsPair = KItemsPair.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        });

        List<Tuple2<String, Integer>> collect2 = KReduceItemsPair.collect();

        JavaPairRDD<String, Integer> KResultItemsPair = KReduceItemsPair.filter(new Function<Tuple2<String,Integer>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, Integer> result) throws Exception {
                if(result._2>=minSuport)
                    return true;
                else
                    return false;
            }
        });

        List<Tuple2<String, Integer>> collect3 = KResultItemsPair.collect();

        JavaRDD<String> save = KResultItemsPair.flatMap(new FlatMapFunction<Tuple2<String,Integer>,String>(){
            public Iterator<String> call(Tuple2<String,Integer> line) {
                formatString = line._1.trim().substring(1, line._1.length()-1).trim();
                formatDouble = line._2.doubleValue()/numOfItems;
                formatDoubleToString = String.format("%.2f",formatDouble);
                return Arrays.asList((formatString+":"+formatDoubleToString)).iterator();
            }
        });

        List<String> collect4 = save.collect();
        finalResult.add(collect4);
//        save.saveAsTextFile(outputPath+"result-"+i);

        /**
         * @des 将字符串形式的集合转化为HashSet形式
         */
        lastList  = KResultItemsPair.map(new Function<Tuple2<String,Integer>, HashSet<String>>() {
            public HashSet call(Tuple2<String,Integer> t) {
                HashSet hs = new HashSet();
                String tmp = t._1.trim().substring(1, t._1.length()-1).trim();
//						[(a, b), (c, d)] -> (a,b) (c,d)
                hs.addAll(Arrays.asList(tmp.split(", ")));
                return hs;
            }
        }).collect();
    }

    /**
     * @author zby
     * @des 由K-1维频繁项集连接生成K维频繁项集合，但没有剪枝
     * @param candidateItem
     * @return
     */
    public static List<HashSet<String>> apriori_gen(List<HashSet<String>> candidateItem) {
        HashSet<HashSet<String>> K_ciSet = new HashSet();
        int size = candidateItem.size();
        HashSet hs1 = new HashSet();
        HashSet hs2 = new HashSet();
        for(int i=0;i<size;i++){
            hs1 = candidateItem.get(i);
            int hsSize = hs1.size();
            for(int j=i;j<size;j++){
                hs2 = (HashSet) candidateItem.get(j).clone();
                hs2.removeAll(hs1);
                hs2.addAll(hs1);
                if((hs2.size()-hsSize)==1){
                    K_ciSet.add(hs2);
                }
            }
        }
        List<HashSet<String>> K_ciList = new ArrayList(K_ciSet);
        return K_ciList;
    }

    /**
     * @author zby
     * @des 由一个事务集中挖掘频繁项
     * @param K_ciList
     * @param line
     * @return
     */
    public List<String> getItems(List<HashSet<String>> K_ciList,String line){
        StringTokenizer st=new StringTokenizer(line,"\t");
        st.nextToken();
        re.clear();
        while(st.hasMoreTokens()){
            re.add(st.nextToken());
        }
        List<String> newLine = new ArrayList();
        for(HashSet<String> hs:K_ciList){
            if(re.containsAll(hs)){
                String hsToString = hs.toString();
                newLine.add(hsToString);
            }
        }
        return newLine;
    }

    /**
     * @author zby
     * @param args
     */
    public static void main(String[] args) {
//		args[0]:hdfs://localhost:9000/spark/apriori_data_mini1.dat
//		args[1]:hdfs://localhost:9000/spark/
        SparkConf sparkConf = new SparkConf().setAppName("SparkApriori").setMaster("local[*]");

//        JavaSparkContext context = new JavaSparkContext( "local", "Apriori",  System.getenv("SPARK_HOME"), System.getenv("SPARK_EXAMPLES_JAR"));
        JavaSparkContext context = new JavaSparkContext(sparkConf);

        String baseUrl = SparkApriori.class.getClassLoader().getResource("").getPath();
        String inputPath = baseUrl + "SparkSimple.txt";
        String outputPath = baseUrl+"SparkAprioriResult";

        String inputPath1 = baseUrl + "sp.txt";

        JavaRDD<String> distFile= context.textFile(inputPath1);
        SparkApriori apriori = new SparkApriori(distFile, outputPath);
        long starttime = System.currentTimeMillis();
        apriori.start();
        long endtime = System.currentTimeMillis();

        System.out.println("time consuming : "+(endtime-starttime));

        for (int i = 0; i < apriori.finalResult.size(); i++) {
            for (int j = 0; j < apriori.finalResult.get(i).size(); j++) {
                System.out.println(apriori.finalResult.get(i).get(j)+"----");
            }
        }
    }
}
