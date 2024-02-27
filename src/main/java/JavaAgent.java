import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboCache;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboDataReader;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboPrinter;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboWriter;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboMethodCache;


public class JavaAgent {
    private static final Logger log = Logger.getLogger(JavaAgent.class.getName());
    /**
     * As soon as the JVM initializes, This  method will be called.
     * Configs for intercepting will be read and added to Transformer so that Transformer will intercept when the
     * corresponding Java Class and Method is loaded.
     *
     * @param agentArgs       The list of agent arguments
     * @param instrumentation The instrumentation object
     * @throws InstantiationException While  an instantiation of object cause an error.
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) throws InstantiationException {

        log.info("Starting Bubo Agent......");
        
        BuboCache timeCache = new BuboCache();
        timeCache.start();
        
        BuboMethodCache methodCache = new BuboMethodCache();
        methodCache.start();

        long startTime = System.currentTimeMillis();
        // Thread printingHook = new Thread(() -> BuboCache.print());
        // Runtime.getRuntime().addShutdownHook(printingHook);

        Thread writingHook = new Thread(() -> {
            try {
                timeCache.join();
                methodCache.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            long endTime = System.currentTimeMillis();
            //System.out.println("Starting Printing ... ( May take a few seconds)");
            //System.out.println("Debug :");
            //System.out.println("Buffer Pointer :" + BuboCache.BufferPointer);
            //System.out.println("Pointer :" + BuboCache.pointer);
            // System.out.println("The value in the array at 1 is :" + BuboCache.Buffer[1]);
            // System.out.println("The value in the array at 2 is :" + BuboCache.Buffer[2]);
            // System.out.println("We have made the aray be and INt, so PRITNING IS oFFF");
            // if (BuboMethodCache.pointer == 0) {
            //     System.out.println("Method Cache is empty, did you forget to enable the profiler");
            //     System.out.println("Add the follwoing command : -Dgraal.EnableProfiler=true ");
            // }
            // else{
             BuboPrinter.printPercentageBar(BuboCache.Buffer, BuboMethodCache.getBuffer(), endTime - startTime );
            // }
            //String filename = "out.txt";
            //BuboDataReader.DumpToFile(BuboCache.BufferArray, BuboCache.pointer, BuboCache.BufferPointer, filename);
            //BuboPrinter.printPercentageBar(BuboPrinter.orderDataByTime(BuboDataReader.convertToHashMap(BuboMethodCache.BufferArray, BuboMethodCache.pointer, BuboMethodCache.BufferPointer)), BuboMethodCache.getBuffer());
            //System.out.println("Bubo Agent Sutting Down......");
    });
        Runtime.getRuntime().addShutdownHook(writingHook);



        //InterceptingClassTransformer interceptingClassTransformer = new InterceptingClassTransformer();
        //interceptingClassTransformer.init();
        //instrumentation.addTransformer(interceptingClassTransformer);
    }
}
