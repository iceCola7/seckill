package me.cxz.seckill.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

@Component
public class SessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.info("---sessionCreated------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        HttpSession session = event.getSession();
        ServletContext application = session.getServletContext();
        // 在 application 范围由一个 HashSet 集保存所有的 session
        HashSet sessions = (HashSet) application.getAttribute("sessions");
        if (sessions == null) {
            sessions = new HashSet();
            application.setAttribute("sessions", sessions);
        }
        // 新创建的 session 均添加到 HashSet 集中
        sessions.add(session);
        // 可以在别处从 application 范围中取出 sessions 集合
        // 然后使用 sessions.size() 获取当前活动的 session 数，即为“在线人数”
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) throws ClassCastException {
        logger.info("---sessionDestroyed----" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        HttpSession session = event.getSession();
        logger.info("deletedSessionId: " + session.getId());

        ServletContext application = session.getServletContext();
        HashSet sessions = (HashSet) application.getAttribute("sessions");

        // 销毁的 session 均从 HashSet 集中移除
        sessions.remove(session);

    }

}
