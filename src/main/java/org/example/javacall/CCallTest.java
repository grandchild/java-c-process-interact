/* CC0 - free software.
To the extent possible under law, all copyright and related or neighboring
rights to this work are waived. See the LICENSE file for more information. */
package org.example.javacall;

import java.lang.Runtime;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.lang.Thread;
// import com.sun.jna.Library;
// import com.sun.jna.Native;


public class CCallTest {
    public final String pauseStr = "pause";
    public final String resumeStr = "resume";
    
    public static void main(String[] args) {
        CCallTest a = new CCallTest();
        String cProgram = "";
        if(args.length >= 1) {
            System.out.println("Using custom binary path");
            cProgram = args[0];
        } else if(System.getProperty("os.name") == "Linux") {
            cProgram = "build/exe/main/linux/main";
        } else {
            cProgram = "build/exe/main/windows/main.exe";
        }
        ProcessBuilder pb = new ProcessBuilder(cProgram);
        pb.redirectErrorStream(true);
        Process p;
        try {
            p = pb.start();
        } catch(Exception err) {
            err.printStackTrace();
            return;
        }
        Thread inThread = new Thread(() -> {
            try(BufferedWriter stdin = new BufferedWriter(
                    new OutputStreamWriter(p.getOutputStream()))) {
                TimeUnit.SECONDS.sleep(5);
                System.out.println("J: Sending pause");
                stdin.write(a.pauseStr);
                // stdin.newLine();  // optional, is not checked in C
                stdin.flush();
                TimeUnit.SECONDS.sleep(2);
                System.out.println("J: Sending resume");
                stdin.write(a.resumeStr);
                // stdin.newLine();  // optional, is not checked in C
                stdin.flush();
                TimeUnit.SECONDS.sleep(2);
                System.out.println("J: Closing stdin stream");
                stdin.close();
            } catch(Exception err) {
                err.printStackTrace();
            }
        });
        Thread outThread = new Thread(() -> {
            try(BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {
                String line;
                while((line=stdout.readLine()) != null) {
                    System.out.println(line);
                }
            } catch(Exception err) {
                err.printStackTrace();
            }
        });
        inThread.start();
        outThread.start();
        try {
            inThread.join();
            outThread.join();
        } catch(InterruptedException err) {
            err.printStackTrace();
        }
    }
    
    
    /* JNA code for signals. Might work on Linux with some hacks. Need to get
     * PID somehow. */
    
    // private final int SIGTSTP = 20;
    // private final int SIGCONT = 18;
    
    // public interface PosixIPC extends Library {
    //     PosixIPC INSTANCE = (PosixIPC) Native.loadLibrary("c", PosixIPC.class);
    //     void kill(int pid, int signal);
    // }
    
    // private void pause() {
    //     PosixIPC posix = (PosixIPC) Native.loadLibrary("c", PosixIPC.class);
    //     posix.kill(this.id, this.SIGTSTP);
    // }
    
    // private void resume() {
    //     PosixIPC posix = (PosixIPC) Native.loadLibrary("c", PosixIPC.class);
    //     posix.kill(this.id, this.SIGCONT);
    // }
}
