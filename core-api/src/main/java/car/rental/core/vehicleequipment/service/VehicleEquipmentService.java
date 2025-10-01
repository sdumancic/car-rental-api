package car.rental.core.vehicleequipment.service;

import car.rental.core.vehicleequipment.domain.model.VehicleEquipment;
import car.rental.core.vehicleequipment.dto.CreateVehicleEquipmentRequest;
import car.rental.core.vehicleequipment.infrastructure.mapper.VehicleEquipmentMapper;
import car.rental.core.vehicleequipment.infrastructure.persistence.PanacheVehicleEquipmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class VehicleEquipmentService {

    private final PanacheVehicleEquipmentRepository panacheVehicleEquipmentRepository;

    @Transactional
    public VehicleEquipment createVehicleEquipment(CreateVehicleEquipmentRequest request) {
        VehicleEquipment vehicleEquipment = VehicleEquipmentMapper.toDomain(request);
        return panacheVehicleEquipmentRepository.save(vehicleEquipment);
    }
}
