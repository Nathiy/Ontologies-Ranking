package topsis;

public class Topsis
{
    private final double[][] decisionMatrix;

    public Topsis(double[][] decisionMatrix)
    {
        this.decisionMatrix = decisionMatrix;
    }

    /*This method calculates the total sum square of a col*/
    public double colSumSquare(int col)
    {
        double sum = 0;
        for (int row = 0; row < decisionMatrix.length; row++)
        {
            sum += (float) Math.pow(decisionMatrix[row][col], 2);
        }
        return sum;
    }

    //Step 1 This method normalizes the decision matrix.
    public double[][] normalizeDecisionMatrix()
    {
        double [][] normalizeDecMatrix = new double[decisionMatrix.length][decisionMatrix[1].length];
        double normValue;
        double colSum;
        for (int row = 0; row < decisionMatrix.length; row++)
        {
            for (int col = 0; col < decisionMatrix[0].length; col++)
            {
                colSum = colSumSquare(col);
                normValue = decisionMatrix[row][col] / (Math.sqrt(colSum));
                normalizeDecMatrix[row][col] = normValue;
            }
        }
        return normalizeDecMatrix;
    }

    //Step 2 This method calculates the weighted normalized matrix using the normalized matrix and the weights.
    public double[][] weightedNormalizedDecisionMatrix()
    {
        EntropyWeightsMethod entropyMethod = new EntropyWeightsMethod(decisionMatrix);
        double[][] normalizeDecMatrix = normalizeDecisionMatrix();
        double[] weights = EntropyWeightsMethod.calWeights(entropyMethod.normalizeMatrix());
        double[][] weightedNormalizedDecMatrix = new double[normalizeDecMatrix.length][normalizeDecMatrix[1].length];

        for (int row = 0; row < normalizeDecMatrix.length; row++)
        {
            for (int col = 0; col < normalizeDecMatrix[col].length; col++)
            {
                weightedNormalizedDecMatrix[row][col] = normalizeDecMatrix[row][col] * weights[col];
            }
        }
        return weightedNormalizedDecMatrix;
    }

    //Step 3 Calculates the positive ideal solutions for each row and return the array.
    public double[] positiveIdealSolution()
    {
        double[][] weightedNormalizedDecMatrix = weightedNormalizedDecisionMatrix();
        double[] idealPositives = new double[weightedNormalizedDecMatrix[0].length];

        for (int col = 0; col < weightedNormalizedDecMatrix[0].length; col++)
        {
            double max = 0;
            for (int row = 0; row < idealPositives.length; row++)
            {
                if (weightedNormalizedDecMatrix[row][col] > max)
                {
                    max = weightedNormalizedDecMatrix[row][col];
                }
            }
            idealPositives[col] = max;
        }
        return idealPositives;
    }

    //Step 3 Calculates the negative ideal solutions for each row and return the array.
    public double[] negativeIdealSolution()
    {

        double[][] weightedNormalizedDecMatrix = weightedNormalizedDecisionMatrix();
        double[] idealNegatives = new double[weightedNormalizedDecMatrix[0].length];

        for (int col = 0; col < weightedNormalizedDecMatrix[0].length; col++)
        {
            double min = 1;
            for (int row = 0; row < idealNegatives.length; row++)
            {
                if (weightedNormalizedDecMatrix[row][col] < min )
                {
                    min = weightedNormalizedDecMatrix[row][col];
                }
            }
            idealNegatives[col] = min;
        }
        return idealNegatives;
    }

    //Calculates the squared sum of alternative distance .
    public double alternativeSquareDistanceSumPos(int row)
    {
        double [] posIdeas = positiveIdealSolution();
        double alterSumSquare = 0;
        double[][] weightedNormalizedDecMatrix = weightedNormalizedDecisionMatrix();

        for (int col = 0; col < weightedNormalizedDecMatrix[row].length; col++)
        {
            alterSumSquare = Math.pow(weightedNormalizedDecMatrix[row][col]-posIdeas[col],2);
        }
        return alterSumSquare;
    }

    //Calculates the squared sum of alternative distance .
    public double alternativeSquareDistanceSumNeg(int row)
    {
        double [] negIdeas = negativeIdealSolution();
        double alterSumSquare = 0;
        double[][] weightedNormalizedDecMatrix = weightedNormalizedDecisionMatrix();

        for (int col = 0; col < weightedNormalizedDecMatrix[row].length; col++)
        {
            alterSumSquare = Math.pow(weightedNormalizedDecMatrix[row][col]-negIdeas[col],2);
        }
        return alterSumSquare;
    }

    // Step 4 Calculates the distances from positives Ideal Solutions and returns the array
    public double[] distancesFromPositiveIdeals()
    {
        double[][] weightedNormalizedDecMatrix = weightedNormalizedDecisionMatrix();
        double[] distPosIdeals = new double[weightedNormalizedDecMatrix.length];

        for (int row = 0; row < distPosIdeals.length; row++)
        {
            double altSquDisSum = alternativeSquareDistanceSumPos(row);
            distPosIdeals[row] = Math.sqrt(altSquDisSum);
        }
        return distPosIdeals;
    }

    // Step 4 Calculates the distances from negative Ideal Solutions and returns the array
    public double[] distancesFromNegativeIdeals()
    {
        double[][] weightedNormalizedDecMatrix = weightedNormalizedDecisionMatrix();
        double[] distNegIdeals = new double[weightedNormalizedDecMatrix.length];

        for (int row = 0; row < distNegIdeals.length; row++)
        {
            double altSquDisSum = alternativeSquareDistanceSumNeg(row);
            distNegIdeals[row] =  Math.sqrt(altSquDisSum);
        }
        return distNegIdeals;
    }

    // Step 5 Calculates the relative Closeness of Alternatives.
    public double[] relativeCloseness()
    {
        double[] distPos = distancesFromPositiveIdeals();
        double[] distNeg = distancesFromNegativeIdeals();
        double[] clossness = new double[weightedNormalizedDecisionMatrix().length];

        for (int row = 0; row < clossness.length; row++)
        {
            clossness[row] = distNeg[row]/ (distPos[row] + distNeg[row]);
        }
        return clossness;
    }

    //prints the relative closeness
    public void printOntologiesRankings()
    {
        double[] relClo = relativeCloseness();
        for (int row = 0; row < relClo.length; row++)
        {
            System.out.println("A" + (row + 1) + ": " + relClo[row]);
        }
    }
}
