package car.rental.core.vehicleequipment.domain.repository;

import car.rental.core.common.domain.BaseRepository;
import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.vehicleequipment.domain.model.VehicleEquipment;

import java.util.List;

public interface VehicleEquipmentRepository extends BaseRepository<VehicleEquipment> {
    void deleteByVehicleIdAndEquipmentId(Long vehicleId, Long equipmentId);

    List<Equipment> findEquipment(Long vehicleId);
}
