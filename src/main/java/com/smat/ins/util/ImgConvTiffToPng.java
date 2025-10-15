package com.smat.ins.util;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class ImgConvTiffToPng {
	public static byte[] convert(byte[] tiff) throws Exception {

        byte[] out = new byte[0];
        InputStream inputStream = new ByteArrayInputStream(tiff);

        TIFFDecodeParam param = null;

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", inputStream, param);
        RenderedImage op = dec.decodeAsRenderedImage(0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PNGEncodeParam jpgparam = null;
        ImageEncoder en = ImageCodec.createImageEncoder("png", outputStream, jpgparam);
        en.encode(op);
        outputStream = (ByteArrayOutputStream) en.getOutputStream();
        out = outputStream.toByteArray();
        outputStream.flush();
        outputStream.close();

        return out;

    }
}
