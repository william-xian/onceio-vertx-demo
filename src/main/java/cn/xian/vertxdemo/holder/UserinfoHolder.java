package cn.xian.vertxdemo.holder;

import cn.xian.vertxdemo.model.entity.Userinfo;
import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.db.dao.Page;
import top.onceio.core.db.dao.tpl.Cnd;
import top.onceio.core.mvc.annocations.Api;
import top.onceio.core.mvc.annocations.AutoApi;
import top.onceio.core.mvc.annocations.Param;

@AutoApi(Userinfo.class)
public class UserinfoHolder extends DaoHolder<Userinfo> {

	@Api
	public Userinfo fetchByNickname(@Param("nickname")String nickname) {
		Cnd<Userinfo> cnd =  new Cnd<>(Userinfo.class);
		cnd.eq().setNickname(nickname);
		Userinfo  ui = super.fetch(null, cnd);
		return ui;
	}
	
	@Api
	public Page<Userinfo> findByUserinfo(@Param Userinfo ui) {
		Cnd<Userinfo> cnd =  new Cnd<>(Userinfo.class);
		cnd.eq().setNickname(ui.getNickname());
		Page<Userinfo>  uis = super.find(cnd);
		return uis;
	}

}
