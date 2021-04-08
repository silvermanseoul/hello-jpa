package io.silverman.hellojpa.web;

import io.silverman.hellojpa.domain.Address;
import io.silverman.hellojpa.domain.Member;
import io.silverman.hellojpa.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String membersNewForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());

        return "member/member-create-form";
    }

    @PostMapping("/members/new")
    public String membersNew(@Valid MemberForm memberForm, BindingResult result) {
        if (result.hasErrors()) {
            return "member/member-create-form";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/members";
    }

    @GetMapping("/members")
    public String members(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "member/member-list";
    }
}
