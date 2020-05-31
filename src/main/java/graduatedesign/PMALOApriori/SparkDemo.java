package graduatedesign.PMALOApriori;

import graduatedesign.utils.Preprocessing;
import org.apache.spark.SparkConf;
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

import static graduatedesign.PMALOApriori.TopApriori.dCountMap;
import static graduatedesign.PMALOApriori.TopApriori.dkCountMap;
import static graduatedesign.PMALOApriori.TopApriori.endTag;


/**
 * Created by ua28 on 5/13/20.
 */
public class SparkDemo implements Serializable {
    private static final long serialVersionUID = 6186486072130711718L;


    public static List<List<String>> record = new ArrayList<List<String>>();
    public static List<List<String>> oneitemset = new ArrayList<>();

    public static HashMap<String, Double> rules = new HashMap<>();
    public static List<Rule> mRules = new ArrayList<>();
    public static HashMap<String, Rule> mHashRules = new HashMap<>();

    private static int MIN_SUPPORT_NUM = 1000;

    public static JavaSparkContext jsc;
    public static JavaRDD<String> lines;

    public static void main(String[] args) {

        String file = "DataSetA.csv";

        record = new Preprocessing().parseAproriData1(file);
        List<List<String>> cItemset = findFirstCandidate();// 获取第一次的备选集
        oneitemset = getSupportedItemset(cItemset);// 获取备选集cItemset满足支持的集合A

        List<List<String>> partOneItemSet = oneitemset.subList(0, 10);

        TopApriori topApriori = new TopApriori();
        long startTime = System.currentTimeMillis();
        topApriori.run(partOneItemSet);
        long endTime1 = System.currentTimeMillis();
        System.out.println("Apriori Algorithm cost time : " + (endTime1-startTime));

        SparkConf sparkConf = new SparkConf()
                .setMaster("local[*]")
                .setAppName("SparkDemo");

        jsc = new JavaSparkContext(sparkConf);
        lines = jsc.textFile(file);

        String baseUrl = SparkApriori.class.getClassLoader().getResource("").getPath();
//        String inputPath = baseUrl + "SparkSimple.txt";
//        String outputPath = baseUrl+"SparkAprioriResult";

        int num = 20;
        int dimension = 5;
        int iteration = 50;
        int ub = 1;
        int lb = -1;

        MALO malo = new MALO(num, iteration, oneitemset.size(), ub, lb);

        List<Double> doubles = malo.searchSolution();
        long endTime = System.currentTimeMillis();

        for (Double doub : doubles)
            System.out.println("results ---- " + doub);

        System.out.println("rules : " + rules.size());
        System.out.println("运行时间:" + (endTime - startTime) + "ms");
        System.out.println("Apriori Algorithm cost time : " + (endTime1-startTime));
        System.out.println("total rules : " + mHashRules.values().size());

//        Set<Map.Entry<String, Rule>> entries = mHashRules.entrySet();
//        List<Rule> ruleList = new ArrayList<>();
//        for (Map.Entry<String, Rule> keyvalue : entries) {
//            ruleList.add(keyvalue.getValue());
//        }
//
//        ruleList.sort(new Comparator<Rule>() {
//            @Override
//            public int compare(Rule o1, Rule o2) {
//                if(o1.getSupport() > o2.getSupport())
//                    return 1;
//                return -1;
//            }
//        });
//
//
//        System.out.println("min support : "  + ruleList.get(0).getRule() + " -- " + ruleList.get(0).getSupport());
//        System.out.println("min 1/4 support : "  + ruleList.get(ruleList.size()/4-1).getRule() + " -- " + ruleList.get(ruleList.size()/4-1).getSupport());
//        System.out.println("middle support : "  + ruleList.get(ruleList.size()/2-1).getRule() + " -- " + ruleList.get(ruleList.size()/2).getSupport());
//        System.out.println("max 1/4 support : "  + ruleList.get(ruleList.size()*3/4-1).getRule() + " -- " + ruleList.get(ruleList.size()*3/4-1).getSupport());
//        System.out.println("max support : "  + ruleList.get(ruleList.size()-1).getRule() + " -- " + ruleList.get(ruleList.size()-1).getSupport());
//
//        for (int i = 0; i < 20; i++)
//            System.out.println("max supports : "  + ruleList.get(ruleList.size()-1-i).getRule() + " -- " + ruleList.get(ruleList.size()-1-i).getSupport());

        for (int i = 0; i < mRules.size(); i++) {
            if (mRules.get(i) == null) {
                int a = 1;
            }
        }
    }

    private static void rulesAnalysisi() {
        Collections.sort(mRules, new Comparator<Rule>() {
            @Override
            public int compare(Rule o1, Rule o2) {
                if (o1.getSupport() > o2.getSupport())
                    return 1;
                else if (o1.getSupport() == o2.getSupport())
                    return 0;
                else
                    return -1;
            }
        });

        System.out.println("min support : "  + mRules.get(0).getRule() + " -- " + mRules.get(0).getSupport());
        System.out.println("min 1/4 support : "  + mRules.get(mRules.size()/4-1).getRule() + " -- " + mRules.get(mRules.size()/4-1).getSupport());
        System.out.println("middle support : "  + mRules.get(mRules.size()/2-1).getRule() + " -- " + mRules.get(mRules.size()/2).getSupport());
        System.out.println("max 1/4 support : "  + mRules.get(mRules.size()*3/4-1).getRule() + " -- " + mRules.get(mRules.size()*3/4-1).getSupport());
        System.out.println("max support : "  + mRules.get(mRules.size()-1).getRule() + " -- " + mRules.get(mRules.size()-1).getSupport());

        for (int i = 0; i < 20; i++)
            System.out.println("max supports : "  + mRules.get(mRules.size()-1-i).getRule() + " -- " + mRules.get(mRules.size()-1-i).getSupport());

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
        boolean end = true;
        List<Integer> counts = new ArrayList<>();
        List<List<String>> supportedItemset = new ArrayList<List<String>>();

        int k = 0;
        for (int i = 0; i < cItemset.size(); i++) {
            int count = countFrequent(cItemset.get(i));//统计记录数

            if (count >= MIN_SUPPORT_NUM) {
                if (cItemset.get(0).size() == 1)
                    dCountMap.put(k++, count);
                else
                    dkCountMap.put(k++, count);

                counts.add(count);
                supportedItemset.add(cItemset.get(i));
                end = false;
            }
        }
        endTag = end;
        for (int i = 0; i < counts.size(); i++) {
            for (int j = 0; j < counts.size()-1; j++) {
                if (counts.get(j) < counts.get(j+1)) {
                    int tmp = counts.get(j);
                    counts.set(j, counts.get(j+1));
                    counts.set(j+1, tmp);

                    List<String> tmplist = supportedItemset.get(j);
                    supportedItemset.set(j, supportedItemset.get(j+1));
                    supportedItemset.set(j+1, tmplist);
                }
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

    public static class MALO implements scala.Serializable {
        private static final long serialVersionUID = -1439845107436950623L;
        private int num;
        private int iteration;
        private double ub, lb;
        private int dimension;
        private List<Antlion> AntsRegistory = new ArrayList<>();
        private List<Antlion> mAntlions = new ArrayList<>();
        private List<Antlion> mAnts = new ArrayList<>();
        private Antlion EliteAntlion;

        private JavaRDD<Antlion> RDDmAntlions;
        private JavaRDD<Antlion> RDDmAnts;
        private JavaRDD<Antlion> RDDAntsRegistory;

        MALO(int num, int iteration, int dimension, double ub, double lb) {
            this.dimension = dimension;
            this.ub = ub;
            this.lb = lb;
            this.num = num;
            this.iteration = iteration;
            for (int i = 0; i < num; i++) {
                mAntlions.add(new Antlion(dimension, ub, lb, i));
                mAnts.add(new Antlion(dimension, ub, lb, i));
            }
            AntsRegistory.addAll(mAntlions);
            AntsRegistory.addAll(mAnts);
        }

        public List<Antlion> sortAntlions(List<Antlion> antlions) {
            Collections.sort(antlions, new Comparator<Antlion>() {
                @Override
                public int compare(Antlion o1, Antlion o2) {
                    if (o1.getFitness() > o2.getFitness()) {
                        return -1;
                    } else if (o1.getFitness() == o2.getFitness()) {
                        return  0;
                    } else {
                        return 1;
                    }
                }
            });
            return antlions;
        }

        /**
         *  1) Modified Random Walking Strategy(walk and sort)
         *  2) Repeated Random Walking and Sort Choosing Elite Strategy
         *  3) Repair Library + Random Walking
         *
         * @return
         */
        public List<Double> searchSolution() {

        RDDmAntlions = jsc.parallelize(mAntlions);
        RDDmAnts = jsc.parallelize(mAnts);
        RDDAntsRegistory = jsc.parallelize(AntsRegistory);


            List<Double> ConvergenceData = new ArrayList<>();
//        mAntlions=sortAntlions(mAntlions);
            EliteAntlion = mAntlions.get(0);

            for (int current_iter = 0; current_iter < this.iteration-1; current_iter++) {

                /**
                 * Part 1 : rollete_indexes4.1.1 并行计算频繁项集
                 */
                // make rolette indexes
                // TODO if fitness == 0, this method will be seriously affected
                List<Rollette> RWS = new ArrayList<>();
                for (int i = 0; i < this.num; i++) {
//                int rolette_index = RouletteWheelSelection();
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

                /**
                 * Part 2 : Pair of RDDmAntlions
                 */
                JavaPairRDD<String, Antlion> IndexTomAntlions = RDDmAntlions.mapToPair(new PairFunction<Antlion, String, Antlion>() {
                    @Override
                    public Tuple2<String, Antlion> call(Antlion antlion) throws Exception {
                        return new Tuple2<>(String.valueOf(antlion.getIndex()), antlion);
                    }
                });

                JavaPairRDD<String, Antlion> IndexTomAnts = RDDmAnts.mapToPair(new PairFunction<Antlion, String, Antlion>() {
                    @Override
                    public Tuple2<String, Antlion> call(Antlion antlion) throws Exception {
                        return new Tuple2<>(String.valueOf(antlion.getIndex()), antlion);
                    }
                });

                JavaPairRDD<String, Tuple2<Integer, Antlion>> Index_indexAntlion = IndexToPositionRolette.join(IndexTomAntlions);
                JavaPairRDD<String, Tuple2<Tuple2<Integer, Antlion>, Antlion>> IndexToPositionRolettemAnts = Index_indexAntlion.join(IndexTomAnts);

                int finalCurrent_iter = current_iter;
                JavaRDD<Tuple2<String, Antlion>> index_newmAnts = IndexToPositionRolettemAnts.map(new Function<Tuple2<String, Tuple2<Tuple2<Integer, Antlion>, Antlion>>, Tuple2<String, Antlion>>() {
                    @Override
                    public Tuple2<String, Antlion> call(Tuple2<String, Tuple2<Tuple2<Integer, Antlion>, Antlion>> index_indexAntlion_mAnt) throws Exception {
                        Antlion RA = Random_wald_around_antlion(index_indexAntlion_mAnt._2()._1()._2(), finalCurrent_iter);

                        Antlion RE = Random_wald_around_antlion(EliteAntlion, finalCurrent_iter);

                        Antlion RM = Random_wald_around_antlion(index_indexAntlion_mAnt._2()._2(), finalCurrent_iter);

                        if (RA.getFitness() > EliteAntlion.getFitness()) {
                            EliteAntlion = RA;
                            addtoRegistory(EliteAntlion);
                        }
                        if (RE.getFitness() > EliteAntlion.getFitness()) {
                            EliteAntlion = RE;
                            addtoRegistory(EliteAntlion);
                        }
                        if (RM.getFitness() > EliteAntlion.getFitness()) {
                            EliteAntlion = RM;
                            addtoRegistory(EliteAntlion);
                        }

                        Antlion RF = getRF(RA, RE, RM);
                        RF.updateFitness();

                        Antlion RZZ = Random_wald_around_antlion(RF, finalCurrent_iter);

                        // TODO Sort of Random_wald_around_antlion

                        // 从三者中选取最佳作为新蚂蚁
                        List<Antlion> RZ = new ArrayList<>();
                        RZ.add(RF);
                        RZ.add(RZZ);
                        RZ.add(RA);
                        RZ.add(RE);
                        RZ.add(RM);
                        RZ = sortAntlions(RZ);
                        return new Tuple2<String, Antlion>(index_indexAntlion_mAnt._1(), RZ.get(0));
                    }
                });


                IndexToPositionRolettemAnts.count();

                JavaRDD<Tuple2<String, Antlion>> index_newmAnts_filtered = index_newmAnts.map(new Function<Tuple2<String, Antlion>, Tuple2<String, Antlion>>() {
                    @Override
                    public Tuple2<String, Antlion> call(Tuple2<String, Antlion> index_mAnt) throws Exception {
                        for (int i = 0; i < index_mAnt._2().getPosition().size(); i++) {
                            if (index_mAnt._2().getPosition().get(i) > ub)
                                index_mAnt._2().getPosition().set(i, ub);
                            else if (index_mAnt._2().getPosition().get(i) < lb)
                                index_mAnt._2().getPosition().set(i, lb);
                        }
                        index_mAnt._2().updateFitness();

                        if (index_mAnt._2().getFitness() < 0.1) {
                            Antlion antlion = getNewAntlion();
                            antlion.updateFitness();
                            return new Tuple2<>(index_mAnt._1(), antlion);
                        }

                        return new Tuple2<>(index_mAnt._1(), index_mAnt._2());
                    }
                });

                JavaRDD<Antlion> filteredAnt = index_newmAnts_filtered.map(new Function<Tuple2<String, Antlion>, Antlion>() {
                    @Override
                    public Antlion call(Tuple2<String, Antlion> index_newmAnt_filtered) throws Exception {
                        return index_newmAnt_filtered._2();
                    }
                });

                JavaRDD<Antlion> unionedmAntlions_mAnts = RDDmAntlions.union(filteredAnt);

                JavaPairRDD<Double, Antlion> fitness_antlions = unionedmAntlions_mAnts.mapToPair(new PairFunction<Antlion, Double, Antlion>() {
                    @Override
                    public Tuple2<Double, Antlion> call(Antlion antlion) throws Exception {
                        return new Tuple2<>(antlion.getFitness(), antlion);
                    }
                });

                JavaPairRDD<Double, Antlion> sortedFitness_Antlions = fitness_antlions.sortByKey();
                JavaRDD<Antlion> sorted_Antlions = sortedFitness_Antlions.map(new Function<Tuple2<Double, Antlion>, Antlion>() {
                    @Override
                    public Antlion call(Tuple2<Double, Antlion> doubleAntlionTuple2) throws Exception {
                        return doubleAntlionTuple2._2();
                    }
                });
                List<Antlion> nextAntlions = sorted_Antlions.take(this.num);

                RDDmAntlions = jsc.parallelize(nextAntlions);

                if (nextAntlions.get(0).getFitness() > EliteAntlion.getFitness()) {
                    EliteAntlion = nextAntlions.get(0);
                    addtoRegistory(EliteAntlion);
                }


                ConvergenceData.add(EliteAntlion.getFitness());
                System.out.println(rules.size()+ "   " + current_iter + " : " + EliteAntlion.getFitness() + " : " + EliteAntlion.toString());
//                System.out.println("EliteAntlion : " + current_iter + " : " + EliteAntlion.getFitness() + " : " + EliteAntlion.toString());
//                System.out.println(rules.size()+ "   " + current_iter + " : " + EliteAntlion.getFitness() + " : " + EliteAntlion.toString());
            }
            System.out.println(ConvergenceData.toString());
            return ConvergenceData;
        }

        private void ReplacemAntsI(Antlion antlion, Antlion antlion1) {
            for (int i = 0; i < antlion.getDimension(); i++) {
                antlion.setPosition(i, antlion1.getPosition().get(i));
                antlion.setFitness(antlion1.getFitness());
            }
        }

        /**
         * 注意，库里保存的是对象的引用，直接返回的话原对象会被修改
         * @return
         */
        private Antlion getNewAntlion_1() {
            Antlion antlion1 = new Antlion(AntsRegistory.get(new Random().nextInt(AntsRegistory.size()-1)));

            Antlion antlion2 = AntsRegistory.get(new Random().nextInt(AntsRegistory.size()-1));
//        antlion1.setUpdatePosition();
            if (antlion1.getPosition().size() > 0) {
                int point = new Random().nextInt(antlion1.getPosition().size());
                for (int i = 0; i < point; i++) {
                    antlion1.setPosition(i, antlion2.getPosition().get(i));
                }
            }

            return antlion1;
        }

        private Antlion getNewAntlion() {
            Antlion antlion = AntsRegistory.get(new Random().nextInt(AntsRegistory.size()-1));
//        List<Antlion> nantlions = Random_wald_around_antlion(antlion, 1);
//        nantlions = sortAntlions(nantlions);
//        return nantlions.get(0);
            return antlion;
        }

        private void addtoRegistory(Antlion eliteAntlion) {
            AntsRegistory = sortAntlions(AntsRegistory);
//        AntsRegistory.add(eliteAntlion);
//        AntsRegistory.add(eliteAntlion);
            AntsRegistory.set(AntsRegistory.size()-1, eliteAntlion);
        }

        // ❤各蚁狮的比例有待调整
        private Antlion getRF(Antlion antlion, Antlion antlion1, Antlion antlion2) {
            Antlion mAntlion = getNewAntlion_1();
            for (int i = 0; i < antlion.getPosition().size(); i++) {
                double temp1 = new Random().nextDouble();
                double temp2 = new Random().nextDouble();
                mAntlion.getPosition().set(i, (temp1*antlion.getPosition().get(i)+antlion1.getPosition().get(i)+temp2*antlion2.getPosition().get(i))/(1+temp1+temp2));
//            mAntlion.getPosition().set(i, (antlion.getPosition().get(i)+antlion1.getPosition().get(i)+antlion2.getPosition().get(i))/3);
            }
            return mAntlion;
        }

        // 随机游走

        /**
         * 1) implemented set & update operator for Random Walking
         *
         * @param antlion
         * @param current_iter
         * @return
         */
        private Antlion Random_wald_around_antlion(Antlion antlion, int current_iter) {
            List<Double> ub = new ArrayList<>();
            List<Double> lb = new ArrayList<>();
            for (int i = 0; i < antlion.getPosition().size(); i++) {
                ub.add(this.ub);
                lb.add(this.lb);
            }
            double I = new Random().nextDouble();
            if (current_iter > this.iteration/10) {
                I = 1 + 100*(current_iter/this.iteration);
            } else if (current_iter > this.iteration/2) {
                I = 1 + 1000*(current_iter/this.iteration);
            } else if (current_iter > this.iteration*0.75) {
                I = 1 + 10000*(current_iter/this.iteration);
            } else if (current_iter > this.iteration*0.9) {
                I = 1 + 100000*(current_iter/this.iteration);
            } else if (current_iter > this.iteration*0.95) {
                I = 1 + 1000000*(current_iter/this.iteration);
            }

            for (int i = 0; i < antlion.getPosition().size(); i++) {
                lb.set(i, lb.get(i)/I);
                ub.set(i, ub.get(i)/I);
            }

            // 将边界随机调整为当前位置的上下界，限制随机游走后新的范围
            if (new Random().nextDouble() < 0.5) {
                for (int i = 0; i < antlion.getPosition().size(); i++) {
                    lb.set(i, lb.get(i)+antlion.getPosition().get(i));
                }
            } else {
                for (int i = 0; i < antlion.getPosition().size(); i++) {
                    lb.set(i, -lb.get(i)+antlion.getPosition().get(i));
                }
            }
            if (new Random().nextDouble() >= 0.5) {
                for (int i = 0; i < antlion.getPosition().size(); i++) {
                    ub.set(i, ub.get(i)+antlion.getPosition().get(i));
                }
            } else {
                for (int i = 0; i < antlion.getPosition().size(); i++) {
                    ub.set(i, -ub.get(i)+antlion.getPosition().get(i));
                }
            }

            // 按维度游走的操作
            List<Antlion> RWs = new ArrayList<>();
            for (int i = 0; i < this.iteration; i++) {
                RWs.add(getNewAntlion_1());
            }
            for (int j = 0; j < antlion.getPosition().size(); j++) {
                List<Integer> X = new ArrayList<>();
                int temp = 0;
                for (int i = 0; i < this.iteration; i++) {
                    temp += (2*(new Random().nextInt(2))-1);
                    X.add(temp);
                }
                int a = Collections.min(X);
                int b = Collections.max(X);
                double c = lb.get(j);
                double d = ub.get(j);
                for (int i = 0; i < this.iteration; i++) {
                    RWs.get(i).getPosition().set(j, ((X.get(i)-a)*(d-c))/(b-a)+c);
                }
            }

            // 更新所有游走解的适应度值
//        for (int i = 0; i < this.iteration; i++) {
//            RWs.get(i).updateFitness();
//        }
            return RWs.get(current_iter);
        }

        private int RouletteWheelSelection() {
            List<Double> accumulation = new ArrayList<>();
            double temp = 0;
            for (int i = 0; i < this.num; i++) {
                temp += Math.abs(1/mAntlions.get(i).getFitness());
                accumulation.add(temp);
            }

            double randp = new Random().nextDouble()*accumulation.get(accumulation.size()-1);

            for (int i = 0; i < this.num; i++) {
                if (accumulation.get(i) > randp) {
                    return i;
                }
            }

            // ?? 什么情况下会运行到这里？？
            return -1;
        }
    }


    public static class Antlion implements Serializable {

        private static final long serialVersionUID = 361783270431667235L;
        private int index;
        private List<Double> position = new ArrayList<>();
        private int dimension;
        private double fitness;

        Antlion(int dimension, double ub, double lb, int index) {
            this.index = index;
            this.dimension = dimension;
            for (int i = 0; i < dimension; i++) {
                position.add(new Random().nextDouble()*(ub-lb)+lb);
            }
            updateFitness();
        }

        public Antlion(Antlion antlion) {
            this.dimension = antlion.getDimension();
            for (double p : antlion.getPosition())
                this.position.add(p);
            this.fitness = antlion.getFitness();
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getDimension() {
            return dimension;
        }

        public List<Double> getPosition() {
            return position;
        }

        public double getFitness() {
            return fitness;
        }

        public void updateFitness() {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < position.size(); i++) {
                if (position.get(i) > 0) {
                    list.add(oneitemset.get(i).get(0));
                }
            }

            String itemKey = String.join(" ", list);
            if (mHashRules.containsKey(itemKey)) {
                fitness = Double.valueOf(mHashRules.get(itemKey).getSupport());
                return;
            }



//        Double fit = (Double) bplusTree.get(list.toString());
//        if (fit != null) {
//            fitness = fit;
//            return;
//        }

//            long num = countFrequentSpark(list);
            int num = countFrequent(list);

            if (list.size() == 0 || num < 1000)
                fitness = Double.MIN_NORMAL;
            else {
                double support = num * 1.0 / record.size();
//            double confidence = ?;
                fitness = support;
                mHashRules.put(itemKey, new Rule(itemKey, support));

//            if (!rules.containsKey(String.join(",", list))) {
//                rules.put(String.join(",", list), fitness);
//                mRules.add(new Rule(String.join(",", list), fitness));
//            }
            }
        }


        public void setPosition(int i, Double value) {
            position.set(i, value);
        }

        private long countFrequentSpark(List<String> items) {

            JavaRDD<String> filtered = lines.filter(new Function<String, Boolean>() {
                @Override
                public Boolean call(String s) throws Exception {
                    String[] splited = s.split(",");
                    int flag = 0;
                    for (String item : items) {
                        for (String tem : splited) {
                            if (tem.equals(item)) {
                                flag++;
                                break;
                            }
                        }
                    }
                    if (flag == items.size())
                        return true;
                    return false;
                }
            });

            return filtered.count();
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

        @Override
        public String toString() {
            String result = "";

            for (int i = 0; i < position.size(); i++) {
                if (position.get(i) > 0) {
                    result += oneitemset.get(i).get(0)+" ";
                }
            }

            return result;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }
    }

}
