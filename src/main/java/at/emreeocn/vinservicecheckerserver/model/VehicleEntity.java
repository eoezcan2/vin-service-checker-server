package at.emreeocn.vinservicecheckerserver.model;

import at.emreeocn.vinservicecheckerserver.dto.VehicleType;
import jakarta.persistence.*;

@Entity
@Table(name="vehicles")
public class VehicleEntity {

    @Id
    private String vin;

    @ManyToOne
    private UserEntity owner;

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    private String name;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }
}
