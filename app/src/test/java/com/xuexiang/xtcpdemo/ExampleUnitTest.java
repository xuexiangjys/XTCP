package com.xuexiang.xtcpdemo;

import com.xuexiang.xtcp.core.model.BCD;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

//        byte[] bytes = ConvertUtils.hexStringToBytes("00112233441122334411223344");
//
//        int length = 4;
//        System.out.println(ConvertUtils.bytesToShort(StorageMode.LittleEndian, bytes, 1, length));
//
//        System.out.println(ConvertUtils.bytesToInt(StorageMode.LittleEndian, bytes, 1, length));
//
//        byte[] b = new byte[20];
////        System.out.println(ConvertUtils.fillIntToBytes(StorageMode.BigEndian, 2133, b, 1, 5));
////
////        System.out.println(ConvertUtils.bytesToHexString(b));
//
//        long value = ConvertUtils.bytesToLong(StorageMode.BigEndian, bytes, 1, 9);
//        System.out.println(value);
//
//        ConvertUtils.fillLongToBytes(StorageMode.BigEndian, value, b, 1, 9);
//        System.out.println(ConvertUtils.bytesToHexString(b));
//
//        String s = "11,22,33,44,55";
//        System.out.println(Arrays.toString(s.split(",")));

//        BCD<Date> bcd = new BCD<>(Date.class, "yy-MM-dd HH-mm");
//        System.out.println(bcd.getProtocolLength());



    }
}