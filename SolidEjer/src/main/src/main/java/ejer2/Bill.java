package ejer2;

import java.util.Date;

public class Bill {
    public String code; // Representa un número de 5 dígitos
    public Date date;
    public float InitialAmount;
    public float totalVAT;
    public float totalDeduction;
    public float billTotal;
    public int deductionPercentage;
    
    // Método que usa las clases especializadas para calcular
    public void billTotalCalc() {
        BillCalculator calculator = new BillCalculator();
        calculator.calculateTotals(this);
    }
}