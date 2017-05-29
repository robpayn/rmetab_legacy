package stream_metab.utils;

import java.io.*;

public class HTMLFactory {
    
    public static void openGeneric(BufferedWriter writer) throws IOException
    {
        writer.write("<HTML><HEAD></HEAD><BODY>");
        writer.newLine();
    }

    public static void closeGeneric(BufferedWriter writer) throws IOException
    {
        writer.write("</BODY></HTML>");
        writer.newLine();
    }

}
