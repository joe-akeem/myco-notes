package ch.joeakeem.myconotes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ch.joeakeem.myconotes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GenusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Genus.class);
        Genus genus1 = new Genus();
        genus1.setId(1L);
        Genus genus2 = new Genus();
        genus2.setId(genus1.getId());
        assertThat(genus1).isEqualTo(genus2);
        genus2.setId(2L);
        assertThat(genus1).isNotEqualTo(genus2);
        genus1.setId(null);
        assertThat(genus1).isNotEqualTo(genus2);
    }
}
