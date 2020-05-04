package graduatedesign.ALO;

/**
 * Created by ua28 on 5/4/20.
 */
public class Function {
    //fitness index
    private int idx;
    //shift
    private double shift;
    private static double Lmax;
    private static double Lmin;

    //set parameter
    public void setParameter(int idx, double shift) {
        this.idx = idx;
        this.shift = shift;
    }

    public double functionvalue (double [] para) {
        int i;
        double rs = 0;
        double tem;
        double[] x = new double[para.length];
        for(i = 0;i < x.length;i++) {
            x[i] = para[i] + shift;
        }
        int dimension = x.length;
        switch(idx) {
            case 1:			/* Sphere Parabola Function  Bounds[-100,100] dim30 optimum=0D ini[50,100]*/
                for(i = 0;i < dimension;i++)
                {
                    rs += x[i]*x[i];
                }
                break;

            case 2:			/* Schwefel 1.2    Bounds[-100,100] dim30 optimum=0D ini[50,100]	*/

                for(i=0; i < dimension;i++)
                {
                    tem = 0;
                    for(int j=0; j<i; j++)
                    {
                        tem = x[j]*x[j] + tem;
                    }
                    rs += tem;
                }
                break;
            case 3:			/* Generalized Rosenbrock    Bounds[-30,30]   dim30 optimum=1D ini[15,30] 	 */
                for(i = 0; i <dimension -1; i++)
                {
                    rs = rs + 100*Math.pow(x[i+1]-x[i]*x[i],2.0)+Math.pow(x[i]-1,2.0);
                }
                break;
            case 4:			/* Ackley 	Function          Bounds[-32,32]   dim30 optimum=0D ini[16,32]*/
                tem = 0;
                for(i = 0;i < dimension;i++)
                {
                    rs += Math.pow(x[i],2.0);
                    tem += Math.cos(2*Math.PI*x[i]);
                }
                rs = -0.2*Math.sqrt(rs/dimension);
                rs = -20*Math.exp(rs);

                tem = -Math.exp(tem/dimension);

                rs += 20 + Math.exp(1.0) + tem;
                break;
            case 5:			/* Generalized Griewank's    Bounds[-600,600] dim30 optimum=0D ini[300,600]*/
                tem = 1.0;
                for(i = 0;i < dimension;i++)
                {
                    rs += Math.pow(x[i],2.0);
                    tem *= Math.cos(x[i]/Math.sqrt(i+1.0));
                }
                rs /= 4000;
                rs += 1 - tem;
                break;
            case 6:			/* Generalized Rastrigin     Bounds[-5.12,5.12]; dim30 optimum=0D ini[2.56,5.12]*/
                tem = 0;
                for(i = 0;i < dimension;i++)
                {
                    rs += Math.pow(x[i],2.0);
                    tem += Math.cos(2*Math.PI*x[i]);
                }
                rs += -10*tem + 10*dimension;
                break;
            case 7:			/* Penalized Function P16    Bounds[-50,50] dim30 optimum=1D ini[25,50]  */
                tem = 0;
                for( i = 0; i < dimension; i++)
                {
                    tem = tem + miu_benchmark(x[i],5.0,100.0,4.0);
                }
                for( i = 0; i < dimension - 1; i++)
                {
                    rs = rs + Math.pow(x[i]-1,2)*(1 + Math.pow(Math.sin(3*Math.PI*x[i+1]),2));
                }
                rs = rs + Math.pow(Math.sin(3*Math.PI*x[0]),2.0);
                rs = rs + Math.pow(x[dimension-1]-1,2)*(1+Math.pow(Math.sin(2*Math.PI*x[dimension-1]),2));
                rs = rs * 0.1;
                rs = rs + tem;
                break;
            case 8:		/* Six Hump Camel-back       Bounds[-5,5]  dim2 optimaum=(-0.0898,0.7126),(0.0898,-0.7126) ini[25,50]*/
                rs = 4*Math.pow(x[0],2.0)-2.1* Math.pow(x[0],4.0)+1.0/3.0*Math.pow(x[0],6.0)+x[0]*x[1]-4*Math.pow(x[1],2)+4*Math.pow(x[1],4);
                break;
            case 9:		/* Goldstein Price           Bounds[-2,2]  dim2 optimaum=(0,-1) ini[1,2]  */
                rs =  1+Math.pow(x[0]+x[1]+1,2)*(19-14*x[0]+3*x[0]*x[0]-14*x[1]+6*x[0]*x[1]+3*x[1]*x[1]);
                rs = rs*(30+Math.pow(2*x[0]-3*x[1],2)*(18-32*x[0]+12*x[0]*x[0]+48*x[1]-36*x[0]*x[1]+27*x[1]*x[1]));
                break;
            // case 12:		/* Schkel 5                  Bounds[0,10] dim4 optimaum=4.0D ini[7.5,10] */
            // break;
            // case 13:		/* Schkel 7                  Bounds[0,10]  dim4 optimaum=4.0D ini[7.5,10]  */
            // break;
            // case 14:		/* Schkel 10                 Bounds[0,10]  dim4 optimaum=4.0D ini[7.5,10] */
            // break;
            case 10:		/* Schaffer') Bounds[-100,100] dim2 optimum=0D ini [50,100]*/
                rs = 0.5 + (Math.pow(Math.sin(Math.sqrt(x[0]*x[0]+x[1]*x[1])),2)-0.5)/Math.pow(1.0+0.001*(x[0]*x[0]+x[1]*x[1]),2);
                break;
            case 11:		/* Axis_parallel_hyper_ellipsoid Bounds[-5.12,5.12] dim 30 optimum 0.0D ini [2.56,5.12]*/
                for( i = 0; i < dimension; i++)
                {
                    rs = rs + i * x[i] * x[i];
                }

                break;
            case 12:		/* Rotated_hyper_ellipsoid Bounds[-65.536,65.536] dim 30 optimum 0.0D ini [32.768,65.536]*/
                for( i = 0; i < dimension; i++)
                {
                    tem = 0;
                    for( int j = 0; j < i; j++)
                    {
                        tem = tem + x[j];
                    }
                    rs = rs + tem * tem;
                }
                break;
            case 13:
                for (i = 0; i < dimension; i++) {
                    rs += x[i];
                }
                break;
            case 14:

//			System.out.println(rs+"----- 1: "+x[0]+"2: "+x[1]);
                break;
            case 15:
                double weight = 0.0, value = 0.0;
                double [] weights = {
                        24,45,15,40,9,37,13,5,43,35,48,50,27,46,24,45,2,7,38,20,20,31,2,20,3,35,27,4,21,22,33,11,5,24,37,31,46,13,12,12,41,36,44,36,34,22,29,50,48,17,8,21,28,2,44,45,25,11,37,35,24,9,40,45,8,47,1,22,1,12,36,35,14,17,5
                };
                double [] values = {
                        2,73,82,12,49,35,78,29,83,18,87,93,20,6,55,1,83,91,71,25,59,94,90,61,80,84,57,1,26,44,44,88,7,34,18,25,73,29,24,14,23,82,38,67,94,43,61,97,37,67,32,89,30,30,91,50,21,3,18,31,97,79,68,85,43,71,49,83,44,86,1,100,28,4,16
                };

                int C = 1433;

                for (int j = 0; j < weights.length; j++) {
                    if (x[j] > 0) {
                        value += values[j];
                        weight += weights[j];
                    }
                }
                if (weight > C) {
                    value = 0;
                }
                rs = value;
                break;
            default:
                System.err.println("invalid function index " + idx);
                System.exit(-1);
        }
        return rs;
    }

    public double sphere (double [] para) {
        int i;
        double value = 0;
        for(i = 0;i < para.length;i ++) {
            value += (para[i]) * (para[i]);
        }
        return value;
    }

    private double rosenbrock (double [] para) {
        int i;
        double value = 0;
        for(i = 1;i < para.length;i ++) {
            value += 100 * (para[i] - para[i-1]*para[i-1])* (para[i] - para[i-1]*para[i-1]) + (para[i-1]-1)*(para[i-1]-1);
        }
        return value;
    }

    private double rastrigrin (double [] para) {
        int i;
        double value = 0;
        for(i = 0;i < para.length;i ++) {
            value += para[i] * para[i] - 10 * Math.cos(2 * Math.PI * para[i]) + 10;
        }
        return value;
    }

    private double griewank (double [] para) {
        int i;
        double value = 1;
        for(i = 0;i < para.length;i ++) {
            value += para[i] * para[i] / 4000;
        }
        double mul = 1;
        for(i = 0;i < para.length;i ++) {
            mul *= Math.cos(para[i] / Math.sqrt(i + 1));
        }
        value -= mul;
        return value;
    }

    private double ellipse (double [] para) {
        int i;
        double value = 0;
        for(i = 0;i < para.length;i ++) {
            value += Math.pow(10, 4 * i / (para.length - 1)) * para[i] * para[i];
        }
        return value;
    }

    private double cigar (double [] para) {
        int i;
        double value = para[0] * para[0];
        for(i = 1;i < para.length;i ++) {
            value += 10000 * para[i] * para[i];
        }
        return value;
    }

    private double tablet (double [] para) {
        int i;
        double value = 10000 * para[0] * para[0];
        for(i = 1;i < para.length;i ++) {
            value += para[i] * para[i];
        }
        return value;
    }

    private double schwefel (double [] para) {
        int i;
        double value = 0;
        for(i = 0;i < para.length;i ++) {
            value += (para[0] - para[i] * para[i]) * (para[0] - para[i] * para[i]) + (para[i] - 1) * (para[i] - 1);
        }
        return value;
    }

    private double ackley (double [] para) {
        int i;
        double squaresum = 0;
        double cossum = 0;
        for(i = 0;i < para.length;i ++) {
            squaresum += para[i] * para[i];
            cossum += Math.cos(2 * Math.PI * para[i] * para[i]);
        }
        double value = 20 + Math.E;
        value -= 20 * Math.exp(-0.2 * Math.sqrt(squaresum / para.length));
        value -= Math.exp(cossum / para.length);
        return value;
    }

    private double miu_benchmark (double x,double a,double k,double m)
    {
        double fit;
        if( x > a ){	fit = k * Math.pow(x-a,m);	}
        else if(x > -a){	fit = 0;}
        else {fit = k * Math.pow(-x-a, m); }
        return fit;
    }
}
