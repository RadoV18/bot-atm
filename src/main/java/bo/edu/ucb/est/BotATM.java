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

        //Comando para iniciar el bot
        if(update.getMessage().getText().equals("/iniciaratm")) {
            Cliente cl = banco.buscarClientePorChatID(chatID);
            Sesion s = null;
            if(cl == null) {
                // Registrar cliente
                s = new Sesion(chatID.toString());
                banco.agregarCliente(s.getCliente());
            } else {
                // Crear sesion
                s = new Sesion(cl);
            }
            usuarios.put(chatID, s);
        }

        
        System.out.println("Generar respuesta");
        System.out.println(usuarios.get(chatID).toString());
        System.out.println("Estado: " + usuarios.get(chatID).getEstado());
        boolean resultado = usuarios.get(chatID).registrarMensaje(update.getMessage().getText());
        Respuesta res = null;
        if(resultado) {
            System.out.println("true");
            if(usuarios.get(chatID).getEstado() == Estado.menuInicio) {
                // para el menu de opciones
                usuarios.get(chatID).controlarFlujo();
                res = usuarios.get(chatID).generarRespuesta();
            } else {
                res = usuarios.get(chatID).generarRespuesta();
                usuarios.get(chatID).controlarFlujo();
            }
        } else {
            System.out.println("false");
            res = usuarios.get(chatID).generarRespuestaError();
        }
        
        enviarMensajes(res, chatID);
        usuarios.get(chatID).setRespuesta(new Respuesta());
        
        //Eliminar el cliente del mapa de sesiones si elige la opcion salir
        if(usuarios.get(chatID).getEstado() == Estado.salir) {
            usuarios.remove(chatID);
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
        return "1989841365:AAFlHVB2rsQh3rsT1YGnRl5PaFF6QuqV07Q";
    }
    
}
