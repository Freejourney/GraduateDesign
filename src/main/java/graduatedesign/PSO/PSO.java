package graduatedesign.PSO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static graduatedesign.PSO.Main.rules;


public class PSO {
    private ParticleSwarm particleSwarm;
    private int iteration;
    private double weight;
    private double c1, c2;

    PSO(int boundmin, int boundmax, int num_partcles, int dimension, double weight, int iteration, double c1, double c2) {
        this.particleSwarm = new ParticleSwarm(boundmin, boundmax, num_partcles, dimension);
        this.iteration = iteration;
        this.weight = weight;
        this.c1 = c1;
        this.c2 = c2;
    }


    public void searchSolution() {
        for (int i = 0; i < this.iteration; i++) {
            for (int j = 0; j < particleSwarm.getSize(); j++) {
                updateVelocity(particleSwarm.getmParticles().get(j));
                updatePosition(particleSwarm.getmParticles().get(j));
                particleSwarm.getmParticles().get(j).setFitnessValue();
                if (particleSwarm.getmParticles().get(j).getFitnessValue() > particleSwarm.getmParticles().get(j).getpBestFitnessValue()) {
                    particleSwarm.getmParticles().get(j).setpBestFitnessValue(particleSwarm.getmParticles().get(j).getFitnessValue());
                    particleSwarm.getmParticles().get(j).setpBestPositon(particleSwarm.getmParticles().get(j).getPosition());
                    if (particleSwarm.getmParticles().get(j).getFitnessValue() > particleSwarm.getGlobalBestParticle().getFitnessValue()) {
                        particleSwarm.setGlobalBestParticle(particleSwarm.getmParticles().get(j));
                    }
                }
            }
            System.out.println(rules.size()+ " " +i+" -- "+particleSwarm.getGlobalBestParticle().getFitnessValue());
        }
    }

    private void updatePosition(Particle particle) {
        for (int i = 0; i < particle.getDimension(); i++) {
            double xi = particle.getPosition().get(i)+particle.getVelocity().get(i);
            if (xi > particle.getBoundMax()) {
                xi = particle.getBoundMax();
            }
            if (xi < particle.getBoundMin()) {
                xi = particle.getBoundMin();
            }
            particle.getPosition().set(i, xi);
        }
    }

    private void updateVelocity(Particle particle) {
        double r1 = new Random().nextDouble();
        double r2 = new Random().nextDouble();
        List<Double> v = particle.getVelocity();
        List<Double> x = particle.getPosition();
        List<Double> pbest = particle.getpBestPositon();
        List<Double> gbest = particleSwarm.getGlobalBestParticle().getPosition();

        List<Double> newVelocity = new ArrayList<>();
        for (int i = 0; i < particle.getDimension(); i++) {
            newVelocity.add(weight*v.get(i)+c1*r1*(pbest.get(i)-x.get(i)) + c2*r2*(gbest.get(i)-x.get(i)));
        }

        particle.setVelocity(newVelocity);
    }

}