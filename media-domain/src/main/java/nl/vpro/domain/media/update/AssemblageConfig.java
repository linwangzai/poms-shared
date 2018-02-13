package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import nl.vpro.domain.media.support.OwnerType;

/**
 * This look a lot like {@link MediaUpdateConfig}
 *
 * TODO: MediaUpdate needs heavy refactoring, and we should think about this kind of stuff then too.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@AllArgsConstructor
@lombok.Builder
@Data
@ToString
public class AssemblageConfig {
    @lombok.Builder.Default
    OwnerType ownerType = OwnerType.BROADCASTER;
    final boolean copyWorkflow;
    final boolean copyLanguageAndCountry;
    final boolean imageMetaData;
    final boolean copyPredictions;
}
