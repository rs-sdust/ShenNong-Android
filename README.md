# ShenNong-Android
农场Android客户端

## 后续提高和改进：

### 1.绘制地块时可以绘制多个地块，思路如下：
   <1>进入绘制地块页面-----选择第一个边界的特征点-----点击“绘制”按钮-----弹出输入属性界面-----输入并上传-----回到绘制地块界面 <br>
             
   <2>进入绘制地块页面----- 循环进行：(选择第一个边界的特征点-----点击“绘制”按钮)-----点击polygon弹出输入属性界面-----判断是否全部输入了属性信息<br>
                                                              
### 2.为首页添加添加动画效果：
    ObjectAnimator + Interpolator + png
    
### 3.进一步解决内存泄漏和OOM问题：
    handler  bitmap  thread ......
    
## 参考文档及说明：

### 1.地图实现：OpenStreetMap + GoogleMap tile 
   注意事项：GoogleMap的地址源要注意是国内服务器地址<br>
   参考文档：<br>
   [osmdroid--OpenStreetMap的SDK](https://github.com/osmdroid/osmdroid)<br>
   [osmdroid--javadoc](http://osmdroid.github.io/osmdroid/javadoc.html)<br>
   [GoogleMap--可以参考(与OSM相似)](https://developers.google.cn/maps/)<br>

### 2.天气模块及短信验证码模块的实现：Mob
   注意事项：天气信息比较准确，但是数据质量堪忧，有时甚至没有预报信息，另外对于某些县一级单位没有数据<br>
   参考文档:<br>
   [短信验证码集成说明](http://wiki.mob.com/sdk-sms-android-3-0-0/)<br>
   [天气接口api](http://api.mob.com/#/apiwiki/weather)<br>
   
### 3.说明：
   服务器的地址保存在了Application全局变量中了，发布时应注意修改这一地址 <br>
   
   #### SharedPreferences---"User":存储User和Farm信息
   |变量名称|变量类型|变量作用|备注|
   |:---|:---|:---|:---|
   |Logedin|boolean|判断是否已经登陆||
   |Token|string|记录用户的Token值|与服务器交互的唯一标识|
   |用户信息：||||
   |User_ID|int|记录用户的id||
   |User_Name|string|记录用户名||
   |PhoneNumber|string|记录用户手机号码|用户的唯一标识，不可修改项|
   |Password|string|即时通讯的密码|服务器随机生成的，已废弃|
   |Role|int|用户的角色|目前只有0和1两个值，0代表农场主，1代表管理员|
   |Farm|int|用户所对应的农场id||
   |Icon|string|用户的头像||
   |农场信息：||||
   |FarmID|int|农场的id|农场的唯一标识，不可修改项|
   |FarmName|string|农场的名称||
   |Province|string|农场所在的省||
   |City|string|农场的所在市||
   |County|string|农场的所在县||
   |ProvinceIndex|int|农场省份在spinner中的位置|默认值是0|
   |CityIndex|int|农场市在spinner中的位置|默认值是0|
   |CountyIndex|int|农场县在spinner中的位置|默认值是0|
