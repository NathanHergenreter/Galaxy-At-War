package net.sqltest.sql_stuff;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

//The Entity tag defines a class as being able to be mapped to a table and is mandatory
//Optional: A Table tag (ex: @Table(name = "Table_Country") ) can be included below Entity
@Entity
@Table(name = "country")
//Allows Hibernate to do changes automatically
//@DynamicUpdate
public class Country {
	
	// The Id tag annotates the id field and is mandatory
    @Id
    
    // Add the GeneratedValue tag to indicate the primary key of the entity
	/* (1) GenerationType.AUTO is the default generation type and lets the persistence 
	 * 	provider choose the generation strategy
	 * (2) GenerationType.IDENTITY relies on an auto-incremented database column 
	 * 	and lets the database generate a new value with each insert operation
	 *  Do not use with Hibernate
	 * (3) GenerationType.SEQUENCE uses a database sequence to generate unique values.
	 *  It requires additional select statements to get the next value from a database sequence
	 *  If you do not specify, the default sequence will be used. Otherwise do:
	 *  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_generator")
	 *  @SequenceGenerator(name="country_generator", sequenceName = "country_seq", allocationSize=50)
	 * (4) GenerationType.TABLE is the other option. It's slow so don't use
    */
    @GeneratedValue(strategy = GenerationType.AUTO)
    // Note: The Column tag is not necessary
    @Column(nullable = false)
    private Long id;
     
    @Column(nullable = false)
    private String name;

    @Cascade(CascadeType.ALL)
    @ManyToMany
    @JoinTable(name="Friends",
     joinColumns=@JoinColumn(name="countryId"),
     inverseJoinColumns=@JoinColumn(name="friendId")
    )
    private List<Country> friends = new ArrayList<>();

    @Cascade(CascadeType.ALL)
    @ManyToMany
    @JoinTable(name="Friends",
     joinColumns=@JoinColumn(name="friendId"),
     inverseJoinColumns=@JoinColumn(name="countryId")
    )
    private List<Country> friendOf = new ArrayList<>();
    
    //Note: The default constructor is required by JPA
    //It is set as protected as it will not be used directly
    protected Country() {}
    
    //Use this constructor to save instances of Country to be saved to the database
    public Country(String name) {
    	this.name = name;
    }
    
    public Long getId() { return id; }
    
    public void setName(String name)
    {
    	this.name = name;
    }
    
    public List<Country> getFriendOf()
    {
    	return friendOf;
    }
    
    public void addFriend(Country friend)
    {
    	friends.add(friend);
    	friend.getFriendOf().add(this);
    }
    
    //Prints out the country's attributes
    @Override
    public String toString() {
        return String.format(
                "Country[id=%d, name='%s']",
                id, name);
    }
    
}
