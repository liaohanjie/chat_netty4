package com.cn.server.module.player.service;

import io.netty.channel.Channel;
import com.cn.common.module.player.response.PlayerResponse;

/**
 * 玩家服务
 * @author -琴兽-
 *
 */
public interface PlayerService {
	
	
	/**
	 * 登录注册用户
	 * @param playerName
	 * @param passward
	 * @return
	 */
	public PlayerResponse registerAndLogin(Channel channel, String playerName, String passward);
	
	
	/**
	 * 登录
	 * @param playerName
	 * @param passward
	 * @return
	 */
	public PlayerResponse login(Channel channel, String playerName, String passward);

}
