package cn.xian.vertxdemo;

import io.vertx.core.Launcher;
import top.onceio.core.annotation.BeansIn;
import top.onceio.plugins.vertx.OIOVerticle;

@BeansIn("cn.xian.vertxdemo")
public class Starter extends OIOVerticle {

	public static void main(String[] args) {
		//System.setProperty("vertx.cwd", "E:\\webapp");
    	Launcher.main(new String[]{"run",Starter.class.getName(),"-conf","src/main/java/conf/config.json"});
	}

}
