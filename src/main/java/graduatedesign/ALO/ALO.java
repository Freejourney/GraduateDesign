package graduatedesign.ALO;

import java.util.*;

public class ALO {

    private int num;
    private int iteration;
    private double ub, lb;
    private List<Antlion> mAntlions = new ArrayList<>();
    private List<Antlion> mAnts = new ArrayList<>();
    private Antlion EliteAntlion;
    List<Double> weights = new ArrayList<>();
    List<Double> values = new ArrayList<>();
    double capicity = 0.0;

    ALO(int num, int iteration, int dimension, double ub, double lb, List<Double> weights, List<Double> values, double capicity) {
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

    public List<Double> searchSolution() {
        List<Double> ConvergenceData = new ArrayList<>();
        sortAntlions(mAntlions);
        EliteAntlion = mAntlions.get(0);
        for (int current_iter = 0; current_iter < this.iteration-1; current_iter++) {
            for (int i = 0; i < this.num; i++) {
                int rolette_index = RouletteWheelSelection();
                if (rolette_index == -1) {
                    rolette_index = 0;
                }
                // 蚂蚁围绕轮盘选赌法选得的蚁狮游走
                List<Antlion> RA = Random_wald_around_antlion(mAntlions.get(rolette_index), current_iter);
                // 蚂蚁围绕精英蚁狮进行游走
                List<Antlion> RE = Random_wald_around_antlion(EliteAntlion, current_iter);
                // 两者位置平均为蚂蚁新位置：蚂蚁之前的位置并为对结果加权？
                Antlion RF = getRF(RA.get(current_iter), RE.get(current_iter));
                RF.updateFitness();
                mAnts.set(i, RF);
            }
            // 新的范围始终得在基本边界范围内
            for (int i = 0; i < this.num; i++) {
                for (int j = 0; j < EliteAntlion.getDimension(); j++) {
                    if (mAnts.get(i).getPosition().get(j) > this.ub) {
                        mAnts.get(i).getPosition().set(j, this.ub);
                    } else if (mAnts.get(i).getPosition().get(j) < this.lb){
                        mAnts.get(i).getPosition().set(j, this.lb);
                    }
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

            if (double_population.get(0).getFitness() > EliteAntlion.getFitness()) {
                EliteAntlion = double_population.get(0);
            }

            //
            mAntlions.set(0, EliteAntlion);
//            System.out.println(current_iter + " : " + EliteAntlion.getFitness());
            ConvergenceData.add(EliteAntlion.getFitness());
        }
        System.out.println(ConvergenceData.toString());
        return ConvergenceData;
    }

    private Antlion getRF(Antlion antlion, Antlion antlion1) {
        Antlion mAntlion = new Antlion(antlion.getDimension(), -1, 1, weights, values, capicity);
        for (int i = 0; i < antlion.getDimension(); i++) {
            mAntlion.getPosition().set(i, (antlion.getPosition().get(i)+antlion1.getPosition().get(i))/2);
        }
        return mAntlion;
    }


    // 随机游走
    private List<Antlion> Random_wald_around_antlion(Antlion antlion, int current_iter) {
        List<Double> ub = new ArrayList<>();
        List<Double> lb = new ArrayList<>();
        for (int i = 0; i < antlion.getDimension(); i++) {
            ub.add(this.ub);
            lb.add(this.lb);
        }
        double I = 1;
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

        for (int i = 0; i < antlion.getDimension(); i++) {
            lb.set(i, lb.get(i)/I);
            ub.set(i, ub.get(i)/I);
        }

        // 将边界随机调整为当前位置的上下界，限制随机游走后新的范围
        if (new Random().nextDouble() < 0.5) {
            for (int i = 0; i < antlion.getDimension(); i++) {
                lb.set(i, lb.get(i)+antlion.getPosition().get(i));
            }
        } else {
            for (int i = 0; i < antlion.getDimension(); i++) {
                lb.set(i, -lb.get(i)+antlion.getPosition().get(i));
            }
        }
        if (new Random().nextDouble() >= 0.5) {
            for (int i = 0; i < antlion.getDimension(); i++) {
                ub.set(i, ub.get(i)+antlion.getPosition().get(i));
            }
        } else {
            for (int i = 0; i < antlion.getDimension(); i++) {
                ub.set(i, -ub.get(i)+antlion.getPosition().get(i));
            }
        }

        // 按维度游走的操作
        List<Antlion> RWs = new ArrayList<>();
        for (int i = 0; i < this.iteration; i++) {
            RWs.add(new Antlion(antlion.getDimension(), -1, 1, weights, values, capicity));
        }
        for (int j = 0; j < antlion.getDimension(); j++) {
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
        return RWs;
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
