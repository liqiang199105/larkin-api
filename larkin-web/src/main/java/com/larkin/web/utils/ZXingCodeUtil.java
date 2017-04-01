package com.larkin.web.utils;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.log4j.Logger;

public class ZXingCodeUtil {
    private static Logger logger = Logger.getLogger(ZXingCodeUtil.class);

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final String DEFAULT_IMAGE_PATH = "/tmp/";

    /**
     *  为指定内容生成码，并生成png文件到指定目录
     * @param contents
     * @param width
     * @param height
     * @param imgPath
     */
    public static void encode(String contents, int width, int height, String imgPath){
      encode(contents, BarcodeFormat.QR_CODE, width, height, imgPath);
    }

    public static void encode(String contents, BarcodeFormat format, int width, int height, String imgPath){
        Map<EncodeHintType, Object> hints = Maps.newHashMap();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);     // 指定纠错等级
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");   // 指定编码格式
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, format, width, height, hints);
            MatrixToImageWriter.writeToFile(bitMatrix, "png", new File(imgPath));
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }




    /**
     * @param contents
     */
    private static void encode(String contents){
        encode(contents, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_IMAGE_PATH);
    }


}
