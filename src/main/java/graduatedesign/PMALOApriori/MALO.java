package graduatedesign.PMALOApriori;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Serializable;
import scala.Tuple2;

import java.util.*;

import static graduatedesign.PMALOApriori.SparkDemo.rules;

public class MALO implements Serializable {
    private static final long serialVersionUID = -1439845107436950623L;
    private int num;
    private int iteration;
    private double ub, lb;
    private int dimension;
    private List<Antlion> AntsRegistory = new ArrayList<>();
    private List<Antlion> mAntlions = new ArrayList<>();
    private List<Antlion> mAnts = new ArrayList<>();
    private Antlion EliteAntlion;


    private JavaSparkContext jsc;

    private JavaRDD<Antlion> RDDmAntlions;
    private JavaRDD<Antlion> RDDmAnts;
    private JavaRDD<Antlion> RDDAntsRegistory;

    MALO(int num, int iteration, int dimension, double ub, double lb, JavaSparkContext jsc) {
        this.jsc = jsc;
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

    public void test() {
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

//            RDDRWS.count();
        // construct pair of rolette_indexes
        JavaPairRDD<String, Integer> IndexToPositionRolette = RDDRWS.mapToPair(new PairFunction<Rollette, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Rollette rollette) throws Exception {
                return new Tuple2<>(String.valueOf(rollette.getIndex()), rollette.getIndex());
            }
        });

        List<Tuple2<String, Integer>> collect = IndexToPositionRolette.collect();
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

//        RDDmAntlions = jsc.parallelize(mAntlions);
//        RDDmAnts = jsc.parallelize(mAnts);
//        RDDAntsRegistory = jsc.parallelize(AntsRegistory);


        List<Double> ConvergenceData = new ArrayList<>();
//        mAntlions=sortAntlions(mAntlions);
        EliteAntlion = mAntlions.get(0);

        for (int current_iter = 0; current_iter < this.iteration-1; current_iter++) {

            /**
             * Part 1 : rollete_indexes
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

//            RDDRWS.count();
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
//            JavaPairRDD<String, Antlion> IndexTomAntlions = RDDmAntlions.mapToPair(new PairFunction<Antlion, String, Antlion>() {
//                @Override
//                public Tuple2<String, Antlion> call(Antlion antlion) throws Exception {
//                    return new Tuple2<>(String.valueOf(antlion.getIndex()), antlion);
//                }
//            });
//
//            JavaPairRDD<String, Antlion> IndexTomAnts = RDDmAnts.mapToPair(new PairFunction<Antlion, String, Antlion>() {
//                @Override
//                public Tuple2<String, Antlion> call(Antlion antlion) throws Exception {
//                    return new Tuple2<>(String.valueOf(antlion.getIndex()), antlion);
//                }
//            });
//
//            JavaPairRDD<String, Tuple2<Integer, Antlion>> Index_indexAntlion = IndexToPositionRolette.join(IndexTomAntlions);
//            JavaPairRDD<String, Tuple2<Tuple2<Integer, Antlion>, Antlion>> IndexToPositionRolettemAnts = Index_indexAntlion.join(IndexTomAnts);
//
//            int finalCurrent_iter = current_iter;
//            IndexToPositionRolettemAnts.map(new Function<Tuple2<String,Tuple2<Tuple2<Integer,Antlion>,Antlion>>, Antlion>() {
//                @Override
//                public Antlion call(Tuple2<String, Tuple2<Tuple2<Integer, Antlion>, Antlion>> index_indexAntlion_mAnt) throws Exception {
//                    Antlion RA = Random_wald_around_antlion(index_indexAntlion_mAnt._2()._1()._2(), finalCurrent_iter);
//
//                    Antlion RE = Random_wald_around_antlion(EliteAntlion, finalCurrent_iter);
//
//                    Antlion RM = Random_wald_around_antlion(index_indexAntlion_mAnt._2()._2(), finalCurrent_iter);
//
//                    if (RA.getFitness() > EliteAntlion.getFitness()) {
//                        EliteAntlion = RA;
//                        addtoRegistory(EliteAntlion);
//                    }
//                    if (RE.getFitness() > EliteAntlion.getFitness()) {
//                        EliteAntlion = RE;
//                        addtoRegistory(EliteAntlion);
//                    }
//                    if (RM.getFitness() > EliteAntlion.getFitness()) {
//                        EliteAntlion = RM;
//                        addtoRegistory(EliteAntlion);
//                    }
//
//                    Antlion RF = getRF(RA, RE, RM);
//                    RF.updateFitness();
//
//                    Antlion RZZ = Random_wald_around_antlion(RF, finalCurrent_iter);
//
//                    // TODO Sort of Random_wald_around_antlion
//
//                    // 从三者中选取最佳作为新蚂蚁
//                    List<Antlion> RZ = new ArrayList<>();
//                    RZ.add(RF);
//                    RZ.add(RZZ);
//                    RZ.add(RA);
//                    RZ.add(RE);
//                    RZ.add(RM);
//                    RZ = sortAntlions(RZ);
//                    ReplacemAntsI(index_indexAntlion_mAnt._2()._2(), RZ.get(0));
//                    return null;
//                }
//            });





            // 新的范围始终得在基本边界范围内
            for (int i = 0; i < this.num; i++) {
                for (int j = 0; j < EliteAntlion.getPosition().size(); j++) {
                    if (mAnts.get(i).getPosition().get(j) > this.ub) {
                        mAnts.get(i).getPosition().set(j, this.ub);
                    } else if (mAnts.get(i).getPosition().get(j) < this.lb){
                        mAnts.get(i).getPosition().set(j, this.lb);
                    }
                }
                mAnts.get(i).updateFitness();
                // 混沌映射修复
                if (mAnts.get(i).getFitness() == 0) {
//                    mAnts.set(i, sortAntlions(Random_wald_around_antlion(getNewAntlion_1(), i)).get(0));
                    mAnts.set(i, getNewAntlion());
                }

                mAnts.get(i).updateFitness();
            }
            List<Antlion> double_population = new ArrayList<>();
            double_population.addAll(mAntlions);
            double_population.addAll(mAnts);
            double_population = sortAntlions(double_population);

            for (int i = 0; i < this.num; i++) {
                mAntlions.set(i, double_population.get(i));
            }

//            EliteAntlion.UpdateFitness(current_iter);
            if (double_population.get(0).getFitness() > EliteAntlion.getFitness()) {
                EliteAntlion = double_population.get(0);
                addtoRegistory(EliteAntlion);
            }

            //
            mAntlions.set(0, EliteAntlion);
            ConvergenceData.add(EliteAntlion.getFitness());
//            System.out.println(rules.size()+ "   " + current_iter + " : " + EliteAntlion.getFitness() + " : " + EliteAntlion.toString());
            System.out.println("rules : " + rules.size()+ "   " + current_iter + " : " + EliteAntlion.getFitness() + " : " + EliteAntlion.toString());
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
