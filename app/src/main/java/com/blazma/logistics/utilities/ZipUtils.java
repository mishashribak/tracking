package com.blazma.logistics.utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.File;

public class ZipUtils {

    private ZipUtils(){
    }
    public static void zipFolder(File sourceFolder, File outputZip) throws IOException {
        if (!outputZip.getParentFile().exists()) {
            outputZip.getParentFile().mkdirs();
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZip))) {
            zipFile(sourceFolder, sourceFolder, zos);
        }
    }

    private static void zipFile(File folderToZip, File baseFolder, ZipOutputStream zos) throws IOException {
        File[] files = folderToZip.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipFile(file, baseFolder, zos);
                    continue;
                }
                try (FileInputStream fis = new FileInputStream(file)) {
                    String relativePath = baseFolder.toURI().relativize(file.toURI()).getPath();
                    ZipEntry zipEntry = new ZipEntry(relativePath);
                    zos.putNextEntry(zipEntry);

                    byte[] bytes = new byte[2048];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }

                    zos.closeEntry();
                }
            }
        }
    }

    //call the zip file function
//    private void makeZipFile(){
//        try {
//
//            File cacheDir = getCacheDir();
//
//// Source folder in cache directory
//            File sourceFolder = new File(cacheDir, "test");
//
//// Destination zip file in cache directory
//            File outputZip = new File(cacheDir, "zipFiles/test.zip");
//
//            ZipUtils.zipFolder(sourceFolder, outputZip);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
