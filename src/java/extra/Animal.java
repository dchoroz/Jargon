package extra;

import annotations.Extpose;
import annotations.Merge;
import java.io.Serializable;
import javax.persistence.Entity;

@Entity
@Extpose
@Merge(Being.class)
public class Animal extends Being implements Serializable{
    
    String name;   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
