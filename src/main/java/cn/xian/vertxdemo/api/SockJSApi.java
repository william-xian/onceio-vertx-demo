package cn.xian.vertxdemo.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;
import top.onceio.core.annotation.Def;
import top.onceio.plugins.vertx.VertxSockJSHandler;
import top.onceio.plugins.vertx.annotation.AsSock;

@Def(nameByInterface = true)
@AsSock(prefix="/chat")
public class SockJSApi implements VertxSockJSHandler {

	@Override
	public SockJSHandlerOptions getSockJSHandlerOptions() {
		SockJSHandlerOptions sockJSHandlerOptions = new SockJSHandlerOptions();
		sockJSHandlerOptions.setHeartbeatInterval(2000);
		sockJSHandlerOptions.setInsertJSESSIONID(true);
		return sockJSHandlerOptions;
	}

	Map<String, SockJSSocket> userHandler = new HashMap<String, SockJSSocket>();
	Map<String,Map<String,Object>> userSession = new HashMap<>();

	@Override
	public Handler<SockJSSocket> getSocketHandler() {
		return (h -> {
			h.handler(b -> {
				String cmd = b.getString(0, 2);
				String uid = h.remoteAddress().host()+":" + h.remoteAddress().port();
				if(CMD.LOGIN.equals(cmd)) {
					String msg = new String(b.getBytes()).substring(2);
					Map<String,Object> session = new HashMap<>();
					session.put("user", msg);
					userSession.put(uid, session);
					userHandler.put(msg, h);
					h.write("hello " + msg);
				}else {
					Map<String,Object> session = userSession.get(uid);
					String user = null;
					if(session != null) {
						user = (String)session.get("user");
					}
					if(user == null || user.equals("")) {
						h.write("Please Login");
					}else {
						String msg = new String(b.getBytes()).substring(2);
						if(CMD.LOGOUT.equals(cmd)){
							h.write("Bye");
							userHandler.remove(user);
							h.close();
						}else if(CMD.CHATTO.equals(cmd)){
							session.put("chatto", msg.replaceAll(" ", "").split(","));
							h.write("Chating with:" + msg);
						}else if(CMD.LISTUSER.equals(cmd)){
							h.write(String.join(", ", userHandler.keySet()));
						}else if(CMD.MSG.equals(cmd)){
							String[] chatList = (String[])session.get("chatto");
							for(String u:chatList) {
								SockJSSocket s = userHandler.get(u);
								if(s != null) {
									s.write(user+"  --  " + new Date() +":\n" + msg);
								}
							}
						}
					}
				}
			});
		});
	}

}
