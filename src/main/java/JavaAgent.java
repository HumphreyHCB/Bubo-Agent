import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;
import org.graalvm.compiler.hotspot.meta.Bubo.BuboCache;
import org.graalvm.compiler.hotspot.meta.Bubo.BuboDataReader;
import org.graalvm.compiler.hotspot.meta.Bubo.BuboPrinter;
import org.graalvm.compiler.hotspot.meta.Bubo.BuboWriter;
import org.graalvm.compiler.hotspot.meta.Bubo.BuboMethodCache;


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
        
        BuboMethodCache methodCache= new BuboMethodCache();
        methodCache.start();
        // Thread printingHook = new Thread(() -> BuboCache.print());
        // Runtime.getRuntime().addShutdownHook(printingHook);

        Thread writingHook = new Thread(() -> {
            System.out.println("Starting Printing ... ( May take a few seconds)");
            System.out.println("Debug :");
            System.out.println("Buffer Pointer :" + timeCache.BufferPointer);
            System.out.println("Pointer :" + timeCache.pointer);
            BuboPrinter.printPercentageBar(BuboPrinter.orderDataByTime(BuboDataReader.convertToHashMap(timeCache.BufferArray, timeCache.pointer, timeCache.BufferPointer)), methodCache.getBuffer());
            System.out.println("Bubo Agent Sutting Down......");
    });
        Runtime.getRuntime().addShutdownHook(writingHook);



        //InterceptingClassTransformer interceptingClassTransformer = new InterceptingClassTransformer();
        //interceptingClassTransformer.init();
        //instrumentation.addTransformer(interceptingClassTransformer);
    }
}
