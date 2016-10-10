
//import org.apache.log4j.Logger;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ru.mtt.webapi.core.IConfigurableObject;
import ru.mtt.webapi.core.XConfigurableObject;

/**
 * User: Nasibullin Rustem
 * Date: 22.03.2011 12:17:03
 */
public class Launcher
{
    public static final String SHUTDOWN_COMMAND = "shutdown";
    static  LauncherLogger logger = new LauncherLogger();

    public static void main2(String... args) throws Exception
    {

        if (args.length == 0)
        {
            logger.info("args.length = 0");
            return;
        }



        java.lang.Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.info ("Uncaught Exception: "+t.getName()+":"+e.getMessage()+"/"+e.getCause());
                e.printStackTrace();
            }
        });


        Class<?> nserver = Class.forName("ru.mtt.webapi.core.WebApiContextsLauncher");
        Class[] argTypes = new Class[]{String[].class};
        Method main = nserver.getDeclaredMethod("main", argTypes);


        logger.info("Loading libraries finishing \n________________________________");

        
        
        if (SHUTDOWN_COMMAND.equals(args[0]))
            main.invoke(null, (Object) args);
        else
        {
            String[] nserverArgs = new String[args.length - 1];
            System.arraycopy(args, 1, nserverArgs, 0, nserverArgs.length);

            try {
              main.invoke(null, (Object) nserverArgs);
            } catch (Throwable ee) {
              ee.printStackTrace();
            }
        }
    }


    public static void main(String... args) throws Exception
    {

        if (args.length == 0)
        {
            logger.info("args.length = 0");
            return;
        }



        java.lang.Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.info ("Uncaught Exception: "+t.getName()+":"+e.getMessage()+"/"+e.getCause());
                e.printStackTrace();
            }
        });



        List<URL> urls = new ArrayList<URL>();
        urls.add(new URL("file:lib/"));
        if (!SHUTDOWN_COMMAND.equals(args[0])) XConfigurableObject._POSTFIX = args[0];
        urls.add(new URL("file:lib/logging/" + args[0] + "/"));

        logger.info("Loading libraries\n________________________________");


        File f = f = new File("lib");
        for (String s : f.list())
            if (s.endsWith(".jar"))
            {
                urls.add(new URL("file:lib/" + s));
                logger.info(new URL("file:lib/" + s));
            }

        URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[]{}));
        Thread.currentThread().setContextClassLoader(loader);
        Class<?> nserver = loader.loadClass("ru.mtt.webapi.core.WebApiContextsLauncher");
        Class[] argTypes = new Class[]{String[].class};
        Method main = nserver.getDeclaredMethod("main", argTypes);


        logger.info("Loading libraries finishing \n________________________________");


        if (SHUTDOWN_COMMAND.equals(args[0]))
            main.invoke(null, (Object) args);
            
        else
        {
            String[] nserverArgs = new String[args.length - 1];
            System.arraycopy(args, 1, nserverArgs, 0, nserverArgs.length);

            try {
              main.invoke(null, (Object) nserverArgs);
            } catch (Throwable ee) {
              ee.printStackTrace();
            }
        }
    }

    
    static class LauncherLogger {
        
        
        public void info (Object data) {
            
               System.out.println (">: "+data);
            
        }
        
        
        
    }
    
    
    
}
