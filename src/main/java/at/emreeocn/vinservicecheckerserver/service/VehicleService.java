package at.emreeocn.vinservicecheckerserver.service;

import at.emreeocn.vinservicecheckerserver.dto.Vehicle;
import at.emreeocn.vinservicecheckerserver.dto.response.VehicleResponse;
import at.emreeocn.vinservicecheckerserver.model.UserEntity;
import at.emreeocn.vinservicecheckerserver.model.VehicleEntity;
import at.emreeocn.vinservicecheckerserver.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle mapVehicleEntityToVehicle(VehicleEntity vehicleEntity) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(vehicleEntity.getVin());
        vehicle.setName(vehicleEntity.getName());
        vehicle.setType(vehicleEntity.getType());
        return vehicle;
    }

    public Optional<VehicleResponse> getVehicleByVin(String vin) {
        Optional<VehicleEntity> ve = vehicleRepository.findVehicleEntityByVin(vin);
        return ve.map(vehicleEntity -> {
            VehicleResponse vehicle = new VehicleResponse();
            vehicle.setVin(vehicleEntity.getVin());
            vehicle.setName(vehicleEntity.getName());
            vehicle.setType(vehicleEntity.getType());
            return vehicle;
        });
    }

    public Optional<VehicleEntity> getVehicleEntityByVin(String vin) {
        return vehicleRepository.findVehicleEntityByVin(vin);
    }

    public void addVehicle(VehicleEntity vehicle) {
        vehicleRepository.save(vehicle);
    }

    public VehicleEntity createVehicleWithOwner(Vehicle vehicle, UserEntity owner) {
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVin(sanitizeVin(vehicle.getVin()));
        vehicleEntity.setName(sanitizeString(vehicle.getName()));
        vehicleEntity.setType(vehicle.getType());
        vehicleEntity.setOwner(owner);
        return vehicleRepository.save(vehicleEntity);
    }

    private String sanitizeVin(String vin) {
        if (vin == null) return null;
        return vin.trim().toUpperCase();
    }

    private String sanitizeString(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("[<>\"']", "");
    }

    public VehicleResponse createVehicleResponse(VehicleEntity vehicleEntity) {
        VehicleResponse vehicleResponse = new VehicleResponse();
        vehicleResponse.setVin(vehicleEntity.getVin());
        vehicleResponse.setName(vehicleEntity.getName());
        vehicleResponse.setType(vehicleEntity.getType());
        return vehicleResponse;
    }

    public boolean vinExists(String vin) {
        return vehicleRepository.findVehicleEntityByVin(vin).isPresent();
    }

    public boolean vinValid(String vin) {
        if (vin == null || vin.length() != 17) {
            return false;
        }
        // VIN should only contain alphanumeric characters (excluding I, O, Q)
        return vin.matches("^[A-HJ-NPR-Z0-9]{17}$");
    }

    public boolean isVehicleOwner(String vin, Long id) {
        Optional<VehicleEntity> ve = vehicleRepository.findVehicleEntityByVin(vin);
        return ve.isPresent() && ve.get().getOwner().getId().equals(id);
    }

    public List<VehicleResponse> getVehiclesByOwner(UserEntity user) {
        ArrayList<VehicleResponse> vehicles = new ArrayList<>();
        vehicleRepository.findVehicleEntitiesByOwner(user).forEach(vehicleEntity -> {
            VehicleResponse vehicle = new VehicleResponse();
            vehicle.setVin(vehicleEntity.getVin());
            vehicle.setName(vehicleEntity.getName());
            vehicle.setType(vehicleEntity.getType());
            vehicles.add(vehicle);
        });
        return vehicles;
    }
}
