# core-api

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8090/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar` (it will listen on port 8090).

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar` (still on port 8090).

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/core-api-1.0-SNAPSHOT-runner` (serving on port 8090).

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Media Upload (Images & Videos)

Single-part uploads for images and videos are handled via:

`POST /videos/upload` (multipart/form-data)

Form fields:

- `file`: the binary file contents
- `fileName`: original file name (used to determine media type & extension)

Container routing logic:

- Images (jpg, jpeg, png, gif, bmp, svg, webp, heic, heif, tiff, tif) -> `images` container
- Videos (mp4, mov, avi, mkv, webm, m4v, wmv, flv) and any other/unknown types -> `videos` container

The service generates a blob name: `UUID-sanitizedBaseName.ext` and returns metadata.

### Windows (cmd.exe) example

```
curl --location http://localhost:8090/videos/upload ^
  --form file=@"C:/Users/you/Videos/food.mp4" ^
  --form fileName=food.mp4
```

### PowerShell example

```
curl -Method POST http://localhost:8090/videos/upload `
  -Form @{ file = Get-Item "C:/Users/you/Videos/food.mp4"; fileName = 'food.mp4' }
```

### Linux / macOS example

```
curl --location 'http://localhost:8090/videos/upload' \
  --form 'file=@"/home/you/Videos/food.mp4"' \
  --form 'fileName="food.mp4"'
```

### Sample JSON Response

```
{
  "container": "videos",
  "blobName": "550e8400-e29b-41d4-a716-446655440000-food.mp4",
  "url": "https://<account>.blob.core.windows.net/videos/550e8400-e29b-41d4-a716-446655440000-food.mp4",
  "mediaCategory": "video",
  "originalFileName": "food.mp4"
}
```

### Error Response Example

```
{
  "error": "Failed to upload file: <reason>"
}
```

### Notes

- For large (>100MB) videos prefer the block upload flow (`/videos/init-upload` -> client uploads blocks ->
  `/videos/complete-upload`).
- Filenames are sanitized; unsafe characters are replaced to avoid Azure `InvalidResourceName` errors.
- A fallback container (`videos-fallback`) is attempted automatically if the primary upload hits certain Azure errors.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
