package es.uvigo.esei.xcs.domain.entities;

import static java.util.Arrays.copyOfRange;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class OwnerTest {
	private String login;
	private String password;
	private String md5Pass;
	private Pet[] pets;
	
	private String newLogin;
	private String newPassword;
	private String newPasswordMD5;
	private Pet petOwned;
	private Pet petNotOwned;
	private Pet[] petsWithoutOwned;
	
	private String shortLogin;
	private String longLogin;
	private String shortPassword;

	@Before
	public void setUp() throws Exception {
		this.login = "Pepe";
		this.password = "pepepa";
		this.md5Pass = "41B0EEB2550AE3A43BF34DC2E8408E90";
		this.pets = new Pet[] {
			new Pet("Lassie", AnimalType.DOG, new Date()),
			new Pet("Pajaroto", AnimalType.BIRD, new Date())
		};
		
		this.newLogin = "José";
		this.newPassword = "josepass";
		this.newPasswordMD5 = "A3F6F4B40B24E2FD61F08923ED452F34";
		this.petNotOwned = new Pet("Doraemon", AnimalType.CAT, new Date());
		this.petOwned = this.pets[1];
		this.petsWithoutOwned = copyOfRange(this.pets, 0, 1);
		
		this.shortLogin = "";
		this.longLogin = repeat('A', 101); 
		this.shortPassword = repeat('A', 5);
	}

	@Test
	public void testOwnerStringString() {
		final String[] logins = { login, "A", repeat('A', 100) };
		
		for (String login : logins) {
			final Owner owner = new Owner(login, password);
	
			assertThat(owner.getLogin(), is(equalTo(login)));
			assertThat(owner.getPassword(), is(equalTo(md5Pass)));
			assertThat(owner.getPets(), is(emptyIterable()));
		}
	}

	@Test(expected = NullPointerException.class)
	public void testOwnerStringStringNullLogin() {
		new Owner(null, password);
	}
	
	@Test(expected = NullPointerException.class)
	public void testOwnerStringStringNullPassword() {
		new Owner(login, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testOwnerStringStringLoginTooShort() {
		new Owner(shortLogin, password);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testOwnerStringStringLoginTooLong() {
		new Owner(longLogin, password);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testOwnerStringStringPasswordTooShort() {
		new Owner(login, shortPassword);
	}
	
	@Test
	public void testOwnerStringStringCollection() {
		final String[] logins = { login, "A", repeat('A', 100) };
		
		for (String login : logins) {
			final Owner owner = new Owner(login, password, pets);
			
			assertThat(owner.getLogin(), is(equalTo(login)));
			assertThat(owner.getPassword(), is(equalTo(md5Pass)));
			assertThat(owner.getPets(), containsInAnyOrder(pets));
		}
	}
	
	@Test
	public void testOwnerStringStringCollectionEmptyPets() {
		final Owner owner = new Owner(login, password, new Pet[0]);
		
		assertThat(owner.getLogin(), is(equalTo(login)));
		assertThat(owner.getPassword(), is(equalTo(md5Pass)));
		assertThat(owner.getPets(), is(emptyIterable()));
	}

	@Test(expected = NullPointerException.class)
	public void testOwnerStringStringCollectionNullLogin() {
		new Owner(null, password, pets);
	}
	
	@Test(expected = NullPointerException.class)
	public void testOwnerStringStringCollectionNullPassword() {
		new Owner(login, null, pets);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testOwnerStringStringCollectionLoginTooShort() {
		new Owner(shortLogin, password, pets);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testOwnerStringStringCollectionLoginTooLong() {
		new Owner(longLogin, password, pets);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testOwnerStringStringCollectionPasswordTooShort() {
		new Owner(login, shortPassword, pets);
	}
	
	@Test(expected = NullPointerException.class)
	public void testOwnerStringStringCollectionNullPets() {
		new Owner(login, password, (Pet[]) null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testOwnerStringStringCollectionPasswordPetsWithNull() {
		new Owner(login, password, new Pet[] { petNotOwned, null });
	}

	@Test
	public void testSetLogin() {
		final String[] logins = { login, "A", repeat('A', 100) };
		
		for (String login : logins) {
			final Owner owner = new Owner(login, password);
			
			owner.setLogin(newLogin);
	
			assertThat(owner.getLogin(), is(equalTo(newLogin)));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetLoginTooShort() {
		final Owner owner = new Owner(login, password);
		
		owner.setLogin(shortLogin);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetLoginTooLong() {
		final Owner owner = new Owner(login, password);
		
		owner.setLogin(longLogin);
	}

	@Test
	public void testSetPassword() {
		final Owner owner = new Owner(login, password);
		
		owner.setPassword(newPasswordMD5);

		assertThat(owner.getPassword(), is(equalTo(newPasswordMD5)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetPasswordNoMD5() {
		final Owner owner = new Owner(login, password);
		
		owner.setPassword("No MD5 password");
	}

	@Test(expected = NullPointerException.class)
	public void testSetPasswordNullValue() {
		final Owner owner = new Owner(login, password);
		
		owner.setPassword(null);
	}

	@Test
	public void testChangePassword() {
		final Owner owner = new Owner(login, password);
		
		owner.changePassword(newPassword);

		assertThat(owner.getPassword(), is(equalTo(newPasswordMD5)));
	}

	@Test(expected = NullPointerException.class)
	public void testChangePasswordNull() {
		final Owner owner = new Owner(login, password);
		
		owner.changePassword(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testChangePasswordTooShort() {
		final Owner owner = new Owner(login, password);
		
		owner.changePassword(shortPassword);
	}

	@Test
	public void testAddPet() {
		final Owner owner = new Owner(login, password);
		
		owner.addPet(petNotOwned);
		
		assertThat(owner.getPets(), contains(petNotOwned));
		assertThat(petNotOwned.getOwner(), is(owner));
	}

	@Test
	public void testAddPetAlreadyOwned() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.addPet(petOwned);
		
		assertThat(owner.getPets(), containsInAnyOrder(pets));
	}

	@Test(expected = NullPointerException.class)
	public void testAddPetNull() {
		final Owner owner = new Owner(login, password);
		
		owner.addPet(null);
	}

	@Test
	public void testRemovePet() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.removePet(petOwned);
		assertThat(owner.getPets(), contains(petsWithoutOwned));
		assertThat(petOwned.getOwner(), is(nullValue()));
	}

	@Test(expected = NullPointerException.class)
	public void testRemovePetNull() {
		final Owner owner = new Owner(login, password);
		
		owner.removePet(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemovePetNotOwned() {
		final Owner owner = new Owner(login, password);
		
		owner.removePet(petNotOwned);
	}

	@Test
	public void testOwnsPet() {
		final Owner owner = new Owner(login, password, pets);

		for (Pet pet : pets) {
			assertThat(owner.ownsPet(pet), is(true));
		}
		assertThat(owner.ownsPet(petNotOwned), is(false));
	}

	@Test
	public void testOwnsPetNotOwned() {
		final Owner owner = new Owner(login, password, pets);

		assertThat(owner.ownsPet(petNotOwned), is(false));
	}

	@Test
	public void testOwnsPetNull() {
		final Owner owner = new Owner(login, password, pets);

		assertThat(owner.ownsPet(null), is(false));
	}

	@Test
	public void testInternalAddPet() {
		final Owner owner = new Owner(login, password);
		
		owner.internalAddPet(petNotOwned);
		
		assertThat(owner.getPets(), contains(petNotOwned));
	}

	@Test
	public void testInternalAddPetAlreadyOwned() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.internalAddPet(petOwned);
		
		assertThat(owner.getPets(), containsInAnyOrder(pets));
	}

	@Test(expected = NullPointerException.class)
	public void testInternalAddPetNull() {
		final Owner owner = new Owner(login, password);
		
		owner.internalAddPet(null);
	}

	@Test
	public void testInternalRemovePet() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.internalRemovePet(petOwned);
		assertThat(owner.getPets(), contains(petsWithoutOwned));
	}

	@Test(expected = NullPointerException.class)
	public void testSetLoginNullValue() {
		final Owner owner = new Owner(login, password);
		
		owner.setLogin(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testInternalRemovePetNull() {
		final Owner owner = new Owner(login, password, pets);
		
		owner.internalRemovePet(null);
	}
}
