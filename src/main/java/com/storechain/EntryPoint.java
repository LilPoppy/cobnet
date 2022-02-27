package com.storechain;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import com.storechain.interfaces.connection.NettyServerProvider;
import com.storechain.interfaces.spring.repository.UserGrantedAuthorityRepository;
import com.storechain.polyglot.PolyglotContext;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.entity.User;
import com.storechain.spring.boot.entity.UserGrantedAuthority;
import com.storechain.utils.DatabaseManager;
import com.storechain.utils.ScriptEngineManager;
import com.storechain.utils.SpringContext;

/**
 * @author lilpoppy  
 */
@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
public class EntryPoint {
	
	private final static String logo = "\n\n\n"
			+ "      _     _ _ ____                              \n"
			+ "     | |   (_) |  _ \\ ___  _ __  _ __  _   _     \n"
			+ "     | |   | | | |_) / _ \\| '_ \\| '_ \\| | | |  \n"
			+ "     | |___| | |  __/ (_) | |_) | |_) | |_| |     \n"
			+ "     |_____|_|_|_|   \\___/| .__/| .__/ \\__, |   \n"
			+ "==========================|_|===|_|====|___/======\n"
			+ "::  LilPoppy ::                                   \n"
			+ "                              ******\n"
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
			+ "   |               *000000*****************\n"
			+ "   |               *00000*****************\n"
			+ "   |               *0000*  **************\n"
			+ "*==-==*            *0000*     **********\n"
			+ "00   000*          *0000*         *****\n"
			+ "*~~~~~000***     **00000*\n"
			+ "*     0*00000***00000000*\n"
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
			+ "       *000000*0000000*00000000**8888\n"
			+ "        *000000*******000000000**888\n"
			+ "          *0000000000000000000**888\n"
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
    
    static void figlet() {
    	
    	PolyglotContext context = ScriptEngineManager.firstOrNewContext();
    	
    	ScriptEngineManager.evalFromResource(context, "python", "/figlet.py");
    	
    	System.out.println(ScriptEngineManager.execute(context, "python", "format", "LilPoppy").asString());
    }

    static void initialize(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

    	SpringApplication.run(EntryPoint.class, args);
    	
    	log.debug(logo);

		if(SpringContext.getNettyConfiguration().isEnable() && SpringContext.getNettyConfiguration().getServers() != null) {
			
			for(NettyConfiguration.ServerConfiguration serverConfig : SpringContext.getNettyConfiguration().getServers()) {

				Class<?> providerClass = serverConfig.getProvider();
				
				NettyServerProvider provider = (NettyServerProvider) providerClass.getConstructor().newInstance();

				provider.provide(serverConfig);
			}
		}
    }
    
    

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, IOException, NoSuchFieldException {
	
		initialize(args);
		var entity = new UserGrantedAuthority("admin");
		
		SpringContext.getPersistenceConfiguration().getAnotherRepo().save(entity);
		User user = new User("admin", "123456", entity);

		SpringContext.getPersistenceConfiguration().getRepo().save(user);
		
		DatabaseManager.commitJpaRepository(UserGrantedAuthorityRepository.class, new Consumer<UserGrantedAuthorityRepository>(){

			@Override
			public void accept(UserGrantedAuthorityRepository t) {
				t.save(entity);
			}
		});
		//DatabaseManager.getJpaRepository(UserGrantedAuthorityRepository.class).save(UserGrantedAuthority.fromData("Tester"));
		//DatabaseManager.getJpaRepository(UserGrantedAuthorityRepository.class).save(UserGrantedAuthority.fromData("Admin"));
		//DatabaseManager.getJpaRepository(UserGrantedAuthorityRepository.class).save(UserGrantedAuthority.fromData("User"));

		//DatabaseManager.getJpaRepository(UserAuthoritiesRepository.class).save(UserGrantedAuthorities.fromData(user, new ArrayList<>() {{
		//	this.add(UserGrantedAuthority.fromData("Tester"));
		//}}));

		//DatabaseManager.getJpaRepository(UserRepository.class).save(user);
		//figlet();
		benchGraalPolyglotContext();
	}
	

}

