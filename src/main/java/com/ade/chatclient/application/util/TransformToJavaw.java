package com.ade.chatclient.application.util;

import java.io.*;

public class TransformToJavaw {
    public static void main(String[] args) throws IOException {
        String srcFileName = args[0] + ".bat", outFileName = args[0] + "w.bat";
        try (
                BufferedReader br = new BufferedReader(new FileReader(srcFileName));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName))
        ) {
            for (int i = 0; i < 3; i++) {
                bw.write(br.readLine() + "\n");
            }
            bw.write("start \"\" \"%DIR%\\javaw\"");
            for (int i = 0; i < 12; i++) {
                br.read();
            }
            bw.write(br.readLine() + " && exit 0\n");
        }
    }
}
