# EditTextUtils
- 单行文本输入框(密码式/非密码式)
- 多行文本输入框 可统计字数
- 一键删除已输入文本


### 添加项目依赖
#### build.gradle
```javascript
allprojects {
	 repositories {
	  ...
		 maven { url 'https://jitpack.io' }
	 }
 }
```

#### dependency
```javascript
dependencies {
	        implementation 'com.github.LDYSummer:EditTextUtils:1.1.4'
	}
```
### 属性说明

|名称 | 属性 | 参数说明 |
|-----|-----|-----------
|输入框模式 | app:editTextMode | modeSingleNormal 非密码形式单行输入框<br>modeSinglePwd 密码形式单行输入框<br>modeMultiLine 多行文本输入框|
|layout背景| app:background | drawable/color resource id 默认#ffffff |
|提示文本 | app:hint | string |
|字体大小 | app:textSize | 默认15sp |
|文本颜色 | app:textColor | color 默认#333333 |
|提示文本颜色 | app:hintColor | color 默认#999999 |
|回车按钮类型 | app:imeOptions | actionNone<br>actionGo<br>actionSearch<br>actionSend<br>actionNext<br>actionDone|
|键盘类型 | app:inputType | system inputType |
|多行文本输入框字数统计是否显示 | app:showTextCount | boolean 默认false|
|多行文本输入框最大字数限制 | app:maxTextCount | integer 默认100 |
|多行文本输入框最小高度 | app:minHeight | 默认150dp | 

### 使用
#### XML
```javascript
<com.example.edittextdemo.EditTextUtils
        android:id="@+id/edit_text_utils_single_normal"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        app:background="@color/editTextUtilsBackgroundDefault"
        app:editTextMode="modeSingleNormal"
        app:hint="单行非密码形式输入框"
        app:textSize="15sp"
        app:textColor="@color/editTextUtilsTextColorDefault"
        app:hintColor="@color/editTextUtilsHintColorDefault"
        app:imeOptions="actionSearch"/>
```
```javascript
    <com.example.edittextdemo.EditTextUtils
        android:id="@+id/edit_text_utils_single_pwd"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        app:hint="单行密码形式输入框"
        app:editTextMode="modeSinglePwd"
	app:inputType="textPassword"/>
```

```javascript
    <com.example.edittextdemo.EditTextUtils
        android:id="@+id/edit_text_utils_multi"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        app:hint="多行文本形式输入框"
        app:editTextMode="modeMultiLine"
        app:maxTextCount="50"
        app:showTextCount="true"
        app:minHeight="200dp"/>
```

#### 回车点击事件监听
```javascript
etu.setOnActionClickListener(new EditTextUtils.OnActionClickListener() {
     @Override
         public void onActionClickFinish(String inputString) {
         ...
     }
 });
```

#### 判断文本内容长度是否不可用
minLength 文本最小长度 可为null<br>maxLength 文本最大长度 可为null<br>fixLength 文本限制固定长度 可为null
```javascript
if (etu_single_normal.contentIsUnusable(minLength,maxLength,fixLength)){
     ...
}
```
#### 其他方法
- getText()
- clearText()
