package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.Slf4jHelper;
import nl.vpro.nep.service.FileDescriptor;
import nl.vpro.nep.service.NEPDownloadService;

import static org.apache.commons.io.IOUtils.copy;


/**
 * This is a wrapper for sftp-itemizer.nepworldwide.nl This is were itemize results are placed by NEP
 */
//@Named("NEPDownloadService") // It doesn't *** work
@Slf4j
public class NEPSSJDownloadServiceImpl implements NEPDownloadService {


    private final String ftpHost;
    private final String username;
    private final String password;
    private final String hostKey;

    @Inject
    public NEPSSJDownloadServiceImpl(

        @Value("${nep.sftp.host}") String ftpHost,
        @Value("${nep.sftp.username}") String username,
        @Value("${nep.sftp.password}") String password,
        @Value("${nep.sftp.hostkey}") String hostKey
    ) {
        this.ftpHost = ftpHost;
        this.username = username;
        this.password = password;
        this.hostKey = "SHA256:" + hostKey;
    }

    @PostConstruct
    public void init() {
        log.info("NEP download service for {}@{}", username, ftpHost);
    }

    @Override
    public void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) {
        log.info("Started nep file transfer service for {}@{} (hostkey: {})", username, ftpHost, hostKey);
        if (StringUtils.isBlank(nepFile)) {
            throw new IllegalArgumentException();
        }
        try {
            checkAvailabilityAndConsume(nepFile, timeout, descriptorConsumer, (handle) -> {
                try (InputStream in = handle.new ReadAheadRemoteFileInputStream(32)) {

                    long copy = copy(in, outputStream, 1024 * 10);
                    log.info("Copied {} bytes", copy);
                } catch (SFTPException sfte) {
                    log.error(sfte.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
    }

    protected void checkAvailabilityAndConsume(String nepFile, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer, Consumer<RemoteFile> remoteFileConsumer) throws IOException  {
        try(final SSHClient sessionFactory = createClient();
            final SFTPClient sftp = sessionFactory.newSFTPClient()) {
            Instant start = Instant.now();
            long count = 0;
            RemoteFile handle = null;
            while (true) {
                count++;
                try {
                    handle = sftp.open(nepFile, EnumSet.of(OpenMode.READ));
                    FileAttributes attributes = handle.fetchAttributes();
                    FileDescriptor descriptor = FileDescriptor.builder()
                        .size(handle.length())
                        .lastModified(Instant.ofEpochMilli(attributes.getMtime()))
                        .fileName(nepFile)
                        .build();
                    if (descriptorConsumer != null) {
                        try {
                            boolean proceed = descriptorConsumer.apply(descriptor);
                            if (!proceed) {
                                break;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                    break;
                } catch (SFTPException sftpe) {
                    if (timeout == null || timeout.equals(Duration.ZERO)) {
                        throw new IllegalStateException("File " + nepFile + " doesn't exist");
                    }
                    if (Duration.between(start, Instant.now()).compareTo(timeout) > 0) {
                        throw new IllegalStateException("File " + nepFile + " didn't appear in " + timeout);
                    }
                    Slf4jHelper.log(log, count < 6 ? Level.DEBUG : Level.INFO, "{}: {}. Waiting for retry", nepFile, sftpe.getMessage());
                    try {
                        Thread.sleep(Duration.ofSeconds(10).toMillis());
                    } catch (InterruptedException ignored) {
                        break;

                    }
                }
            }
            if (handle != null) {
                remoteFileConsumer.accept(handle);
                handle.close();
            }
        }

    }

    protected SSHClient createClient() throws IOException {
        return SSHClientFactory
                .create(hostKey, ftpHost, username, password);
    }


    @Override
    public String toString() {
        return username + "@" + ftpHost;
    }
}
