package es.uvigo.esei.xcs.http.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

public class HasHttpStatus extends TypeSafeMatcher<Response> {
	private StatusType status;
	
	public HasHttpStatus(StatusType status) {
		this.status = status;
	}
	
	public HasHttpStatus(int statusCode) {
		this(Response.Status.fromStatusCode(statusCode));
	}
	
	@Override
	public void describeTo(Description description) {
		description.appendValue(this.status);
	}

	@Override
	protected boolean matchesSafely(Response item) {
		return this.status.getStatusCode() == item.getStatus();
	}
	
	@Factory
	public static HasHttpStatus hasHttpStatus(int statusCode) {
		return new HasHttpStatus(statusCode);
	}
	
	@Factory
	public static HasHttpStatus hasHttpStatus(StatusType status) {
		return new HasHttpStatus(status);
	}
}
