package com.crazymakercircle.imServer.serverProcesser;

import com.crazymakercircle.im.common.bean.msg.ProtoMsg;
import com.crazymakercircle.imServer.server.session.LocalSession;
import com.crazymakercircle.imServer.server.session.ServerSession;
import com.crazymakercircle.imServer.server.session.service.SessionManger;
import com.crazymakercircle.util.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("ChatRedirectProcesser")
public class ChatRedirectProcesser extends AbstractServerProcesser {

    public static final int RE_DIRECT = 1;

    @Override
    public ProtoMsg.HeadType op() {
        return ProtoMsg.HeadType.MESSAGE_REQUEST;
    }

    @Override
    public Boolean action(LocalSession fromSession, ProtoMsg.Message proto) {
        // 聊天处理
        ProtoMsg.MessageRequest messageRequest = proto.getMessageRequest();
        Logger.tcfo("chatMsg | from="
                + messageRequest.getFrom()
                + " , to =" + messageRequest.getTo()
                + " , MsgType =" + messageRequest.getMsgType()
                + " , content =" + messageRequest.getContent());

        // 获取接收方的chatID
        String to = messageRequest.getTo();
        // int platform = messageRequest.getPlatform();
        List<ServerSession> toSessions = SessionManger.inst().getSessionsBy(to);
        if (toSessions == null) {
            //接收方离线
            Logger.tcfo("[" + to + "] 不在线，需要保存为离线消息，请保存到nosql如mongo中!");
        } else {

            toSessions.forEach((session) ->
            {
                // 将IM消息发送到接收客户端；
                // 如果是remotesession，则转发到对应的服务节点
                session.writeAndFlush(proto);

            });
        }
        return null;
    }

}
