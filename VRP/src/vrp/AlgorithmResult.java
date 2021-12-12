package vrp;

import java.util.ArrayList;

public class AlgorithmResult {

    private String fileName;
    private int maxVehiclesNumber;
    private int maxVehiclesCapacity;
    private int customersNumber;
    private int populationSize;
    private double mutationProbability;
    private String fitnessFunction;
    private int generationsNumber;
    private ArrayList<Integer> PATH;
    private int vehicleNumber;
    private double distance;
    private String time;

    public AlgorithmResult(String fileName, int maxVehiclesNumber, int maxVehiclesCapacity, int customersNumber, int populationSize, double mutationProbability, String fitnessFunction, int generationsNumber, ArrayList<Integer> PATH, int vehicleNumber, double distance, String time) {
        this.fileName = fileName;
        this.maxVehiclesNumber = maxVehiclesNumber;
        this.maxVehiclesCapacity = maxVehiclesCapacity;
        this.customersNumber = customersNumber;
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.fitnessFunction = fitnessFunction;
        this.generationsNumber = generationsNumber;
        this.PATH = PATH;
        this.vehicleNumber = vehicleNumber;
        this.distance = distance;
        this.time = time;
    }

    public String getFileName() {
        return fileName;
    }

    public int getMaxVehiclesNumber() {
        return maxVehiclesNumber;
    }

    public int getMaxVehiclesCapacity() {
        return maxVehiclesCapacity;
    }

    public int getCustomersNumber() {
        return customersNumber;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    public String getFitnessFunction() {
        return fitnessFunction;
    }

    public int getGenerationsNumber() {
        return generationsNumber;
    }

    public ArrayList<Integer> getPATH() {
        return PATH;
    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public double getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }
}
