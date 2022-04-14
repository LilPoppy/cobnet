package com.cobnet;

import com.cobnet.common.ImageUtils;
import com.cobnet.connection.websocket.WebSocketServer;
import com.cobnet.security.RoleRule;
import com.cobnet.security.permission.UserPermission;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Address;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
@EnableAspectJAutoProxy(proxyTargetClass = false)
public class EntryPoint {

	private final static Logger LOG = LoggerFactory.getLogger(EntryPoint.class);

	private final static String LOGO = """
	Spring Application has been loaded successfully.
			      _     _ _ ____              |{$project.name}|
			     | |   (_) |  _ \\ ___  _ __  _ __  _   _    \s
			     | |   | | | |_) / _ \\| '_ \\| '_ \\| | | | \s
			     | |___| | |  __/ (_) | |_) | |_) | |_| |    \s
			     |_____|_|_|_|   \\___/| .__/| .__/ \\__, |  \s
			==========================|_|===|_|====|___/======
			:: LilPoppy ::                 ({$project.version})
			                              *******
			                             *00000000*
			                          *000000000000*
			                         ==0000000000000*
			                      *0====000000000000*
			        ***************00====00000000000000*
			       ***000000000000000000==☆==000000*********
			      *0000000000000000000000==000000**********
			     *000000000000000000000000000000************
			     *0000000000000000000000000000*************
			   *000000000000000000000000000***************
			   *00000000000000000000000000****************
			   *00000000000000=0000000000*****************
			    *000000000000=0000000000******************
			     ============0000000000*******************
			      *0000000000000000000*******************
			         ******00000000000******************
			   /             *00000000******************
			   |               *000000*****************
			   |               *00000*****************
			*==-==*            *0000*  **************
			*~~~~~*            *0000*     **********
			00   000*          *0000*         *****
			*     000***     **00000*
			*     0*00000***00000000*
			******)*****00000000000*
			         *0000000000000*
			        *0000000*000*000*
			       *00000000*000*0000*
			       *00000000*000*00000*
			      *000000000*000*0000000*
			     *0000000000*000*00000000*
			    *00000000000*000*000000000*
			    *00000000000*000*0000000000*
			    *00000000000*000*00000000000*
			     *000000000*00000*000000000*
			      *0000000*0000000*00000000**
			       *000000*0000000*00000000**8888
			        *000000*******000000000**888
			          *0000000000000000000**888
			            *0000000000000000*
			             *000**********000*
			  ****      *000*          *0000*
			 *0000******000*           *000000*
			 *0000000000000*         *000000000*
			  *000000000000*        *00000000*
			    *0000000000*       *0000000*
			      *********        *00000*
			                        ****
			""";

	public static void main(String[] args) throws IOException {

		SpringApplication.run(EntryPoint.class, args);

		if(ProjectBeanHolder.getCacheConfiguration().isStartClear()) {

			clearCaches();
		}

		LOG.info(EntryPoint.getLogo());


		User user = new User("admin", "123456", "Bob", "Smith", new Address.Builder().setStreet("1 Heaven Street").build(), new UserRole("admin", RoleRule.ADMIN, false, new UserPermission("admin.read.test"), new UserPermission("user.op"), new UserPermission("user.read.lm"), new UserPermission("user.test")));

		ProjectBeanHolder.getUserRoleRepository().save(new UserRole("user", RoleRule.USER, true));
		ProjectBeanHolder.getUserRepository().save(user);

		if(Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("agent"))) {

			System.exit(0);
		}
	}

	@Bean
	public WebSocketServer webSocketServer() {

		return (WebSocketServer) new WebSocketServer("web-socket").bind(8090);
	}

	private static void clearCaches() {

		Collection<String> cacheNames = ProjectBeanHolder.getRedisCacheManager().getCacheNames();

		for(String name : cacheNames) {

			Objects.requireNonNull(ProjectBeanHolder.getRedisCacheManager().getCache(name)).clear();
		}
	}

	public static String getLogo() {

		String[] array = EntryPoint.LOGO.split("\n");

		String name = ProjectBeanHolder.getProjectConfiguration().getInformation().getName();

		String version = ProjectBeanHolder.getProjectConfiguration().getInformation().getVersion();

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
									n -= 5;
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
}
