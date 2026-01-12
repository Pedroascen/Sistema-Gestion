package sv.ascen2000.InventorySystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Permission(){
    }

    public Permission(String perName){
        this.name = perName;
    }

    //G and S
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
