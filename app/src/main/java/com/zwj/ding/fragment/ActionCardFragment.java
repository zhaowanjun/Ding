package com.zwj.ding.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zwj.ding.MyApplication;
import com.zwj.ding.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActionCardFragment extends Fragment implements View.OnClickListener{
    private EditText etWebhook;
    private EditText etTitle, etMsgLink, etMarkdown;
    private Button btnSend;
    private final int COUNT_DOWN = 1;
    private final int ENABLE_TRUE = 2;
    private final int SEND_SUCCESS = 3;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case COUNT_DOWN:
                    int second = (int) msg.obj;
                    btnSend.setText(getString(R.string.send) + "("+ second +"s)");
                    if(second-- == 0) {
                        btnSend.setText(R.string.send);
                        btnSend.setEnabled(true);
                    } else {
                        countDown(second);
                    }
                    return true;
                case ENABLE_TRUE:
                    btnSend.setEnabled(true);
                    return true;
                case SEND_SUCCESS:
                    Toast.makeText(getActivity(), getString(R.string.send_successful), Toast.LENGTH_SHORT).show();
                    etTitle.getText().clear();
                    etMsgLink.getText().clear();
                    etMarkdown.getText().clear();
                    return true;
            }
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_action_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etWebhook = view.findViewById(R.id.et_webhook);
        etTitle = view.findViewById(R.id.et_title);
        etMsgLink = view.findViewById(R.id.et_msg_link);
        etMarkdown = view.findViewById(R.id.et_markdown);
        btnSend = view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String webhookString = etWebhook.getText().toString().trim();
                String titleString = etTitle.getText().toString().trim();
                String msgLinkString = etMsgLink.getText().toString().trim();
                String markdownString = etMarkdown.getText().toString().trim();
                if(!webhookString.isEmpty() && !titleString.isEmpty()
                        && !markdownString.isEmpty() && !msgLinkString.isEmpty()) {
                    btnSend.setEnabled(false);
                    RequestBody body = RequestBody.create(MyApplication.MEDIA_TYPE, toJsonString(titleString, markdownString, msgLinkString, false));
                    final Request request = new Request.Builder()
                            .url(webhookString)
                            .post(body)
                            .build();
                    Call call = MyApplication.okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                            String errcode = jsonObject.getString("errcode");
                            if("0".equals(errcode)) {
                                countDown(5); //倒计时
                                handler.sendEmptyMessage(SEND_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(ENABLE_TRUE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "webhook地址和文本内容不能为空", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void countDown(final int second) {
        if(second >= 0) {
            Message message = handler.obtainMessage(COUNT_DOWN, second);
            handler.sendMessageDelayed(message, 1000);
        }
    }

    public String toJsonString(String title, String text, String singleURL, boolean hideAvatar) {
        Map<String, Object> items = new HashMap();
        items.put("msgtype", "actionCard");
        Map<String, Object> actionCardContent = new HashMap();
        actionCardContent.put("title", title);
        actionCardContent.put("text", text);
        if (hideAvatar) {
            actionCardContent.put("hideAvatar", "1");
        }
        actionCardContent.put("singleTitle", "阅读全文");
        actionCardContent.put("singleURL", singleURL);
        items.put("actionCard", actionCardContent);
        return JSON.toJSONString(items);
    }

}
