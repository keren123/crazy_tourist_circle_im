package com.crazymakercircle.imServer.serverHandler;

import com.crazymakercircle.constants.ServerConstants;
import com.crazymakercircle.entity.ImNode;
import com.crazymakercircle.im.common.bean.Notification;
import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.imServer.server.session.LocalSession;
import com.crazymakercircle.imServer.server.session.service.SessionManger;
import com.crazymakercircle.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("RemoteNotificationHandler")
@ChannelHandler.Sharable
public class RemoteNotificationHandler
        extends ChannelInboundHandlerAdapter
{


    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception
    {
        if (null == msg || !(msg instanceof ProtoMsg.Message))
        {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;

        //取得请求类型,如果不是通知消息，直接跳过
        ProtoMsg.HeadType headType = pkg.getType();

        if (!headType.equals(ProtoMsg.HeadType.MESSAGE_NOTIFICATION))
        {
            super.channelRead(ctx, msg);
            return;
        }

        //处理消息的内容
        ProtoMsg.MessageNotification notificationPkg = pkg.getNotification();
        String json = notificationPkg.getJson();

        log.info("收到通知, json={}", json);
        Notification<Notification.ContentWrapper> notification =
                JsonUtil.jsonToPojo(json, new TypeToken<Notification<Notification.ContentWrapper>>()
                {
                }.getType());

         //下线的通知
        if (notification.getType() == Notification.SESSION_OFF)
        {
            String sid = notification.getWrapperContent();
            log.info("收到用户下线通知, sid={}", sid);
            SessionManger.inst().removeRemoteSession(sid);
        }
        //上线的通知
        if (notification.getType() == Notification.SESSION_ON)
        {
            String sid = notification.getWrapperContent();
            log.info("收到用户上线通知, sid={}", sid);

            //待开发
//            SessionManger.inst().addRemoteSession(remoteSession);
        }


        //节点的链接成功
        if (notification.getType() == Notification.CONNECT_FINISHED)
        {

            Notification<ImNode> nodInfo =
                    JsonUtil.jsonToPojo(json, new TypeToken<Notification<ImNode>>()
                    {
                    }.getType());


            log.info("收到分布式节点连接成功通知, node={}", json);

            //ctx.pipeline().remove("loginRequest");
            ctx.pipeline().remove("login");
            ctx.channel().attr(ServerConstants.CHANNEL_NAME).set(JsonUtil.pojoToJson(nodInfo));
        }




    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception
    {
        LocalSession session = LocalSession.getSession(ctx);

        if (null != session)
        {

            session.unbind();

        }
    }
}
