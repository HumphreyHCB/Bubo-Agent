package BuboCache;

import java.lang.Thread;

public class HumphreysCache extends Thread {
        
        public static long[] Buffer;
        public static int pointer;

        public HumphreysCache() {
                Buffer = new long[50];
                pointer = 0;

        }
        public static void dummyPrint(){
                System.out.println("In Humphrey Cache");
        }

        public static void add(long item)
        {
                Buffer[pointer] = item;
                pointer++;

        }

        public static void print(){

                for (long l : Buffer) {
                        System.out.print(l);
                }
        }


}
