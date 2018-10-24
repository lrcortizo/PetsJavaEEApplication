package es.uvigo.esei.xcs.domain.entities;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.inclusiveBetween;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity(name="Vaccination")
public class Vaccination implements Serializable{
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(length = 100, nullable = false)
	private String name;
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@Column(nullable = false)
	private int price;
	
	@OneToMany(
			mappedBy = "vaccination",
			targetEntity = Pet.class,
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.EAGER
		)
	private Collection<Pet> pets;
	
	Vaccination(){}
	
	Vaccination(int id, String name, Date date, int price) {
		this(name, date, price);
		this.id = id;
	}
	
	public Vaccination(String name, Date date, int price) {
		this(name, date, price, null);
	}
	
	public Vaccination(String name, Date date, int price, Pet ... pets) {
		this.setName(name);
		this.setDate(date);
		this.setPrice(price);
		this.pets = new ArrayList<>();
		stream(pets).forEach(this::addPet);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		requireNonNull(name, "name can't be null");
		inclusiveBetween(1, 100, name.length(), "name must have a length between 1 and 100");
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		requireNonNull(date, "date can't be null");
		inclusiveBetween(new Date(0), new Date(), date,
			"date must be previous to the current time"
		);
		this.date = date;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		requireNonNull(date, "prize can't be null");
		this.price = price;
	}

	public Collection<Pet> getPets() {
		return unmodifiableCollection(pets);
	}

	public void addPet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		if (!this.ownsPet(pet)) {
			pet.setVaccination(this);
		}
	}
	
	public void removePet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		if (this.ownsPet(pet)) {
			pet.setVaccination(null);
		} else {
			throw new IllegalArgumentException("pet doesn't belong to this owner");
		}
	}
	
	public boolean ownsPet(Pet pet) {
		return this.pets.contains(pet);
	}
	
	void internalAddPet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		if (!this.ownsPet(pet))
			this.pets.add(pet);
	}
	
	
	void internalRemovePet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		this.pets.remove(pet);
	}
	
}
