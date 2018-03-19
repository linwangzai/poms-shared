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
import java.util.function.Function;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nl.vpro.nep.service.FileDescriptor;
import nl.vpro.nep.service.NEPFTPDownloadService;

import static org.apache.commons.io.IOUtils.copy;

@Service("NEPFTPDownloadService")
@Slf4j
public class NEPFTPDownloadServiceImpl implements NEPFTPDownloadService {


    private final String ftpHost;
    private final String username;
    private final String password;
    private final String hostKey;

    @Inject
    public NEPFTPDownloadServiceImpl(

        @Value("${nep.sftp.host}") String ftpHost,
        @Value("${nep.sftp.username}") String username,
        @Value("${nep.sftp.password}") String password,
        @Value("${nep.sftp.hostkey}") String hostKey
    ) {
        this.ftpHost = ftpHost;
        this.username = username;
        this.password = password;
        this.hostKey = hostKey;
    }

    @Override
    public void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) {
        log.info("Started nep file transfer service for {} @ {} (hostkey: {})", username, ftpHost, hostKey);

        if (StringUtils.isBlank(nepFile)) {
            throw new IllegalArgumentException();
        }
        try {
            final SSHClient sessionFactory = SSHClientFactory.create(hostKey, ftpHost, username, password);
            final SFTPClient sftp = sessionFactory.newSFTPClient();
            Instant start = Instant.now();
            InputStream in;
            while (true) {
                try {
                    final RemoteFile handle = sftp.open(nepFile, EnumSet.of(OpenMode.READ));
                    in = handle.new ReadAheadRemoteFileInputStream(16);
                    FileAttributes attributes = handle.fetchAttributes();
                    FileDescriptor descriptor = FileDescriptor.builder()
                        .size(handle.length())
                        .lastModified(Instant.ofEpochMilli(attributes.getMtime()))
                        .build();
                    if (descriptorConsumer != null) {
                        try {
                            boolean proceeed = descriptorConsumer.apply(descriptor);
                            if (! proceeed) {
                                return;
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
                    Thread.sleep(Duration.ofSeconds(10).toMillis());
                }
            }
            log.info("File appeared {} in {}, now copying.", nepFile, Duration.between(start, Instant.now()));

            copy(in, outputStream);

            try {
                sftp.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            try {
                sessionFactory.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        return username + "@" + ftpHost;
    }
}