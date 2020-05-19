package graduatedesign.PMALOApriori;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ua28 on 5/18/20.
 */
public class SparkTester {

    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf()
                .setMaster("local[*]")
                .setAppName("SparkDemo");

        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        List<Rollette> RWS = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            int rolette_index = new Random().nextInt(30);
            if (rolette_index == -1)
                rolette_index = 0;
            RWS.add(new Rollette(rolette_index));
        }

        // convert rolette_indexes to RDD
        JavaRDD<Rollette> RDDRWS = jsc.parallelize(RWS);

        RDDRWS.count();
        // construct pair of rolette_indexes
        JavaPairRDD<String, Integer> IndexToPositionRolette = RDDRWS.mapToPair(new PairFunction<Rollette, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Rollette rollette) throws Exception {
                return new Tuple2<>(String.valueOf(rollette.getIndex()), rollette.getIndex());
            }
        });

        List<Tuple2<String, Integer>> collect = IndexToPositionRolette.collect();

        int i = 0;
    }
}
