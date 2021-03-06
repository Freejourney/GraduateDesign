package graduatedesign.ALO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class Main {

    private static List<List<Double>> Weights = new ArrayList<>();
    private static List<List<Double>> Values = new ArrayList<>();
    private static List<Double> Max_capicity = new ArrayList<>();
    private static List<Integer> Dimension = new ArrayList<>();
    private static List<Double> Max_value = new ArrayList<>();
    private static int flag = 0;

    public static void main(String[] args) {
        // write your code here
        URL resource = Main.class.getClassLoader().getResource("data.dsv");
        toArrayByFileReader1(resource.getPath());

//        URL resource = Main.class.getClassLoader().getResource("small-01data.txt");
//        extractDataFromFile(resource.getPath());


        for (int t = 0; t < 10; t++) {
            System.out.println("KP"+t+": -------------------------------------------");
            List<Double> Results = new ArrayList<>();
            List<Double> ConvergenceData = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                System.out.print(i+" : ");
                ALO alo = new ALO(30, 140, Dimension.get(t), 1, -1, Weights.get(t), Values.get(t), Max_capicity.get(t));
                long startTime = System.currentTimeMillis();
                ConvergenceData.addAll(alo.searchSolution());
                long endTime = System.currentTimeMillis();
                System.out.println("运行时间:" + (endTime - startTime) + "ms");
                Results.add(ConvergenceData.get(ConvergenceData.size()-1));
            }
            System.out.println(Results.toString());
            calcu(Results);
        }
        System.out.println("总共有"+flag+"个标准差为0");
    }

    public static void calcu(List<Double> Results) {
        double [] arr = new double[Results.size()];
        for (int i = 0; i < Results.size(); i++) {
            arr[i] = Results.get(i);
        }

        // 排序：
        for (int i = 0; i < arr.length-1; i++) {
            for (int j = 0; j < arr.length-1-i; j++) {
                if (arr[j] < arr[j+1] ) {
                    double temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }

        // 排序后结果
        System.out.println("排序结果：");
        for (int i = 0; i < arr.length; i++)
            System.out.print(arr[i]+" ");
        System.out.println();

        // 中位数
        System.out.print("中位数为： ");
        if (arr.length % 2 == 0) {
            System.out.println((arr[(arr.length/2)-1]+arr[(arr.length/2)+1])/2);
        } else if (arr.length % 2 == 1) {
            System.out.println(arr[(arr.length/2)]);
        }


        //求出数组的总和
        double sum = 0;
        for(int i=0;i<arr.length;i++){
            sum += arr[i];
        }
        System.out.println("总和："+sum);

        //求出数组的平均数
        double average = sum*1.0/arr.length;
        System.out.println("平均数："+average);

        //求出方差
        double total=0;
        for(int i=0;i<arr.length;i++){
            total += (arr[i]-average)*(arr[i]-average);
        }

        //求出标准差
        double standardDeviation = Math.sqrt(total*1.0/arr.length);
        System.out.println("标准差"+standardDeviation);

        if (standardDeviation == 0) {
            flag++;
        }
    }

    public static void extractDataFromFile(String name) {
        ArrayList<String> list = new ArrayList<>();

        try {
            FileReader fr = new FileReader(name);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            bf.readLine();
            while ((str = bf.readLine())!=null) {
                list.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String str : list) {
            String[] roughSplit = str.split("\t");
            Dimension.add(Integer.valueOf(roughSplit[1]));
            Max_value.add(Double.valueOf(roughSplit[2]));
            String wcp = roughSplit[3];

            int state = 0;
            int i = 0;
            int begin = 0;
            int end = 0;
            while (i != wcp.length()) {
                switch (state) {
                    case 0:
                        if (wcp.charAt(i)=='(') {
                            state = 1;
                            begin = i+1;
                        }
                        break;
                    case 1:
                        if (wcp.charAt(i)==')') {
                            end = i;
                            String wsubstring = wcp.substring(begin, end);
                            String[] ws = wsubstring.split(",");
                            List<Double> tmplist = new ArrayList<>();
                            for (String w : ws)
                                tmplist.add(Double.valueOf(w));

                            if (Weights.size() > Values.size()) {
                                Values.add(tmplist);
                            } else {
                                Weights.add(tmplist);
                            }
                            state = 2;
                        }
                        break;
                    case 2:
                        if (wcp.charAt(i) == '=') {
                            begin = i+1;
                        }
                        if (wcp.charAt(i) == ',' && begin > end) {
                            end = i;
                            String csubstring = wcp.substring(begin, end);
                            Max_capicity.add(Double.valueOf(csubstring));
                            state = 0;
                        }
                        break;
                    default: break;
                }
                i++;
            }
        }
    }

    public static void toArrayByFileReader1(String name) {
        // 使用ArrayList来存储每行读取到的字符串
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(name);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 对ArrayList中存储的字符串进行处理
        int length = arrayList.size();
        for (int i = 0; i < length; i++) {
            List<Double> weights = new ArrayList<>();
            List<Double> values = new ArrayList<>();
            Double max_value = 0.0;
            Double max_c = 0.0;
            int dimension = 0;

            String s = arrayList.get(i);
            s = s.substring(2);
            String[] temp = s.split(",");
            switch ((i + 1) % 5) {
                case 1:
                    for (int j = 0; j < temp.length; j++) {
                        weights.add(Double.parseDouble(temp[j]));
                    }
                    Weights.add(weights);
                    break;
                case 2:
                    max_c = Double.parseDouble(temp[0]);
                    Max_capicity.add(max_c);
                    break;
                case 3:
                    for (int j = 0; j < temp.length; j++) {
                        values.add(Double.parseDouble(temp[j]));
                    }
                    Values.add(values);
                    break;
                case 4:
                    dimension = Integer.parseInt(temp[0]);
                    Dimension.add(dimension);
                    break;
                case 0:
                    max_value = Double.parseDouble(temp[0]);
                    Max_value.add(max_value);
                    break;
            }
        }
    }
}
