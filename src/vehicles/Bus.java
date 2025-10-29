package vehicles;

import interfaces.FuelConsumable;
import interfaces.PassengerCarrier;
import interfaces.CargoCarrier;
import interfaces.Maintainable;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import exceptions.InsufficientFuelException;

public class Bus extends LandVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable{

    private double fuelLevel;
    private final int passengerCapacity;
    private int currentPassengers;
    private final double cargoCapacity;
    private double currentCargo;
    private boolean maintenanceNeeded;

    public Bus(String id, String model, double maxSpeed, double currentMileage, int numWheels)throws InvalidOperationException{
        super(id, model, maxSpeed, currentMileage, numWheels) ;
        this.fuelLevel = 0.0;
        this.passengerCapacity = 50;
        this.cargoCapacity = 500.0;
        this.currentCargo = 0.0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance<0) {
            throw new InvalidOperationException("Distance cannot be less than 0");
        }

        double fuelNeeded = distance/calculateFuelEfficiency();

        if (fuelLevel >= fuelNeeded){
            fuelLevel -= fuelNeeded;
            setCurrentMileage(getCurrentMileage()+distance);
            System.out.println("Transporting passengers and cargo for " + distance + " km...");
        } 
        else{
            throw new InsufficientFuelException("Not enough fuel for bus to travel " + distance + " km");
        }
    }


    @Override
    public double calculateFuelEfficiency(){
        return 10.0;
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
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException{
        if (weight <= 0){
            throw new InvalidOperationException("Cargo weight must be more than 0");
        }
        if (currentCargo+weight > cargoCapacity){
            throw new OverloadException("Cargo overload: exceeds capacity of " + cargoCapacity + " kg");
        }
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException{
        if (weight <= 0){
            throw new InvalidOperationException("Unload weight must be positive");
        }
        if (weight>currentCargo){
            throw new InvalidOperationException("Cannot unload more cargo than currently loaded (" + currentCargo + " kg)");
        }
        currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity(){
        return cargoCapacity;
    }

    @Override
    public double getCurrentCargo(){
        return currentCargo;
    }
    
    @Override
    public void scheduleMaintenance(){
        maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance(){
        return ((getCurrentMileage()>10000) || maintenanceNeeded);
    }

    @Override
    public void performMaintenance(){
        maintenanceNeeded = false;
        System.out.println("Bus maintenance done.");
    }
}
