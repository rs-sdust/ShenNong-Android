# ShenNong-Android
农场Android客户端

##后续提高和改进：

###1.绘制地块时可以绘制多个地块，思路如下：
   <1>进入添加地块页面-----选择第一个边界的特征点-----点击“绘制”按钮-----弹出输入属性界面-----输入并上传
             | _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ |
             
   <2>进入添加地块页面-----选择第一个边界的特征点-----点击“绘制”按钮-----点击polygon弹出输入属性界面-----判断是否全部输入了属性信息
                                 | _ _ _ _ _ _ _ _ _ |
                                 
###2.为首页添加添加动画效果：
    ObjectAnimator + Interpolator + png
    
###3.进一步解决内存泄漏和OOM问题：
    handler  bitmap  thread ......
    
##参考文档及说明：

###1.地图实现：OpenStreetMap + GoogleMap tile \n
   注意事项：GoogleMap的地址源要注意是国内服务器地址
   参考文档：
   https://github.com/osmdroid/osmdroid
   http://osmdroid.github.io/osmdroid/javadoc.html
   https://developers.google.cn/maps/

###2.天气模块及短信验证码模块的实现：Mob
   注意事项：天气信息比较准确，但是数据质量堪忧，有时甚至没有预报信息，另外对于某些县一级单位没有数据
   参考文档:
   短信验证码  http://wiki.mob.com/sdk-sms-android-3-0-0/
   天气接口    http://api.mob.com/#/apiwiki/weather
   
###3.说明：
   服务器的地址保存在了Application全局变量中了，发布时应注意修改这一地址
