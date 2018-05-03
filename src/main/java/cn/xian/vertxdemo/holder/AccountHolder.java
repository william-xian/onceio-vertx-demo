package cn.xian.vertxdemo.holder;

import cn.xian.vertxdemo.model.entity.Account;
import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.mvc.annocations.AutoApi;

@AutoApi(Account.class)
public class AccountHolder extends DaoHolder<Account> {

}
