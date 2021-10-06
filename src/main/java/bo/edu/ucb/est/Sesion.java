package bo.edu.ucb.est;

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

    public Respuesta generarRespuestaError() {
        respuesta.generarMensajesError(estado, accion, valor, cliente);
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

    public boolean registrarMensaje(String msg) {
        mensajeRecibido = msg;
        boolean resultado = true;
        if(estado == Estado.registro) {
            if(valor == ValorAIngresar.nombre) {
                System.out.println("Set nombre " + mensajeRecibido);
                cliente.setNombre(mensajeRecibido);
            } else {
                System.out.println("Set pin " + mensajeRecibido);
                try {
                    cliente.setPinSeguridad(mensajeRecibido);
                } catch (Exception e) {
                    resultado = false;
                }
            }
        } else if(estado == Estado.inicioBot) {
            try {
                if(accion == Accion.iniciarSesion && !mensajeRecibido.equals("/iniciaratm")) {
                    if(!cliente.validarIngreso(mensajeRecibido)) {
                        resultado = false;
                        System.out.println(mensajeRecibido + "!=" + cliente.getPinSeguridad());
                    } else {
                        respuesta.agregarMensajes("Bienvenido.");
                        setMenuInicio();
                    }
                }
            } catch (Exception e) {
                resultado = false;
            }
        } else if(estado == Estado.menuInicio) {
            int opc = -1;
            try {
                opc = Integer.parseInt(mensajeRecibido);
            } catch (Exception e) {
                resultado = false;
            }
            if(opc <= 0 || opc > 5) {
                resultado = false;
                System.out.println("Opcion invalida");
            }
            switch(opc) {
                case 1:
                case 2:
                case 3:
                    if(cliente.getCuentas().isEmpty()) {
                        System.out.println("sin cuentas");
                        resultado = false;
                    }
                    break;
                
                case 5:
                    estado = Estado.salir;
                    accion = Accion.salir;
                    valor = null;
                    break;   
            }
        } else if(estado == Estado.seleccionTipoCuenta) {
            int opc = -1;
            try {
                opc = Integer.parseInt(mensajeRecibido);
                if(opc < 1 || opc > 2) {
                    throw new Exception();
                }
                if(opc == 1) {
                    cliente.agregarCuenta(new Cuenta("Bolivianos", nroCuentaDisponible));
                } else {
                    cliente.agregarCuenta(new Cuenta("Dólares", nroCuentaDisponible));
                }
                System.out.println("Cuenta agregada " + nroCuentaDisponible);
                estado = Estado.regresarMenu;
                nroCuentaDisponible++;
            } catch (Exception e) {
                resultado = false;
            }
        } else if(estado == Estado.seleccionCuenta) {
            try {
                int opcion = Integer.parseInt(mensajeRecibido);
                cuentaActual = cliente.getCuentaSeleccionada(opcion);
                cliente.setCuentaActiva(cuentaActual);
                if(accion == Accion.verSaldo) {
                    estado = Estado.regresarMenu;
                } else if(accion == Accion.retirar) {
                    estado = Estado.retiro;
                } else if(accion == Accion.depositar) {
                    estado = Estado.deposito;
                }
                System.out.println("Cuenta seleccionada: " + cuentaActual);
            } catch (Exception e) {
                cuentaActual = null;
                resultado = false;
            }
        } else if(estado == Estado.retiro && valor == ValorAIngresar.montoRetiro) {
            try {
                int monto = Integer.parseInt(mensajeRecibido);
                cliente.getCuentaActiva().retirar(monto);
            } catch (Exception e) {
                resultado = false;    
            }
        } else if(estado == Estado.deposito && valor == ValorAIngresar.montoDeposito) {
            try {
                int monto = Integer.parseInt(mensajeRecibido);
                cliente.getCuentaActiva().depositar(monto);
            } catch (Exception e) {
                resultado = false;
            }
        }
        return resultado;
    }

    public void controlarFlujo() {
        System.out.println("******************");
        System.out.println("Estado inicial: " + estado);
        System.out.println("Accion inicial: " + accion);
        System.out.println("Valor inicial: " + valor);
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
        System.out.println("|||||||||||||||||||||");
        System.out.println("Estado final: " + estado);
        System.out.println("Accion final: " + accion);
        System.out.println("Valor final: " + valor);
        System.out.println("******************");
    }
}
