package topsis;
import java.util.*;

public class EntropyWeightsMethod
{

    private final double [][] matrix;
    public EntropyWeightsMethod(double [][] matrix){
        this.matrix = matrix;
    }
    //This method calculates the column sum
    public double calculateColumnSum(double [][] matrixToCal, int col){
        double sum = 0;
        for(int row = 0; row < matrixToCal.length; row++ ){
            sum += matrixToCal[row][col];
        }
        return sum;
    }

    //Step 1 This method returns a normalized matrix
    public double [][] normalizeMatrix(){
        double[][] normalizeMatrix = new double[matrix.length][matrix[1].length];
        for(int row = 0; row < matrix.length; row++){
            for(int col = 0; col < matrix[row].length; col++){
                double colSum = calculateColumnSum(matrix, col);
                normalizeMatrix[row][col] = (float)matrix[row][col]/colSum;
            }
        }
        return normalizeMatrix;
    }
    //This methods calculates the weights using Entropy Method
    public static double[] calWeights(double[][]normalizedMatrix) {
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

}
