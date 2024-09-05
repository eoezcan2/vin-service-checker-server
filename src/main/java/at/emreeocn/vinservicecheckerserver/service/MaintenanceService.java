package at.emreeocn.vinservicecheckerserver.service;

import at.emreeocn.vinservicecheckerserver.dto.request.MaintenanceRequest;
import at.emreeocn.vinservicecheckerserver.dto.response.MaintenanceResponse;
import at.emreeocn.vinservicecheckerserver.model.MaintenanceEntity;
import at.emreeocn.vinservicecheckerserver.model.VehicleEntity;
import at.emreeocn.vinservicecheckerserver.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private VehicleService vehicleService;

    public List<MaintenanceResponse> getMaintenancesByVehicle(VehicleEntity vehicle) {
        List<MaintenanceEntity> me = maintenanceRepository.findByVehicle(vehicle);
        return me.stream().map(maintenanceEntity -> {
            MaintenanceResponse maintenance = new MaintenanceResponse();
            maintenance.setId(maintenanceEntity.getId());
            maintenance.setDate(maintenanceEntity.getDate());
            maintenance.setCategory(maintenanceEntity.getCategory());
            maintenance.setDescription(maintenanceEntity.getDescription());
            maintenance.setMileage(maintenanceEntity.getMileage());
            maintenance.setCost(maintenanceEntity.getCost());
            return maintenance;
        }).toList().stream().sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).toList();
    }

    public MaintenanceEntity addMaintenance(MaintenanceRequest maintenanceRequest, String vin) {
        MaintenanceEntity me = new MaintenanceEntity();
        me.setVehicle(vehicleService.getVehicleEntityByVin(vin).orElse(null));
        me.setDate(maintenanceRequest.getDate());
        me.setCategory(maintenanceRequest.getCategory());
        me.setDescription(maintenanceRequest.getDescription());
        me.setMileage(maintenanceRequest.getMileage());
        me.setCost(maintenanceRequest.getCost());
        return maintenanceRepository.save(me);
    }

    public Optional<MaintenanceEntity> deleteMaintenance(Long id) {
        Optional<MaintenanceEntity> me = maintenanceRepository.findById(id);
        me.ifPresent(maintenanceRepository::delete);
        return me;
    }

    public Optional<MaintenanceEntity> getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id);
    }
}
