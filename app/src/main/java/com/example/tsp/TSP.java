package com.example.tsp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TSP {

    enum DistanceType {EUCLIDEAN, WEIGHTED}

    public class City {
        public int index;
        public double x, y;
    }

    public class Tour {

        double distance;
        int dimension;
        City[] path;

        public Tour(Tour tour) {
            distance = tour.distance;
            dimension = tour.dimension;
            path = tour.path.clone();
        }

        public Tour(int dimension) {
            this.dimension = dimension;
            path = new City[dimension];
            distance = Double.MAX_VALUE;
        }

        public Tour clone() {
            return new Tour(this);
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public City[] getPath() {
            return path;
        }

        public void setPath(City[] path) {
            this.path = path.clone();
        }

        public void setCity(int index, City city) {
            path[index] = city;
            distance = Double.MAX_VALUE;
        }
    }

    String name;
    City start;
    List<City> cities = new ArrayList<>();
    int numberOfCities;
    double[][] weights;
    DistanceType distanceType = DistanceType.EUCLIDEAN;
    int numberOfEvaluations, maxEvaluations;


    public TSP(String path, int maxEvaluations) {
        loadData(path);
        numberOfEvaluations = 0;
        this.maxEvaluations = maxEvaluations;
    }

    public TSP() {

    }

    public void evaluate(Tour tour) {
        double distance = 0;
        distance += calculateDistance(start, tour.getPath()[0]);
        for (int index = 0; index < numberOfCities - 1; index++) {
            if (index + 1 < numberOfCities) {
                distance += calculateDistance(tour.getPath()[index], tour.getPath()[index + 1]);
            }
            else {
                distance += calculateDistance(tour.getPath()[index], start);
            }
        }
        tour.setDistance(distance);
        numberOfEvaluations++;
    }

    public double calculateDistance(City from, City to) {
        //TODO implement
        double value = 0.0;
        switch (distanceType) {
            case EUCLIDEAN:
                value = (Math.sqrt(Math.pow(to.x-from.x,2.0)+Math.pow(to.y-from.y,2.0)));
                return value;
            case WEIGHTED:
                value = weights[from.index][to.index];
                return value;
            default:
                return Double.MAX_VALUE;
        }
    }

    public Tour generateTour(ArrayList<Integer> indexes) {
        List<Integer> nums = new ArrayList<>();


        numberOfCities = indexes.toArray().length;
        for(int i=0;i<numberOfCities;i++){
            nums.add(indexes.get(i));
        }


        for(int i=1;i<numberOfCities;i++){
            int rand_int1 = RandomUtils.nextInt(1, numberOfCities);
            Collections.swap(nums,rand_int1,i);
        }

        Tour newTour = new Tour(numberOfCities);
        City[] tempPath = new City[numberOfCities];
        for(int i=0;i<nums.size();i++){
            tempPath[i] = cities.get(nums.get(i));
        }

        newTour.setPath(tempPath);
        return newTour;
    }

    private void loadData(String path) {
        //TODO set starting city, which is always at index 0
        InputStream inputStream = TSP.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            System.err.println("File " + path + " not found!");
            return;
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
                if (line.contains("DIMENSION")) {
                    numberOfCities = Integer.parseInt(line.split(": ")[1]);
                    weights = new double[numberOfCities][numberOfCities];
                }
                if (Objects.equals(line, "EDGE_WEIGHT_TYPE : EUC_2D")) {
                    lines.add(line);
                    line = br.readLine();
                    line = br.readLine();
                    while (line != null) {
                        lines.add(line);


                        if (!Objects.equals(line.split(" ")[0], "EOF")) {
                            cities.add(new City());
                            cities.get(cities.size() - 1).index = Integer.parseInt(line.split(" ")[0]) - 1;
                            cities.get(cities.size() - 1).x = Double.parseDouble(line.split(" ")[1]);
                            cities.get(cities.size() - 1).y = Double.parseDouble(line.split(" ")[2]);
                            line = br.readLine();
                        } else {
                            line = br.readLine();
                        }
                    }
                } else if (Objects.equals(line, "EDGE_WEIGHT_TYPE: EXPLICIT")) {
                    distanceType = DistanceType.WEIGHTED;
                    //Ustvari City
                    for(int v=0;v<numberOfCities;v++){
                        cities.add(new City());
                        cities.get(v).index = v;
                    }
                    while (line != null) {
                        lines.add(line);
                        line = br.readLine();
                        if (Objects.equals(line, "EDGE_WEIGHT_SECTION")) {
                            int currentLine = 0;

                            while (line != null) {
                                lines.add(line);
                                line = br.readLine();

                                if (!Objects.equals(line.split(" ")[0], "EOF")) {

                                    String[] weightsString = (line.split(" "));
                                    int currentIndex = 0;
                                    for(int i=0;i<weightsString.length;i++) {
                                        if(currentLine >= numberOfCities){
                                            currentLine++;
                                            break;
                                        }
                                        if(weightsString[i] != "") {
                                            weights[currentLine][currentIndex] = Double.parseDouble(weightsString[i]);
                                            currentIndex++;
                                        }


                                    }
                                    currentLine++;
                                } else {
                                    line = br.readLine();
                                }

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        start = cities.get(0);
        //TODO parse data
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public int getNumberOfEvaluations() {
        return numberOfEvaluations;
    }
}
