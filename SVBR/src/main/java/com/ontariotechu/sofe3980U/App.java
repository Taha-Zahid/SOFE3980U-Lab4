package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.*;
import com.opencsv.*;

public class App {

    public static void SVBR(String filePath) {
        List<String[]> allData;
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withSkipLines(1).build()) {
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return;
        }

        int TP = 0, FP = 0, TN = 0, FN = 0;
        double BCE = 0.0, threshold = 0.5;
        List<Double> yTrueList = new ArrayList<>();
        List<Double> yPredList = new ArrayList<>();

        for (String[] row : allData) {
            int yTrue = Integer.parseInt(row[0]);
            double yPred = Double.parseDouble(row[1]);
            yTrueList.add((double) yTrue);
            yPredList.add(yPred);
            
            BCE += yTrue * Math.log(yPred + 1e-9) + (1 - yTrue) * Math.log(1 - yPred + 1e-9);
            
            int yPredBinary = (yPred >= threshold) ? 1 : 0;
            if (yTrue == 1 && yPredBinary == 1) TP++;
            else if (yTrue == 0 && yPredBinary == 1) FP++;
            else if (yTrue == 0 && yPredBinary == 0) TN++;
            else FN++;
        }

        BCE = -BCE / allData.size();
        double accuracy = (TP + TN) / (double) (TP + TN + FP + FN);
        double precision = (TP + FP) > 0 ? (double) TP / (TP + FP) : 0;
        double recall = (TP + FN) > 0 ? (double) TP / (TP + FN) : 0;
        double f1Score = (precision + recall) > 0 ? 2 * precision * recall / (precision + recall) : 0;
        double auc = calculateAUC(yTrueList, yPredList);

        // Print results concisely
        System.out.println("Results for: " + filePath);
        System.out.printf("BCE: %.4f | Accuracy: %.4f | Precision: %.4f | Recall: %.4f | F1: %.4f | AUC: %.4f%n", 
                          BCE, accuracy, precision, recall, f1Score, auc);
        System.out.println("Confusion Matrix: TP=" + TP + " FP=" + FP + " FN=" + FN + " TN=" + TN);
    }

    public static double calculateAUC(List<Double> yTrueList, List<Double> yPredList) {
        int n = yTrueList.size();
        double[] x = new double[101], y = new double[101];
        double auc = 0;

        for (int i = 0; i <= 100; i++) {
            double threshold = i / 100.0;
            int tpCount = 0, fpCount = 0, fnCount = 0, tnCount = 0;
            
            for (int j = 0; j < n; j++) {
                double pred = yPredList.get(j);
                int trueLabel = yTrueList.get(j).intValue();
                
                if (trueLabel == 1 && pred >= threshold) tpCount++;
                if (trueLabel == 0 && pred >= threshold) fpCount++;
                if (trueLabel == 1 && pred < threshold) fnCount++;
                if (trueLabel == 0 && pred < threshold) tnCount++;
            }

            double tpr = tpCount / (double) (tpCount + fnCount + 1e-9);
            double fpr = fpCount / (double) (fpCount + tnCount + 1e-9);
            x[i] = fpr;
            y[i] = tpr;

            if (i > 0) auc += (y[i - 1] + y[i]) * Math.abs(x[i - 1] - x[i]) / 2;
        }
        return auc;
    }

    public static void main(String[] args) {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        for (String filePath : filePaths) {
            SVBR(filePath);
        }
    }
}
