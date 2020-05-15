package graduatedesign.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ua28 on 5/15/20.
 */
public class Preprocessing {

    public List<List<String>> parseStudentPerformance(String path) {
        List<List<String>> result = new ArrayList<>();
        try {
            FileReader fr = new FileReader(new File(path));
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String str;
            while ((str = br.readLine())!= null) {
                List<String> list = new ArrayList<>();
                String[] elements = str.split(",");
                Collections.addAll(list, elements);
                result.add(list);

                for (int i = 5; i <= 7; i++) {
                    double grade = Double.valueOf(list.get(i).substring(1, list.get(i).length()-1));
                    if (grade > 90)
                        list.set(i, "A");
                    else if (grade > 80)
                        list.set(i, "B");
                    else if (grade > 70)
                        list.set(i, "C");
                    else if (grade > 60)
                        list.set(i, "D");
                    else if (grade > 50)
                        list.set(i, "E");
                    else if (grade > 40)
                        list.set(i, "F");
                    else if (grade > 30)
                        list.set(i, "G");
                    else if (grade > 20)
                        list.set(i, "H");
                    else if (grade > 10)
                        list.set(i, "J");
                    else
                        list.set(i, "K");
                    list.set(i, list.get(i)+(i-4));
                }
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
