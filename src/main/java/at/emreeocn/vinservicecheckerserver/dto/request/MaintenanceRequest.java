package at.emreeocn.vinservicecheckerserver.dto.request;

import at.emreeocn.vinservicecheckerserver.dto.MaintenanceCategory;
import jakarta.validation.constraints.*;

import java.util.Date;

public class MaintenanceRequest {

    @NotBlank(message = "VIN is required")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be exactly 17 characters and contain only valid characters")
    private String vin;
    
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private Date date;
    
    @NotNull(message = "Category is required")
    private MaintenanceCategory category;
    
    @NotBlank(message = "Description is required")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;
    
    @Min(value = 0, message = "Mileage cannot be negative")
    @Max(value = 999999, message = "Mileage cannot exceed 999,999")
    private int mileage;
    
    @DecimalMin(value = "0.0", message = "Cost cannot be negative")
    @DecimalMax(value = "999999.99", message = "Cost cannot exceed 999,999.99")
    private float cost;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public MaintenanceCategory getCategory() {
        return category;
    }

    public void setCategory(MaintenanceCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
}
