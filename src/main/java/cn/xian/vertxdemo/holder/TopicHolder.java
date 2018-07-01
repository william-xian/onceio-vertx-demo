package cn.xian.vertxdemo.holder;

import cn.xian.vertxdemo.model.entity.Topic;
import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.mvc.annocations.AutoApi;

@AutoApi(Topic.class)
public class TopicHolder extends DaoHolder<Topic> {

}
