package com.storechain;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import com.storechain.interfaces.connection.NettyServerProvider;
import com.storechain.interfaces.spring.repository.UserRepository;
import com.storechain.polyglot.PolyglotContext;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.RedisConfiguration;
import com.storechain.spring.boot.configuration.SystemConfiguration;
import com.storechain.spring.boot.entity.User;
import com.storechain.utils.DatabaseManager;
import com.storechain.utils.ScriptEngineManager;

/**
 * @author lilpoppy  
 */
@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
public class EntryPoint {
	
	private final static String logo = "\n"
			+ "                               ******\n"
			+ "                             *00000000*\n"
			+ "                          *000000000000*\n"
			+ "                         ==0000000000000*\n"
			+ "                      *0====000000000000*\n"
			+ "        ***************00====00000000000000*\n"
			+ "       ***000000000000000000==☆==000000*********\n"
			+ "      *0000000000000000000000==000000**********\n"
			+ "     *000000000000000000000000000000************\n"
			+ "     *0000000000000000000000000000*************\n"
			+ "   *000000000000000000000000000***************\n"
			+ "   *00000000000000000000000000****************\n"
			+ "   *00000000000000=0000000000*****************\n"
			+ "    *000000000000=0000000000******************\n"
			+ "     ============0000000000*******************\n"
			+ "      *0000000000000000000*******************\n"
			+ "         ******00000000000******************\n"
			+ "                 *00000000******************\n"
			+ "                   *000000*****************\n"
			+ "                   *00000*****************\n"
			+ "                   *0000*  **************\n"
			+ "*******            *0000*     **********\n"
			+ "*0000000*          *0000*         *****\n"
			+ "*00000000***     **00000*\n"
			+ "*000000*00000***00000000*\n"
			+ "******)*****00000000000*\n"
			+ "         *0000000000000*\n"
			+ "        *0000000*000*000*\n"
			+ "       *00000000*000*0000*\n"
			+ "       *00000000*000*00000*\n"
			+ "      *000000000*000*0000000*\n"
			+ "     *0000000000*000*00000000*\n"
			+ "    *00000000000*000*000000000*\n"
			+ "    *00000000000*000*0000000000*\n"
			+ "    *00000000000*000*00000000000*\n"
			+ "     *000000000*00000*000000000*\n"
			+ "      *0000000*0000000*00000000**\n"
			+ "       *000000*0000000*00000000**888\n"
			+ "        *000000*******000000000**88\n"
			+ "          *0000000000000000000**8\n"
			+ "            *0000000000000000*\n"
			+ "             *000**********000*\n"
			+ "  ****      *000*          *0000*\n"
			+ " *0000******000*           *000000*\n"
			+ " *0000000000000*         *000000000*\n"
			+ "  *000000000000*        *00000000*\n"
			+ "    *0000000000*       *0000000*\n"
			+ "      *********        *00000*\n"
			+ "                        ****";

	private final static Logger log = LoggerFactory.getLogger(EntryPoint.class);
	
	public static ServletWebServerApplicationContext CONTEXT;
	
	public static NettyConfiguration NETTY_CONFIG;
	
	public static SystemConfiguration SYSTEM_CONFIG;
	
	public static RedisConfiguration REDIS_CONFIG;

    static long benchGraalPolyglotContext() throws IOException {
    	
        System.out.println("=== Graal.js via org.graalvm.polyglot.Context === ");
        
        long sum = 0;
        
    	PolyglotContext context = ScriptEngineManager.firstOrNewContext();
    	
    	ScriptEngineManager.evalFromResource(context, "js", "/src.js");
        
        System.out.println("warming up ...");
        
        for (int i = 0; i < 15; i++) {
        	
        	ScriptEngineManager.execute(context, "js", "primesMain");
        }
        
        System.out.println("warmup finished, now measuring");
        
        for (int i = 0; i < 10; i++) {
        	
            long start = System.currentTimeMillis();
            ScriptEngineManager.execute(context, "js", "primesMain");
            long took = System.currentTimeMillis() - start;
            sum += took;
            System.out.println("iteration: " + took);
        }

		System.out.println(String.format("installed guest languages: %s ", ScriptEngineManager.getAvailableLanguages()));
		
		System.out.println(ScriptEngineManager.getContexts());
		
        return sum;	
        
    }

    static void initialize(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    	
    	log.debug(logo);
		EntryPoint.CONTEXT = (ServletWebServerApplicationContext) SpringApplication.run(EntryPoint.class, args);
		EntryPoint.NETTY_CONFIG = EntryPoint.CONTEXT.getBean(NettyConfiguration.class);
		EntryPoint.SYSTEM_CONFIG = EntryPoint.CONTEXT.getBean(SystemConfiguration.class);
		EntryPoint.REDIS_CONFIG = EntryPoint.CONTEXT.getBean(RedisConfiguration.class);
		
		if(NETTY_CONFIG.isEnable() && NETTY_CONFIG.getServers() != null) {
			
			for(NettyConfiguration.ServerConfiguration serverConfig : NETTY_CONFIG.getServers()) {

				Class<?> providerClass = serverConfig.getProvider();
				
				NettyServerProvider provider = (NettyServerProvider) providerClass.getConstructor().newInstance();

				provider.provide(serverConfig);
			}
		}
    }
    

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, IOException, NoSuchFieldException {
	
		initialize(args);
		//DatabaseManager.getJpaRepository(UserRepository.class).save(User.fromData("admin", "123456", new ArrayList<>(), false, false, true, true));
		benchGraalPolyglotContext();
	}
	

}

