package car.rental.core.azure.service;

import car.rental.core.azure.dto.UploadResult;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@ApplicationScoped
public class AzureBlobService {

    private BlobServiceClient blobServiceClient;

    @ConfigProperty(name = "azure.storage.connection-string")
    String connectionString;

    @PostConstruct
    void init() {
        log.info("Azure Storage Connection String present: {}", connectionString != null);

        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        blobServiceClient.listBlobContainers().stream().forEach(containerName -> {
            log.info("Azure Blob Container Name: {}", containerName);
        });

        log.info("Azure Blob Service Client created");
    }

    /**
     * Uploads media to Azure Blob Storage.
     * For vehicles: containers vehicles-images or vehicles-videos with path vehicleId/filename
     * For customers: containers customers-images or customers-videos with path customerId/filename
     * Creates the container if it does not exist.
     */
    private String generateBlobNameWithPath(Long entityId, String original, String ext) {
        int dot = original.lastIndexOf('.');
        String base = (dot > 0 ? original.substring(0, dot) : original);
        base = sanitizeBlobName(base);
        if (base.length() > 80) base = base.substring(0, 80);
        String generatedUuid = UUID.randomUUID().toString();
        String fileName = ext.isBlank() ? generatedUuid + "-" + base : generatedUuid + "-" + base + "." + ext;
        return entityId + "/" + fileName;
    }

    /**
     * Uploads media for a vehicle.
     */
    public UploadResult uploadMediaForVehicle(InputStream fileInput, String fileName, Long vehicleId) {
        return uploadMedia(fileInput, fileName, vehicleId, "vehicles");
    }

    /**
     * Generic upload method that handles both vehicles and customers.
     */
    private UploadResult uploadMedia(InputStream fileInput, String fileName, Long entityId, String entityType) {
        if (fileInput == null) throw new IllegalArgumentException("fileInput is null");
        if (entityId == null) throw new IllegalArgumentException("entityId is required");
        if (entityType == null || (!entityType.equals("vehicles") && !entityType.equals("customers"))) {
            throw new IllegalArgumentException("entityType must be 'vehicles' or 'customers'");
        }

        String original = fileName == null ? "file" : fileName.trim();
        String ext = getFileExtension(original);
        String mediaCategory = detectMediaCategory(ext);
        if (mediaCategory.equals("other")) throw new IllegalArgumentException("Unsupported file type: " + ext);

        // Container name: vehicles-images, vehicles-videos, customers-images, customers-videos
        String container = entityType + "-" + mediaCategory;
        BlobContainerClient containerClient = getOrCreateContainer(container);

        String blobName = generateBlobNameWithPath(entityId, original, ext);
        String sanitizedBlobName = strictSanitizeBlobName(stripQuotes(blobName));
        validateBlobName(sanitizedBlobName);

        BlockBlobClient strictClient = containerClient.getBlobClient(sanitizedBlobName).getBlockBlobClient();
        try {
            byte[] bytes = fileInput.readAllBytes();
            if (mediaCategory.equals("images")) {
                uploadImage(strictClient, bytes);
            } else if (mediaCategory.equals("videos")) {
                uploadVideo(strictClient, bytes);
            }
            String url = strictClient.getBlobUrl();
            return new UploadResult(container, sanitizedBlobName, url, mediaCategory, original);
        } catch (Exception e) {
            log.error("Failed to upload file to Azure Blob Storage: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    private void uploadImage(BlockBlobClient client, byte[] bytes) {
        client.upload(new java.io.ByteArrayInputStream(bytes), bytes.length, true);
    }

    private void uploadVideo(BlockBlobClient client, byte[] bytes) {
        int blockSize = 4 * 1024 * 1024;
        List<String> blockIds = new ArrayList<>();
        int offset = 0;
        int blockNum = 0;
        while (offset < bytes.length) {
            int len = Math.min(blockSize, bytes.length - offset);
            String blockId = Base64.getEncoder().encodeToString(String.format("block-%05d", blockNum++).getBytes());
            blockIds.add(blockId);
            client.stageBlock(blockId, new java.io.ByteArrayInputStream(bytes, offset, len), len);
            offset += len;
        }
        client.commitBlockList(blockIds);
    }

    private String getFileExtension(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        int dot = lower.lastIndexOf('.');
        return (dot > 0 && dot < lower.length() - 1) ? lower.substring(dot + 1) : "";
    }

    private String detectMediaCategory(String ext) {
        Set<String> videoExt = Set.of("mp4", "mov", "avi", "mkv", "webm", "m4v", "wmv", "flv");
        Set<String> imageExt = Set.of("jpg", "jpeg", "png", "gif", "bmp", "svg", "webp", "heic", "heif", "tiff", "tif");
        if (videoExt.contains(ext)) return "videos";
        if (imageExt.contains(ext)) return "images";
        return "other";
    }

    private BlobContainerClient getOrCreateContainer(String container) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        if (!containerClient.exists()) containerClient.create();
        return containerClient;
    }

    /**
     * Generic delete method that handles both vehicles and customers.
     */
    private boolean deleteBlob(String blobName, Long entityId, String entityType, String mediaCategory) {
        try {
            if (mediaCategory == null || (!mediaCategory.equals("images") && !mediaCategory.equals("videos"))) {
                throw new IllegalArgumentException("mediaCategory must be 'images' or 'videos'");
            }
            String container = entityType + "-" + mediaCategory;
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
            String blobPath = entityId + "/" + strictSanitizeBlobName(stripQuotes(blobName));
            validateBlobName(blobPath);
            if (!containerClient.exists()) {
                log.warn("Container '{}' does not exist", containerClient.getBlobContainerName());
                return false;
            }
            BlockBlobClient blobClient = containerClient.getBlobClient(blobPath).getBlockBlobClient();
            if (!blobClient.exists()) {
                log.warn("Blob '{}' does not exist in container '{}'", blobPath, containerClient.getBlobContainerName());
                return false;
            }
            blobClient.delete();
            log.info("Deleted blob '{}' from container '{}'", blobPath, containerClient.getBlobContainerName());
            return true;
        } catch (Exception e) {
            log.error("Failed to delete blob: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete blob: " + e.getMessage(), e);
        }
    }

    /**
     * Downloads a blob for a vehicle from Azure Storage.
     */
    public byte[] downloadBlobForVehicle(String blobName, Long vehicleId, String mediaCategory) {
        return downloadBlob(blobName, vehicleId, "vehicles", mediaCategory);
    }

    /**
     * Downloads a blob for a customer from Azure Storage.
     */
    public byte[] downloadBlobForCustomer(String blobName, Long customerId, String mediaCategory) {
        return downloadBlob(blobName, customerId, "customers", mediaCategory);
    }

    /**
     * Generic download method that handles both vehicles and customers.
     */
    private byte[] downloadBlob(String blobName, Long entityId, String entityType, String mediaCategory) {
        try {
            if (mediaCategory == null || (!mediaCategory.equals("images") && !mediaCategory.equals("videos"))) {
                throw new IllegalArgumentException("mediaCategory must be 'images' or 'videos'");
            }
            String container = entityType + "-" + mediaCategory;
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
            String blobPath = entityId + "/" + strictSanitizeBlobName(stripQuotes(blobName));
            validateBlobName(blobPath);
            if (!containerClient.exists()) {
                log.warn("Container '{}' does not exist", containerClient.getBlobContainerName());
                throw new NotFoundException("Container not found");
            }
            var blobClient = containerClient.getBlobClient(blobPath);
            if (!blobClient.exists()) {
                log.warn("Blob '{}' does not exist in container '{}'", blobPath, containerClient.getBlobContainerName());
                throw new NotFoundException("Blob not found");
            }
            return blobClient.openInputStream().readAllBytes();
        } catch (Exception e) {
            log.error("Failed to download blob: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download blob: " + e.getMessage(), e);
        }
    }

    /**
     * Legacy method for backward compatibility - downloads a blob for a vehicle.
     * Attempts to find the blob in both images and videos containers.
     *
     * @deprecated Use downloadBlobForVehicle with mediaCategory instead
     */
    @Deprecated
    public byte[] downloadBlob(String blobName, Long vehicleId) {
        // Try images first, then videos
        try {
            return downloadBlobForVehicle(blobName, vehicleId, "images");
        } catch (NotFoundException e) {
            return downloadBlobForVehicle(blobName, vehicleId, "videos");
        }
    }

    /**
     * Generates a download link with SAS token for a vehicle.
     */
    public String generateDownloadLinkForVehicle(Long vehicleId, String blobName, String mediaCategory) {
        return generateDownloadLink(vehicleId, blobName, "vehicles", mediaCategory);
    }

    /**
     * Generates a download link with SAS token for a customer.
     */
    public String generateDownloadLinkForCustomer(Long customerId, String blobName, String mediaCategory) {
        return generateDownloadLink(customerId, blobName, "customers", mediaCategory);
    }

    /**
     * Generic method to generate a download link with SAS token valid for 1 hour.
     */
    private String generateDownloadLink(Long entityId, String blobName, String entityType, String mediaCategory) {
        if (mediaCategory == null || (!mediaCategory.equals("images") && !mediaCategory.equals("videos"))) {
            throw new IllegalArgumentException("mediaCategory must be 'images' or 'videos'");
        }
        String container = entityType + "-" + mediaCategory;
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        String blobPath = entityId + "/" + blobName;
        BlockBlobClient blobClient = containerClient.getBlobClient(blobPath).getBlockBlobClient();
        if (!blobClient.exists()) {
            throw new NotFoundException("Blob not found");
        }
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission)
                .setStartTime(OffsetDateTime.now())
                .setContainerName(container)
                .setBlobName(blobPath);
        String sasToken = blobClient.generateSas(values);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    /**
     * Legacy method for backward compatibility - generates download link for a vehicle.
     * Attempts to find the blob in both images and videos containers.
     *
     * @deprecated Use generateDownloadLinkForVehicle with mediaCategory instead
     */
    @Deprecated
    public String generateDownloadLink(Long vehicleId, String blobName) {
        // Try images first, then videos
        try {
            return generateDownloadLinkForVehicle(vehicleId, blobName, "images");
        } catch (NotFoundException e) {
            return generateDownloadLinkForVehicle(vehicleId, blobName, "videos");
        }
    }

    /**
     * Uploads a driver license for a user.
     */
    public UploadResult uploadDriverLicense(InputStream fileInput, String fileName, Long userId) {
        if (fileInput == null) throw new IllegalArgumentException("fileInput is null");
        if (userId == null) throw new IllegalArgumentException("userId is required");

        String original = fileName == null ? "driver-license" : fileName.trim();
        String ext = getFileExtension(original);
        String mediaCategory = detectMediaCategory(ext);
        if (mediaCategory.equals("other")) throw new IllegalArgumentException("Unsupported file type: " + ext);

        // Container name: users-documents
        String container = "users-documents";
        BlobContainerClient containerClient = getOrCreateContainer(container);

        String blobName = generateBlobNameWithPath(userId, original, ext);
        String sanitizedBlobName = strictSanitizeBlobName(stripQuotes(blobName));
        validateBlobName(sanitizedBlobName);

        BlockBlobClient strictClient = containerClient.getBlobClient(sanitizedBlobName).getBlockBlobClient();
        try {
            byte[] bytes = fileInput.readAllBytes();
            if (mediaCategory.equals("images")) {
                uploadImage(strictClient, bytes);
            } else if (mediaCategory.equals("videos")) {
                uploadVideo(strictClient, bytes);
            }
            String url = strictClient.getBlobUrl();
            return new UploadResult(container, sanitizedBlobName, url, mediaCategory, original);
        } catch (Exception e) {
            log.error("Failed to upload driver license to Azure Blob Storage: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload driver license: " + e.getMessage(), e);
        }
    }

    /**
     * Downloads a driver license for a user.
     */
    public byte[] downloadDriverLicense(String blobName, Long userId) {
        try {
            String container = "users-documents";
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
            String blobPath = userId + "/" + strictSanitizeBlobName(stripQuotes(blobName));
            validateBlobName(blobPath);
            if (!containerClient.exists()) {
                log.warn("Container '{}' does not exist", containerClient.getBlobContainerName());
                throw new NotFoundException("Container not found");
            }
            var blobClient = containerClient.getBlobClient(blobPath);
            if (!blobClient.exists()) {
                log.warn("Blob '{}' does not exist in container '{}'", blobPath, containerClient.getBlobContainerName());
                throw new NotFoundException("Driver license not found");
            }
            return blobClient.openInputStream().readAllBytes();
        } catch (Exception e) {
            log.error("Failed to download driver license: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download driver license: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a download link with SAS token for a driver license.
     */
    public String generateDriverLicenseDownloadLink(Long userId, String blobName) {
        String container = "users-documents";
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
        String blobPath = userId + "/" + blobName;
        BlockBlobClient blobClient = containerClient.getBlobClient(blobPath).getBlockBlobClient();
        if (!blobClient.exists()) {
            throw new NotFoundException("Driver license not found");
        }
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission)
                .setStartTime(OffsetDateTime.now())
                .setContainerName(container)
                .setBlobName(blobPath);
        String sasToken = blobClient.generateSas(values);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    // Blob name: keep logic simple but do not over-restrict (Azure allows most URL-safe chars). We conservatively replace control chars & '?' '#' '\\' with '-'.
    private String sanitizeBlobName(String blobName) {
        if (blobName == null) return "blob";
        String name = blobName.replaceAll("\"", "").trim();
        // Replace control chars
        name = name.replaceAll("[\r\n\t]", "-");
        // Disallow characters that commonly break URLs or Azure parsing when unencoded.
        name = name.replaceAll("[?#\\\\]", "-");
        // Collapse spaces to single '-'
        name = name.replaceAll(" +", "-");
        while (name.startsWith(".")) name = name.substring(1);
        while (name.endsWith(".")) name = name.substring(0, name.length() - 1);
        if (name.isEmpty()) name = "blob";
        return name;
    }

    private String stripQuotes(String s) {
        return s == null ? null : s.replaceAll("^\"|\"$", "");
    }

    private void validateBlobName(String blobName) {
        if (blobName == null || blobName.isEmpty()) {
            throw new IllegalArgumentException("Blob name is empty after sanitization");
        }
        if (blobName.length() > 1024) {
            throw new IllegalArgumentException("Blob name too long");
        }
        if (blobName.startsWith("/") || blobName.endsWith("/")) {
            throw new IllegalArgumentException("Blob name cannot start or end with a slash");
        }
        if (blobName.equals(".") || blobName.equals("..")) {
            throw new IllegalArgumentException("Blob name cannot be '.' or '..'");
        }
    }

    private String strictSanitizeBlobName(String name) {
        if (name == null) return "blob";
        String s = name;
        // Remove characters that sometimes trigger Azurite / InvalidResourceName: anything not unreserved or common safe punctuation
        s = s.replaceAll("[^a-zA-Z0-9\\-._/() ]", "-");
        // Replace spaces with '-'
        s = s.replaceAll(" +", "-");
        // Remove any leading or trailing dots or slashes
        while (s.startsWith(".")) s = s.substring(1);
        while (s.startsWith("/")) s = s.substring(1);
        while (s.endsWith(".")) s = s.substring(0, s.length() - 1);
        while (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        if (s.isEmpty()) s = "blob";
        if (s.length() > 1024) s = s.substring(0, 1024);
        return s;
    }
}
