package com.example.demo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class UpdateDeckWinRate {
    public static void main(String[] args) {
        DeckDatabase deckDatabase = new DeckDatabase();
        String csvFile = "winRate.csv";
//        initCSVFile(csvFile, deckDatabase);
        readCSVFile(csvFile, deckDatabase);
    }

    private static void readCSVFile(String csvFile, DeckDatabase deckDatabase) {
        try (Reader in = new FileReader(csvFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                String deck1 = record.get("deck1");
                String deck2 = record.get("deck2");
                Float winRate1 = Float.valueOf(record.get("winRate"));
                Float dLossRate2 = Float.valueOf(record.get("dLossRate"));
                deckDatabase.updateWinRates(deck1, deck2, winRate1, 1 - winRate1 - dLossRate2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initCSVFile(String csvFile, DeckDatabase deckDatabase) {
        try (FileWriter writer = new FileWriter(csvFile)) {
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("deck1", "deck2", "winRate", "dLossRate"));
            List<Deck> deckList = deckDatabase.getDecks();
            for (int i = 0; i < deckList.size() - 1; i++) {
                for (int j = i + 1; j < deckList.size(); j++) {
                    csvPrinter.printRecord(deckList.get(i).getName(),
                            deckList.get(j).getName(),
                            deckList.get(i).getWinRates().getOrDefault(deckList.get(j).getName(), 0F),
                            1 - deckList.get(i).getWinRates().getOrDefault(deckList.get(j).getName(), 0F) - deckList.get(j).getWinRates().getOrDefault(deckList.get(i).getName(), 0F));
                }
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
