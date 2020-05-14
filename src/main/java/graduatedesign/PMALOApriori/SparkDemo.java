package graduatedesign.PMALOApriori;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ua28 on 5/13/20.
 */
public class SparkDemo {

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setMaster("local[*]")
                .setAppName("SparkDemo");

        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        String baseUrl = SparkApriori.class.getClassLoader().getResource("").getPath();
        String inputPath = baseUrl + "SparkSimple.txt";
        String outputPath = baseUrl+"SparkAprioriResult";

        JavaRDD<String> data = jsc.textFile(inputPath);
        List<String> results = new ArrayList<>();

        List<String> collect = data.collect();


        JavaRDD<String> result = data.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                return Arrays.asList(s.split("\t")).iterator();
            }
        });

        List<String> collect1 = result.collect();

        JavaRDD<String> stringJavaRDD = data.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                String[] split = s.split("\t");
                for (String sr : split)
                    results.add(sr);
                return results.iterator();
            }
        });

        data.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                results.add(s);
                return null;
            }
        });

        results.size();
    }


}
