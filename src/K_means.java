import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

public class K_means {

    private int k;

    private List<double[]> dataSet;



    private static String filename = "data.csv";

    public K_means(int k) {
        this.k = k;
    }

    private List<double[]> getDataSet(String filename) throws IOException {
        List<double[]> dataset = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = bufferedReader.readLine())!=null){
            String [] values = line.split("[^\\d.\\d]+");
            double[] data = new double[values.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = Double.parseDouble(values[i]);
            }
            dataset.add(data);
        }



        return dataset;

    }



    public void makeCluster() throws IOException {
        dataSet = getDataSet(filename);
        List<double[]> centroids = getCentroids(dataSet);
        train(centroids);
    }



    private void train(List<double[]> centroids){
        int iterations = 1;
        while (true) {
            HashMap<double[], List<double[]>> clusters = calculate(dataSet, centroids);
            List<double[]> oldCentroids = new ArrayList<>(centroids);
            System.out.println("Iteration number: " + iterations);
            int count = 1;
            centroids.clear();
            for (Map.Entry<double[], List<double[]>> element : clusters.entrySet()) {
                List<double[]> value = element.getValue();
                System.out.println("Cluster " + count +" :"+Arrays.toString(element.getKey())+": ");
                System.out.println("Sum of squares inside clusters: "+ sumOfValue(value));
                for (double[] tmp : value){
                    System.out.print(Arrays.toString(tmp) + " ");
                }
                count++;
                System.out.println();
                centroids.add(updateCentroids(value));
            }
            iterations++;
            boolean changed = false;
            for (int i = 0; i < centroids.size(); i++) {
                if (changed(centroids.get(i), oldCentroids.get(i))){
                    changed = true;
                    break;
                }
            }
            if (!changed){
                break;
            }
        }




    }

    private List<double[]> getCentroids(List<double[]> list){
        List<double[]> result = new ArrayList<>(k);
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            int num = random.nextInt(list.size());
            double [] vector = list.get(num);
            result.add(vector);
        }
        return result;
    }

    private boolean changed(double[]newCentroids, double[] oldCentroids){
        boolean tmp = true;
        for (int i = 0; i < newCentroids.length; i++) {
            if(newCentroids[i] == oldCentroids[i]){
                tmp = false;
            }
        }
        return tmp;
    }


    private HashMap<double[], List<double[]>> calculate(List<double[]> vectors, List<double[]> centroids){
        HashMap<double[], List<double[]>> clusters = new HashMap<>();
        for (double[] vector : vectors) {
            double minDistance = Double.MAX_VALUE;
            double[] minCentroid = null;
            for (double[] centroid : centroids) {
                double distance = calculateDistance(vector, centroid);
                if (distance < minDistance) {
                    minDistance = distance;
                    minCentroid = centroid;
                }
            }
            if (minCentroid != null) {
                clusters.computeIfAbsent(minCentroid, k -> new ArrayList<>()).add(vector);
            }
        }

        return clusters;
    }


    private static double calculateDistance(double[] vector1, double[] vector2) {
        double sum = 0;
        for (int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(sum);
    }


    private double[] updateCentroids(List<double[]> list){
            double[] sumVector = new double[list.get(0).length];
            for (double[] vector : list) {
                for (int i = 0; i < vector.length; i++) {
                    sumVector[i] += vector[i];
                }

            }
            double[] result = new double[sumVector.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = sumVector[i] / list.size();
            }
        return result;
    }

    private double sumOfValue(List<double[]> value){
        int n = value.size()-1;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            double[] current = value.get(i);
            for (int j = i+1; j < n; j++) {
                double[] next = value.get(j);
                double distance = getSquaredDistance(current, next);
                sum+=distance;

            }
        }


        return sum;

    }


    private static double getSquaredDistance(double[] current, double[] next){
        double sum = 0;
        for (int i = 0; i < current.length; i++) {
            double diff = Math.pow(current[i] - next[i], 2);
            sum+=diff;
        }
        return sum;
    }



}
