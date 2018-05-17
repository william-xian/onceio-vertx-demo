package cn.xian.vertxdemo;

import io.vertx.core.eventbus.EventBus;
import top.onceio.core.annotation.Def;
import top.onceio.core.annotation.OnCreate;
import top.onceio.core.annotation.Using;

@Def
public class EventBusConsumer {
	@Using
	EventBus eventBus;
	
	@OnCreate
	public void onCreate() {
		eventBus.consumer("chat.msg",msg -> {
			System.out.println("received:" + msg.body());
		});
	}
}
