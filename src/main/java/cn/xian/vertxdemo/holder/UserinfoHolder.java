package cn.xian.vertxdemo.holder;

import cn.xian.vertxdemo.model.entity.Userinfo;
import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.mvc.annocations.AutoApi;

@AutoApi(Userinfo.class)
public class UserinfoHolder extends DaoHolder<Userinfo> {

}
