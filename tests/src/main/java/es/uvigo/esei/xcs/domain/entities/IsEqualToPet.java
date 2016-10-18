package es.uvigo.esei.xcs.domain.entities;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsEqualToPet extends IsEqualToEntity<Pet> {
	private final boolean checkRelations;
	
	public IsEqualToPet(Pet pet, boolean checkRelations) {
		super(pet);
		this.checkRelations = checkRelations;
	}
	
	@Override
	protected boolean matchesSafely(Pet actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("id", Pet::getId, actual)
				&& checkAttribute("name", Pet::getName, actual)
				&& checkAttribute("animal", Pet::getAnimal, actual)
				&& checkAttribute("birth", Pet::getBirth, actual)
				&& (!this.checkRelations || checkAttribute("owner", Pet::getOwner, actual, IsEqualToOwner::equalToOwnerWithoutRelations));
		}
	}

	@Factory
	public static IsEqualToPet equalToPet(Pet pet) {
		return new IsEqualToPet(pet, true);
	}
	
	@Factory
	public static IsEqualToPet equalToPetWithoutRelations(Pet pet) {
		return new IsEqualToPet(pet, false);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Pet>> containsPetsInAnyOrder(Pet ... pets) {
		return containsEntityInAnyOrder(IsEqualToPet::equalToPet, pets);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Pet>> containsPetsWithoutRelationsInAnyOrder(Pet ... pets) {
		return containsEntityInAnyOrder(IsEqualToPet::equalToPetWithoutRelations, pets);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Pet>> containsPetsInAnyOrder(Iterable<Pet> pets) {
		return containsEntityInAnyOrder(IsEqualToPet::equalToPet, pets);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Pet>> containsPetsWithoutRelationsInAnyOrder(Iterable<Pet> pets) {
		return containsEntityInAnyOrder(IsEqualToPet::equalToPetWithoutRelations, pets);
	}
}
