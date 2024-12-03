package ru.eddyz.telegramnewsbot.util;


import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.Optional;

@Component
public class BrowserUtil {


    public Optional<File> downlandImage(URL url) {
        File image = new File("image.png");
        try(BufferedInputStream bis = new BufferedInputStream(url.openStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(image))) {

            byte[] buffer = new byte[2048];
            int read;

            while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, read);
            }

            return Optional.of(image);
        } catch (IOException e) {
            return Optional.empty();
        }
    }


}
