package electre;

import java.util.*;

public class Electre
{
    // 1. -------------------------Normalize Decision Matrix------------------------------------
	//This method calculates the column sum
    public static double calculateColumnSum(double [][] matrix, int col){
        double sum = 0;
        for(int row = 0; row < matrix.length; row++ ){
            sum += matrix[row][col];
        }
        return sum;
    }

    //This method returns a normalized matrix
    public static double [][] normalizeMatrix(double [][] matrix){
        double [][] normalizeMatrix = new double[matrix.length][matrix[1].length];
        for(int row = 0; row < matrix.length; row++){
            for(int col = 0; col < matrix[row].length; col++){
            	double colSum = calculateColumnSum(matrix, col);
                normalizeMatrix[row][col] = matrix[row][col]/colSum;
            }
        }
        return normalizeMatrix;
    }
    
    //This methods calculates the weights using Sum Method
    public static double [] calculateWeights(double [][] normalizeMatrix){
            double [] weights = new double[normalizeMatrix.length];

            for(int row = 0; row < normalizeMatrix.length; row++){
                double weightsColSum = 0;
                for(int col = 0; col < normalizeMatrix[row].length; col++){
                    double colSum = calculateColumnSum(normalizeMatrix, col);
                    weightsColSum += normalizeMatrix[row][col]/colSum;
                }
                weights[row] = weightsColSum;
                for (int weight =0; weight <weights.length; weight++)
                	weights[weight]= weights[weight]*0.33;
            }

            return weights;
    }

  //This methods calculates the weights using Entropy Method
    public static double[] entropymethod(double[][]normalizedMatrix) {
    	double h = 1/Math.log(normalizedMatrix.length);
    	double[][]eMatrix = new double[normalizedMatrix.length][normalizedMatrix[0].length];
    	for(int i =0; i<normalizedMatrix.length;i++) {
    		for(int j =0;j<normalizedMatrix[i].length;j++) {
    			//if(j==0) eMatrix[i][j]=normalizedMatrix[i][j];
    			if(normalizedMatrix[i][j]==0)eMatrix[i][j]=0.0;
    			else eMatrix[i][j]=normalizedMatrix[i][j] * Math.log(normalizedMatrix[i][j]);
    		}
    	}
    	double[]sums = new double[eMatrix[0].length];
    	Arrays.fill(sums, 0.0);
    	for(int row =0; row<eMatrix.length;row++) {
    		for(int col =0; col<eMatrix[row].length;col++) {
    			sums[col]= sums[col]+eMatrix[row][col];
    		}
    	}
    	double ejMatrix[] = new double[sums.length];
    	for(int sum = 0;sum<sums.length;sum++)
    		ejMatrix[sum] = sums[sum]* -h;
    	double []djMatrix = new double[ejMatrix.length];
    	double normalSum =0;
    	for(int d=0;d<djMatrix.length;d++) {
    		djMatrix[d] = 1-ejMatrix[d];
    		normalSum= normalSum +djMatrix[d];
    	}
    	double weightMatrix[] = new double[djMatrix.length];
    	for(int w=0; w<djMatrix.length;w++)
    		weightMatrix[w]=djMatrix[w]/normalSum;
    	return weightMatrix;
    }
    
    // 2. ----------------------------------Weighted Normalized Matrix----------------------------------------
    //Calculate weighted normalized matrix
    public static double[][] calculateWeightedNormalizedMatrix(double [][]normalizedMatrix,double[]weights){
    	double[][]weighted = new double[normalizedMatrix.length][normalizedMatrix[0].length];
    	for(int i=0;i<normalizedMatrix.length;i++) {
    		for(int j=0;j<normalizedMatrix[i].length;j++) {
    			if(j==0)weighted[i][j]= normalizedMatrix[i][j];
    			else weighted[i][j]= normalizedMatrix[i][j]*weights[j];
    		}
    	}
    	return weighted;
    }
    
    // 3. ---------------------------Concordance and Discornance Set------------------------------------------
    // get the discornance set of Ar vs As
    public static ArrayList<MySet> getDiscordanceSetRS(double[][] weightedNormalMatrix, int r, int s){
    	ArrayList<MySet> set = new ArrayList<>();
    	
    	for (int column = 0; column < weightedNormalMatrix[r].length; column++) {
    		if(weightedNormalMatrix[s][column] <= weightedNormalMatrix[r][column]) {
    			set.add(new MySet(column, weightedNormalMatrix[s][column]));
    		}
    	}
    	
    	return set;
    } 
    
    // get the concordance set of Ar vs As
    public static ArrayList<MySet> getConcordanceSetRS(double[][] weightedNormalMatrix, int r, int s){
    	ArrayList<MySet> set = new ArrayList<>();
    	
    	for (int column = 0; column < weightedNormalMatrix[r].length; column++) {
    		if(weightedNormalMatrix[s][column] >= weightedNormalMatrix[r][column]) {
    			set.add(new MySet(column, weightedNormalMatrix[s][column]));
    		}
    	}
    	return set;
    } 
    
    //4. --------------------------------------------Concordance and Discordance Matrices---------------------------
    
    // 4a. Calculate concordance matrix
    
    public static double[][] calculateConcordanceMatrix(double[][] weightedNormalMatrix, double[] weights){
    	double[][]concordanceMatrix = new double[weightedNormalMatrix.length][weightedNormalMatrix.length];
    	
    	for(int i=0;i<concordanceMatrix.length;i++) {
    		for(int j =0;j<concordanceMatrix.length;j++) {
    			if(i != j) {
    				ArrayList<MySet> concordanceSet = getConcordanceSetRS(weightedNormalMatrix, i, j);
        			double sum = 0;
        			
        			for(MySet getJ: concordanceSet) {
        				sum += weights[getJ.getJ()];	// sums the weights of elements in Concordance set
        			}
        			concordanceMatrix[i][j] = sum;
    			}
    		}
    	}
    	return concordanceMatrix;
    }
    
 // 4b. Calculating the Discordance Matrix
    public static double[][] calculateDiscordance(double[][] weightedNormalMatrix){
    	double[][] discordanceMatrix = new double[weightedNormalMatrix.length][weightedNormalMatrix.length];
    	
    	for(int i=0;i<discordanceMatrix.length;i++) {
    		for(int j =0;j<discordanceMatrix.length;j++) {
    			if(i != j) {
    				if(getRS_MAX(weightedNormalMatrix, i, j) != 0) {
    					discordanceMatrix[i][j] = getDiscordanceSetMax(weightedNormalMatrix, i, j)/getRS_MAX(weightedNormalMatrix, i, j);
    				}
    			}
    		}
    	}
    	return discordanceMatrix;
    }
    
    
    // get the discordance maximum value of the discordance set
    
    private static double getDiscordanceSetMax(double[][] weightedNormalMatrix, int r, int s){
    	ArrayList<MySet> disSet = getDiscordanceSetRS(weightedNormalMatrix, r, s);
    	double max = 0;
    	
    	for(int j = 0; j < weightedNormalMatrix[s].length; j++) {
    		for(int i = 0; i < disSet.size(); i++) {
    			if(j == disSet.get(i).getJ()) { 		// check if j is an element of discordance set
    				max = Math.abs(weightedNormalMatrix[s][j]-weightedNormalMatrix[r][j]);
    			}
    		}
    	}
    	
    	return max;
    }
    
    // get the maximum value of the |Vsj - Vrj| elements in As and Ar
    private static double getRS_MAX(double[][] weightedNormalMatrix, int r, int s) {
    	double max = 0;
    	
    	// get max value of |Vsj - Vrj|
    	for(int j = 0; j < weightedNormalMatrix[s].length; j++) {
    		double absDiffirenceSR = Math.abs(weightedNormalMatrix[s][j]-weightedNormalMatrix[r][j]);
    		if(absDiffirenceSR > max) max = absDiffirenceSR;
    	}
    	
    	return max;
    }
    
    
    // 5. -------------------------------Threshold and Domince Matrix-----------------------------------------
    // calculate the threshold concordance/discordance 
    public static double calculateThreshold(double[][]matrix) {
    	double[]sums = new double[matrix[0].length];
    	double threshold =0.0;
    	double entries = Math.pow((matrix.length), 2) - (matrix.length);
    	//System.out.println(entries);
    	Arrays.fill(sums, 0.0);
    	for(int row =0; row<matrix.length;row++) {
    		for(int col =0; col<matrix[row].length;col++) {
    			sums[col]= sums[col]+matrix[row][col];
    		}
    	}
    	for(double d:sums)
    		threshold +=d;
    	threshold /=entries;
    	//System.out.println(threshold);
    	return threshold;
    }
    
    // calculate the concordance/discordance dominance matrix
    public static double[][]calculateDominanceMatrix(double[][]matrix,double threshold){
    	double dominanceMatrix[][] = new double[matrix.length][matrix.length];
    	for(int i=0;i<matrix.length;i++) {
    		for(int j=0;j<matrix.length;j++) {
    			if(matrix[i][j]>=threshold) dominanceMatrix[i][j] = 1;
    			else dominanceMatrix[i][j]=0;
    		}
    	}
    	return dominanceMatrix;
    }
    
    // 6. Aggregate Dominance matrix------------------------------------------------------------------------------
    
    public static double[][] calculateAggregateDomMatrix(double[][] conDominanceMat, double[][] disDominanceMat){
    	double[][] aggregateMatrix = new double[conDominanceMat.length][conDominanceMat.length];
    	
    	for(int i=0;i<conDominanceMat.length;i++) {
    		for(int j=0;j<conDominanceMat.length;j++) {
    			aggregateMatrix[i][j] = conDominanceMat[j][i]*disDominanceMat[i][j];
    		}
    	}
    	return aggregateMatrix;
    }
    
    // 7. Scores --------------------------------------------------------------------------------------
    
    public static double[] calculateScores(double[][] aggregateMatrix) {
    	double[] scores = new double[aggregateMatrix.length];
    	
    	for(int k = 0; k < scores.length; k++) {
    		double sum1 = 0;
    		double sum2 = 0;
    		for(int i = 0; i < aggregateMatrix.length; i++) {
    			sum1 += aggregateMatrix[k][i];
    		}
    		for(int j = 0; j < aggregateMatrix.length; j++) {
    			sum2 += aggregateMatrix[j][k];
    		}
    		scores[k] = sum1+sum2;
    	}
    	return scores;
    }
}
