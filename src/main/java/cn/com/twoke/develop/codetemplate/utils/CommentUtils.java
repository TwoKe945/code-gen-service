package cn.com.twoke.develop.codetemplate.utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class CommentUtils {
    /**
     * 构建类上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException
     */
    public static BufferedWriter buildClassComment(BufferedWriter bw, String text) throws IOException {
        bw.newLine();
        bw.newLine();
        bw.write("/**");
        bw.newLine();
        bw.write(" * " + text);
        bw.newLine();
        bw.write(" */");
        return bw;
    }

    public static BufferedWriter buildPropertyComment(BufferedWriter bw, String text) throws IOException {
        bw.newLine();
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * " + text);
        bw.newLine();
        bw.write("\t */");
        return bw;
    }

    /**
     * 构建方法上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException
     */
    public static StringBuilder buildMethodComment(StringBuilder bw, String text){
        bw.append("\n");
        bw.append("\t/**");
        bw.append("\n");
        bw.append("\t * " + text);
        bw.append("\n");
        bw.append("\t */");
        return bw;
    }
}
