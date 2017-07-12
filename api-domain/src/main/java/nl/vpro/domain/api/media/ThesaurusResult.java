package nl.vpro.domain.api.media;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.beeldengeluid.gtaa.GTAAPerson;
import nl.vpro.domain.api.Result;

@XmlRootElement(name = "thesaurusResult")
@XmlType(name = "thesaurusResultType")
public class ThesaurusResult extends Result<GTAAPerson>{

	public ThesaurusResult(List<GTAAPerson> asList, long l, Integer max, long m) {
		super(asList, l, max, m);
	}

}
