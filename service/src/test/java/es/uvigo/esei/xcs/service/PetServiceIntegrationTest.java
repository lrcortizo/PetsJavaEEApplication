package es.uvigo.esei.xcs.service;

import static es.uvigo.esei.xcs.domain.entities.IsEqualsToPet.containsPetsInAnyOrder;
import static es.uvigo.esei.xcs.domain.entities.IsEqualsToPet.equalsToPet;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.existentPetId;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.newPet;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.newPetWithOwner;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.nonExistentPetId;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.ownerWithPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.ownerWithoutPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.pet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.esei.xcs.domain.entities.AnimalType;
import es.uvigo.esei.xcs.domain.entities.Owner;
import es.uvigo.esei.xcs.domain.entities.OwnersDataset;
import es.uvigo.esei.xcs.domain.entities.Pet;
import es.uvigo.esei.xcs.service.util.security.RoleCaller;
import es.uvigo.esei.xcs.service.util.security.TestPrincipal;

@RunWith(Arquillian.class)
@UsingDataSet("owners.xml")
@CleanupUsingScript({ "cleanup.sql", "cleanup-autoincrement.sql" })
public class PetServiceIntegrationTest {
	@Inject
	private PetService facade;

	@EJB(beanName = "owner-caller")
	private RoleCaller asOwner;
	
	@Inject
	private TestPrincipal principal;
	
	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
			.addClasses(PetService.class, OwnersDataset.class)
			.addPackage(RoleCaller.class.getPackage())
			.addPackage(Pet.class.getPackage())
			.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
			.addAsWebInfResource("jboss-web.xml")
			.addAsWebInfResource("beans.xml", "beans.xml");

		return archive;
	}
	
	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testGet() throws LoginException {
		final int id = existentPetId();
		final Pet pet = pet(id);
		principal.setName(pet.getOwner().getLogin());
		
		final Pet actual = asOwner.call(() -> facade.get(id));
		
		assertThat(actual, equalsToPet(pet));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testGetBadId() throws LoginException {
		final int id = nonExistentPetId();
		
		principal.setName(ownerWithoutPets().getLogin());
		
		final Pet actual = asOwner.call(() -> facade.get(id));
		
		assertThat(actual, is(nullValue()));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testGetOthersPetId() throws LoginException {
		final Owner ownerWithoutPets = ownerWithoutPets();
		final Owner ownerWithPets = ownerWithPets();
		final int petId = ownerWithPets.getPets().iterator().next().getId();
		
		principal.setName(ownerWithoutPets.getLogin());
		
		asOwner.run(() -> facade.get(petId));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testList() throws LoginException {
		final Owner owner = ownerWithPets();
		final Pet[] ownedPets = owner.getPets().toArray(new Pet[0]);
		principal.setName(owner.getLogin());
		
		final List<Pet> pets = asOwner.call(() -> facade.list());
		
		assertThat(pets, containsPetsInAnyOrder(ownedPets));
	}

	@Test
	@ShouldMatchDataSet("owners.xml")
	public void testListNoPets() throws LoginException {
		final Owner owner = ownerWithoutPets();
		
		principal.setName(owner.getLogin());
		
		final List<Pet> pets = asOwner.call(() -> facade.list());
		
		assertThat(pets, is(empty()));
	}

	@Test
	@ShouldMatchDataSet({ "owners.xml", "owners-create-pet.xml" })
	public void testCreate() {
		final Owner owner = ownerWithoutPets();
		principal.setName(owner.getLogin());
		
		final Pet pet = newPetWithOwner(owner);
		
		asOwner.call(() -> facade.create(pet));
	}

	@Test
	@ShouldMatchDataSet({ "owners.xml", "owners-create-pet.xml" })
	public void testCreateNullOwner() {
		principal.setName(ownerWithoutPets().getLogin());
		
		final Pet pet = newPet();
		
		asOwner.run(() -> facade.create(pet));
	}
	
	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet({ "owners.xml" })
	public void testCreateNullPet() {
		principal.setName(ownerWithoutPets().getLogin());
		
		asOwner.run(() -> facade.create(null));
	}
	
	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet({ "owners.xml" })
	public void testCreateWrongOwner() {
		final Owner owner = ownerWithoutPets();
		final Owner otherOwner = ownerWithPets();
		
		principal.setName(owner.getLogin());
		
		final Pet pet = newPetWithOwner(otherOwner);
		
		asOwner.run(() -> facade.create(pet));
	}
	
	@Test
	@ShouldMatchDataSet("owners-update-pet.xml")
	public void testUpdate() throws LoginException {
		final int id = existentPetId();
		final Pet pet = pet(id);
		
		principal.setName(pet.getOwner().getLogin());
		
		pet.setName("UpdateName");
		pet.setAnimal(AnimalType.BIRD);
		pet.setBirth(new Date(946771261000L));
		
		asOwner.run(() -> facade.update(pet));
	}

	@Test
	@ShouldMatchDataSet({ "owners.xml", "owners-create-pet.xml" })
	public void testUpdateNewPetWithOwner() {
		final Owner owner = ownerWithoutPets();
		principal.setName(owner.getLogin());
		
		final Pet pet = newPetWithOwner(owner);
		
		asOwner.call(() -> facade.update(pet));
	}
	
	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testUpdateNull() throws LoginException {
		asOwner.run(() -> facade.update(null));
	}
	
	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet({ "owners.xml" })
	public void testUpdateWrongOwner() {
		final Owner owner = ownerWithoutPets();
		final Owner otherOwner = ownerWithPets();
		
		principal.setName(owner.getLogin());
		
		final Pet pet = otherOwner.getPets().iterator().next();
		
		asOwner.run(() -> facade.update(pet));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet({ "owners.xml", "owners-create-pet.xml" })
	public void testUpdatePetNoOwner() {
		final int id = existentPetId();
		final Pet pet = pet(id);
		
		principal.setName(pet.getOwner().getLogin());
		pet.setOwner(null);
		
		
		asOwner.run(() -> facade.update(pet));
	}

	@Test
	@ShouldMatchDataSet("owners-remove-pet.xml")
	public void testRemove() throws LoginException {
		final int id = existentPetId();
		final Pet pet = pet(id);
		principal.setName(pet.getOwner().getLogin());
		
		asOwner.run(() -> facade.remove(id));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testRemoveBadId() throws LoginException {
		final int id = nonExistentPetId();
		
		principal.setName(ownerWithoutPets().getLogin());
		
		asOwner.run(() -> facade.remove(id));
	}

	@Test(expected = EJBTransactionRolledbackException.class)
	@ShouldMatchDataSet("owners.xml")
	public void testRemoveOthersPetId() throws LoginException {
		final Owner ownerWithoutPets = ownerWithoutPets();
		final Owner ownerWithPets = ownerWithPets();
		final int petId = ownerWithPets.getPets().iterator().next().getId();
		
		principal.setName(ownerWithoutPets.getLogin());
		
		asOwner.run(() -> facade.remove(petId));
	}
}
