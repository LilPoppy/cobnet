package com.storechain.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.storechain.spring.boot.service.JpaExtensionRepository;

@Configuration
@ConfigurationProperties("spring")
@EnableJpaRepositories(value = "com.storechain.interfaces.spring.repository", repositoryBaseClass = JpaExtensionRepository.class)
public class PersistenceConfiguration {

	private DataSourceConfiguration datasource;
	
	private JpaConfiguration jpa;
	

	public DataSourceConfiguration getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSourceConfiguration datasource) {
		this.datasource = datasource;
	}

	public JpaConfiguration getJpa() {
		return jpa;
	}

	public void setJpa(JpaConfiguration jpa) {
		this.jpa = jpa;
	}
	
	public static class DataSourceConfiguration {

		private String driverClassName;

		private String url;

		private String username;

		private String password;

		public String getDriverClassName() {
			return driverClassName;
		}

		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
	
	public static class JpaConfiguration {
		
		private HibernateConfiguration hibernate;
		
		private Boolean showSql;
		
		public static class HibernateConfiguration {
			
			private String ddlAuto;

			public String getDdlAuto() {
				return ddlAuto;
			}

			public void setDdlAuto(String ddlAuto) {
				this.ddlAuto = ddlAuto;
			}
			
			
		}

		public HibernateConfiguration getHibernate() {
			return hibernate;
		}

		public void setHibernate(HibernateConfiguration hibernate) {
			this.hibernate = hibernate;
		}

		public Boolean getShowSql() {
			return showSql;
		}

		public void setShowSql(Boolean showSql) {
			this.showSql = showSql;
		}
	}
}
