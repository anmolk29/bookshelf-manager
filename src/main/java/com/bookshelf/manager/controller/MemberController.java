package com.bookshelf.manager.controller;

import com.bookshelf.manager.dto.CreateMemberRequest;
import com.bookshelf.manager.dto.MemberResponse;
import com.bookshelf.manager.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Members are the people borrowing books; email must stay unique in this demo app.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<MemberResponse> list() {
        return memberService.findAll();
    }

    @GetMapping("/{id}")
    public MemberResponse byId(@PathVariable Long id) {
        return memberService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse create(@Valid @RequestBody CreateMemberRequest body) {
        return memberService.create(body);
    }
}
