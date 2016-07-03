package extra;

import annotations.Include;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Include
public abstract class Being implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long ic;
    boolean alive;    
   
    public Being(){
        
    }
    
    public long getIc() {
        return ic;
    }

    public void setIc(long ic) {
        this.ic = ic;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }    
}
