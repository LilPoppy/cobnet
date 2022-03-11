package com.storechain.spring.boot.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.storechain.interfaces.security.Operator;
import com.storechain.interfaces.security.permission.Permissible;
import com.storechain.interfaces.security.permission.Permission;
import com.storechain.security.RoleRule;
import com.storechain.security.permission.OwnedPermissionCollection;
import com.storechain.spring.boot.entity.utils.JsonMapConverter;
import com.storechain.utils.DatabaseManager;

import lombok.Data;
import reactor.util.annotation.NonNull;

@Entity
@Data
public class ExternalUser extends EntityBase implements OidcUser, Serializable, Operator, Permissible {

	private static final long serialVersionUID = 73179483542138315L;

	@Id
	private String identity;
	
	@Column(nullable = false, length = 4096)
	private String idToken;
	
	@Column(nullable = false)
	private String accessToken;
	
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
	
	public transient OwnedPermissionCollection permissionCollection;
	
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "external_user_authorities", joinColumns = { @JoinColumn(name = "identity", referencedColumnName = "identity") },
    		inverseJoinColumns = { @JoinColumn(name = "authority", referencedColumnName = "name") })
	private Set<ExternalUserAuthority> authorities = new HashSet<ExternalUserAuthority>();
	
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "json")
	private Map<String, Object> attributes = new HashMap<String, Object>();
    
    public ExternalUser() {}
    
    public ExternalUser(@NonNull String identity, @NonNull String provider, @NonNull String idToken, @NonNull String accessToken , List<ExternalUserAuthority> authorities, Map<String, Object> attributes) {
    	
    	this.identity = provider + ":" + identity;
    	this.idToken = idToken;
    	this.accessToken = accessToken;
    	this.getOwnedPermissionCollection().addAll(authorities);
    	this.setAttributes(attributes);
    }
    
    public String getIdentity() {
    	
    	return this.identity;
    }

	public String getProvider() {
		
		return this.identity.split(":")[0];
	}

	public void setProvider(String provider) {
		
		String[] nodes = this.identity.split(":");
		
		if(nodes.length > 0) {
			
			nodes[0] = provider;

			this.identity = String.join(":", Arrays.copyOfRange(nodes, 1, nodes.length));
		}
	}
	
	
	public OwnedPermissionCollection getOwnedPermissionCollection() {
		
		if(this.permissionCollection == null) {
			
			this.permissionCollection = new OwnedPermissionCollection(this, this.authorities);
		}
		
		return this.permissionCollection;
	}
	
	public Map<String, Object> getAttributes() {
		
		return this.attributes;
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		
		this.attributes.clear();
		attributes.keySet().forEach(key -> this.attributes.put(key, attributes.get(key)));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return Collections.unmodifiableCollection(this.authorities);
	}
	
	@Override
	public String getName() {
		
		return this.getFullName();
	}
	
	@Override
	public String getUsername() {
		
		String[] nodes = this.identity.split(":");
    	
    	if(nodes.length > 0) {
    		
        	return String.join(":", Arrays.copyOfRange(nodes, 1, nodes.length));
    	}
    	
    	return null;
	}
	
	public User getUser() {
		
		return this.user;
	}
	
	public void setUser(User user) {
		
		this.user = user;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		
		this.accessToken = accessToken;
	}
	
	public void setIdToken(String idToken) {
		
		this.idToken = idToken;
	}

	@Override
	public Map<String, Object> getClaims() {
		
		return this.getAttributes();
	}

	@Override
	public OidcUserInfo getUserInfo() {
		
		return this.getClaims().isEmpty() ? null : new OidcUserInfo(this.getClaims());
	}

	@Override
	public OidcIdToken getIdToken() {

		return this.idToken == null || this.getClaims().isEmpty() ? null : new OidcIdToken(this.idToken, this.getIssuedAt(), this.getExpiresAt(), this.getClaims());
	}
	
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj instanceof ExternalUser) {
			
			ExternalUser other = (ExternalUser) obj;
			
			return this.identity.equals(other.identity) && this.idToken.equals(other.idToken) && this.accessToken.equals(other.accessToken) && this.attributes.equals(other.attributes) && this.authorities.equals(other.authorities);
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		
		if(this.identity == null) {

			return 0;
		}
		
		return this.identity.hashCode() + this.idToken.hashCode() + this.accessToken.hashCode() + this.attributes.entrySet().stream().map(entry -> entry.hashCode()).collect(Collectors.summingInt(Integer::intValue)) + this.authorities.stream().map(authority -> authority.hashCode()).collect(Collectors.summingInt(Integer::intValue));
	}
	
	@Override
	public String toString() {

		return this.getClass().getName() + ": "
				+ "{"
				+ "Name: " + this.getName() + ", "
				+ "Provider: " + this.getProvider() + ", "
				+ "Username: " + this.getUsername() + ", "
				+ "User: " + (this.getUser() == null ? null : this.getUser().getUsername()) + ", "
				+ "Id token: " + this.idToken + ", "
				+ "Access token: " + this.accessToken + "}";
	}

	@Override
	public RoleRule getRule() {
		
		return this.getUser() == null ? RoleRule.PRE_USER : RoleRule.PRE_USER;
	}

	@Override
	public Collection<? extends Permission> getPermissions() {
		
		return Collections.unmodifiableSet(this.authorities);
	}

	@Override
	public boolean isPermitted(String authority) {
		return this.getOwnedPermissionCollection().hasPermission(authority);
	}

	@Override
	public <T extends Permission> void addPermission(T permission) {
		
		this.getOwnedPermissionCollection().add(permission);
		
	}

	@Override
	public <T extends Permission> void removePermission(T permission) {
		
		this.getOwnedPermissionCollection().remove(permission);
	}
}
