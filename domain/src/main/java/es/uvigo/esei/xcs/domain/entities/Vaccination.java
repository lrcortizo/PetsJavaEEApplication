package es.uvigo.esei.xcs.domain.entities;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.inclusiveBetween;

import java.io.Serializable;
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
		this.setName(name);
		this.setDate(date);
		this.setPrize(price);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public void setPrize(int price) {
		requireNonNull(date, "prize can't be null");
		this.price = price;
	}

	public Collection<Pet> getPets() {
		return unmodifiableCollection(pets);
	}

	public void addPet(Pet pet) {
		requireNonNull(pet, "pet can't be null");
		
		pets.add(pet);
	}
	
}
