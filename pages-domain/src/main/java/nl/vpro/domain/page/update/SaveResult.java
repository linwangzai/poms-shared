package nl.vpro.domain.page.update;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.xml.bind.annotation.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
@XmlRootElement(name = "deleteresult")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Slf4j
public class SaveResult {
    @XmlTransient
    final CompletableFuture<?> future;

    @XmlAttribute
    private boolean success = true;

    @XmlAttribute
    private Instant creationDate;

    @XmlElement
    private List<String> replaces;


    @XmlElement
    private String message;



    private SaveResult() {
        this.future = CompletableFuture.completedFuture(null);
        // just for silly jaxb
    }

    @lombok.Builder
    private SaveResult(CompletableFuture<?> future, Instant creationDate, boolean success, List<String> replaces, String message) {
        this.future = future == null ? CompletableFuture.completedFuture(null) : future;
        this.creationDate = creationDate;
        this.success = success;
        this.replaces = replaces;
        this.message = message;
    }

}

