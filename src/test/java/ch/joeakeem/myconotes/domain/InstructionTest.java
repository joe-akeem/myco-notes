package ch.joeakeem.myconotes.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ch.joeakeem.myconotes.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstructionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Instruction.class);
        Instruction instruction1 = new Instruction();
        instruction1.setId(1L);
        Instruction instruction2 = new Instruction();
        instruction2.setId(instruction1.getId());
        assertThat(instruction1).isEqualTo(instruction2);
        instruction2.setId(2L);
        assertThat(instruction1).isNotEqualTo(instruction2);
        instruction1.setId(null);
        assertThat(instruction1).isNotEqualTo(instruction2);
    }
}
