package ch.joeakeem.myconotes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ch.joeakeem.myconotes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TekTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tek.class);
        Tek tek1 = new Tek();
        tek1.setId(1L);
        Tek tek2 = new Tek();
        tek2.setId(tek1.getId());
        assertThat(tek1).isEqualTo(tek2);
        tek2.setId(2L);
        assertThat(tek1).isNotEqualTo(tek2);
        tek1.setId(null);
        assertThat(tek1).isNotEqualTo(tek2);
    }
}
