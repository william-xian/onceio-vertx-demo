package cn.xian.vertxdemo.holder;

import cn.xian.vertxdemo.model.entity.Neure;
import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.mvc.annocations.AutoApi;

@AutoApi(Neure.class)
public class NeureHolder extends DaoHolder<Neure> {

}
