package at.emreeocn.vinservicecheckerserver.repository;

import at.emreeocn.vinservicecheckerserver.model.UserEntity;
import at.emreeocn.vinservicecheckerserver.model.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

    Optional<VehicleEntity> findVehicleEntityByVin(String vin);

    List<VehicleEntity> findVehicleEntitiesByOwner(UserEntity owner);

}
