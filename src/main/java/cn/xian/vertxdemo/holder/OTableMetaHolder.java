package cn.xian.vertxdemo.holder;

import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.db.tbl.OTableMeta;
import top.onceio.core.mvc.annocations.AutoApi;


@AutoApi(OTableMeta.class)
public class OTableMetaHolder extends DaoHolder<OTableMeta> {

}