package com.egovalley.service.impl;

import com.egovalley.common.ConstantMagicIce;
import com.egovalley.common.ConstantRegex;
import com.egovalley.service.MagicIceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

@Service("magicIceService")
public class MagicIceServiceImpl implements MagicIceService {

    private static final Logger logger = LoggerFactory.getLogger(MagicIceServiceImpl.class);

    private List<String> welcomeWords = new ArrayList<>();
    private List<String> goodbyeWords = new ArrayList<>();
    private List<String> doingWords = new ArrayList<>();
    private List<String> eatingWords = new ArrayList<>();
    private List<String> morningWords = new ArrayList<>();
    private List<String> noonWords = new ArrayList<>();
    private List<String> nightWords = new ArrayList<>();
    private List<String> callWords = new ArrayList<>();
    private List<String> loveWords = new ArrayList<>();
    private List<String> beautyWords = new ArrayList<>();
    private List<String> hobbyWords = new ArrayList<>();
    private List<String> answerWords = new ArrayList<>();
    private List<String> laughWords = new ArrayList<>();
    private List<String> unknownWords = new ArrayList<>();

    private List<String> welcomeRegs = new ArrayList<>();
    private List<String> goodbyeRegs = new ArrayList<>();
    private List<String> doingRegs = new ArrayList<>();
    private List<String> eatingRegs  = new ArrayList<>();
    private List<String> morningRegs = new ArrayList<>();
    private List<String> noonRegs = new ArrayList<>();
    private List<String> nightRegs = new ArrayList<>();
    private List<String> callRegs = new ArrayList<>();
    private List<String> loveRegs = new ArrayList<>();
    private List<String> beautyRegs = new ArrayList<>();
    private List<String> hobbyRegs = new ArrayList<>();
    private List<String> answerRegs = new ArrayList<>();
    private List<String> laughRegs = new ArrayList<>();

    @PostConstruct
    private void addList() {
        welcomeWords.add(ConstantMagicIce.WELCOME_WORD_1);
        welcomeWords.add(ConstantMagicIce.WELCOME_WORD_2);
        welcomeWords.add(ConstantMagicIce.WELCOME_WORD_3);
        welcomeWords.add(ConstantMagicIce.WELCOME_WORD_4);
        welcomeWords.add(ConstantMagicIce.WELCOME_WORD_5);

        goodbyeWords.add(ConstantMagicIce.GOODBYE_WORD_1);
        goodbyeWords.add(ConstantMagicIce.GOODBYE_WORD_2);
        goodbyeWords.add(ConstantMagicIce.GOODBYE_WORD_3);
        goodbyeWords.add(ConstantMagicIce.GOODBYE_WORD_4);
        goodbyeWords.add(ConstantMagicIce.GOODBYE_WORD_5);

        doingWords.add(ConstantMagicIce.DOING_WORD_1);
        doingWords.add(ConstantMagicIce.DOING_WORD_2);
        doingWords.add(ConstantMagicIce.DOING_WORD_3);
        doingWords.add(ConstantMagicIce.DOING_WORD_4);
        doingWords.add(ConstantMagicIce.DOING_WORD_5);

        eatingWords.add(ConstantMagicIce.EATING_WORD_1);
        eatingWords.add(ConstantMagicIce.EATING_WORD_2);
        eatingWords.add(ConstantMagicIce.EATING_WORD_3);
        eatingWords.add(ConstantMagicIce.EATING_WORD_4);
        eatingWords.add(ConstantMagicIce.EATING_WORD_5);

        morningWords.add(ConstantMagicIce.MORNING_WORD_1);
        morningWords.add(ConstantMagicIce.MORNING_WORD_2);
        morningWords.add(ConstantMagicIce.MORNING_WORD_3);
        morningWords.add(ConstantMagicIce.MORNING_WORD_4);
        morningWords.add(ConstantMagicIce.MORNING_WORD_5);

        noonWords.add(ConstantMagicIce.NOON_WORD_1);
        noonWords.add(ConstantMagicIce.NOON_WORD_2);
        noonWords.add(ConstantMagicIce.NOON_WORD_3);
        noonWords.add(ConstantMagicIce.NOON_WORD_4);
        noonWords.add(ConstantMagicIce.NOON_WORD_5);

        nightWords.add(ConstantMagicIce.NIGHT_WORD_1);
        nightWords.add(ConstantMagicIce.NIGHT_WORD_2);
        nightWords.add(ConstantMagicIce.NIGHT_WORD_3);
        nightWords.add(ConstantMagicIce.NIGHT_WORD_4);
        nightWords.add(ConstantMagicIce.NIGHT_WORD_5);

        callWords.add(ConstantMagicIce.CALL_WORD_1);
        callWords.add(ConstantMagicIce.CALL_WORD_2);
        callWords.add(ConstantMagicIce.CALL_WORD_3);
        callWords.add(ConstantMagicIce.CALL_WORD_4);
        callWords.add(ConstantMagicIce.CALL_WORD_5);

        loveWords.add(ConstantMagicIce.LOVE_WORD_1);
        loveWords.add(ConstantMagicIce.LOVE_WORD_2);
        loveWords.add(ConstantMagicIce.LOVE_WORD_3);
        loveWords.add(ConstantMagicIce.LOVE_WORD_4);
        loveWords.add(ConstantMagicIce.LOVE_WORD_5);

        beautyWords.add(ConstantMagicIce.BEAUTY_WORD_1);
        beautyWords.add(ConstantMagicIce.BEAUTY_WORD_2);
        beautyWords.add(ConstantMagicIce.BEAUTY_WORD_3);
        beautyWords.add(ConstantMagicIce.BEAUTY_WORD_4);
        beautyWords.add(ConstantMagicIce.BEAUTY_WORD_5);

        hobbyWords.add(ConstantMagicIce.HOBBY_WORD_1);
        hobbyWords.add(ConstantMagicIce.HOBBY_WORD_2);
        hobbyWords.add(ConstantMagicIce.HOBBY_WORD_3);
        hobbyWords.add(ConstantMagicIce.HOBBY_WORD_4);
        hobbyWords.add(ConstantMagicIce.HOBBY_WORD_5);

        answerWords.add(ConstantMagicIce.ANSWER_WORD_1);
        answerWords.add(ConstantMagicIce.ANSWER_WORD_2);
        answerWords.add(ConstantMagicIce.ANSWER_WORD_3);
        answerWords.add(ConstantMagicIce.ANSWER_WORD_4);
        answerWords.add(ConstantMagicIce.ANSWER_WORD_5);

        laughWords.add(ConstantMagicIce.LAUGH_WORD_1);
        laughWords.add(ConstantMagicIce.LAUGH_WORD_2);
        laughWords.add(ConstantMagicIce.LAUGH_WORD_3);
        laughWords.add(ConstantMagicIce.LAUGH_WORD_4);
        laughWords.add(ConstantMagicIce.LAUGH_WORD_5);

        unknownWords.add(ConstantMagicIce.UNKNOWN_WORD_1);
        unknownWords.add(ConstantMagicIce.UNKNOWN_WORD_2);
        unknownWords.add(ConstantMagicIce.UNKNOWN_WORD_3);
        unknownWords.add(ConstantMagicIce.UNKNOWN_WORD_4);
        unknownWords.add(ConstantMagicIce.UNKNOWN_WORD_5);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        welcomeRegs.add(ConstantRegex.WELCOME_REG_1);
        welcomeRegs.add(ConstantRegex.WELCOME_REG_2);
        welcomeRegs.add(ConstantRegex.WELCOME_REG_3);
        welcomeRegs.add(ConstantRegex.WELCOME_REG_4);

        goodbyeRegs.add(ConstantRegex.GOODBYE_REG_1);
        goodbyeRegs.add(ConstantRegex.GOODBYE_REG_2);
        goodbyeRegs.add(ConstantRegex.GOODBYE_REG_3);
        goodbyeRegs.add(ConstantRegex.GOODBYE_REG_4);
        goodbyeRegs.add(ConstantRegex.GOODBYE_REG_5);

        doingRegs.add(ConstantRegex.DOING_REG_1);
        doingRegs.add(ConstantRegex.DOING_REG_2);
        doingRegs.add(ConstantRegex.DOING_REG_3);
        doingRegs.add(ConstantRegex.DOING_REG_4);
        doingRegs.add(ConstantRegex.DOING_REG_5);

        eatingRegs.add(ConstantRegex.EATING_REG_1);
        eatingRegs.add(ConstantRegex.EATING_REG_2);
        eatingRegs.add(ConstantRegex.EATING_REG_3);
        eatingRegs.add(ConstantRegex.EATING_REG_4);
        eatingRegs.add(ConstantRegex.EATING_REG_5);
        eatingRegs.add(ConstantRegex.EATING_REG_6);

        morningRegs.add(ConstantRegex.MORNING_REG_1);
        morningRegs.add(ConstantRegex.MORNING_REG_2);

        noonRegs.add(ConstantRegex.NOON_REG_1);
        noonRegs.add(ConstantRegex.NOON_REG_2);

        nightRegs.add(ConstantRegex.NIGHT_REG_1);
        nightRegs.add(ConstantRegex.NIGHT_REG_2);

        callRegs.add(ConstantRegex.CALL_REG_1);
        callRegs.add(ConstantRegex.CALL_REG_2);
        callRegs.add(ConstantRegex.CALL_REG_3);

        loveRegs.add(ConstantRegex.LOVE_REG_1);
        loveRegs.add(ConstantRegex.LOVE_REG_2);
        loveRegs.add(ConstantRegex.LOVE_REG_3);
        loveRegs.add(ConstantRegex.LOVE_REG_4);

        beautyRegs.add(ConstantRegex.BEAUTY_REG_1);
        beautyRegs.add(ConstantRegex.BEAUTY_REG_2);
        beautyRegs.add(ConstantRegex.BEAUTY_REG_3);
        beautyRegs.add(ConstantRegex.BEAUTY_REG_4);

        hobbyRegs.add(ConstantRegex.HOBBY_REG_1);
        hobbyRegs.add(ConstantRegex.HOBBY_REG_2);
        hobbyRegs.add(ConstantRegex.HOBBY_REG_3);
        hobbyRegs.add(ConstantRegex.HOBBY_REG_4);
        hobbyRegs.add(ConstantRegex.HOBBY_REG_5);
        hobbyRegs.add(ConstantRegex.HOBBY_REG_6);
        hobbyRegs.add(ConstantRegex.HOBBY_REG_7);

        answerRegs.add(ConstantRegex.ANSWER_REG_1);
        answerRegs.add(ConstantRegex.ANSWER_REG_2);

        laughRegs.add(ConstantRegex.LAUGH_REG_1);
        laughRegs.add(ConstantRegex.LAUGH_REG_2);
        laughRegs.add(ConstantRegex.LAUGH_REG_3);
    }

    /**
     * 字符串
     * 模糊匹配: ^(.*张三.*李四.*)$
     * 等值匹配 ^(张三)$
     *
     * 随机数
     * 1~6: int num = (int) (Math.random() * 6) + 1;
     * 0~5: int num = (int) (Math.random() * 6);
     */
    @Override
    public Map<String, Object> discernMessage(String message) {
        Map<String, Object> returnMap = new HashMap<>();

        for (String regex : welcomeRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, welcomeWords.get(new Random().nextInt(welcomeWords.size())));
        for (String regex : goodbyeRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, goodbyeWords.get(new Random().nextInt(goodbyeWords.size())));
        for (String regex : doingRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, doingWords.get(new Random().nextInt(doingWords.size())));
        for (String regex : eatingRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, eatingWords.get(new Random().nextInt(eatingWords.size())));
        for (String regex : morningRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, morningWords.get(new Random().nextInt(morningWords.size())));
        for (String regex : noonRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, noonWords.get(new Random().nextInt(noonWords.size())));
        for (String regex : nightRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, nightWords.get(new Random().nextInt(nightWords.size())));
        for (String regex : callRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, callWords.get(new Random().nextInt(callWords.size())));
        for (String regex : loveRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, loveWords.get(new Random().nextInt(loveWords.size())));
        for (String regex : hobbyRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, hobbyWords.get(new Random().nextInt(hobbyWords.size())));
        for (String regex : beautyRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, beautyWords.get(new Random().nextInt(beautyWords.size())));
        for (String regex : answerRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, answerWords.get(new Random().nextInt(answerWords.size())));
        for (String regex : laughRegs) if (Pattern.matches(regex, message)) return getMap(returnMap, laughWords.get(new Random().nextInt(laughWords.size())));

        return getMap(returnMap, unknownWords.get(new Random().nextInt(unknownWords.size())));
    }

    private Map<String,Object> getMap(Map<String,Object> returnMap, String returnMessage) {
        logger.info(">>> 小冰话术: " + returnMessage);
        returnMap.put("resCode", 200);
        returnMap.put("resMsg", returnMessage);
        return returnMap;
    }

}
