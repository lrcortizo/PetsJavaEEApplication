package es.uvigo.esei.xcs.domain.entities;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.matchesPattern;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * A pet owner.
 * 
 * @author Miguel Reboiro-Jato
 */
@Entity
public class Owner implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 100, nullable = false)
	private String login;
	
	@Column(length = 32, nullable = false)
	private String password;
	
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<Pet> pets;
	
	// Required for JPA
	Owner() {}
	
	/**
	 * Creates a new instance of {@code Owner} without pets.
	 * 
	 * @param login the login that identifies the user. This parameter must be a
	 * non empty and non {@code null} string with a maximum length of 100 chars.
	 * @param password the raw password of the user. This parameter must be a
	 * non {@code null} string with a minimum length of 6 chars.
	 * 
	 * @throws NullPointerException if a {@code null} value is passed as the
	 * value for any parameter.
	 * @throws IllegalArgumentException if value provided for any parameter is
	 * not valid according to its description.
	 */
	public Owner(String login, String password) {
		this.setLogin(login);
		this.changePassword(password);
		this.pets = new ArrayList<>();
	}
	
	/**
	 * Creates a new instance of {@code Owner} with pets.
	 * 
	 * @param login the login that identifies the user. This parameter must be a
	 * non empty and non {@code null} string with a maximum length of 100 chars.
	 * @param password the raw password of the user. This parameter must be a
	 * non {@code null} string with a minimum length of 4 chars.
	 * @param pets the pets that belong to this owner. The list of pets can be
	 * empty. {@code null} values are not supported.
	 * 
	 * @throws NullPointerException if a {@code null} value is passed as the
	 * value for any parameter.
	 * @throws IllegalArgumentException if value provided for any parameter is
	 * not valid according to its description.
	 */
	public Owner(String login, String password, Pet ... pets) {
		this.setLogin(login);
		this.changePassword(password);
		this.pets = new ArrayList<>();
		
		stream(pets).forEach(this::addPet);
	}

	/**
	 * Returns the login of this owner.
	 * 
	 * @return the login of this owner.
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Sets the login of this owner.
	 * 
	 * @param login the login that identifies the owner. This parameter must be
	 * a non empty and non {@code null} string with a maximum length of 100 
	 * chars.
	 * @throws NullPointerException if {@code null} is passed as parameter.
	 * @throws IllegalArgumentException if the length of the string passed is
	 * not valid.
	 */
	public void setLogin(String login) {
		requireNonNull(login, "login can't be null");
		inclusiveBetween(1, 100, login.length(), "login must have a length between 1 and 100");
		
		this.login = login;
	}
	
	/**
	 * Returns the MD5 of the owner's password. Capital letters are used in the
	 * returned string.
	 * 
	 * @return the MD5 of the owner's password. Capital letters are used in the
	 * returned string.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the MD5 password of the owner. The MD5 string is stored with capital
	 * letters.
	 * 
	 * @param password the MD5 password of the user. This parameter must be a
	 * non {@code null} MD5 string.
	 * @throws NullPointerException if {@code null} is passed as parameter.
	 * @throws IllegalArgumentException if the string passed is not a valid MD5
	 * string.
	 */
	public void setPassword(String password) {
		requireNonNull(password, "password can't be null");
		matchesPattern(password, "[a-zA-Z0-9]{32}", "password must be a valid uppercase MD5 string");
		
		this.password = password.toUpperCase();
	}
	
	/**
	 * Changes the password of the owner. This method receives the raw value of
	 * the password and stores it in MD5 format.
	 * 
	 * @param password the raw password of the user. This parameter must be a
	 * non {@code null} string with a minimum length of 6 chars.
	 * 
	 * @throws NullPointerException if the {@code password} is {@code null}.
	 * @throws IllegalArgumentException if the length of the string passed is
	 * not valid.
	 */
	public void changePassword(String password) {
		requireNonNull(password, "password can't be null");
		if (password.length() < 6)
			throw new IllegalArgumentException("password can't be shorter than 6");
		
		try {
			final MessageDigest digester = MessageDigest.getInstance("MD5");
			final HexBinaryAdapter adapter = new HexBinaryAdapter();

			this.password = adapter.marshal(digester.digest(password.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 algorithm not found", e);
		}
	}
	
	/**
	 * Returns the pets that belongs to this owner. The collection returned is
	 * unmodifiable and no order are guaranteed.
	 * If the pet already belongs to this owner, no action will be done.
	 * 
	 * @return the pets that belongs to this owner. 
	 */
	public Collection<Pet> getPets() {
		return unmodifiableCollection(pets);
	}
	
	/**
	 * Adds a pet to this owner. The pet's owner will be set to this instance.
	 * 
	 * @param pet the pet to add to this owner. {@code null} values not
	 * supported.
	 * @throws NullPointerException if the {@code pet} is {@code null}.
	 */
	public void addPet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		if (!this.ownsPet(pet)) {
			pet.setOwner(this);
		}
	}
	
	/**
	 * Removes a pet from this owner. The pet's owner will be set to
	 * {@code null}.
	 * 
	 * @param pet the pet to remove from this owner. {@code null} values not
	 * supported.
	 * @throws NullPointerException if the {@code pet} is {@code null}.
	 * @throws IllegalArgumentException if the {@code pet} does not belong to
	 * this owner.
	 */
	public void removePet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		if (this.ownsPet(pet)) {
			pet.setOwner(null);
		} else {
			throw new IllegalArgumentException("pet doesn't belong to this owner");
		}
	}
	
	/**
	 * Checks if a pet belongs to this owner.
	 * 
	 * @param pet the pet whose property will be checked.
	 * @return {@code true} if the pet belongs to this owner. {@code false}
	 * otherwise.
	 */
	public boolean ownsPet(Pet pet) {
		return this.pets.contains(pet);
	}
	
	/**
	 * Adds a pet directly to the pets collection of this owner if the pet does
	 * not already belongs to this owner. The pet's owner will not be updated.
	 * 
	 * @param pet the pet to add to this owner. {@code null} values not
	 * supported.
	 * @throws NullPointerException if the {@code pet} is {@code null}.
	 */
	void internalAddPet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		if (!this.ownsPet(pet))
			this.pets.add(pet);
	}
	
	/**
	 * Removes a pet directly from the pets collection of this owner. The pet's
	 * owner will not be updated.
	 * 
	 * @param pet the pet to remove from this owner. {@code null} values not
	 * supported.
	 * @throws NullPointerException if the {@code pet} is {@code null}.
	 */
	void internalRemovePet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		this.pets.remove(pet);
	}
}
