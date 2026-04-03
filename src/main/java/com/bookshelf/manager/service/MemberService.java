package com.bookshelf.manager.service;

import com.bookshelf.manager.dto.CreateMemberRequest;
import com.bookshelf.manager.dto.MemberResponse;
import com.bookshelf.manager.exception.BusinessRuleException;
import com.bookshelf.manager.exception.ResourceNotFoundException;
import com.bookshelf.manager.model.Member;
import com.bookshelf.manager.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles member lifecycle: uniqueness of email is enforced here before persistence.
 */
@Service
public class MemberService {

    private final MemberRepository members;

    public MemberService(MemberRepository members) {
        this.members = members;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return members.findAll().stream().map(MemberResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(Long id) {
        Member m = members.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
        return MemberResponse.from(m);
    }

    @Transactional
    public MemberResponse create(CreateMemberRequest req) {
        String email = req.email().trim().toLowerCase();
        if (members.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException("Email already registered: " + email);
        }
        Member member = new Member(req.name().trim(), email);
        return MemberResponse.from(members.save(member));
    }
}
