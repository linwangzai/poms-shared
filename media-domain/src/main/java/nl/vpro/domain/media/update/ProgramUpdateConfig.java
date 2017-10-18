/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Data;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
@Data
public class ProgramUpdateConfig extends MediaUpdateConfig {

    private boolean isEpisodeOfUpdate = true;

    private boolean ratingsUpdate = true;
}
