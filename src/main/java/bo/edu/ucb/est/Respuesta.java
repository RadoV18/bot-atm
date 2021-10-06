package bo.edu.ucb.est;

import java.util.ArrayList;

public class Respuesta {
    private ArrayList<String> mensajes;

    public Respuesta() {
        mensajes = new ArrayList<String>();
    }

    public void agregarMensajes(String... texto) {
        System.out.println("agregarMensajes");
        for(String t : texto) {
            mensajes.add(t);
        }
    }

    public ArrayList<String> getMensajes() {
        return this.mensajes;
    }

    public void setMensajes(ArrayList<String> mensajes) {
        this.mensajes = mensajes;
    }

    public void generarMensajes(Estado estado, Accion accion, ValorAIngresar valor, Cliente cl) {
        System.out.println("Generar para " + estado + " " + accion + " " + valor);
        switch(estado) {
            case inicioBot:
                if(accion == Accion.registrar) {
                    agregarMensajes("Bienvenido al Banco de la Fortuna.",
                                 "He notado que aún no eres cliente, procedamos a registrarte.",
                                 "¿Cuál es tu nombre completo?");
                } else if(accion == Accion.iniciarSesion) {
                    agregarMensajes("Hola de nuevo " + cl.getNombre(),
                    "Solo por seguridad ¿cuál es tu PIN?");
                }
                break;

            case inicioSesion:
                agregarMensajes("asdf");
                break;
            
            case registro:
                if(valor == ValorAIngresar.nombre) {
                    agregarMensajes("Por favor elige un PIN de seguridad, este te "+
                    "será requerido cada que ingreses al sistema.");
                } else if(valor == ValorAIngresar.pinNuevo) {
                    agregarMensajes("Te hemos registrado correctamente.");
                    generarMensajes(Estado.inicioBot, Accion.iniciarSesion, valor, cl);
                }
                break;
            
            case menuInicio:
                agregarMensajes("Elige una opción:\n\n" +
                                "1. Ver Saldo\n" +
                                "2. Retirar dinero\n" +
                                "3. Depositar dinero\n" +
                                "4. Crear cuenta\n" +
                                "5. Salir");
                break;

            case regresarMenu:
                if(accion == Accion.crearCuenta) {
                    Cuenta cu = cl.getCuentaNueva();
                    agregarMensajes("Se ha creado una cuenta en " + cu.getMoneda() + " con saldo cero, " + 
                                    "cuyo número es: " + cu.getNroCuenta());   
                } else if (accion == Accion.verSaldo) {
                    Cuenta cu = cl.getCuentaActiva();
                    agregarMensajes(cu.mostrarSaldo());
                }
                if(valor != ValorAIngresar.montoRetiro && valor != ValorAIngresar.montoDeposito) {
                    generarMensajes(Estado.menuInicio, accion, valor, cl);
                }
                break;
            
            case salir:
                agregarMensajes("Vuelva pronto.");
                break;
            
            case seleccionCuenta:
                agregarMensajes("Seleccione una cuenta:\n" + cl.getListaCuentas());
                break;

            case seleccionTipoCuenta:
                agregarMensajes("Seleccione la moneda:\n" +
                                "1. Bolivianos\n" + 
                                "2. Dólares");
                break;
            
            case retiro:
                if(valor == ValorAIngresar.montoRetiro) {
                    agregarMensajes("Retiro realizado correctamente.");
                    generarMensajes(Estado.menuInicio, accion, valor, cl);
                } else {
                    generarMensajes(Estado.regresarMenu, Accion.verSaldo, ValorAIngresar.montoDeposito, cl);
                    agregarMensajes("Ingrese el monto a retirar:");
                }
                break;    

            case deposito:
                if(valor == ValorAIngresar.montoDeposito) {
                    agregarMensajes("Depósito realizado correctamente.");
                    generarMensajes(Estado.menuInicio, accion, valor, cl);
                } else {
                    generarMensajes(Estado.regresarMenu, Accion.verSaldo, ValorAIngresar.montoRetiro, cl);
                    agregarMensajes("Ingrese el monto a depositar:");
                }
                break;
            
            default:
                break;

        }
    }

    public void generarMensajesError(Estado estado, Accion accion, ValorAIngresar valor, Cliente cl) {
        if(valor == ValorAIngresar.pinNuevo) {
            agregarMensajes("Ingrese un PIN válido.");
            generarMensajes(Estado.registro, accion, ValorAIngresar.nombre, cl);
        } else if(valor == ValorAIngresar.pin) {
            agregarMensajes("PIN inválido/incorrecto.");
            generarMensajes(Estado.inicioBot, accion, valor, cl);
        } else if(estado == Estado.menuInicio) {
            agregarMensajes("Usted no tiene cuentas. Cree una primero.");
            generarMensajes(Estado.menuInicio, Accion.ingresarOpcion, ValorAIngresar.opcion, cl);
        }
    }
}
