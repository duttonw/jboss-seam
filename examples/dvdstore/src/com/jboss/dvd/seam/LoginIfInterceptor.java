package com.jboss.dvd.seam;

import java.util.Arrays;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.BusinessProcessInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;

@Around({BijectionInterceptor.class, ValidationInterceptor.class, 
   ConversationInterceptor.class, BusinessProcessInterceptor.class})
@Within(RemoveInterceptor.class)
public class LoginIfInterceptor
{
    public static final String LOGIN_KEY = "loggedIn";

    @AroundInvoke
    public Object loginIf(InvocationContext invocation) 
        throws Exception
    {
        Object  result = invocation.proceed();
        LoginIf anno   = invocation.getMethod().getAnnotation(LoginIf.class);
        if (anno != null) {
            //log.info("testing result " + result);
            if (Arrays.asList(anno.outcome()).contains(result))  {
                //log.info("logged in");
                Contexts.getSessionContext().set(LOGIN_KEY, true);
            }
        }

        return result;
    }
}
