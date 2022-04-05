package com.cobnet.spring.boot.core;

import com.google.common.io.Files;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class QRCodeProvider {

    public BufferedImage encode(String text, BufferedImage logo, int width, int height, Pair<Color, Color> colors, Map.Entry<EncodeHintType, ?>... options) throws WriterException, NotFoundException {

        BitMatrix matrix = encode(text, width, height, options);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix, new MatrixToImageConfig(colors.getFirst().getRGB() , colors.getSecond().getRGB()));

        int deltaHeight = image.getHeight() - logo.getHeight();
        int deltaWidth = image.getWidth() - logo.getWidth();

        BufferedImage combined = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) combined.getGraphics();

        graphics.drawImage(image, 0, 0, null);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        graphics.drawImage(logo, Math.round((float)deltaWidth / 2), Math.round((float)deltaHeight / 2), null);

        return combined;
    }

    public BufferedImage encode(String text, byte[] logo, int width, int height, Pair<Color, Color> colors, Map.Entry<EncodeHintType, ?>... options) throws IOException, NotFoundException, WriterException {

        return encode(text, ImageIO.read(new ByteArrayInputStream(logo)), width, height, colors, options);
    }

    public BufferedImage encode(String text, String logo, int width, int height, Pair<Color, Color> colors, Map.Entry<EncodeHintType, ?>... options) throws IOException, NotFoundException, WriterException {

        return encode(text, ImageIO.read(new FileInputStream(logo)), width, height, colors, options);
    }

    @Async
    public Future<BufferedImage> encode(String text, URL logo, int width, int height, Pair<Color, Color> colors, Map.Entry<EncodeHintType, ?>... options) throws IOException, NotFoundException, WriterException {

        return CompletableFuture.completedFuture(encode(text, ImageIO.read(logo), width, height, colors, options));
    }

    public BitMatrix encode(String text, int width, int height, Map.Entry<EncodeHintType, ?>... options) throws WriterException {

        return new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height, Arrays.stream(options).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public byte[] encode(String text, int width, int height, String format, Pair<Color, Color> colors, Map.Entry<EncodeHintType, ?>... options) throws WriterException, IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(encode(text, width, height, options), format, stream, new MatrixToImageConfig(colors.getFirst().getRGB() , colors.getFirst().getRGB()));

        return stream.toByteArray();
    }

    public void write(String path, String text, int width, int height, Map.Entry<EncodeHintType, ?>... options) throws WriterException, IOException {

        MatrixToImageWriter.writeToPath(encode(text, width, height, options), Files.getFileExtension(path), Path.of(path));
    }

    public boolean write(String path, String text, int width, int height, Pair<Color, Color> colors, Map.Entry<EncodeHintType, ?>... options) throws WriterException, IOException {

        return ImageIO.write(MatrixToImageWriter.toBufferedImage(encode(text, width, height, options), new MatrixToImageConfig(colors.getFirst().getRGB(), colors.getSecond().getRGB())), Files.getFileExtension(path).toUpperCase(), Path.of(path).toFile());
    }

    public boolean writeA(String path, String text, String logo, int width, int height, Pair<Color, Color> colors, Map.Entry<EncodeHintType, ?>... options) throws NotFoundException, IOException, WriterException {

        return ImageIO.write(encode(text, logo, width, height, colors, options), Files.getFileExtension(path), Path.of(path).toFile());
    }

    @Async
    public Future<Boolean> writeA(String path, String text, URL logo, int width, int height, Pair<Color, Color> colors, Map.Entry<EncodeHintType, ?>... options) throws NotFoundException, IOException, WriterException, ExecutionException, InterruptedException {

        return CompletableFuture.completedFuture(ImageIO.write(encode(text, logo, width, height, colors, options).get(), Files.getFileExtension(path), Path.of(path).toFile()));
    }

    public Result decode(BinaryBitmap bitmap, Map.Entry<DecodeHintType, ?>... options) throws ChecksumException, NotFoundException, FormatException {

        return new QRCodeReader().decode(bitmap, Arrays.stream(options).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public Result decode(BitMatrix matrix, Map.Entry<DecodeHintType, ?>... options) throws ChecksumException, NotFoundException, FormatException {

        return decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(MatrixToImageWriter.toBufferedImage(matrix)))), options);
    }

    public Result decode(byte[] bs, Map.Entry<DecodeHintType, ?>... options) throws IOException, ChecksumException, NotFoundException, FormatException {

        return decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new ByteArrayInputStream(bs))))), options);
    }

    public Result decode(String path, Map.Entry<DecodeHintType, ?>... options) throws ChecksumException, NotFoundException, FormatException, IOException {

        return decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(path))))), options);
    }
}
