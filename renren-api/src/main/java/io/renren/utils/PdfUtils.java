package io.renren.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfUtils {


    /**
     * 读取PDF内容 并且转成字符串集合 1000个字以内1个元素
     */
    public static List<String> parsePdf(InputStream inputStream) throws IOException {
// 打开 PDF 文件
        PDDocument document = PDDocument.load(inputStream);
        // 创建 PDFTextStripper 对象
        PDFTextStripper stripper = new PDFTextStripper();
        // 获取文本内容
        String text = stripper.getText(document);
        text = text.replaceAll("\\s", " ").replaceAll("(\\r\\n|\\r|\\n|\\n\\r)"," ").replaceAll("\\.", "。");
        //按照长度 切成数组
//        String[] strings = stringToStringArray(text, 1000);
        String[] split = text.split("。");
        List<String> list = new ArrayList<>(Arrays.asList(split));
        return list;
    }

    public static void main(String[] args) {
        String s ="dsadasdsadas.dsdasdsadas.dsadasdasdsa";
        String s1 = s.replaceAll("\\.", "。");
        System.out.println(s1);
    }


    /**
     * 将字符串按照指定长度分割成字符串数组
     *
     * @param src
     * @param length
     * @return
     */
    public static String[] stringToStringArray(String src, int length) {
        //检查参数是否合法
        if (null == src || src.equals("")) {
            return null;
        }

        if (length <= 0) {
            return null;
        }
        int n = (src.length() + length - 1) / length; //获取整个字符串可以被切割成字符子串的个数
        String[] split = new String[n];
        for (int i = 0; i < n; i++) {
            if (i < (n - 1)) {
                split[i] = src.substring(i * length, (i + 1) * length);
            } else {
                split[i] = src.substring(i * length);
            }
        }
        return split;
    }
}



