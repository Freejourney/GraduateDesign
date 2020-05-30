package graduatedesign.PSO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static graduatedesign.PSO.Main.*;

public class Particle {
    private List<Double> position = new ArrayList<>();
    private List<Double> velocity = new ArrayList<>();
    private double fitnessValue;
    private int dimension;
    private double boundMin, boundMax;
    private List<Double> pBestPositon = new ArrayList<>();
    private double pBestFitnessValue;

    Particle(double boundMin, double boundMax, int dimension){
        this.boundMin = boundMin;
        this.boundMax = boundMax;
        this.dimension = dimension;
        for (int i = 0; i < dimension; i++) {
            position.add(new Random().nextDouble()*2-1);
            velocity.add(new Random().nextDouble()*2-1);
        }
        pBestPositon = position;
        setFitnessValue();
        pBestFitnessValue = getFitnessValue();
        fitnessValue = getFitnessValue();
    }

    public void setPosition(List<Double> position) {
        this.position = position;
    }

    public List<Double> getPosition() {
        return position;
    }

    public void setVelocity(List<Double> velocity) {
        this.velocity = velocity;
    }

    public List<Double> getVelocity() {
        return velocity;
    }

    public int getDimension() {
        return dimension;
    }

    public void setpBestPositon(List<Double> pBestPositon) {
        this.pBestPositon = pBestPositon;
    }

    public List<Double> getpBestPositon() {
        return pBestPositon;
    }

    public void setFitnessValue() {
        this.fitnessValue = calcufitness();
    }

    private double calcufitness() {
//        double fitness = 0;
//        for (int i = 0; i < dimension; i++)
//            fitness += position.get(i);
//        return fitness;

        List<String> list = new ArrayList<>();
        for (int i = 0; i < position.size(); i++) {
            if (position.get(i) > 0) {
                list.add(oneitemset.get(i).get(0));
            }
        }

        int num = countFrequent(list);

        double fitness;
        if (list.size() == 0 || num < 1000)
            fitness  = Double.MIN_NORMAL;
        else {
            fitness = num * 1.0 / record.size();
            if (!rules.containsKey(String.join(",", list))) {
                rules.put(String.join(",", list), fitness);
            } else {
                return fitness/2;
            }
        }
        return fitness;
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public double getpBestFitnessValue() {
        return pBestFitnessValue;
    }

    public void setpBestFitnessValue(double pBestFitnessValue) {
        this.pBestFitnessValue = pBestFitnessValue;
    }

    public double getBoundMax() {
        return boundMax;
    }

    public double getBoundMin() {
        return boundMin;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "position=" + position +
                ", velocity=" + velocity +
                ", fitnessValue=" + fitnessValue +
                ", dimension=" + dimension +
                ", pBestPositon=" + pBestPositon +
                ", pBestFitnessValue=" + pBestFitnessValue +
                '}';
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
