package car.rental.core.vehicle.service;

import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.dto.CreateVehicleRequest;
import car.rental.core.vehicle.dto.QueryVehicleRequest;
import car.rental.core.vehicle.dto.VehiclePageResponse;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import car.rental.core.vehicle.infrastructure.persistence.PanacheVehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class VehicleService {

    private final PanacheVehicleRepository panacheVehicleRepository;

    @Transactional
    public Vehicle createVehicle(CreateVehicleRequest request) {
        Vehicle vehicle = VehicleMapper.toDomain(request);
        return panacheVehicleRepository.save(vehicle);
    }

    public Vehicle findVehicleById(Long id) {
        return panacheVehicleRepository.findById(id).orElse(null);
    }

    public VehiclePageResponse findVehicles(QueryVehicleRequest query) {
        List<Vehicle> vehicles = panacheVehicleRepository.findByQuery(query);
        long totalRecords = panacheVehicleRepository.countByQuery(query);

        return VehiclePageResponse.builder()
                .data(vehicles)
                .metadata(VehiclePageResponse.PageMetadata.builder()
                        .page(query.getPage())
                        .size(query.getSize())
                        .totalRecords(totalRecords)
                        .currentPageUrl(buildPageUrl(query, query.getPage()))
                        .prevPageUrl(query.getPage() > 0 ? buildPageUrl(query, query.getPage() - 1) : null)
                        .nextPageUrl((query.getPage() + 1) * query.getSize() < totalRecords ? buildPageUrl(query, query.getPage() + 1) : null)
                        .build())
                .build();
    }

    private String buildPageUrl(QueryVehicleRequest query, int page) {
        StringBuilder url = new StringBuilder("/v1/vehicles?page=").append(page).append("&size=").append(query.getSize());

        if (query.getMake() != null && !query.getMake().trim().isEmpty()) {
            url.append("&make=").append(URLEncoder.encode(query.getMake().trim(), StandardCharsets.UTF_8));
        }

        if (query.getModel() != null && !query.getModel().trim().isEmpty()) {
            url.append("&model=").append(URLEncoder.encode(query.getModel().trim(), StandardCharsets.UTF_8));
        }

        if (query.getYear() != null) {
            url.append("&year=").append(query.getYear());
        }

        if (query.getVehicleType() != null) {
            url.append("&vehicleType=").append(query.getVehicleType());
        }

        if (query.getPassengers() != null) {
            url.append("&passengers=").append(query.getPassengers());
        }

        if (query.getDoors() != null) {
            url.append("&doors=").append(query.getDoors());
        }

        if (query.getFuelType() != null) {
            url.append("&fuelType=").append(query.getFuelType());
        }

        if (query.getTransmission() != null) {
            url.append("&transmission=").append(query.getTransmission());
        }

        if (query.getSort() != null && !query.getSort().trim().isEmpty()) {
            url.append("&sort=").append(URLEncoder.encode(query.getSort().trim(), StandardCharsets.UTF_8));
        }

        return url.toString();
    }
}
