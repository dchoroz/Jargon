package extra;

import javax.persistence.Embeddable;

@Embeddable
public class Name {
    String lname;
    String fname;

    public Name() {
    }

    public Name(String lname, String fname) {
        this.lname = lname;
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }
    
    
}
