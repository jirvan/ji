package com.jirvan.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.jirvan.lang.NotFoundRuntimeException;
import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.ImageResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HtmlToPdf {

    public static void main(String[] args) {

        String html = "<html>\n" +
                      "    <body>\n" +
                      "    <img src=\"com/jirvan/JirvanNameLogo 98x38.png\" style=\"border: 0;height: auto;line-height: 100%;outline: none;text-decoration: none;\"></img>\n" +
                      "    <h1>Stuff</h1>\n" +
                      "            <p>The quick brown fox.</p>\n" +
                      "    </body>\n" +
                      "</html>";

        try {
            OutputStream outputStream = new FileOutputStream("/tmp/example.pdf");
            toOutputStream(outputStream, html);
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void toOutputStream(OutputStream outputStream, String html) throws IOException {
        try {
            ITextRenderer renderer = new ITextRenderer();
            UserAgent callback = new UserAgent(renderer.getOutputDevice());
            callback.setSharedContext(renderer.getSharedContext());
            renderer.getSharedContext().setUserAgentCallback(callback);
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static class UserAgent extends ITextUserAgent {


        public UserAgent(ITextOutputDevice outputDevice) {
            super(outputDevice);
        }


        public ImageResource getImageResource(String path) {
            try {
                ImageResource imageResource = (ImageResource) _imageCache.get(path);
                if (imageResource == null) {
                    Image image = getResource(path);
                    scaleToOutputResolution(image);
                    imageResource = new ImageResource(path, new ITextFSImage(image));
                    _imageCache.put(path, imageResource);
                }
                if (imageResource != null) {
                    FSImage image = imageResource.getImage();
                    if (image instanceof ITextFSImage) {
                        image = (FSImage) ((ITextFSImage) imageResource.getImage()).clone();
                    }
                    imageResource = new ImageResource(imageResource.getImageUri(), image);
                } else {
                    imageResource = new ImageResource(path, null);
                }
                return imageResource;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void scaleToOutputResolution(Image image) {
            float dotsPerPixel = getSharedContext().getDotsPerPixel();
            if (dotsPerPixel != 1.0f) {
                image.scaleAbsolute(image.getPlainWidth() * dotsPerPixel, image.getPlainHeight() * dotsPerPixel);
            }
        }

        public static Image getResource(String path) throws IOException, BadElementException {
            InputStream is = ClassLoader.getSystemResourceAsStream(path);
            if (is == null) {
                throw new NotFoundRuntimeException("Couldn't find resource \"" + path + "\"");
            }
            try {
                return Image.getInstance(IOUtils.toByteArray(is));
            } finally {
                is.close();
            }
        }

    }

}
