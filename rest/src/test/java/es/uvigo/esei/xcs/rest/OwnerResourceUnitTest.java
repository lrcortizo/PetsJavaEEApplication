package es.uvigo.esei.xcs.rest;

import static es.uvigo.esei.xcs.domain.entities.IsEqualsToOwner.containsOwnersInAnyOrder;
import static es.uvigo.esei.xcs.domain.entities.IsEqualsToOwner.equalsToOwner;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.anyLogin;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.anyOwner;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.newOwnerWithFreshPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.newOwnerWithPersistentPets;
import static es.uvigo.esei.xcs.domain.entities.OwnersDataset.owners;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.esei.xcs.domain.entities.Owner;
import es.uvigo.esei.xcs.domain.entities.OwnersDataset;
import es.uvigo.esei.xcs.service.OwnerService;

@RunWith(EasyMockRunner.class)
public class OwnerResourceUnitTest extends EasyMockSupport {
	@TestSubject
	private OwnerResource resource = new OwnerResource();
	
	@Mock
	private OwnerService facade;
	
	@Mock
	private UriInfo uriInfo;
	
	@Mock
	private UriBuilder uriBuilder;

	@After
	public void tearDown() throws Exception {
		verifyAll();
	}

	@Test
	public void testGet() {
		final Owner owner = OwnersDataset.anyOwner();
		
		expect(facade.get(owner.getLogin()))
			.andReturn(owner);
		
		replayAll();
		
		final Response response = resource.get(owner.getLogin());
		
		assertThat(response.getStatusInfo(), is(equalTo(Response.Status.OK)));
		assertThat(response.getEntity(), is(instanceOf(Owner.class)));
		assertThat((Owner) response.getEntity(), is(equalsToOwner(owner)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNull() {
		replayAll();
		
		resource.get(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMissing() {
		final String login = anyLogin();
		
		expect(facade.get(login))
			.andReturn(null);
		
		replayAll();
		
		resource.get(login);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testList() {
		final Owner[] owners = owners();
		
		expect(facade.list())
			.andReturn(asList(owners));
		
		replayAll();
		
		final Response response = resource.list();
		
		assertThat(response.getStatusInfo(), is(equalTo(Response.Status.OK)));
		assertThat(response.getEntity(), is(instanceOf(List.class)));
		assertThat((List<Owner>) response.getEntity(), containsOwnersInAnyOrder(owners));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testListEmpty() {
		expect(facade.list())
			.andReturn(emptyList());
		
		replayAll();
		
		final Response response = resource.list();
		
		assertThat(response.getStatusInfo(), is(equalTo(Response.Status.OK)));
		assertThat(response.getEntity(), is(instanceOf(List.class)));
		assertThat((List<Owner>) response.getEntity(), is(empty()));
	}

	@Test
	public void testCreate() throws Exception {
		final Owner newOwner = newOwnerWithFreshPets();
		final Owner createdOwner = newOwnerWithPersistentPets();
		
		final URI mockUri = new URI("http://host/api/owner/" + newOwner.getLogin());
		
		expect(facade.create(newOwner))
			.andReturn(createdOwner);
		
		expect(uriInfo.getAbsolutePathBuilder())
			.andReturn(uriBuilder);
		expect(uriBuilder.path(newOwner.getLogin()))
			.andReturn(uriBuilder);
		expect(uriBuilder.build())
			.andReturn(mockUri);
		
		replayAll();
		
		final Response response = resource.create(newOwner);
		
		assertThat(response.getStatusInfo(), is(equalTo(Response.Status.CREATED)));
		assertThat(response.getHeaderString("Location"), is(equalTo(mockUri.toString())));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNull() {
		replayAll();
		
		resource.create(null);
	}

	@Test
	public void testUpdate() {
		final Owner owner = anyOwner();
		
		facade.update(owner);
		
		expectLastCall().andReturn(owner);
		
		replayAll();
		
		final Response response = resource.update(owner);
		
		assertThat(response.getStatusInfo(), is(equalTo(Response.Status.OK)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateNull() {
		replayAll();
		
		resource.update(null);
	}

	@Test
	public void testDelete() {
		final String login = OwnersDataset.anyLogin();
		
		facade.remove(login);
		
		replayAll();
		
		final Response response = resource.delete(login);
		
		assertThat(response.getStatusInfo(), is(equalTo(Response.Status.OK)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteNull() {
		replayAll();
		
		resource.delete(null);
	}
}
