package graduatedesign.MALO;

import java.util.*;

public class MALO {
    private int num;
    private int iteration;
    private double ub, lb;
    private int dimension;
    private List<Antlion> AntsRegistory = new ArrayList<>();
    private List<Antlion> mAntlions = new ArrayList<>();
    private List<Antlion> mAnts = new ArrayList<>();
    private Antlion EliteAntlion;

    List<Double> weights = new ArrayList<>();
    List<Double> values = new ArrayList<>();
    double capicity = 0.0;

    MALO(int num, int iteration, int dimension, double ub, double lb, List<Double> weights, List<Double> values, double capicity) {
        this.dimension = dimension;
        this.ub = ub;
        this.lb = lb;
        this.num = num;
        this.iteration = iteration;
        this.weights = weights;
        this.values = values;
        this.capicity = capicity;
        for (int i = 0; i < num; i++) {
            mAntlions.add(new Antlion(dimension, ub, lb, weights, values, capicity));
            mAnts.add(new Antlion(dimension, ub, lb, weights, values, capicity));
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
        List<Double> ConvergenceData = new ArrayList<>();
        mAntlions=sortAntlions(mAntlions);
        EliteAntlion = mAntlions.get(0);
//        TEliteAntlion = mAntlions.get(0);
        for (int current_iter = 0; current_iter < this.iteration-1; current_iter++) {
//            EliteAntlion.UpdateFitness(current_iter);
            for (int i = 0; i < this.num; i++) {
                int rolette_index = RouletteWheelSelection();
                if (rolette_index == -1) {
                    rolette_index = 0;
                }

                // 蚂蚁围绕轮盘选赌法选得的蚁狮游走
                List<Antlion> RA = Random_wald_around_antlion(mAntlions.get(rolette_index), current_iter);
                // 蚂蚁围绕精英蚁狮进行游走
                List<Antlion> RE = Random_wald_around_antlion(EliteAntlion, current_iter);

                // 对随机游走解进行排序，选取随机游走解中的最优解来更新新蚂蚁位置
                RA = sortAntlions(RA);
                RE = sortAntlions(RE);

                List<Antlion> RM = Random_wald_around_antlion(mAnts.get(i), current_iter);
                RM = sortAntlions(RM);

                if (RA.get(0).getFitness() > EliteAntlion.getFitness()) {
                    EliteAntlion = RA.get(0);
                    addtoRegistory(EliteAntlion);
                }
                if (RE.get(0).getFitness() > EliteAntlion.getFitness()) {
                    EliteAntlion = RE.get(0);
                    addtoRegistory(EliteAntlion);
                }
                if (RM.get(0).getFitness() > EliteAntlion.getFitness()) {
                    EliteAntlion = RM.get(0);
                    addtoRegistory(EliteAntlion);
                }
                // 三者位置平均为蚂蚁新位置
                Antlion RF = getRF(RA.get(0), RE.get(0), RM.get(0));
                RF.updateFitness();

                List<Antlion> RZZ = Random_wald_around_antlion(RF, current_iter);
                RZZ = sortAntlions(RZZ);

                // 从三者中选取最佳作为新蚂蚁
                List<Antlion> RZ = new ArrayList<>();
                RZ.add(RF);
                RZ.add(RZZ.get(0));
                RZ.add(RA.get(0));
                RZ.add(RE.get(0));
                RZ.add(RM.get(0));

                RZ = sortAntlions(RZ);

                mAnts.set(i, RZ.get(0));
            }
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
                    mAnts.set(i, sortAntlions(Random_wald_around_antlion(getNewAntlion_1(), i)).get(0));
//                    mAnts.set(i, getNewAntlion());
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
//            System.out.println(current_iter + " : " + EliteAntlion.getFitness() + " : " + EliteAntlion.getC());
        }
        System.out.println(ConvergenceData.toString());
        return ConvergenceData;
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

        antlion1.setWeight(antlion2.getWeight());
        return antlion1;
    }

    private Antlion getNewAntlion() {
        Antlion antlion = AntsRegistory.get(new Random().nextInt(AntsRegistory.size()-1));
        List<Antlion> nantlions = Random_wald_around_antlion(antlion, 1);
        nantlions = sortAntlions(nantlions);
        return nantlions.get(0);
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
    private List<Antlion> Random_wald_around_antlion(Antlion antlion, int current_iter) {
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
//                RWs.get(i).getPosition().set(j, ((X.get(i)-a)*(d-c))/(b-a)+c);

                // unavailiable for individuals with 0 fitness(pulished individuals have larger weight but 0 fitness value)
                // only suitable individuals will use "setAndUpdate() method" for monitoring elites
                if (RWs.get(i).getFitness() <= 0.1) {
                    RWs.get(i).getPosition().set(j, ((X.get(i) - a) * (d - c)) / (b - a) + c);
                } else {
                    double tmpfitness = RWs.get(i).setAndUpdate(j, ((X.get(i) - a) * (d - c)) / (b - a) + c);
                    if (tmpfitness > EliteAntlion.getFitness()) {
                        EliteAntlion = Clone(RWs.get(i));
                    }
                }


            }
        }

        // 更新所有游走解的适应度值
        for (int i = 0; i < this.iteration; i++) {
            RWs.get(i).updateFitness();
        }
        return RWs;
    }

    private Antlion Clone(Antlion antlion) {
        Antlion antlion1 = new Antlion(this.dimension, ub, lb, weights, values, capicity);
        for (int i = 0; i < antlion.weights.size(); i++) {
            antlion1.setPosition(i, antlion.getPosition().get(i));
        }
        antlion1.setWeight(antlion.getWeight());
        antlion1.setFitness(antlion.getFitness());
        antlion1.updateFitness();
        return antlion1;
    }

    private int RouletteWheelSelection() {
        List<Double> accumulation = new ArrayList<>();
        double temp = 0;
        for (int i = 0; i < this.num; i++) {
            temp += Math.abs(1/mAntlions.get(i).getFitness());
            accumulation .add(temp);
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
