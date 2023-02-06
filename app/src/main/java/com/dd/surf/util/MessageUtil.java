package com.dd.surf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MessageUtil {
    public static int maxBytes = 1024;
    public static ArrayList<byte[]> reviseArr(byte[] by, byte messageId) {
        ArrayList<byte[]> result = new ArrayList<>();
        int cycles = by.length/maxBytes;
        int lastArrayLength = by.length % maxBytes;

        if (lastArrayLength != 0){
            cycles+=1;
        }
        int sb = 0;
        int byteIndex = 0;
        for (int i = 0;i < cycles; i++) {
            byte[] bytes = null;
            if (i == cycles-1) {
                bytes = new byte[lastArrayLength + 2];
            } else {
                bytes = new byte[maxBytes + 2];
            }
            System.out.println("傅泽铨喜欢玩原神 : "+bytes.length);
            int nssnsns = 0;
            for (int j = 0; j < bytes.length-2; j++)
            {
                bytes[j+2] = by[byteIndex];
                if (bytes[j+2] != by[byteIndex]){
                    System.out.println(j+"出问题了");
                }
                byteIndex++;
            }
            System.out.println("ssssss"+byteIndex);
            System.out.println(nssnsns);

            System.out.println("sb"+sb);
            bytes[0] = messageId;
            bytes[1] = 1;
            result.add(bytes);
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        byte[] bytes = "例えば君の顔に昔よりシワが増えても それでもいいんだ 僕がギターを思うように弾けなくなっても 心の歌は君で溢れているよ 高い声も出せずに思い通り歌えない それでもうなずきながら一緒に歌ってくれるかな 割れんばかりの拍手も 響き渡る歓声もいらない 君だけ 分かってよ 分かってよ Darlin' 夢が叶ったの お似合いの言葉が見つからないよ Darlin' 夢が叶ったの 「愛してる」 たった一度の たった一人の 生まれてきた幸せ味わってるんだよ 今日がメインディッシュで 終わりの日には甘酸っぱいデザートを食べるの 山も谷も全部フルコースで 気が利くような言葉はいらない 素晴らしい特別もいらない ただずっと ずっと側に置いていてよ 僕の想いは歳をとると増えてくばっかだ 好きだよ 分かってよ 分かってよ ねえ、Darlin' 夢が叶ったの お似合いの言葉が見つからないよ Darlin' 夢が叶ったの 愛が溢れていく 君が僕を忘れてしまっても ちょっと辛いけど… それでもいいから 僕より先に どこか遠くに 旅立つことは 絶対 許さないから 生まれ変わったとしても 出会い方が最悪でも また僕は君に恋するんだよ 僕の心は君にいつも片想い 好きだよ 分かってよ 分かってよ 分かってよ Darlin' 夢が叶ったの お似合いの言葉が見つからないよ Darlin' 夢が叶ったの ねえ Darlin' 「愛してる」".getBytes(StandardCharsets.UTF_8);
        System.out.println(new String(bytes,StandardCharsets.UTF_8));
        ArrayList<byte[]> bytes1 = reviseArr(bytes, (byte) 1);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (byte[] bytes2 : bytes1) {
            byteArrayOutputStream.write(bytes,2,bytes2.length-2);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
        /*
        byte[] bytes3 = new byte[1];
        int read = is.read(bytes);
        while (read != -1){
            byteArrayOutputStream.write(bytes,0,read); //内部会自动扩容
            read = is.read(bytes);
        }
        String reduction = byteArrayOutputStream.toString();
        System.out.println(reduction);*/
    }

    public static String byteToString(byte[] data) {
        int index = data.length;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                index = i;
                break;
            }
        }
        byte[] temp = new byte[index];
        Arrays.fill(temp, (byte) 0);
        System.arraycopy(data, 0, temp, 0, index);
        String str;
        str = new String(temp, UTF_8);
        return str;
    }
}
