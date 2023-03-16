package com.example.olive.service;

import com.example.olive.scheduler.JodiSourceScrapeImpl;
import com.example.olive.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IOServiceImpl implements IOService {
    @Autowired
    private Utils utils;

    Logger logger = LoggerFactory.getLogger(IOServiceImpl.class);

    public XSSFWorkbook xlsxWorkbookParsing(File file) {
        try {
            OPCPackage pkg = OPCPackage.open(file);
            return new XSSFWorkbook(pkg);
        } catch (IOException | InvalidFormatException e) {
            logger.error("Unable to parse xlsx workbook (POI apache)");
            return null;
        }
    }

    public HSSFWorkbook xlsWorkbookParsing(File file) {
        try {
            return new HSSFWorkbook(new POIFSFileSystem(file));
        } catch (IOException e) {
            logger.error("Unable to parse xls workbook (POI apache)");
            return null;
        }
    }

    public File[] getDataFiles(String folder) {
        String resourcePath = String.format("src/main/resources/downloads/%sDataset/", folder);
        File resource = new File(resourcePath);
        return resource.listFiles();
    }

    public List<File> listFilesForFolderWithFilter(String folder, String extension) {
        String resourcePath = String.format("src/main/resources/downloads/%sDataset/", folder);
        try (Stream<Path> walk = Files.walk(Paths.get(resourcePath))) {
            // Finding files with specified extension
            return walk.map(Path::toFile)
                    .filter(file -> FilenameUtils.getExtension(file.getName()).equals(extension))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Unable to search for files in the downloads directory with extension of " + extension);
        }
        return null;
    }

    public List<File> listFilesForFolder(String folder) {
        String resourcePath = String.format("src/main/resources/downloads/%sDataset/", folder);
        try (Stream<Path> walk = Files.walk(Paths.get(resourcePath))) {
            // Finding all files
            return walk.map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Unable to list all files in the downloads directory");
        }
        return null;
    }


    public boolean deleteDataFiles(String country) {
        File[] dataFiles = getDataFiles(country);
        for (File f : dataFiles) {
            if (!f.delete()) {
                return false;
            }
        }
        return true;
    }


}
