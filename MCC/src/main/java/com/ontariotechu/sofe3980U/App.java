package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

public class App 
{
    public static void main( String[] args )
    {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;
        
        try {
            filereader = new FileReader(filePath); 
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }
        
        int[][] confusionMatrix = new int[5][5];  // Confusion matrix for 5 classes
        double crossEntropy = 0.0;
        
        int count = 0;
        for (String[] row : allData) { 
            int yTrue = Integer.parseInt(row[0]) - 1;  // Adjust to 0-based index
            float[] yPredicted = new float[5];
            
            // Store predicted probabilities
            for (int i = 0; i < 5; i++) {
                yPredicted[i] = Float.parseFloat(row[i + 1]);
            }
            
            // Calculate Cross-Entropy
            crossEntropy += -Math.log(yPredicted[yTrue] + 1e-9);  // Avoid log(0)
            
            // Update Confusion Matrix
            int predictedClass = getPredictedClass(yPredicted);
            confusionMatrix[yTrue][predictedClass]++;
            
            count++;
            if (count == 10) {  // Process only the first 10 rows
                break;
            }
        }
        
        crossEntropy /= count;  // Average Cross-Entropy
        
        // Print Confusion Matrix
        System.out.println("Confusion Matrix:");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(confusionMatrix[i][j] + "\t");
            }
            System.out.println();
        }

        System.out.println("Average Cross-Entropy: " + crossEntropy);
    }

    // Get class with highest predicted probability
    public static int getPredictedClass(float[] yPredicted) {
        int predictedClass = 0;
        for (int i = 1; i < 5; i++) {
            if (yPredicted[i] > yPredicted[predictedClass]) {
                predictedClass = i;
            }
        }
        return predictedClass;
    }
}

