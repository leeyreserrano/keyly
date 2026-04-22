- No se puede subir una imagen con el mismo nombre

    public void saveImage(UUID uuid, MultipartFile file) {
        try {
            Files.createDirectories(root); <--- ESTO PETA MUCHO

            String fileName = IMAGES_PATH + file.getOriginalFilename();
            Path destinationFile = root.resolve(fileName);

            Files.copy(file.getInputStream(), destinationFile);

            uploadImage(uuid, fileName);
        } catch (IOException e) {
            throw new ImageException("La imatge no s'ha pogut guardar.");
        }
    }