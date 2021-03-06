package nl.vpro.domain.page.update;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@XmlRootElement(name = "deleteresult")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Slf4j
public class DeleteResult {
    @XmlTransient
    final CompletableFuture<?> future;
    @XmlAttribute
    private int count;
    @XmlAttribute
    private int notallowedCount;
    @XmlAttribute
    private boolean success = true;

    private DeleteResult() {
        this.future = CompletableFuture.completedFuture(null);
        // just for silly jaxb
    }

    @lombok.Builder
    public DeleteResult( CompletableFuture<?> future, Integer count, Integer notallowedCount) {
        this.future = future == null ? CompletableFuture.completedFuture(null) : future;
        this.count = count == null ? 1 : count;
        this.notallowedCount = notallowedCount == null ? 0 : notallowedCount;
    }

    @JsonCreator
    protected DeleteResult(
        @JsonProperty("count") Integer count,
        @JsonProperty("notallowedCount") Integer notallowedCount) {
        this.count = count;
        this.notallowedCount = notallowedCount;
        this.future = CompletableFuture.completedFuture(null);
    }

    @Override
    public String toString() {
        return "Deleted " + count +  " " + (notallowedCount > 0 ? " (not allowed : " + notallowedCount + ")" : "") + (future != null && ! future.isDone() ? " (still running) " : "");
    }

    public DeleteResult and(DeleteResult result) {

        return DeleteResult.builder()
            .count(this.count + result.getCount())
            .notallowedCount(this.notallowedCount+ result.getNotallowedCount())
            .future(this.future.thenApply((v) -> {
                try {
                    return result.getFuture().get();
                } catch (ExecutionException | InterruptedException iae) {
                    log.error(iae.getMessage());
                    return null;
                }
            }))
            .build();
    }

}

