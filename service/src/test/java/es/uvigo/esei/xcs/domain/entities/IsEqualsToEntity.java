package es.uvigo.esei.xcs.domain.entities;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public abstract class IsEqualsToEntity<T> extends TypeSafeMatcher<T> {
	protected final T expected;
	private Consumer<Description> describeTo;
	
	public IsEqualsToEntity(T entity) {
		this.expected = entity;
	}

	@Override
	public void describeTo(Description description) {
		if (this.describeTo != null) {
			this.describeTo.accept(description);
		}
	}
	
	protected void addTemplatedDescription(String attribute, Object expected) {
		this.describeTo = d -> d.appendText(String.format(
			"%s entity with value '%s' for %s",
			this.expected.getClass().getSimpleName(),
			expected, attribute
		));
	}
	
	protected void addMatcherDescription(Matcher<?> matcher) {
		this.describeTo = matcher::describeTo;
	}
	
	protected void clearDescribeTo() {
		this.describeTo = null;
	}
	
	@SafeVarargs
	protected static <T> Matcher<Iterable<? extends T>> containsEntityInAnyOrder(
		Function<T, Matcher<? super T>> converter, T ... entities
	) {
		final Collection<Matcher<? super T>> ownerMatchers = stream(entities)
			.map(converter)
		.collect(toList());
		
		return containsInAnyOrder(ownerMatchers);
	}
}
