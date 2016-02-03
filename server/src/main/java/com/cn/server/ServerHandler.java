package com.cn.server;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.cn.common.core.model.Request;
import com.cn.common.core.model.Response;
import com.cn.common.core.model.Result;
import com.cn.common.core.model.ResultCode;
import com.cn.common.core.serial.Serializer;
import com.cn.common.module.ModuleId;
import com.cn.server.channel.ChannelManager;
import com.cn.server.constant.AttrKeys;
import com.cn.server.module.player.dao.entity.Player;
import com.cn.server.scanner.Invoker;
import com.cn.server.scanner.InvokerHoler;
/**
 * 消息接受处理类
 * @author -琴兽-
 *
 */
public class ServerHandler extends SimpleChannelInboundHandler<Request> {
	
	/**
	 * 接收消息
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {

		handlerMessage(ctx, request);
	}
	
	
	/**
	 * 消息处理
	 * @param channelId
	 * @param request
	 */
	private void handlerMessage(ChannelHandlerContext ctx, Request request){
		
		Response response = new Response(request);
		
		System.out.println("module:"+request.getModule() + "   " + "cmd：" + request.getCmd());
		
		//获取命令执行器
		Invoker invoker = InvokerHoler.getInvoker(request.getModule(), request.getCmd());
		if(invoker != null){
			try {
				Result<?> result = null;
				//假如是玩家模块传入channel参数，否则传入playerId参数
				if(request.getModule() == ModuleId.PLAYER){
					result = (Result<?>)invoker.invoke(ctx.channel(), request.getData());
				}else{
					Object attachment = ctx.channel().attr(AttrKeys.PLAYER_KEY).get();
					if(attachment != null){
						Player player = (Player) attachment;
						result = (Result<?>)invoker.invoke(player.getPlayerId(), request.getData());
					}else{
						//会话未登录拒绝请求
						response.setStateCode(ResultCode.LOGIN_PLEASE);
						ctx.channel().writeAndFlush(response);
						return;
					}
				}
				
				//判断请求是否成功
				if(result.getResultCode() == ResultCode.SUCCESS){
					//回写数据
					Object object = result.getContent();
					if(object != null){
						Serializer content = (Serializer)object;
						response.setData(content.getBytes());
					}
					ctx.channel().writeAndFlush(response);
				}else{
					//返回错误码
					response.setStateCode(result.getResultCode());
					ctx.channel().writeAndFlush(response);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				//系统未知异常
				response.setStateCode(ResultCode.UNKOWN_EXCEPTION);
				ctx.channel().writeAndFlush(response);
			}
		}else{
			//未找到执行者
			response.setStateCode(ResultCode.NO_INVOKER);
			ctx.channel().writeAndFlush(response);
			return;
		}
	}
	
	/**
	 * 断线移除会话
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Object object = ctx.channel().attr(AttrKeys.PLAYER_KEY).get();
		if(object != null){
			Player player = (Player)object;
			ChannelManager.removeChannel(player.getPlayerId());
		}
	}
	
	
}
