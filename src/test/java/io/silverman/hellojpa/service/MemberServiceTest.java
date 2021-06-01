package io.silverman.hellojpa.service;

import io.silverman.hellojpa.domain.Member;
import io.silverman.hellojpa.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입() throws Exception {
        // Given
        Member member = new Member();
        member.setName("Park");

        // When
        Long savedId = memberService.signUp(member);
        Member foundMember = memberRepository.findOne(savedId);

        // Then
        assertEquals(member, foundMember);
    }

    @Test
    void 중복회원예외() {
        // Given
        Member member1 = new Member();
        member1.setName("Park");
        Member member2 = new Member();
        member2.setName("Park");

        // When
        memberService.signUp(member1);
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class,
                () -> memberService.signUp(member2));

        // Then
        assertEquals("이미 존재하는 회원입니다.", e.getMessage());
    }
}