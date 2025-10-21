package ejer2;

public class BillCalculator {
    
    private DeductionCalculator deductionCalculator;
    private VATCalculator vatCalculator;
    
    public BillCalculator() {
        this.deductionCalculator = new DeductionCalculator();
        this.vatCalculator = new VATCalculator();
    }
    
    public void calculateTotals(Bill bill) {
        // Calcular deducción usando la clase especializada
        bill.totalDeduction = deductionCalculator.calculate(bill.InitialAmount, bill.deductionPercentage);
        
        // Calcular IVA usando la clase especializada
        bill.totalVAT = vatCalculator.calculate(bill.InitialAmount);
        
        // Calcular total final
        bill.billTotal = (bill.InitialAmount - bill.totalDeduction) + bill.totalVAT;
    }
}