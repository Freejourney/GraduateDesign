package graduatedesign.PMALOApriori;

import java.io.Serializable;

/**
 * Created by ua28 on 5/18/20.
 */
public class Rollette implements Serializable {
    private static final long serialVersionUID = 6961272256829097961L;

    private int index;

    Rollette(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
