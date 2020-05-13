package graduatedesign.PMALOApriori;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by ua28 on 5/13/20.
 */
public class SparkDemo {

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setMaster("local[*]")
                .setAppName("SparkDemo");

        JavaSparkContext jsc = new JavaSparkContext(sparkConf);


    }


}
