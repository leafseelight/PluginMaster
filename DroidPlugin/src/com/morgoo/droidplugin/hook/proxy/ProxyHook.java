/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.droidplugin.hook.proxy;

import android.content.Context;
import android.text.TextUtils;

import com.morgoo.droidplugin.hook.Hook;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.helper.MyProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/14.
 */
public abstract class ProxyHook extends Hook implements InvocationHandler {

    protected Object mOldObj;

    public ProxyHook(Context hostContext) {
        super(hostContext);
    }

    public void setOldObj(Object oldObj) {
        this.mOldObj = oldObj;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        try {
            if (!isEnable()) {
                return method.invoke(mOldObj, args);
            }
            HookedMethodHandler hookedMethodHandler = mHookHandles.getHookedMethodHandler(method);
            if (hookedMethodHandler != null) {
                return hookedMethodHandler.doHookInner(mOldObj, method, args);
            }
            return method.invoke(mOldObj, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause != null && MyProxy.isMethodDeclaredThrowable(method, cause)) {
                throw cause;
            } else if (cause != null) {
                RuntimeException runtimeException = !TextUtils.isEmpty(cause.getMessage()) ? new RuntimeException(cause.getMessage()) : new RuntimeException();
                runtimeException.initCause(cause);
                throw runtimeException;
            } else {
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        } catch (IllegalArgumentException e) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(" DROIDPLUGIN{");
                if (method != null) {
                    sb.append("method[").append(method.toString()).append("]");
                } else {
                    sb.append("method[").append("NULL").append("]");
                }
                if (args != null) {
                    sb.append("args[").append(Arrays.toString(args)).append("]");
                } else {
                    sb.append("args[").append("NULL").append("]");
                }
                sb.append("}");

                String message = e.getMessage() + sb.toString();
                throw new IllegalArgumentException(message, e);
            } catch (Throwable e1) {
                throw e;
            }
        } catch (Throwable e) {
            if (MyProxy.isMethodDeclaredThrowable(method, e)) {
                throw e;
            } else {
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        }
    }
}
