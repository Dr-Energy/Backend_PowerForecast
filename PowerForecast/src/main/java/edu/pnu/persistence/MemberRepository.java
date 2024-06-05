package edu.pnu.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByMemberId(String memberId);
	Member findByPhoneNumber(String phoneNum);
	Member findByMemberIdAndPhoneNumber(String id, String phoneNum);
}
