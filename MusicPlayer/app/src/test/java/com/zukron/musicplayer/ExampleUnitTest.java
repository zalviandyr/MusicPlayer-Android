package com.zukron.musicplayer;

import android.telephony.AccessNetworkConstants;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test(){
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("asds.mp3", "asds.cop", "asdasdasx.mp3"));

        for (String s : arrayList) {
            if (s.contains(".mp3")) {
                System.out.println(s);
            }
        }

    }
}