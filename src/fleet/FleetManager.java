package fleet;

import java.util.*;
import java.io.*;

import vehicles.*;
import interfaces.*;
import exceptions.*;

public class FleetManager{

    private final List<Vehicle> fleet;

    //set to maintain distinct model names
    private Set<String> modelSet = new HashSet<>();

    public FleetManager(){
        this.fleet = new ArrayList<>();

    }

    public void addVehicle(Vehicle v)throws InvalidOperationException{
        Objects.requireNonNull(v, "Vehicle cannot be null");
        String id = v.getID();
        boolean exists = fleet.stream().anyMatch(x -> x.getID().equals(id));
        if (exists){
            throw new InvalidOperationException("Duplicate vehicle id: " + id);
        }
        fleet.add(v);

        String model = v.getModel();
        boolean added = modelSet.add(model);
        if (!added) {
            // model already existed, letting user know
            System.out.println("Note: model already present in fleet: " + model);
        }
    }


    public void removeVehicle(String id) throws InvalidOperationException{

        Iterator<Vehicle> it = fleet.iterator();
        while (it.hasNext()){
            Vehicle v = it.next();
            if (v.getID().equals(id)){
                String removedModel = v.getModel();
                it.remove();

                // If no other vehicle uses this model, remove it from modelSet
                boolean stillUsed = fleet.stream().anyMatch(x -> removedModel.equals(x.getModel()));
                if (!stillUsed){
                    modelSet.remove(removedModel);
                    }
                return;
                }
            }
            modelSet.removeIf(model -> fleet.stream().noneMatch(v -> v.getModel().equals(model)));

        throw new InvalidOperationException("Vehicle not found: " + id);
        }

    public Set<String> getDistinctModels(){
        // Return an alphabetically ordered view using tree set
        return new TreeSet<>(modelSet);
    }


    public Map<String, String> startAllJourneys(double distance){
        Map<String, String> result=new LinkedHashMap<>();
        for (Vehicle v:fleet){
            try{
                v.move(distance);
                result.put(v.getID(), "Ok");
            } 
            catch(InvalidOperationException | InsufficientFuelException e){
                result.put(v.getID(), "failed: " + e.getMessage());
            } 
            catch (Exception e){
                result.put(v.getID(), "unexpected fail: " + e.getMessage());
            }
        }
        return result;
    }

    public double consumeFuelForAll(double distance) {
        double tot=0.0;
        for (Vehicle v:fleet){
            if (v instanceof FuelConsumable){
                FuelConsumable fuel_consum= (FuelConsumable) v;
                try{
                    double used = fuel_consum.consumeFuel(distance);
                    tot += used;
                } 
                catch (InsufficientFuelException e){
                    System.out.println("Vehicle " + v.getID() + " skipped: " + e.getMessage());
                }
            }
        }
        return tot;
    }

    public void displayFleet() {
        if (fleet.isEmpty()) {
            System.out.println("No vehicles in the fleet.");
            return;
        }

        System.out.println("\nCurrent Fleet:");
        System.out.println("------------------------------------------------");
        for (Vehicle v : fleet) {
            v.displayInfo();
            System.out.println("------------------------------------------------");
        }
    }


    public void maintainAll(){
        for (Vehicle v:fleet){
            if (v instanceof Maintainable){
                Maintainable m=(Maintainable)v;
                if (m.needsMaintenance()){
                    m.performMaintenance();
                }
            }
        }
    }

    public List<Vehicle> searchByType(Class<?> type){
        List<Vehicle> ans=new ArrayList<>();
        for (Vehicle v:fleet){
            if (type.isInstance(v)) ans.add(v);
        }
        return ans;
    }


    public List<Vehicle> getFleetSortedBySpeed() {
        List<Vehicle> sorted = new ArrayList<>(fleet);
        Comparator<Vehicle> cmp = Comparator.comparingDouble(Vehicle::getMaxSpeed);
        cmp = cmp.reversed();
        sorted.sort(cmp);
        return sorted;
    }

    public List<Vehicle> getFleetSortedByModel(){
        List<Vehicle> sorted = new ArrayList<>(fleet);
        Comparator<Vehicle> cmp = (a,b) -> a.getModel().compareToIgnoreCase(b.getModel());
        sorted.sort(cmp);
        return sorted;

    }
    public List<Vehicle> getFleetSortedByEfficiency(){
        List<Vehicle> sorted = new ArrayList<>(fleet);
        Comparator<Vehicle> cmp = Comparator.comparingDouble(Vehicle::calculateFuelEfficiency);
        cmp  = cmp.reversed();
        sorted.sort(cmp);
        return sorted;
    }
    public Vehicle getFastestVehicle(){
        if(fleet.isEmpty()) return null;
        return Collections.max(fleet,Comparator.comparingDouble(Vehicle::getMaxSpeed));

    }
    public Vehicle getSlowestVehicle(){
        if(fleet.isEmpty()) return null;
        return Collections.min(fleet,Comparator.comparingDouble(Vehicle::getMaxSpeed));
        
    }




    public String generateReport(){
        StringBuilder str_bldr = new StringBuilder();
        str_bldr.append("Fleet report\n------------\n");
        str_bldr.append("Total vehicles: ").append(fleet.size()).append("\n");

        Map<String,Integer> byType=new TreeMap<>();
        double totalEff=0;
        int effCount=0;
        double totalMileage=0;
        int maintenanceCount=0;

        for (Vehicle v:fleet){
            byType.merge(v.getClass().getSimpleName(), 1, Integer::sum);
            double eff=v.calculateFuelEfficiency();
            if (eff>0) { totalEff += eff; effCount++; }
            totalMileage += v.getCurrentMileage();
            if (v instanceof Maintainable && ((Maintainable) v).needsMaintenance()) maintenanceCount++;
        }

        str_bldr.append("By type:\n");
        byType.forEach((k,c)-> str_bldr.append("  ").append(k).append(": ").append(c).append("\n"));
        str_bldr.append("Total mileage: ").append(totalMileage).append(" km\n");
        str_bldr.append("Avg fuel efficiency: ");
        str_bldr.append(effCount>0 ? String.format("%.2f km/l", totalEff / effCount) : "N/A").append("\n");
        str_bldr.append("Vehicles needing maintenance: ").append(maintenanceCount).append("\n");

        return str_bldr.toString();
    }

    public List<Vehicle> getVehiclesNeedingMaintenance(){
        List<Vehicle> output=new ArrayList<>();
        for (Vehicle v:fleet){
            if (v instanceof Maintainable && ((Maintainable) v).needsMaintenance()){
                output.add(v);
            }
        }
        return output;
    }

    public void saveToFile(String filename) throws IOException{
        try (PrintWriter prnt_writer = new PrintWriter(new FileWriter(filename))){
                for (Vehicle v:fleet){
                    if (v instanceof Car){
                        Car c=(Car) v;
                        prnt_writer.printf("Car,%s,%s,%.2f,%.2f,%d,%.2f,%d,%d%n",
                                c.getID(), c.getModel(), c.getMaxSpeed(), c.getCurrentMileage(),
                                c.getNumWheels(), c.getFuelLevel(), 
                                c.getPassengerCapacity(), c.getCurrentPassengers());
                    } 
                    else if (v instanceof Truck){
                        Truck t=(Truck)v;
                        prnt_writer.printf("Truck,%s,%s,%.2f,%.2f,%d,%.2f,%.2f,%.2f%n",
                                t.getID(), t.getModel(), t.getMaxSpeed(), t.getCurrentMileage(),
                                t.getNumWheels(), t.getFuelLevel(),
                                t.getCargoCapacity(), t.getCurrentCargo());
                    } 
                    else if (v instanceof Bus){
                        Bus b=(Bus)v;
                        prnt_writer.printf("Bus,%s,%s,%.2f,%.2f,%d,%.2f,%d,%d,%.2f,%.2f%n",
                                b.getID(), b.getModel(), b.getMaxSpeed(), b.getCurrentMileage(),
                                b.getNumWheels(), b.getFuelLevel(),
                                b.getPassengerCapacity(), b.getCurrentPassengers(),
                                b.getCargoCapacity(), b.getCurrentCargo());
                    }
                    else if (v instanceof Airplane){
                        Airplane a = (Airplane) v;
                        prnt_writer.printf("Airplane,%s,%s,%.2f,%.2f,%.2f,%.2f,%d,%d,%.2f,%.2f%n",
                                a.getID(), a.getModel(), a.getMaxSpeed(), a.getCurrentMileage(),
                                a.getMaxAltitude(), a.getFuelLevel(),
                                a.getPassengerCapacity(), a.getCurrentPassengers(),
                                a.getCargoCapacity(), a.getCurrentCargo());
                    } 
                    else if (v instanceof CargoShip){
                        CargoShip cs=(CargoShip) v;
                        prnt_writer.printf("CargoShip,%s,%s,%.2f,%.2f,%b,%.2f,%.2f,%.2f%n",
                                cs.getID(), cs.getModel(), cs.getMaxSpeed(), cs.getCurrentMileage(),
                                cs.hasSail(), cs.getFuelLevel(),
                                cs.getCargoCapacity(), cs.getCurrentCargo());
                    }
                }
            }
        }

    public void loadFromFile(String filename) throws IOException{
        List<Vehicle> load1=new ArrayList<>();
        try (BufferedReader br=new BufferedReader(new FileReader(filename))){
            String line;
            int lineNo=0;
            while ((line = br.readLine())!=null){
                lineNo++;
                if (line.trim().isEmpty()) continue;
                String[] tokens=splitCsvLine(line);
                try{
                    Vehicle v=VehicleFactory.createVehicle(tokens);
                    load1.add(v);
                } 
                catch(Exception e){
                    System.out.println("Skipping line " + lineNo + ": " + e.getMessage());
                }
            }
        }
        // replace fleet with loaded vehicles
        fleet.clear();
        fleet.addAll(load1);
        modelSet.clear();
        for (Vehicle v : fleet) {
            modelSet.add(v.getModel());
        }

    }

    private String[] splitCsvLine(String line) {
        return line.split(",", -1);
    }


    private static class VehicleFactory{
        static Vehicle createVehicle(String[] tok) throws Exception{
            if (tok.length==0) throw new InvalidOperationException("Empty CSV line");
            String type=tok[0].trim();

            switch (type){
                case "Car":{
                    if (tok.length < 9) throw new InvalidOperationException("Malformed Car CSV");
                    String id = tok[1];
                    String model = tok[2];
                    double maxSpeed = Double.parseDouble(tok[3]);
                    double currentMileage = Double.parseDouble(tok[4]);
                    int numWheels = Integer.parseInt(tok[5]);
                    Car c = new Car(id, model, maxSpeed, currentMileage, numWheels);
                    return c;
                }
                case "Truck":{
                    if (tok.length < 9) throw new InvalidOperationException("Malformed Truck CSV");
                    String id = tok[1];
                    String model = tok[2];
                    double maxSpeed = Double.parseDouble(tok[3]);
                    double currentMileage = Double.parseDouble(tok[4]);
                    int numWheels = Integer.parseInt(tok[5]);
                    Truck t = new Truck(id, model, maxSpeed, currentMileage, numWheels);
                    try{t.refuel(Double.parseDouble(tok[6]));}
                    catch (Exception ignored){}
                    return t;
                }
                case "Bus":{
                    if (tok.length < 11) throw new InvalidOperationException("Malformed Bus CSV");
                    String id = tok[1];
                    String model = tok[2];
                    double maxSpeed = Double.parseDouble(tok[3]);
                    double currentMileage = Double.parseDouble(tok[4]);
                    int numWheels = Integer.parseInt(tok[5]);
                    Bus b=new Bus(id, model, maxSpeed, currentMileage, numWheels);
                    try {b.refuel(Double.parseDouble(tok[6]));} 
                    catch (Exception ignored) {}
                    return b;
                }
                case "Airplane":{
                    if (tok.length<11) throw new InvalidOperationException("Malformed Airplane CSV");
                    String id = tok[1];
                    String model = tok[2];
                    double maxSpeed = Double.parseDouble(tok[3]);
                    double currentMileage = Double.parseDouble(tok[4]);
                    double maxAltitude = Double.parseDouble(tok[5]);
                    Airplane a=new Airplane(id, model, maxSpeed, currentMileage, maxAltitude);
                    try {a.refuel(Double.parseDouble(tok[6]));}
                    catch (Exception ignored) {}
                    return a;
                }
                case "CargoShip":{
                    if (tok.length < 9) throw new InvalidOperationException("Malformed CargoShip CSV");
                    String id = tok[1];
                    String model = tok[2];
                    double maxSpeed = Double.parseDouble(tok[3]);
                    double currentMileage = Double.parseDouble(tok[4]);
                    boolean hasSail = Boolean.parseBoolean(tok[5]);
                    CargoShip cs = new CargoShip(id, model, maxSpeed, currentMileage, hasSail);
                    try {cs.refuel(Double.parseDouble(tok[6]));}
                    catch (Exception ignored) {}
                    return cs;
                }
                default:
                    throw new InvalidOperationException("Unknown vehicle type: " + type);
            }
        }
    }

    public List<Vehicle> getFleetSnapshot() {
        return new ArrayList<>(fleet);
    }














}
