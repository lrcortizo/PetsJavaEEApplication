package es.uvigo.esei.xcs.domain.entities;

import static es.uvigo.esei.xcs.domain.entities.IsEqualsToOwner.equalsToOwnerWithoutRelations;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsEqualsToPet extends IsEqualsToEntity<Pet> {
	private final boolean checkRelations;
	
	public IsEqualsToPet(Pet pet, boolean checkRelations) {
		super(pet);
		this.checkRelations = checkRelations;
	}
	
	@Override
	protected boolean matchesSafely(Pet actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("pet", expected.toString());
			return false;
		} else if (this.expected.getId() != actual.getId()) {
			this.addTemplatedDescription("id", expected.getId());
			return false;
		} else if (!this.expected.getName().equals(actual.getName())) {
			this.addTemplatedDescription("name", expected.getName());
			return false;
		} else if (!this.expected.getAnimal().equals(actual.getAnimal())) {
			this.addTemplatedDescription("animal", expected.getAnimal());
			return false;
		} else if (this.expected.getBirth().getTime() != actual.getBirth().getTime()) {
			this.addTemplatedDescription("birth", expected.getBirth());
			return false;
		} else if (this.checkRelations) {
			final IsEqualsToOwner equalsToOwner = equalsToOwnerWithoutRelations(this.expected.getOwner());
			
			if (equalsToOwner.matchesSafely(actual.getOwner())) {
				return true;
			} else {
				this.addMatcherDescription(equalsToOwner);
				return false;
			}
		} else {
			return true;
		}
	}

	@Factory
	public static IsEqualsToPet equalsToPet(Pet pet) {
		return new IsEqualsToPet(pet, true);
	}
	
	@Factory
	public static IsEqualsToPet equalsToPetWithoutRelations(Pet pet) {
		return new IsEqualsToPet(pet, false);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Pet>> containsPetsInAnyOrder(Pet ... pets) {
		return containsEntityInAnyOrder(IsEqualsToPet::equalsToPet, pets);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Pet>> containsPetsWithoutRelationsInAnyOrder(Pet ... pets) {
		return containsEntityInAnyOrder(IsEqualsToPet::equalsToPetWithoutRelations, pets);
	}
}
