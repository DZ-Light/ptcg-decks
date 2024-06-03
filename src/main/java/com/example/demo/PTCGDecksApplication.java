package com.example.demo;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PTCGDecksApplication {
	static CardDatabase db =new CardDatabase();
	public static void main(String[] args) {

		String directoryPath = "deck"; // 替换为你的CSV文件目录路径

		Path directory = Paths.get(directoryPath);
		try {
			List<Path> csvFiles = Files.walk(directory)
					.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".csv"))
					.collect(java.util.stream.Collectors.toList());

			for (Path csvFile : csvFiles) {
				System.out.println("Reading file: " + csvFile.getFileName());
				readCSVFile(csvFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void readCSVFile(Path csvFile) {
		try (CSVReader reader = new CSVReader(new FileReader(csvFile.toFile()))) {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				// 处理每一行数据，nextLine 是一个字符串数组，代表CSV中的一行
				Pattern pattern = Pattern.compile("^[0-9]+$");
				Matcher matcher = pattern.matcher(nextLine[0]);
				boolean isNumber = matcher.matches();
				if (isNumber){
					Card card = new Card();
					card.setName(nextLine[1]);
					card.setType(nextLine[2]);
					card.setMark(nextLine[3].split("/")[5]);
					card.setNumber(nextLine[3].split("/")[6].split("\\.")[0]);
					db.addCard(card);
				}
			}
		} catch (IOException | CsvValidationException e) {
			e.printStackTrace();
		}
	}

}
