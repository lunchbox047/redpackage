package com.example.osq.redpackage;

import java.util.List;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

@SuppressLint("NewApi")
public class QQhongbao extends AccessibilityService
{

    static final String TAG = "QiangHongBao";

    /** 红包消息的关键字 */
    static final String QQ_HONGBAO_TEXT_KEY = "[QQ红包]";


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        final int eventType = event.getEventType(); // ClassName:

        // 通知栏事件
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
        {
            List<CharSequence> texts = event.getText();
            if (!texts.isEmpty())
            {
                for (CharSequence t : texts)
                {
                    String text = String.valueOf(t);
                    if ( text.contains(QQ_HONGBAO_TEXT_KEY))
                    {
                        openNotify(event);
                        break;
                    }
                }
            }
        }else if(event.getClassName().toString().equals("android.widget.TextView")){
            AccessibilityNodeInfo node=event.getSource();
            if(node.getText()==null){
                return;
            }
            if(node.getText().toString().contains("[QQ红包]")){
                event.getSource().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                //和别人聊天时候抢红包
            }
        }
        else{
            Log.w("在聊天界面了","准备抢红包");
            Log.w("QQ红包",event.getSource().getClassName().toString());
            checkKey(event);
        }
    }

    @Override
    public void onInterrupt()
    {
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected()
    {
        super.onServiceConnected();
        Toast.makeText(this, "连接抢红包服务", Toast.LENGTH_SHORT).show();
    }

    /** 打开通知栏消息 */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotify(AccessibilityEvent event)
    {
        if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification))
        {
            return;
        }
        // 将通知栏消息打开
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;
        try
        {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e)
        {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkKey(AccessibilityEvent event)
    {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        Log.w("QQ红包",nodeInfo.getClassName().toString());
        if (nodeInfo == null)
        {
            Log.w(TAG, "rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> qqList = nodeInfo.findAccessibilityNodeInfosByText("QQ红包");
        Log.w("QQ红包",String.valueOf(qqList.size()));
        if (!qqList.isEmpty())
        {
            for (AccessibilityNodeInfo n : qqList)
            {
                Log.w("列表",n.getClassName().toString());
                if(n.getText()!=null){
                    Log.w("文本信息",n.getText().toString());
                    if(n.getText().toString().equals("QQ红包")){
                        Log.w("父亲结点",n.getParent().getChild(1).getText().toString());
                        if(n.getParent()!=null&&n.getParent().getChild(1)!=null){
                            Log.w("界面文字",n.getParent().getChild(1).getText().toString());
                            if(!n.getParent().getChild(1).getText().toString().equals("已拆开")){
                                n.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
                }
            }
        }
    }
}
