package com.storechain.spring.boot.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.storechain.security.OwnedProviderAuthorityCollection;
import com.storechain.spring.boot.entity.utils.JsonMapConverter;

import lombok.Data;
import reactor.util.annotation.NonNull;

@Entity
@Data
public class UserProvider extends EntityBase implements OidcUser, Serializable {

	@Id
	private String identify;
	
	private String name;
	
	@Column(nullable = false)
	private String accessToken;
	
	@Column(nullable = false)
	private String idToken;
	
	@Column(nullable = false)
	private String provider;
	
	public transient OwnedProviderAuthorityCollection authorityCollection;
	
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "user_provider_authorities", joinColumns = { @JoinColumn(name = "id", referencedColumnName = "identify"), @JoinColumn(name = "provider", referencedColumnName = "provider") },
    		inverseJoinColumns = { @JoinColumn(name = "authority", referencedColumnName = "name") })
	private Set<UserProviderAuthority> authorities = new HashSet<UserProviderAuthority>();
	
    @Convert(converter = JsonMapConverter.class)
	private Map<String, Object> attributes = new HashMap<String, Object>();
    
    public UserProvider() {}
    
    public UserProvider(String identify, @NonNull String provider, String name, @NonNull String accessToken , @NonNull String idToken, List<UserProviderAuthority> authorities, Map<String, Object> attributes) {
    	
    	this.identify = identify;
    	this.provider = provider;
    	this.name = name;
    	this.accessToken = accessToken;
    	this.idToken = idToken;
    	this.getOwnedProviderAuthorityCollection().addAll(authorities);
    	attributes.keySet().forEach(key -> this.attributes.put(key, attributes.get(key)));
    }
    
    public String getIdentity() {
    	
    	return this.identify;
    }

	public String getProvider() {
		return this.provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	
	public OwnedProviderAuthorityCollection getOwnedProviderAuthorityCollection() {
		
		if(this.authorityCollection == null) {
			
			this.authorityCollection = new OwnedProviderAuthorityCollection(this.authorities);
		}
		
		return this.authorityCollection;
	}
	
	public Map<String, Object> getAttributes() {
		
		return this.attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return Collections.unmodifiableCollection(this.authorities);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		
		return this.name;
	}

	@Override
	public Map<String, Object> getClaims() {
		
		return this.getAttributes();
	}

	@Override
	public OidcUserInfo getUserInfo() {
		
		return new OidcUserInfo(this.getClaims());
	}

	@Override
	public OidcIdToken getIdToken() {
		
		OidcIdToken.Builder builder = OidcIdToken.withTokenValue(this.idToken);
		
		for(String key : this.attributes.keySet()) {
			
			builder.claim(key, this.attributes.get(key));
		}
		
		return builder.build();
	}

	public String getToken() {
		return this.idToken;
	}

	public void setToken(String token) {
		this.idToken = token;
	}

}
