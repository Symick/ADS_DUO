package nl.hva.ict.ads.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class PathUtils {

    public static List<Path> findFilesToScan(String sourceLocation, String prefix) throws IOException {
        List<Path> filesToScan = new ArrayList<>();
        Files.walkFileTree(Path.of(sourceLocation), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().contains(prefix) && file.getFileName().toString().endsWith(".xml"))
                    filesToScan.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        return filesToScan;
    }

    /**
     * get absolute path of a resource location,
     * if it does not exist, try to fall back to the data-files or downloads folder of the project module
     * this way, you can work transparently with large, downloaded datasets that shall not go into git.
     * @param resourceName
     * @return
     */
    public static String getResourcePath(String resourceName) {
        try {
            URL url = PathUtils.class.getResource(resourceName);
            if (url != null) {
                return new File(url.toURI()).getPath();
            }

            // trim leading slashes to resolve relatively
            while (resourceName.startsWith("/")) resourceName = resourceName.substring(1);

            url = PathUtils.class.getResource("/");
            URI projectRootURI = url.toURI();
            String resourceFilePath = null;
            while (resourceFilePath == null && projectRootURI.getPath().length()>15) {
                projectRootURI = projectRootURI.resolve("..");
                // try to find the resource in a data files folder up the project/file system tree
                resourceFilePath = new File(projectRootURI.resolve("data-files/").resolve(resourceName)).getPath();
                if (!Files.exists(Path.of(resourceFilePath))) {
                    resourceFilePath = new File(projectRootURI.resolve("downloads/").resolve(resourceName)).getPath();
                }
                if (!Files.exists(Path.of(resourceFilePath))) {
                    resourceFilePath = null;
                }
            }

            return resourceFilePath;

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
