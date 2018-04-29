package cn.xian.app;

import io.vertx.core.Launcher;
import top.onceio.core.annotation.BeansIn;
import top.onceio.plugins.vertx.OIOVerticle;

@BeansIn("cn.xian.app")
public class Starter extends OIOVerticle {

	public static void main(String[] args) {
		
    	Launcher.main(new String[]{"run",Starter.class.getName(),"-conf","src/main/java/conf/config.json"});
	}

}
