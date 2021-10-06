package bo.edu.ucb.est;

import bo.edu.ucb.est.modelo.Cliente;
import bo.edu.ucb.est.modelo.Cuenta;

enum Estado {inicioBot, registro, inicioSesion, regresarMenu, menuInicio, seleccionCuenta,
             deposito, retiro, seleccionTipoCuenta, salir}
enum Accion {registrar, iniciarSesion, ingresarOpcion, verSaldo, retirar, depositar, crearCuenta, salir}
enum ValorAIngresar {nombre, pinNuevo, pin, opcion, opcionCuenta, montoDeposito, montoRetiro}

public class Sesion {

    private static int nroCuentaDisponible = 1000;

    private Cliente cliente;
    private Estado estado;
    private Accion accion;
    private ValorAIngresar valor;
    private String mensajeRecibido;
    private Respuesta respuesta;

    private Cuenta cuentaActual;

    // Cuando se registra un cliente nuevo
    public Sesion(String codigoCliente) {
        cliente = new Cliente(codigoCliente);
        estado = Estado.inicioBot;
        accion = Accion.registrar;
        valor = ValorAIngresar.nombre;
        mensajeRecibido = null;
        respuesta = new Respuesta();
    }

    // Cuando se crea la sesion de un cliente existente
    public Sesion(Cliente cl) {
        this.cliente = cl;
        estado = Estado.inicioBot;
        accion = Accion.iniciarSesion;
        valor = ValorAIngresar.pin;
        mensajeRecibido = null;
        respuesta = new Respuesta();
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public Estado getEstado() {
        return this.estado;
    }

    public Respuesta generarRespuesta() {
        respuesta.generarMensajes(estado, accion, valor, cliente);
        return respuesta;
    }

    public Respuesta generarRespuestaError(String mensajeError) {
        respuesta.generarMensajesError(mensajeError, estado, accion, valor, cliente);
        return respuesta;
    }

    public void setEstado(Estado e) {
        this.estado = e;
    }

    public void setRespuesta(Respuesta res) {
        this.respuesta = res;
    }

    public void setMenuInicio() {
        estado = Estado.menuInicio;
        accion = Accion.ingresarOpcion;
        valor = ValorAIngresar.opcion;
    }

    public void registrarMensaje(String msg) throws Exception {
        mensajeRecibido = msg;
        switch(estado) {
            case registro:
                if(valor == ValorAIngresar.nombre) {
                    System.out.println("Set nombre " + mensajeRecibido);
                    cliente.setNombre(mensajeRecibido);
                } else {
                    System.out.println("Set pin " + mensajeRecibido);
                    cliente.setPinSeguridad(mensajeRecibido);
                }
                break;
            
            case inicioBot:
                if(accion == Accion.iniciarSesion && !mensajeRecibido.equals("/iniciaratm")) {
                    if(!cliente.validarIngreso(mensajeRecibido)) {
                        System.out.println(mensajeRecibido + "!=" + cliente.getPinSeguridad());
                        throw new Exception("PIN incorrecto.");
                    } else {
                        respuesta.agregarMensajes("Bienvenido.");
                        setMenuInicio();
                    }
                }
                break;
            
            case menuInicio:
                int opcionMenu = -1;
                try {
                    opcionMenu = Integer.parseInt(mensajeRecibido);
                    if(opcionMenu <= 0 || opcionMenu > 5) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    throw new Exception("Opción inválida.");
                }
                switch(opcionMenu) {
                    case 1:
                    case 2:
                    case 3:
                        if(cliente.getCuentas().isEmpty()) {
                            throw new Exception("Usted no tiene cuentas. Cree una primero.");
                        }
                        break;
                    
                    case 5:
                        estado = Estado.salir;
                        accion = Accion.salir;
                        valor = null;
                        break;   
                }
                break;

            case seleccionTipoCuenta:
                int opcionCuenta = -1;
                try {
                    opcionCuenta = Integer.parseInt(mensajeRecibido);
                    if(opcionCuenta < 1 || opcionCuenta > 2) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    throw new Exception("Opción inválida.");
                }
                if(opcionCuenta == 1) {
                    cliente.agregarCuenta(new Cuenta("Bolivianos", nroCuentaDisponible));
                } else {
                    cliente.agregarCuenta(new Cuenta("Dólares", nroCuentaDisponible));
                }
                System.out.println("Cuenta agregada " + nroCuentaDisponible);
                estado = Estado.regresarMenu;
                nroCuentaDisponible++;
                break;
            
            case seleccionCuenta:
                int opcion;
                try {
                    opcion = Integer.parseInt(mensajeRecibido);
                } catch (Exception e) {
                    throw new Exception("Opción inválida.");
                }
                cuentaActual = cliente.getCuentaSeleccionada(opcion);
                cliente.setCuentaActiva(cuentaActual);
                if(accion == Accion.verSaldo) {
                    estado = Estado.regresarMenu;
                } else if(accion == Accion.retirar) {
                    if(cuentaActual.getSaldo() == 0) {
                        estado = Estado.menuInicio;
                        valor = ValorAIngresar.opcion;
                        throw new Exception("Error. El saldo de la cuenta seleccionada es 0.");
                    }
                    estado = Estado.retiro;
                } else if(accion == Accion.depositar) {
                    estado = Estado.deposito;
                }
                System.out.println("Cuenta seleccionada: " + cuentaActual);
                break;

            case retiro:
                if(valor == ValorAIngresar.montoRetiro) {
                    int monto;
                    try {
                        monto = Integer.parseInt(mensajeRecibido);
                    } catch (Exception e) {
                        throw new Exception("Monto inválido.");
                    }
                    cliente.getCuentaActiva().retirar(monto);
                }
                break;
            
            case deposito:
                if(valor == ValorAIngresar.montoDeposito) {
                    int monto;
                    try {
                        monto = Integer.parseInt(mensajeRecibido);
                    } catch (Exception e) {
                        throw new Exception("Monto inválido.");
                    }
                    cliente.getCuentaActiva().depositar(monto);
                }
                break;

        }
    }

    public void controlarFlujo() {
        System.out.println("******************");
        System.out.print(estado + " " + accion + " " + valor + " -> ");
        switch(estado) {
            // /iniciaratm
            case inicioBot:
                //Verificar si el cliente esta registrado o no
                if(cliente.getNombre().equals("")) {
                    // Cliente nuevo
                    estado = Estado.registro;
                    accion = Accion.registrar;
                    valor = ValorAIngresar.nombre;
                } else {
                    // Cliente registrado
                    estado = Estado.inicioBot;
                    accion = Accion.iniciarSesion;
                    valor = ValorAIngresar.pin;
                }
                break;
            
            case registro:
                if(valor == ValorAIngresar.nombre) {
                    valor = ValorAIngresar.pinNuevo;
                } else if(valor == ValorAIngresar.pinNuevo) {
                    estado = Estado.inicioBot;
                    accion = Accion.iniciarSesion;
                    valor = ValorAIngresar.pin;
                }
                break;

            case inicioSesion:
                setMenuInicio();
                break;
            
            case menuInicio:
                //menu de opciones
                switch(Integer.valueOf(mensajeRecibido)) {
                    case 1: // ver saldo
                        estado = Estado.seleccionCuenta;
                        accion = Accion.verSaldo;
                        valor = ValorAIngresar.opcionCuenta;
                        break;
                    
                    case 2: // retirar
                        estado = Estado.seleccionCuenta;
                        accion = Accion.retirar;
                        valor = ValorAIngresar.opcionCuenta;
                        break;
                    
                    case 3: // depositar
                        estado = Estado.seleccionCuenta;
                        accion = Accion.depositar;
                        valor = ValorAIngresar.opcionCuenta;
                        break;
                    
                    case 4: // crear cuenta
                        estado = Estado.seleccionTipoCuenta;
                        accion = Accion.crearCuenta;
                        valor = ValorAIngresar.opcion;
                        break;
                    
                    case 5: // salir
                        estado = Estado.salir;
                        accion = Accion.salir;
                        valor = null;
                        break;
                }
                break;

            case seleccionCuenta:
                if(accion == Accion.verSaldo) {
                    estado = Estado.regresarMenu;
                    valor = ValorAIngresar.opcion;
                }
                break;
            

            case deposito:
                if(valor == ValorAIngresar.montoDeposito) {
                    setMenuInicio();
                } else {
                    valor = ValorAIngresar.montoDeposito;
                }
                break;

            case retiro:
                if(valor == ValorAIngresar.montoRetiro) {
                    setMenuInicio();
                } else {
                    valor = ValorAIngresar.montoRetiro;
                }
                break;

            case seleccionTipoCuenta:
                estado = Estado.regresarMenu;
                break;

            case regresarMenu:
                setMenuInicio();
                break;
            
            case salir:
                estado = Estado.salir;
                accion = Accion.salir;
                valor = null;
                break;

        }
        System.out.println(estado + " " + accion + " " + valor);
        System.out.println("******************\n");
    }
}
