/*
 * Copyright (C) 2019 brahim
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package vrp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author brahim remmouche
 */
public class Instance {
    
    private String name;
    private int vehicles_number,vehicle_capacity, customers_number;
    private double [][] distances;
    private int [][] graphCoordinate;
    private HashMap<Integer, Integer[]> customers;
    private int MAX_COORDINATE_WIDTH = Integer.MIN_VALUE,
                MAX_COORDINATE_HEIGHT = Integer.MIN_VALUE,
                MIN_COORDINATE_WIDTH = Integer.MAX_VALUE,
                MIN_COORDINATE_HEIGHT = Integer.MAX_VALUE;
    
    private ObservableList<Object> solution;

    public Instance(File file) throws Exception {
        scanFile(file);
    }
    
    public void setSolution(ArrayList<Integer> PATH, double distance, double vehicles_number) {
        solution = FXCollections.observableArrayList(PATH, distance, vehicles_number);
    }
    
    public boolean isResolved() {
        return solution != null;
    }
    
    public ArrayList<Integer> getSolutionPATH() {
        try {
            return (ArrayList<Integer>) solution.get(0);
        } catch (Exception e) {
            return null;
        }
    }
    
    public double getSolutionDistance() {
        return (double) solution.get(1);
    }
    
    public double getSolutionVehicles_number() {
        return (double) solution.get(2);
    }
    
    private void scanFile(File file) throws Exception {
        Scanner fileIn = new Scanner(file);
        String line;
        if (!fileIn.hasNext()) {
            throw new Exception("File empty.");
        }
        try {
            line = fileIn.nextLine();
            while (line.equals("")) {line = fileIn.nextLine();}
            name = line;
            line = fileIn.nextLine();
            while (line.equals("")) {line = fileIn.nextLine();}
            if (!line.equals("VEHICLE")) {
                throw new Exception("Syntax error.");
            }
            line = fileIn.nextLine();
            line = fileIn.nextLine();
            ArrayList<String> vehicle = new ArrayList<>(FXCollections.observableArrayList(line.split(" ")));
            vehicle.removeIf(item -> item == null || "".equals(item));
            String [] vehicleInf = vehicle.toArray(new String[0]);
            this.vehicles_number = Integer.parseInt(vehicleInf[0]);
            this.vehicle_capacity = Integer.parseInt(vehicleInf[1]);
            line = fileIn.nextLine();
            while (line.equals("")) {line = fileIn.nextLine();}
            if (!line.equals("CUSTOMER")) {
                throw new Exception("Syntax error.");
            }
            this.customers = new HashMap<>();
            line = fileIn.nextLine();
            line = fileIn.nextLine();
            while ( fileIn.hasNext() ) {
                line = fileIn.nextLine();
                if (line.equals("")) {break;}
                ArrayList<String> customer = new ArrayList<>(FXCollections.observableArrayList(line.split(" ")));
                customer.removeIf(item -> item == null || "".equals(item));
                String [] customerInf = customer.toArray(new String[0]);
                customers.put(Integer.parseInt(customerInf[0]),
                        new Integer[]{Integer.parseInt(customerInf[1]),
                                      Integer.parseInt(customerInf[2]),
                                      Integer.parseInt(customerInf[3]),
                                      Integer.parseInt(customerInf[4]),
                                      Integer.parseInt(customerInf[5]),
                                      Integer.parseInt(customerInf[6])});
                if ( MAX_COORDINATE_WIDTH < Integer.parseInt(customerInf[1]) ) {
                    MAX_COORDINATE_WIDTH = Integer.parseInt(customerInf[1]);
                }
                if ( MAX_COORDINATE_HEIGHT < Integer.parseInt(customerInf[2]) ) {
                    MAX_COORDINATE_HEIGHT = Integer.parseInt(customerInf[2]);
                }
                if ( MIN_COORDINATE_WIDTH > Integer.parseInt(customerInf[1]) ) {
                    MIN_COORDINATE_WIDTH = Integer.parseInt(customerInf[1]);
                }
                if ( MIN_COORDINATE_HEIGHT > Integer.parseInt(customerInf[2]) ) {
                    MIN_COORDINATE_HEIGHT = Integer.parseInt(customerInf[2]);
                }
            }
            this.customers_number = this.customers.size();
            this.distances = distance();
            this.graphCoordinate = new int[customers.size()][2];
        } catch (Exception e) {
            throw new Exception("Syntax error.");
        }
    }
    
    private double [][] distance(){
        double [][] dis = new double[this.customers_number][this.customers_number];
        for (int i = 0; i < this.customers_number; i++) {
            for (int j = i; j < this.customers_number; j++) {
                double d = d2(this.customers.get(i)[0],this.customers.get(i)[1],
                        this.customers.get(j)[0],this.customers.get(j)[1]);
                dis[i][j] = d;
                dis[j][i] = d;
            }
        }
        return dis;
    }
    
    private double d2(float x1, float y1, float x2, float y2){
        return Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
    }
    
    public double distanceBetween(int c1, int c2) {
        return distances[c1][c2];
    }
    
    public int getCustomerDemand(int id) {
        return customers.get(id)[2];
    }
    
    public String getDepositInformations() {
        return "Vehicles number : "+vehicles_number
                +"\nVehicles capacity : "+vehicle_capacity
                +"\nDue date : "+customers.get(0)[4];
    }
    
    public String getCustomerInformations(int id) {
        return "Customer : "+id
                +"\nDemand : "+customers.get(id)[2]
                +"\nReady time : "+customers.get(id)[3]
                +"\nDue date : "+customers.get(id)[4]
                +"\nService : "+customers.get(id)[5];
    }

    public String getName() {
        return name;
    }

    public int getVehicles_number() {
        return vehicles_number;
    }

    public int getVehicle_capacity() {
        return vehicle_capacity;
    }

    public int getCustomers_number() {
        return customers_number;
    }
    
    public int[][] getGraphCoordinate(int width, int height, int margin) {
        for (int i = 0; i < this.customers_number; i++) {
            graphCoordinate[i][0] = (int)((customers.get(i)[0]-MIN_COORDINATE_WIDTH)*(width-2*margin)/(MAX_COORDINATE_WIDTH-MIN_COORDINATE_WIDTH)) + margin;
            graphCoordinate[i][1] = (int)((customers.get(i)[1]-MIN_COORDINATE_HEIGHT)*(height-2*margin)/(MAX_COORDINATE_HEIGHT-MIN_COORDINATE_HEIGHT)) + margin;
        }
        return graphCoordinate;
    }
    
    public int[] getNodeCoordinate(int node) {
        return new int[]{graphCoordinate[node][0], graphCoordinate[node][1]};
    }
    
}