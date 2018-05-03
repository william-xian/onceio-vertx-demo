package cn.xian.vertxdemo.holder;

import cn.xian.vertxdemo.model.entity.UserWallet;
import top.onceio.core.db.dao.DaoHolder;
import top.onceio.core.mvc.annocations.AutoApi;

@AutoApi(UserWallet.class)
public class UserWalletHolder extends DaoHolder<UserWallet> {

}
