package car.rental.core.vehicleequipment.service;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.equipment.service.EquipmentService;
import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.service.VehicleService;
import car.rental.core.vehicleequipment.domain.model.VehicleEquipment;
import car.rental.core.vehicleequipment.dto.CreateVehicleEquipmentRequest;
import car.rental.core.vehicleequipment.infrastructure.persistence.PanacheVehicleEquipmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class VehicleEquipmentService {

    private final PanacheVehicleEquipmentRepository panacheVehicleEquipmentRepository;
    private final VehicleService vehicleService;
    private final EquipmentService equipmentService;

    @Transactional
    public VehicleEquipment createVehicleEquipment(CreateVehicleEquipmentRequest request) {
        final Vehicle vehicleById = vehicleService.findVehicleById(request.getVehicleId());
        final Equipment equipmentById = equipmentService.findEquipmentById(request.getEquipmentId());
        VehicleEquipment vehicleEquipment = VehicleEquipment.builder().vehicle(vehicleById).equipment(equipmentById).build();
        return panacheVehicleEquipmentRepository.save(vehicleEquipment);
    }

    @Transactional
    public void deleteVehicleEquipment(Long vehicleId, Long equipmentId) {
        panacheVehicleEquipmentRepository.deleteByVehicleIdAndEquipmentId(vehicleId, equipmentId);
    }

    public List<Equipment> findEquipment(Long vehicleId) {
        return panacheVehicleEquipmentRepository.findEquipment(vehicleId);
    }
}
