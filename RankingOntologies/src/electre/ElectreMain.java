package electre;

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

public class ElectreMain
{
    public static double [][] decisionMatricArray(String filename) throws Exception{

         File file = new File(filename);
         Scanner sc = new Scanner(file);
         double [][] matrix = new double[50][11];

         while(sc.hasNextLine()){
             for(int row = 0; row < matrix.length; row++){
                 String[] line = sc.nextLine().split("\\s+");
                 for(int col = 1; col < line.length; col++){
                     matrix[row][col-1] = Double.valueOf(line[col]);
                 }
             }
         }
         sc.close();
         return matrix;
    }

    public static void main(String[] args) throws Exception
    {

        String file = "Resources/Dataset.txt";
        File fileWrite = new File("Resources/ELECTRE_outputs.txt");
        PrintStream stream = new PrintStream(fileWrite);
        System.setOut(stream);
        
        double [][] desMatrix = decisionMatricArray(file);
        
        //------------Decision Matrix------------------------------------------------
        System.out.println("-----------------Decision Matrix----------------------");
        printMatrix(desMatrix);
        
        // 1. Normalized Decision Matrix
        System.out.println("-----------------Normalized Matrix----------------------");
        double [][] normalizedMatrix = Electre.normalizeMatrix(desMatrix);
        printMatrix(normalizedMatrix);
        
        // 2. Weighted Normalized Matrix
        double[] weights = Electre.entropymethod(normalizedMatrix);
        System.out.println("-----------------Criteria Weights----------------------");
        printWeights(weights);
        double [][] weightedNormalizedMatrix = Electre.calculateWeightedNormalizedMatrix(normalizedMatrix, weights);
        System.out.println("-----------------Weighted Normalized Matrix----------------------");
        printMatrix(weightedNormalizedMatrix);
        
        // 4. Concordance and Discordance Matrices
        System.out.println("-----------------Concordance Matrix----------------------");
        double[][] concordanceMatrix = Electre.calculateConcordanceMatrix(weightedNormalizedMatrix, weights);
        printMatrix(concordanceMatrix);
        
        System.out.println("-----------------Discordance Matrix----------------------");
        double[][] discordanceMatrix = Electre.calculateDiscordance(weightedNormalizedMatrix);
        printMatrix(discordanceMatrix);
        
        // 5. threshold and Dominance Matrices
        double thresholdC = Electre.calculateThreshold(concordanceMatrix);
        System.out.printf("Threshold Concordance: %f \n", thresholdC);
        System.out.println("-----------------Concordance Dominance Matrix----------------------");
        double[][] concordanceDominanceMatrix = Electre.calculateDominanceMatrix(concordanceMatrix, thresholdC);
        printMatrixOnes(concordanceDominanceMatrix);
        
        double thresholdD = Electre.calculateThreshold(discordanceMatrix);
        System.out.printf("Threshold Discordance: %f \n", thresholdD);
        System.out.println("-----------------Discordance Dominance Matrix----------------------");
        double[][] discordanceDominanceMatrix = Electre.calculateDominanceMatrix(discordanceMatrix, thresholdD);
        printMatrixOnes(discordanceDominanceMatrix);
        
        // 6. Aggregate Matrix
        double[][] aggregateMatrix = Electre.calculateAggregateDomMatrix(concordanceDominanceMatrix, discordanceDominanceMatrix);
        System.out.println("-----------------Aggregate Dominance Matrix----------------------");
        printMatrixOnes(aggregateMatrix);
        
        // 7. Scores
        double[] scores = Electre.calculateScores(aggregateMatrix);
        System.out.println("-----------------Scores----------------------");
        printScores(scores);
        System.out.println("-----------------Ranked Scores----------------------");
        printScoresAscending(scores);
    }
    
    public static void printMatrix(double[][] matrix) {
    	
    	for(int row = 0 ; row < matrix.length; row++){
            System.out.printf("A%d : ", row+1);
    		for(int col = 0; col < matrix[row].length; col++){
                System.out.printf("%f", matrix[row][col] );
                System.out.print("    ");
            }
            System.out.println();
        }
    	System.out.println("\n-----------------------------------------------------------------------------------------------\n");
    }
    
    public static void printMatrixOnes(double[][] matrix) {
    	
    	for(int row = 0 ; row < matrix.length; row++){
            System.out.printf("A%2d : ", row+1);
    		for(int col = 0; col < matrix[row].length; col++){
                System.out.printf("%d", (int) matrix[row][col] );
                System.out.print(" ");
            }
            System.out.println();
        }
    	System.out.println("\n-----------------------------------------------------------------------------------------------\n");
    }
    
    public static void printScores(double[] scores) {
    	for(int i = 0; i < scores.length; i++) {
    		System.out.printf("A%d : %d", i+1, (int) scores[i]);
    		System.out.println();
    	}
    }
    
    public static void printScoresAscending(double[] scores) {
    	Map<String, Double> rankedScoresAscending = new LinkedHashMap<String, Double>();
    	for(int i = 0; i < scores.length; i++) {
    		rankedScoresAscending.put("A"+(i+1), scores[i]);
    	}
    	rankedScoresAscending = sortByValue(rankedScoresAscending);
    	List<Entry<String, Double>> list = new ArrayList<>(rankedScoresAscending.entrySet());
    	
    	for (Entry<String, Double> entry : list) {
            System.out.printf("%s : %d \n", entry.getKey(), entry.getValue().intValue());
        }
    }
    
    public static void printWeights(double[] weight) {
    	
    	for(int i = 0; i < weight.length; i++) {
    		System.out.printf("w%d : %f  ", (i+1), weight[i]);
    		System.out.println();
    	}
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
