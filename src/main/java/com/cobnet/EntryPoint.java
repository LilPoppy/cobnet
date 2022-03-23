package com.cobnet;

import com.cobnet.common.KeyValuePair;
import com.cobnet.connection.NettyChannel;
import com.cobnet.connection.NettyServer;
import com.cobnet.connection.handler.ChannelInitializeHandler;
import com.cobnet.connection.handler.ServerInitializeHandler;
import com.cobnet.interfaces.connection.ChannelProvider;
import com.cobnet.security.RoleRule;
import com.cobnet.security.permission.UserPermission;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.entity.UserRole;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.IOException;
import java.util.Arrays;

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

		LOG.info(EntryPoint.getLogo());



		User user = new User("admin", "123456", new UserRole("admin", RoleRule.ADMIN, new UserPermission("admin.read.test"), new UserPermission("user.op"), new UserPermission("user.read.lm"), new UserPermission("user.test")));

		ProjectBeanHolder.getUserRepository().save(user);

		if(Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("agent"))) {

			System.exit(0);
		}
	}

	@Bean
	public NettyServer<NettyChannel> nettyServer() {

		NettyServer<NettyChannel>  server = new NettyServer<>("tester") {

			@Override
			protected NettyServer<NettyChannel>.Builder builder(NettyServer<NettyChannel>.Builder builder) {

				return builder.setChildOptions(new KeyValuePair<>(ChannelOption.TCP_NODELAY, true), new KeyValuePair<>(ChannelOption.SO_KEEPALIVE, true)).
						setHandler(new ServerInitializeHandler<NettyServer<NettyChannel>, NettyChannel>(this)).
						setChildHandler(new ChannelInitializeHandler<NettyServer<NettyChannel>, NettyChannel>(this) {

					@Override
					protected ChannelProvider<NettyServer<NettyChannel>, NettyChannel> getProvider() {
						return NettyChannel::new;
					}
				});
			}

		}.bind(8091);

		return server;
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
