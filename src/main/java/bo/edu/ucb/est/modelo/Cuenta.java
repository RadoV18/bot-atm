package bo.edu.ucb.est.modelo;


public class Cuenta {
    private String moneda;
    private int nroCuenta;
    private int saldo;

    public Cuenta(String moneda, int nroCuenta) {
        this.moneda = moneda;
        this.nroCuenta = nroCuenta;
        this.saldo = 0;
    }

    public String getMoneda() {
        return this.moneda;
    }

    public int getNroCuenta() {
        return this.nroCuenta;
    }

    public int getSaldo() {
        return this.saldo;
    }

    public void depositar(int monto) throws Exception {
        if(monto <= 0) {
            throw new Exception("Monto inválido.");
        }
        saldo += monto;
    }

    public void retirar(int monto) throws Exception {
        if(monto <= 0) {
            throw new Exception("Monto inválido.");
        }
        if(monto > saldo) {
            throw new Exception("Saldo insuficiente.");
        }
        saldo -= monto;
    }

    @Override
    public String toString() {
        return "Cuenta Nro. " + nroCuenta + " en " + moneda;
    }

    public String mostrarSaldo() {
        return "Cuenta Nro. " + nroCuenta + " en " + moneda + "\n" +
               "Saldo disponible: " + saldo + 
               (moneda.equals("Bolivianos") ? " Bs." : " US$") + "\n";
    }
}
