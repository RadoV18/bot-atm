package bo.edu.ucb.est.modelo;

import java.util.Map;
import java.util.HashMap;

public class Banco {
    private String nombre;
    private Map<Long, Cliente> clientes;

    public Banco(String nombre) {
        this.nombre = nombre;
        this.clientes = new HashMap<Long, Cliente>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void agregarCliente(Cliente cl) {
        clientes.put(Long.valueOf(cl.getCodigoCliente()), cl);
    }

    public Cliente buscarClientePorChatID(Long chatID) {
        return clientes.get(chatID);
    }
}
