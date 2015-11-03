package es.uvigo.esei.xcs.jsf;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import es.uvigo.esei.xcs.domain.entities.AnimalType;
import es.uvigo.esei.xcs.domain.entities.Pet;
import es.uvigo.esei.xcs.service.PetService;

@ManagedBean(name = "pet")
@SessionScoped
public class PetManagedBean {
	@Inject
	private PetService service;
	
	private String name;
	private Date birth;
	private AnimalType animal;
	
	private Pet currentPet;
	
	private String errorMessage;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public String getAnimal() {
		return Optional.ofNullable(this.animal)
			.map(AnimalType::name)
		.orElse(null);
	}

	public void setAnimal(String animal) {
		this.animal = AnimalType.valueOf(animal);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public boolean isError() {
		return this.errorMessage != null;
	}
	
	public boolean isEditing() {
		return this.currentPet != null;
	}
	
	public List<Pet> getPets() {
		return this.service.list();
	}
	
	public String edit(Pet pet) {
		this.currentPet = pet;
		this.name = this.currentPet.getName();
		this.birth = this.currentPet.getBirth();
		this.animal = this.currentPet.getAnimal();
		
		return this.getViewId();
	}
	
	public String cancelEditing() {
		this.clear();
		
		return this.getViewId();
	}

	public String remove(int id) {
		this.service.remove(id);

		return redirectTo(this.getViewId());
	}
	
	public String store() {
		try {
			if (this.isEditing()) {
				this.currentPet.setName(this.name);
				this.currentPet.setBirth(this.birth);
				this.currentPet.setAnimal(this.animal);
				
				this.service.update(this.currentPet);
			} else {
				this.service.create(new Pet(name, animal, birth));
			}
			
			this.clear();
			
			return redirectTo(this.getViewId());
		} catch (Throwable t) {
			this.errorMessage = t.getMessage();
			
			return this.getViewId();
		}
	}
	
	private void clear() {
		this.currentPet = null;
		this.name = null;
		this.birth = null;
		this.animal = null;
		this.errorMessage = null;
	}

	private String redirectTo(String url) {
		return url  + "?faces-redirect=true";
	}

	private String getViewId() {
		return FacesContext.getCurrentInstance().getViewRoot().getViewId();
	}
}
