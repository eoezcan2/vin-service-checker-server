package at.emreeocn.vinservicecheckerserver.dto.request;

import at.emreeocn.vinservicecheckerserver.dto.MaintenanceCategory;

import java.util.Date;

public class MaintenanceRequest {

    private String vin;
    private Date date;
    private MaintenanceCategory category;
    private String description;
    private int mileage;
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
