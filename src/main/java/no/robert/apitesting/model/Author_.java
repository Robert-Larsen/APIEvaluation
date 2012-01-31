package no.robert.apitesting.model;

import javax.persistence.metamodel.SingularAttribute;;

@javax.persistence.metamodel.StaticMetamodel(no.robert.apitesting.model.Author.class)

public class Author_ {

	public static volatile SingularAttribute<Author, Long> id;
	public static volatile SingularAttribute<Author,String> name;

}