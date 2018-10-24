package com.syc.am.cntr;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by miles.shi on 2017/7/25.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Const {

    String max_length = "one hundred";
    int a = 109;


   /*    git checkout -b <branch name> <SHA1>
        这样就checkout之前的一个commit并开一个branch指向它了.

    如果不打算做修改,
    只是想checkout出来的话, git checkout <SHA1>就行, 用
    detached HEAD特性.

    提交代码：

    1
    #git commit -a -m "fix bug:params incorrect in function rpc_execute_context_query(),which will cause dbus work abnormal"
     注意：这只是提交到branch "dbus_work_ok"上，并没有合并到主干上（因为当前的分支是"dbus_work_ok"）.

    切换到主干master :  git checkout master
     (当前分支是"master")
    合并到主干master上: git merge dbus_work_ok
   删除不需要的branch  "dbus_work_ok": git branch -d dbus_work_ok
    6.在主干分支"master"上继续工作.
*/

}
