package no.robert.apitesting;


import static javax.persistence.Persistence.createEntityManagerFactory;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import no.robert.apitesting.model.Author;
import no.robert.apitesting.model.Book;
import no.robert.apitesting.model.Publisher;
import no.robert.apitesting.model.QAuthor;
import no.robert.apitesting.model.QBook;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;

public class AppTest
{
	private EntityManager entityManager;
	private Book aBook, anotherBook;
	private Author author, anotherAuthor;
	private Publisher publisher, anotherPublisher;


	@Before
	public void onlyOnce() {
		entityManager = createEntityManagerFactory("no.robert.apitesting").createEntityManager();
		entityManager.getTransaction().begin();

		publisher = new Publisher("Manning");
		anotherPublisher = new Publisher("Addison-Wesley");
		author = new Author("Rune Flobakk");
		anotherAuthor = new Author("Robert Larsen");

		aBook = new Book("A booktitle", author, 10, publisher);
		anotherBook = new Book("A booktitle 2.0", anotherAuthor, 100, anotherPublisher);

		entityManager.persist(publisher);
		entityManager.persist(anotherPublisher);
		entityManager.persist(author);
		entityManager.persist(anotherAuthor);
		entityManager.persist(aBook);
		entityManager.persist(anotherBook);
	}

	@After
	public void rollback() {
		entityManager.getTransaction().rollback();
	}

	@Test
	public void querydsl() {
		JPAQuery query = new JPAQuery ( entityManager ) ;
		QBook book = QBook.book;
		QAuthor author = QAuthor.author;
		List<Book> books = query.from(book).
				where(book.authors.contains(
						new JPASubQuery().from(author).where(author.name.eq("Rune Flobakk")).unique(author)))
						.list(book);
		assertThat( books, hasSize(1));
		assertThat(books, hasItem(aBook));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void hibernatecriteria() {
		Session session = (Session) entityManager.getDelegate();
		Criteria criteria = session.createCriteria(Book.class, "book");
		criteria.createAlias("book.authors", "authors");
		criteria.add(Restrictions.eq("authors.name", "Rune Flobakk"));
		List<Book> books = criteria.list();
		assertThat( books, hasSize(1));
		assertThat(books, hasItem(aBook));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void jpacriteria() {


		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Book> criteria = criteriaBuilder.createQuery(Book.class);
		Root<Book> root = criteria.from(Book.class);
		criteria.select(root);
		Expression<Collection<Author>> authors = root.get("authors");
		Subquery subquery = criteria.subquery(Author.class);
		Root<Author> subroot = subquery.from(Author.class);
		Predicate condition = criteriaBuilder.equal(subroot.get("name"),"Rune Flobakk");
		subquery.select(subroot).where(condition);
		criteria.where( criteriaBuilder.isMember( subquery , authors));
		List<Book> books = entityManager.createQuery(criteria).getResultList();
		assertThat( books, hasSize(1));
		assertThat( books, hasItem( aBook));		
	}
}
