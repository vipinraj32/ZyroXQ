package xyz.zyro.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import xyz.zyro.entity.type.AuthProviderType;
import xyz.zyro.entity.type.Role;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {

	@Id
	@NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
	private String email;
//	@NotBlank(message = "Name is required")
	private String userName;
	private String password;
	private Integer roleId;
	 
    private String providerId;
    @Column(name = "access_token", length = 1024)
    private String accessToken;
    @Enumerated(EnumType.STRING)
    private AuthProviderType providerType;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
			name = "user_role",
			joinColumns = @JoinColumn(name = "email",referencedColumnName = "email"),
			inverseJoinColumns = @JoinColumn(name="role",referencedColumnName = "id")
			)
    private Set<Role> roles=new HashSet<>();
    @OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "influncer_username", referencedColumnName = "username")
    private Influncer influncer;
    @OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "company_name", referencedColumnName = "companyName")
   private Advertiser advertiser;
   private Boolean profileStatus;
    
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority>authorities=this.roles.stream().map((role)-> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
		return authorities;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		if(userName!=null)
		return userName;
		
	  return email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public AuthProviderType getProviderType() {
		return providerType;
	}

	public void setProviderType(AuthProviderType providerType) {
		this.providerType = providerType;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public void addRole(Role role) {
		this.roles.add(role);
		role.getUsers().add(this);
	}
	
	public void removeRole(Role role) {
		this.roles.remove(role);
		role.getUsers().remove(this);
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	public Influncer getInfluncer() {
		return influncer;
	}

	public void setInfluncer(Influncer influncer) {
		this.influncer = influncer;
	}

	public Advertiser getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(Advertiser advertiser) {
		this.advertiser = advertiser;
	}

	public Boolean getProfileStatus() {
		return profileStatus;
	}

	public void setProfileStatus(Boolean profileStatus) {
		this.profileStatus = profileStatus;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}


}
