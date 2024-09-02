package at.emreeocn.vinservicecheckerserver.model;

import at.emreeocn.vinservicecheckerserver.dto.MaintenanceCategory;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "maintenances")
public class MaintenanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date date;

    @ManyToOne
    private VehicleEntity vehicle;

    @Enumerated(EnumType.STRING)
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public VehicleEntity getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleEntity vehicle) {
        this.vehicle = vehicle;
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

    public void setCost(float price) {
        this.cost = price;
    }
}
