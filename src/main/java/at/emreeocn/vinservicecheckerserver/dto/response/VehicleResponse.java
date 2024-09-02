package at.emreeocn.vinservicecheckerserver.dto.response;

import at.emreeocn.vinservicecheckerserver.dto.VehicleType;

public class VehicleResponse {

    private String vin;
    private String name;
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
