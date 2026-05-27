package com.splitease.repository;

import com.splitease.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByGroupId(Long groupId);

    List<Member> findByGroupIdAndIsActiveTrue(Long groupId);

    Optional<Member> findByIdAndGroupId(Long id, Long groupId);
}
