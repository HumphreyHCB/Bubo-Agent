import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboCache;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboDataReader;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboPrinter;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboWriter;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboMethodCache;
import jdk.graal.compiler.hotspot.meta.Bubo.BuboCompUnitCache;


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

        System.out.println("Starting Bubo Agent......");
        
        BuboCache timeCache = new BuboCache();
        timeCache.start();

        BuboMethodCache methodCache = new BuboMethodCache();
        methodCache.start();

        BuboCompUnitCache compunitCache = new BuboCompUnitCache();
        compunitCache.start();



        long startTime = System.currentTimeMillis();

        Thread writingHook = new Thread(() -> {
            System.out.println("Bubo Agent Joining......");
            try {
                timeCache.join();
                methodCache.join();
                compunitCache.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Bubo Agent Starting Printing......");
            // long endTime = System.currentTimeMillis();

            if (BuboMethodCache.pointer == 0) {
                System.out.println("Method Cache is empty, did you forget to enable the profiler");
                System.out.println("Add the follwoing command : -Dgraal.EnableProfiler=true ");
                boolean didSOmthing = false;
                for (int i = 0; i < 1000; i++) {
                    if (BuboCache.Buffer[i] > 0) {
                        didSOmthing = true;
                    }
                }
                if (didSOmthing) {
                    System.out.println("We did find at least one method entry in the raw cache; therefore, something was recorded, but we don't know which method it belongs to.");
                }
                
            }
            else{
             //BuboPrinter.printPercentageBar(BuboCache.Buffer, BuboMethodCache.getBuffer(), endTime - startTime );
             //BuboPrinter.printMultiBufferDebug(BuboCache.TimeBuffer,BuboCache.ActivationCountBuffer,BuboCache.CyclesBuffer, BuboMethodCache.getBuffer(), agentArgs);
             BuboPrinter.printCompUnit(BuboCache.TimeBuffer,BuboCache.ActivationCountBuffer,BuboCache.CyclesBuffer, BuboMethodCache.getBuffer(), compunitCache.Buffer); 
            }

            //BuboPrinter.addToFile("VisualVM Run Count : " + BuboMethodCache.getBuffer().size());
            System.out.println("Bubo Agent Sutting Down......");
    });
        Runtime.getRuntime().addShutdownHook(writingHook);



    }
}
