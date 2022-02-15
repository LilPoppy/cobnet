package com.storechain;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import com.storechain.interfaces.spring.connection.NettyServerProvider;
import com.storechain.polyglot.PolyglotContext;
import com.storechain.polyglot.PolyglotSource;
import com.storechain.polyglot.PolyglotValue;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.SystemConfiguration;
import com.storechain.utils.ScriptEngineManager;


/**
 * ))))))
   ))))))
   ))))))                          ******
   ))))))                        *00000000*
   ))))))                     *000000000000*
   ))))))                    ==0000000000000*
   ))))))                 *0====000000000000*
   ))))))  ***************00====00000000000000*
=====))***000000000000000000====000000*********
======*0000000000000000000000==000000**********
======000000000000000000000000000000************
 ====)*0000000000000000000000000000*************
   )))*000000000000000000000000000***************
   )))*00000000000000000000000000****************
   )))*00000000000000=0000000000*****************
   ))))*000000000000=0000000000******************
   )))))============0000000000*******************
   ))))))*0000000000000000000*******************
   ))))))   ******00000000000******************
   ))))))           *00000000******************
   ))))))             *000000*****************
   ))))))             *00000*****************
   ))))))             *0000*  **************
  *******            *0000*     **********
 *0000000*          *0000*         *****
 *00000000***     **00000*
 *000000*00000***00000000*
  ******)*****00000000000*
   ))))))   *0000000000000*
   ))))))  *0000000*000*000*
   )))))) *00000000*000*0000*
   )))))) *00000000*000*00000*
         *000000000*000*0000000*
        *0000000000*000*00000000*
       *00000000000*000*000000000*
       *00000000000*000*0000000000*
       *00000000000*000*00000000000*
        *000000000*00000*000000000*
         *0000000*0000000*00000000*
          *000000*0000000*00000000*
           *000000*******000000000*
             *0000000000000000000*
               *0000000000000000*
                *000**********000*
     ****      *000*          *0000*
    *0000******000*           *000000*
    *0000000000000*         *000000000*
     *000000000000*        *00000000*
       *0000000000*       *0000000*
         *********        *00000*
                           ****
 */

/**
 * @author lilpoppy  
 */
//@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
public class EntryPoint {

	private final static Logger log = LoggerFactory.getLogger(EntryPoint.class);
	
	public static ServletWebServerApplicationContext CONTEXT;
	
	public static NettyConfiguration NETTY_CONFIG;
	
	public static SystemConfiguration SYSTEM_CONFIG;

    static long benchGraalPolyglotContext() throws IOException {
    	
        System.out.println("=== Graal.js via org.graalvm.polyglot.Context === ");
        
        long sum = 0;
        
        	PolyglotContext context = ScriptEngineManager.firstOrNewContext();
        	
            context.eval(PolyglotSource.readFromResource("js", "/src.js"));
            
            PolyglotValue primesMain = context.getBindings("js").getMember("primesMain");
            System.out.println("warming up ...");
            for (int i = 0; i < 15; i++) {
                primesMain.execute();
            }
            System.out.println("warmup finished, now measuring");
            for (int i = 0; i < 10; i++) {
                long start = System.currentTimeMillis();
                primesMain.execute();
                long took = System.currentTimeMillis() - start;
                sum += took;
                System.out.println("iteration: " + took);
            }

            return sum;	
        
    }

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, IOException {

		benchGraalPolyglotContext();
		
		EntryPoint.CONTEXT = (ServletWebServerApplicationContext) SpringApplication.run(EntryPoint.class, args);
		EntryPoint.NETTY_CONFIG = EntryPoint.CONTEXT.getBean(NettyConfiguration.class);
		EntryPoint.SYSTEM_CONFIG = EntryPoint.CONTEXT.getBean(SystemConfiguration.class);

		if(NETTY_CONFIG.isEnable() && NETTY_CONFIG.getServers() != null) {
			
			for(NettyConfiguration.ServerConfiguration serverConfig : NETTY_CONFIG.getServers()) {

				Class<?> provider_class = serverConfig.getProvider();
				
				NettyServerProvider provider = (NettyServerProvider) provider_class.getConstructor().newInstance();

				provider.provide(serverConfig);
			}
		}
	
		benchGraalPolyglotContext();	
		
		System.out.println(String.format("installed scripts: %s ", ScriptEngineManager.getAvailableLanguages()));
		
		System.out.println(ScriptEngineManager.getContexts());
		
		
		System.out.println(ScriptEngineManager.getContexts().get(0).getBindings("js").getMemberKeys());
	}
}

