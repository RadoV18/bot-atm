package bo.edu.ucb.est;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.Map;
import java.util.HashMap;

public class BotATM extends TelegramLongPollingBot{

    private Banco banco;
    private Map<Long, Sesion> usuarios;

    public BotATM() {
        this.banco = new Banco("Banco de La Fortuna");
        usuarios = new HashMap<Long, Sesion>();
    }

    public void onUpdateReceived(Update update) {
        Long chatID = update.getMessage().getChatId();
        System.out.println("Mensaje de: " + chatID + " " + update.getMessage().getFrom().getFirstName() + " -> " + update.getMessage().getText());
        Sesion usuarioActual = null;
        //Comando para iniciar el bot
        if(update.getMessage().getText().equals("/iniciaratm")) {
            Cliente cl = banco.buscarClientePorChatID(chatID);
            if(cl == null) {
                // Registrar cliente
                usuarioActual = new Sesion(chatID.toString());
                banco.agregarCliente(usuarioActual.getCliente());
            } else {
                // Crear sesion
                usuarioActual = new Sesion(cl);
            }
            usuarios.put(chatID, usuarioActual);
        } else {
            usuarioActual = usuarios.get(chatID);
        }
        if(usuarioActual != null) {
            Respuesta res = null;
            try {
                usuarioActual.registrarMensaje(update.getMessage().getText());
                if(usuarioActual.getEstado() == Estado.menuInicio) {
                    // para el menu de opciones
                    usuarioActual.controlarFlujo();
                    res = usuarioActual.generarRespuesta();
                } else {
                    res = usuarioActual.generarRespuesta();
                    usuarioActual.controlarFlujo();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage() + " excepcion botATM");
                System.out.println(usuarioActual.getEstado());
                res = usuarioActual.generarRespuestaError(e.getMessage());
            }
            
            enviarMensajes(res, chatID);
            usuarioActual.setRespuesta(new Respuesta());
            
            //Eliminar el cliente del mapa de sesiones si elige la opcion salir
            if(usuarioActual.getEstado() == Estado.salir) {
                usuarios.remove(chatID);
            }
        }
    }

    public void enviarMensajes(Respuesta r, Long chatID) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatID.toString());
        for(String text : r.getMensajes()) {
            msg.setText(text);
            try {
                execute(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "Bot ATM RadoV";
    }

    @Override
    public String getBotToken() {
        return "";
    }
    
}
