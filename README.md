# AccessbilityServiceDemo

### 序

1. 认识AccessibilityService
2. 通过AccessbilityService完成自动安装功能(有些鸡肋)

## 基础使用

1. 创建自己的服务类继承AccessibilityService实现父类的相关方法

```java
public class MyAccessbilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        LogUtil.i("onAccessibilityEvent");
        if (event.getSource() != null)
        {
            findAndPerformAction("安装");
            findAndPerformAction("下一步");
            findAndPerformAction("完成");
        }
    }

    private void findAndPerformAction(String action) {
// AccessibilityEvent.getSource(),
// findFocus(int),
// getWindow()或者
// getRootInActiveWindow()获取窗口内容
        if (getRootInActiveWindow() == null) {
            return;
        }
        List<AccessibilityNodeInfo> text = getRootInActiveWindow().
                findAccessibilityNodeInfosByText(action);
        for (int i = 0; i < text.size(); i++) {
            AccessibilityNodeInfo info = text.get(i);
            if (info.getClassName().equals("android.widget.Button")&&info.isEnabled()){
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtil.i("onServiceConnected");
    }
}
```



| 方法                                       | 作用                                       | 描述                                       |
| ---------------------------------------- | ---------------------------------------- | ---------------------------------------- |
| onServiceConnected()                     | 系统成功绑定该服务时被触发,也就是当你在设置中开启相应的服务,系统成功的绑定了该服务时会触发,通常我们可以在这里做一些初始化操作 | 手机设置里面->辅助功能在这里面找到你自己实现的辅助类。启动自己的服务类时会触发该回调方法。 |
| onAccessibilityEvent(AccessibilityEvent event) | 有关AccessibilityEvent事件的回调函数.系统通过sendAccessibiliyEvent()不断的发送AccessibilityEvent到此处 | 当屏幕有变化或者是相关监听事件发生时触发该回调方法。               |
| findFoucs(int falg)                      | 禁用当前服务,也就是在服务可以通过该方法停止运行                 | getRootInActiveWindow()                  |
| getSeviceInfo()                          | 获取当前服务的配置信息                              |                                          |

2. 声明服务

像其他Service服务一样,需要在AndroidManifest.xml中声明.除此之外,该服务还必须配置以下两项:

* 配置``,其name为固定的android.accessibilityservice.AccessibilityService

- 声明BIND_ACCESSIBILITY_SERVICE权限,以便系统能够绑定该服务(4.1版本后要求)

注意:任何一点配置错误,系统都检测不到该服务,因此其固定配置如下:

```xml
   <service
            android:name=".service.MyAccessbilityService"
            android:enabled="true"
            android:exported="true"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService"/>
            </intent-filter>
  </service>
```

3. 配置服务

在AndroidManifest.xml声明了该服务之后,接下来就是需要对该服务进行一些参数设置了.该服务能够被配置用来接受指定类型的事件,监听指定package,检索窗口内容,获取事件类型的时间等等.目前有两种配置方法:

* 4.0之后提供了可以通过`<meta-data>`标签进行配置

在manifest生命的servce中提供一个meta-data标签,然后通过android:resource指定相应的配置文件(在res目录下创建xml文件,并在其中创建配置文件accessibility.xml):

###### AndroidManifest.xml

```xml
        <service
            android:name=".service.MyAccessbilityService"
            android:enabled="true"
            android:exported="true"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService"/>
            </intent-filter>

            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessbility">
            </meta-data>
        </service>
```

###### accessbility.xml

```xml
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
                       android:description="@string/description"
                       android:accessibilityEventTypes="typeWindowStateChanged|typeWindowContentChanged|typeNotificationStateChanged"
                       android:accessibilityFeedbackType="feedbackAllMask"
                       android:packageNames="com.android.packageinstaller"
                       android:notificationTimeout="10"
                       android:accessibilityFlags="flagDefault"
                       android:canRetrieveWindowContent="true">

</accessibility-service>
    <!--com.zdd.accessbilityservicedemo-->
    <!--com.tencent.mm,com.tencent.mobileqq-->
    <!--com.android.packageinstaller-->

    <!--accessibilityEventTypes:表示该服务对界面中的哪些变化感兴趣,即哪些事件通知,比如窗口打开,滑动,焦点变化,长按等.具体的值可以在AccessibilityEvent类中查到,如typeAllMask表示接受所有的事件通知.-->
    <!--accessibilityFeedbackType:表示反馈方式,比如是语音播放,还是震动-->
    <!--canRetrieveWindowContent:表示该服务能否访问活动窗口中的内容.也就是如果你希望在服务中获取窗体内容的化,则需要设置其值为true.-->
    <!--notificationTimeout:接受事件的时间间隔,通常将其设置为100即可.-->
    <!--packageNames:表示对该服务是用来监听哪个包的产生的事件-->
    <!--更多信息参见官方文档https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo.html-->
```

* 通过setServiceInfo()进行配置

通过`setServiceInfo(AccessibilityServiceInfo)`为其配置信息,除此之外,通过该方法可以在运行期间动态修改服务配置.需要注意,该方法只能用来配置动态属性:eventTypes,feedbackType,flags,notificaionTimeout及packageNames.

通常是在`onServiceConnected()`进行配置,如下代码:

```java
@Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.packageNames = new String[]{"com.tencent.mm"}; 
        serviceInfo.notificationTimeout=10;
        setServiceInfo(serviceInfo);
    }
```



## 使用AccessibilityService实现自动安装

