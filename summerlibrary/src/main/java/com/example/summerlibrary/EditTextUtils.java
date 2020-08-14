package com.example.summerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class EditTextUtils extends RelativeLayout {

    private Context mContext;
    private boolean canActionNext = false;

    private int mBackground;
    private String mHintString;
    private float mTextSize;
    private int mTextColor;
    private int mHintColor;
    private int mInputMode;
    private int mInputType;
    private boolean mShowTextCount;
    private int mMaxTextCount;
    private float mMinHeight;

    private int mMode;
    public static final int TYPE_SINGLE_LINE = 0x00000000;
    public static final int TYPE_SINGLE_LINE_PWD = 0x00000001;
    public static final int TYPE_MULTILINE = 0x00000002;

    private EditText editText;
    //single line view group
    private ImageView single_btn_eye;
    private ImageView single_btn_delete;
    private boolean isHide;
    private boolean hasEye;
    //multiline view group
    private TextView multi_tv_counter;

    /**
     * system init
     * @param context context
     */
    public EditTextUtils(Context context){
        super(context);
    }

    /**
     * system init
     * @param context context
     * @param attrs attrs
     */
    public EditTextUtils(Context context, AttributeSet attrs) {
        super(context,attrs);
        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EditTextUtils);
        mMode = ta.getInteger(R.styleable.EditTextUtils_editTextMode,TYPE_SINGLE_LINE);
        mBackground = ta.getResourceId(R.styleable.EditTextUtils_background, R.color.editTextUtilsBackgroundDefault);
        mHintString = ta.getString(R.styleable.EditTextUtils_hint);
        mTextSize = ta.getDimension(R.styleable.EditTextUtils_textSize,getResources().getDimension(R.dimen.editTextUtilsTextFontDefault));
        mTextColor = ta.getColor(R.styleable.EditTextUtils_textColor,getResources().getColor(R.color.editTextUtilsTextColorDefault));
        mHintColor = ta.getColor(R.styleable.EditTextUtils_hintColor,getResources().getColor(R.color.editTextUtilsHintColorDefault));
        mInputMode = ta.getInt(R.styleable.EditTextUtils_imeOptions,EditorInfo.IME_ACTION_DONE);
        mInputType = ta.getInt(R.styleable.EditTextUtils_inputType, InputType.TYPE_NULL);
        if (mMode == TYPE_MULTILINE){
            mShowTextCount = ta.getBoolean(R.styleable.EditTextUtils_showTextCount,false);
            mMaxTextCount = ta.getInteger(R.styleable.EditTextUtils_maxTextCount,100);
            mMinHeight = ta.getDimension(R.styleable.EditTextUtils_minHeight,getResources().getDimension(R.dimen.editTextUtilsMinHeightDefault));
        }
        ta.recycle();

        initView();
    }

    /**
     * init RelativeLayout View
     */
    private void initView(){

        switch (mMode){
            case TYPE_SINGLE_LINE:
            case TYPE_SINGLE_LINE_PWD:
                hasEye = (mMode == TYPE_SINGLE_LINE_PWD);
                LayoutInflater.from(mContext).inflate(R.layout.public_edit_text_singleline,this);
                break;
            case TYPE_MULTILINE:
                LayoutInflater.from(mContext).inflate(R.layout.public_edit_text_multiline,this);
                break;
            default:
                break;
        }
        setBackgroundResource(mBackground);
        setUI();
    }

    /**
     * set ui for different mode
     */
    private void setUI(){

        switch (mMode){
            case TYPE_SINGLE_LINE:
            case TYPE_SINGLE_LINE_PWD:
                setSingleLineUI();
                break;
            case TYPE_MULTILINE:
                setMultiLineUI();
                break;
            default:
                break;
        }

    }

    /**
     * set single line ui
     * use for TYPE_SINGLE_LINE || TYPE_SINGLE_LINE_PWD
     */
    private void setSingleLineUI(){

        single_btn_delete = findViewById(R.id.public_single_line_btn_delete);
        single_btn_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearText();
            }
        });

        editText = findViewById(R.id.public_single_line_et);
        editText.setHint(mHintString);
        editText.setHintTextColor(mHintColor);
        //设置字体大小 需要设置TypedValue.COMPLEX_UNIT_PX 否自字体返回px值 太大
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
        editText.setTextColor(mTextColor);
        editText.setImeOptions(mInputMode);
        editText.setInputType(mInputType);
        editText.setOnFocusChangeListener(focusChangeListener);
        editText.setOnEditorActionListener(editorActionListener);

        if (hasEye){
            single_btn_eye = findViewById(R.id.public_single_line_btn_hide);
            single_btn_eye.setVisibility(VISIBLE);
            single_btn_eye.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isHide = !isHide;
                    setEyes();
                }
            });
            isHide = true;
            setEyes();
        }

    }

    /**
     * refresh eyes status and drawable
     */
    private void setEyes(){
        editText.setTransformationMethod(isHide?PasswordTransformationMethod.getInstance():HideReturnsTransformationMethod.getInstance());
        single_btn_eye.setImageDrawable(ContextCompat.getDrawable(mContext,isHide? R.drawable.edit_text_utils_eye_hide : R.drawable.edit_text_utils_eye_show));
        //光标控制在输入内容的末尾
        editText.setSelection(getText().length());
    }

    /**
     * set multi line ui
     * use for TYPE_MULTILINE
     */
    private void setMultiLineUI(){

        multi_tv_counter = findViewById(R.id.public_multiline_counter);
        multi_tv_counter.setVisibility(mShowTextCount?VISIBLE:GONE);
        if (mShowTextCount){
            multi_tv_counter.setText(new StringBuffer().append("0 / ").append(mMaxTextCount));
        }

        editText = findViewById(R.id.public_multiline_et);
        editText.setHint(mHintString);
        editText.setHintTextColor(mHintColor);
        //设置字体大小 需要设置TypedValue.COMPLEX_UNIT_PX 否自字体返回px值 太大
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
        editText.setTextColor(mTextColor);
        editText.setImeOptions(mInputMode);
        editText.setInputType(mInputType);
        editText.addTextChangedListener(textWatcher);
        editText.setMinHeight(px2dip(mContext,mMinHeight));
        editText.setOnFocusChangeListener(focusChangeListener);
        editText.setOnEditorActionListener(editorActionListener);

    }

    /**
     * edit text focus change listener
     */
    OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                if (mMode != TYPE_MULTILINE){
                    single_btn_delete.setVisibility(VISIBLE);
                }
            }else {
                if (mMode != TYPE_MULTILINE){
                    single_btn_delete.setVisibility(INVISIBLE);
                }
                closeKeyboard(v);
            }
        }
    };

    /**
     * edit text action enter click listener
     */
    EditText.OnEditorActionListener editorActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (mMode != TYPE_MULTILINE){
                editText.clearFocus();
            }
            if (canActionNext) {
                mOnActionClickListener.onActionClickFinish(v.getText().toString());
            }
            return false;
        }
    };

    /**
     * edit text string change watcher
     */
    TextWatcher textWatcher = new TextWatcher() {

        private CharSequence enterWords;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            enterWords= s;
            //TextView显示已有字数
            if (mShowTextCount){
                multi_tv_counter.setText(new StringBuffer().append(enterWords.length()).append(" / ").append(mMaxTextCount));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

            int selectionStart = editText.getSelectionStart();
            int selectionEnd = editText.getSelectionEnd();
            if (enterWords.length() > mMaxTextCount) {
                //删除多余输入的字（不会显示出来）
                s.delete(selectionStart - 1, selectionEnd);
                editText.setText(s);
                //设置光标在最后
                editText.setSelection(s.length());
            }
        }
    };

    /**
     * close system keyboard
     * @param view view this
     */
    public void closeKeyboard( View view ){
        view.clearFocus();
        InputMethodManager manager = ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE));
        assert manager != null;
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * get edit text finish text
     * @return string text
     */
    public String getText(){
        return editText.getText().toString();
    }

    /**
     * clean all the values that have been entered
     */
    private void clearText(){
        editText.setText("");
    }

    /**
     * 判断输入框内容是否不符合要求
     * @param minLength min length
     * @param maxLength max length
     * @param fixLength 有固定要求的长度
     * @return true 为不符合
     */
    public boolean contentIsUnusable(Integer minLength, Integer maxLength, Integer fixLength){
        if (editText.getText().length() == 0){
            //先判断是否为空
            return true;
        }else {
            boolean isUnusable;
            if (minLength == null && maxLength == null && fixLength == null) {
                //这个位置只是为了判断是否为空,在上面判断完了已经,直接返回就行了
                isUnusable = false;
            }else {
                if (fixLength != null){
                    //如果存在固定长度要求 直接判断是不是满足固定长度就行了
                    isUnusable = editText.getText().length() != fixLength;
                } else{
                    //没有固定长度要求
                    if (minLength == null){
                        //没有最小长度时 判断是不是低于最大长度
                        isUnusable = editText.getText().length() > maxLength;
                    }else {
                        //最小 最大长度存在要求时 判断是不是处于其区间
                        isUnusable = editText.getText().length() < minLength || editText.getText().length() > maxLength;
                    }
                }
            }
            return isUnusable;
        }
    }

    /**
     * on action finish click listener
     * if action is done, can do something next
     */
    private OnActionClickListener mOnActionClickListener;
    public interface OnActionClickListener{
        void onActionClickFinish(String inputString);
    }

    public void setOnActionClickListener(OnActionClickListener listener){
        if (!canActionNext){
            canActionNext = true;
        }
        mOnActionClickListener = listener;
    }

    /**
     * px to dip util
     * @param context context
     * @param pxValue px value
     * @return int dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
