package com.cobnet.test;

import com.cobnet.polyglot.PolyglotContext;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.core.ScriptEngineManager;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
class SpringApplicationTests {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Test
	void polyglot() throws IOException {

		System.out.println("=== Graal.js via org.graalvm.polyglot.Context === ");

		/**
		 * TODO: Unable to pass test in build
		 * [ERROR] polyglot  Time elapsed: 0.042 s  <<< ERROR!
		 * java.lang.ExceptionInInitializerError
		 * 	at com.cobnet.test.SpringApplicationTests.polyglot(SpringApplicationTests.java:31)
		 * Caused by: java.lang.NullPointerException: Cannot invoke "com.oracle.truffle.api.impl.TVMCI.createRuntimeSupport(Object)"
		 * because the return value of "com.oracle.truffle.api.impl.Accessor.access$000()" is null
		 * 	at com.cobnet.test.SpringApplicationTests.polyglot(SpringApplicationTests.java:31)
		 */

//		PolyglotContext context = ScriptEngineManager.firstOrNewContext();
//
//		ScriptEngineManager.evalFromResource(context, "js", "/src.js");
//
//		System.out.println("warming up ...");
//
//		for (int i = 0; i < 15; i++) {
//
//			ScriptEngineManager.execute(context, "js", "primesMain");
//		}
//
//		System.out.println("warmup finished, now measuring");
//
//		for (int i = 0; i < 10; i++) {
//
//			long start = System.currentTimeMillis();
//			ScriptEngineManager.execute(context, "js", "primesMain");
//			long took = System.currentTimeMillis() - start;
//			System.out.println("iteration: " + took);
//		}
//
//		System.out.println(String.format("installed guest languages: %s ", ScriptEngineManager.getAvailableLanguages()));
//
//		System.out.println(ScriptEngineManager.getContexts());
	}

	@Test
	void message() {
		//System.out.println(Messager.fetch(Messager.message("phone", "Hello").setMediaUrl("https://demo.twilio.com/owl.png").create().getSid()).fetch());
	}

	@Test
	void mail() {
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setFrom("noreply@storechain.com");
//		message.setTo("hotflow96@gmail.com");
//		message.setSubject("test email service");
//		message.setText("this is a test mail.");
//		ProjectBeanHolder.getJavaMailSender().send(message);
	}

	@Test
	void redis() {

		String key = "Bob Smith";

		Person person = new Person();
		person.firstname = "Bob";
		Address address = new Address();
		address.city = "New York";
		address.country = "United States";
		person.address = address;
		person.date = new Date(System.currentTimeMillis());
		person.lastname = "Smith";
		person.localDateTime = LocalDateTime.now();

		//TODO not work?
		ProjectBeanHolder.getRedisTemplate().convertAndSend(new ChannelTopic("chat").getTopic(), "Hello from Redis");

		ProjectBeanHolder.getRedisHashOperations().putAll(key,  ProjectBeanHolder.getHashMapper().toHash(person));

		System.out.println(ProjectBeanHolder.getHashMapper().fromHash(ProjectBeanHolder.getRedisHashOperations().entries(key)));

		ProjectBeanHolder.getRedisTemplate().expire(key, 5, TimeUnit.SECONDS);

	}

	static class Person {

		public String getFirstname() {
			return firstname;
		}

		public String getLastname() {
			return lastname;
		}

		public Address getAddress() {
			return address;
		}

		public Date getDate() {
			return date;
		}

		public LocalDateTime getLocalDateTime() {
			return localDateTime;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public void setLocalDateTime(LocalDateTime localDateTime) {
			this.localDateTime = localDateTime;
		}

		String firstname;
		String lastname;
		Address address;
		Date date;
		LocalDateTime localDateTime;

		@Override
		public String toString() {

			return super.toString() + "[" + String.join(",", firstname, lastname, address.toString(), date.toString(), localDateTime.toString()) + "]";
		}
	}

	static class Address {

		public String getCity() {
			return city;
		}

		public String getCountry() {
			return country;
		}

		String city;

		public void setCity(String city) {
			this.city = city;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		String country;

		@Override
		public String toString() {

			return super.toString() + "[" + String.join(",", city, country) + "]";
		}
	}
}
