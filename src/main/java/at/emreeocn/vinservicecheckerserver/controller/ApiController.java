package at.emreeocn.vinservicecheckerserver.controller;

import at.emreeocn.vinservicecheckerserver.dto.Vehicle;
import at.emreeocn.vinservicecheckerserver.dto.request.MaintenanceRequest;
import at.emreeocn.vinservicecheckerserver.dto.response.MaintenanceResponse;
import at.emreeocn.vinservicecheckerserver.dto.response.VehicleResponse;
import at.emreeocn.vinservicecheckerserver.model.MaintenanceEntity;
import at.emreeocn.vinservicecheckerserver.model.VehicleEntity;
import at.emreeocn.vinservicecheckerserver.security.UserPrincipal;
import at.emreeocn.vinservicecheckerserver.service.MaintenanceService;
import at.emreeocn.vinservicecheckerserver.service.VehicleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

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
    public ResponseEntity<VehicleEntity> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        logger.info("Creating vehicle with VIN: {}", vehicle.getVin());
        
        if (!vehicleService.vinValid(vehicle.getVin())) {
            logger.warn("Invalid VIN format: {}", vehicle.getVin());
            return ResponseEntity.badRequest().build();
        }
        
        VehicleEntity ve = new VehicleEntity();
        ve.setVin(vehicle.getVin());
        ve.setName(vehicle.getName());
        ve.setType(vehicle.getType());
        
        if (vehicleService.vinExists(ve.getVin())) {
            logger.warn("VIN already exists: {}", vehicle.getVin());
            return ResponseEntity.badRequest().body(ve);
        }
        
        vehicleService.addVehicle(ve);
        logger.info("Vehicle created successfully with VIN: {}", vehicle.getVin());
        return ResponseEntity.ok(ve);
    }

    /**
     * Add a new vehicle for the current user
     * @param vehicle   The vehicle data
     * @return  The created vehicle response
     */
    @PostMapping("/vehicle/add")
    public ResponseEntity<VehicleResponse> addVehicle(@Valid @RequestBody Vehicle vehicle) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        logger.info("User {} adding vehicle with VIN: {}", userPrincipal.getUser().getId(), vehicle.getVin());
        
        // Validate VIN
        if (!vehicleService.vinValid(vehicle.getVin())) {
            logger.warn("User {} provided invalid VIN: {}", userPrincipal.getUser().getId(), vehicle.getVin());
            return ResponseEntity.badRequest().build();
        }
        
        // Check if VIN already exists
        if (vehicleService.vinExists(vehicle.getVin())) {
            logger.warn("User {} attempted to add existing VIN: {}", userPrincipal.getUser().getId(), vehicle.getVin());
            return ResponseEntity.badRequest().build();
        }
        
        // Create vehicle with owner
        VehicleEntity vehicleEntity = vehicleService.createVehicleWithOwner(vehicle, userPrincipal.getUser());
        VehicleResponse vehicleResponse = vehicleService.createVehicleResponse(vehicleEntity);
        
        logger.info("User {} successfully added vehicle with VIN: {}", userPrincipal.getUser().getId(), vehicle.getVin());
        return ResponseEntity.ok(vehicleResponse);
    }

    /**
     * Get all vehicles of the current user
     * @return  A list of vehicles
     */
    @GetMapping("/vin/list")
    public ResponseEntity<List<String>> getVehicles() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<VehicleResponse> ve = vehicleService.getVehiclesByOwner(userPrincipal.getUser());
        List <String> vinList = vehicleService.getVehiclesByOwner(userPrincipal.getUser()).stream().map(VehicleResponse::getVin).toList();
        return ResponseEntity.ok(vinList);
    }

    @GetMapping("/vin/list/data")
    public ResponseEntity<List<VehicleResponse>> getVehiclesData() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<VehicleResponse> ve = vehicleService.getVehiclesByOwner(userPrincipal.getUser());
        return ResponseEntity.ok(ve);
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
    public ResponseEntity<MaintenanceEntity> createMaintenance(@Valid @RequestBody MaintenanceRequest maintenanceRequest) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String vin = maintenanceRequest.getVin();

        logger.info("User {} creating maintenance for VIN: {}", userPrincipal.getUser().getId(), vin);

        if (!vehicleService.vinExists(vin)) {
            logger.warn("User {} attempted maintenance on non-existent VIN: {}", userPrincipal.getUser().getId(), vin);
            return ResponseEntity.notFound().build();
        }
        
        if (!vehicleService.isVehicleOwner(vin, userPrincipal.getUser().getId())) {
            logger.warn("User {} attempted maintenance on vehicle they don't own: {}", userPrincipal.getUser().getId(), vin);
            return ResponseEntity.badRequest().build();
        }

        MaintenanceEntity me = maintenanceService.addMaintenance(maintenanceRequest, vin);
        logger.info("User {} successfully created maintenance for VIN: {}", userPrincipal.getUser().getId(), vin);
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
        return ResponseEntity.ok(me);
    }

    @DeleteMapping("/maintenance/{id}")
    public ResponseEntity<MaintenanceEntity> deleteMaintenance(@PathVariable(value="id") Long id) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        logger.info("User {} attempting to delete maintenance with ID: {}", userPrincipal.getUser().getId(), id);
        
        MaintenanceEntity me1 = maintenanceService.getMaintenanceById(id).orElse(null);
        if (me1 == null) {
            logger.warn("User {} attempted to delete non-existent maintenance ID: {}", userPrincipal.getUser().getId(), id);
            return ResponseEntity.notFound().build();
        }
        
        if (!vehicleService.isVehicleOwner(me1.getVehicle().getVin(), userPrincipal.getUser().getId())) {
            logger.warn("User {} attempted to delete maintenance they don't own: {}", userPrincipal.getUser().getId(), id);
            return ResponseEntity.badRequest().build();
        }
        
        Optional<MaintenanceEntity> me2 = maintenanceService.deleteMaintenance(id);
        logger.info("User {} successfully deleted maintenance with ID: {}", userPrincipal.getUser().getId(), id);
        return me2.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
