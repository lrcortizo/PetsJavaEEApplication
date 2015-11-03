package es.uvigo.esei.xcs.jsf;

import static java.util.stream.Collectors.joining;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import es.uvigo.esei.xcs.domain.entities.Owner;
import es.uvigo.esei.xcs.domain.entities.Pet;
import es.uvigo.esei.xcs.service.OwnerService;

@ManagedBean(name = "owner")
@SessionScoped
public class OwnerManagerdBean {
	@Inject
	private OwnerService service;
	
	private String login;
	private String password;
	
	private Owner currentOwner;
	
	private String errorMessage;
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String name) {
		this.login = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public boolean isError() {
		return this.errorMessage != null;
	}
	
	public boolean isEditing() {
		return this.currentOwner != null;
	}
	
	public List<Owner> getOwners() {
		return this.service.list();
	}
	
	public String getPetNames(String login) {
		return this.service.getPets(login).stream()
			.map(Pet::getName)
		.collect(joining(", "));
	}
	
	public String edit(Owner owner) {
		this.currentOwner = owner;
		this.login = this.currentOwner.getLogin();
		
		return this.getViewId();
	}
	
	public String cancelEditing() {
		this.clear();
		
		return this.getViewId();
	}
	
	public String remove(String login) {
		this.service.remove(login);
		
		return redirectTo(this.getViewId());
	}
	
	public String store() {
		try {
			if (this.isEditing()) {
				this.currentOwner.changePassword(this.password);
				
				this.service.update(this.currentOwner);
			} else {
				this.service.create(new Owner(login, password));
			}

			this.clear();
			
			return redirectTo(this.getViewId());
		} catch (Throwable t) {
			this.errorMessage = t.getMessage();
			
			return this.getViewId();
		}
	}
	
	private void clear() {
		this.login = null;
		this.password = null;
		this.errorMessage = null;
		this.currentOwner = null;
	}

	private String redirectTo(String url) {
		return url  + "?faces-redirect=true";
	}
	
	private String getViewId() {
		return FacesContext.getCurrentInstance().getViewRoot().getViewId();
	}
}
