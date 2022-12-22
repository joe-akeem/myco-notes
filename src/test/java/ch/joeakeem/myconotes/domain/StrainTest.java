package ch.joeakeem.myconotes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ch.joeakeem.myconotes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StrainTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Strain.class);
        Strain strain1 = new Strain();
        strain1.setId(1L);
        Strain strain2 = new Strain();
        strain2.setId(strain1.getId());
        assertThat(strain1).isEqualTo(strain2);
        strain2.setId(2L);
        assertThat(strain1).isNotEqualTo(strain2);
        strain1.setId(null);
        assertThat(strain1).isNotEqualTo(strain2);
    }
}
