package vehicles;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import exceptions.InsufficientFuelException;
import interfaces.CargoCarrier;
import interfaces.Maintainable;
import interfaces.FuelConsumable;

public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {

    private final double cargoCapacity;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double fuelLevel;

    public CargoShip(String id,String model,double maxSpeed,double currentMileage,boolean hasSail)throws InvalidOperationException{
        super(id,model,maxSpeed,currentMileage,hasSail);
        this.cargoCapacity = 50000.0;
        this.currentCargo = 0.0;
        this.maintenanceNeeded = false;
        this.fuelLevel = 0.0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException{
        if (distance<0){
            throw new InvalidOperationException("Distance cannot be less than 0");
        }

        double efficiency = calculateFuelEfficiency();
        if (efficiency==0){                                          // if fuel efficiency is 0, that means cargoship is sailing
            setCurrentMileage(getCurrentMileage() + distance);
            System.out.println("Sailing with cargo for " + distance + " km using sails");
        }
        else{                                                        // using fuel
            double fuelNeeded = distance/efficiency;
            if (fuelLevel >= fuelNeeded){
                fuelLevel -= fuelNeeded;
                setCurrentMileage(getCurrentMileage() + distance);
                System.out.println("Sailing with cargo for " + distance + " km using fuel");
            } 
            else{
                throw new InsufficientFuelException("Not enough fuel to sail " + distance + " km");
            }
        }
    }

    @Override
    public double calculateFuelEfficiency(){
        if (hasSail()){
            return 0.0;
        }
        return 4.0;
    }

    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException{
        if (weight <= 0){
            throw new InvalidOperationException("Cargo weight must be positive");
        }
        if (currentCargo+weight > cargoCapacity){
            throw new OverloadException("Cargo overload: exceeds capacity of " + cargoCapacity + " kg");
        }
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException{
        if (weight<=0){
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
        return getCurrentMileage()>10000 || maintenanceNeeded;
    }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        System.out.println("Cargo ship maintenance done.");
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (hasSail()){
            throw new InvalidOperationException("Ship uses sails, cannot be refueled");
        }
        if (amount <= 0){
            throw new InvalidOperationException("Refuel amount must be more than 0");
        }
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() {
        return hasSail()?0.0 : fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException{
        if (hasSail()){
            return 0.0;
        }
        double needed = distance/calculateFuelEfficiency();
        if (fuelLevel >= needed){
            fuelLevel -= needed;
            return needed;
        }
        throw new InsufficientFuelException("Not enough fuel for " + distance + " km");
    }
}
