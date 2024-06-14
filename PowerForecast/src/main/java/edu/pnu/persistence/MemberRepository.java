package edu.pnu.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.pnu.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByMemberId(String memberId);
	Member findByPhoneNumber(String phoneNum);
	Member findByMemberIdAndPhoneNumber(String id, String phoneNum);
	@Query("Select m from Member m where m.memberId = ?1")
	Member existsByMemberId(String memberId);
}
