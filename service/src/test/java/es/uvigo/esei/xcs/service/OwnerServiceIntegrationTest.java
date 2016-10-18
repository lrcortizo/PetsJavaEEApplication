package es.uvigo.esei.xcs.service;

import static es.uvigo.esei.xcs.domain.entities.IsEqualToOwner.containsOwnersInAnyOrder;
import static es.uvigo.esei.xcs.domain.entities.IsEqualToOwner.equalToOwner;
import static es.uvigo.esei.xcs.domain.entities.IsEqualToPet.containsPetsInAnyOrder;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.existentLogin;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.existentOwner;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.newOwnerWithFreshPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.newOwnerWithPersistentPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.newOwnerWithoutPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.newPasswordForExistentOwner;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.nonExistentLogin;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.nonExistentPetName;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.ownerWithLogin;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.ownerWithPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.ownerWithoutPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.owners;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.ownersOf;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.petNameWithMultipleOwners;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.petNameWithSingleOwner;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.esei.xcs.domain.entities.Owner;
import es.uvigo.esei.xcs.domain.entities.OwnersDataset;
import es.uvigo.esei.xcs.domain.entities.Pet;
import es.uvigo.esei.xcs.service.util.security.RoleCaller;

@RunWith(Arquillian.class)
@UsingDataSet("owners.xml")
@CleanupUsingScript({ "cleanup.sql", "cleanup-autoincrement.sql" })
public class OwnerServiceIntegrationTest {
	@Inject
	private OwnerService facade;
	
	@EJB(beanName = "admin-caller")
	private RoleCaller asAdmin;
	
	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
			.addClasses(OwnerService.class, OwnersDataset.class)
			.addPackage(RoleCaller.class.getPackage())
			.addPackage(Owner.class.getPackage())
			.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
			.addAsWebInfResource("jboss-web.xml")
			.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		
		return archive;
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testGetOwner() {
		final String login = existentLogin();
		
		final Owner actual = asAdmin.call(() -> facade.get(login));
		
		assertThat(actual, is(equalToOwner(ownerWithLogin(login))));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testGetOwnerNonExistent() {
		final String login = nonExistentLogin();
		
		final Owner actual = asAdmin.call(() -> facade.get(login));
		
		assertThat(actual, is(nullValue()));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testGetOwnerNull() {
		asAdmin.call(() -> facade.get(null));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testList() {
		final List<Owner> actual = asAdmin.call(() -> facade.list());
		
		assertThat(actual, is(containsOwnersInAnyOrder(owners())));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testFindByPetName() {
		final String petName = petNameWithSingleOwner();
		final Owner owner = ownersOf(petName)[0];
		
		final List<Owner> owners = asAdmin.call(() -> facade.findByPetName(petName));
		
		assertThat(owners, hasSize(1));
		assertThat(owners.get(0), is(equalToOwner(owner)));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testFindByPetNameMultipleOwners() {
		final String petName = petNameWithMultipleOwners();
		
		final List<Owner> owners = asAdmin.call(() -> facade.findByPetName(petName));
		
		final Owner[] expectedOwners = ownersOf(petName);
		
		assertThat(owners, containsOwnersInAnyOrder(expectedOwners));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testFindByPetNameNoPet() {
		final String pet = nonExistentPetName();
		
		final List<Owner> owners = asAdmin.call(() -> facade.findByPetName(pet));
		
		assertThat(owners, is(empty()));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testFindByPetNameNull() {
		asAdmin.run(() -> facade.findByPetName(null));
	}

	@Test
	@ShouldMatchDataSet({"owners.xml", "owners-create-without-pets.xml"})
	public void testCreateWithoutPets() {
		final Owner newOwner = newOwnerWithoutPets();
		
		final Owner actual = asAdmin.call(() -> facade.create(newOwner));
		
		assertThat(actual, is(equalToOwner(newOwner)));
	}

	@Test
	@ShouldMatchDataSet({"owners.xml", "owners-create-with-pets.xml"})
	public void testCreateWithPets() {
		final Owner actual = asAdmin.call(() -> facade.create(newOwnerWithFreshPets()));
		
		assertThat(actual, is(equalToOwner(newOwnerWithPersistentPets())));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testCreateExistentLogin() {
		asAdmin.run(() -> facade.create(existentOwner()));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testCreateNull() {
		asAdmin.call(() -> facade.create(null));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testUpdateNull() {
		asAdmin.run(() -> facade.update(null));
	}

	@Test
	@ShouldMatchDataSet("owners-update-password.xml")
	public void testUpdatePassword() {
		final Owner owner = existentOwner();
		owner.changePassword(newPasswordForExistentOwner());
		
		asAdmin.run(() -> facade.update(owner));
	}

	@Test
	@ShouldMatchDataSet({"owners.xml", "owners-create-without-pets.xml"})
	public void testUpdateNewOwnerWithoutPets() {
		final Owner newOwner = newOwnerWithoutPets();
		
		final Owner actual = asAdmin.call(() -> facade.update(newOwner));
		
		assertThat(actual, is(equalToOwner(newOwner)));
	}

	@Test
	@ShouldMatchDataSet({"owners.xml", "owners-create-with-pets.xml"})
	public void testUpdateNewOwnerWithPets() {
		final Owner actual = asAdmin.call(() -> facade.update(newOwnerWithFreshPets()));
		
		assertThat(actual, is(equalToOwner(newOwnerWithPersistentPets())));
	}

	@Test
	@ShouldMatchDataSet("owners-remove-without-pets.xml")
	public void testRemoveWithoutPets() {
		asAdmin.run(() -> facade.remove(ownerWithoutPets().getLogin()));
	}

	@Test
	@ShouldMatchDataSet("owners-remove-with-pets.xml")
	public void testRemoveWithPets() {
		asAdmin.run(() -> facade.remove(ownerWithPets().getLogin()));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testRemoveNonExistentOwner() {
		asAdmin.run(() -> facade.remove(nonExistentLogin()));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testRemoveNull() {
		asAdmin.run(() -> facade.remove(null));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testGetPets() {
		final Owner owner = ownerWithPets();
		final Pet[] ownedPets = owner.getPets().toArray(new Pet[0]);
		
		final List<Pet> pets = asAdmin.call(() -> facade.getPets(owner.getLogin()));
		
		assertThat(pets, containsPetsInAnyOrder(ownedPets));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testGetPetsNoPets() {
		final Owner owner = ownerWithoutPets();
		
		final List<Pet> pets = asAdmin.call(() -> facade.getPets(owner.getLogin()));
		
		assertThat(pets, is(empty()));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testGetPetsNonExistentOwner() {
		asAdmin.call(() -> facade.getPets(nonExistentLogin()));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testGetPetsNull() {
		asAdmin.call(() -> facade.getPets(null));
	}
}
