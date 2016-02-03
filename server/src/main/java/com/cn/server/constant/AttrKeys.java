package com.cn.server.constant;

import com.cn.server.module.player.dao.entity.Player;
import io.netty.util.AttributeKey;

/**
 * 固定key值
 * @author -琴兽-
 *
 */
public interface AttrKeys {

	/**
	 * 玩家属性key
	 */
	public static AttributeKey<Player> PLAYER_KEY  = AttributeKey.valueOf("PLAYER_KEY");
}
