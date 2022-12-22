package ch.joeakeem.myconotes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ch.joeakeem.myconotes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExperimentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Experiment.class);
        Experiment experiment1 = new Experiment();
        experiment1.setId(1L);
        Experiment experiment2 = new Experiment();
        experiment2.setId(experiment1.getId());
        assertThat(experiment1).isEqualTo(experiment2);
        experiment2.setId(2L);
        assertThat(experiment1).isNotEqualTo(experiment2);
        experiment1.setId(null);
        assertThat(experiment1).isNotEqualTo(experiment2);
    }
}
