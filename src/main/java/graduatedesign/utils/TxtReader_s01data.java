package graduatedesign.utils;

import java.io.*;
import java.util.List;

/**
 * Created by ua28 on 5/14/20.
 */
public class TxtReader_s01data {

    public static void main(String[] args) throws IOException {
        List<List<String>> data = new Preprocessing().parseStudentPerformance(Preprocessing.class.getClassLoader().getResource("studentperformance.csv").getPath());


        String path = TxtReader_s01data.class.getClassLoader().getResource("").getPath()+"sp.txt";
        File file = new File(path);
        //如果文件不存在，则自动生成文件；
        if(!file.exists()){
            file.createNewFile();
        }

        int num = 1;
        //引入输出流
        OutputStream outPutStream;
        try{
            outPutStream = new FileOutputStream(file);
            StringBuilder stringBuilder = new StringBuilder();//使用长度可变的字符串对象；
            //TODO 这里写你的代码逻辑;
            for (List<String> line : data) {
                String tmp = ""+num+++"\t"+line.toString().substring(1, line.toString().length()-1)+"\n";
                tmp = tmp.replaceAll(", ", "\t");
                stringBuilder.append(tmp);
            }

            String context = stringBuilder.toString();//将可变字符串变为固定长度的字符串，方便下面的转码；
            byte[]  bytes = context.getBytes("UTF-8");//因为中文可能会乱码，这里使用了转码，转成UTF-8；
            outPutStream.write(bytes);//开始写入内容到文件；
            outPutStream.close();//一定要关闭输出流；
        }catch(Exception e){
            e.printStackTrace();//获取异常
        }
    }

}
