package ejer3;

public class Car extends MotorVehicle implements Matricula{
    String matricula;

    @Override
    public void matricular(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public String getMatricula() {
        return this.matricula;
    }
}
