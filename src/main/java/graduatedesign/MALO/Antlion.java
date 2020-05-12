package graduatedesign.MALO;

import graduatedesign.ALO.Function;

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

    private double weight;

    private double testweight;
    private double testfitness;

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

    public Antlion(Antlion antlion) {
        this.dimension = antlion.getDimension();
        for (double w : antlion.getWeights())
            this.weights.add(w);
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

    public List<Double> getWeights() {
        return weights;
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

        this.weight = weight;

        testweight = getTestWeight();
        testfitness = getTestFitness();
    }


    public void setPosition(int i, Double value) {
        position.set(i, value);
    }

    public double setAndUpdate(int i, double pos) {
        double prePos = this.position.get(i);

        int flag = 0;
        if (prePos <= 0 && pos > 0) {
            weight += weights.get(i);
            flag = 1;
        } else if (prePos > 0 && pos <= 0) {
            weight -= weights.get(i);
            flag = 2;
        }

        if (weight < capicity) {
            if (fitness == 0) {
                fitness = getTestFitness();
            } else {
                switch (flag) {
                    case 1: fitness += values.get(i);break;
                    case 2: fitness -= values.get(i);break;
                    default:break;
                }
            }
        } else {
            fitness = 0;
        }

        position.set(i, pos);

        testweight = getTestWeight();
        testfitness = getTestFitness();

        return fitness;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getTestWeight() {
        double weight = 0.0;
        for (int i = 0; i < position.size(); i++)
            if (position.get(i) > 0)
                weight += weights.get(i);
        return weight;
    }

    public double getTestFitness() {
        double fitness = 0.0;
        for (int i = 0; i < position.size(); i++)
            if (position.get(i) > 0)
                fitness += values.get(i);
        return fitness;
    }
}
