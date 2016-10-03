package org.ozwillo.dcexporter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CsvUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtils.class);

    public static Optional<File> writeToFile(List<String> headers, List<List<String>> rows) {
        File resourceFile;
        try {
            resourceFile = File.createTempFile("export-", ".csv");
            LOGGER.info("Writing data in temp file {}", resourceFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Error while creating temp file", e);
            return Optional.empty();
        }

        try {
            FileWriter resourceFileWriter = new FileWriter(resourceFile);
            Optional<String> header = headers.stream().reduce((result, key) -> result + "," + key);
            if (header.isPresent()) {
                resourceFileWriter.write(header.get());
                resourceFileWriter.write("\n");
            }

            rows.forEach(row -> {
                Optional<String> rowAsCsv = row.stream()
                    .map(value -> "\"" + value + "\"")
                    .reduce((result, key) -> result + "," + key);
                if (rowAsCsv.isPresent()) {
                    try {
                        resourceFileWriter.write(rowAsCsv.get());
                        resourceFileWriter.write("\n");
                    } catch (IOException e) {
                        LOGGER.error("Unable to write row {}", rowAsCsv.get());
                    }
                }
            });

            resourceFileWriter.flush();
            resourceFileWriter.close();
        } catch (IOException e) {
            LOGGER.error("Error while writing CSV file", e);
            return Optional.empty();
        }

        return Optional.of(resourceFile);
    }
}
