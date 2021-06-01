package io.silverman.hellojpa.api;

import io.silverman.hellojpa.domain.Member;
import io.silverman.hellojpa.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //== 회원 생성 ==//

    @PostMapping("/api/v2.0/members")
    public MemberCreateResponse createMemberV2_0(@RequestBody @Valid MemberCreateRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.signUp(member);
        return new MemberCreateResponse(id);
    }

    @Data
    static class MemberCreateRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class MemberCreateResponse {
        private Long id;
    }

    //== 회원 변경 ==//

    @PatchMapping("/api/v2.0/members/{id}")
    public MemberUpdateResponse updateMemberV2_0(@PathVariable("id") Long id, @RequestBody @Valid MemberUpdateRequest request) {
        memberService.updateMember(id, request.getName());
        Member member = memberService.findMember(id);
        return new MemberUpdateResponse(member.getId(), member.getName());
    }

    @Data
    static class MemberUpdateRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class MemberUpdateResponse {
        private Long id;
        private String name;
    }

    //== 회원 조회 ==//

    @GetMapping("/api/v2.0/members")
    public ResponseWrapper<List<MemberDto>> membersV2_0() {
        List<Member> members = memberService.findMembers();
        List<MemberDto> memberDtos = members.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(toList());
        return new ResponseWrapper<>(memberDtos);
    }

    @GetMapping("/api/v2.0/members/{id}")
    public MemberDto memberV2_0(@PathVariable("id") Long id) {
        Member member = memberService.findMember(id);
        return new MemberDto(member.getName());
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
