package websocket.deserializer;

import com.google.gson.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.lang.reflect.Type;

public class Deserializer implements JsonDeserializer<ServerMessage> {
    @Override
    public ServerMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        ServerMessage serverMessage = null;
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String ttype = jsonObject.get("serverMessageType").getAsString();
        if("LOAD_GAME".equals(ttype)){
            serverMessage =  new Gson().fromJson(jsonObject, LoadGameMessage.class);
        }if("NOTIFICATION".equals(ttype)){
            serverMessage =  new Gson().fromJson(jsonObject, NotificationMessage.class);
        }if("ERROR".equals(ttype)){
            serverMessage =  new Gson().fromJson(jsonObject, ErrorMessage.class);
        }
        return serverMessage;

    }
}
