package com.example.demo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PTCGDecksApplication {
    static CardDatabase db = new CardDatabase();

    public static void main(String[] args) {
        String directoryPath = "deck"; // 替换为你的CSV文件目录路径
        Path directory = Paths.get(directoryPath);
        try {
            List<Path> csvFiles = Files.walk(directory).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".csv")).collect(java.util.stream.Collectors.toList());

            for (Path csvFile : csvFiles) {
                System.out.println("Reading file: " + csvFile.getFileName());
                readCSVFile(csvFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readCSVFile(Path csvFile) {
        try (Reader in = new FileReader(csvFile.toFile())) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                String Name = record.get("Name");
                String Type = record.get("Type");
                String URL = record.get("URL");
                Card card = new Card();
                card.setName(Name);
                card.setType(Type);
                card.setMark(URL.split("/")[5]);
                card.setNumber(URL.split("/")[6].split("\\.")[0]);
                db.addCard(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
