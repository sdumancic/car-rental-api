package car.rental.core.azure.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class AzureBlobService {

    private BlobServiceClient blobServiceClient;

    @ConfigProperty(name = "azure.storage.connection-string")
    String connectionString;

    @ConfigProperty(name = "azure.storage.debug", defaultValue = "false")
    boolean debug;

    @ConfigProperty(name = "azure.storage.autofix-azurite", defaultValue = "true")
    boolean autoFixAzurite;

    private String accountName;

    @PostConstruct
    void init() {
        log.info("Azure Storage Connection String present: {}", connectionString != null);
        String originalConnectionString = connectionString;
        if (connectionString != null) {
            accountName = extractValue(connectionString, "AccountName");
            String endpointSuffix = extractValue(connectionString, "EndpointSuffix");
            String blobEndpoint = extractValue(connectionString, "BlobEndpoint");
            String queueEndpoint = extractValue(connectionString, "QueueEndpoint");
            String tableEndpoint = extractValue(connectionString, "TableEndpoint");
            if (debug) {
                log.info("[AzureBlobService:init] accountName='{}' endpointSuffix='{}' blobEndpoint='{}' queueEndpoint='{}' tableEndpoint='{}' conn.len={}", accountName, endpointSuffix, blobEndpoint, queueEndpoint, tableEndpoint, connectionString.length());
            } else {
                log.info("[AzureBlobService:init] accountName='{}' (debug disabled)", accountName);
            }
            // Auto-fix Azurite endpoints if missing /devstoreaccount1
            if (autoFixAzurite && accountName != null && accountName.equals("devstoreaccount1")) {
                boolean needsFix = false;
                if (blobEndpoint != null && blobEndpoint.matches("(?i)http://[0-9.]+:10000/?")) needsFix = true;
                if (queueEndpoint != null && queueEndpoint.matches("(?i)http://[0-9.]+:10001/?")) needsFix = true;
                if (tableEndpoint != null && tableEndpoint.matches("(?i)http://[0-9.]+:10002/?")) needsFix = true;
                if (needsFix) {
                    log.warn("[AzureBlobService:init] Auto-fixing Azurite endpoints to include /devstoreaccount1 path segments (set azure.storage.autofix-azurite=false to disable)");
                    connectionString = rebuildAzuriteConnectionString(originalConnectionString, blobEndpoint, queueEndpoint, tableEndpoint);
                    if (debug) {
                        log.info("[AzureBlobService:init] Adjusted connection string: {}", connectionString);
                    }
                } else if (blobEndpoint != null && (blobEndpoint.matches("(?i)http://localhost:10000/?") || blobEndpoint.matches("(?i)http://127.0.0.1:10000/?"))) {
                    // If not fixed but still plain - could be missing keys
                    log.warn("[AzureBlobService:init] Detected Azurite blob endpoint style without account path but auto-fix conditions not met.");
                }
            } else if (accountName != null && accountName.equals("devstoreaccount1") && !autoFixAzurite) {
                log.warn("[AzureBlobService:init] Azurite endpoints may be incomplete but auto-fix disabled (azure.storage.autofix-azurite=false)");
            }
        }
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        if (debug) {
            try {
                var first = blobServiceClient.listBlobContainers().stream().findFirst();
                log.info("[AzureBlobService:init] listBlobContainers firstPresent={} name={} uri={}",
                        first.isPresent(),
                        first.map(c -> c.getName()).orElse(null),
                        first.map(c -> blobServiceClient.getBlobContainerClient(c.getName()).getBlobContainerUrl()).orElse(null));
            } catch (Exception e) {
                log.warn("[AzureBlobService:init] listBlobContainers failed: {}", e.toString());
            }
        }
        log.info("Azure Blob Service Client created");
    }

    private String rebuildAzuriteConnectionString(String original, String blobEndpoint, String queueEndpoint, String tableEndpoint) {
        // Keep existing AccountName & AccountKey, replace endpoints with suffixed variants
        String accountKey = extractValue(original, "AccountKey");
        StringBuilder sb = new StringBuilder();
        sb.append("DefaultEndpointsProtocol=http");
        sb.append(";AccountName=devstoreaccount1");
        if (accountKey != null) sb.append(";AccountKey=").append(accountKey);
        String be = (blobEndpoint == null || blobEndpoint.isBlank()) ? "http://127.0.0.1:10000/devstoreaccount1" : normalizeAzuriteEndpoint(blobEndpoint, 10000);
        String qe = (queueEndpoint == null || queueEndpoint.isBlank()) ? "http://127.0.0.1:10001/devstoreaccount1" : normalizeAzuriteEndpoint(queueEndpoint, 10001);
        String te = (tableEndpoint == null || tableEndpoint.isBlank()) ? "http://127.0.0.1:10002/devstoreaccount1" : normalizeAzuriteEndpoint(tableEndpoint, 10002);
        sb.append(";BlobEndpoint=").append(be);
        sb.append(";QueueEndpoint=").append(qe);
        sb.append(";TableEndpoint=").append(te);
        return sb.toString();
    }

    private String normalizeAzuriteEndpoint(String endpoint, int port) {
        if (endpoint == null) return null;
        String trimmed = endpoint.trim();
        if (trimmed.endsWith("/")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        // If already contains /devstoreaccount1 suffix, keep it
        if (trimmed.matches("(?i).*/devstoreaccount1$")) return trimmed;
        // If only host:port present, append suffix
        if (trimmed.matches("(?i)http://(localhost|127.0.0.1):" + port)) {
            return trimmed + "/devstoreaccount1";
        }
        return trimmed; // leave unchanged
    }

    private String extractValue(String conn, String key) {
        try {
            for (String part : conn.split(";")) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2 && kv[0].equalsIgnoreCase(key)) return kv[1];
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public AzureBlobService() {
        // Default constructor required for CDI
    }

    public String generateBlobName(String fileName) {
        return UUID.randomUUID() + "-" + (fileName == null ? "file" : fileName);
    }

    public List<String> generateBlockUploadUrls(String containerName, String blobName, int totalParts) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlockBlobClient blobClient = containerClient.getBlobClient(blobName).getBlockBlobClient();
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < totalParts; i++) {
            String blockId = Base64.getEncoder().encodeToString(String.format("block-%05d", i).getBytes());
            BlobSasPermission perms = new BlobSasPermission()
                    .setWritePermission(true)
                    .setCreatePermission(true);
            BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
                    OffsetDateTime.now().plusHours(1), perms
            ).setStartTime(OffsetDateTime.now());
            String sas = blobClient.generateSas(values);
            String url = String.format(
                    "http://127.0.0.1:10000/devstoreaccount1/%s/%s?comp=block&blockid=%s&%s",
                    containerName,
                    blobName,
                    URLEncoder.encode(blockId, StandardCharsets.UTF_8),
                    sas
            );
            urls.add(url);
        }
        return urls;
    }

    public String commitBlockList(String containerName, String blobName, int totalParts) {
        try {
            log.info("Committing block list for container: {}, blob: {}, parts: {}", containerName, blobName, totalParts);
            String sanitizedBlobName = sanitizeBlobName(blobName);
            log.info("Using sanitized blob name: {}", sanitizedBlobName);
            ensureContainerExists(containerName);
            BlockBlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName)
                    .getBlobClient(sanitizedBlobName)
                    .getBlockBlobClient();
            List<String> blockIds = new ArrayList<>();
            for (int i = 0; i < totalParts; i++) {
                blockIds.add(Base64.getEncoder().encodeToString(String.format("block-%05d", i).getBytes()));
            }
            log.info("Committing block list with {} blocks for blob: {}", blockIds.size(), sanitizedBlobName);
            blobClient.commitBlockList(blockIds);
            log.info("Successfully committed block list, blob URL: {}", blobClient.getBlobUrl());
            return blobClient.getBlobUrl();
        } catch (BlobStorageException e) {
            logBlobStorageException("commitBlockList", e, Map.of(
                    "container", containerName,
                    "blobName", blobName
            ));
            debugListBlocks(containerName, blobName);
            return "Error: " + (e.getServiceMessage() != null ? e.getServiceMessage() : e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in commitBlockList: {}", e.getMessage(), e);
            return "Unexpected error: " + e.getMessage();
        }
    }

    public String uploadFile(String containerName, String blobName, InputStream fileInput) {
        try {
            String resolvedContainer = normalizeAndValidateContainerName(containerName);
            if (resolvedContainer == null) {
                resolvedContainer = "videos"; // default
            }
            log.info("Requested container: '{}', resolved container: '{}'", containerName, resolvedContainer);
            log.info("Container hex: {}", toHex(resolvedContainer));
            ensureContainerExists(resolvedContainer);

            String originalBlobName = stripQuotes(blobName);
            String sanitizedBlobName = sanitizeBlobName(originalBlobName);
            validateBlobName(sanitizedBlobName);
            log.info("Uploading with sanitized blob name: '{}' (orig: '{}') hex: {}", sanitizedBlobName, originalBlobName, toHex(sanitizedBlobName));
            debugNameDiagnostics("pre-upload", resolvedContainer, sanitizedBlobName);

            BlockBlobClient blobClient = blobServiceClient.getBlobContainerClient(resolvedContainer)
                    .getBlobClient(sanitizedBlobName)
                    .getBlockBlobClient();

            byte[] bytes = fileInput.readAllBytes();
            log.info("Read {} bytes for upload", bytes.length);
            try {
                blobClient.upload(new java.io.ByteArrayInputStream(bytes), bytes.length, true);
            } catch (BlobStorageException e) {
                String errorCode = safe(() -> e.getErrorCode().toString());
                logBlobStorageException("primaryUpload", e, Map.of(
                        "container", resolvedContainer,
                        "blob", sanitizedBlobName,
                        "length", String.valueOf(bytes.length)
                ));
                if (errorCode != null && errorCode.equalsIgnoreCase("InvalidResourceName")) {
                    // Attempt stricter sanitization once
                    String strictName = strictSanitizeBlobName(sanitizedBlobName);
                    if (!strictName.equals(sanitizedBlobName)) {
                        log.warn("Retrying upload with stricter sanitized blob name. before='{}' after='{}'", sanitizedBlobName, strictName);
                        validateBlobName(strictName);
                        debugNameDiagnostics("strict-retry", resolvedContainer, strictName);
                        BlockBlobClient strictClient = blobServiceClient.getBlobContainerClient(resolvedContainer)
                                .getBlobClient(strictName)
                                .getBlockBlobClient();
                        try {
                            strictClient.upload(new java.io.ByteArrayInputStream(bytes), bytes.length, true);
                            log.info("Strict retry upload successful: {} ({} bytes)", strictClient.getBlobUrl(), bytes.length);
                            return strictClient.getBlobUrl();
                        } catch (BlobStorageException e2) {
                            logBlobStorageException("strictRetryUpload", e2, Map.of(
                                    "container", resolvedContainer,
                                    "blob", strictName
                            ));
                        }
                    }
                }
                // Fallback: attempt with dedicated fallback container (auto-create)
                String fallbackContainer = "videos-fallback";
                ensureContainerExists(fallbackContainer);
                log.info("Retrying upload with fallback container: {}", fallbackContainer);
                BlockBlobClient fallbackBlobClient = blobServiceClient.getBlobContainerClient(fallbackContainer)
                        .getBlobClient(sanitizedBlobName)
                        .getBlockBlobClient();
                fallbackBlobClient.upload(new java.io.ByteArrayInputStream(bytes), bytes.length, true);
                log.info("Fallback upload successful: {} ({} bytes)", fallbackBlobClient.getBlobUrl(), bytes.length);
                return fallbackBlobClient.getBlobUrl();
            }
            log.info("Upload successful: {} ({} bytes)", blobClient.getBlobUrl(), bytes.length);
            return blobClient.getBlobUrl();
        } catch (Exception e) {
            log.error("Failed to upload file to Azure Blob Storage: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    // region Helpers

    private void ensureContainerExists(String containerName) {
        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            if (!containerClient.exists()) { // HEAD request
                log.info("Container '{}' does not exist. Creating... (account='{}')", containerName, accountName);
                containerClient.create();
                log.info("Container '{}' created", containerName);
            } else if (debug) {
                log.info("Container '{}' already exists (account='{}')", containerName, accountName);
            }
        } catch (BlobStorageException e) {
            if (e.getStatusCode() == 409) {
                log.debug("Container '{}' already exists (409)", containerName);
            } else if (e.getStatusCode() == 400) {
                log.error("[ensureContainerExists] Received 400 (Bad Request) for container='{}' account='{}' patternValid={} hex='{}' message='{}' serviceMessage='{}'", containerName, accountName, CONTAINER_PATTERN.matcher(containerName).matches(), toHex(containerName), e.getMessage(), e.getServiceMessage());
                logPotential400Causes(containerName);
                throw e;
            } else {
                logBlobStorageException("ensureContainerExists", e, Map.of("container", containerName));
                throw e;
            }
        } catch (Exception ex) {
            log.error("[ensureContainerExists] Unexpected error for container='{}' account='{}': {}", containerName, accountName, ex.toString());
            throw ex;
        }
    }

    private void logPotential400Causes(String container) {
        // Provide hints for typical 400 empty body causes.
        List<String> hints = new ArrayList<>();
        if (container.contains("--")) hints.add("Consecutive dashes not ideal for some tooling");
        if (container.length() < 3 || container.length() > 63) hints.add("Container length out of 3-63 bounds");
        if (!container.equals(container.toLowerCase(Locale.ROOT))) hints.add("Uppercase letters present");
        if (accountName == null || accountName.isBlank()) hints.add("AccountName missing in connection string");
        if (connectionString != null && connectionString.toLowerCase(Locale.ROOT).contains("devstoreaccount1") && !container.equals("videos") && !container.equals("images") && !container.startsWith("video")) {
            hints.add("Using Azurite devstoreaccount1: ensure Azurite is running and port 10000 accessible");
        }
        if (connectionString != null && connectionString.contains("DefaultEndpointsProtocol=https") && debug) {
            hints.add("If using Azurite, set UseDevelopmentStorage=true or proper emulator connection string");
        }
        if (hints.isEmpty()) hints.add("No obvious local hints; check network, DNS, or shared key validity");
        log.warn("[ensureContainerExists:hints] container='{}' hints={}", container, hints);
    }

    private void logBlobStorageException(String context, BlobStorageException e, Map<String, String> extra) {
        log.error("[AzureBlobService:{}] BlobStorageException status={} errorCode={} serviceMessage={} message={} extra={}",
                context,
                e.getStatusCode(),
                safe(() -> e.getErrorCode().toString()),
                e.getServiceMessage(),
                e.getMessage(),
                extra,
                e);
    }

    private void debugListBlocks(String container, String blobName) {
        try {
            BlockBlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(container)
                    .getBlobClient(sanitizeBlobName(blobName))
                    .getBlockBlobClient();
            var blockList = blobClient.listBlocks(com.azure.storage.blob.models.BlockListType.ALL);
            if (blockList != null) {
                log.info("Committed blocks: {}", blockList.getCommittedBlocks());
                log.info("Uncommitted blocks: {}", blockList.getUncommittedBlocks());
            } else {
                log.info("No block list returned for blob '{}'/{}", container, blobName);
            }
        } catch (Exception ex) {
            log.warn("Could not list blocks for blob '{}'/{}: {}", container, blobName, ex.getMessage());
        }
    }

    private <T> T safe(SupplierWithException<T> s) {
        try {
            return s.get();
        } catch (Exception ignored) {
            return null;
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
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

    private static final Pattern CONTAINER_PATTERN = Pattern.compile("^[a-z0-9](?:[a-z0-9-]{1,61}[a-z0-9])?$");

    private String normalizeAndValidateContainerName(String name) {
        if (name == null || name.isBlank()) return null;
        String c = name.trim().toLowerCase(Locale.ROOT);
        // Remove invalid chars
        c = c.replaceAll("[^a-z0-9-]", "-");
        // Collapse multiple '-'
        c = c.replaceAll("-+", "-");
        // Trim leading/trailing '-'
        c = c.replaceAll("^-|-$", "");
        if (c.length() < 3) c = (c + "---").substring(0, 3); // pad to minimum length
        if (c.length() > 63) c = c.substring(0, 63);
        if (!CONTAINER_PATTERN.matcher(c).matches()) {
            log.warn("Container name '{}' invalid after normalization -> '{}'", name, c);
            throw new IllegalArgumentException("Invalid container name after normalization: " + c);
        }
        return c;
    }

    private String toHex(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(String.format("%02x ", (int) c));
        }
        return sb.toString().trim();
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

    private void debugNameDiagnostics(String phase, String container, String blobName) {
        try {
            log.info("[name-diagnostics:{}] container='{}' blob='{}' blobLength={} containerValid={} blobHex={} containerHex={}",
                    phase,
                    container,
                    blobName,
                    blobName.length(),
                    CONTAINER_PATTERN.matcher(container).matches(),
                    toHex(blobName),
                    toHex(container));
            if (blobName.contains("//")) {
                log.warn("Blob name contains double slash sequence which may cause InvalidResourceName.");
            }
            if (blobName.startsWith(" ") || blobName.endsWith(" ")) {
                log.warn("Blob name has leading/trailing spaces after sanitization.");
            }
        } catch (Exception ignored) {
        }
    }
    // endregion

    public static class UploadResult {
        public String container;
        public String blobName;
        public String url;
        public String mediaCategory; // image | video | other
        public String originalFileName;

        public UploadResult(String container, String blobName, String url, String mediaCategory, String originalFileName) {
            this.container = container;
            this.blobName = blobName;
            this.url = url;
            this.mediaCategory = mediaCategory;
            this.originalFileName = originalFileName;
        }
    }

    /**
     * Generic media upload helper that decides target container (videos|images) based on file extension.
     * Falls back to 'videos' for unknown but video-like or 'other' category (still stored in videos to simplify infra).
     * The blob name will be UUID + sanitized base name + original extension when present.
     */
    public UploadResult uploadMethod(InputStream fileInput, String fileName) {
        if (fileInput == null) {
            throw new IllegalArgumentException("fileInput is null");
        }
        String original = fileName == null ? "file" : fileName.trim();
        String lower = original.toLowerCase(Locale.ROOT);
        String ext = "";
        int dot = lower.lastIndexOf('.');
        if (dot > 0 && dot < lower.length() - 1) {
            ext = lower.substring(dot + 1);
        }
        Set<String> videoExt = Set.of("mp4", "mov", "avi", "mkv", "webm", "m4v", "wmv", "flv");
        Set<String> imageExt = Set.of("jpg", "jpeg", "png", "gif", "bmp", "svg", "webp", "heic", "heif", "tiff", "tif");
        String mediaCategory;
        if (videoExt.contains(ext)) mediaCategory = "video";
        else if (imageExt.contains(ext)) mediaCategory = "image";
        else mediaCategory = "other";

        String container = mediaCategory.equals("image") ? "images" : "videos"; // store other also in videos
        // Sanitize base name (without extension) and rebuild
        String base = (dot > 0 ? original.substring(0, dot) : original);
        base = sanitizeBlobName(base);
        if (base.length() > 80) base = base.substring(0, 80); // keep names reasonable
        String generatedUuid = UUID.randomUUID().toString();
        String blobName = ext.isBlank() ? generatedUuid + "-" + base : generatedUuid + "-" + base + "." + ext;

        log.info("uploadMethod deciding container='{}' mediaCategory='{}' original='{}' blobName='{}'", container, mediaCategory, original, blobName);
        String url = uploadFile(container, blobName, fileInput);
        return new UploadResult(container, blobName, url, mediaCategory, original);
    }
}
