
package no.robert.apitesting.model;
import java.util.Set;


import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;;

@javax.persistence.metamodel.StaticMetamodel(no.robert.apitesting.model.Book.class)

public class Book_ {

	public static volatile SingularAttribute<Book, String> title;
	public static volatile SetAttribute<Book, Set<Author>> authors;
	public static volatile SingularAttribute<Book,Publisher> publisher;
	public static volatile SingularAttribute<Book, Long> id;
	public static volatile SingularAttribute<Book,Integer> pages;
	public static volatile SingularAttribute<Book,Double> price;
	public static volatile SingularAttribute<Book,Boolean> available;
	public static volatile SingularAttribute<Book,Boolean> isPaperback;
}