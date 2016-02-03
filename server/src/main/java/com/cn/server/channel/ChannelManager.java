package com.cn.server.channel;

import io.netty.channel.Channel;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.cn.common.core.model.Response;
import com.cn.common.core.serial.Serializer;
/**
 * 会话管理者
 * @author -琴兽-
 *
 */
public class ChannelManager {

	/**
	 * 在线会话
	 */
	private static final ConcurrentHashMap<Long, Channel> onlineChannels = new ConcurrentHashMap<>();
	
	
	/**
	 * 加入
	 * @param playerId
	 * @param channel
	 * @return
	 */
	public static boolean putChannel(long playerId, Channel channel){
		if(!onlineChannels.containsKey(playerId)){
			boolean success = onlineChannels.putIfAbsent(playerId, channel)== null? true : false;
			return success;
		}
		return false;
	}
	
	/**
	 * 移除
	 * @param playerId
	 */
	public static Channel removeChannel(long playerId){
		return onlineChannels.remove(playerId);
	}
	
	/**
	 * 发送消息
	 * @param <T>
	 * @param playerId
	 * @param message
	 */
	public static <T extends Serializer> void sendMessage(long playerId, short module, short cmd, T message){
		Channel channel = onlineChannels.get(playerId);
		if (channel != null && channel.isActive()) {
			Response response = new Response(module, cmd, message.getBytes());
			channel.writeAndFlush(response);
		}
	}
	
	/**
	 * 是否在线
	 * @param playerId
	 * @return
	 */
	public static boolean isOnlinePlayer(long playerId){
		return onlineChannels.containsKey(playerId);
	}
	
	/**
	 * 获取所有在线玩家
	 * @return
	 */
	public static Set<Long> getOnlinePlayers() {
		return Collections.unmodifiableSet(onlineChannels.keySet());
	}
}
