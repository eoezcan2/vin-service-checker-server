package at.emreeocn.vinservicecheckerserver.dto;

public class Vehicle {

    private String vin;

    private String name;

    private VehicleType type;

    public String getVin() {
        return vin;
    }

    public String getName() {
        return name;
    }

    public VehicleType getType() {
        return type;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }
}
