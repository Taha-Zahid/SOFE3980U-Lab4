package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {
    public static void main(String[] args) {
        String[] modelFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};
        String bestModel = "";
        double minMSE = Double.MAX_VALUE;

        for (String file : modelFiles) {
            System.out.println("Evaluating " + file);
            double[] metrics = calculateMetrics(file);
            System.out.println("MSE: " + metrics[0] + ", MAE: " + metrics[1] + ", MARE: " + metrics[2] + "%");

            if (metrics[0] < minMSE) {
                minMSE = metrics[0];
                bestModel = file;
            }
        }

        System.out.println("Best model based on MSE: " + bestModel);
    }

    public static double[] calculateMetrics(String filePath) {
        double mse = 0, mae = 0, mare = 0;
        int count = 0;
        double epsilon = 1e-10;

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withSkipLines(1).build()) {
            for (String[] row : csvReader) {
                double yTrue = Double.parseDouble(row[0]);
                double yPred = Double.parseDouble(row[1]);
                double error = yTrue - yPred;
                
                mse += error * error;
                mae += Math.abs(error);
                mare += Math.abs(error) / (Math.abs(yTrue) + epsilon);
                count++;
            }
        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            e.printStackTrace();
        }

        if (count > 0) {
            mse /= count;
            mae /= count;
            mare = (mare / count) * 100;
        }
        return new double[]{mse, mae, mare};
    }
}
