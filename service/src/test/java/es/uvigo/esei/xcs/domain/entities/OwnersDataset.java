package es.uvigo.esei.xcs.domain.entities;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

public class OwnersDataset {
	public static Owner owner(String login) {
		return stream(owners())
			.filter(owner -> owner.getLogin().equals(login))
			.findFirst()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	public static Pet pet(int id) {
		return stream(pets())
			.filter(pet -> pet.getId() == id)
			.findFirst()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	public static Owner[] owners(String ... logins) {
		final Set<String> loginSet = stream(logins).collect(toSet());
		
		return stream(owners())
			.filter(owner -> loginSet.contains(owner.getLogin()))
		.toArray(Owner[]::new);
	}
	
	public static Owner[] owners() {
		return new Owner[] {
			new Owner("pepe", "pepepass",
				new Pet(1, "Pepecat", AnimalType.CAT, new Date(946684861000L))),
			new Owner("juan", "juanpass",
				new Pet(2, "Max", AnimalType.CAT, new Date(946684861000L)),
				new Pet(3, "Juandog", AnimalType.DOG, new Date(946684861000L))
			),
			new Owner("ana", "anapass",
				new Pet(4, "Anacat", AnimalType.CAT, new Date(946684861000L)),
				new Pet(5, "Max", AnimalType.DOG, new Date(946684861000L)),
				new Pet(6, "Anabird", AnimalType.BIRD, new Date(946684861000L))
			),
			new Owner("lorena", "lorenapass")
		};
	}
	
	public static Pet[] pets() {
		return stream(owners())
			.map(Owner::getPets)
			.flatMap(Collection::stream)
		.toArray(Pet[]::new);
	}
	
	public static Owner newOwnerWithoutPets() {
		return new Owner("jacinto", "jacintopass");
	}
	
	public static Owner newOwnerWithFreshPets() {
		return new Owner("jacinto", "jacintopass",
			new Pet("Jacintocat", AnimalType.CAT, new Date(946684861000L)),
			new Pet("Jacintodo", AnimalType.DOG, new Date(946684861000L)),
			new Pet("Jacintobird", AnimalType.BIRD, new Date(946684861000L))
		);
	}
	
	public static Owner newOwnerWithPersistentPets() {
		return new Owner("jacinto", "jacintopass",
			new Pet(7, "Jacintocat", AnimalType.CAT, new Date(946684861000L)),
			new Pet(8, "Jacintodo", AnimalType.DOG, new Date(946684861000L)),
			new Pet(9, "Jacintobird", AnimalType.BIRD, new Date(946684861000L))
		);
	}
	
	public static Owner ownerWithPets() {
		return owners()[1];
	}
	
	public static Owner ownerWithoutPets() {
		return owners()[3];
	}
	
	public static Pet newPet() {
		return new Pet("Lorenacat", AnimalType.CAT, new Date(946684861000L));
	}
}
