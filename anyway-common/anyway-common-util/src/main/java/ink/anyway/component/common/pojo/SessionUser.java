package ink.anyway.component.common.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 已登录用户信息
 */
@Data
public class SessionUser implements Serializable {

	private static final long serialVersionUID = 1764365572138947234L;

	protected String id;

	protected String username;

	protected String realName;

	private String tenantId;

	private Set<String> urlAuthorities;

	private Set<String> blockAuthorities;

	private String token;

	public SessionUser(String id, String username, String realName, String tenantId, Set<String> urlAuthorities, Set<String> blockAuthorities, String token) {
		this.id = id;
		this.username = username;
		this.realName = realName;
		this.tenantId = tenantId;
		this.urlAuthorities = urlAuthorities;
		this.blockAuthorities = blockAuthorities;
		this.token = token;
	}

}
