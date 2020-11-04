package topsis;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class RankingOntologiesMain
{
    //this method reads the file
    public static double [][] decisionMatricArray(String filename) throws Exception{

         File file = new File(filename);
         Scanner sc = new Scanner(file);
         double [][] matrix = new double[50][11];

         while(sc.hasNextLine()){
             for(int row = 0; row < matrix.length; row++){
                 String[] line = sc.nextLine().split("\\s+");
                 for(int col = 1; col < line.length; col++){
                     matrix[row][col-1] = Float.valueOf(line[col]);
                 }
             }
         }
         return matrix;
    }
    //Prints the weights in a 2 D array
    public static void printWeights(double [] array){
        for(int col = 0; col < array.length; col++){
            System.out.println( "col" + (col+1) + ": " + array[col] + "  ");
        }
    }
    //Prints a matrix in a 2 D array
    public static void printMatrix(double [][] array){
        for(int row = 0; row < array.length; row++){
            for(int col = 0; col < array[0].length; col++){
                System.out.print( array[row][col] + "  ");
            }
            System.out.println();
        }
    }
    //Print ideal solutions in a 1 D array
    public static void printIdeals(double [] array){
        for(int col = 0; col < array.length; col++){
            System.out.print( "col" + (col+1) + ": " + array[col]);
            System.out.println();
        }
    }
    //print distances in a 1 D array
    public static void printDistances(double [] array){
        for(int row = 0; row < array.length; row++){
            System.out.println("A" + (row+1) + ": " + array[row]);
        }
    }
    //print relative closeness in ascending order
    public static void printScoresAscending(double[] scores) {
    	Map<String, Double> rankedScoresAscending = new LinkedHashMap<String, Double>();
    	for(int i = 0; i < scores.length; i++) {
    		rankedScoresAscending.put("A"+(i+1), scores[i]);
    	}
    	rankedScoresAscending = sortByValue(rankedScoresAscending);
    	List<Entry<String, Double>> list = new ArrayList<>(rankedScoresAscending.entrySet());
    	
    	for (Entry<String, Double> entry : list) {
            System.out.printf("%s : %f \n", entry.getKey(), entry.getValue());
        }
    }
    public static void main(String[] args) throws Exception
    {

        String file = "Resources/Dataset.txt";
        File fileWrite = new File("Resources/Topsis_ranking_ontologies1.txt");
        PrintStream stream = new PrintStream(fileWrite);
        System.setOut(stream);
        double [][] desMatrix = decisionMatricArray(file);
        Topsis topsis = new Topsis(desMatrix);
        EntropyWeightsMethod entropy = new EntropyWeightsMethod(desMatrix);

        System.out.println("______________Normalized matrix_________________");
        printMatrix(topsis.normalizeDecisionMatrix());

        System.out.println("______________Entropy criteria weights_________________");
        printWeights(entropy.calWeights(entropy.normalizeMatrix()));
        System.out.println();

        System.out.println("\n" + "______________weighted normalized decision matrix_________________");
        printMatrix(topsis.weightedNormalizedDecisionMatrix());

        System.out.println("\n" + "______________ Maximum values of each column - positive ideal _________________");
        printIdeals(topsis.positiveIdealSolution());
        System.out.println();

        System.out.println("\n" +"______________ Minimum values of each column - negative ideal _________________");
        printIdeals(topsis.negativeIdealSolution());
        System.out.println();

        System.out.println( "\n" +"______________distances from positive ideals_________________");
        printDistances(topsis.distancesFromPositiveIdeals());
        System.out.println();

        System.out.println( "\n" +"______________distances from negative ideals_________________");
        printDistances(topsis.distancesFromNegativeIdeals());
        System.out.println();

        System.out.println( "\n" +"______________relative closeness of each ontology_________________");
        topsis.printOntologiesRankings();
        
        System.out.println( "\n" +"______________Rank of the closeness in ascending order_________________");
        printScoresAscending(topsis.relativeCloseness());
        
        
        stream.close();
    }
    
    private static <String, Double extends Comparable<? super Double>> Map<String, Double> sortByValue(Map<String, Double> map) {
        List<Entry<String, Double>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<String, Double> result = new LinkedHashMap<>();
        for (Entry<String, Double> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
