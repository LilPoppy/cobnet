package com.cobnet;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import java.io.IOException;
import java.util.Arrays;

@EnableEurekaClient
@SpringBootApplication(proxyBeanMethods = false)
public class EntryPoint {

	private final static Logger LOG = LoggerFactory.getLogger(EntryPoint.class);

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

	public static void main(String[] args) throws IOException {

		SpringApplication.run(EntryPoint.class, args);

		LOG.info(EntryPoint.getLogo());

		if(Arrays.stream(args).anyMatch(arg -> arg.toUpperCase().equals("agent"))) {

			System.exit(0);
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