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

    public boolean vinExists(String vin) {
        return vehicleRepository.findVehicleEntityByVin(vin).isPresent();
    }

    public boolean vinValid(String vin) {
        return vin.length() == 17;
    }

    public boolean isVehicleOwner(String vin, String username) {
        Optional<VehicleEntity> ve = vehicleRepository.findVehicleEntityByVin(vin);
        return ve.isPresent() && ve.get().getName().equals(username);
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
