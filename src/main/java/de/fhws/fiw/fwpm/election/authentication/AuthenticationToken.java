package de.fhws.fiw.fwpm.election.authentication;

/**
 * Created by christianbraun on 12/07/16.
 */
public class AuthenticationToken {

	public enum TokenType{
		API,
		BEARER
	}

	private TokenType type;
	private String token;
	private long expiresAt;

	public TokenType getType() {
		return type;
	}

	public void setType(String type) {
		if(type.equalsIgnoreCase(AuthenticationToken.TokenType.API.name())){
			setType(TokenType.API);
		}
		else if(type.equalsIgnoreCase(AuthenticationToken.TokenType.BEARER.name())){
			setType(AuthenticationToken.TokenType.BEARER);
		}
	}

	public void setType(TokenType type){
		this.type = type;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public boolean isType(TokenType type){
		return this.type == type;
	}

	public boolean sameToken(String token){
		return this.token.equals(token);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AuthenticationToken that = (AuthenticationToken) o;

		if (type != that.type) return false;
		return token.equals(that.token);

	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + token.hashCode();
		return result;
	}
}
