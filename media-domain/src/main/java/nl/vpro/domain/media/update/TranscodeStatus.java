package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transcodeStatusType")
@XmlRootElement(name = "transcodeStatus")
public class TranscodeStatus {

    String fileName;
    String status;
    String statusMessage;
    String workflowType;

    @NotNull
    String mid;

    @NotNull
    String workflowId;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant startTime;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant updateTime;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant endTime;

    public TranscodeStatus() {
    }
}
