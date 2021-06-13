package com.crazymakercircle.im.common.bean;

import lombok.Data;

/**
 * 通知的bean
 *
 * @param <T> 内容的类型
 */

@Data
public class Notification<T>
{
    public static final int SESSION_ON = 10;//上线的通知
    public static final int SESSION_OFF = 20;//下线的通知
    public static final int CONNECT_FINISHED = 30;//节点的链接成功
    private int type;
    private T data;

    public Notification()
    {
    }

    public Notification(T t)
    {
        data = t;
    }

    public static Notification<ContentWrapper> wrapContent(String content)
    {
        ContentWrapper wrapper = new ContentWrapper();
        wrapper.setContent(content);
        return new Notification<ContentWrapper>(wrapper);
    }

    @Data
    public static class ContentWrapper
    {
        String content;
    }

    public String getWrapperContent()
    {
        if (data instanceof ContentWrapper)
        {
            return ((ContentWrapper) data).getContent();
        }
        return null;
    }
}
