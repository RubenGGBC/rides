package ejer3;

public class Trailer extends NoMotorVehicle implements Matricula{
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
