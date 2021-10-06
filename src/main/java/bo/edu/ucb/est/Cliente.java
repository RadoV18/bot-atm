package bo.edu.ucb.est;

import java.util.HashMap;
import java.util.ArrayList;


public class Cliente {
    private String nombre;
    private String codigoCliente;
    private String pinSeguridad;
    private HashMap<Integer, Cuenta> cuentas;
    private Cuenta cuentaActiva;

    public Cliente(String codigoCliente) {
        nombre = "";
        this.codigoCliente = codigoCliente;
        pinSeguridad = "";
        cuentas = new HashMap<Integer,Cuenta>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getCodigoCliente() {
        return this.codigoCliente;
    }

    public String getPinSeguridad() {
        return this.pinSeguridad;
    }

    public Cuenta getCuentaActiva() {
        return this.cuentaActiva;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public void setPinSeguridad(String pinSeguridad) throws Exception{
        validarPin(pinSeguridad);
        this.pinSeguridad = pinSeguridad;
    }

    public void setCuentas(HashMap<Integer, Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public void setCuentaActiva(Cuenta cuenta) {
        this.cuentaActiva = cuenta;
    }

    public void validarPin(String pinSeguridad) throws Exception{
        // Verificar que el pin solo tenga 4 digitos
        if(pinSeguridad.length() != 4) {
            throw new Exception();
        }
        // Verificar que el pin solo tenga numeros
        Integer.parseInt(pinSeguridad);
    }

    public boolean validarIngreso(String pinSeguridad) throws Exception{
        validarPin(pinSeguridad);
        if(this.pinSeguridad.equals(pinSeguridad)) {
            return true;
        }
        return false;
    }

    public void agregarCuenta(Cuenta cuenta) {
        cuentas.put(cuenta.getNroCuenta(), cuenta);
    }

    public HashMap<Integer, Cuenta> getCuentas() {
        return cuentas;
    }

    public Cuenta getCuentaNueva() {
        ArrayList<Integer> numerosCuenta = new ArrayList<Integer>(cuentas.keySet());
        return cuentas.get((numerosCuenta.get(numerosCuenta.size() - 1)));
    }

    public boolean datosRegistrados() {
        return (nombre != null && codigoCliente != null &&
            pinSeguridad != null && cuentas != null) ? true : false;
    }

    public String getListaCuentas() {
        String lista = "";
        int i = 1;
        for(int key : cuentas.keySet()) {
            lista += (i + ". " + cuentas.get(key).toString() + "\n");
            i++;
        }
        return lista;
    }

    public Cuenta getCuentaSeleccionada(int pos) throws Exception{
        ArrayList<Integer> numerosCuenta = new ArrayList<Integer>(cuentas.keySet());
        return cuentas.get((numerosCuenta.get(pos - 1)));
    }

}
