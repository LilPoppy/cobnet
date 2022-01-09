package com.storechain;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import com.storechain.interfaces.spring.connection.NettyServerProvider;
import com.storechain.polyglot.PolyglotContext;
import com.storechain.polyglot.PolyglotValue;
import com.storechain.spring.boot.configuration.NettyConfiguration;
import com.storechain.spring.boot.configuration.SystemConfiguration;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

//@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
public class EntryPoint {

	private final static Logger log = LoggerFactory.getLogger(EntryPoint.class);
	
	public static ServletWebServerApplicationContext CONTEXT;
	
	public static NettyConfiguration NETTY_CONFIG;
	
	public static SystemConfiguration SYSTEM_CONFIG;
	
    public static final int WARMUP = 15;
    public static final int ITERATIONS = 10;
    public static final String BENCHFILE = "src/bench.js";
    
    public static final String SOURCE2 = "function hello(name) {print ('Hello, ' + name);}";

    public static final String SOURCE = ""
            + "var N = 2000;\n"
            + "var EXPECTED = 17393;\n"
            + "\n"
            + "function Natural() {\n"
            + "    x = 2;\n"
            + "    return {\n"
            + "        'next' : function() { return x++; }\n"
            + "    };\n"
            + "}\n"
            + "\n"
            + "function Filter(number, filter) {\n"
            + "    var self = this;\n"
            + "    this.number = number;\n"
            + "    this.filter = filter;\n"
            + "    this.accept = function(n) {\n"
            + "      var filter = self;\n"
            + "      for (;;) {\n"
            + "          if (n % filter.number === 0) {\n"
            + "              return false;\n"
            + "          }\n"
            + "          filter = filter.filter;\n"
            + "          if (filter === null) {\n"
            + "              break;\n"
            + "          }\n"
            + "      }\n"
            + "      return true;\n"
            + "    };\n"
            + "    return this;\n"
            + "}\n"
            + "\n"
            + "function Primes(natural) {\n"
            + "    var self = this;\n"
            + "    this.natural = natural;\n"
            + "    this.filter = null;\n"
            + "\n"
            + "    this.next = function() {\n"
            + "        for (;;) {\n"
            + "            var n = self.natural.next();\n"
            + "            if (self.filter === null || self.filter.accept(n)) {\n"
            + "                self.filter = new Filter(n, self.filter);\n"
            + "                return n;\n"
            + "            }\n"
            + "        }\n"
            + "    };\n"
            + "}\n"
            + "\n"
            + "function primesMain() {\n"
            + "    var primes = new Primes(Natural());\n"
            + "    var primArray = [];\n"
            + "    for (var i=0;i<=N;i++) { primArray.push(primes.next()); }\n"
            + "    if (primArray[N] != EXPECTED) { throw new Error('wrong prime found: '+primArray[N]); }\n"
            + "}\n";
    
    private static long benchScriptEngineIntl(ScriptEngine eng) throws IOException {
        long sum = 0L;
        try {
            eng.eval(SOURCE);
            Invocable inv = (Invocable) eng;
            System.out.println("warming up ...");
            for (int i = 0; i < WARMUP; i++) {
                inv.invokeFunction("primesMain");
            }
            System.out.println("warmup finished, now measuring");
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.currentTimeMillis();
                inv.invokeFunction("primesMain");
                long took = System.currentTimeMillis() - start;
                sum += took;
                System.out.println("iteration: " + (took));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return sum;
    }
    
    static long benchGraalPolyglotContext() throws IOException {
    	
    	/**
    	
    	------ read file from the class path ----------
    	
        String scriptText;
        
        try (Scanner scanner = new Scanner(
                EntryPoint.class.getResourceAsStream("/demo.js"), StandardCharsets.UTF_8)) {
            scriptText = scanner.useDelimiter("\\A").next();
        }
        
        try(PolyglotContext context = PolyglotContext.create("js")) {
        
        	context.eval("js", scriptText);
        	
        	PolyglotValue function = context.getBindings("js").getMember("method");
        	
        	PolyglotValue result = function.execute(arguments);
        }
        
        ------------------------------------------------
        **/

        System.out.println("=== Graal.js via org.graalvm.polyglot.Context === ");
        
        long sum = 0;
        
        try(PolyglotContext context = PolyglotContext.create()) {
        	
            System.out.println(String.format("installed scripts with %s: ", context));
            
        	for(String key : context.getEngine().getLanguages().keySet()) {
        		System.out.println(key);
        	}
            context.eval(Source.newBuilder("js", SOURCE, "src.js").build());
            PolyglotValue primesMain = context.getBindings("js").getMember("primesMain");
            System.out.println("warming up ...");
            for (int i = 0; i < WARMUP; i++) {
                primesMain.execute();
            }
            System.out.println("warmup finished, now measuring");
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.currentTimeMillis();
                primesMain.execute();
                long took = System.currentTimeMillis() - start;
                sum += took;
                System.out.println("iteration: " + took);
            }

            return sum;	
        }
    }

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, IOException {

		benchGraalPolyglotContext();
		
		EntryPoint.CONTEXT = (ServletWebServerApplicationContext) SpringApplication.run(EntryPoint.class, args);
		System.out.println(Context.create().getEngine().getLanguages().keySet().toString());
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
	}
}

