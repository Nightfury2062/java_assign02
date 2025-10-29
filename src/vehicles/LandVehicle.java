package vehicles;
import exceptions.InvalidOperationException;

abstract class LandVehicle extends Vehicle{
    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, double currentMileage, int numWheels)throws InvalidOperationException{
        super(id,model,maxSpeed,currentMileage);
        this.numWheels = numWheels;

    }

    @Override
    double estimateJourneyTime(double distance){

        double base_time = distance / getMaxSpeed();
        return base_time*1.1;
        
    }
    public int getNumWheels(){
        return numWheels;
    }
}
