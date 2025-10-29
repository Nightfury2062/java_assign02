package vehicles;

import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import exceptions.InsufficientFuelException;

public class Truck extends LandVehicle implements FuelConsumable,CargoCarrier,Maintainable{

    private double fuelLevel;
    private double cargoCapacity;
    private int currentCargo;
    private boolean maintenanceNeeded;

    public Truck(String id,String model,double maxSpeed,double currentMileage,int numWheels)throws InvalidOperationException{
        super(id, model, maxSpeed, currentMileage, numWheels);
        this.fuelLevel=0.0;
        this.cargoCapacity=5000;
        //currentCargo will automatically be set to 0 maintainceNeeded will automatically be false

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
            System.out.println("Hauling cargo for "+distance+"km");
        } 
        else{
            throw new InsufficientFuelException("Not enough fuel to drive for " +distance +"km");
        }
    }

    @Override
    public double calculateFuelEfficiency(){
        double base=8.0;
        if (currentCargo>cargoCapacity*0.5){
            base *= 0.9; // reduce efficiency by 10% when loaded more than 50%
        }
        return base;
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
        this.maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance(){
        return ((getCurrentMileage()>10000) || maintenanceNeeded);
    }

    @Override
    public void performMaintenance(){
        this.maintenanceNeeded = false;
        System.out.println("Truck maintenance has been done.");
    }

    

    
}