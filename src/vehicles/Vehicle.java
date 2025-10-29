package vehicles;
import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle>{
    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;

    public Vehicle(String id, String model, double maxSpeed,double currentMileage)throws InvalidOperationException{
        if(id == null || id.trim().isEmpty()){
            throw new InvalidOperationException("Vehicle ID cannot be empty");
             
        }
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = currentMileage;
    }

    //Concrete methods

    public void displayInfo(){
        System.out.printf(
            "Vehicle Info:\nID: %s\nModel: %s\nMax Speed: %.2f km/h\nCurrent Mileage: %.2f km\n",
            id, model, maxSpeed, currentMileage
        );
    }
    public double getCurrentMileage(){
        return currentMileage;
    }
    public String getID(){
        return id;
    }
    public String getModel(){
        return model;
    }
    public double getMaxSpeed(){
        return maxSpeed;
    }
    public void setCurrentMileage(double mileage) {
    this.currentMileage = mileage;
    }

    @Override
    public int compareTo(Vehicle other) {
        double o = other.calculateFuelEfficiency();
        double my = this.calculateFuelEfficiency();
        return Double.compare(o, my);
    }


    //Abstract methods

    public abstract void move(double distance)throws InvalidOperationException, InsufficientFuelException;;
    public abstract double calculateFuelEfficiency();
    abstract double estimateJourneyTime(double distance);




    
}
