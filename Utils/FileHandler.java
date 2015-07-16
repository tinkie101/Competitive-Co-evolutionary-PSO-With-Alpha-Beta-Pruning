package Utils;

import java.io.*;

/**
 * Created by tinkie101 on 2015/02/28.
 */
public class FileHandler {
    public static void writeFile(String fileName, String content) throws IOException {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write(content);
            out.close();
        } catch (IOException e) {
            throw e;
        }
    }

    public static String readFile(String fileName) throws Exception
    {
        BufferedReader in = new BufferedReader(new FileReader(fileName));

        StringBuilder builder = new StringBuilder();


        String temp = in.readLine();
        boolean first = true;
        while(temp != null )
        {
            if(first){
                builder.append(temp);
            }
            else
                builder.append("\n" + temp);

            temp = in.readLine();
        }
        return builder.toString();
    }
}
