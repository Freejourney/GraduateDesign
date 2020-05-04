package graduatedesign.ALO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Antlion {
    private List<Double> position = new ArrayList<>();
    private int dimension;
    private double fitness;
    List<Double> weights = new ArrayList<>();
    List<Double> values = new ArrayList<>();
    double capicity = 0.0;
    private Function function = new Function();

    Antlion(int dimension, double ub, double lb, List<Double> weights, List<Double> values, double capicity) {
        this.dimension = dimension;
        this.weights = weights;
        this.values = values;
        this.capicity = capicity;
        for (int i = 0; i < dimension; i++) {
            position.add(new Random().nextDouble()*(ub-lb)+lb);
        }
        updateFitness();
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
        fitness = 0;
        double weight = 0;
        for (int i = 0; i < this.dimension; i++) {
            if (position.get(i) > 0) {
                fitness += values.get(i);
                weight += weights.get(i);
            }
        }
        if (weight > capicity) {
            fitness = 0;
        }
//        double[] x = new double[position.size()];
//        for (int i = 0; i < position.size(); i++) {
//            x[i] = position.get(i);
//        }
//        fitness = -function.sphere(x);
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

}
