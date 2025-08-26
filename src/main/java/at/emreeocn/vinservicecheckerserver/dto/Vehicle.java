package at.emreeocn.vinservicecheckerserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Vehicle {

    @NotBlank(message = "VIN is required")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be exactly 17 characters and contain only valid characters")
    private String vin;

    @NotBlank(message = "Vehicle name is required")
    @Size(min = 1, max = 100, message = "Vehicle name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Vehicle type is required")
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
