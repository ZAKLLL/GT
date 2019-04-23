package com.example.hp.gt.Util;

import java.util.HashMap;
import java.util.Map;

public class Languages {
    Map<String, String> map = new HashMap();

    public  String getCode(String language) {
        map.put("中文", "zh-CHS");
        map.put("日文", "ja");
        map.put("英文", "EN");
        map.put("韩文", "ko");
        map.put("法文", "fr");
        map.put("阿拉伯文", "ar");
        map.put("波兰文", "pl");
        map.put("丹麦文", "da");
        map.put("德文", "de");
        map.put("俄文", "ru");
        map.put("芬兰文", "fi");
        map.put("荷兰文", "nl");
        map.put("捷克文", "cs");
        map.put("罗马尼亚文", "ro");
        map.put("挪威文", "no");
        map.put("葡萄牙文", "pt");
        map.put("瑞典文", "sv");
        map.put("斯洛伐克文", "sk");
        map.put("西班牙文", "es");
        map.put("印地文", "hi");
        map.put("印度尼西亚文", "id");
        map.put("意大利文", "it");
        map.put("泰文", "th");
        map.put("土耳其文", "tr");
        map.put("希腊文", "el");
        map.put("匈牙利文", "hu");

        return map.get(language);
    }
}
