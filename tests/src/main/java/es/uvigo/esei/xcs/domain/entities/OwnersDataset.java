package es.uvigo.esei.xcs.domain.entities;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

public class OwnersDataset {
	public static final String EXISTENT_LOGIN = "pepe";
	public static final String NON_EXISTENT_LOGIN = "non-existent";
	public static final String OWNER_WITH_PETS_LOGIN = "juan";
	public static final String OWNER_WITHOUT_PETS_LOGIN = "lorena";

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
			new Owner(EXISTENT_LOGIN, "pepepass",
				new Pet(1, "Pepecat", AnimalType.CAT, new Date(946684861000L))),
			new Owner(OWNER_WITH_PETS_LOGIN, "juanpass",
				new Pet(2, "Max", AnimalType.CAT, new Date(946684861000L)),
				new Pet(3, "Juandog", AnimalType.DOG, new Date(946684861000L))
			),
			new Owner("ana", "anapass",
				new Pet(4, "Anacat", AnimalType.CAT, new Date(946684861000L)),
				new Pet(5, "Max", AnimalType.DOG, new Date(946684861000L)),
				new Pet(6, "Anabird", AnimalType.BIRD, new Date(946684861000L))
			),
			new Owner(OWNER_WITHOUT_PETS_LOGIN, "lorenapass")
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
	
	public static Owner anyOwner() {
		return ownerWithPets();
	}
	
	public static Owner ownerWithPets() {
		return owner(OWNER_WITH_PETS_LOGIN);
	}
	
	public static Owner ownerWithoutPets() {
		return owner(OWNER_WITHOUT_PETS_LOGIN);
	}
	
	public static Pet anyPet() {
		return pet(existentPetId());
	}
	
	public static Pet newPet() {
		return newPetWithOwner(null);
	}
	
	public static Pet newPetWithOwner(Owner owner) {
		return new Pet("Lorenacat", AnimalType.CAT, new Date(946684861000L), owner);
	}
	
	public static String anyLogin() {
		return EXISTENT_LOGIN;
	}
	
	public static String existentLogin() {
		return EXISTENT_LOGIN;
	}
	
	public static String nonExistentLogin() {
		return NON_EXISTENT_LOGIN;
	}
	
	public static String existentPetName() {
		return "Pepecat";
	}
	
	public static String nonExistentPetName() {
		return "NonExistentPet";
	}
	
	public static int existentPetId() {
		return 2;
	}
	
	public static int nonExistentPetId() {
		return 1000000;
	}

	public static Owner nonExistentOwner() {
		final String login = nonExistentLogin();
		return new Owner(login, login + "pass");
	}
}
