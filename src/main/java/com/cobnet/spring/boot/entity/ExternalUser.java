package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.security.Account;
import com.cobnet.interfaces.security.Permissible;
import com.cobnet.interfaces.security.Permission;
import com.cobnet.security.RoleRule;
import com.cobnet.security.permission.PermissionValidator;
import com.cobnet.spring.boot.entity.support.JsonMapConverter;
import com.cobnet.spring.boot.entity.support.JsonPermissionSetConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import reactor.util.annotation.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class ExternalUser extends EntityBase implements OidcUser, Serializable, Account, Permissible {

	private static final long serialVersionUID = 73179483542138315L;

	private static final Logger LOG = LoggerFactory.getLogger(ExternalUser.class);

	@Id
	private String identity;
	
	@Column(nullable = false, length = 4096)
	private String idToken;
	
	@Column(nullable = false)
	private String accessToken;
	
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

	@Convert(converter = JsonPermissionSetConverter.class)
	@Column(columnDefinition = "json")
	private Set<Permission> authorities = new HashSet<>();

	@SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "json")
	private HashMap<String, Object> attributes = new HashMap<>();

	@Transient
	private transient PermissionValidator permissionValidator;

    public ExternalUser() {}
    
    public ExternalUser(@NonNull String identity, @NonNull String provider, @NonNull String idToken, @NonNull String accessToken , Set<? extends Permission> permissions, Map<String, Object> attributes) {
    	
    	this.identity = provider + ":" + identity;
    	this.idToken = idToken;
    	this.accessToken = accessToken;
		this.authorities.addAll(permissions);
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

	private PermissionValidator getPermissionValidator() {
		
		if(this.permissionValidator == null) {
			
			this.permissionValidator = new PermissionValidator(this, this.authorities);
		}
		
		return this.permissionValidator;
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

		if (obj instanceof ExternalUser other) {

			return this.identity.equals(other.identity) && this.idToken.equals(other.idToken) && this.accessToken.equals(other.accessToken) && this.attributes.equals(other.attributes) && this.authorities.equals(other.authorities);
		}

		return false;
	}

	@Override
	public int hashCode() {

		if(this.identity == null) {

			return 0;
		}

		return this.identity.hashCode() + this.idToken.hashCode() + this.accessToken.hashCode() + this.attributes.entrySet().stream().map(Map.Entry::hashCode).mapToInt(Integer::intValue).sum() + this.authorities.stream().map(Object::hashCode).mapToInt(Integer::intValue).sum();
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
		
		return this.getUser() == null ? RoleRule.PRE_USER : this.getUser().getRule();
	}

	@Override
	public Collection<? extends Permission> getPermissions() {
		
		return this.authorities;
	}

	@Override
	public boolean isPermitted(String authority) {
		return this.getPermissionValidator().hasPermission(authority);
	}

	@Override
	public boolean addPermission(Permission permission) {
		
		return this.authorities.add(permission);
	}

	@Override
	public boolean removePermission(Permission permission) {
		
		return this.authorities.remove(permission);
	}

	public static class Builder {

		private String identity;

		private String provider;

		private String idToken;

		private String accessToken;

		private Set<? extends Permission> permissions;

		private Map<String, Object> attributes;

		public Builder setIdentity(String identity) {

			this.identity = identity;

			return this;
		}

		public Builder setProvider(String provider) {

			this.provider = provider;

			return this;
		}

		public Builder setIdToken(String idToken) {

			this.idToken = idToken;

			return this;
		}

		public Builder setAccessToken(String accessToken) {

			this.accessToken = accessToken;

			return this;
		}

		public <T extends Permission> Builder setPermissions(T... permissions) {

			this.permissions = Arrays.stream(permissions).collect(Collectors.toSet());

			return this;
		}

		public Builder setAttributes(Map<String, Object> attributes) {

			this.attributes = attributes;

			return this;
		}

		public ExternalUser build() {

			return new ExternalUser(this.identity, this.provider, this.idToken, this.accessToken, this.permissions, this.attributes);
		}
	}
}
