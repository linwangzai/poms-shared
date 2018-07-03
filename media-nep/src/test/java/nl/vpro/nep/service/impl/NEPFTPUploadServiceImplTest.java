package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import nl.vpro.logging.simple.Slf4jSimpleLogger;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class NEPFTPUploadServiceImplTest {

    private NEPFTPUploadServiceImpl impl;

    private String[] files = new String[] {"/Users/michiel/npo/media/huge1.mp4", "/Users/michiel/npo/media/huge2.mp4"};


    @Before
    public void init() {

        impl = new NEPFTPUploadServiceImpl(
            "ftp.nepworldwide.nl",
            "npoweb-vpro",
            "May8xGLHUumFAZo",
            "9b:b4:4c:54:d1:7a:aa:63:71:e0:ef:cb:78:22:73:83");
    }

    @Test
    @Ignore("This actually does something")

    public void upload() throws Exception {
        byte[] example = new byte[]{1, 2, 3, 4};
        String filename = "npoweb-vpro/test.1235";
        impl.upload(new Slf4jSimpleLogger(log), filename, (long) example.length, new ByteArrayInputStream(example));
    }


    @Test
    @Ignore("This actually does something")
    public void uploadHuge() throws Exception {
        Instant start = Instant.now();
        File file = new File(files[0]);
        String filename = "test.1235";
        impl.upload(new Slf4jSimpleLogger(log), filename, file.length(), new FileInputStream(file));
        log.info("Took {}", Duration.between(start, Instant.now()));
    }

    @Test
    //@Ignore("This actually does something")
    @SneakyThrows
    public void async() {
        List<ForkJoinTask> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(ForkJoinPool.commonPool().submit(upload(new File(files[i % 2]), "test." + i)));

        }
        for (ForkJoinTask task : tasks) {
            task.get();
        }
        tasks.clear();
        for (int i = 0; i < 10; i++) {
            tasks.add(ForkJoinPool.commonPool().submit(upload(new File(files[i % 2]), "test." + i)));
        }
        for (ForkJoinTask t : tasks) {
            t.get();
        }
    }


    public Runnable upload(File from, String to) {
        return () -> {
            Instant start = Instant.now();
            File file = from;
            String filename = to;
            try {
                impl.upload(new Slf4jSimpleLogger(log), filename, file.length(), new FileInputStream(file));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            log.info("Took {}", Duration.between(start, Instant.now()));
        };
    }
}
