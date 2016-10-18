package es.uvigo.esei.xcs.domain.entities;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsEqualToOwner extends IsEqualToEntity<Owner> {
	private final boolean checkRelations;
	
	public IsEqualToOwner(Owner owner, boolean checkRelations) {
		super(owner);
		this.checkRelations = checkRelations;
	}

	@Override
	protected boolean matchesSafely(Owner actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("login", Owner::getLogin, actual)
				&& checkAttribute("password", Owner::getPassword, actual)
				&& (!this.checkRelations || checkIterableAttribute("pets", Owner::getPets, actual, IsEqualToPet::containsPetsWithoutRelationsInAnyOrder));
		}
	}

	@Factory
	public static IsEqualToOwner equalToOwner(Owner owner) {
		return new IsEqualToOwner(owner, true);
	}
	
	@Factory
	public static IsEqualToOwner equalToOwnerWithoutRelations(Owner owner) {
		return new IsEqualToOwner(owner, false);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Owner>> containsOwnersInAnyOrder(Owner ... owners) {
		return containsEntityInAnyOrder(IsEqualToOwner::equalToOwner, owners);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Owner>> containsOwnersWithoutRelationsInAnyOrder(Owner ... owners) {
		return containsEntityInAnyOrder(IsEqualToOwner::equalToOwnerWithoutRelations, owners);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Owner>> containsOwnersInAnyOrder(Iterable<Owner> owners) {
		return containsEntityInAnyOrder(IsEqualToOwner::equalToOwner, owners);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Owner>> containsOwnersWithoutRelationsInAnyOrder(Iterable<Owner> owners) {
		return containsEntityInAnyOrder(IsEqualToOwner::equalToOwnerWithoutRelations, owners);
	}
}
