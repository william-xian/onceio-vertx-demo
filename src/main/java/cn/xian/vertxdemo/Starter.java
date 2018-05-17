package cn.xian.vertxdemo;

import io.vertx.core.Launcher;
import top.onceio.core.annotation.BeansIn;
import top.onceio.plugins.vertx.OIOVerticle;

@BeansIn(value="cn.xian.vertxdemo",conf="conf")
public class Starter extends OIOVerticle {

	public static void main(String[] args) {
    	Launcher.main(new String[]{"run",Starter.class.getName()});
	}

}
