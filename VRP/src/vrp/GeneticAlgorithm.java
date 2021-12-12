/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrp;

import DataStructure.SavingsElaments;
import controllers.MainWindowController;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author brahim
 */
public class GeneticAlgorithm {

    private double distance_coefficient = 0.4;
    private double vehicles_coefficient = 0.6;

    private MainWindowController root;
    private Instance instance;
    private double MUTATE_PROBABILITY = 0.10;
    private int POPULATION_SIZE = 100;
    private int NUMBERGENERATIONS = 0;

    private Random random;
    private ArrayList<Integer> GENES;
    private ArrayList<Individual> population;
    private int generation;

    public GeneticAlgorithm(MainWindowController root, Instance instance) {
        this.root = root;
        this.instance = instance;
        this.NUMBERGENERATIONS = 0;
        random = new Random();
        GENES = new ArrayList<>();
        for (int i = 0; i < instance.getCustomers_number() - 1; i++) {
            GENES.add(i + 1);
        }
    }

    public GeneticAlgorithm(MainWindowController root, Instance instance, int POPULATION_SIZE) {
        this(root, instance);
        this.NUMBERGENERATIONS = 0;
        this.POPULATION_SIZE = POPULATION_SIZE;
    }

    public GeneticAlgorithm(MainWindowController root, Instance instance, int POPULATION_SIZE,
                            int MUTATE_PROBABILITY, int NUMBERGENERATIONS,
                            int distance_coefficient, int vehicles_coefficient) {
        this(root, instance, POPULATION_SIZE);
        this.MUTATE_PROBABILITY = MUTATE_PROBABILITY / 100.0;
        this.NUMBERGENERATIONS = NUMBERGENERATIONS;
        this.distance_coefficient = distance_coefficient / 100.0;
        this.vehicles_coefficient = vehicles_coefficient / 100.0;
    }

    public void setBestIndividual() {
        instance.setSolution(population.get(0).chromosome,
                population.get(0).distance,
                population.get(0).vehicles_number);
    }

    private ArrayList<Individual> initPopulation() {
        ArrayList<Individual> initial_population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Individual indv = new Individual(createChromosome());
            initial_population.add(indv);
        }
        return initial_population;
    }

    private ArrayList createChromosome() {
        ArrayList<Integer> chromosome = new ArrayList<>();
        ArrayList<Integer> numbers = new ArrayList<>(GENES);
        double vehicle_capacity = 0;
        int i = 0;
        while (i < instance.getCustomers_number() - 1) {
            if (numbers.isEmpty()) break;
            int ind = random.nextInt(numbers.size());
            if (vehicle_capacity + instance.getCustomerDemand(numbers.get(ind)) >
                    instance.getVehicle_capacity()) {
                vehicle_capacity = 0;
                chromosome.add(0);
            } else {
                vehicle_capacity += instance.getCustomerDemand(numbers.get(ind));
                chromosome.add(numbers.remove(ind));
                i++;
            }
        }
        return chromosome;
    }

    private ArrayList[] crossover(Individual indv1, Individual indv2) {
        ArrayList<Integer> child1 = new ArrayList<>();
        ArrayList<Integer> child2 = new ArrayList<>();
        ArrayList<Integer> indv1Ch = new ArrayList<>(indv1.chromosome);
        ArrayList<Integer> indv2Ch = new ArrayList<>(indv2.chromosome);
        int cross = random.nextInt(Math.min(indv1.chromosome.size(), indv2.chromosome.size())-4)+2;
        child1.addAll(indv1Ch.subList(0, cross));
        child2.addAll(indv2Ch.subList(0, cross));

        for (int i = 0; i < child1.size(); i++) {
            try {
                indv1Ch.remove(indv1Ch.indexOf(child2.get(i)));
            } catch (Exception e) {}
            try {
                indv2Ch.remove(indv2Ch.indexOf(child1.get(i)));
            } catch (Exception e) {}
        }

        child1.addAll(indv2Ch);
        child2.addAll(indv1Ch);

        return new ArrayList[]{child1, child2};
    }

    private int runRouletteWheel(double[] rouletteWheel) {
        double r = random.nextDouble();
        int a = 0, b = rouletteWheel.length - 1, m = 0;
        while (b - a > 1) {
            m = (a + b) / 2;
            if (r < rouletteWheel[m]) {
                b = m;
            } else {
                a = m;
            }
        }
        return m;
    }

    private double d2(float x1, float y1, float x2, float y2){
        return Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
    }

    private SavingsElaments calcSavings_Cij() {
        SavingsElaments savingsElaments = new SavingsElaments();
        for (int i = 1; i < instance.getCustomers_number()-1; i++) {
            for (int j = i+1; j < instance.getCustomers_number(); j++) {
                double d = instance.distanceBetween(0, i) + instance.distanceBetween(j, 0) - instance.distanceBetween(i, j);
                savingsElaments.addSaving(i, j, d);
            }
        }
        return savingsElaments;
    }

    private int containedIn(int c, ArrayList<ArrayList<Integer>> routes) {
        int pos = -1;
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).contains(c)) {
                return i;
            }
        }
        return pos;
    }

    private double routeDemande(ArrayList<Integer> customers) {
        double d = 0;
        for (Integer c:customers) {
            d += instance.getCustomerDemand(c);
        }
        return d;
    }

    private Individual savings() {
        SavingsElaments savingsElements = calcSavings_Cij();
        savingsElements.sort();
        ArrayList<ArrayList<Integer>> routes = new ArrayList();
        int i = 0;
        while (i < instance.getCustomers_number()-1) {
            int Ci = savingsElements.getFirstCi();
            int Cj = savingsElements.getFirstCj();
            savingsElements.removeFirst();
            int posCi = containedIn(Ci, routes);
            int posCj = containedIn(Cj, routes);
            if (posCi == -1 && posCj == -1) {
                routes.add(new ArrayList(FXCollections.observableArrayList(Ci, Cj)));
                i+=2;
            } else {
                if (posCi != -1 && posCj == -1) {
                    if (routeDemande(routes.get(posCi)) > instance.getVehicle_capacity()) {
                        routes.add(new ArrayList(FXCollections.observableArrayList(Cj)));
                        i++;
                    } else {
                        if (routes.get(posCi).get(0) == Ci) {
                            routes.get(posCi).add(0, Cj);
                            i++;
                        }
                        if (routes.get(posCi).get(routes.get(posCi).size()-1) == Ci) {
                            routes.get(posCi).add(routes.get(posCi).size(), Cj);
                            i++;
                        }
                    }
                } else if (posCi == -1 && posCj != -1) {
                    if (routeDemande(routes.get(posCj)) > instance.getVehicle_capacity()) {
                        routes.add(new ArrayList(FXCollections.observableArrayList(Ci)));
                        i++;
                    } else {
                        if (routes.get(posCj).get(0) == Cj) {
                            routes.get(posCj).add(0, Ci);
                            i++;
                        }
                        if (routes.get(posCj).get(routes.get(posCj).size()-1) == Cj) {
                            routes.get(posCj).add(routes.get(posCj).size(), Ci);
                            i++;
                        }
                    }
                } else {
                    if (posCi != posCj
                            && (routeDemande(routes.get(posCi))+routeDemande(routes.get(posCj))) <= instance.getVehicle_capacity()) {
                        if (routes.get(posCi).get(routes.get(posCi).size()-1) == Ci
                                && routes.get(posCj).get(0) == Cj) {
                            routes.get(posCi).addAll(routes.get(posCj));
                            routes.remove(posCj);
                        } else if (routes.get(posCi).get(0) == Ci
                                && routes.get(posCj).get(routes.get(posCj).size()-1) == Cj) {
                            routes.get(posCj).addAll(routes.get(posCi));
                            routes.remove(posCi);
                        }
                    }
                }
            }
        }
        ArrayList<Integer> finalRoute = new ArrayList();
        for (int j = 0; j < routes.size(); j++) {
            finalRoute.addAll(routes.get(j));
            finalRoute.add(0);
        }
        finalRoute.remove(finalRoute.size()-1);
        return new Individual(finalRoute);
    }

    /* Start Genetic Algorithm . */
    public void start() throws InterruptedException {
        Individual savingIndv = savings();
        root.statusBar("info", "Population : "+POPULATION_SIZE+" @ Mutation probability : "+MUTATE_PROBABILITY*100+" % @ F(D,V) = "+distance_coefficient+" D + "+vehicles_coefficient+" V @ Generation :");
        root.statusBar("status", "Working");
        generation = 0;
        double lastBestFitness;
        int stabilization = 0;
        population = initPopulation();

        lastBestFitness = population.get(0).fitness;

        boolean end = false;
        while (!end) {
            generation++;
            root.statusBar("generation", String.valueOf(generation));
            /* initialize roulette wheel ---------------------------- */
            double S = 0;
            for (int i = 0; i < POPULATION_SIZE; i++) {
                S += 1 / population.get(i).fitness;
            }
            double[] RW = new double[POPULATION_SIZE];
            RW[0] = (1 / population.get(0).fitness) / S;
            for (int i = 1; i < POPULATION_SIZE; i++) {
                RW[i] = RW[i - 1] + (1 / population.get(i).fitness) / S;
            }
            /* end initialize roulette wheel ------------------------ */

            /* Crossover ------------------- ------------------------ */
            int ii = 0;
            while (ii < POPULATION_SIZE / 2) {
                int indCross1;
                int indCross2;
                indCross1 = runRouletteWheel(RW);
                indCross2 = runRouletteWheel(RW);
                if (random.nextFloat() < 1) {
                    ArrayList[] cross = crossover(population.get(indCross1),
                            population.get(indCross2));
                    Individual child1 = new Individual(cross[0]);
                    Individual child2 = new Individual(cross[1]);
                    /* Mutate ------------------------------------------- */
                    if (indCross1 == indCross2) {
                        if (child1.mutate()) population.add(child1);
                        if (child2.mutate()) population.add(child2);
                    } else {
                        if (random.nextDouble() > 1 - MUTATE_PROBABILITY) {
                            if (child1.mutate()) population.add(child1);
                        } else {
                            if (child1.isValid()) population.add(child1);
                        }
                        if (random.nextDouble() > 1 - MUTATE_PROBABILITY) {
                            if (child2.mutate()) population.add(child2);
                        } else {
                            if (child2.isValid()) population.add(child2);
                        }
                    }
                    /* end Mutate---------------------------------------- */
                    ii++;
                }
            }
            /* end Crossover ------------------- -------------------- */

            /* Sort population -------------------------------------- */
            population.sort((I1, I2) -> {
                return (int) (I1.fitness * 10000 - I2.fitness * 10000);
            });
            /* end Sort population ---------------------------------- */

            /* Selection -------------------------------------------- */
            ArrayList<Individual> newPopulation = new ArrayList<>(population.subList(0, 3 * POPULATION_SIZE / 5));
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    Individual indv = new Individual(population.get(i).getChromosome());
                    if (indv.mutate()) newPopulation.add(indv);
                }
            }
            population = new ArrayList<>(population.subList(3 * POPULATION_SIZE / 5, population.size()));
            for (int i = newPopulation.size(); i < POPULATION_SIZE; i++) {
                newPopulation.add(population.remove(random.nextInt(population.size())));
            }
            population = newPopulation;
            /* end Selection ---------------------------------------- */
            /* Delete repetition ------------------------------------ */
            int k = 0;
            while (k < population.size()-1) {
                int l = k+1;
                while (l < population.size()) {
                    if (population.get(k).chromosome.equals(population.get(l).chromosome)) {
                        population.get(l++).mutate();
                    } else {
                        l++;
                    }
                }
                k++;
            }
            /* end Delete repetition -------------------------------- */

            /* Draw solution ---------------------------------------- */
            if (lastBestFitness != population.get(0).fitness) {
                lastBestFitness = population.get(0).fitness;
                setBestIndividual();
                root.drawSolution();
                Thread.sleep(0);
                stabilization = 0;
            }
            /* end Draw solution ------------------------------------ */
            Thread.sleep(0);
            /* Stop conditions -------------------------------------- */
            if (stabilization == 10000 || NUMBERGENERATIONS == generation) { break; }

            if (generation == 2) population.set(0, savingIndv);

            if (stabilization == 1000) {
                System.out.println("Correction g:"+generation);
                population = new ArrayList<>(population.subList(0, 3*POPULATION_SIZE/5));
                population.addAll(new ArrayList<>(initPopulation().subList(population.size(), POPULATION_SIZE)));
            }
            /* end Stop conditions ---------------------------------- */
            stabilization++;
        }
        root.statusBar("status", "Done");
    }

    public AlgorithmResult getResult(String time) {
        return new AlgorithmResult(instance.getName(), instance.getVehicles_number(), instance.getVehicle_capacity(),
                instance.getCustomers_number()-1, this.POPULATION_SIZE, this.MUTATE_PROBABILITY*100,
                this.distance_coefficient+"*D + "+this.vehicles_coefficient+"*V",
                generation, population.get(0).chromosome, population.get(0).vehicles_number,
                population.get(0).distance, time);
    }

    private void printPopulation(ArrayList<Individual> population) {
        System.out.println("-------------------------------------------------");
        for (Individual individual : population) {
            System.out.print(individual.chromosome);
            System.out.print(": " + individual.fitness);
            System.out.print(" | " + individual.vehicles_number);
            System.out.println(" ... " + individual.isValid());
        }
        System.out.println("-------------------------------------------------");
    }

    private class Individual {

        private ArrayList<Integer> chromosome;
        private int vehicles_number;
        private double fitness;
        private double distance;

        public Individual(ArrayList<Integer> chromosome) {
            this.chromosome = chromosome;
            this.fitness = calcFitness();
            this.distance = distance();
        }

        public ArrayList getChromosome() {
            return new ArrayList(chromosome);
        }

        private boolean validate() {
            while (chromosome.get(chromosome.size() - 1) == 0)
                chromosome.remove(chromosome.size() - 1);
            if (chromosome.get(0) == 0) chromosome.remove(0);
            vehicles_number = 1;
            double capacity = 0;
            int vehicle = 1;
            int i = 0;
            while (i < chromosome.size()) {
                if (vehicle > instance.getVehicles_number()) return false;
                if (chromosome.get(i) == 0) {
                    vehicle++;
                    capacity = 0;
                    vehicles_number++;
                    while (i + 1 < chromosome.size() && chromosome.get(i + 1) == 0) {
                        chromosome.remove(i + 1); // delete repetition of 0
                    }
                } else if (capacity + instance.getCustomerDemand(chromosome.get(i)) > instance.getVehicle_capacity()) {
                    chromosome.add(i, 0);
                    vehicle++;
                    capacity = 0;
                    vehicles_number++;
                } else {
                    capacity += instance.getCustomerDemand(chromosome.get(i));
                }
                i++;
            }
            distance = distance();
            fitness = calcFitness();
            return true;
        }

        public boolean isValid() {
            double capacity = 0;
            int vehicle = 1;
            for (int i = 0; i < chromosome.size(); i++) {
                if (vehicle > instance.getVehicles_number()) {
                    return false;
                }
                if (chromosome.get(i) == 0) {
                    capacity = 0;
                    vehicle++;
                } else {
                    capacity += instance.getCustomerDemand(chromosome.get(i));
                    if (capacity > instance.getVehicle_capacity()) {
                        return false;
                    }
                }
            }
            return true;
        }

        private double calcFitness() {
            return distance_coefficient * distance()
                    + vehicles_coefficient * vehicles_number;
        }

        private double distance() {
            vehicles_number = 0;
            double d = instance.distanceBetween(0, chromosome.get(0));
            for (int i = 1; i < chromosome.size(); i++) {
                d += instance.distanceBetween(chromosome.get(i - 1), chromosome.get(i));
                if (chromosome.get(i) == 0) {
                    vehicles_number++;
                }
            }
            d += instance.distanceBetween(chromosome.get(chromosome.size() - 1), 0);
            vehicles_number++;
            this.distance = d;
            return d;
        }

        private boolean mutate() {
            double prob = random.nextDouble();
            if (prob < 0.15) {
                int t = random.nextInt(POPULATION_SIZE / 5);
                return mutate1(1);
            } else if (prob < 0.75) {
                return mutate2();
            } else {
                return mutate3();
            }
        }

        private boolean mutate1(int t) {
            for (int i = 0; i < t; i++) {
                int r1, r2;
                do {
                    do r1 = random.nextInt(chromosome.size()); while (chromosome.get(r1) == 0);
                    do r2 = random.nextInt(chromosome.size()); while (chromosome.get(r2) == 0);
                } while (r1 == r2);
                int c = chromosome.get(r1);
                chromosome.set(r1, chromosome.get(r2));
                chromosome.set(r2, c);
            }
            return validate();
        }

        private boolean mutate2() {
            int r1, r2;
            r2 = random.nextInt(chromosome.size() - 2) + 2;
            r1 = random.nextInt(r2);
            ArrayList<Integer> subList = new ArrayList<>();
            for (int i = r1; i < r2; i++) subList.add(chromosome.remove(r1));
            int r3 = random.nextInt(chromosome.size());
            chromosome.addAll(r3, subList);
            return validate();
        }

        private boolean mutate3() {
            double capacity = 0;
            int i = 0;
            while (i < chromosome.size() - 1) {
                if (chromosome.get(i) == 0) {
                    if (capacity + instance.getCustomerDemand(chromosome.get(i + 1)) < instance.getVehicle_capacity()) {
                        capacity += instance.getCustomerDemand(chromosome.get(i + 1));
                        chromosome.remove(i);
                    } else {
                        capacity = 0;
                        i++;
                    }
                } else {
                    capacity += instance.getCustomerDemand(chromosome.get(i));
                    i++;
                }
            }
            return validate();
        }
    }
}