package nl.vpro.domain.media.update;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Data
public class MediaUpdateTable {


    ScheduleUpdate schedule = null;

    List<ProgramUpdate> programs = new ArrayList<>();

    List<GroupUpdate> groups = new ArrayList<>();
}
