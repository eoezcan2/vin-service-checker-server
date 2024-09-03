package at.emreeocn.vinservicecheckerserver.repository;

import at.emreeocn.vinservicecheckerserver.model.MaintenanceEntity;
import at.emreeocn.vinservicecheckerserver.model.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository extends JpaRepository<MaintenanceEntity, Long> {

    List<MaintenanceEntity> findByVehicle(VehicleEntity vehicle);

    Optional<MaintenanceEntity> findById(Long id);

}
