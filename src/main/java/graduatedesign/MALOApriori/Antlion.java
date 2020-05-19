package graduatedesign.MALOApriori;

import graduatedesign.ALO.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static graduatedesign.MALOApriori.Main.oneitemset;
import static graduatedesign.MALOApriori.Main.record;
import static graduatedesign.MALOApriori.Main.rules;


public class Antlion {
    private List<Double> position = new ArrayList<>();
    private int dimension;
    private double fitness;
    List<Double> values = new ArrayList<>();
    double capicity = 0.0;

    Antlion(int dimension, double ub, double lb) {
        this.dimension = dimension;
        for (int i = 0; i < dimension; i++) {
            position.add(new Random().nextDouble()*(ub-lb)+lb);
        }
        updateFitness();
    }

    public Antlion(Antlion antlion) {
        this.dimension = antlion.getDimension();
        for (double v : antlion.getValues())
            this.values.add(v);
        this.capicity = antlion.getCapicity();
        for (double p : antlion.getPosition())
            this.position.add(p);
        this.fitness = antlion.getFitness();
    }

    public double getCapicity() {
        return capicity;
    }

    public List<Double> getValues() {
        return values;
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

        int num = countFrequent(list);

        if (list.size() == 0 || num < 1000)
            fitness = 0.0001;
        else {
            fitness = num * 1.0 / record.size();
            if (!rules.containsKey(String.join(",", list))) {
                rules.put(String.join(",", list), fitness);
            }
        }
    }


    public void setPosition(int i, Double value) {
        position.set(i, value);
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
}
