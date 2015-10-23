package es.uvigo.esei.xcs.domain.entities;

import static es.uvigo.esei.xcs.domain.entities.IsEqualsToPet.containsPetsWithoutRelationsInAnyOrder;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsEqualsToOwner extends IsEqualsToEntity<Owner> {
	private final boolean checkRelations;
	
	public IsEqualsToOwner(Owner owner, boolean checkRelations) {
		super(owner);
		this.checkRelations = checkRelations;
	}

	@Override
	protected boolean matchesSafely(Owner actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("owner", expected.toString());
			return false;
		} else if (!expected.getLogin().equals(actual.getLogin())) {
			this.addTemplatedDescription("login", expected.getLogin());
			return false;
		} else if (!expected.getPassword().equals(actual.getPassword())) {
			this.addTemplatedDescription("password", expected.getPassword());
			return false;
		} else if (this.checkRelations) {
			final Matcher<Iterable<? extends Pet>> petsMatcher =
				containsPetsWithoutRelationsInAnyOrder(
					expected.getPets().toArray(new Pet[0]));
			
			if (petsMatcher.matches(actual.getPets())) {
				this.addMatcherDescription(petsMatcher);
				
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Factory
	public static IsEqualsToOwner equalsToOwner(Owner owner) {
		return new IsEqualsToOwner(owner, true);
	}
	
	@Factory
	public static IsEqualsToOwner equalsToOwnerWithoutRelations(Owner owner) {
		return new IsEqualsToOwner(owner, false);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Owner>> containsOwnersInAnyOrder(Owner ... owners) {
		return containsEntityInAnyOrder(IsEqualsToOwner::equalsToOwner, owners);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Owner>> containsOwnersWithoutRelationsInAnyOrder(Owner ... owners) {
		return containsEntityInAnyOrder(IsEqualsToOwner::equalsToOwnerWithoutRelations, owners);
	}
}
