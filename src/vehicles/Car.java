package vehicles;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import exceptions.InsufficientFuelException;

import interfaces.FuelConsumable;
import interfaces.PassengerCarrier;
import interfaces.Maintainable;

public class Car extends LandVehicle implements FuelConsumable,PassengerCarrier,Maintainable{

    private double fuelLevel;
    private final int passengerCapacity;
    private int currentPassengers;
    private boolean maintenanceNeeded;

    public Car(String id,String model,double maxSpeed,double currentMileage,int numWheels)throws InvalidOperationException{
        super(id, model, maxSpeed, currentMileage, numWheels);
        this.fuelLevel=0.0;
        this.passengerCapacity=5;
        //passengers will automatically be set to 0 and maintainceNeeded will automatically be false

    }

    @Override
    public void move(double distance)throws InvalidOperationException,InsufficientFuelException{
        if(distance<0){
            throw new InvalidOperationException("Distance can't be less than 0");
        }

        double fuelNeeded=distance/calculateFuelEfficiency();

        if (fuelLevel >= fuelNeeded){
            fuelLevel -= fuelNeeded;
            setCurrentMileage(getCurrentMileage()+distance);
            System.out.println("Driving on road for "+distance+"km");
        } 
        else{
            throw new InsufficientFuelException("Not enough fuel to drive for " +distance +"km");
        }
    }

    @Override
    public double calculateFuelEfficiency(){
        return 15.0;
    }

    @Override
    public void refuel(double amount)throws InvalidOperationException{
        if (amount <= 0){
        throw new InvalidOperationException("Refuel amount must be more than 0");
    }
    fuelLevel+=amount;
    }

    @Override
    public double getFuelLevel(){
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance)throws InsufficientFuelException{
        double fuelNeeded=distance/calculateFuelEfficiency();
        if (fuelLevel >= fuelNeeded){
            fuelLevel -= fuelNeeded;
            return fuelNeeded;
        }

        throw new InsufficientFuelException("Not enough fuel for " + distance + "km");
    }

    @Override
    public void boardPassengers(int count) throws OverloadException, InvalidOperationException{
        if (count<=0){
        throw new InvalidOperationException("Passenger count must be more than 0");
    }
    if (currentPassengers+count > passengerCapacity){
        throw new OverloadException("Passenger overflow:Capacity exceeded");
    }
    currentPassengers += count;
    }
    

    @Override
    public void disembarkPassengers(int count)throws InvalidOperationException{
        if (count <= 0){
        throw new InvalidOperationException("Passenger count must be more than 0");
    }
    if (count>currentPassengers){
        throw new InvalidOperationException("Cannot disembark more passengers than present");
    }
    currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity(){
        return passengerCapacity;
    }

    @Override
    public int getCurrentPassengers(){
        return currentPassengers;
    }

    @Override
    public void scheduleMaintenance(){
        this.maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance(){
        return ((getCurrentMileage()>10000) || maintenanceNeeded);
    }

    @Override
    public void performMaintenance(){
        this.maintenanceNeeded = false;
        System.out.println("Car maintenance has been done.");
    }
}
