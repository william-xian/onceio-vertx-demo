package cn.xian.vertxdemo.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.http.ServerWebSocket;
import top.onceio.core.annotation.Def;
import top.onceio.core.util.OLog;
import top.onceio.plugins.vertx.VertxWebSocketHandler;


@Def(nameByInterface=true)
public class WebSocketApi implements VertxWebSocketHandler {

	Map<String, ServerWebSocket> userHandler = new ConcurrentHashMap<String, ServerWebSocket>();
	Map<String,Map<String,Object>> userSession = new ConcurrentHashMap<>();
	@Override
	public void handle(ServerWebSocket event) {
		
		String uid = event.binaryHandlerID();
		final Map<String,Object> session = userSession.get(uid);
		
		event.textMessageHandler(data -> {
			
			String cmd = data.substring(0, 2);
			
			String msg = data.substring(2);
			if(CMD.LOGIN.equals(cmd)) {
				Map<String,Object> ns = new HashMap<>();
				ns.put("user", msg);
				userSession.put(uid, ns);
				userHandler.put(msg, event);
				event.writeTextMessage("hello " + msg);
			}else {
				String user = null;
				if(session != null) {
					user = (String)session.get("user");
				}
				if(user == null || user.equals("")) {
					event.writeTextMessage("Please Login");
				}else {
					if(CMD.LOGOUT.equals(cmd)){
						event.writeTextMessage("Bye");
						userHandler.remove(user);
						event.close();
					}else if(CMD.CHATTO.equals(cmd)){
						session.put("chatto", msg.replaceAll(" ", "").split(","));
						event.writeTextMessage("Chating with:" + msg);
					}else if(CMD.LISTUSER.equals(cmd)){
						event.writeTextMessage(String.join(", ", userHandler.keySet()));
					}else if(CMD.MSG.equals(cmd)){
						String[] chatList = (String[])session.get("chatto");
						for(String u:chatList) {
							ServerWebSocket s = userHandler.get(u);
							if(s != null) {
								s.writeTextMessage(user+"  --  " + new Date() +":\n" + msg);
							}
						}
					}
				}
			}
		});
		event.closeHandler( h -> {
			OLog.debug("closeHandler,uid=%s",uid);
			if(session != null) {
				String user = (String)session.get("user");
				if(user != null) {
					userHandler.remove(user);
				}
				userSession.remove(uid);
			}
		});
	}
	
}
