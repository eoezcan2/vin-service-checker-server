package at.emreeocn.vinservicecheckerserver.controller;

import at.emreeocn.vinservicecheckerserver.dto.request.MaintenanceRequest;
import at.emreeocn.vinservicecheckerserver.dto.Vehicle;
import at.emreeocn.vinservicecheckerserver.dto.response.MaintenanceResponse;
import at.emreeocn.vinservicecheckerserver.dto.response.VehicleResponse;
import at.emreeocn.vinservicecheckerserver.model.MaintenanceEntity;
import at.emreeocn.vinservicecheckerserver.model.VehicleEntity;
import at.emreeocn.vinservicecheckerserver.security.UserPrincipal;
import at.emreeocn.vinservicecheckerserver.service.MaintenanceService;
import at.emreeocn.vinservicecheckerserver.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private MaintenanceService maintenanceService;

    /**
     * Create a vehicle
     * @param vehicle   The vehicle data
     * @return  The created vehicle
     */
    @PostMapping("/vin")
    public ResponseEntity<VehicleEntity> createVehicle(@RequestBody Vehicle vehicle) {
        if (!vehicleService.vinValid(vehicle.getVin())) return ResponseEntity.badRequest().build();
        VehicleEntity ve = new VehicleEntity();
        ve.setVin(vehicle.getVin());
        ve.setName(vehicle.getName());
        ve.setType(vehicle.getType());
        if (vehicleService.vinExists(ve.getVin())) return ResponseEntity.badRequest().body(ve);
        vehicleService.addVehicle(ve);
        return ResponseEntity.ok(ve);
    }

    /**
     * Get all vehicles of the current user
     * @return  A list of vehicles
     */
    @GetMapping("/vin/list")
    public ResponseEntity<List<VehicleResponse>> getVehicles() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<VehicleResponse> ve = vehicleService.getVehiclesByOwner(userPrincipal.getUser());
        return !ve.isEmpty() ? ResponseEntity.ok(ve) : ResponseEntity.notFound().build();
    }

    /**
     * Check if a vehicle exists by VIN
     * @param vin   The VIN of the vehicle
     * @return  The vehicle existence
     */
    @GetMapping("/vin/{vin}")
    public ResponseEntity<VehicleEntity> getVehicleExistence(@PathVariable(value="vin") String vin) {
        Optional<VehicleResponse> ve = vehicleService.getVehicleByVin(vin);
        return ve.isPresent() ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * Get vehicle data by VIN
     * @param vin   The VIN of the vehicle
     * @return  The vehicle data
     */
    @GetMapping("/vin/{vin}/data")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable(value="vin") String vin) {
        Optional<VehicleResponse> ve = vehicleService.getVehicleByVin(vin);
        return ve.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a maintenance entry for a vehicle
     * @param maintenanceRequest    The maintenance request
     * @return  The created maintenance entry
     */
    @PostMapping("/maintenance")
    public ResponseEntity<MaintenanceEntity> createMaintenance(@RequestBody MaintenanceRequest maintenanceRequest) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String vin = maintenanceRequest.getVin();

        if (!vehicleService.vinExists(vin)) return ResponseEntity.notFound().build();
        if (vehicleService.isVehicleOwner(vin, userPrincipal.getUsername())) return ResponseEntity.badRequest().build();
        if (maintenanceRequest.getMileage() < 0 || maintenanceRequest.getCost() < 0) return ResponseEntity.badRequest().build();
        if (maintenanceRequest.getCategory().toString().isEmpty()) return ResponseEntity.badRequest().build();

        MaintenanceEntity me = maintenanceService.addMaintenance(maintenanceRequest, vin);
        return ResponseEntity.ok(me);
    }

    /**
     * Get all maintenance entries for a vehicle
     * @param vin   The VIN of the vehicle
     * @return    A list of maintenance entries
     */
    @GetMapping("/maintenance/{vin}")
    public ResponseEntity<List<MaintenanceResponse>> getVehicleMaintenance(@PathVariable(value="vin") String vin) {
        if (!vehicleService.vinExists(vin)) return ResponseEntity.notFound().build();

        VehicleEntity ve = vehicleService.getVehicleEntityByVin(vin).orElse(null);
        if(ve == null) return ResponseEntity.notFound().build();

        List<MaintenanceResponse> me = maintenanceService.getMaintenancesByVehicle(ve);
        return !me.isEmpty() ? ResponseEntity.ok(me) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/maintenance/{id}")
    public ResponseEntity<MaintenanceEntity> deleteMaintenance(@PathVariable(value="id") Long id) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MaintenanceEntity me1 = maintenanceService.getMaintenanceById(id).orElse(null);
        if (me1 == null) return ResponseEntity.notFound().build();
        if (!vehicleService.isVehicleOwner(me1.getVehicle().getVin(), userPrincipal.getUsername())) return ResponseEntity.badRequest().build();
        Optional<MaintenanceEntity> me2 = maintenanceService.deleteMaintenance(id);
        return me2.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
