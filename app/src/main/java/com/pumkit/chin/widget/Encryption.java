package com.pumkit.chin.widget;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {

    private static String userPassword;

    /**
     * 패스워드 암호화
     * @param userPassword
     *            사용자 패스워드
     * @return 암호화 된 사용자 패스워드
     *         암호화 방식 : SHA-512
     */
    public static boolean encryption(String userPassword) {
        MessageDigest md;
        boolean isSuccess;
        String tempPassword = "";

        try {
            md = MessageDigest.getInstance("SHA-512");

            md.update(userPassword.getBytes());
            byte[] mb = md.digest();
            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                tempPassword += s;
            }
            setPassword(tempPassword);
            isSuccess = true;
        } catch (NoSuchAlgorithmException e) {
            isSuccess = false;
            return isSuccess;
        }
        return isSuccess;
    }

    public static void setPassword(String temppassword) {
        userPassword = temppassword;
    }

    public static String getPassword() {
        return userPassword;
    }
}