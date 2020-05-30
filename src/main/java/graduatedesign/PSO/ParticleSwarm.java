package graduatedesign.PSO;


import java.util.ArrayList;
import java.util.List;

public class ParticleSwarm {

    private List<Particle> mParticles = new ArrayList<>();
    private int Size;
    private Particle GlobalBestParticle;

    ParticleSwarm(int boundmin, int boundmax, int num_partcles, int dimension) {
        this.Size = num_partcles;

        for (int i = 0; i < Size; i++) {
            mParticles.add(new Particle(boundmin, boundmax, dimension));
        }

        setGlobalBestParticle(mParticles.get(0));
    }

    public void setGlobalBestParticle(Particle globalBestPosition) {
        this.GlobalBestParticle = globalBestPosition;
    }

    public Particle getGlobalBestParticle() {
        return GlobalBestParticle;
    }

    public List<Particle> getmParticles() {
        return mParticles;
    }

    public int getSize() {
        return Size;
    }

}
