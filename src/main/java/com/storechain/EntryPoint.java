package com.storechain;


import com.storechain.common.MultiwayTreeNode;
import com.storechain.interfaces.connection.NettyServerProvider;
import com.storechain.polyglot.PolyglotContext;
import com.storechain.security.RoleRule;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.entity.User;
import com.storechain.spring.boot.entity.UserPermission;
import com.storechain.spring.boot.entity.UserRole;
import com.storechain.utils.DatabaseManager;
import com.storechain.utils.ScriptEngineManager;
import com.storechain.utils.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * @author lilpoppy  
 */
@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
//@EnableAutoConfiguration(exclude = { WebMvcAutoConfiguration.class })
public class EntryPoint {
	
	
	private final static String LOGO = "Spring Application has been loaded successfully.\n\n\n"
			+ "      _     _ _ ____             |{$project.name}|\n"
			+ "     | |   (_) |  _ \\ ___  _ __  _ __  _   _     \n"
			+ "     | |   | | | |_) / _ \\| '_ \\| '_ \\| | | |  \n"
			+ "     | |___| | |  __/ (_) | |_) | |_) | |_| |     \n"
			+ "     |_____|_|_|_|   \\___/| .__/| .__/ \\__, |   \n"
			+ "==========================|_|===|_|====|___/======\n"
			+ ":: LilPoppy ::                ({$project.version})\n"
			+ "                              *******\n"
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
			+ "   /             *00000000******************\n"
			+ "   |               *000000*****************\n"
			+ "   |               *00000*****************\n"
			+ "*==-==*            *0000*  **************\n"
			+ "*~~~~~*            *0000*     **********\n"
			+ "00   000*          *0000*         *****\n"
			+ "*     000***     **00000*\n"
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

    public static String getLogo() {
		
		String[] array = EntryPoint.LOGO.split("\n");
		
		String name = SpringContext.getProjectInformationConfiguration().getName();

		String version = SpringContext.getProjectInformationConfiguration().getVersion();
		
		StringBuilder sb = new StringBuilder();
		
		String[] adjust = {"{$project.name}", "{$project.version}"};
		
		int index = 0;
		
		lineloop:
		for(int i = 0; i < array.length; i++) {
			
			String line = array[i];
			
			for(int j = index; j < adjust.length; j++) {
				
				String key = adjust[j];
				
				int posStart = line.indexOf(key);
				int posEnd = posStart + key.length();
				int length = line.length();
				
				if(posStart > -1) {
					
					String format = null;
					
					switch(index) {
					case 0:
						format = name;
						break;
					case 1:
						format = version;
						break;
					}
					
					if(format != null) {
						
						StringBuilder builder = new StringBuilder(line).delete(posStart, posEnd).insert(posStart, format);

						
						if(builder.length() > length) {
							
							int n = posStart - 1 - (builder.length() - length);
							
							switch(index) {
							case 0:
								n -= 8;
								break;
							}
							
							builder.delete(n, posStart - 1);
							
						} else {
							
							for(int k = 0; k < Math.abs(format.length() - key.length()); k++) {
								
								builder.insert(posStart - 1, " ");
							}
						}
						
						builder.append("\n");
						
						sb.append(builder);
						
						index++;
						
						continue lineloop;
					}
				}
			}
			
			sb.append(line).append("\n");
			
		}
		
		return sb.toString();
	}
	
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
    	
    	log.info(getLogo());

		if(SpringContext.getNettyConfiguration().isEnable() && SpringContext.getNettyConfiguration().getServers() != null) {
			
			for(NettyConfiguration.ServerConfiguration serverConfig : SpringContext.getNettyConfiguration().getServers()) {

				Class<?> providerClass = serverConfig.getProvider();
				
				NettyServerProvider provider = (NettyServerProvider) providerClass.getConstructor().newInstance();

				provider.provide(serverConfig);
			}
		}
    }
    

//	@Bean
//	public BeanPostProcessor beanPostProcessor() {
//
//		return new BeanPostProcessor() {
//
//			@Override
//		    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//
//				System.out.println("** " + beanName + " ** (" + bean.getClass().getName() + ") post process after initialization," );
//
//		        return bean;
//		    }
//
//		    @Override
//		    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//
//				System.out.println("** " + beanName + " ** (" + bean.getClass().getName() + ") post process before initialization," );
//
//		        return bean;
//		    }
//		};
//	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, IOException, NoSuchFieldException {

		initialize(args);

		MultiwayTreeNode.from("test.aaa").bfs((t, index) -> {
			System.out.println(t.getValue());
			return true;
		});
		UserPermission[] permissions = new UserPermission[1000];
		
		for(int i = 0 ; i < 1000; i++) {
			
			permissions[i] = new UserPermission("test." + i);
		}
		
		User user = new User("admin", "123456", new UserRole("admin", RoleRule.ADMIN, new UserPermission("admin.read.test"), new UserPermission("user.op"), new UserPermission("user.read.lm"), new UserPermission("user.test")));

		
		DatabaseManager.getUserRepository().save(user);



		//DatabaseManager.getJpaRepository(UserRepository.class).save(user);
		//figlet();
		//benchGraalPolyglotContext();
        
        long start = System.currentTimeMillis();
        
		for(int i = 0; i < 1; i++) {

			System.out.println(user.isPermitted("user.read.lm"));
		}
		
        long took = System.currentTimeMillis() - start;
        
        System.out.println("took:" + took);

		if(Arrays.stream(args).anyMatch(arg -> arg.toUpperCase().equals("-DCI"))) {
			
			System.exit(0);
		}
		
	}
	
}

