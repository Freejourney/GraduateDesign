package graduatedesign.PMALOApriori;

/**
 * Created by ua28 on 5/21/20.
 */
public class Rule {

    private String rule;
    private double support;
    private double confidence;

    public Rule(String rule, double support) {
        this.rule = rule;
        this.support = support;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
