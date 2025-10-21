package ejer3;

public class MotorVehicle extends TransportationDevice{
    Engine engine;
    int numberOfWheels;

    public void startEngine(){
        engine.state = Engine.State.ON;
    }

}
