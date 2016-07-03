package extra;

import annotations.Extpose;
import annotations.Merge;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PERSON")
@Extpose
@EntityListeners(core.EntityCrudListener.class)
@Merge(Being.class)
public class Person extends Being implements Serializable{
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    long ic;
    
     @Embedded 
     Name name;
     
//     String lname;
//     String fname;
     int age;
//    cascade= CascadeType.ALL,fetch=javax.persistence.FetchType.EAGER,
         @OneToMany( cascade= CascadeType.ALL,fetch=javax.persistence.FetchType.EAGER)//,mappedBy = "person")

     List<Phone> phoneNumbers = new ArrayList<Phone>();
    
    
    public Person() {
    }

    public Person(int ic) {
        this.ic = ic;
    }
    
    public Person(String fname, String lname, int age) {
        this.name = new Name();
        this.name.fname = fname;
        this.name.lname = lname;
        this.age = age;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

//    public Long getIc() {
//        return ic;
//    }
//
//    public void setIc(Long ic) {
//        this.ic = ic;
//    }

//    public String getLname() {
//        return lname;
//    }
//
//    public String getFname() {
//        return fname;
//    }
//
//    public void setLname(String lname) {
//        this.lname = lname;
//    }
//
//    public void setFname(String fname) {
//        this.fname = fname;
//    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
    public List<Phone> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<Phone> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public String toString() {
        return "basic.Person[ id=" + ic + ", fname ="+name.fname +" ]";
    }
}
